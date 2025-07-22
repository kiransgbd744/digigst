package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableKafka
@Configuration
public class ItpConsumerService {	
	 @Bean
	    public ConsumerFactory<String, String> consumerFactory() {
		 String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\" password=\"Endpoint=sb://ci-dv-itp-evh01.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=N7wDPS3TT6hRHxzPqrNBHqDluxjR70LrPsnUe/DyDPM=\";";																																		

	        Map<String, Object> props = new HashMap<>();
	        props.put(
	          ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
	          "ci-dv-itp-evh01.servicebus.windows.net:9093");
	        props.put(
	          ConsumerConfig.GROUP_ID_CONFIG, 
	          "SAP_DTE_GROUP");
	        props.put(
	          ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
	          StringDeserializer.class);
	        props.put(
	          ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
	          StringDeserializer.class);
	        props.put("sasl.jaas.config", saslJaasConfig);
	        props.put("security.protocol", "SASL_SSL");
	        props.put("sasl.mechanism", "PLAIN");
	        return new DefaultKafkaConsumerFactory<>(props);
	    }
	 
	 @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, String> 
	      kafkaListenerContainerFactory() {
	   
	        ConcurrentKafkaListenerContainerFactory<String, String> factory =
	          new ConcurrentKafkaListenerContainerFactory<>();
	        factory.setConsumerFactory(consumerFactory());
	        return factory;
	    }
}
