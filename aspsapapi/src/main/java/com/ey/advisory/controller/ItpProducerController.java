package com.ey.advisory.controller;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ItpProducerController {
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	@Autowired
	private KafkaListenerService KafkaListenerService;
	
	private static final String ITP_BOOTSTRAP_SERVERS = "ey.kafka.internal.bootstrapServers";

	private static final String ITP_SASL_JASS_CONFIG = "ey.kafka.internal.saslJaasConfig";

	private static final String ITP_CONSUMER_TOPIC = "ey.kafka.internal.consumer.topic";


	private String bootstrapServer;

	private String saslJassConfig;

	private String topic = null;
	
	@PostConstruct
	public void initProperties() {

		Map<String, Config> configMap = configManager.getConfigs("ITP",
				"ey.kafka.internal");

		bootstrapServer = configMap.containsKey(ITP_BOOTSTRAP_SERVERS)
				? configMap.get(ITP_BOOTSTRAP_SERVERS).getValue()
				: "ci-dv-itp-evh01.servicebus.windows.net:9093";
		saslJassConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"Endpoint=sb://ci-dv-itp-evh01.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=N7wDPS3TT6hRHxzPqrNBHqDluxjR70LrPsnUe/DyDPM=\";";
		topic = configMap.containsKey(ITP_CONSUMER_TOPIC)
				? configMap.get(ITP_CONSUMER_TOPIC).getValue()
				: "itpdmscallback";


	}
	
    @Autowired
    public ItpProducerController(KafkaListenerService kafkaMessageConsumer) {
        this.KafkaListenerService = kafkaMessageConsumer;
    }

/*
	@RequestMapping(value = "/api/azureEventProducer", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> azureEventProducerMethod(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject reqObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject reqJson = reqObject.get("req").getAsJsonObject();
		String msg = reqJson.get("message").toString();
		try {
			String connectionString = "Endpoint=sb://ci-dv-itp-evh01.servicebus.windows.net/;SharedAccessKeyName=SAPSharedAccessKey;SharedAccessKey=iG44KVBfwoI8R8vJNx+LqdIs6xdMSvnlb+AEhMXgudU=";

			String eventHubName = "itpdmscallback";

			if (LOGGER.isDebugEnabled()) {
				String msg2 = String.format(
						" inside azureEventProducer for jsonReq %s",
						jsonString.toString());
				LOGGER.debug(msg2);
			}

			// Create an Event Hub producer client
			EventHubProducerClient producer = new EventHubClientBuilder()
					.connectionString(connectionString, eventHubName)
					.buildProducerClient();

			// Create an event data
			List<EventData> eventData = new ArrayList<>();

			eventData.add(new EventData(msg));

			// Send the event to the Event Hub
			producer.send(eventData);

			if (LOGGER.isDebugEnabled()) {
				String msg2 = String.format(" producer sended the message - %s",
						eventData.toString());
				LOGGER.debug(msg2);
			}

			// Close the producer client
			producer.close();
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("Success"));
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg2 = "Exception on azureEventProducerMethod" + ex;
			LOGGER.error(msg2, ex);
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}
	}
*/
	@RequestMapping(value = "/api/createRequestMessage", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> azureEventKafkaProducerMethod(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject reqObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		try {
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String msg = reqJson.get("message").toString();

		/*	String bootstrapServers = "ci-dv-itp-evh01.servicebus.windows.net:9093";
			String eventHubName = "itpdmscallback";
			String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"Endpoint=sb://ci-dv-itp-evh01.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=N7wDPS3TT6hRHxzPqrNBHqDluxjR70LrPsnUe/DyDPM=\";";
*/
			// Kafka producer properties
			Properties properties = new Properties();
			properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
					bootstrapServer);
			properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringSerializer");
			properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					"org.apache.kafka.common.serialization.StringSerializer");
			properties.put("sasl.jaas.config", saslJassConfig);
			properties.put("security.protocol", "SASL_SSL");
			properties.put("sasl.mechanism", "PLAIN");

			// Create a Kafka producer
			Producer<String, String> producer = new KafkaProducer<>(properties);
		//	String topicName = "itpdmscallback";

			try {
				ProducerRecord<String, String> record = new ProducerRecord<>(
						topic, msg);
				producer.send(record);

				LOGGER.debug("Message sent to consumer: " + msg);

			} catch (Exception e) {
				throw new AppException(e);
			} finally {
				producer.close();
				LOGGER.debug("Producer is closed.");
			}

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("Success"));
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			JsonObject respMsg = new JsonObject();
			respMsg.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respMsg.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(respMsg.toString(), HttpStatus.OK);
		}

	}
	
	
}