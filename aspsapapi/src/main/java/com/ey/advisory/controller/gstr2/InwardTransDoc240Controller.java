package com.ey.advisory.controller.gstr2;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.InwardPayloadEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.InwardPayloadRepository;
import com.ey.advisory.app.data.services.common.InwardPayloadService;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveRespDto;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardDocSave240Service;
import com.ey.advisory.app.services.docs.gstr2.InwardDocSvErrCorrectionSaveService;
import com.ey.advisory.app.services.docs.gstr2.InwardLowerVersionValidationService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardDocSearchService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardListingService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardSvErrDocSearchService;
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

/**
 * This class is responsible for APIs related to Save Document Get Error
 * Documents for Correction, Update Corrected Document and other APIs which are
 * required for Error Correction UI screen for GSTR2
 * 
 * @author Mohana.Dasari
 *
 */
@RestController
public class InwardTransDoc240Controller {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardTransDoc240Controller.class);

	@Autowired
	@Qualifier("DefaultInwardDocSave240Service")
	private DefaultInwardDocSave240Service docSave240Service;

	@Autowired
	@Qualifier("BasicInwardDocSearchService")
	private BasicInwardDocSearchService inwardDocSearch;

	@Autowired
	@Qualifier("BasicInwardSvErrDocSearchService")
	private BasicInwardSvErrDocSearchService inwardSvErrDocSearch;

	@Autowired
	@Qualifier("DefaultInwardDocSvErrCorrectionSaveService")
	private InwardDocSvErrCorrectionSaveService inwardSvErrDocSaveSvc;

	@Autowired
	@Qualifier("BasicInwardListingService")
	private BasicInwardListingService basicInwardListingService;

	@Autowired
	@Qualifier("InwardPayloadServiceImpl")
	private InwardPayloadService inwardPayloadService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	@Qualifier("InwardLowerVersionValidationService")
	private InwardLowerVersionValidationService inlwValidationService;

	@Autowired
	@Qualifier("InwardPayloadRepository")
	private InwardPayloadRepository inwardPayloadRepository;

	/*@Autowired
	private ASBProducerCommonService asbProducerService;
*/
	
	@PostMapping(value = "/api/saveInwardDocERP240", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardDocFromERP240(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");

		return saveInwardDocERP240(payloadId, checksum, docCount, pushType,
				companyCode, jsonString);
	}

	private ResponseEntity<String> saveInwardDocERP240(String payloadId,
			String checksum, String docCount, String pushType,
			String companyCode, String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = new JsonObject();
		ResponseEntity<String> responseEntity = null;
		List<String> metaDataRevIntjobParamsList = new ArrayList<>();
		JsonObject metaDataRevIntjobjsonParams = new JsonObject();
		boolean isRevIntgReq = true;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			String sourceId = null;
			if (sourceIds != null && !sourceIds.isEmpty()) {
				sourceId = String.valueOf(sourceIds.get(0));
			}
			metaDataRevIntjobjsonParams.addProperty("payloadId", payloadId);
			metaDataRevIntjobjsonParams.addProperty("sourceId", sourceId);
			metaDataRevIntjobjsonParams.addProperty("scenarioName",
					APIConstants.INWARD_PAYLOAD_METADATA_REV_INTG);
			metaDataRevIntjobParamsList
					.add(metaDataRevIntjobjsonParams.toString());

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genChekSum = Base64.getEncoder().encodeToString(hashVal);

			Optional<InwardPayloadEntity> isRecordPres = inwardPayloadRepository
					.findByCloudCheckSumAndStatus(genChekSum, APIConstants.IP);
			if (isRecordPres.isPresent()) {
				isRevIntgReq = false;
				String errMsg = "Record is already in InProgress.";
				inwardPayloadService.create(payloadId, companyCode, docCount,
						checksum, pushType, genChekSum, sourceId,
						APIConstants.DUP);
				LOGGER.error(errMsg);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("P", errMsg)));
				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}
			inwardPayloadService.create(payloadId, companyCode, docCount,
					checksum, pushType, genChekSum, sourceId, APIConstants.IP);

			if (!Strings.isNullOrEmpty(checksum)) {
				if (!APIConstants.NOCHECKSUM.equalsIgnoreCase(checksum)) {
					if (!checksum.equalsIgnoreCase(genChekSum)) {
						String msg = "CheckSum is not matching";

						String errorCode = "ER7777";
						LOGGER.error(errorCode, msg);
						throw new AppException(msg, errorCode);
					}
				}
			}
			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, "InwardJson240Schema.json");
			if (errorList.isEmpty()) {
				LocalDateTime reqReceivedTime = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("ReqReceived Time " + reqReceivedTime);
				}
				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				Type listType = new TypeToken<List<InwardTransDocument>>() {
				}.getType();

				JsonArray json = requestObject.get("req").getAsJsonArray();
				List<InwardTransDocument> documents = gsonEwb.fromJson(json,
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
					LocalDate docDate = document.getDocDate();
					String finYear = GenUtil.getFinYear(docDate);
					document.setFinYear(finYear);
					document.setCreatedBy(user.getUserPrincipalName());
				});
				InwardDocSaveFinalRespDto finalResp = docSave240Service
						.saveDocuments(documents, sourceId, payloadId);
//				createJobs(finalResp.getJobParamsList());
				createMetaDataJobs(metaDataRevIntjobParamsList);
				Integer errorCount = finalResp.getErrors();
				Integer totalCount = finalResp.getTotalRecords();
				List<InwardDocSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();
				
			/*	asbProducerService.produceMessage("SAP_PR_DOC2", "DTLDSAPPRDOC2",
						"DigiSAP", TenantContext.getTenantId(),
						null, payloadId);
				asbProducerService.produceMessage("SAP_PR_ERROR2", "DTLDSAPPRERROR2",
						"DigiSAP", TenantContext.getTenantId(),
						null, payloadId);
				*/
				
				JsonElement respBody = gson.toJsonTree(docSaveResponse);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				if (errorCount > 0) {
					inwardPayloadService.update(payloadId, APIConstants.PE,
							errorCount, totalCount);
				} else {
					inwardPayloadService.update(payloadId, APIConstants.P,
							errorCount, totalCount);
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
				inwardPayloadService.updateError(payloadId, APIConstants.E,
						errorCode, msg);
				createMetaDataJobs(metaDataRevIntjobParamsList);
			}
			return responseEntity;
		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = ex.getMessage();
			if (errorMsg.length() > 500) {
				errorMsg = errorMsg.substring(0, 500);
			}
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			try {
				inwardPayloadService.updateError(payloadId, APIConstants.E,
						errorCode, errorMsg);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("saveInwardDoc method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			if (isRevIntgReq) {
				createMetaDataJobs(metaDataRevIntjobParamsList);
			}
			return responseEntity;
			// return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private void createJobs(List<String> jobParamsList) {
		if (jobParamsList == null || jobParamsList.isEmpty())
			return;

		for (String jobParams : jobParamsList) {
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.ERROR_DOCS_REV_INTG, jobParams,
					APIConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(),
					APIConstants.INWARD_PAYLOAD_METADATA_REV_INTG, jobParams,
					APIConstants.SYSTEM, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}
}
