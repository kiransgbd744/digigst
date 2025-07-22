package com.ey.advisory.app.services.ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.ledger.RetLiabBalanceReqDto;
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
@Service("getRetLiabBalanceImpl")
public class GetRetLiabBalanceImpl implements GetRetLiabBalance {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetRetLiabBalanceImpl.class);

	@Autowired
	@Qualifier("retLiabBalanceDataAtGstnImpl")
	private RetLiabBalanceDataAtGstnImpl taxPayable;
	
	@Override
	public String findTaxPayable(String jsonReq, String groupCode) {

		String apiResp = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			RetLiabBalanceReqDto dto = gson.fromJson(reqObject,
					RetLiabBalanceReqDto.class);
			apiResp = taxPayable.fromGstn(dto, groupCode);
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
