package com.ey.advisory.controllers.anexure1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx1.counterparty.CounterPartyDetailService;
import com.ey.advisory.app.anx1.counterparty.CounterPartyDetailsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Anx1CounterPartyDetailController {
	
	@Autowired
	@Qualifier("CounterPartyDetailServiceImpl")
	private CounterPartyDetailService cpService;
		
	@PostMapping(value="/ui/getCounterPartyDetail" ,
						produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getCounterPartyDetail(@RequestBody
			String jsonString){
		
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin CounterPartyDetailServiceImpl"
						+ ".getAllCounterPartyDetails ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String sgstin = requestObject.get("gstins").getAsString();
				
			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartyDetailServiceImpl"
						+ ".getAllCounterPartyDetails,"
						+ "Input Request has been parsed and validated,"
						+ "calling CounterPartyDetailService ";
				LOGGER.debug(msg);
			}
			List<String> sgstins = new ArrayList<>();
			sgstins.add(sgstin);
			
			validateInput(taxPeriod, sgstins);
			
			List<CounterPartyDetailsDto> summary = 
					cpService.getAllCounterPartyDetails(
							sgstins, taxPeriod);
			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartyDetailServiceImpl"
					+ ".getAllCounterPartyDetails Preparing Response Object";
				LOGGER.debug(msg);
			}
			
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(summary);
			detResp.add("det", respBody);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End CounterPartyDetailServiceImpl"
					+ ".getAllCounterPartyDetails, before returning response";
				LOGGER.debug(msg);
			}
			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {

			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}
	private void validateInput(String taxPeriod, List<String> sgstins) {

		String msg = null;

		if (StringUtils.isBlank(taxPeriod)) {
			msg = "Tax period cannot be empty.";
		} else if (sgstins == null || sgstins.isEmpty()) {
			msg = "At least one gstin should be selected.";

		}

		if (msg != null) {
			throw new AppException(msg);
		}
	}

}
