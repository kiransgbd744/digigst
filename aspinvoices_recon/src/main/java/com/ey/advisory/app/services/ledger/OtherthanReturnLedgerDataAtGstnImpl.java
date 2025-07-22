package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetOtherthanReturnLedgerReqDto;
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
@Service("otherthanReturnLedgerDataAtGstnImpl")
public class OtherthanReturnLedgerDataAtGstnImpl
		implements OtherthanReturnLedgerDataAtGstn {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CashITCBalanceDataAtGstnImpl.class);

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;
	
	@Override
	public String fromGstn(GetOtherthanReturnLedgerReqDto dto,
			String groupCode) {
		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("fromDate", dto.getFromDate());
		APIParam param3 = new APIParam("toDate", dto.getToDate());
		APIParam param4 = new APIParam("demid", dto.getDemid());
		APIParam param5 = new APIParam("stayStatus", dto.getStayStatus());
		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.LEDGER_GET_NRTN, param1, param2, param3);
		APIResponse resp = apiExecutor.execute(params, null);
		// If the GSTN API returns a failure code, then throw an exception
		// so that we can update the batch table with the error description.
		if (!resp.isSuccess()) {
			String msg = "Failed to do Get Other than Return Ledger from Gstn";
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
