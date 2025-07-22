package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.erp.vertical.services.Gstr7JsonToCsvConverterAndUploaderService;
import com.ey.advisory.app.docs.dto.Gstr7HeaderReqDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.app.services.docs.Gstr7PayloadService;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocSaveService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.functions.EinvJsonSchemaValidatorHelper;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@RestController
@Slf4j
public class Gstr7ErpPushController {

	@Autowired
	@Qualifier("Gstr7PayloadServiceImpl")
	private Gstr7PayloadService gstr7PayloadService;

	@Autowired
	@Qualifier("Gstr7JsonToCsvConverterAndUploaderService")
	private Gstr7JsonToCsvConverterAndUploaderService convAndUpdService;

	@Autowired
	@Qualifier("Gstr7TransDocSaveServiceImpl")
	private Gstr7TransDocSaveService srFileSaveService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@PostMapping(value = "/api/saveGstr7Api", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveNilNonExtDocs(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String cntrlPayloadId = req.getHeader("cntrlPayloadId");
		String cntrlChecksum = req.getHeader("cntrlChecksum");
		String cntrlCount = req.getHeader("cntrlCount");
		String companyCode = req.getHeader("companyCode");

		Enumeration headerNames = req.getHeaderNames();
		Map<String, String> hdrmap = new HashMap<String, String>();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = req.getHeader(key);
			hdrmap.put(key, value);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr7requestHeaders are  {}", hdrmap);
		}

		JsonObject resp = new JsonObject();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7 request payload is {}", jsonString);
			}
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}
			Gstr7HeaderReqDto document = gsonEwb.fromJson(reqJson,
					Gstr7HeaderReqDto.class);
			String sgstin = document.getSgstin();
			String retPeriod = document.getReturnPeriod();
			gstr7PayloadService.persistPayload(cntrlPayloadId, cntrlChecksum,
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
				String msg = "failed to upload the docSeries";
				gstr7PayloadService.updateError(cntrlPayloadId, APIConstants.E,
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
			gstr7PayloadService.update(cntrlPayloadId, APIConstants.P);

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
				gstr7PayloadService.updateError(cntrlPayloadId, APIConstants.E,
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

	@PostMapping(value = "/api/saveGstr7TransApi", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr7TransApi(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String cntrlPayloadId = req.getHeader("cntrlPayloadId");
		String cntrlChecksum = req.getHeader("cntrlChecksum");
		String cntrlCount = req.getHeader("cntrlCount");
		String companyCode = req.getHeader("companyCode");

		Enumeration headerNames = req.getHeaderNames();
		Map<String, String> hdrmap = new HashMap<String, String>();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = req.getHeader(key);
			hdrmap.put(key, value);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr7requestHeaders are  {}", hdrmap);
		}

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		ResponseEntity<String> responseEntity = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7 request payload is {}", jsonString);
			}
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonArray reqJson = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", reqJson);
			}

			Type listType = new TypeToken<List<Gstr7TransDocHeaderEntity>>() {
			}.getType();

			List<Gstr7TransDocHeaderEntity> documents = gson
					.fromJson(reqJson, listType);

			LOGGER.error("documents {} ", documents);
			gstr7PayloadService.persistPayload(cntrlPayloadId, cntrlChecksum,
					cntrlCount, companyCode, null, null);

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
			String schemaName = "Gstr7TransactionalJsonSchema.json";

			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, schemaName);

			if (errorList.isEmpty()) {
				EInvoiceOutwardDocSaveRespDto finalResp = srFileSaveService
						.saveDocuments(documents, null, null, null);
				List<EInvoiceDocSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();
				LOGGER.error("docSaveResponse {} ", docSaveResponse);
				JsonElement respBody = gson.toJsonTree(docSaveResponse);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);	
				LOGGER.error("respBody {} ", respBody);
				gstr7PayloadService.update(cntrlPayloadId, APIConstants.P);
				return new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);
			} else {

				String errMsg = "Error occured while validating json schema";
				JsonElement respBody = gson.toJsonTree(errorList);
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto(APIConstants.E, errMsg)));
				resp.add("resp", respBody);

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				String errorCode = "ER7778";
				String msg = respBody.toString();
				if (msg.length() > 500) {
					msg = msg.substring(0, 500);
				}
				gstr7PayloadService.updateError(cntrlPayloadId, APIConstants.E,
						errorCode, errMsg);
				return responseEntity;
			}

		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;
			try {
				gstr7PayloadService.updateError(cntrlPayloadId, APIConstants.E,
						errorCode, errorMsg);

			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("Gstr7 method exception ", ex);
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto(APIConstants.E, errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			return responseEntity;
		}
	}

}
