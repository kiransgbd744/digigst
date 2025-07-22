package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Slf4j
@Component("Gstr2xDataAtGstnImpl")
public class Gstr2xDataAtGstnImpl implements Gstr2XDataAtGstn {

	private static final String LOG1 = "findGstr2XDataAtGstn method started for {}";
	private static final String LOG2 = "findGstr2XDataAtGstn method competed with txnId {}";

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public Long findGstr2xDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Gstr2xGetCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("from_time", dto.getFromTime());
		APIParam param5 = new APIParam("rec_type", dto.getRecType());
		/*
		 * APIParam param4 = new APIParam("ctin", dto.getCtin()); APIParam
		 * param6 = new APIParam("be_number", dto.getBeNum()); APIParam param7 =
		 * new APIParam("be_date", dto.getBeDate());
		 */
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR2X_RETURN_TYPE);

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && APIConstants.TCSANDTDS.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR2X_GET_TCS_TDS, param1, param2, param3,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Gstr2XGetTdsSuccessHandler";
		} else {
			LOGGER.error(
					"GSTR2X GET non of the sections matched to make GSTN call.");
			return null;

		}

		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}

}
