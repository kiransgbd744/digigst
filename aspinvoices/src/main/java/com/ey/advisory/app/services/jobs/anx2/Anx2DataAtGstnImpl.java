package com.ey.advisory.app.services.jobs.anx2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx2DataAtGstnImpl")
@Slf4j
public class Anx2DataAtGstnImpl implements Anx2DataAtGstn {
	
	private static final String LOG1 = "findAnx2DataAtGstn method started for {}";
	private static final String LOG2 = "findAnx2DataAtGstn method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	@Override
	public String findAnx2DataAtGstn(Anx2GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = null;
		String failureHandler = "Anx2GetCommonFailureHandler";
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam param3 = new APIParam("from_time", dto.getFromTime());
		APIParam param4 = new APIParam("doc_action", dto.getAction());
		APIParam param5 = new APIParam("ctin", dto.getCtin());
		
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX2_RETURN_TYPE);
		APIParams params = null;
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && type.equals(APIConstants.B2B)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_B2B, param1, param2, param3, param4,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetB2bSuccessHandler";
			//failureHandler = "Anx2GetB2bFailureHandler";
		} else if (type != null && type.equals(APIConstants.SEZWP)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_SEZWP, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetSezwpSuccessHandler";
			//failureHandler = "Anx2GetSezwpFailureHandler";
		} else if (type != null && type.equals(APIConstants.SEZWOP)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_SEZWOP, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetSezwopSuccessHandler";
			//failureHandler = "Anx2GetSezwopFailureHandler";
		} else if (type != null && type.equals(APIConstants.DE)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_DE, param1, param2, param3, param4,
					param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetDeSuccessHandler";
			//failureHandler = "Anx2GetDeFailureHandler";
		} else if (type != null && type.equals(APIConstants.ISDC)) {
			param4 = new APIParam("action", dto.getAction());
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_ISDC, param1, param2, param3,
					param4, param5, returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetIsdcSuccessHandler";
			//failureHandler = "Anx2GetIsdcFailureHandler";
		} else if (type != null && type.equals(APIConstants.ITCSUM)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.ANX2_GET_ITCSUM, param1, param2, 
					returnPeriodParam, returnTypeParam);
			successHandler = "Anx2GetItcSumSuccessHandler";
			//failureHandler = "Anx2GetItcSumFailureHandler";
		}
		
		/*APIResponse resp = apiExecutor.execute(params, null);
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "failed to get Get ANX2 {} from Gstn";
			LOGGER.error(msg, type);
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		// Build the Json Object.
		String apiResp = resp.getResponse();
		return apiResp;*/
		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId().toString());
		}
		return result.getTransactionId().toString();
	}

}
