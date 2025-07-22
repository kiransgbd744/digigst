package com.ey.advisory.app.services.jobs.ret;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("RetDataAtGstnImpl")
@Slf4j
public class RetDataAtGstnImpl implements RetDataAtGstn {
	
	private static final String LOG1 = "findRetDataAtGstn method started for {}";
	private static final String LOG2 = "findRetDataAtGstn method completed with txnId {}";
	
	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;
	
	@Override
	public String findRetDataAtGstn(RetGetInvoicesReqDto dto, String groupCode,
			String jsonReq) {

		String successHandler = "RetGetCommonSuccessHandler";
		String failureHandler = "RetGetCommonFailureHandler";
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.RET_RETURN_TYPE);
		APIParams params = null;

		params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.RET_GET, param1, param2, returnPeriodParam,
				returnTypeParam);

		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId().toString());
		}
		return result.getTransactionId().toString();
	}

}
