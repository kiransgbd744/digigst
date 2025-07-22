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

import com.ey.advisory.app.returnfiling.ReturnFilingCounterParty;
import com.ey.advisory.app.returnfiling.ReturnFilingCounterPartyStatusDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@RestController
@Slf4j
public class ReturnFilingCounterPartyStatusController {

	@Autowired
	@Qualifier("ReturnFilingCounterPartyImpl")
	ReturnFilingCounterParty returnFilingCounterParty;

	@PostMapping(value = "/ui/ReturnFilingCounterParty", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCounterPartyStatus(
			@RequestBody String jsonReq) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String userName = json.get("user").getAsString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getting TaxPayer Config Details :");
			}

			List<ReturnFilingCounterPartyStatusDto> counterPartyStatus = 
					returnFilingCounterParty.getCounterPartyStatus(userName);
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(counterPartyStatus);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}

	}

}
