package com.ey.advisory.sap.controller;

import java.util.List;
import java.util.Map;

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
import com.ey.advisory.app.services.refidpolling.gstr7.Gstr7RefIdPollingManger;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.services.savetogstn.jobs.gstr7.Gstr7CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr7.Gstr7SaveInvicesIdentifier;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.PollingMessage;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
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
public class Gstr7SaveToGstnTestController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SaveToGstnTestController.class);
	private static final String GROUP_CODE = TestController.staticTenantId();
	private static final String ERRMSG = "Unexpected error while saving Gstr7 docs to GSTN";
	private static final String ZEROMSG = "Zero eligible Gstr7 docs found to do Save to Gstn with arg {} ";

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("Gstr7SaveInvicesIdentifierImpl")
	Gstr7SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("Gstr7CancelledInvicesIdentifierImpl")
	private Gstr7CancelledInvicesIdentifier gstnCancelData;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PostMapping(value = { "/saveGstr7TdsToGstn",
			"/saveGstr7TdsaToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr7ToGstn(@RequestBody String jsonReq,
			HttpServletRequest request) {

		String path = request.getServletPath();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} method called with arg {}", path, jsonReq);
		}
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();

			Map<String, Config> confiMap = configManager.getConfigs("GSTR7",
					"gstr7.transactional.cutOff", "DEFAULT");

			String cutOffRetPeriod = confiMap
					.get("gstr7.transactional.cutOff") == null ? "202506"
							: confiMap.get("gstr7.transactional.cutOff")
									.getValue();

			Integer cutOffRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(cutOffRetPeriod);

			Integer currentRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(retPeriod);

			ProcessingContext gstr7Context = new ProcessingContext();
			gstr7Context.seAttribute(APIConstants.TRANSACTIONAL,
					currentRetPeriodInt < cutOffRetPeriodInt ? false : true);

			if ("/saveGstr7TdsToGstn.do".equals(path)) {
				respList = saveData.findGstr7SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDS, null,
						SaveToGstnOprtnType.SAVE, 0l, gstr7Context, null);

			} else if ("/saveGstr7TdsaToGstn.do".equals(path)) {
				respList = saveData.findGstr7SaveInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDSA, null,
						SaveToGstnOprtnType.SAVE, 0l, gstr7Context, null);

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
	@Qualifier("DefaultGstr7RefIdPollingManager")
	private Gstr7RefIdPollingManger gSTR7RefIdPollingManager;

	@PostMapping(value = "/getGstr7RefIdStaus")
	public ResponseEntity<String> getGstr7RefIdStatus(
			@RequestBody String jsonReq) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("executing the processRefIds method in SaveToGstn");
		}

		String groupCode = GROUP_CODE;
		LOGGER.info("groupCode {} is set", groupCode);

		Gson gson = new Gson();
		PollingMessage reqDto = gson.fromJson(jsonReq, PollingMessage.class);

		return gSTR7RefIdPollingManager.processGstr7RefIds(reqDto, groupCode);
	}

	@PostMapping(value = { "/saveGstr7CanTdsToGstn",
			"/saveGstr7CanTdsaToGstn" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr1CanToGstn(
			@RequestBody String jsonReq, HttpServletRequest request) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveGstr1CanToGstn method called with arg {}",
					jsonReq);
		}
		List<SaveToGstnBatchRefIds> respList = null;
		try {
			String path = request.getServletPath();
			Pair<String, String> requestPair = requestPair(jsonReq);
			String gstin = requestPair.getValue0();
			String retPeriod = requestPair.getValue1();

			Map<String, Config> confiMap = configManager.getConfigs("GSTR7",
					"gstr7.transactional.cutOff", "DEFAULT");

			String cutOffRetPeriod = confiMap
					.get("gstr7.transactional.cutOff") == null ? "202506"
							: confiMap.get("gstr7.transactional.cutOff")
									.getValue();

			Integer cutOffRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(cutOffRetPeriod);

			Integer currentRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(retPeriod);

			ProcessingContext gstr7context = new ProcessingContext();
			gstr7context.seAttribute(APIConstants.TRANSACTIONAL,
					currentRetPeriodInt < cutOffRetPeriodInt ? false : true);

			boolean isTransactional = (boolean) gstr7context
					.getAttribute(APIConstants.TRANSACTIONAL);
			if (isTransactional) {
				Map<String, Map<Long, Long>> map = gstnCancelData
						.findOrgCanInvoicesMap(gstin, retPeriod,
								TenantContext.getTenantId(),
								SaveToGstnOprtnType.CAN, 0L, gstr7context);

				gstnCancelData.findGstr7TransCanInvoices(gstin, retPeriod,
						TenantContext.getTenantId(), map, 0L, gstr7context);

			} else {

			}

			if ("/saveGstr7CanTdsToGstn.do".equals(path)) {
				respList = gstnCancelData.findGstr7CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDS, SaveToGstnOprtnType.CAN,
						0l, gstr7context);

			} else if ("/saveGstr7CanTdsaToGstn.do".equals(path)) {
				respList = gstnCancelData.findGstr7CanInvoices(gstin, retPeriod,
						GROUP_CODE, APIConstants.TDSA, SaveToGstnOprtnType.CAN,
						0l, gstr7context);

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
