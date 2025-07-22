package com.ey.advisory.admin.dmsOnboarding.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.OnboardingRequestPayloadRepository;
import com.ey.advisory.admin.services.onboarding.EntityConfigPrmtService;
import com.ey.advisory.admin.services.onboarding.OnboardingRequestPayloadService;
import com.ey.advisory.app.dms.DmsFetchRulesApiServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.ey.advisory.core.dto.RuleAnsDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * '@POST For DMS_CONFIG_PARAMTR
 * 
 * @author ashutosh.kar
 *
 */
@RestController
public class DmsOnboardingController {

	private static final Logger LOGGER = LoggerFactory.
			getLogger(DmsOnboardingController.class);
	
	public static final String LOGIN_FAILED_MSG = "Error encountered in fetching the rules. " + 
            "Please verify the credentials provided/reach out to central team.";
	
	@Autowired
	@Qualifier("DmsConfigPrmtServiceImpl")
	private EntityConfigPrmtService entityConfigPrmtService;
	
	@Autowired
	@Qualifier("DmsFetchRulesApiServiceImpl")
	private DmsFetchRulesApiServiceImpl dmsFetchRulesApiServiceImpl;
	
	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String RESP = "resp";
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadRepository")
	private OnboardingRequestPayloadRepository payloadRepo;
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadService")
	private OnboardingRequestPayloadService payloadService;
	
	@PostMapping(value = "/getDmsConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGroupConfig(@RequestBody String jsonReqObj) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestReqObject = JsonParser.parseString(jsonReqObj).getAsJsonObject().get("req")
					.getAsJsonObject();
			EntityConfigPrmtReqDto dto = gson.fromJson(requestReqObject, EntityConfigPrmtReqDto.class);

			List<EntityConfigPrmtResDto> resDtos = entityConfigPrmtService.getEntityConfigPrmt(dto);
			resp.add("resp", gson.toJsonTree(resDtos));
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/updateDmsConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateConfigPrmt(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long id = null;
		try {
			JsonArray jsonReqArray = JsonParser.parseString(jsonReq)
			        .getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<EntityConfigPrmtReqDto>>() {
			}.getType();
			
			 id = payloadService.savePayoadRequest(jsonReq, 0, "DMS");
			
			List<EntityConfigPrmtReqDto> dtos = gson.fromJson(jsonReqArray, listType);
			 
			
			entityConfigPrmtService.updateConfigParmetr(dtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
			        APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));			
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	
	@GetMapping(value = "/getFetchRules.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFetchRule() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String groupCode = TenantContext.getTenantId();
			if (groupCode == null || groupCode.trim().isEmpty()) {
				String errorMsg = "Failed to fetch rules: GroupCode is null or empty.";
				LOGGER.error(errorMsg);
				resp.add("hdr", gson.toJsonTree(new APIRespDto("E", errorMsg)));
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.toString());
			}

			LOGGER.info("Fetching DMS rules for groupCode: {}", groupCode);

			String rules = dmsFetchRulesApiServiceImpl.getFetchRules(groupCode);

			if (rules.contains(LOGIN_FAILED_MSG)) {
				LOGGER.error("Login failed for groupCode: {}", groupCode);
				resp.add("hdr", gson.toJsonTree(new APIRespDto("E", rules)));
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.toString());
			}
			
			if (StringUtils.isBlank(rules) || rules.equalsIgnoreCase("No rules found")) {
				LOGGER.warn("No rules found for groupCode: {}", groupCode);
				return ResponseEntity.ok(gson.toJson(new APIRespDto("E", "No rules found")));
			}

			resp.add("resp", gson.toJsonTree(new APIRespDto("S", rules)));
			return ResponseEntity.ok(resp.toString());

		} catch (Exception e) {
			String errorMessage = "Unexpected error while fetching rules from DMS App";
			LOGGER.error(errorMessage, e);

			resp.add("hdr", gson.toJsonTree(new APIRespDto("E", errorMessage)));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.toString());
		}
	}

	
	@GetMapping(value = "/getViewRules.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getViewRules() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long id = null;
		try {
			List<RuleAnsDto> rules = dmsFetchRulesApiServiceImpl.getViewRules();
			resp.add("rules", gson.toJsonTree(rules));
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.getSuccessStatus(), "Rules Fetched successfully")));
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
