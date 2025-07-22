package com.ey.advisory.admin.azurebus.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component
public class ITPEventResponseHandler {

	ImmutableMap<String, String> immutableErrorMap = ImmutableMap.of("DIGI102",
			"No Record found for the provided Inputs", "DIGI103",
			"Invalid API Request Parameters", "GEN501",
			"Missing Mandatory Params");

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ITPEventResponseServiceImpl")
	ITPEventResponseService itpEventService;

	public void messageHandler(ServiceBusReceivedMessageContext context) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String inputString = context.getMessage().getBody().toString();
			LOGGER.debug("message {} ", inputString);
			ITPEventRequestDto requestDto = gson.fromJson(inputString,
					ITPEventRequestDto.class);
			ITPEventRequestParamsDto requestParams = null;
			String groupCode = null;
			String category = requestDto.getData().getCategory();
			String appType = requestDto.getData().getAppType();
			if(!category.equalsIgnoreCase(ITPEventConstants.BULK_ONBOARDING_USER)){
				groupCode = requestDto.getData().getTenenatid();
	
			}else{
				requestParams = requestDto.getData().getRequestParams();
			    groupCode = requestParams.getTenantCode();

			}


			if (Strings.isNullOrEmpty(category)
					|| Strings.isNullOrEmpty(appType)) {
				sendMissingMandatoryParamsError(gson, category, appType);
				context.complete();
				return;
			}

			if (appType.equalsIgnoreCase(ITPEventConstants.DIGISAP)) {
				ServiceBusSenderClient senderClient = createSenderClient();
				if (category.equalsIgnoreCase(ITPEventConstants.GROUP)) {
					sendMessage(senderClient,
							itpEventService.getListOfGroups());
				} else if (category.equalsIgnoreCase(ITPEventConstants.ENTITY)
						|| category.equalsIgnoreCase(ITPEventConstants.USER)) {
					if (Strings.isNullOrEmpty(groupCode)) {
						sendMissingMandatoryParamsError(gson, category,
								appType);
						context.complete();
						return;
					}
					TenantContext.setTenantId(groupCode);
					if (category.equalsIgnoreCase(ITPEventConstants.ENTITY)) {
						sendMessage(senderClient,
								itpEventService.getEntitiesDetails(groupCode));
					} else {
						sendMessage(senderClient,
								itpEventService.getUserDetails(groupCode));
					}
				} else if (category
						.equalsIgnoreCase(ITPEventConstants.USER_ONBOARDED)) {
					TenantContext.setTenantId(groupCode);
					itpEventService.updateUserDetails(requestDto);
				}else if (category.equalsIgnoreCase(ITPEventConstants.BULK_ONBOARDING_USER)) {
					
					TenantContext.setTenantId(groupCode);
					sendMessage(senderClient,itpEventService.getBulkUserPermissionsDetails(requestParams));
				}else {
					sendErrorResponse(gson, "DIGI103",
							immutableErrorMap.get("DIGI103"));
				}
			} else {
				sendErrorResponse(gson, "DIGI103",
						immutableErrorMap.get("DIGI103"));
				LOGGER.error("Invalid API Request Parameters");

			}
			context.complete();
		} catch (Exception e) {
			LOGGER.error(
					"Exception while retrieving the Details, Hence abandoning the Message.",
					e);
			context.abandon();
		} finally {
			TenantContext.clearTenant();
		}
	}

	private ServiceBusSenderClient createSenderClient() {
		return new ServiceBusClientBuilder()
				.connectionString(
						commonUtility.getProp("azureBus.connection.string"))
				.sender()
				.queueName(commonUtility.getProp("azureBus.resp.queue"))
				.buildClient();
	}

	private void sendMessage(ServiceBusSenderClient senderClient,
			String messageContent) {
		ServiceBusMessage message = new ServiceBusMessage(messageContent);
		senderClient.sendMessage(message);
		senderClient.close();
	}

	private void sendMissingMandatoryParamsError(Gson gson, String category,
			String appType) {
		sendErrorResponse(gson, "GEN501", immutableErrorMap.get("GEN501"),
				category, appType);
		LOGGER.error("Missing Mandatory Params");
	}

	private void sendErrorResponse(Gson gson, String errorCode,
			String errorMessage) {
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		ITPEventDataErrorDto errorDto = new ITPEventDataErrorDto();
		errorDto.setErrorCode(errorCode);
		errorDto.setMsg(errorMessage);
		eventResponseDto.setError(Arrays.asList(errorDto));
		sendMessage(createSenderClient(), gson.toJson(eventResponseDto));

	}

	private void sendErrorResponse(Gson gson, String errorCode,
			String errorMessage, String category, String appType) {
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		ITPEventDataErrorDto errorDto = new ITPEventDataErrorDto();
		errorDto.setErrorCode(errorCode);
		errorDto.setMsg(errorMessage);
		eventResponseDto.setError(Arrays.asList(errorDto));
		eventResponseDto.setCategory(category);
		eventResponseDto.setAppType(appType);
		sendMessage(createSenderClient(), gson.toJson(eventResponseDto));
	}

}
