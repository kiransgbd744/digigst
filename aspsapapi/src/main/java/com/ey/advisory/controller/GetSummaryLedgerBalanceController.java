package com.ey.advisory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.ledger.GetSummaryLedgerBalance;
import com.ey.advisory.app.services.ledger.GetSummaryLedgerBalanceDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@RestController
public class GetSummaryLedgerBalanceController {

	@Autowired
	@Qualifier("GetSummaryLedgerBalanceImpl")
	GetSummaryLedgerBalance getSummaryLedgerBalance;

	@PostMapping(value = "/ui/GetSummaryLedgerBalance", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getSummaryLedgerBalance(
			@RequestBody String jsonReq, HttpServletRequest request) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for GetSummaryLedgerBalance with" + " params : %s",
					jsonReq);
			LOGGER.debug(msg);
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			//Long entityId = reqObject.get("entityId").getAsLong();
			//String taxPeriod = reqObject.get("taxPeriod").getAsString();
			
			Gstr1GetInvoicesReqDto reqDto = gson
					.fromJson(reqObject, Gstr1GetInvoicesReqDto.class);
			
			List<GetSummaryLedgerBalanceDto> apiResp = getSummaryLedgerBalance
					.getSummaryLedgerBalanceDetails(reqDto);

			JsonElement reponseBody = gson.toJsonTree(apiResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", reponseBody);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Response for Ledger Summary : %s",
						apiResp);
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String.format(
					"Error Occured while Fetching Ledger Data From DB '%s'",
					e.getMessage());
			APIRespDto dto = new APIRespDto("Failed", msg);
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

}
