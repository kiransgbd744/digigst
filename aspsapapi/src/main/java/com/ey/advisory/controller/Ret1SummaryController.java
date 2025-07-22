package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.ey.advisory.app.services.search.simplified.docsummary.Ret1ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumLatefee6ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumPayment7ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumRefund8ReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1SumReqRespHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Balakrishna.S
 *
 */
@RestController
public class Ret1SummaryController {

	@Autowired
	@Qualifier("Ret1ReqRespHandler")
	Ret1ReqRespHandler ret1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1SumReqRespHandler")
	Ret1SumReqRespHandler retSum1ReqRespHandler;

	@Autowired
	@Qualifier("Ret1SumLatefee6ReqRespHandler")
	Ret1SumLatefee6ReqRespHandler lateFess;

	@Autowired
	@Qualifier("Ret1SumPayment7ReqRespHandler")
	Ret1SumPayment7ReqRespHandler taxPayment;

	@Autowired
	@Qualifier("Ret1SumRefund8ReqRespHandler")
	Ret1SumRefund8ReqRespHandler refund;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1SummaryController.class);

	@PostMapping(value = "/ui/ret1MainSummary", produces = {
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
					.handleRet1ReqAndResp(ret1SummaryRequest);

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

	@PostMapping(value = "/ui/ret1Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyAnnexure1MainSummary(
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

			JsonElement handleRet1ReqAndResp = retSum1ReqRespHandler
					.handleRe1ReqAndResp(ret1SummaryRequest);

			JsonElement handleRet1ReqLateFee = lateFess
					.handleRe1ReqAndResp(ret1SummaryRequest);
			JsonElement handleRet1ReqTaxPayment = taxPayment
					.handleRe1ReqAndResp(ret1SummaryRequest);

			JsonElement handleRe1ReqRefund = refund
					.handleRe1ReqAndResp(ret1SummaryRequest);

			Map<String, JsonElement> combinedMap2 = new HashMap<>();

			combinedMap2.put("aspValues", handleRet1ReqAndResp);
			combinedMap2.put("lateFee", handleRet1ReqLateFee);
			combinedMap2.put("taxPayment", handleRet1ReqTaxPayment);
			combinedMap2.put("refund", handleRe1ReqRefund);

			JsonElement refundSummaryRespbody = gson
					.toJsonTree(combinedMap2);
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

}
