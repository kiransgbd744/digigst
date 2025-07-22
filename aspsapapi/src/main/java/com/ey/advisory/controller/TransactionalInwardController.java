/**
 * 
 */
package com.ey.advisory.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.TransactionalReportingDto;
import com.ey.advisory.app.docs.dto.TransactionalReqDto;
import com.ey.advisory.app.services.docs.einvoice.TransactionalInwardService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class TransactionalInwardController {

	@Autowired
	private TransactionalInwardService transactionalInwardService;

	private static final String NO_RECORD_FOUND = "No Record Found";
	private static final String RESP = "resp";
	private static final String HDR = "hdr";
	private static final String EXCEPTION_OCCUR = "Exception Occure: ";
	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";
	private static final String REQ = "req";

	@PostMapping(value = "/api/TransactionalInward", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> dailyOutwardReconAPi(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String cntrlChecksum = req.getHeader("cntrlChecksum");
		String Idtoken = req.getHeader("Idtoken");

		JsonObject resp = new JsonObject();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("TransactionalInward request payload is {}", jsonString);
			}
			JsonObject json = requestObject.get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}
			TransactionalReportingDto payloadData = gsonEwb.fromJson(json,
					TransactionalReportingDto.class);

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
			if (payloadData != null) {
				transactionalInwardService.persistData(
						payloadData.getDateOfReporting(),
						payloadData.getData());
			}

			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("S", "SUCCESS")));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);

			return responseEntity;
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			LOGGER.error("TransactionalInward method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			return responseEntity;
			// return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	@PostMapping(value = "/ui/getTransactionalInwardDocs", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDailyReconResults(
			@RequestBody String jsonReq) {
		
		JsonObject resp = new JsonObject();
		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject().get(REQ).getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			TransactionalReqDto request = gson.fromJson(requestObject,
					TransactionalReqDto.class);

			TransactionalReportingDto transResp = transactionalInwardService
					.getData(request);

			if (transResp != null) {
				JsonElement resBody = gson.toJsonTree(transResp);
				resp.add(HDR, gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add(RESP, resBody);
			} else {
				resp.add(RESP, gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), NO_RECORD_FOUND)));
			}
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
			resp.add(HDR, new Gson().toJsonTree(new APIRespDto(
					APIRespDto.getErrorStatus(), e.getMessage())));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"end of TransactionalInward.getData(request)" + "{}",
					resp.toString());
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}




}
