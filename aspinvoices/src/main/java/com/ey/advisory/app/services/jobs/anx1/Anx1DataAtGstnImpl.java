/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("anx1DataAtGstnImpl")
@Slf4j
public class Anx1DataAtGstnImpl implements Anx1DataAtGstn {

	private static final String LOG1 = "findAnx1DataAtGstn method started for {}";
	private static final String LOG2 = "findAnx1DataAtGstn method completed with txnId {}";
	
	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public String findAnx1DataAtGstn(Anx1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Anx1GetCommonFailureHandler";
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX1_RETURN_TYPE);
		APIParams params = null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && type.equals(APIConstants.B2C)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_B2C, param1, param2, param3,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetB2cSuccessHandler";
			// failureHandler = "Anx1GetB2cFailureHandler";
		} else if (type != null && type.equals(APIConstants.B2B)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_B2B, param1, param2, param3, param4,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetB2bSuccessHandler";
			// failureHandler = "Anx1GetB2bFailureHandler";
		} else if (type != null && type.equals(APIConstants.B2BA)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_B2BA, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetB2baSuccessHandler";
			// failureHandler = "Anx1GetB2bFailureHandler";
		} else if (type != null && type.equals(APIConstants.REV)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_REV, param1, param2, param3, param4,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetRevSuccessHandler";
			// failureHandler = "Anx1GetRevFailureHandler";
		} else if (type != null && type.equals(APIConstants.ECOM)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_ETIN,
					dto.getEtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_ECOM, param1, param2, param3,
					param4, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetEcomSuccessHandler";
			// failureHandler = "Anx1GetEcomFailureHandler";
		} else if (type != null && type.equals(APIConstants.EXPWOP)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_EXPWOP, param1, param2, param3,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetExpwopSuccessHandler";
			// failureHandler = "Anx1GetExpwopFailureHandler";
		} else if (type != null && type.equals(APIConstants.EXPWP)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_EXPWP, param1, param2, param3,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetExpwpSuccessHandler";
			// failureHandler = "Anx1GetExpwpFailureHandler";
		} else if (type != null && type.equals(APIConstants.MIS)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_MIS, param1, param2, param3, param4,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetMisSuccessHandler";
			// failureHandler = "Anx1GetMisFailureHandler";
		} else if (type != null && type.equals(APIConstants.DE)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_DE, param1, param2, param3, param4,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetDeSuccessHandler";
			// failureHandler = "Anx1GetDeFailureHandler";
		} else if (type != null && type.equals(APIConstants.DEA)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_DEA, param1, param2, param3, param4,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetDeaSuccessHandler";
			// failureHandler = "Anx1GetDeFailureHandler";
		}

		else if (type != null && type.equals(APIConstants.IMPS)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());

			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_IMPS, param1, param2, param3,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetImpsSuccessHandler";
			// failureHandler = "Anx1GetImpsFailureHandler";
		} else if (type != null && type.equals(APIConstants.IMPG)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_IMPG, param1, param2, param3,
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetImpgSuccessHandler";
			// failureHandler = "Anx1GetImpgFailureHandler";
		} else if (type != null && type.equals(APIConstants.IMPGSEZ)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_IMPGSEZ, param1, param2, param3,
					param4, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetImpgSezSuccessHandler";
			// failureHandler = "Anx1GetImpgSezFailureHandler";
		} else if (type != null && type.equals(APIConstants.SEZWP)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_SEZWP, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetSezwpSuccessHandler";
			// failureHandler = "Anx1GetSezwpFailureHandler";
		} else if (type != null && type.equals(APIConstants.SEZWPA)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_SEZWPA, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetSezwpaSuccessHandler";
			// failureHandler = "Anx1GetSezwpFailureHandler";
		} else if (type != null && type.equals(APIConstants.SEZWOP)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_SEZWOP, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetSezwopSuccessHandler";
			// failureHandler = "Anx1GetSezwopFailureHandler";
		}

		else if (type != null && type.equals(APIConstants.SEZWOPA)) {
			APIParam param3 = new APIParam(APIConstants.ANX1_FROMTIME,
					dto.getFromTime());
			APIParam param4 = new APIParam(APIConstants.ANX1_DOCACTION,
					dto.getAction());
			APIParam param5 = new APIParam(APIConstants.ANX1_CTIN,
					dto.getCtin());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX1_GET_SEZWOPA, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx1GetSezwopaSuccessHandler";
			// failureHandler = "Anx1GetSezwopFailureHandler";
		}

		/*
		 * APIResponse resp = apiExecutor.execute(params, null); // If the GSTN
		 * API returns a failure code, then throw an exception // so that we can
		 * update the batch table with the error description. if
		 * (!resp.isSuccess()) { String msg =
		 * "failed to get Get ANX1 {} from Gstn"; JsonObject resp1 = new
		 * JsonObject(); resp1.add("hdr", new Gson().toJsonTree(new
		 * APIRespDto("E", msg))); } // Build the Json Object. String apiResp =
		 * resp.getResponse(); return apiResp;
		 */

		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId().toString());
		}
		return result.getTransactionId().toString();
	}

}
