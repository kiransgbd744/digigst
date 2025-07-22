package com.ey.advisory.aspsapapi.azurebus.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service
public class AzureDmsServiceReceiverDashboardServiceStarter {

	@Autowired
	@Qualifier("DMSOnboardingReceiver")
	private ServiceBusProcessorClient processerClient;

	@PostConstruct
	private void init() {
		LOGGER.error("Service Process DMS Client was Started.");
		processerClient.start();
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.error("Service Process DMS Client was Stop.");
		processerClient.stop();
	}

}
