package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.search.simplified.docsummary.Ret1APaymentTax6ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1ASumInterestLatefeeReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1ASumReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1aReqRespHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@RestController
@Slf4j
public class Ret1ASummaryController {

	@Autowired
	@Qualifier("Ret1aReqRespHandler")
	Ret1aReqRespHandler ret1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1ASumInterestLatefeeReqRespHandler")
	private Ret1ASumInterestLatefeeReqRespHandler ret1ASumInterestLatefeeReqRespHandler;

	@Autowired
	@Qualifier("Ret1APaymentTax6ReqRespHandler")
	Ret1APaymentTax6ReqRespHandler taxPayment;

	@Autowired
	@Qualifier("Ret1ASumReqRespHandler")
	Ret1ASumReqRespHandler outsupply;
	/*
	 * @Autowired
	 * 
	 * @Qualifier("Ret1SumPayment7ReqRespHandler") Ret1SumPayment7ReqRespHandler
	 * taxPayment;
	 */

	@PostMapping(value = "/ui/ret1ASummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyAnnexure1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			Map<String, JsonElement> handleRet1ReqAndResp = ret1ReqRespHandler
					.handleRet1aReqAndResp(ret1SummaryRequest);

			JsonElement refundSummaryRespbody = gson
					.toJsonTree(handleRet1ReqAndResp);

			JsonElement respBody = gson.toJsonTree(refundSummaryRespbody);
			JsonObject resp = new JsonObject();
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/ret1aSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> aspComputedSummary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto ret1AreviewSum = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			// siva api
			JsonElement handleRet1ASupplyReqAndResp = outsupply
					.handleRe1ReqAndResp(ret1AreviewSum);

			// Mahesh API
			JsonElement handleRet1ReqAndResp = ret1ASumInterestLatefeeReqRespHandler
					.handleRe1ReqAndResp(ret1AreviewSum);
			// Balakrishna API
			JsonElement handleRet1APayReqAndResp = taxPayment
					.handleRe1ReqAndResp(ret1AreviewSum);

			Map<String, JsonElement> combineAllSumDatas = new HashMap<>();
			combineAllSumDatas.put("aspValues", handleRet1ASupplyReqAndResp);

			combineAllSumDatas.put("lateFee", handleRet1ReqAndResp);
			combineAllSumDatas.put("taxPayment", handleRet1APayReqAndResp);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(combineAllSumDatas);
			JsonElement respBody = gson.toJsonTree(refundSummaryRespbody);
			JsonObject resp = new JsonObject();
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing RET1A "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
