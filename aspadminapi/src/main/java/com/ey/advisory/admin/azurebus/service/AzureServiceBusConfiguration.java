package com.ey.advisory.admin.azurebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.ey.advisory.common.CommonUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Configuration
public class AzureServiceBusConfiguration {

	@Autowired
	ITPEventResponseHandler responseHandler;
	
	@Autowired
	ITPEventAPIResponseHandler apiResponseHandler;

	@Autowired
	CommonUtility commonUtility;

	@Bean("ITPOnboardingReceiver")
	ServiceBusProcessorClient getServiceReceiver() {

		return new ServiceBusClientBuilder()
				.connectionString(
						commonUtility.getProp("azureBus.connection.string"))
				.processor()
				.queueName(commonUtility.getProp("azureBus.req.queue"))
				.disableAutoComplete().processMessage(context -> {
					LOGGER.debug("Receiver Started");
					responseHandler.messageHandler(context);
				}).processError(context -> {
					LOGGER.error("Error occurred {} ", context.getException());
				}).buildProcessorClient();
	}
	
	@Bean("ITPDashboardReceiver")
	ServiceBusProcessorClient getServiceApiReceiver() {

		return new ServiceBusClientBuilder()
				.connectionString(
						commonUtility.getProp("azureBus.connection.string"))
				.processor()
				.queueName(commonUtility.getProp("azureBus.api.req.queue"))
				.disableAutoComplete().processMessage(context -> {
					LOGGER.debug("Receiver Started");
					apiResponseHandler.messageHandler(context);
				}).processError(context -> {
					LOGGER.error("Error occurred {} ", context.getException());
				}).buildProcessorClient();
	} 

}
