package com.ey.advisory.app.services.jobs.gstr2a;

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
import com.ey.advisory.core.dto.Gstr2aDataAtGstn;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2aDataAtGstnImpl")
public class Gstr2aDataAtGstnImpl implements Gstr2aDataAtGstn {

	private static final String LOG1 = "findGstr2ADataAtGstn method started for {}";
	private static final String LOG2 = "findGstr2ADataAtGstn method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto, String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Gstr2aGetCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("from_time", dto.getFromTime());
		APIParam param4 = new APIParam("port_code", dto.getPortCode());
		APIParam param5 = new APIParam("ctin", dto.getCtin());
		APIParam param6 = new APIParam("be_number",
				dto.getBeNum() != null ? dto.getBeNum().toString() : null);
		APIParam param7 = new APIParam("be_date", dto.getBeDate());
		APIParam returnPeriodParam = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE, APIConstants.GSTR2A_RETURN_TYPE);

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && APIConstants.B2B.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_B2B, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetB2bSuccessHandler";
		} else if (type != null && APIConstants.B2BA.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_B2BA, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetB2bSuccessHandler";
		} else if (type != null && APIConstants.CDN.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_CDN, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetCdnSuccessHandler";
		} else if (type != null && APIConstants.CDNA.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_CDNA, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetCdnSuccessHandler";
		} else if (type != null && APIConstants.ISD.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_ISD, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetIsdSuccessHandler";
		} else if (type != null && APIConstants.ISDA.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_ISDA, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetIsdSuccessHandler";
		} else if (type != null && APIConstants.TDS.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_TDS, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetTdsSuccessHandler";
		} else if (type != null && APIConstants.TDSA.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_TDSA, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetTdsSuccessHandler";
		} else if (type != null && APIConstants.TCS.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_TCS, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetTcsSuccessHandler";
		} else if (type != null && APIConstants.AMDHIST.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_AMDHIST, param1, param4,
					param6, param7, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetAmdhistSuccessHandler";
		} else if (type != null && APIConstants.IMPG.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_IMPG, param1, param2,
					param3, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetImpgSuccessHandler";
		} else if (type != null && APIConstants.IMPGSEZ.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_IMPGSEZ, param1, param2,
					param3, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetImpgSezSuccessHandler";
		} else if (type != null && APIConstants.ECOM.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_ECOM, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetEcomSuccessHandler";
		} else if (type != null && APIConstants.ECOMA.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN, APIIdentifiers.GSTR2A_GET_ECOMA, param1, param2,
					param3, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2aGetEcomSuccessHandler";
		}else {
			LOGGER.error("GSTR2A GET non of the sections matched to make GSTN call.");
			return null;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR2a GET GSTN API {} section APIParams are  ", type,
					params);
		}
		APIInvocationResult result = apiInvoker.invoke(params, null, successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}

}
