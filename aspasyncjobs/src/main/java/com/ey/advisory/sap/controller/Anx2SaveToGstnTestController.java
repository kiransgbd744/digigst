/**
 * 
 */
package com.ey.advisory.sap.controller;

import java.util.List;

import org.javatuples.Pair;
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

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx2.Anx2CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.anx2.Anx2SaveInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * This is Dummy class to test the async Job Service(Anx2 SaveToGstn) service
 * Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx2SaveToGstnTestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1SaveToGstnTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving documents to GSTN";
	private static final String ZEROMSG = "Zero eligible documents found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("Anx2SaveInvicesIdentifierImpl")
	private Anx2SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("Anx2CancelledInvicesIdentifierImpl")
	private Anx2CancelledInvicesIdentifier gstnCancelData;
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@PostMapping(value = { "/saveAnx2CanToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr1CanToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveAnx2CanToGstn method called with arg {}",
					jsonReq);
		}
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = gstnCancelData.findCanInvoices(gstin, retPeriod, 
					GROUP_CODE, SaveToGstnOprtnType.CAN);
			if (respList != null && !respList.isEmpty()) {
				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if (respList != null && respList.isEmpty()) {
				LOGGER.warn(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);
			} else {
				LOGGER.warn(ERRMSG, jsonReq);
				JsonObject resp = respObject(ERRMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CONFLICT);
			}
		} catch (Exception ex) {
			LOGGER.error(ERRMSG, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = { "/saveAnx2B2bToGstn", "/saveAnx2B2baToGstn",
			"/saveAnx2DeToGstn", "/saveAnx2DeaToGstn", "/saveAnx2SezwpToGstn",
			"/saveAnx2SezwpaToGstn", "/saveAnx2SezwopToGstn",
			"/saveAnx2SezwopaToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr1B2bToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {

		String path = request.getServletPath();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} method called with arg {}", path, jsonReq);
		}
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			
			if ("/saveAnx2B2bToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.B2B, null, 
						SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2B2baToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.B2BA, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2DeToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.DE, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2DeaToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.DEA, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2SezwpToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWP, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2SezwpaToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWPA, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2SezwopToGstn.do".equals(path)) {
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWOP, null, SaveToGstnOprtnType.SAVE);

			} else if ("/saveAnx2SezwopaToGstn.do".equals(path)){
				respList = saveData.findAnx2SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWOPA, null, SaveToGstnOprtnType.SAVE);
			}
			if (respList != null && !respList.isEmpty()) {

				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else if (respList != null && respList.isEmpty()) {

				LOGGER.warn(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);

			} else {
				LOGGER.warn(ERRMSG, jsonReq);
				JsonObject resp = respObject(ERRMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CONFLICT);
			}
		} catch (Exception ex) {
			LOGGER.error(ERRMSG, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Pair<String, String> requestPair(String jsonReq) {
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);
		List<Pair<String, String>> gstinRetPeriodPairs = 
				screenExtractor.getAnx1CombinationPairs(dto, GROUP_CODE);
		return gstinRetPeriodPairs.get(0);
		
	}

	public JsonObject respObject(Object msg) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(msg);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

}
