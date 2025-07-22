package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
 * @author Anand3.M
 *
 */
@Component("gstr1EInvDataAtGstnImpl")
@Slf4j
public class Gstr1EInvDataAtGstnImpl implements Gstr1EInvDataAtGstn {

	private static final String LOG1 = "findGstr1EinvDataAtGstn method started for {}";
	private static final String LOG2 = "findGstr1EinvDataAtGstn method completed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	@Override
	public Long findEInvDataAtGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, String jsonReq) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		LOGGER.debug("{} section GET API frame work call is initiated",
				 type.toUpperCase());
		APIParam param3 = new APIParam("sec", type.toUpperCase());

		
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1_GET_EINV, param1, param2, param3);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} section GET API frame work call is initiated",
					APIIdentifiers.GSTR1_GET_EINV);
		}

		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr1EInvSuccessHandler", "Gstr1GetCommonFailureHandler",
				jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();

		/*
		 * APIResponse resp = apiExecutor.execute(params, null); // If the GSTN
		 * API returns a failure code, then throw an exception // so that we can
		 * update the batch table with the error description. if
		 * (!resp.isSuccess()) { String msg =
		 * "failed to get Get Gstr1 B2B from Gstn"; if(LOGGER.isErrorEnabled()){
		 * LOGGER.error(msg); } JsonObject resp1 = new JsonObject();
		 * resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg))); }
		 * // Build the Json Object. return resp.getResponse();
		 */
	}

}
