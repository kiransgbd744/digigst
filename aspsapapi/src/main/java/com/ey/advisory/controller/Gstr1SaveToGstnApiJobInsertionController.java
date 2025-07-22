/**
 * 
 */
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
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr1SaveToGstnApiJobInsertionController {

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

	@PostMapping(value = "/ui/gstr1SaveToGstnJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1SaveToGstnJob(
			@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"gstr1SaveToGstnJob Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.info("groupCode {} is set", groupCode);

			/*
			 * JsonArray respBody = getAllActiveGstnList(dto);
			 * 
			 * if(dto.getSgstins() == null || (dto.getSgstins() != null &&
			 * dto.getSgstins().isEmpty())){
			 * 
			 * if(LOGGER.isDebugEnabled()){
			 * LOGGER.debug("No active GSTNs found for GSTR1 save"); }
			 * resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			 * resp.add("resp",gson.toJsonTree(respBody));
			 * 
			 * return new ResponseEntity<>(resp.toString(), HttpStatus.OK); }
			 */

			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getGstr1CombinationPairs(dto, groupCode);

			JsonArray respBody = getAllActiveGstnList(gstr1Pairs);

			if (gstr1Pairs == null || gstr1Pairs.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR1 save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			// List<SaveToGstnEventStatusEntity> repEntity = new ArrayList<>();

			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				/*
				 * List<String> value0 = pairs.getValue0(); List<String> value1
				 * = pairs.getValue1();
				 */
				// if (!value0.isEmpty() && !value1.isEmpty()) {

				// SaveToGstnEventStatusEntity entity = null;
				// if (taxPeriod != null && taxPeriod.length() > 0 && gstin !=
				// null
				// && gstin.length() > 0) {

				/*
				 * AsyncExecJob job = asyncJobsService.createJob(groupCode,
				 * JobConstants.GSTR1_SAVETOGSTN, reqObject.toString(),
				 * JobConstants.SYSTEM, JobConstants.PRIORITY,
				 * JobConstants.PARENT_JOB_ID,
				 * JobConstants.SCHEDULE_AFTER_IN_MINS);
				 */

				for (int i = 0; i < gstr1Pairs.size(); i++) {

					Pair<String, String> pair = gstr1Pairs.get(i);

					/*Integer lastJobStatusCode = saveToGstnEventStatus
							.findLastJobStatusCode(pair.getValue0(),
									pair.getValue1(), groupCode);
					// Just to make sure the last SaveToGstn job has ran.
					if (lastJobStatusCode != null && lastJobStatusCode < 30) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg", "GSTR1 is Inprogress Already.");
						respBody.add(json);
						continue;
					}*/
					if (!gstnUserRequestUtil.isNextSaveRequestEligible(
							pair.getValue0(), pair.getValue1(),
							APIConstants.SAVE.toUpperCase(),
							APIConstants.GSTR1.toUpperCase(), groupCode)) {

						JsonObject json = new JsonObject();
						json.addProperty("gstin", pair.getValue0());
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						continue;

					}
					String userName = SecurityContext.getUser().getUserPrincipalName();
					Long userRequestId = gstnUserRequestUtil
							.createGstnUserRequest(pair.getValue0(),
									pair.getValue1(), APIConstants.SAVE,
									APIConstants.GSTR1.toUpperCase(), groupCode,
									userName, dto.getIsNilUserInput(),
									dto.getIsHsnUserInput(),
									false);
					SaveToGstnReqDto singleDto = new SaveToGstnReqDto(
							pair.getValue0(), pair.getValue1(), userRequestId, null);

					String eachJson = gson.toJson(singleDto);

					asyncJobsService.createJob(groupCode,
							JobConstants.GSTR1_SAVETOGSTN, eachJson.toString(),
							userName, JobConstants.PRIORITY,
							JobConstants.PARENT_JOB_ID,
							JobConstants.SCHEDULE_AFTER_IN_MINS);

					// status code 10 says status as USER REQUEST INITIATED
					saveToGstnEventStatus.EventEntry(pair.getValue1(),
							pair.getValue0(), 10, groupCode);
					/*
					 * if(entity != null) { repEntity.add(entity); }
					 */
					JsonObject json = new JsonObject();
					json.addProperty("gstin", pair.getValue0());
					json.addProperty("msg",
							"GSTR1 Save Request Submitted Successfully");
					respBody.add(json);

				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respBody));

				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);
				// }
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);
			// }

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
			LOGGER.debug("Getting all the active GSTNs for GSTR1 save");
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
		}
		return respBody;
	}
	



}
