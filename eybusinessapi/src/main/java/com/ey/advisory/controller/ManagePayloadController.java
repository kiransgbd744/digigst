package com.ey.advisory.controller;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.business.dto.PayloadReqDto;
import com.ey.advisory.app.data.business.dto.PayloadRespDto;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PayloadCommUtility;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
public class ManagePayloadController {

	@Autowired
	private PayloadCommUtility paylComUtil;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	@PostMapping(value = "/api/getPayloads", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPayloads(@RequestBody String jsonString,
			HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		PayloadRespDto respDto = new PayloadRespDto();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			PayloadReqDto hdr = gson.fromJson(requestObject.get("req"),
					PayloadReqDto.class);

			Optional<ERPRequestLogEntity> ent = paylComUtil.payloadService(hdr);

			if (ent.isPresent()) {

				String reqBody = ent.get().getReqPayload();
				String nicReqPay = ent.get().getNicReqPayload();
				String nicResp = ent.get().getNicResPayload();
				String cloudTimeStamp = ent.get().getCloudTimestamp()
						.format(formatter);
				respDto.setReqBody(reqBody);
				respDto.setNicReqPayload(nicReqPay);
				respDto.setNicRespPayload(nicResp);
				respDto.setId(ent.get().getId());
				respDto.setCloudTimeStamp(cloudTimeStamp);
				respDto.setApiType(ent.get().getApiType());
				JsonElement respBody = gson.toJsonTree(respDto);
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				jsonObj.add("resp", respBody);
				return new ResponseEntity<>(jsonObj.toString().trim(),
						HttpStatus.OK);
			} else {
				respDto.setErrorMessage("No Error Records Available");
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.add("resp", gson.toJsonTree(respDto));
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}

		} catch (Exception ex) {

			LOGGER.error("Exception while Retriving the Payloads ", ex);
			JsonObject resp = new JsonObject();
			respDto.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respDto));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/api/saveFallBackLogDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveLogDetails(
			@RequestBody String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceBcapi();
		try {
			ERPRequestLogEntity logEntity = gson.fromJson(jsonString,
					ERPRequestLogEntity.class);
			logEntity.setId(null);
			reqLogHelper.saveLogEntity(logEntity);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp",
					gson.toJsonTree(APIConstants.SUCCESS.toLowerCase()));
			return new ResponseEntity<>(jsonObj.toString().trim(),
					HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Logging the Payloads ", ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);

		}
	}

}
