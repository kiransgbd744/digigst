package com.ey.advisory.app.services.jobs.itc04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Component("Itc04DataAtGstnImpl")
public class Itc04DataAtGstnImpl implements Itc04DataAtGstn {

	private static final String LOG1 = "findItc04DataAtGstn method started for {}";
	private static final String LOG2 = "findItc04DataAtGstn method completed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findItc04DataAtGstn(Itc04GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Itc04GetCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());

		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ITC04_RETURN_TYPE);
		APIParams params = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}

		params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ITC04_GET_INVOICES, param1, param2,
				returnTypeParam);
		successHandler = "Itc04GetInvoicesDetailsSuccessHandler";

		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}

}
