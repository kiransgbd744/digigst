package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.List;

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
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetIrnDataAtGstnImpl")
public class GetIrnDataAtGstnImpl implements Gstr2aDataAtGstn {

	private static final String LOG1 = "GetIrnDataAtGstnImpl IRN LIST method started for {}";
	private static final String LOG2 = "GetIrnDataAtGstnImpl IRN LIST method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	private static final List<String> GETIRN_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.INV_TYPE_SEZWP, APIConstants.INV_TYPE_SEZWOP,
			APIConstants.INV_TYPE_DEXP, APIConstants.INV_TYPE_EXPWP,
			APIConstants.INV_TYPE_EXPWOP);

	@Override
	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {
		if(APIConstants.INV_TYPE_DXP.equalsIgnoreCase(dto.getType())){
        	dto.setType(APIConstants.INV_TYPE_DEXP);
		} 
			
		if(APIConstants.INV_TYPE_DXP.equalsIgnoreCase(type)){
			type=APIConstants.INV_TYPE_DEXP;
		}
		
		String successHandler = null;
		String failureHandler = "GetIrnListCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("rtin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd",dto.getReturnPeriod());
		APIParam param3 = new APIParam("suptyp", type);
		APIParam param4 = new APIParam("gstin", dto.getGstin());
		APIParam param5 = new APIParam("ret_period",dto.getReturnPeriod());

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && APIConstants.INV_TYPE_B2B.equalsIgnoreCase(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GET_IRN_JSON, param1, param2, param3,
					param4,param5);
			successHandler = "GetIrnListB2bSuccessHandler";
		} else if (type != null && GETIRN_SUPPLY_TYPES.contains(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GET_IRN_JSON, param1, param2, param3,
					param4,param5);
			successHandler = "GetInwardIrnListSectionSuccessHandler";
		} else {
			LOGGER.error(
					"IRN LIST GET type of the sections no matched to make GSTN call.");
			return null;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("IRN LIST GET GSTN API {} section APIParams are  ",
					type, params);
		}
		APIInvocationResult result = apiInvoker.invoke(params, null,
				successHandler, failureHandler, jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG2, result.getTransactionId());
		}
		return result.getTransactionId();
	}
}
