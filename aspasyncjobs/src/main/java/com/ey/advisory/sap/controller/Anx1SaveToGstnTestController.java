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

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.refidpolling.gstr1.GSTR1RefIdPollingManager;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.Anx1CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.Anx1SaveInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
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
import lombok.extern.slf4j.Slf4j;

/**
 * This is Dummy class to test the async Job Service(Anx1 SaveToGstn) 
 * service Through API.
 * 
 * @author Hemasundar.J
 *
 */
@RestController
@Slf4j
public class Anx1SaveToGstnTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Anx1 docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Anx1 docs found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("anx1SaveInvicesIdentifierImpl")
	private Anx1SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("anx1CancelledInvicesIdentifierImpl")
	private Anx1CancelledInvicesIdentifier gstnCancelData;
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@PostMapping(value = { "/saveAnx1CanToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1CanToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1CanToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = gstnCancelData.findCanInvoices(gstin,
					retPeriod, GROUP_CODE, SaveToGstnOprtnType.CAN);
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

	@PostMapping(value = { "/saveAnx1B2cToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1B2cToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1B2cToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
					GROUP_CODE, APIConstants.B2C, null,
					SaveToGstnOprtnType.SAVE);
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

	@PostMapping(value = { "/saveAnx1B2bToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1B2bToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1B2bToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin,retPeriod,
					GROUP_CODE, APIConstants.B2B, null,
					SaveToGstnOprtnType.SAVE);
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

	@PostMapping(value = { "/saveAnx1ExpwpToGstn",
			"/saveAnx1ExpwopToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1ExpToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1ExpToGstn method called with arg {}", jsonReq);
		String path = request.getServletPath();
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			
			if ("/saveAnx1ExpwpToGstn.do".equals(path)) {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.EXPWP, null,
						SaveToGstnOprtnType.SAVE);

			} else {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.EXPWOP, null,
						SaveToGstnOprtnType.SAVE);
			}
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

	@PostMapping(value = { "/saveAnx1SezwpToGstn",
			"/saveAnx1SezwopToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1SezToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1SezToGstn method called with arg {}", jsonReq);
		String path = request.getServletPath();
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			if ("/saveAnx1SezwpToGstn.do".equals(path)) {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWP, null, SaveToGstnOprtnType.SAVE);

			} else {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod, GROUP_CODE,
						APIConstants.SEZWOP, null, SaveToGstnOprtnType.SAVE);
			}
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

	@PostMapping(value = { "/saveAnx1DeToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1DeToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1DeToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod, GROUP_CODE,
					APIConstants.DE, null, SaveToGstnOprtnType.SAVE);

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

	@PostMapping(value = { "/saveAnx1RevToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1RevToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		LOGGER.debug("saveAnx1RevToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod, GROUP_CODE,
					APIConstants.REV, null, SaveToGstnOprtnType.SAVE);

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

	@PostMapping(value = { "/saveAnx1ImpsToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1ImpsToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {

		LOGGER.debug("saveAnx1ImpsToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod, GROUP_CODE,
					APIConstants.IMPS, null, SaveToGstnOprtnType.SAVE);

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

	@PostMapping(value = { "/saveAnx1ImpgToGstn",
			"/saveAnx1ImpgSezToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1ImpgToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {

		LOGGER.debug("saveAnx1ImpgToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		String path = request.getServletPath();
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			if ("/saveAnx1ImpgToGstn.do".equals(path)) {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.IMPG, null,
						SaveToGstnOprtnType.SAVE);

			} else {
				respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.IMPGSEZ, null,
						SaveToGstnOprtnType.SAVE);
			}
			
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


	@PostMapping(value = { "/saveAnx1EcomToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1EcomToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {

		LOGGER.debug("saveAnx1EcomToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
					GROUP_CODE, APIConstants.ECOM, null,
					SaveToGstnOprtnType.SAVE);

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
	

	@PostMapping(value = { "/saveAnx1MisToGstn" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAnx1MisToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {

		LOGGER.debug("saveAnx1MisToGstn method called with arg {}", jsonReq);
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();
			respList = saveData.findAnx1SaveInvoices(gstin, retPeriod,
					GROUP_CODE, APIConstants.MIS, null,
					SaveToGstnOprtnType.SAVE);

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
		Anx1SaveToGstnReqDto dto = gson.fromJson(requestObject,
				Anx1SaveToGstnReqDto.class);
		List<Pair<String, String>> gstinRetPeriodPairs = 
				screenExtractor.getAnx1CombinationPairs(dto, GROUP_CODE);
		return gstinRetPeriodPairs.get(0);
		
	}
	
	@Autowired
	@Qualifier("DefaultAnx1RefIdPollingManager")
	private GSTR1RefIdPollingManager gSTR1RefIdPollingManager;
	
	@PostMapping(value = "/getAnx1RefIdStaus")
	public ResponseEntity<String> getStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		
		return gSTR1RefIdPollingManager.processAnx1RefIds(reqDto, groupCode);
	}
	
	
}
