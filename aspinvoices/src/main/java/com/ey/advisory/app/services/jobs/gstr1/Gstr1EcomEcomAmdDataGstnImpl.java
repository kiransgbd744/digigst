package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr1EcomEcomAmdDataGstnImpl")
@Slf4j
public class Gstr1EcomEcomAmdDataGstnImpl implements Gstr1EcomEcomAmdDataGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	@Override
	public Long findEcomDataGstn(Gstr1GetInvoicesReqDto dto, String groupCode,
			String type, String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("I am from findSupEcomDataATGstn " + " Group Code: ",
					groupCode);
		}
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParams params = null;
		if (APIConstants.ECOM.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1_GET_ECOM, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1_GET_ECOM);
			}
		} else {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR1_GET_ECOM_AMD, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR1_GET_ECOM_AMD);
			}
		}
		APIResponse response = apiExecutor.execute(params, null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("error: {}", response.getError().getErrorDesc());
		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr1EcomEcomAmdSuccessHandler", "Gstr1GetCommonFailureHandler",
				jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();
	}
}
