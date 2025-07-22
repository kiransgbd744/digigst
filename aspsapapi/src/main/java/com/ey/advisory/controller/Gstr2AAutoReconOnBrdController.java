package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoRecon2AOnBrdService;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AutoRecon2APROnBoardingReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
public class Gstr2AAutoReconOnBrdController {

	@Autowired
	@Qualifier("AutoRecon2AOnBrdServiceImpl")
	AutoRecon2AOnBrdService autoRecon2AService;

	@PostMapping(value = "/ui/saveAutoReconOnBoarding", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAutoReconOnBoarding(
			@RequestBody String jsonString, HttpServletRequest request,
			HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", requestObject);
			}

			AutoRecon2APROnBoardingReqDto reqDto = gson.fromJson(requestObject,
					AutoRecon2APROnBoardingReqDto.class);

			String status = autoRecon2AService.saveOnBoardingDetails(reqDto);

			if ("Success".equalsIgnoreCase(status)) {
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree("Data Saved Successfully"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception ex) {
			LOGGER.error("Exception while saveAutoReconOnBoarding ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getAutoReconOnBoarding", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoReconOnBoarding(
			HttpServletRequest request, HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			JsonObject respObj = autoRecon2AService
					.getOnBoardingDetails(entityId);

			LOGGER.debug("Response {} ", respObj.toString());
			return new ResponseEntity<>(respObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while saveAutoReconOnBoarding ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Fetch the Records"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	
	
	@PostMapping(value = "/ui/saveAutoReconOnBrdAddParam", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveAutoReconOnBrdAddParam(
			@RequestBody String jsonString, HttpServletRequest request,
			HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		JsonObject resp = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", requestObject);
			}

			AutoRecon2APROnBoardingReqDto reqDto = gson.fromJson(requestObject,
					AutoRecon2APROnBoardingReqDto.class);

			String status = autoRecon2AService.saveOnBrdAddParamDetails(reqDto);

			if ("Success".equalsIgnoreCase(status)) {
				resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree("Data Saved Successfully"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree("Failed to Save Records"));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception ex) {
			LOGGER.error("Exception while saveAutoReconOnBoarding ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Save Records"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getAutoReconAddParamOnBrd", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAutoReconAddParamOnBrd(
			HttpServletRequest request, HttpServletResponse response) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Id ", entityId);
			}
			JsonObject respObj = autoRecon2AService
					.getOnBoardingAddParamDetails(entityId);

			LOGGER.debug("Response {} ", respObj.toString());
			return new ResponseEntity<>(respObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while saveAutoReconOnBoarding ", ex);
			resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree("Failed to Fetch the Records"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
