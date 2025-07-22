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
import com.ey.advisory.app.services.refidpolling.gstr2x.Gstr2XRefIdPollingManger;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.services.savetogstn.jobs.gstr2x.Gstr2XSaveInvicesIdentifier;
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
public class Gstr2XSaveToGstnTestController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2XSaveToGstnTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Gstr2X docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Gstr2X docs found to do Save to Gstn with arg {} ";
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;
	
	@Autowired
	@Qualifier("Gstr2XSaveInvicesIdentifierImpl")
	Gstr2XSaveInvicesIdentifier saveData;
	
	@PostMapping(value = { "/saveGstr2XTdsToGstn", "/saveGstr2xTdsaToGstn",
			"/saveGstr2XTcsToGstn", "/saveGstr2XTcsaToGstn" }, produces = {
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
			
			if ("/saveGstr2XTdsToGstn.do".equals(path)) {
				respList = saveData.findGstr2XSaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDS,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr2xTdsaToGstn.do".equals(path)) {
				respList = saveData.findGstr2XSaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDSA,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr2XTcsToGstn.do".equals(path)) {
				respList = saveData.findGstr2XSaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TCS,
						SaveToGstnOprtnType.SAVE,0l);

			} else if ("/saveGstr2XTcsaToGstn.do".equals(path)) {
				respList = saveData.findGstr2XSaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TCSA,
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
		Anx1SaveToGstnReqDto dto = gson.fromJson(reqJson,
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
	@Qualifier("DefaultGstr2XRefIdPollingManager")
	private Gstr2XRefIdPollingManger gSTR6RefIdPollingManager;

	@PostMapping(value = "/getGstr2XRefIdStaus")
	public ResponseEntity<String> getGstr2XRefIdStatus(@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}
		
		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);
		
		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq,
				PollingMessage.class);
		
		return gSTR6RefIdPollingManager.processGstr2XRefIds(reqDto, groupCode);
	}
}
