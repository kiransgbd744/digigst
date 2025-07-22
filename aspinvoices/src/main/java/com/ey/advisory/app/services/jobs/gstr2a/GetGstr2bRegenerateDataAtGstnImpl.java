package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetGstr2bRegenerateDataAtGstnImpl")
public class GetGstr2bRegenerateDataAtGstnImpl implements Gstr2aDataAtGstn {

	private static final String LOG1 = "GetGstr2bRegenerateDataAtGstnImpl  method started for {}";
	private static final String LOG2 = "GetGstr2bRegenerateDataAtGstnImpl  method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;
	
	@Override
	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {
		
		String successHandler = "GetGstr2bRegenerateSuccessHandler";
		String failureHandler = "GetGstr2bRegenerateFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("rtin", dto.getGstin());
		APIParam param2 = new APIParam("itcprd",dto.getReturnPeriod());

		APIParams params = null;
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2B_REGENERATE, param1, param2);

			
	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" GSTR2b_regenerate {} section APIParams are  ",params);
		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}
}
