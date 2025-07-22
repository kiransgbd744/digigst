package com.ey.advisory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.credit.reversal.CreditRevFinancialYearWiseServiceImpl;
import com.ey.advisory.app.services.credit.reversal.CreditReversalForFinancialYearDto;
import com.ey.advisory.app.services.credit.reversal.CreditTurnOverFinancialItemDto;
import com.ey.advisory.app.services.credit.reversal.CreditTurnOverFinancialProcess2Dto;
import com.ey.advisory.app.services.credit.reversal.FinancialYearFinalRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class CreditRevForFinancialYearWiseController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditRevForFinancialYearWiseController.class);

	@Autowired
	@Qualifier("CreditRevFinancialYearWiseServiceImpl")
	private CreditRevFinancialYearWiseServiceImpl credRevsalProcServImpl;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;

	@PostMapping(value = "/ui/getFinancialYearWiseEntity", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFinancialYearWiseEntity(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject jsonObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(jsonObj,
					Annexure1SummaryReqDto.class);

			Annexure1SummaryReqDto anx1SumReqDto = basicCommonSecParam
					.setOutwardSumDataSecuritySearchParams(reqDto);

			FinancialYearFinalRespDto finYearFinalResp = credRevsalProcServImpl
					.getFinYearForProceSummary(anx1SumReqDto);
			resp.add("resp", gson.toJsonTree(finYearFinalResp));
			resp.add("hdr", gson.toJsonTree(APIRespDto.getSuccessStatus()));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/getProdCallCompFinaYearCredRev", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProdCallCompFinaYearCreditRev(
			@RequestBody String jsonReq) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			String msg = credRevsalProcServImpl
					.proceCallForFinancialYear(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(msg));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param Get
	 *            Credit Reversal for Financial year
	 * 
	 * 
	 * @return
	 */
	@PostMapping(value = "/ui/getCredReversalFinancialYear", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCredReversal(@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredReversal() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			List<CreditReversalForFinancialYearDto> reversTurnOverDtos = credRevsalProcServImpl
					.getCredReviewReversalSummary(reqDto);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(reversTurnOverDtos));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredReversal() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param get
	 *            Credit Turn Over Records
	 * @return
	 */
	@PostMapping(value = "/ui/getFinancialYearCredTurnOverPart1", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFinancialYearCredTurnOverPart1(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			List<CreditTurnOverFinancialItemDto> reversTurnOverDtos = credRevsalProcServImpl
					.getFinancialYearCredTurnOverPart1(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(reversTurnOverDtos));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
	
	
	/**
	 * 
	 * @param get
	 *            Credit Turn Over Records
	 * @return
	 */
	@PostMapping(value = "/ui/getFinancialYearCredTurnOverPart2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFinancialYearCredTurnOverPart2(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			List<CreditTurnOverFinancialProcess2Dto> reversTurnOverDtos = credRevsalProcServImpl
					.getFinancialYearCredTurnOverPart2(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(reversTurnOverDtos));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
