package com.ey.advisory.sap.controller;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.refidpolling.gstr1.GSTR1RefIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1SaveInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *This is Dummy class to test the async Job Service(Gstr1 SaveToGstn) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr1SaveToGstnTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving documents to GSTN";
	private static final String ZEROMSG = "Zero eligible documents found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("gstr1SaveInvicesIdentifierImpl")
	private Gstr1SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("gstr1CancelledInvicesIdentifierImpl")
	private Gstr1CancelledInvicesIdentifier gstnCancelData;

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

//	@PostMapping(value = { "/saveGstr1CanToGstn" }, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1CanToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1CanToGstn method called with arg {}",
//					jsonReq);
//		}
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			Map<String, Map<Long, Long>> map = gstnCancelData
//					.findOrgCanInvoicesMap(gstin, retPeriod, GROUP_CODE,
//							SaveToGstnOprtnType.CAN, null, null);
//			respList = gstnCancelData.findCanInvoices(gstin, retPeriod,
//					GROUP_CODE, map, 0l, null, null, null);
//			if (respList != null && !respList.isEmpty()) {
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//			} else if (respList != null && respList.isEmpty()) {
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1B2bToGstn",
//			"/saveGstr1B2baToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1B2bToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1B2bToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//
//			if ("/saveGstr1B2bToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2B, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2BA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1AtToGstn",
//			"/saveGstr1AtaToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1AtToGstn(@RequestBody String jsonReq,
//			HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1AtToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1AtToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.AT, null,
//						SaveToGstnOprtnType.SAVE, 0l, null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.ATA, null,
//						SaveToGstnOprtnType.SAVE, 0l, null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1B2clToGstn",
//			"/saveGstr1B2claToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1B2clToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1B2clToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1B2clToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2CL, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2CLA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1ExpToGstn",
//			"/saveGstr1ExpaToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1ExpToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1ExpToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1ExpToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.EXP, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.EXPA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1CdnrToGstn",
//			"/saveGstr1CdnraToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1CdnrToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1CdnrToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1CdnrToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.CDNR, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.CDNRA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1CdnurToGstn",
//			"/saveGstr1CdnuraToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1CdnurToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1CdnurToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1CdnurToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.CDNUR, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.CDNURA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1B2csToGstn",
//			"/saveGstr1B2csaToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1B2csToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1B2csToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1B2csToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2CS, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.B2CSA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1TxpToGstn",
//			"/saveGstr1TxpaToGstn" }, produces = {
//					MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1TxpToGstn(
//			@RequestBody String jsonReq, HttpServletRequest request) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1TxpToGstn method called with arg {}",
//					jsonReq);
//		}
//		String path = request.getServletPath();
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			if ("/saveGstr1TxpToGstn.do".equals(path)) {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.TXP, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			} else {
//				respList = saveData.findSaveInvoices(gstin, retPeriod,
//						GROUP_CODE, APIConstants.TXPA, null,
//						SaveToGstnOprtnType.SAVE, 0l,null);
//			}
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}
//
//	@PostMapping(value = { "/saveGstr1HsnSumToGstn" }, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1HsnToGstn(
//			@RequestBody String jsonReq) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1HsnSumToGstn method called with arg {}",
//					jsonReq);
//		}
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			respList = saveData.findSaveInvoices(gstin, retPeriod, GROUP_CODE,
//					APIConstants.HSNSUM, null, SaveToGstnOprtnType.SAVE, 0l,null);
//
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}
//
//	@PostMapping(value = { "/saveGstr1DocIssuedToGstn" }, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1DocIssuedToGstn(
//			@RequestBody String jsonReq) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1DocIssuedToGstn method called with arg {}",
//					jsonReq);
//		}
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			respList = saveData.findSaveInvoices(gstin, retPeriod, GROUP_CODE,
//					APIConstants.DOCISS, null, SaveToGstnOprtnType.SAVE, 0l,null);
//
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@PostMapping(value = { "/saveGstr1NilToGstn" }, produces = {
//			MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> saveGstr1NilToGstn(
//			@RequestBody String jsonReq) {
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("saveGstr1NilToGstn method called with arg {}",
//					jsonReq);
//		}
//		List<SaveToGstnBatchRefIds> respList = null;
//		try {
//			Pair<String, String> requestPair = requestPair(jsonReq);
//			String gstin = requestPair.getValue0();
//			String retPeriod = requestPair.getValue1();
//			respList = saveData.findSaveInvoices(gstin, retPeriod, GROUP_CODE,
//					APIConstants.NIL, null, SaveToGstnOprtnType.SAVE, 0l,null);
//			if (respList != null && !respList.isEmpty()) {
//
//				JsonObject resp = respObject(respList);
//				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
//
//			} else if (respList != null && respList.isEmpty()) {
//
//				LOGGER.warn(ZEROMSG, jsonReq);
//				JsonObject resp = respObject(ZEROMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.NO_CONTENT);
//			} else {
//				LOGGER.warn(ERRMSG, jsonReq);
//				JsonObject resp = respObject(ERRMSG);
//				return new ResponseEntity<>(resp.toString(),
//						HttpStatus.CONFLICT);
//			}
//		} catch (Exception ex) {
//			LOGGER.error(ERRMSG, ex);
//			JsonObject resp = new JsonObject();
//			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ERRMSG)));
//			return new ResponseEntity<>(resp.toString(),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	public JsonObject respObject(Object msg) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(msg);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

	private Pair<String, String> requestPair(String jsonReq) {
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Gstr1SaveToGstnReqDto.class);
		List<Pair<String, String>> gstinRetPeriodPairs = screenExtractor
				.getGstr1CombinationPairs(dto, GROUP_CODE);
		return gstinRetPeriodPairs.get(0);

	}
	
	@Autowired
	@Qualifier("DefaultGSTR1RefIdPollingManager")
	private GSTR1RefIdPollingManager gSTR1RefIdPollingManager;

	@PostMapping(value = "/getRefIdStaus")
	public ResponseEntity<String> getrefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		
		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);
		
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		
		return gSTR1RefIdPollingManager.processGstr1RefIds(reqDto, groupCode);
	}
}
