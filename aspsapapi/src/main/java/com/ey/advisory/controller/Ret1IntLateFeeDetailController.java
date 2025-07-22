package com.ey.advisory.controller;
/**
 * 
 * @author Balakrishna.S
 *
 */

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

import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1PaymentTaxDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1ALateFeeDetailService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1AspDetailService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1LateFeeDetailService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1PaymentTaxDetailService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1RefundDetailService;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1aAspDetailService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;



@RestController
public class Ret1IntLateFeeDetailController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1IntLateFeeDetailController.class);

	@Autowired
	@Qualifier("Ret1LateFeeDetailService")
	Ret1LateFeeDetailService service;
	
	@Autowired
	@Qualifier("Ret1ALateFeeDetailService")
	Ret1ALateFeeDetailService ret1AService;
	
	@Autowired
	@Qualifier("Ret1PaymentTaxDetailService")
	Ret1PaymentTaxDetailService paymentService;
	
	@Autowired
	@Qualifier("Ret1RefundDetailService")
	Ret1RefundDetailService refundService;
	@Autowired
	@Qualifier("Ret1AspDetailService")
	Ret1AspDetailService aspDetail;
	
	@Autowired
	@Qualifier("Ret1aAspDetailService")
	Ret1aAspDetailService ret1aDetail;
	
	@PostMapping(value = "/ui/ret1LateFeeDetail", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyLateFeeSummary(
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

			SearchResult<Ret1LateFeeDetailSummaryDto> searchResult = service
					.find(ret1SummaryRequest, null,
							Ret1LateFeeDetailSummaryDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(searchResult.getResult());

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
	
	@PostMapping(value = "/ui/ret1PaymentTax", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyPaymentTaxSummary(
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

			SearchResult<Ret1PaymentTaxDetailSummaryDto> paymentResult = paymentService
					.find(ret1SummaryRequest, null,
							Ret1PaymentTaxDetailSummaryDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(paymentResult.getResult());

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

	@PostMapping(value = "/ui/ret1Refund", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyRefundSummary(
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

			SearchResult<Ret1RefundDetailSummaryDto> paymentResult = refundService
					.find(ret1SummaryRequest, null,
							Ret1RefundDetailSummaryDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(paymentResult.getResult());

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

	
	// RET1A Late Fee 
	@PostMapping(value = "/ui/ret1ALateFeeDetail", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1ALateFeeSummary(
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

			SearchResult<Ret1LateFeeDetailSummaryDto> searchResult = ret1AService
					.find(ret1SummaryRequest, null,
							Ret1LateFeeDetailSummaryDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(searchResult.getResult());

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
	
	// RET1A Payment Tax
	@PostMapping(value = "/ui/ret1APaymentTax", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1APaymentSummary(
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

			SearchResult<Ret1PaymentTaxDetailSummaryDto> paymentResult = paymentService
					.find(ret1SummaryRequest, null,
							Ret1PaymentTaxDetailSummaryDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(paymentResult.getResult());

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

	//  RET1A  3A4, 3C1, 4A5, 4B1, 4B2  these Sections
	
	@PostMapping(value = "/ui/ret1aAspDetail", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1aAspDetail(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Ret1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Ret1SummaryReqDto.class);

			SearchResult<Ret1AspDetailRespDto> paymentResult = ret1aDetail
					.find(ret1SummaryRequest, null,
							Ret1AspDetailRespDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(paymentResult.getResult());

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
	
//  Asp Detail API For 3A8/3C5/4A4/4B4/4B5 these Sections 
	
	@PostMapping(value = "/ui/ret1AspDetail", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ey1AspDetail(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Ret1SummaryReqDto ret1SummaryRequest = gson
					.fromJson(reqJson.toString(), Ret1SummaryReqDto.class);

			SearchResult<Ret1AspDetailRespDto> paymentResult = aspDetail
					.find(ret1SummaryRequest, null,
							Ret1AspDetailRespDto.class);
			JsonElement refundSummaryRespbody = gson
					.toJsonTree(paymentResult.getResult());

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
