/**
 * 
 */
package com.ey.advisory.sap.controller;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.ret.RetSaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.services.savetogstn.jobs.ret.RetCancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.ret.RetSaveInvicesIdentifier;
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
import lombok.extern.slf4j.Slf4j;

/**
 * This is Dummy class to test the async Job Service(Ret SaveToGstn) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
@Slf4j
public class RetSaveToGstnTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Ret docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Ret docs found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("RetSaveInvicesIdentifierImpl")
	private RetSaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("RetCancelledInvicesIdentifierImpl")
	private RetCancelledInvicesIdentifier gstnCancelData;
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@PostMapping(value = { "/saveRetCanToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveRetCanToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveRetCanToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = gstnCancelData.findCanInvoices(gstin, retPeriod, GROUP_CODE,
					SaveToGstnOprtnType.CAN);
			if (respList != null && !respList.isEmpty()) {
				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if (respList != null && respList.isEmpty()) {
				LOGGER.info(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);
			} else {
				LOGGER.info(ERRMSG, jsonReq);
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

	@PostMapping(value = { "/saveRetTbl3aToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveRetB2cToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveRetTbl3aToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findRetSaveInvoices(gstin, retPeriod, GROUP_CODE,
					APIConstants.TBL3A, null, SaveToGstnOprtnType.SAVE);
			if (respList != null && !respList.isEmpty()) {

				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else if (respList != null && respList.isEmpty()) {

				LOGGER.info(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);

			} else {
				LOGGER.info(ERRMSG, jsonReq);
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

	@PostMapping(value = { "/saveRetTbl3cToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveRetB2bToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveRetTbl3cToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findRetSaveInvoices(gstin, retPeriod, GROUP_CODE,
					APIConstants.TBL3C, null, SaveToGstnOprtnType.SAVE);
			if (respList != null && !respList.isEmpty()) {

				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else if (respList != null && respList.isEmpty()) {

				LOGGER.info(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);

			} else {
				LOGGER.info(ERRMSG, jsonReq);
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

	@PostMapping(value = { "/saveRetTbl3dToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveRetExpToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveRetTbl3dToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
				respList = saveData.findRetSaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.TBL3D, null, SaveToGstnOprtnType.SAVE);

			if (respList != null && !respList.isEmpty()) {

				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else if (respList != null && respList.isEmpty()) {

				LOGGER.info(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);

			} else {
				LOGGER.info(ERRMSG, jsonReq);
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

	@PostMapping(value = { "/saveRetTbl4aToGstn"}, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveRetSezToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveRetTbl4aToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
				respList = saveData.findRetSaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.TBL4A, null, SaveToGstnOprtnType.SAVE);

			if (respList != null && !respList.isEmpty()) {

				JsonObject resp = respObject(respList);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else if (respList != null && respList.isEmpty()) {

				LOGGER.info(ZEROMSG, jsonReq);
				JsonObject resp = respObject(ZEROMSG);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.NO_CONTENT);
			} else {
				LOGGER.info(ERRMSG, jsonReq);
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
		RetSaveToGstnReqDto dto = gson.fromJson(requestObject,
				RetSaveToGstnReqDto.class);
		List<Pair<String, String>> gstinRetPeriodPairs = 
				screenExtractor.getRetCombinationPairs(dto, GROUP_CODE);
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
