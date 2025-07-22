/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("Anx1SummaryDataAtGstnImpl")
public class Anx1SummaryDataAtGstnImpl implements Anx1SummaryDataAtGstn {
	
	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public String getAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX1_RETURN_TYPE);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX1_GETSUM, param1, param2, returnPeriodParam,
				returnTypeParam);
		APIResponse resp = apiExecutor.execute(params, null);

		String apiResp = resp.getResponse();
		return apiResp;
	}
	
	@Override
	public String generateAnx1Summary(Anx1GetInvoicesReqDto dto,
			String groupCode, String data) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX1_RETURN_TYPE);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX1_GENSUM, param1, param2, returnPeriodParam,
				returnTypeParam);
		APIResponse resp = apiExecutor.execute(params, data);

		String apiResp = resp.getResponse();
		return apiResp;
	}

}