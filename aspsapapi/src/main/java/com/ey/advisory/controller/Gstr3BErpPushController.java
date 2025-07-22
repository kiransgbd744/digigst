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

import com.ey.advisory.app.data.erp.vertical.services.Gstr3BJsonToCsvConverterAndUploaderService;
import com.ey.advisory.app.docs.dto.Gstr3BHeaderReqDto;
import com.ey.advisory.app.services.docs.Gstr3BPayloadService;
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
 * 
 * @author Mahesh.Golla
 *
 */
@RestController
@Slf4j
public class Gstr3BErpPushController {

	@Autowired
	@Qualifier("Gstr3BPayloadServiceImpl")
	private Gstr3BPayloadService gstr3BPayloadService;

	@Autowired
	@Qualifier("Gstr3BJsonToCsvConverterAndUploaderService")
	private Gstr3BJsonToCsvConverterAndUploaderService convAndUpdService;

	@PostMapping(value = "/api/saveGstr3BApi", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr3BDocs(ServletRequest request,
			@RequestBody String jsonString) {

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
				LOGGER.debug("Gstr3B request payload is {}", jsonString);
			}
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			Gstr3BHeaderReqDto document = gsonEwb.fromJson(reqJson,
					Gstr3BHeaderReqDto.class);
			String sgstin = document.getSgstin();
			String retPeriod = document.getReturnPeriod();
			gstr3BPayloadService.persistPayload(cntrlPayloadId, cntrlChecksum,
					cntrlCount, companyCode, sgstin, retPeriod);

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

						resp.add("hdr", new Gson().toJsonTree(
								new APIRespDto(APIConstants.P, msg)));
						responseEntity = new ResponseEntity<>(resp.toString(),
								HttpStatus.OK);
						return responseEntity;
					}
				}
			}
			String fileName = convAndUpdService.convertAndUpload(document,
					cntrlPayloadId);
			if (fileName == null || fileName.isEmpty()) {
				String errCode = "ER8666";
				String msg = "failed to upload the gstr3b/itc4243";
				gstr3BPayloadService.updateError(cntrlPayloadId, APIConstants.E,
						errCode, msg);

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
			gstr3BPayloadService.update(cntrlPayloadId, APIConstants.P);

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
				gstr3BPayloadService.updateError(cntrlPayloadId, APIConstants.E,
						errorCode, errorMsg);

			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("Gstr7 method exception ", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto(APIConstants.P, errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			return responseEntity;
			// return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}
