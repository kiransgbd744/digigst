package com.ey.advisory.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaListenerService {

	 @KafkaListener(topics = "itpdmscallback", groupId = "SAP_DTE_GROUP")
	    public void listenGroup(String message) {
	        LOGGER.debug("Received Message in group SAP_DTE_GROUP: " + message);
	       
	    }
}
