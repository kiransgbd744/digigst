package com.ey.advisory.app.get.notices.handlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("GstNoticeDataAtGstnImpl")
public class GstNoticeDataAtGstnImpl implements GstNoticeDataAtGstn {

	private static final String LOG1 = "GstNoticeDataAtGstnImpl GST NOTICES method started for {}";
	private static final String LOG2 = "GstNoticeDataAtGstnImpl GST NOTICES method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long getGstNoticesData(GstNoticesReqDto dto,
			String groupCode, String type, String jsonReq) {
		
		String successHandler = null;
		String failureHandler = "GstNoticesCommonFailureHandler";
		
		LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = today.format(formatter);
        
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("date",formattedDate);

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && APIConstants.GST_NOTICE_DTL.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GST_NOTICE_DTL, param1, param2);
			successHandler = "GetNoticesSuccessHandler";
		} 
		 else {
			LOGGER.error(
					"GST Notice GET type of the sections no matched to make GSTN call.");
			return null;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GST Notice GSTN API {} section APIParams are  ",
					type, params);
		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}
}
