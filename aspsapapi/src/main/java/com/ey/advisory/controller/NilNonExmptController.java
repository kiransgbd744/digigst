/**
 * 
 */
package com.ey.advisory.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.erp.vertical.services.NilNonExtJsonToCsvConverterAndUploaderService;
import com.ey.advisory.app.docs.dto.NilNonExmptHeaderReqDto;
import com.ey.advisory.app.services.docs.NilNonExmptPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class NilNonExmptController {

	@Autowired
	@Qualifier("NilNonExmptPayloadServiceImpl")
	private NilNonExmptPayloadService nilNonExmptPayloadService;

	@Autowired
	@Qualifier("NilNonExtJsonToCsvConverterAndUploaderService")
	private NilNonExtJsonToCsvConverterAndUploaderService convAndUpdService;

	@PostMapping(value = { "/api/saveNilNonExt",
			"/api/saveNewNilNonExt" }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveNilNonExtDocs(ServletRequest request,
			@RequestBody String jsonString, HttpServletRequest httpRequest)
			throws Exception {

		HttpServletRequest req = (HttpServletRequest) request;
		String cntrlPayloadId = req.getHeader("cntrlPayloadId");
		String cntrlChecksum = req.getHeader("cntrlChecksum");
		String cntrlCount = req.getHeader("cntrlCount");
		String companyCode = req.getHeader("companyCode");

		JsonObject resp = new JsonObject();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DocSeries request payload is {}", jsonString);
			}
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			NilNonExmptHeaderReqDto document = gsonEwb.fromJson(reqJson,
					NilNonExmptHeaderReqDto.class);
			String sgstin = document.getSgstin();
			String retPeriod = document.getReturnPeriod();
			nilNonExmptPayloadService.persistPayload(cntrlPayloadId,
					cntrlChecksum, cntrlCount, companyCode, sgstin, retPeriod);

			if (Strings.isNullOrEmpty(cntrlPayloadId)) {
				String msg = "cntrlPayloadId is not available";
				String errorCode = "ER6666";
				LOGGER.error(errorCode, msg);

				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto(APIConstants.P, msg)));
				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}
			if (!Strings.isNullOrEmpty(cntrlChecksum)) {
				if (!APIConstants.NOCHECKSUM.equalsIgnoreCase(cntrlChecksum)) {
					byte[] hashVal = Hashing.sha1()
							.hashString(jsonString, StandardCharsets.UTF_8)
							.asBytes();
					String genChecksm = Base64.getEncoder()
							.encodeToString(hashVal);
					if (!cntrlChecksum.equalsIgnoreCase(genChecksm)) {
						String msg = "CheckSum is not matching";
						String errorCode = "ER7777";
						LOGGER.error(errorCode, msg);
						throw new AppException(msg, errorCode);
					}
				}
			}
			String path = httpRequest.getServletPath();
			String fileName = convAndUpdService.convertAndUpload(document,
					cntrlPayloadId, path);
			if (fileName == null || fileName.isEmpty()) {
				String errCode = "ER8666";
				String msg = "failed to upload the docSeries";
				nilNonExmptPayloadService.updateError(cntrlPayloadId,
						APIConstants.E, errCode, msg);

				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("P", msg)));
				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", "SUCCESS")));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			nilNonExmptPayloadService.update(cntrlPayloadId, APIConstants.P);

			return responseEntity;
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;
			try {
				nilNonExmptPayloadService.updateError(cntrlPayloadId,
						APIConstants.E, errorCode, errorMsg);

			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("nilNonExt method exception ", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto(APIConstants.P, errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			return responseEntity;
			// return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}
