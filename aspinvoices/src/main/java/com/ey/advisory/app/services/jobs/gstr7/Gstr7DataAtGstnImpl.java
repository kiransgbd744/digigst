package com.ey.advisory.app.services.jobs.gstr7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr7GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Component("Gstr7DataAtGstnImpl")
public class Gstr7DataAtGstnImpl implements Gstr7DataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findGstr7DataAtGstn(Gstr7GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = "Gstr7GetTdsDetailsSuccessHandler";
		String failureHandler = "Gstr7GetCommonFailureHandler";

		try {

			// Invoke the GSTN API - Get Return Status API and get the JSON.
			APIParam param1 = new APIParam("gstin", dto.getGstin());
			APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());

			APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
					APIConstants.GSTR7_RETURN_TYPE);
			APIParam sectionTypeParam = new APIParam("type",
					dto.getType());

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR7_GET_TDS, param1, param2,
					returnTypeParam, sectionTypeParam);

			APIInvocationResult result = apiInvoker.invoke(params, null,
					successHandler, failureHandler, jsonReq);

			return result.getTransactionId();
		} catch (Exception e) {
			String msg = "Exception While Invoking the Wrapper Framework for Gstr7";
			LOGGER.error(msg, e);
			throw new APIException(msg);
		}
	}

}
