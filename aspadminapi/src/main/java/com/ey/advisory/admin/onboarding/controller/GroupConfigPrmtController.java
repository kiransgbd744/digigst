package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.List;

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

import com.ey.advisory.admin.data.repositories.client.OnboardingRequestPayloadRepository;
import com.ey.advisory.admin.services.onboarding.EntityConfigPrmtService;
import com.ey.advisory.admin.services.onboarding.OnboardingRequestPayloadService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * '@POST For ENTITY_CONFIG_PARAMTR
 * 
 * @author Umesh
 *
 */
@RestController
public class GroupConfigPrmtController {

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(GroupConfigPrmtController.class);

	@Autowired
	@Qualifier("GroupConfigPrmtServiceImpl")
	private EntityConfigPrmtService entityConfigPrmtService;
	
	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String RESP = "resp";
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadRepository")
	private OnboardingRequestPayloadRepository payloadRepo;
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadService")
	private OnboardingRequestPayloadService payloadService;
	

	@PostMapping(value = "/getGroupConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGroupConfig(@RequestBody String jsonReqObj) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestReqObject = JsonParser.parseString(jsonReqObj)
			        .getAsJsonObject().get("req").getAsJsonObject();
			EntityConfigPrmtReqDto dto = gson.fromJson(requestReqObject,
			        EntityConfigPrmtReqDto.class);
			List<EntityConfigPrmtResDto> resDtos = entityConfigPrmtService
			        .getEntityConfigPrmt(dto);
			resp.add("resp", gson.toJsonTree(resDtos));
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
			        HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/updateGroupConfigPrmt.do", produces = {
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
			
			List<EntityConfigPrmtReqDto> dtos = gson.fromJson(jsonReqArray, listType);
			 id = payloadService.savePayoadRequest(jsonReq, 0, "GROUP");
			
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
}
