package com.ey.advisory.admin.azurebus.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@RestController
@Slf4j
public class AzureBusServiceTestController {

	private static final String QUEUE_NAME = "sapqueue";

	private static final String CONN_STR = "Endpoint=sb://ci-dv-td-sbtd01.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=tTc4Bd/VSZMT0Mfp/bxpIzeGjdciSeMlN54h2z2j94A=";
//Test Commit from VS Code Test 1.
	@PostMapping(value = "/api/PublishMessage", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> publishAzureMessage(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
					.connectionString(CONN_STR).sender().queueName(QUEUE_NAME)
					.buildClient();
			ServiceBusMessage message = new ServiceBusMessage(jsonString);
			senderClient.sendMessage(message);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", gson
					.toJsonTree("Message has been Published Successfully."));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while publishing the Message  ", e);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp",
					gson.toJsonTree("Exception while publishing the Message "
							+ e.getMessage()));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

}