package com.ey.advisory.controller.vendor.validation;

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

import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.app.vendor.service.VendorApiGstinValidatorPayloadServiceImpl;
import com.ey.advisory.app.vendor.service.VendorValidatorPayloadEntity;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.Verma
 */
@RestController
@Slf4j
public class VendorValidationApiPushController {

	@Autowired
	@Qualifier("VendorApiGstinValidatorPayloadServiceImpl")
	private VendorApiGstinValidatorPayloadServiceImpl payloadService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@PostMapping(value = "/api/vendorValidationAPiPush", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveVendorValidatorApiRequest(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		// String companyCode = req.getHeader("companyCode");
		// String pushType = req.getHeader("pushType");
		String checkSum = req.getHeader("checkSum");

		return saveGstinValidatorRequest(payloadId, null, null, null,
				jsonString, "VendorValidatorSchema.json", checkSum);
	}

	private ResponseEntity<String> saveGstinValidatorRequest(String payloadId,
			String category, String companyCode, String pushType,
			String jsonString, String schemaName, String checkSum) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		ResponseEntity<String> responseEntity = null;
		List<String> revIntjobParamsList = new ArrayList<>();
		JsonObject revIntjobjsonParams = new JsonObject();

		String sourceId = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			/*
			 * List<Object> sourceIds = erpCompanyCodeMappingRepository
			 * .findSourceIdByCompanyCode(companyCode); if (sourceIds != null &&
			 * !sourceIds.isEmpty()) { sourceId =
			 * String.valueOf(sourceIds.get(0)); }
			 */

			if (!Strings.isNullOrEmpty(checkSum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checkSum)
					&& !checkSum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}

			Optional<VendorValidatorPayloadEntity> isRecordPres = payloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);

			if (isRecordPres.isPresent()) {
				payloadService.create(payloadId, companyCode, null, pushType,
						genCheckSum, category, APIConstants.DUP, checkSum,
						jsonString);

				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);

				JsonObject hdrMsg = new JsonObject();
				hdrMsg.addProperty("status", "E");
				hdrMsg.addProperty("payloadId", payloadId);
				hdrMsg.addProperty("msg", "Record is already in InProgress.");

				JsonObject respMsg = new JsonObject();
				

				resp.add("hdr", new Gson().toJsonTree(hdrMsg));
				resp.add("resp", new Gson().toJsonTree(respMsg));

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}

			payloadService.create(payloadId, companyCode, null, pushType,
					genCheckSum, category, APIConstants.IP, checkSum,
					jsonString);

			revIntjobjsonParams.addProperty("payloadId", payloadId);
			// revIntjobjsonParams.addProperty("sourceId", sourceId);
			revIntjobjsonParams.addProperty("scenarioName",
					APIConstants.VENDOR_VALIDATOR_API_REV_INTG);

			revIntjobParamsList.add(revIntjobjsonParams.toString());

			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, schemaName);

			if (errorList.isEmpty()) {

				JsonObject jsonParams = new JsonObject();

				jsonParams.addProperty("payloadId", payloadId);
				// jsonParams.addProperty("sourceId", sourceId);

				asyncJobsService.createJob(TenantContext.getTenantId(),
						JobConstants.VENDOR_VALIDATOR_API_PUSH,
						jsonParams.toString(), "SYSTEM", JobConstants.PRIORITY,
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
						msg);

				createMetaDataJobs(revIntjobParamsList);

				return responseEntity;
			}
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode()
					: "ER8888";
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
				LOGGER.error("Exception while connecting server {} ", e);
			}
			LOGGER.error("VendorValidationApiPushController"
					+ " method exception {} ", ex);

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

		String jobCategory = APIConstants.VENDOR_VALIDATOR_API_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

}
