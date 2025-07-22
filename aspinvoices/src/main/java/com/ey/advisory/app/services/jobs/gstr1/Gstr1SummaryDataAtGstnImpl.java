package com.ey.advisory.app.services.jobs.gstr1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Component("gstr1SummaryDataAtGstnImpl")
public class Gstr1SummaryDataAtGstnImpl implements Gstr1SummaryDataAtGstn {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse findSummaryDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("smrytyp", "L");
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1_GET_SUMMARY, param1, param2, param3);
		APIResponse resp = apiExecutor.execute(params, null);
		return resp;
	}
	
	@Override
	public APIResponse findGstr1ASummaryDataAtGstn(Gstr1GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());
		APIParam param3 = new APIParam("smrytyp", "L");
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR1A_GET_SUMMARY, param1, param2, param3);
		APIResponse resp = apiExecutor.execute(params, null);
		return resp;
	}

}
