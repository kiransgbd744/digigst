package com.ey.advisory.app.asb.service;

import org.springframework.stereotype.Component;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("ASBProducerCommonService")
public class ASBProducerCommonService {

	private static final String QUEUE_NAME = "sapqueue";

	private static final String CONN_STR = "Endpoint=sb://ci-dv-td-sbtd01.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=tTc4Bd/VSZMT0Mfp/bxpIzeGjdciSeMlN54h2z2j94A=";

	private static final String CONN_STR_QA = "Endpoint=sb://ci-qa-td-sb01.servicebus.windows.net/;SharedAccessKeyName=SendAccessKey;SharedAccessKey=GduVXSqx8td5yrPLRWUtysmjTgKaT3VD8+ASbAP6tyk=";
	
	public String produceMessage(String tableType, String requestType,
			String appId, String groupCode, Long fileId, String payloadId) {

		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			jsonObj.addProperty("tableType", tableType);
			jsonObj.addProperty("requestType", requestType);
			jsonObj.addProperty("appId", appId);
			jsonObj.addProperty("groupCode", groupCode);

			if (fileId != null) {
				jsonObj.addProperty("fileId", fileId.toString());
			} else {
				jsonObj.addProperty("payLoadId", payloadId);
			}

			ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
					.connectionString(CONN_STR_QA).sender().queueName(QUEUE_NAME)
					.buildClient();
			ServiceBusMessage message = new ServiceBusMessage(
					gson.toJson(jsonObj));
			LOGGER.debug(" jsonString send to DWH - {}", gson.toJson(jsonObj));

			senderClient.sendMessage(message);
			senderClient.close();
			
			return "SUCCESS";

		} catch (Exception e) {
			LOGGER.error("Exception while publishing the Message  ", e);

			return "FAILURE";
		}

	}

}