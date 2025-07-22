package com.ey.advisory.gstnapi;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component("DefaultFailureHandler")
@Slf4j
public class DefaultFailureHandler implements FailureHandlerWrapper {

	@Override
	public void handleFailure(String failureHandler, FailureResult result,
			String apiParams) {

		String status = APIConstants.FAILED;
		String errorCode = result.getError().getErrorCode();
		String errorDesc = result.getError().getErrorDesc();

		if (APIConstants.NO_DOCUMENT_FOUND.equalsIgnoreCase(errorCode)
				|| APIConstants.NO_DOCUMENT_FOUND1.equalsIgnoreCase(errorCode)
				|| APIConstants.NO_DOCUMENT_FOUND2.equalsIgnoreCase(errorCode)
				|| APIConstants.NO_DOCUMENT_FOUND3.equalsIgnoreCase(errorCode)
				|| APIConstants.NO_DOCUMENT_FOUND4.equalsIgnoreCase(errorCode)
				|| APIConstants.NO_DOCUMENT_FOUND5
						.equalsIgnoreCase(errorCode)) {
			status = APIConstants.SUCCESS_WITH_NO_DATA;
		}

		GstinGetStatusService gstinGetStatusService = StaticContextHolder
				.getBean("GstinGetStatusServiceImpl",
						GstinGetStatusService.class);

		JsonObject inpJson = (new JsonParser()).parse(apiParams)
				.getAsJsonObject();

		JsonObject apiParams1 = inpJson.getAsJsonObject("apiParams");

		gstinGetStatusService.saveOrUpdateGSTNGetStatus(apiParams1.toString(),
				status, errorDesc);

		StaticContextHolder.getBean(failureHandler, FailureHandler.class)
				.handleFailure(result, apiParams);
	}

}
