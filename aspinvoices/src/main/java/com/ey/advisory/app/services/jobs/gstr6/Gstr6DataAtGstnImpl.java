package com.ey.advisory.app.services.jobs.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Component("Gstr6DataAtGstnImpl")
public class Gstr6DataAtGstnImpl implements Gstr6DataAtGstn {

	private static final String LOG1 = "findGstr6DataAtGstn method started for {}";
	private static final String LOG2 = "findGstr6DataAtGstn method completed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findGstr6DataAtGstn(Gstr6GetInvoicesReqDto dto, String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Gstr6GetCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		// APIParam returnPeriodParam = new
		// APIParam("ret_period",dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE, APIConstants.GSTR6_RETURN_TYPE);
		APIParams params = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && type.equals(APIConstants.B2B)) {
			APIParam param3 = new APIParam(APIConstants.GSTR6_FROMTIME, dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.GSTR6_DOCACTION, dto.getAction());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_B2B, param1, param2,
					param3, param4, returnTypeParam);
			successHandler = "Gstr6GetB2bSuccessHandler";
			// failureHandler = "Gstr6GetB2bFailureHandler";
		}
		if (type != null && type.equals(APIConstants.B2BA)) {
			// APIParam param3 = new APIParam(APIConstants.GSTR6_FROMTIME,
			// dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.GSTR6_DOCACTION, dto.getAction());
			// APIParam param5 = new
			// APIParam(APIConstants.ANX1_CTIN,dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_B2BA, param1, param2,
					param4, returnTypeParam);
			successHandler = "Gstr6GetB2baSuccessHandler";
			// failureHandler = "Gstr6GetB2baFailureHandler";
		}
		if (type != null && type.equals(APIConstants.CDN)) {
			APIParam param3 = new APIParam(APIConstants.GSTR6_FROMTIME, dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.GSTR6_DOCACTION, dto.getAction());
			// APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
			// dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_CDN, param1, param2,
					param3, param4, returnTypeParam);
			successHandler = "Gstr6GetCdnSuccessHandler";
			// failureHandler = "Gstr6GetCdnFailureHandler";
		}
		if (type != null && type.equals(APIConstants.CDNA)) {
			// APIParam param3 = new APIParam(APIConstants.GSTR6_FROMTIME,
			// dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.GSTR6_DOCACTION, dto.getAction());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_CDNA, param1, param2,
					param4, returnTypeParam);
			successHandler = "Gstr6GetCdnaSuccessHandler";
			// failureHandler = "Gstr6GetCdnaFailureHandler";
		}
		if (type != null && type.equals(APIConstants.LATEFEE)) {
			// APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
			// dto.getFromTime());
			// APIParam param4 = new
			// APIParam(APIConstants.ANX1_ETIN,dto.getEtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_LATEFEE, param1, param2,
					returnTypeParam);
			successHandler = "Gstr6GetLateFeeSuccessHandler";
			// failureHandler = "Anx1GetEcomFailureHandler";
		}
		if (type != null && type.equals(APIConstants.ISD)) {
			// APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
			// dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_ISD, param1, param2,
					returnTypeParam);
			successHandler = "Gstr6GetIsdSuccessHandler";
			// failureHandler = "Anx1GetExpwopFailureHandler";
		}

		if (type != null && type.equals(APIConstants.ISDA)) {
			// APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
			// dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_ISDA, param1, param2,
					returnTypeParam);
			successHandler = "Gstr6GetIsdaSuccessHandler";
			// failureHandler = "Anx1GetExpwopFailureHandler";
		}
		if (type != null && type.equals(APIConstants.ITC)) {
			// APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
			// dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR6_GET_ITC, param1, param2,
					returnTypeParam);
			successHandler = "Gstr6GetItcSuccessHandler";
			// failureHandler = "Anx1GetExpwpFailureHandler";
		}

		APIInvocationResult result = apiInvoker.invoke(params, null, successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}

}
