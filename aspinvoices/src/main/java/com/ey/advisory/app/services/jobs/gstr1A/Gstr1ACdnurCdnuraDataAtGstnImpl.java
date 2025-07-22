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

@Component("gstr1ACdnurCdnuraDataAtGstnImpl")
@Slf4j
public class Gstr1ACdnurCdnuraDataAtGstnImpl
		implements Gstr1ACdnurCdnuraDataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	APIExecutor apiExcecuter;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	@Override
	public Long findCdnurCdnuraDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"I am from findCdnurCdnuraDataAtGstn " + " Group Code: ",
					groupCode);
		}

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("action_required",
				dto.getAction());
		APIParam param4 = new APIParam("ctin", dto.getCtin());
		APIParam param5 = new APIParam("from_time", dto.getFromTime());
		APIParams params = null;

		if (APIConstants.CDNUR.equals(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_CDNUR, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_CDNUR);
			}
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1A_GET_CDNURA, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1A_GET_CDNURA);
			}

		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr1ACdnurCdnuraSuccessHandler",
				"Gstr1AGetCommonFailureHandler", jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();

		/*
		 * APIResponse response = apiExcecuter.execute(params, null); if
		 * (!response.isSuccess()) { String msg =
		 * "Failed to get Gstr1 CDNR from GSTN"; if (LOGGER.isErrorEnabled()) {
		 * LOGGER.error(msg); } JsonObject resp1 = new JsonObject();
		 * resp1.add("hrd", new Gson().toJsonTree(new APIRespDto("E", msg))); }
		 * if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug("End of findCdnurCdnuraDataAtGstn Response: {}" +
		 * response.getResponse()); } return response.getResponse();
		 */
	}

}
