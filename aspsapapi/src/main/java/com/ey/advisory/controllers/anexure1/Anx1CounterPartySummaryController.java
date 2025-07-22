package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx1.counterparty.CounterPartyInfoResponseSummaryDto;
import com.ey.advisory.app.anx1.counterparty.CounterPartySummaryService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Anx1CounterPartySummaryController {
	
	@Autowired
	@Qualifier("CounterPartySummaryServiceImpl")
	private CounterPartySummaryService cpService;
	
	@PostMapping(value="/ui/getCounterPartySummary",
						produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getCounterPartySummary(@RequestBody 
	String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject req = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String sgstin = req.get("gstins").getAsString();
			Gson googleJson = new Gson();

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			JsonArray tableSection = req.has("tableSection")
					&& req.get("tableSection").isJsonArray()
							? req.getAsJsonArray("tableSection") : null;

			ArrayList<String> tableSections = googleJson.fromJson(tableSection,
					listType);

			JsonArray docType = req.has("docType") && 
					req.get("docType").isJsonArray()?
							req.getAsJsonArray("docType") : null;
			ArrayList<String> docTypes = googleJson.fromJson(docType, listType);

			String taxPeriod = req.get("taxPeriod").getAsString();
			
			List<String> sgstins = new ArrayList<>();
			sgstins.add(sgstin);
			validateInput(taxPeriod, sgstins);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary ,"
						+ "Input Request has been parsed and validated,"
						+ "calling CounterPartySummaryService ";
				LOGGER.debug(msg);
			}
			
			List<CounterPartyInfoResponseSummaryDto> summary = cpService
					.getCounterPartySummary(sgstins, taxPeriod, tableSections,
							docTypes);
			if (LOGGER.isDebugEnabled()) {
				String msg = "CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary Preparing Response Object";
				LOGGER.debug(msg);
			}
			
			/**
			 * we are using stream further on response to get SAVED and
			 * NOT-SAVED action type for calculating Processed Action Type.
			 */
			
			CounterPartyInfoResponseSummaryDto processed = summary.stream()
					.filter(cprs -> cprs.getType().equals("S")
							|| cprs.getType().equals("NS"))
					.collect(Collectors.reducing(
							new CounterPartyInfoResponseSummaryDto(),
							(cprs1, cprs2) -> addToCPRS(cprs1, cprs2)));
			
			
			/*CounterPartyInfoResponseSummaryDto processed = summary.stream()
			        .filter(cprs -> "S".equals(cprs.getType()) || "NS".equals(cprs.getType()))
			        .findFirst()
			        .orElse(null);*/

			
			summary.add(processed);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(summary);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End CounterPartySummaryServiceImpl"
						+ ".getCounterPartySummary ,before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}
	/*
	 * We are using this method as accumulator to accumulate SAVED and 
	 * NOT-SAVED action type of respective field into one object. 
	 */

	private CounterPartyInfoResponseSummaryDto addToCPRS(
			CounterPartyInfoResponseSummaryDto cprs1,
			CounterPartyInfoResponseSummaryDto cprs2) {
		cprs1.setType("PROCESSED");
		cprs1.setCgst(cprs1.getCgst().add(cprs2.getCgst()));
		cprs1.setSgst(cprs1.getSgst().add(cprs2.getSgst()));
		cprs1.setIgst(cprs1.getIgst().add(cprs2.getIgst()));
		cprs1.setCess(cprs1.getCess().add(cprs2.getCess()));
		cprs1.setTaxableVal(cprs1.getTaxableVal().add(cprs2.getTaxableVal()));
		cprs1.setTaxPayable(cprs1.getTaxPayable().add(cprs2.getTaxPayable()));
		cprs1.setCount(cprs1.getCount() + cprs2.getCount());
		cprs1.setPercent(cprs1.getPercent() + cprs2.getPercent());
		return cprs1;
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
