package com.ey.advisory.controller;

import java.util.ArrayList;
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

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
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
public class Gstr2XSaveToGstnApiJobInsertionController {
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

	@PostMapping(value = "/ui/gstr2XSaveToGstnJob", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2XSaveToGstnJob(@RequestBody String jsonParam) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr2XSaveToGstnJob Request received from UI as {} ", jsonParam);
			}

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonObject requestObject = (new JsonParser()).parse(jsonParam).getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject, Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);
			List<Pair<String, String>> gstr1Pairs = screenExtractor.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);

			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR2X save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				for (int i = 0; i < gstr1Pairs.size(); i++) {
					Pair<String, String> pair = gstr1Pairs.get(i);					
					if (!gstnUserRequestUtil.isNextSaveRequestEligible(pair.getValue0(), pair.getValue1(), "SAVE",
							"GSTR2X", groupCode)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg", "GSTR2X SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;
					}
					//Table sections 
					List<String> sections = dto.getTableSections();
						if (sections == null || sections.isEmpty()) {
							JsonObject json = new JsonObject();
							json.addProperty("gstin", pair.getValue0());
							json.addProperty("msg", "No Section is selected to SAVE GSTR2X");
							respBody.add(json);
							resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
							resp.add("resp", gson.toJsonTree(respBody));
							return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
						}
					String userName = SecurityContext.getUser().getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE, APIConstants.GSTR2X.toUpperCase(), groupCode, userName, false, false, false);
					
					sections.forEach(section -> {	
						SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(pair.getValue0(), pair.getValue1(), userRequestId,
								section.toLowerCase());
						String sectionJson = gson.toJson(sectionDto);
						createAsyncJobForSaveGstr2x(groupCode, userName, sectionJson);
					});

					// status code 10 says status as USER REQUEST INITIATED
					saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10, groupCode);
					JsonObject json = new JsonObject();
					json.addProperty("gstin", pair.getValue0());
					json.addProperty("msg",
							"Save initiated for selected(active) GSTINs. Please review after 15 minutes.");
					respBody.add(json);
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void createAsyncJobForSaveGstr2x(String groupCode, String userName, String sectionJson) {
		asyncJobsService.createJob(groupCode, JobConstants.GSTR2X_SAVETOGSTN, sectionJson, userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID, JobConstants.SCHEDULE_AFTER_IN_MINS);
		
	}

	private JsonArray getAllActiveGstnList(List<Pair<String, String>> gstr1Pairs) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR2X save");
		}

		String msg = "";
		List<Pair<String, String>> activeGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (gstr1Pairs != null) {
			for (Pair<String, String> pair : gstr1Pairs) {
				String gstin = pair.getValue0();
				String taxPeriod = pair.getValue1();
				JsonObject json = new JsonObject();
				String authStatus = authTokenService.getAuthTokenStatusForGstin(gstin);
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
		}
		return respBody;
	}
}
