package com.ey.advisory.app.services.jobs.gstr1A;

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
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Component("gstr1AB2clB2claDataAtGstnImpl")
@Slf4j
public class Gstr1AB2clB2claDataAtGstnImpl implements Gstr1AB2clB2claDataAtGstn {

	
	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	/**
	 * find BTCL data from Gstin 
	 */
	public Long findB2CLDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("I am from findBTCLDataAtGstn method: {}",type);
		}
		
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("action_required",
				dto.getAction());
		APIParam param4 = new APIParam("ctin", dto.getCtin());
		APIParam param5 = new APIParam("from_time", dto.getFromTime());
		APIParams params = null;
		if (APIConstants.B2CL.equals(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_B2CL, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_B2CL);
			}
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_B2CLA, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_B2CLA);

			}
		}
		
		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr1AB2clB2claSuccessHandler", "Gstr1AGetCommonFailureHandler",
				jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();
		
		/*APIResponse resp = apiExecutor.execute(params, null);
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "failed to get Gstr1 BTCL from Gstn";
				LOGGER.error(msg);	
			
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		apiResp = resp.getResponse();
		LOGGER.info(
				"End of findBTCLDataAtGstn method Repsonse: {}", resp);
		return apiResp;*/
	}

	
}
