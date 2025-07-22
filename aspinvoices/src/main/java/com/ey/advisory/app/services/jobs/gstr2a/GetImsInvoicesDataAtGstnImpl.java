package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetImsInvoicesDataAtGstnImpl")
public class GetImsInvoicesDataAtGstnImpl implements Gstr2aDataAtGstn {

	private static final String LOG1 = "GetImsInvoicesDataAtGstnImpl IMS INVOICES method started for {}";
	private static final String LOG2 = "GetImsInvoicesDataAtGstnImpl IMS INVOICES method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	
	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	@Override
	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {
		
		String successHandler = null;
		String failureHandler = "GetImsInvoicesCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("section", type);
		APIParam param3 = new APIParam("ret_period",dto.getReturnPeriod());

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && APIConstants.INV_TYPE_B2B.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.IMS_INVOICE, param1, param2, param3);
			successHandler = "GetImsInvoicesB2bSuccessHandler";
		} 
		
		else if (type != null && GETIMS_SUPPLY_TYPES.contains(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.IMS_INVOICE, param1, param2, param3);
			successHandler = "GetImsInvoicesSectionSuccessHandler";
		} else {
			LOGGER.error(
					"IRN LIST GET type of the sections no matched to make GSTN call.");
			return null;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("IRN LIST GET GSTN API {} section APIParams are  ",
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
