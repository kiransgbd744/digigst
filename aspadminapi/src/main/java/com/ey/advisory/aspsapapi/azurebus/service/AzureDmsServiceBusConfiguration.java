package com.ey.advisory.aspsapapi.azurebus.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.KeyVaultUtility;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Configuration
public class AzureDmsServiceBusConfiguration {

	@Autowired
	DMSEventResponseHandler responseHandler;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Bean("DMSOnboardingReceiver")
	ServiceBusProcessorClient getServiceReceiver() {

		try {
			Map<String, Config> configMap = configManager.getConfigs("DMS_ASB", "dms.asb", "DEFAULT");

			String connectionStrSecretName;
			String sharedAccessKey;

			try {
				connectionStrSecretName = getValueFromMap(configMap, "dms.asb.keyvault.connectionStr");
				String accessKeySecretName = getValueFromMap(configMap, "dms.asb.keyvault.secretname");

				sharedAccessKey = KeyVaultUtility.getKey(accessKeySecretName);

			} catch (Exception e) {
				LOGGER.error("Error retrieving Azure Service Bus connection details: {}", e.getMessage(), e);
				throw new AppException("Error retrieving Azure Service Bus connection details", e);
			}
			String azureBusConnection = String.format(connectionStrSecretName, sharedAccessKey);

			String reqQueue = configMap.get("dms.asb.req.queue").getValue();

			return new ServiceBusClientBuilder().connectionString(azureBusConnection).processor().queueName(reqQueue)
					.disableAutoComplete().processMessage(context -> {
						LOGGER.debug("Receiver Started");
						responseHandler.messageHandler(context);
					}).processError(context -> {
						LOGGER.error("Error occurred {} ", context.getException());
					}).buildProcessorClient();

		} catch (Exception e) {
			LOGGER.error("Exception while downloading the file from folder ",
					e);
			throw new AppException(e);
		}
	}

	private String getValueFromMap(Map<String, Config> configMap, String key) {
	    return Optional.ofNullable(configMap.get(key))
	            .map(Config::getValue)
	            .orElseThrow(() -> new AppException("Missing config key: " + key));
	}
}