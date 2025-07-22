/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.common.InwardPayloadService;
import com.ey.advisory.app.services.docs.einvoice.OutwardPayloadService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class SaveCPIErrorController {

	@Autowired
	@Qualifier("OutwardPayloadServiceImpl")
	private OutwardPayloadService outwardPayloadService;

	@Autowired
	@Qualifier("InwardPayloadServiceImpl")
	private InwardPayloadService inwardPayloadService;

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";

	@PostMapping(value = "/api/outwardSaveCpiError", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveOutwardCPIError(
			@RequestHeader(value = "payloadId") String payloadId,
			@RequestHeader(value = "checksum") String checksum,
			@RequestHeader(value = "docCount") String docCount,
			@RequestHeader(value = "pushType") String pushType,
			@RequestHeader(value = "companyCode") String companyCode,
			@RequestHeader(value = "errorCode") String errorCode,
			@RequestHeader(value = "jsonErrorResp") String jsonErrorResp,
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			outwardPayloadService.saveCPIError(payloadId, companyCode, docCount,
					checksum, pushType,errorCode,jsonErrorResp);

			resp.add("hdr", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			String msg = "Unexpected error while saving/updating meta data";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/api/inwardSaveCpiError", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardCPIError(
			@RequestHeader(value = "payloadId") String payloadId,
			@RequestHeader(value = "checksum") String checksum,
			@RequestHeader(value = "docCount") String docCount,
			@RequestHeader(value = "pushType") String pushType,
			@RequestHeader(value = "companyCode") String companyCode,
			@RequestHeader(value = "errorCode") String errorCode,
			@RequestHeader(value = "jsonErrorResp") String jsonErrorResp,
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			inwardPayloadService.saveCPIError(payloadId, companyCode, docCount,
					checksum, pushType,errorCode,jsonErrorResp);

			resp.add("hdr", gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			String msg = "Unexpected error while saving/updating meta data";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
