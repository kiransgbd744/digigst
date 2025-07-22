package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@RestController
public class Gstr7SaveToGstnApiJobInsertionController {
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PostMapping(value = "/ui/gstr7SaveToGstnJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr7SaveToGstnJob(
			@RequestBody String jsonParam) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr7SaveToGstnJob Request received from UI as {} ",
						jsonParam);
			}

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonObject requestObject = JsonParser.parseString(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr7CombinationPairs(dto, groupCode);

			Map<String, Config> confiMap = configManager.getConfigs("GSTR7",
					"gstr7.transactional.cutOff", "DEFAULT");

			String cutOffRetPeriod = confiMap
					.get("gstr7.transactional.cutOff") == null ? "202506"
							: confiMap.get("gstr7.transactional.cutOff")
									.getValue();

			Integer cutOffRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(cutOffRetPeriod);
			String retPeriod = dto.getReturnPeriod();
			Integer currentRetPeriodInt = GenUtil
					.convertTaxPeriodToInt(retPeriod);

			boolean isTransactional = currentRetPeriodInt < cutOffRetPeriodInt
					? false : true;

			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);

			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR7 save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				for (int i = 0; i < gstr1Pairs.size(); i++) {
					Pair<String, String> pair = gstr1Pairs.get(i);
					if (!isTransactional) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR-7 transactional save is not allowed for the selected tax period.");
						respBody.add(json);
						continue;
					}
					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(), "SAVE", "GSTR7",
							groupCode)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR7 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;
					}
					String userName = SecurityContext.getUser()
							.getUserPrincipalName();

					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(pair.getValue0(),
									pair.getValue1(), APIConstants.SAVE,
									APIConstants.GSTR7.toUpperCase(), groupCode,
									userName, false, false, false);

					SaveToGstnReqDto singleDto = new SaveToGstnReqDto(
							pair.getValue0(), pair.getValue1(), userRequestId,
							null);

					String eachJson = gson.toJson(singleDto);

					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR7_SAVETOGSTN, eachJson.toString(),
							userName, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					// status code 10 says status as USER REQUEST INITIATED
					saveToGstnEventStatus.EventEntry(pair.getValue1(),
							pair.getValue0(), 10, groupCode);
					JsonObject json = new JsonObject();
					json.addProperty("gstin", pair.getValue0());
					json.addProperty("msg",
							"Save initiated for selected(active) GSTINs. Please review after 15 minutes.");
					respBody.add(json);
				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private JsonArray getAllActiveGstnList(
			List<Pair<String, String>> gstr1Pairs) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR7 save");
		}

		String msg = "";
		List<Pair<String, String>> activeGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstr1Pairs != null) {
			for (Pair<String, String> pair : gstr1Pairs) {
				String gstin = pair.getValue0();
				String taxPeriod = pair.getValue1();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (authStatus.equals("A")) {
					activeGstins.add(new Pair<>(gstin, taxPeriod));
				} else {
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}
			gstr1Pairs.clear();
			gstr1Pairs.addAll(activeGstins);
		} else {
			JsonObject json = new JsonObject();
			msg = "No Gstins found for this Entity";
			json.addProperty("msg", msg);
			respBody.add(json);
		}
		return respBody;
	}

}
