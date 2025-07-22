/**
 * 
 */
package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.docs.dto.Itc04DocDetailsSaveRespDto;
import com.ey.advisory.app.docs.dto.Itc04DocSaveRespDto;
import com.ey.advisory.app.services.docs.einvoice.Itc04PayloadService;
import com.ey.advisory.app.services.itc04.Itc04DefaultDocSaveService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
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

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class Itc04DocController {

	@Autowired
	@Qualifier("Itc04DefaultDocSaveService")
	private Itc04DefaultDocSaveService itc04DefaultDocSaveService;

	@Autowired
	@Qualifier("Itc04PayloadServiceImpl")
	private Itc04PayloadService itc04PayloadService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;
	
	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@PostMapping(value = "/api/itc04SaveDocuments", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> saveDocumentsFromERP(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");
		String sourceId = req.getHeader("sourceId");
		return saveItc04Documents(payloadId, checksum, docCount, pushType,
				companyCode, sourceId, jsonString);
	}

	private ResponseEntity<String> saveItc04Documents(String payloadId,
			String checksum, String docCount, String pushType,
			String companyCode, String sourceId, String jsonString) {
		
		List<Object> sourceIds = erpCompanyCodeMappingRepository
				.findSourceIdByCompanyCode(companyCode);
		
		if (!sourceIds.isEmpty()) {
			sourceId = sourceId != null ? sourceId
					: String.valueOf(sourceIds.get(0));
		}
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		List<String> metaDataRevIntjobParamsList = new ArrayList<>();
		JsonObject metaDataRevIntjobjsonParams = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}
			metaDataRevIntjobjsonParams.addProperty("payloadId", payloadId);
			metaDataRevIntjobjsonParams.addProperty("scenarioName",
					APIConstants.ITC04_PAYLOAD_METADATA_REV_INTG);
			metaDataRevIntjobParamsList
					.add(metaDataRevIntjobjsonParams.toString());

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			itc04PayloadService.create(payloadId, companyCode, docCount,
					checksum, pushType, sourceId, genCheckSum);

			if (!Strings.isNullOrEmpty(checksum)) {
				if (!APIConstants.NOCHECKSUM.equalsIgnoreCase(checksum)) {
					if (!checksum.equalsIgnoreCase(genCheckSum)) {
						String msg = "CheckSum is not matching";
						String errorCode = "ER7777";
						LOGGER.error(errorCode, msg);
						throw new AppException(msg, errorCode);
					}
				}
			}
			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, "Itc04SyncJsonSchema.json");

			if (errorList.isEmpty()) {
				LocalDateTime reqReceivedTime = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				Type listType = new TypeToken<List<Itc04HeaderEntity>>() {
				}.getType();

				JsonArray json = requestObject.get("req").getAsJsonArray();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request Json {}", json);
				}

				List<Itc04HeaderEntity> documents = gsonEwb.fromJson(json,
						listType);
				User user = SecurityContext.getUser();
				documents.forEach(document -> {
					document.setReqReceivedOn(reqReceivedTime);

					LocalDateTime hciRecevedOn = document.getHciReceivedOn();
					if (hciRecevedOn != null) {
						LocalDateTime convertHciRecevedOn = EYDateUtil
								.toUTCDateTimeFromLocal(hciRecevedOn);
						document.setHciReceivedOn(convertHciRecevedOn);
					}
					LocalDate deliveryChallanDate = document
							.getDeliveryChallanaDate();
					String dcfinYear = GenUtil.getFinYear(deliveryChallanDate);
					document.setFyDcDate(dcfinYear);

					LocalDate jwdcDate = document.getJwDeliveryChallanaDate();
					String jwdcFinYear = GenUtil.getFinYear(jwdcDate);
					document.setFyjwDcDate(jwdcFinYear);

					LocalDate invDate = document.getInvDate();
					String invFinYear = GenUtil.getFinYear(invDate);
					document.setFyInvDate(invFinYear);
					document.setCreatedBy(user.getUserPrincipalName());
				});
				Itc04DocSaveRespDto finalResp = itc04DefaultDocSaveService
						.saveItc04Documents(documents);
				//createJobs(finalResp.getJobParamsList());
				//createMetaDataJobs(finalResp.getMetaDataRevIntJobParamsList());
				Integer errorCount = finalResp.getErrors();
				Integer totalCount = finalResp.getTotalRecords();
				List<Itc04DocDetailsSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();

				JsonElement respBody = gson.toJsonTree(docSaveResponse);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				if (errorCount > 0) {
					itc04PayloadService.update(payloadId, APIConstants.PE,
							errorCount, sourceId, totalCount);
				} else {
					itc04PayloadService.update(payloadId, APIConstants.P,
							errorCount, sourceId, totalCount);
				}

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.CREATED);
				return responseEntity;
			} else {
				JsonElement respBody = gson.toJsonTree(errorList);
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("P",
						"Error occured while validating json schema")));
				resp.add("resp", respBody);

				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				// String msg = "Error occured while validating json schema";
				String errorCode = "ER7778";
				String msg = respBody.toString();
				if (msg.length() > 500) {
					msg = msg.substring(0, 500);
				}
				itc04PayloadService.updateError(payloadId, APIConstants.E,
						errorCode, sourceId, msg);
				//createMetaDataJobs(metaDataRevIntjobParamsList);
			}
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
				itc04PayloadService.updateError(payloadId, APIConstants.E,
						errorCode, sourceId, errorMsg);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			//createMetaDataJobs(metaDataRevIntjobParamsList);
			return responseEntity;
			// return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.ITC04_PAYLOAD_METADATA_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private void createJobs(List<String> jobParamsList) {
		if (jobParamsList == null || jobParamsList.isEmpty())
			return;

		// String groupCode = TenantContext.getTenantId();
		String jobCategory = JobConstants.ERROR_ITC04_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : jobParamsList) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

}
