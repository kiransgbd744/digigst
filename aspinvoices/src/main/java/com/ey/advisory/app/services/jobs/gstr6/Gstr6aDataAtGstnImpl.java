package com.ey.advisory.app.services.jobs.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.gstr6a.Gstr6aDataAtGstn;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr6aDataAtGstnImpl")
@Slf4j
public class Gstr6aDataAtGstnImpl implements Gstr6aDataAtGstn {

	private static final String LOG1 = "findGstr6aDataAtGstn method started for {}";
	private static final String LOG2 = "findGstr6aDataAtGstn method completed with txnId {}";
	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findGstr6aDataAtGstn(Gstr6aGetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Gstr6aGetCommonFailureHandler";
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());

		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR6A_RETURN_TYPE);
		APIParams params = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && type.equals(APIConstants.B2B)) {

			APIParam param5 = new APIParam(APIConstants.GSTR6A_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6A_GET_B2B, param1, param2, param5,
					returnTypeParam);
			successHandler = "Gstr6aGetB2bSuccessHandler";

		}
		if (type != null && type.equals(APIConstants.B2BA)) {

			APIParam param5 = new APIParam(APIConstants.GSTR6A_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6A_GET_B2BA, param1, param2, param5,
					returnTypeParam);
			successHandler = "Gstr6aGetB2baSuccessHandler";

		}
		if (type != null && type.equals(APIConstants.CDN)) {

			APIParam param5 = new APIParam(APIConstants.GSTR6A_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6A_GET_CDN, param1, param2, param5,
					returnTypeParam);
			successHandler = "Gstr6aGetCdnSuccessHandler";

		}
		if (type != null && type.equals(APIConstants.CDNA)) {

			APIParam param5 = new APIParam(APIConstants.GSTR6A_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6A_GET_CDNA, param1, param2, param5,
					returnTypeParam);
			successHandler = "Gstr6aGetCdnaSuccessHandler";

		}

		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}

}
