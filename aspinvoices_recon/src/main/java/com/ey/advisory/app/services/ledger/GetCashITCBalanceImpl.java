package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.GetCashITCBalanceReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("getCashITCBalanceImpl")
public class GetCashITCBalanceImpl implements GetCashITCBalance {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetCashITCBalanceImpl.class);

	@Autowired
	@Qualifier("cashITCBalanceDataAtGstnImpl")
	private CashITCBalanceDataAtGstn balanceDataAtGstn;

	@Override
	public String findBalance(String jsonReq, String groupCode) {

		String apiResp = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			GetCashITCBalanceReqDto dto = gson.fromJson(reqObject,
					GetCashITCBalanceReqDto.class);
			apiResp = balanceDataAtGstn.fromGstn(dto, groupCode);
			//static value
			//apiResp= "{\"cash_bal\":{\"sgst_tot_bal\":0,\"sgst\":{\"intr\":0,\"oth\":0,\"tx\":0,\"fee\":0,\"pen\":0},\"igst_tot_bal\":0,\"cgst\":{\"intr\":0,\"oth\":0,\"tx\":0,\"fee\":0,\"pen\":0},\"cess\":{\"intr\":0,\"oth\":0,\"tx\":0,\"fee\":0,\"pen\":0},\"cess_tot_bal\":0,\"igst\":{\"intr\":0,\"oth\":0,\"tx\":0,\"fee\":0,\"pen\":0},\"cgst_tot_bal\":0},\"itc_bal\":{\"cgst_bal\":0,\"igst_bal\":0,\"sgst_bal\":0,\"cess_bal\":0},\"gstin\":\"33GSPTN0481G1ZA\"}";
			if (apiResp != null) {

			}
		} catch (Exception ex) {

			String msg = "App Exeption";
			LOGGER.error(msg, ex);
			JsonObject resp1 = new JsonObject();
			resp1.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
		JsonObject resp1 = new JsonObject();
		JsonElement respBody = gson.toJsonTree(apiResp);
		resp1.add("resp", respBody);
		new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		return null;
	}

}
