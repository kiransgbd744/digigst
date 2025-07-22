package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.BCAPIPushCtrlRepository;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.einv.dto.GenerateIrnResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@RestController
public class ManageAutoDraftController {

	@Autowired
	private BCAPIPushCtrlRepository bcAPIPushCtrlRepo;

	@RequestMapping(value = "/api/updateBatchStatus", method = RequestMethod.POST, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> updateBatchStatus(
			@RequestBody String sapRespObj, HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		GenerateIrnResponseDto response = new GenerateIrnResponseDto();
		JsonObject requestObject = (new JsonParser()).parse(sapRespObj)
				.getAsJsonObject();
		try {
			String pushStatus = req.getHeader("pushStatus");
			String batchId = req.getHeader("batchId");
			String status = requestObject.get("hdr").getAsJsonObject()
					.get("status").getAsString();
			String statusStr = status.equalsIgnoreCase("S") ? "Success"
					: "Failed";
			updateBatchStatus(pushStatus, sapRespObj.toString(), batchId);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
//			response.setSuccMsg(statusStr);
			jsonObj.add("resp", gson.toJsonTree(response));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while updateBatchStatus ", ex);
			response.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			response.setErrorMessage(
					"Unable to Update the Auto Drafting Batch Status");
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp", gson.toJsonTree(response));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	private void updateBatchStatus(String status, String msg, String batchId) {
		try {
			bcAPIPushCtrlRepo.updateBatchStatus(status, msg, batchId);
		} catch (Exception e) {
			String msg1 = "Exception occured while updating the job ";
			LOGGER.error(msg1, e);
		}
	}

}
