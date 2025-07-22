package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.sql.Clob;
import java.util.List;

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
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EntityConfigPrmtReqDto;
import com.ey.advisory.core.dto.EntityConfigPrmtResDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * '@POST For ENTITY_CONFIG_PARAMTR
 * 
 * @author Umesh
 *
 */
@RestController
public class EntityConfigPrmtController {

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(EntityConfigPrmtController.class);

	@Autowired
	@Qualifier("entityConfigPrmtService")
	private EntityConfigPrmtService entityConfigPrmtService;
	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String RESP = "resp";
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadRepository")
	private OnboardingRequestPayloadRepository payloadRepo;
	
	@Autowired
	@Qualifier("OnboardingRequestPayloadService")
	private OnboardingRequestPayloadService payloadService;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	

	@PostMapping(value = "/getConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getConfig(@RequestBody String jsonReqObj) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestReqObject = (new JsonParser()).parse(jsonReqObj)
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

	@PostMapping(value = "/updateConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateConfigPrmt(@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Long id = null;
		try {
			JsonArray jsonReqArray = (new JsonParser()).parse(jsonReq)
			        .getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<EntityConfigPrmtReqDto>>() {
			}.getType();
			
			List<EntityConfigPrmtReqDto> dtos = gson.fromJson(jsonReqArray, listType);
			 
			 id = payloadService.savePayoadRequest(jsonReq, 
					Integer.valueOf(dtos.get(0).getEntityId().intValue()), "ENTITY");
			
			entityConfigPrmtService.updateConfigParmetr(dtos);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
			        APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
			try {
				 payloadRepo.updateRespMsg(new 
						 javax.sql.rowset.serial.SerialClob(UPDATE_SUCCESSFULLY
						 .toCharArray()), id);
			}catch(Exception ex) {
				LOGGER.error(ex.getLocalizedMessage());
			}
			
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			Clob errorMsg = null;
			try {
				 errorMsg = new javax.sql.rowset.serial.SerialClob(
					e.getLocalizedMessage().toCharArray());
			}catch(Exception ex) {
				LOGGER.error(ex.getLocalizedMessage());
			}
			payloadRepo.updateErrorLog(errorMsg, id);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/getResetConfigPrmt.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getViewRules() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			resp.add(RESP,
					gson.toJsonTree(new APIRespDto(APIRespDto.getSuccessStatus(), "Reset Config Prmt Get API is Tested")));
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/resetConfigPrmt.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> resetConfig(@RequestBody String jsonReqObj) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestReqObject = JsonParser.parseString(jsonReqObj)
					.getAsJsonObject().get("req").getAsJsonObject();

			EntityConfigPrmtReqDto dto = gson.fromJson(requestReqObject,
					EntityConfigPrmtReqDto.class);
			
			String type= dto.getType();
			Long entityId=dto.getEntityId();
			
			if(type!=null){
				type=type.toUpperCase();
			}
			
			if(entityId==null){
				entityId=0L;
			}
			
			
			String status = callResetDefaultAnsProc(type, entityId);
			

			if ("SUCCESS".equalsIgnoreCase(status)) {
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			} 

			
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private String callResetDefaultAnsProc(String type, Long entityId) {

		String response = "";
		try {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The parameters passed to USP_DEFAULT_CONFG_ANSWERS proc "
								+ " type {}, entityId {}",
						type, entityId);
			}
			
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_DEFAULT_CONFG_ANSWERS");

			
			storedProc.registerStoredProcedureParameter("P_CATEGORY",
					String.class, ParameterMode.IN);
			
			storedProc.registerStoredProcedureParameter("P_ENTITY_ID",
					Long.class, ParameterMode.IN);
			
			
			storedProc.setParameter("P_CATEGORY", type);

			storedProc.setParameter("P_ENTITY_ID", entityId);

			response = (String) storedProc.getSingleResult();

			LOGGER.error("USP_DEFAULT_CONFG_ANSWERS" + " :: " + response);
			
		} catch (Exception ex) {
			LOGGER.error("USP_DEFAULT_CONFG_ANSWERS proc Failed  "
					+ ex.getLocalizedMessage());
			response = "FAIL";
		}
		return response;

	}
}
