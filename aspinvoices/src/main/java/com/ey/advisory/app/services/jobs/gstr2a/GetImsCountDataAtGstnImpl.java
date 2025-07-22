package com.ey.advisory.app.services.jobs.gstr2a;

import java.util.Arrays;
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
@Component("GetImsCountDataAtGstnImpl")
public class GetImsCountDataAtGstnImpl implements Gstr2aDataAtGstn {

	private static final String LOG1 = "GetImsCountDataAtGstnImpl IMS COUNT method started for {}";
	private static final String LOG2 = "GetImsCountDataAtGstnImpl IMS COUNT method competed with txnId {}";

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	APIInvoker apiInvoker;

	private final List<String> GETIMS_GOODS_TYPES = ImmutableList
			.copyOf(Arrays.asList(APIConstants.IMS_COUNT_TYPE_ALL_OTH,
					APIConstants.IMS_COUNT_TYPE_INV_SUPP_ISD,
					APIConstants.IMS_COUNT_TYPE_IMP_GDS));

	@Override
	public Long findGstr2aDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		String successHandler = "GetImsCountSectionSuccessHandler";
		String failureHandler = "GetImsCountCommonFailureHandler";

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("goods_typ", type);
		APIParam param3 = new APIParam("ret_period", "000000");

		APIParams params = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(LOG1, type);
		}
		if (type != null && GETIMS_GOODS_TYPES.contains(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.IMS_COUNT, param1, param2, param3);
		} else {
			LOGGER.error(
					"IMS Count GET type of the sections no matched to make GSTN call.");
			return null;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("IMS Count GET GSTN API {} section APIParams are  ",
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
