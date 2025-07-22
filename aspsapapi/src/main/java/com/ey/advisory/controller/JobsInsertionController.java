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

import com.ey.advisory.app.data.entities.client.SaveToGstnEventStatusEntity;
import com.ey.advisory.app.data.entities.simplified.client.RequestIdWiseEntity;
import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.jobs.anx2.RequestIdWiseUtil;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class JobsInsertionController {

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
	GSTNAuthTokenService authTokenService;
	
	@Autowired
	private RequestIdWiseUtil reqIdUtil;

	@PostMapping(value = "/ui/b2cs/views/jobs", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createJob(@RequestBody String jsonParam) {
		try {
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.B2CSVIEW, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

/*	@PostMapping(value = "/ui/gstr1SaveToGstnJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1SaveToGstnJob(@RequestBody 
			String jsonParam) {

		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr1SaveToGstnJob Request received from UI as {} ",jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();
			
			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Gstr1SaveToGstnReqDto.class);
			
			String groupCode = TenantContext.getTenantId();
			
			JsonArray respBody = getAllActiveGstnList(dto);
			
			if(dto.getSgstins() == null || (dto.getSgstins() != null && dto.getSgstins().isEmpty())){
				
				if(LOGGER.isDebugEnabled()){
					
					LOGGER.debug("No active GSTNs found for GSTR1 save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp",gson.toJsonTree(respBody));
				
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			
			List<Pair<String, String>> gstr1Pairs = screenExtractor.
					getTestGstr1CombinationPairs(dto, groupCode);
			List<SaveToGstnEventStatusEntity> repEntity = new ArrayList<>();
			
			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				//if (!value0.isEmpty() && !value1.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					LOGGER.info("groupCode {} is set", groupCode);
					
				SaveToGstnEventStatusEntity entity = null;
//			if (taxPeriod != null && taxPeriod.length() > 0 && gstin != null
//					&& gstin.length() > 0) {
				AsyncExecJob job = asyncJobsService.createJob(groupCode,
						JobConstants.GSTR1_SAVETOGSTN, reqObject.toString(),
						JobConstants.SYSTEM,
						JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
				
				for (int i = 0; i < gstr1Pairs.size(); i++) {
					
					Pair<String, String> pair = gstr1Pairs.get(i);
					
					// status code 10 says status as USER REQUEST INITIATED
					entity = saveToGstnEventStatus.EventEntry(pair.getValue1(), 
							pair.getValue0(), 10, groupCode);
					if(entity != null) {
						repEntity.add(entity);
					}
					JsonObject json = new JsonObject();
					json.addProperty("gstin",pair.getValue0());
					json.addProperty("msg","GSTR1 Save Request Submitted Successfully");
					respBody.add(json);
					
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp",gson.toJsonTree(respBody));
				
				return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
			//}
		}	
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);	
//			}
			
			
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
*/	
	
	@PostMapping(value = "/ui/anx1SaveToGstnJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createAnx1SaveToGstnJob(@RequestBody 
			String jsonParam) {

		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("anx1SaveToGstnJob Request received from UI as {} ",jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Anx1SaveToGstnReqDto dto = gson.fromJson(reqObject,
					Anx1SaveToGstnReqDto.class);
			String groupCode = TenantContext.getTenantId();
			List<Pair<String, String>> anx1Pairs = screenExtractor.
					getAnx1CombinationPairs(dto, groupCode);
			List<SaveToGstnEventStatusEntity> repEntity = new ArrayList<>();
			
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(repEntity);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			
			if (anx1Pairs != null && !anx1Pairs.isEmpty()) {
				/*List<String> value0 = pairs.getValue0();
				List<String> value1 = pairs.getValue1();
				if (!value0.isEmpty() && !value1.isEmpty()) {*/
					TenantContext.setTenantId(groupCode);
					LOGGER.info("groupCode {} is set", groupCode);
					
				SaveToGstnEventStatusEntity entity = null;
//			if (taxPeriod != null && taxPeriod.length() > 0 && gstin != null
//					&& gstin.length() > 0) {
				AsyncExecJob job = asyncJobsService.createJob(groupCode,
						JobConstants.ANX1_SAVETOGSTN, reqObject.toString(),
						JobConstants.SYSTEM,
						JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
				
				for (int i = 0; i < anx1Pairs.size(); i++) {
					
					Pair<String, String> pair = anx1Pairs.get(i);
					// status code 10 says status as USER REQUEST INITIATED
					entity = saveToGstnEventStatus.EventEntry(pair.getValue1(), 
							pair.getValue0(), 10, groupCode);
					if(entity != null) {
						repEntity.add(entity);
					}
				}
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
					
			//}
		}	
			return new ResponseEntity<>(resp.toString(), HttpStatus.NO_CONTENT);	
//			}
			
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	/*@PostMapping(value = "/ui/Gstr1GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1GstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1GstnGetSection Request received from UI as {} ",request);
			}
			JsonObject resp = new JsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			
			if (requestObject != null) {
				
				JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
				Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {}.getType();
				List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
				
				if(dtos != null && !dtos.isEmpty()) {
					
					String gstin = dtos.get(0).getGstin();
					String authStatus = authTokenService
							.getAuthTokenStatusForGstin(gstin);
					
					if("A".equalsIgnoreCase(authStatus)){
						
						String jsonParam = null;
						
						jsonParam = requestObject.toString();
					
						String groupCode = TenantContext.getTenantId();
						AsyncExecJob job = asyncJobsService.createJob(groupCode,
								JobConstants.GSTR1_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
								JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
								JobConstants.SCHEDULE_AFTER_IN_MINS);
			
						JsonElement respBody = gson.toJsonTree(job);
						resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
						resp.add("resp", respBody);
						
					} else {
						String msg = "Auth Token is Inactive, Please Activate";
						resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
					}
				}
			} else {
				
				String msg = "In valid GSTIN & Taxperiod";
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
				
				
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	/*@PostMapping(value = "/ui/Gstr2aGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr2AGstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr2AGstnGetSection Request received from UI as {} ",request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/

	
	@PostMapping(value = "/ui/Anx1GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createAnx1GstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx1GstnGetSection Request received from UI as {} ",request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.ANX1_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/ui/Anx2GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createAnx2GstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx2GstnGetSection Request received from UI as {} ",request);
			}
			JsonArray asJsonArray = null;
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			
			//Extra logic only in ANX2 GET to generate RequestID and to 
			//attach that RequestID to user request		
			String groupCode = TenantContext.getTenantId();
			RequestIdWiseEntity reqIdBatch = reqIdUtil.createReqIdEntity(
					requestObject != null ? requestObject.toString() : null, groupCode);
			Long requestId = reqIdBatch.getId();
			if(requestObject!=null){
			asJsonArray = requestObject.get("req").getAsJsonArray();
			}
			if(asJsonArray!=null){
			asJsonArray.forEach(jsonObject -> {
				jsonObject.getAsJsonObject().addProperty("requestId",requestId);
			});
			}
			
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.ANX2_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/RetGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createRetGstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("RetGstnGetSection Request received from UI as {} ",request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.RET_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*@PostMapping(value = "/ui/Gstr6GstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr6GstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6GstnGetSection Request received from UI as {} ",request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	*/
	/*@PostMapping(value = "/ui/Gstr6aGstnGetSection", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr6aGstnGetJob(@RequestBody String request) {
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr6aGstnGetSection Request received from UI as {} ",request);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(request)
					.getAsJsonObject();
			//JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			String jsonParam = null;
			if (requestObject != null) {
				jsonParam = requestObject.toString();
			}
			String groupCode = TenantContext.getTenantId();
			AsyncExecJob job = asyncJobsService.createJob(groupCode,
					JobConstants.GSTR6A_GSTN_GET_SECTION, jsonParam, JobConstants.SYSTEM,
					JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(job);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents";
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	*/

	private JsonArray getAllActiveGstnList(Gstr1SaveToGstnReqDto gstr1SaveDto){
		
		if(LOGGER.isDebugEnabled()){
			
			LOGGER.debug("Getting all the active GSTNs for GSTR1 save");
		}
		
		String msg = "";
		List<String> activeGstins = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if(gstr1SaveDto != null){
			
			for (String gstin : gstr1SaveDto.getSgstins()) {
				JsonObject json = new JsonObject();
				String groupCode = TenantContext.getTenantId();
				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstin);
				
				if (authStatus.equals("A")) {
					
					activeGstins.add(gstin);
				} else {
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin",gstin);
					json.addProperty("msg",msg);
					respBody.add(json);
					
				}
			}
			gstr1SaveDto.setSgstins(activeGstins);
		}
		return respBody;
	}

	// ----------------------------GSTR6---------------------------//
	/*@PostMapping(value = "/ui/gstr6SaveToGstnJob", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr6SaveToGstnJob(
			@RequestBody String jsonParam) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr6SaveToGstnJob Request received from UI as {} ",
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
			JsonArray respBody = getAllActiveGstnList(dto);
			if (dto.getSgstins() == null || (dto.getSgstins() != null
					&& dto.getSgstins().isEmpty())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("No active GSTNs found for GSTR6 save");
				}
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			List<Pair<String, String>> gstr1Pairs = screenExtractor
					.getTestGstr1CombinationPairs(dto, groupCode);
			List<SaveToGstnEventStatusEntity> repEntity = new ArrayList<>();
			if (gstr1Pairs != null && !gstr1Pairs.isEmpty()) {
				TenantContext.setTenantId(groupCode);
				LOGGER.info("groupCode {} is set", groupCode);
				SaveToGstnEventStatusEntity entity = null;
				AsyncExecJob job = asyncJobsService.createJob(groupCode,
						JobConstants.GSTR6_SAVETOGSTN, reqObject.toString(),
						JobConstants.SYSTEM, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);
				for (int i = 0; i < gstr1Pairs.size(); i++) {
					Pair<String, String> pair = gstr1Pairs.get(i);
					entity = saveToGstnEventStatus.EventEntry(pair.getValue1(),
							pair.getValue0(), 10, groupCode);
					if (entity != null) {
						repEntity.add(entity);
					}
					JsonObject json = new JsonObject();
					json.addProperty("gstin", pair.getValue0());
					json.addProperty("msg",
							"GSTR6 Save Request Submitted Successfully");
					respBody.add(json);
				}
				resp.add("hdr",gson.toJsonTree(APIRespDto.createSuccessResp()));
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
	}*/
}
