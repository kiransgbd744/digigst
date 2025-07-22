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
import com.ey.advisory.app.services.refidpolling.gstr6.Gstr6RefIdPollingManger;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6CanInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6SaveInvicesIdentifier;
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
 * @author SriBhavya
 *
 */

@RestController
public class Gstr6SaveToGstnTestController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveToGstnTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Gstr6 docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Gstr6 docs found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("Gstr6SaveInvicesIdentifierImpl")
	Gstr6SaveInvicesIdentifier saveData;
	
	@Autowired
	@Qualifier("Gstr6CanInvicesIdentifierImpl")
	private Gstr6CanInvicesIdentifier saveCanData;

	@PostMapping(value = { "/saveGstr6B2bToGstn", "/saveGstr6B2baToGstn",
			"/saveGstr6CdnToGstn", "/saveGstr6IsdToGstn",
			"/saveGstr6IsdaToGstn", "/saveGstr6CdnaToGstn" }, produces = {
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
			
			if ("/saveGstr6B2bToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.B2B, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr6B2baToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.B2BA, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr6CdnToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.CDN, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr6IsdToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISD, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr6IsdaToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISDA, null,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr6CdnaToGstn.do".equals(path)) {
				respList = saveData.findGstr6SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.CDNA, null,
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
	@Qualifier("DefaultGstr6RefIdPollingManager")
	private Gstr6RefIdPollingManger gSTR6RefIdPollingManager;

	@PostMapping(value = "/getGstr6RefIdStaus")
	public ResponseEntity<String> getGstr6RefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		
		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);
		
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		
		return gSTR6RefIdPollingManager.processGstr6RefIds(reqDto, groupCode);
	}
	
	@PostMapping(value = { "/canSaveGstr6B2bToGstn", "/canSaveGstr6B2baToGstn",
			"/canSaveGstr6CdnToGstn", "/canSaveGstr6IsdCrToGstn","/canSaveGstr6IsdInvToGstn.do",
			"/canSaveGstr6IsdaRcrToGstn","/canSaveGstr6IsdaRnvToGstn.do", "/canSaveGstr6CdnaToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> canSaveGstr6ToGstn(
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
			
			if ("/canSaveGstr6B2bToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.B2B, 
						SaveToGstnOprtnType.CAN,0l,null);

			} else if ("/canSaveGstr6B2baToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.B2BA, 
						SaveToGstnOprtnType.CAN,0l,null);

			} else if ("/canSaveGstr6CdnToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.CDN, 
						SaveToGstnOprtnType.CAN,0l,null);

			} else if ("/canSaveGstr6IsdCrToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISD, 
						SaveToGstnOprtnType.CAN,0l,APIConstants.CR);

			} else if ("/canSaveGstr6IsdInvToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISD, 
						SaveToGstnOprtnType.CAN,0l,APIConstants.INV);

			}else if ("/canSaveGstr6IsdaRcrToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISDA, 
						SaveToGstnOprtnType.CAN,0l,APIConstants.RCR);

			}else if ("/canSaveGstr6IsdaRnvToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.ISDA, 
						SaveToGstnOprtnType.CAN,0l,APIConstants.RNV);

			} else if ("/canSaveGstr6CdnaToGstn.do".equals(path)) {
				respList = saveCanData.findGstr6CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.CDNA, 
						SaveToGstnOprtnType.CAN,0l,null);

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
