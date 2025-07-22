package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DummyController {
	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpScenarioMasterRepository masterRepo;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@PostMapping(value = "/ui/dummyRev", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3bAutoCalcAndSave(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		JsonObject resp = new JsonObject();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqJson = requestObject.get("req").getAsJsonObject();

			String gstin = reqJson.get("gstin").getAsString();

			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			//reverseIntegration("y8nvcqp4f9", gstin, taxPeriod);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree("Success"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while get3BAutoCalAndSave";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private void reverseIntegration(String groupCode, String gstin,
			String taxPeriod) {
		
		GSTNDetailEntity gstinEntity = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);
			
		Long entityId = gstinEntity.getEntityId();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration entityId -> '%d'",entityId);
			LOGGER.debug(msg);
		}
		Long scenarioId = masterRepo.findSceIdOnScenarioName(JobConstants.GSTR3B_REV_INT);	
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration scenarioId -> '%d'",scenarioId);
			LOGGER.debug(msg);
		}
		 		
		if (scenarioId == null) {

			LOGGER.error("GSTR3B reverseIntegration returning without data,"
					+ " No scenarioId found for  -> {} ", scenarioId);
			return;

		}
		ErpEventsScenarioPermissionEntity scenPermissionEntity = 
				erpEventsScenPermiRep.findByScenarioIdAndIsDeleteFalse(scenarioId);
		
		String destName = scenPermissionEntity.getDestName();
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration scenPermissionEntity -> '%s'",
					scenPermissionEntity);
			LOGGER.debug(msg);
		}

		JsonObject jsonParams = new JsonObject();
		
		jsonParams.addProperty("groupcode", groupCode);
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("finYear", taxPeriod);
		jsonParams.addProperty("entityId", entityId.toString());
		jsonParams.addProperty("scenarioId", scenarioId);
		jsonParams.addProperty("destinationName",destName);
		jsonParams.addProperty("source"," GSTR3B_SAVE");
		
		asyncJobsService.createJob(groupCode,
				JobConstants.GSTR3B_REV_INT, jsonParams.toString(),
				"SYSTEM", 1L, null, null);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration submitted job "
					+ "from tasnaction polling  jsonParams -> '%s'",
					jsonParams.toString());
			LOGGER.debug(msg);
		}
	}
}
