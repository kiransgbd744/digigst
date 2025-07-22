/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva Krishna
 *
 */
@Slf4j
@Component("Gstr6SummaryDataAtGstnImpl")
public class Gstr6SummaryDataAtGstnImpl {
	
	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	
	public String gstr6Summary(Gstr6GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("fnl", GSTConstants.N);
		
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_GETSUM, param1, param2,param3);
		APIResponse resp = apiExecutor.execute(params, null);

		String apiResp = resp.getResponse();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr6 get summery api respons: {}",apiResp);
		}
		return apiResp;
	}
	
	public String gstr6ItcDetailsSummary(Gstr6GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		// APIParam returnPeriodParam = new
		// APIParam("ret_period",dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.GSTR6_RETURN_TYPE);

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR6_GET_ITC, param1, param2, returnTypeParam);

		APIResponse resp = apiExecutor.execute(params, null);

		String apiResp = resp.getResponse();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr6 get ITC Details summary api respons: {}", apiResp);
		}
		return apiResp;
	}
	
	
}