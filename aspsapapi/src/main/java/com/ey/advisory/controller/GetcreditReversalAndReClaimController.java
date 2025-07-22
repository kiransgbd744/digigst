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

import com.ey.advisory.app.docs.dto.ledger.ItcCrReversalAndReclaimDto;
import com.ey.advisory.app.docs.dto.ledger.NegativeDetailsRespDto;
import com.ey.advisory.app.docs.dto.ledger.RcmDetailsRespDto;
import com.ey.advisory.app.services.ledger.GetCreditClaimAndReverseBalDetailsImpl;
import com.ey.advisory.app.services.ledger.GetNegativeDetailsImpl;
import com.ey.advisory.app.services.ledger.GetRcmDetailsImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author kiran s
 *
 */
@Slf4j
@RestController
public class GetcreditReversalAndReClaimController {

	@Autowired
	@Qualifier("GetCreditClaimAndReverseBalDetailsImpl")
	private GetCreditClaimAndReverseBalDetailsImpl getReversalAndReclaim;
	
	@Autowired
	@Qualifier("GetRcmDetailsImpl")
	private GetRcmDetailsImpl getRcmDetails;
	
	@Autowired
	@Qualifier("GetNegativeDetailsImpl")
	private GetNegativeDetailsImpl getNegativeDetails;
	
	

	@PostMapping(value = "/ui/getCreditClaimAndReverseBalDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getITCLedgerDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {
		LOGGER.debug("findCrReversalAndReclaim method called with arg {}", jsonReq);
		JsonObject resp1 = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<ItcCrReversalAndReclaimDto> apiRespList = getReversalAndReclaim.findCrReversalAndReclaim(jsonReq);
			JsonElement respBody = gson.toJsonTree(apiRespList);
			resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Fetching findCrReversalAndReclaim method";
			LOGGER.error(msg, ex);
			resp1.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);

		}

	}
	@PostMapping(value = "/ui/getRcmBalDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getRcmBalDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {
		LOGGER.debug("findCrReversalAndReclaim method called with arg {}", jsonReq);
		JsonObject resp1 = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<RcmDetailsRespDto> apiRespList = getRcmDetails.findRcmDetails(jsonReq);
			JsonElement respBody = gson.toJsonTree(apiRespList);
			resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Fetching findCrReversalAndReclaim method";
			LOGGER.error(msg, ex);
			resp1.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);

		}

	}
	@PostMapping(value = "/ui/getNegativeBalDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNegativeBalDetails(
			@RequestBody String jsonReq, HttpServletRequest request) {
		LOGGER.debug("findCrReversalAndReclaim method called with arg {}", jsonReq);
		JsonObject resp1 = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<NegativeDetailsRespDto> apiRespList = getNegativeDetails.findNegativeDetails(jsonReq);
			JsonElement respBody = gson.toJsonTree(apiRespList);
			resp1.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp1.add("resp", respBody);
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Fetching findCrReversalAndReclaim method";
			LOGGER.error(msg, ex);
			resp1.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			return new ResponseEntity<>(resp1.toString(), HttpStatus.OK);

		}

	}
}
