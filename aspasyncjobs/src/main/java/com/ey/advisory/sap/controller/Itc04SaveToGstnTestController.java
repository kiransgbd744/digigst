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
import com.ey.advisory.app.services.refidpolling.itc04.Itc04RefIdPollingManger;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.services.savetogstn.jobs.itc04.Itc04SaveInvicesIdentifier;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
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
 * @author Sri Bhavya
 *
 */

@RestController
public class Itc04SaveToGstnTestController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Itc04SaveToGstnTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Itc04 docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Itc04 docs found to do Save to Gstn with arg {} ";
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;
	
	@Autowired
	@Qualifier("Itc04SaveInvicesIdentifierImpl")
	Itc04SaveInvicesIdentifier saveData;
	
	@PostMapping(value = { "/saveItc04M2jwToGstn", "/saveItc04Table5AToGstn",
			"/saveItc04Table5BToGstn", "/saveItc04Table5CToGstn"}, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr6ToGstn(
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
			
			if ("/saveItc04M2jwToGstn.do".equals(path)) {
				respList = saveData.findItc04SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.M2JW, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveItc04Table5AToGstn.do".equals(path)) {
				respList = saveData.findItc04SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5A, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveItc04Table5BToGstn.do".equals(path)) {
				respList = saveData.findItc04SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5B, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveItc04Table5CToGstn.do".equals(path)) {
				respList = saveData.findItc04SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5C, null,
						SaveToGstnOprtnType.SAVE,0l);

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
		String reqJson = requestObject.get("req").getAsJsonObject().toString();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Anx1SaveToGstnReqDto dto = gson.fromJson(reqJson.toString(),
				Anx1SaveToGstnReqDto.class);
		List<Pair<String, String>> gstinRetPeriodPairs = screenExtractor
				.getAnx1CombinationPairs(dto, GROUP_CODE);
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
	
	@Autowired
	@Qualifier("DefaultItc04RefIdPollingManager")
	private Itc04RefIdPollingManger itc04RefIdPollingManager;

	@PostMapping(value = "/getItc04RefIdStaus")
	public ResponseEntity<String> getItc04RefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		
		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);
		
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		
		return itc04RefIdPollingManager.processItc04RefIds(reqDto, groupCode);
	}
	
	
	@PostMapping(value = { "/canItc04M2jwToGstn", "/canItc04Table5AToGstn",
			"/canItc04Table5BToGstn", "/canItc04Table5CToGstn"}, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> canGstr6ToGstn(
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
			
			if ("/canItc04M2jwToGstn.do".equals(path)) {
				respList = saveData.findItc04CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.M2JW, null,
						SaveToGstnOprtnType.CAN,0l);

			} else if ("/canItc04Table5AToGstn.do".equals(path)) {
				respList = saveData.findItc04CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5A, null,
						SaveToGstnOprtnType.CAN,0l);

			} else if ("/canItc04Table5BToGstn.do".equals(path)) {
				respList = saveData.findItc04CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5B, null,
						SaveToGstnOprtnType.CAN,0l);

			} else if ("/canItc04Table5CToGstn.do".equals(path)) {
				respList = saveData.findItc04CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TABLE5C, null,
						SaveToGstnOprtnType.CAN,0l);

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
}
