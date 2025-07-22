package com.ey.advisory.app.services.jobs.gstr8;

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
import com.ey.advisory.gstnapi.APIInvocationResult;
import com.ey.advisory.gstnapi.APIInvoker;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr8UrdUrdaDataAtGstnImpl")
@Slf4j
public class Gstr8UrdUrdaDataAtGstnImpl implements Gstr8UrdUrdaDataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;
	
	@Autowired
	@Qualifier("APIInvokerDefaultImpl")
	private APIInvoker apiInvoker;

	@Override
	public Long findUrdUrdaDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode, String type, String jsonReq) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("ret_type",
				dto.getAction());
		APIParam param4 = new APIParam("from_time", dto.getFromTime());
		APIParams params = null;
		if (APIConstants.URD.equals(type)) {
			params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR8_GET_URD, param1, param2);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} section GET API frame work call is initiated",
						APIIdentifiers.GSTR8_GET_URD);
			}
		}
		
		APIInvocationResult result = apiInvoker.invoke(params, null,
				"Gstr8UrdUrdaSuccessHandler", "Gstr1GetCommonFailureHandler",
				jsonReq);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET Framework Call is been completed with transactionId {}",
					result.getTransactionId().toString());
		}
		return result.getTransactionId();
	}

}
