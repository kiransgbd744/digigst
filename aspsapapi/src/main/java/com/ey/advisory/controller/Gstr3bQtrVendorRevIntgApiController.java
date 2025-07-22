package com.ey.advisory.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.Gstr3bQtrFilingPayloadRepository;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingPayloadEntity;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.functions.EinvJsonSchemaValidatorHelper;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * This Api will return Gstr3b Filing Details for Quarterly Filing Type from
 * Gstin Side and also validate if this is Valid GSTIN or not
 */

@Slf4j
@RestController
public class Gstr3bQtrVendorRevIntgApiController {

	@Autowired
	@Qualifier("Gstr3bQtrFilingPayloadServiceImpl")
	private Gstr3bQtrFilingPayloadService payloadService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr3bQtrFilingPayloadRepository")
	private Gstr3bQtrFilingPayloadRepository payloadRepository;

	@PostMapping(value = "/api/gstr3bQtrFilingApiPush", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstinValidatorApiRequest(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String pushType = req.getHeader("pushType");
		String checkSum = req.getHeader("checkSum");

		return saveGstr3bQtrFilingRequest(payloadId, null, pushType, jsonString,
				"Gstr3bQtrFilingSchema.json", checkSum);
	}

	private ResponseEntity<String> saveGstr3bQtrFilingRequest(String payloadId,
			String category, String pushType, String jsonString,
			String schemaName, String checkSum) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		ResponseEntity<String> responseEntity = null;
		List<String> revIntjobParamsList = new ArrayList<>();
		JsonObject revIntjobjsonParams = new JsonObject();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			if (!Strings.isNullOrEmpty(checkSum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checkSum)
					&& !checkSum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}

			Optional<Gstr3bQtrFilingPayloadEntity> isRecordPres = payloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);

			int gstinCount = 0;

			if (isRecordPres.isPresent()) {
				payloadService.create(payloadId, String.valueOf(gstinCount),
						pushType, genCheckSum, category, APIConstants.DUP,
						checkSum, jsonString);

				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);

				JsonObject respMsg = new JsonObject();
				respMsg.addProperty("msg", "Record is already in InProgress.");

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}

			payloadService.create(payloadId, String.valueOf(gstinCount),
					pushType, genCheckSum, category, APIConstants.IP, checkSum,
					jsonString);

			revIntjobjsonParams.addProperty("payloadId", payloadId);
			revIntjobjsonParams.addProperty("scenarioName",
					APIConstants.GSTR3B_QTR_FILING_PAYLOAD_METADATA_REV_INTG);

			revIntjobParamsList.add(revIntjobjsonParams.toString());

			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, schemaName);

			if (errorList.isEmpty()) {
				JsonObject jsonParams = new JsonObject();

				jsonParams.addProperty("payloadId", payloadId);
				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.GSTR3B_QTR_FILING_API_PUSH,
						jsonParams.toString(), userName, JobConstants.PRIORITY,
						JobConstants.PARENT_JOB_ID,
						JobConstants.SCHEDULE_AFTER_IN_MINS);

				JsonObject respMsg = new JsonObject();

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "S");
				hdrMsg.addProperty("payloadId", payloadId);
				hdrMsg.addProperty("msg", "Received Successfully");

				resp.add("hdr", gson.toJsonTree(hdrMsg));

				resp.add("resp", gson.toJsonTree(respMsg));

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);

				return responseEntity;
			} else {

				JsonElement respBody = gson.toJsonTree(errorList);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);
				hdrMsg.addProperty("msg",
						"Error occured while validating json schema");

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", respBody);

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				String errorCode = "ER7778";
				String msg = respBody.toString();
				if (msg.length() > 500) {
					msg = msg.substring(0, 500);
				}
				payloadService.updateError(payloadId, APIConstants.E, errorCode,
						msg,Integer.valueOf(pushType));

				createMetaDataJobs(revIntjobParamsList);

				return responseEntity;
			}
		} catch (Exception ex) {
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = "ERP (run time error) -" + ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;
			try {
				payloadService.updateErrorExc(payloadId, APIConstants.E,
						errorCode.length() > 500 ? errorCode.substring(0, 500)
								: errorCode,
						errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
								: errorMsg,
						9);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server", e);
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);

			JsonObject hdrMsg = new JsonObject();
			hdrMsg.addProperty("status", "E");
			hdrMsg.addProperty("payloadId", payloadId);

			JsonObject respMsg = new JsonObject();
			respMsg.addProperty("msg", errorMsg);

			resp.add("hdr", new Gson().toJsonTree(hdrMsg));
			resp.add("resp", new Gson().toJsonTree(respMsg));

			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);

			createMetaDataJobs(revIntjobParamsList);

			return responseEntity;
		} finally {
			CommonContext.clearContext();
		}
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.GSTR3B_QTR_FILING_PAYLOAD_METADATA_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private static List<Object[]> convertJsonArrayToList(JsonArray jsonArray) {
		List<Object[]> resultList = new ArrayList<>();

		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Object[] file = jsonObject.entrySet().stream()
						.map(entry -> entry.getValue()).toArray();
				resultList.add(file);
			}
		}

		return resultList;
	}

	public static void printByteArray(byte[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.printf("%02X", array[i]);
			if ((i % 4) == 3)
				System.out.print(" ");
		}
		System.out.println();
	}
}
