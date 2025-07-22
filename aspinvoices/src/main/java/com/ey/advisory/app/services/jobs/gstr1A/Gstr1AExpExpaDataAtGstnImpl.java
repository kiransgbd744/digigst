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

@Component("gstr1AExpExpaDataAtGstnImpl")
@Slf4j
public class Gstr1AExpExpaDataAtGstnImpl implements Gstr1AExpExpaDataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	@Override
	public Long findExpDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findExpDataAtGstn method {}", type);
		}

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("action_required",
				dto.getAction());
		APIParam param4 = new APIParam("ctin", dto.getCtin());
		APIParam param5 = new APIParam("from_time", dto.getFromTime());
		APIParams params = null;
		if (APIConstants.EXP.equals(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_EXP, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_EXP);
			}
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_EXPA, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_EXPA);
			}
		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr1AExpExpaSuccessHandler", "Gstr1AGetCommonFailureHandler",
				jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();

		/*
		 * APIResponse resp = apiExecutor.execute(params, null); if
		 * (!resp.isSuccess()) { String msg = "Failed to get GSTR1 from Gstn";
		 * if(LOGGER.isErrorEnabled()){ LOGGER.error(msg); } JsonObject resp1 =
		 * new JsonObject(); resp1.add("hrd", new Gson().toJsonTree(new
		 * APIRespDto("E", msg)));
		 * 
		 * } if(LOGGER.isDebugEnabled()){
		 * LOGGER.debug("END OF findExpDataAtGstn method Response: {}",resp.
		 * getResponse()); } return resp.getResponse();
		 */
	}

}
