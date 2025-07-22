package com.ey.advisory.admin.azurebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service
public class AzureServiceReceiverDashboardServiceStarter {

	@Autowired
	@Qualifier("ITPDashboardReceiver")
	private ServiceBusProcessorClient processerClient;

	@PostConstruct
	private void init() {
		LOGGER.debug("Service Process Client was Started.");
		processerClient.start();
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.debug("Service Process Client was Stop.");
		processerClient.stop();
	}

}
