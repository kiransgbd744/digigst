package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.services.onboarding.MakerCheckerWorkflowServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.MakerCheWorkFlowFinalRespDto;
import com.ey.advisory.core.dto.MakerCheckerReqDto;
import com.ey.advisory.core.dto.MakerCheckerWorkflowRespDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MakerCheckerWorkflowController {

	@Autowired
	@Qualifier(value = "MakerCheckerWorkflowServiceImpl")
	private MakerCheckerWorkflowServiceImpl workflowServiceImpl;

	@PostMapping(value = "/getMakerCheckerWorkflowMaster.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMakerCheckerWorkflowMaster(
			@RequestBody String reqJson) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObject = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			MakerCheckerReqDto reqDto = gson.fromJson(reqObject,
					MakerCheckerReqDto.class);
			List<MakerCheckerWorkflowRespDto> makerCheckWorkflow = workflowServiceImpl
					.getMakerCheckerWorkflowDetails(reqDto.getEntityId());

			JsonElement respDto = gson.toJsonTree(makerCheckWorkflow);
			resp.add("resp", respDto);

		} catch (Exception e) {
			LOGGER.error("Exception Ocuured:{}", e);
			throw new AppException("Exception Occured: {}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/updateMasterCheckerWorkflowMaster.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMasterCheckerWorkflowMaster(
			@RequestBody String reqObj) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonArray req = (new JsonParser().parse(reqObj)).getAsJsonObject()
					.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<MakerCheckerReqDto>>() {
			}.getType();
			List<MakerCheckerReqDto> reqDtos = gson.fromJson(req, listType);

			workflowServiceImpl.updateMkrCkrMapEntity(reqDtos);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			throw new AppException("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getMakerCheckerWorkflow.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMakerCheckerWorkflow(
			@RequestBody String req) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			MakerCheckerReqDto reqDto = gson.fromJson(reqObj,
					MakerCheckerReqDto.class);
			MakerCheWorkFlowFinalRespDto mkrCheWrkFlowFinalRespDto = workflowServiceImpl
					.getMkrCkrWorkFlow(reqDto.getEntityId());
			JsonElement jsonElement = gson
					.toJsonTree(mkrCheWrkFlowFinalRespDto);
			resp.add("resp", jsonElement);
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			throw new AppException("Exception Occured:{}", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/updateMakerCheckerWorkflow.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateMakerCheckerWorkflow(
			@RequestBody String reqObj) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonArray jsonReq = (new JsonParser().parse(reqObj))
					.getAsJsonObject().get("req").getAsJsonArray();

			Type listType = new TypeToken<List<MakerCheckerReqDto>>() {
			}.getType();

			List<MakerCheckerReqDto> reqDtos = gson.fromJson(jsonReq, listType);
			workflowServiceImpl.updateMkrCkrWorkFlow(reqDtos);
			resp.add("resp", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), "Updated Successfully")));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			throw new AppException("Exception Occured:{]", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
