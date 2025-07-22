package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("cashITCBalanceDataAtGstnImpl")
public class CashITCBalanceDataAtGstnImpl
		implements CashITCBalanceDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CashITCBalanceDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Override
	public String fromGstn(GetCashITCBalanceReqDto dto, String groupCode) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getRetPeriod());
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.LEDGER_GET_BAL, param1, param2);
		APIResponse resp = apiExecutor.execute(params, null);
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "Failed to do Get Cash ITC Balance from Gstn";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg);
			}
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		// Build the Json Object.
		return resp.getResponse();
	}

}
