/**
 * 
 */
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
import com.ey.advisory.core.api.APIResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2SummaryDataAtGstnImpl")
@Slf4j
public class Anx2SummaryDataAtGstnImpl implements Anx2SummaryDataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public String getAnx2Summary(Anx2GetInvoicesReqDto dto, String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX2_RETURN_TYPE);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX2_GETSUM, param1, param2, returnPeriodParam,
				returnTypeParam);
		APIResponse resp = apiExecutor.execute(params, null);

		String apiResp = resp.getResponse();
		return apiResp;
	}

	@Override
	public String generateAnx2Summary(Anx2GetInvoicesReqDto dto,
			String groupCode, String data) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("rtnprd", dto.getReturnPeriod());
		APIParam returnPeriodParam = new APIParam("ret_period",
				dto.getReturnPeriod());
		APIParam returnTypeParam = new APIParam(APIConstants.RETURN_TYPE,
				APIConstants.ANX2_RETURN_TYPE);
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.ANX2_GENSUM, param1, param2, returnPeriodParam,
				returnTypeParam);
		APIResponse resp = apiExecutor.execute(params, data);

		String apiResp = resp.getResponse();
		return apiResp;
	}

}
