package com.ey.advisory.controller.gstr2;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Anx2InwardErrorHeaderEntity;
import com.ey.advisory.app.data.entities.client.InwardPayloadEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2BReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2ReconResultRespPsdRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.InwardPayloadRepository;
import com.ey.advisory.app.data.services.common.InwardPayloadService;
import com.ey.advisory.app.docs.dto.InwardListingReqDto;
import com.ey.advisory.app.docs.dto.InwardListingResponseDto;
import com.ey.advisory.app.docs.dto.InwardSvErrDocDto;
import com.ey.advisory.app.docs.dto.PageReqDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSvErrSaveRespDto;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocumentDto;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardDocSave240Service;
import com.ey.advisory.app.services.docs.gstr2.InwardDocSaveService;
import com.ey.advisory.app.services.docs.gstr2.InwardDocSvErrCorrectionSaveService;
import com.ey.advisory.app.services.docs.gstr2.InwardLowerVersionValidationService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardDocSearchService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardListingService;
import com.ey.advisory.app.services.search.docsearch.BasicInwardSvErrDocSearchService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
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
public class InwardTransDocController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardTransDocController.class);

	@Autowired
	@Qualifier("DefaultInwardDocSaveService")
	private InwardDocSaveService docService;

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

	@Autowired
	@Qualifier("Gstr2ReconResultRespPsdRepository")
	private Gstr2ReconResultRespPsdRepository gstr2ReconResultRespPsdRepository;

	@Autowired
	@Qualifier("Gstr2BReconResultRespPsdRepository")
	private Gstr2BReconResultRespPsdRepository gstr2BReconResultRespPsdRepository;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	CommonUtility commonUtility;

	@PostMapping(value = "/ui/saveInwardDocUI", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardDocFromUI(
			@RequestBody String jsonString) {

		return saveInwardDoc(jsonString);
	}

	@PostMapping(value = "/api/saveInwardDocERP", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardDocFromERP(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");

		return saveInwardDocERP(payloadId, checksum, docCount, pushType,
				companyCode, jsonString);
	}

	@PostMapping(value = "/api/saveInwardDocERPV2", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardDocLowerVersion(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");

		return saveInwardDocERPLowerVersion(payloadId, checksum, docCount,
				pushType, companyCode, jsonString);
	}

	private ResponseEntity<String> saveInwardDocERPLowerVersion(
			String payloadId, String checksum, String docCount, String pushType,
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

			LocalDateTime reqReceivedTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ReqReceived Time " + reqReceivedTime);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<InwardTransDocument>>() {
			}.getType();

			JsonArray jsonArray = requestObject.get("req").getAsJsonArray();

			inlwValidationService.jsonValidationForDecimal(jsonArray);

			List<InwardTransDocument> documents = gsonEwb.fromJson(jsonArray,
					listType);
			User user = SecurityContext.getUser();
			documents.forEach(document -> {

				inlwValidationService
						.SchemaValidationsForLowerVersion(document);

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
			InwardDocSaveFinalRespDto finalResp = docService
					.saveDocuments(documents, sourceId, payloadId);
//			createJobs(finalResp.getJobParamsList());
			createMetaDataJobs(metaDataRevIntjobParamsList);
			Integer errorCount = finalResp.getErrors();
			Integer totalCount = finalResp.getTotalRecords();
			List<InwardDocSaveRespDto> docSaveResponse = finalResp
					.getSavedDocsResp();
			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
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

		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
			String errorMsg = ex.getMessage();
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

	private ResponseEntity<String> saveInwardDoc(String jsonString) {
		try {
			// Set Req Received Time for Performance Testing
			// LocalDateTime reqReceivedTime = LocalDateTime.now();
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
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
			List<InwardTransDocument> documents = gsonEwb.fromJson(json,
					listType);
			User user = SecurityContext.getUser();
			boolean is240FormatFlag = false;
			for (InwardTransDocument document : documents) {
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
				is240FormatFlag = document.getIs240Format();
			}
			InwardDocSaveFinalRespDto finalResp = null;
			if (!is240FormatFlag) {
				finalResp = docService.saveDocuments(documents, null, null);
			} else {
				finalResp = docSave240Service.saveDocuments(documents, null,
						null);
			}
			List<InwardDocSaveRespDto> docSaveResponse = finalResp
					.getSavedDocsResp();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Entered saveInwardDoc method exception "
					+ ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private ResponseEntity<String> saveInwardDocERP(String payloadId,
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
					.validateInptJson(jsonString, "InwardJsonSchema.json");
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
				InwardDocSaveFinalRespDto finalResp = docService
						.saveDocuments(documents, sourceId, payloadId);
//				createJobs(finalResp.getJobParamsList());
				createMetaDataJobs(metaDataRevIntjobParamsList);
				Integer errorCount = finalResp.getErrors();
				Integer totalCount = finalResp.getTotalRecords();
				List<InwardDocSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();
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

	/**
	 * This method searches for the document based on the input filter
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/inwardDocSearch", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDocuments(@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			// Gson gson = GsonUtil.gsonInstanceWithExpose();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			SearchCriteria dto = gson.fromJson(reqJson, DocSearchReqDto.class);
			DocSearchReqDto reqDto = gson.fromJson(reqJson,
					DocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			Long entityId = reqDto.getEntityId().get(0);
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<InwardDocumentDto> searchResult = inwardDocSearch
					.find(dto, pageRequest, InwardDocumentDto.class);
			for (InwardDocumentDto i : searchResult.getResult()) {

				String docType = i.getDocType();
				if (docType != null && (docType.equalsIgnoreCase("CR")
						|| docType.equalsIgnoreCase("C")
						|| docType.equalsIgnoreCase("RCR")
						|| docType.equalsIgnoreCase("ADJ"))) {
					if (i.getInvoiceOtherCharges() != null) {
						i.setInvoiceOtherCharges(CheckForNegativeValue(
								i.getInvoiceOtherCharges()));
					}
					if (i.getInvoiceAssessableAmount() != null) {
						i.setInvoiceAssessableAmount(CheckForNegativeValue(
								i.getInvoiceAssessableAmount()));
					}
					if (i.getInvoiceIgstAmount() != null) {
						i.setInvoiceIgstAmount(CheckForNegativeValue(
								i.getInvoiceIgstAmount()));
					}
					if (i.getInvoiceCgstAmount() != null) {
						i.setInvoiceCgstAmount(CheckForNegativeValue(
								i.getInvoiceCgstAmount()));
					}
					if (i.getInvoiceSgstAmount() != null) {
						i.setInvoiceSgstAmount(CheckForNegativeValue(
								i.getInvoiceSgstAmount()));
					}
					if (i.getInvoiceCessAdvaloremAmount() != null) {
						i.setInvoiceCessAdvaloremAmount(CheckForNegativeValue(
								i.getInvoiceCessAdvaloremAmount()));
					}
					if (i.getInvoiceCessSpecificAmount() != null) {
						i.setInvoiceCessSpecificAmount(CheckForNegativeValue(
								i.getInvoiceCessSpecificAmount()));
					}
					if (i.getInvoiceCessAdvaloremAmount() != null) {
						i.setInvoiceCessAdvaloremAmount(CheckForNegativeValue(
								i.getInvoiceCessAdvaloremAmount()));
					}
					if (i.getInvoiceCessSpecificAmount() != null) {
						i.setInvoiceCessSpecificAmount(CheckForNegativeValue(
								i.getInvoiceCessSpecificAmount()));
					}
					if (i.getPurchaseVoucherNum() != null) {
						i.setPurchaseVoucherNum(i.getPurchaseVoucherNum());
					}
					if (i.getInvoiceStateCessAmount() != null) {
						i.setInvoiceStateCessAmount(CheckForNegativeValue(
								i.getInvoiceStateCessAmount()));
					}
					if (i.getInvStateCessSpecificAmt() != null) {
						i.setInvStateCessSpecificAmt(CheckForNegativeValue(
								i.getInvStateCessSpecificAmt()));
					}
					if (i.getCgstAmount() != null) {
						i.setCgstAmount(
								CheckForNegativeValue(i.getCgstAmount()));
					}
					if (i.getSgstAmount() != null) {
						i.setSgstAmount(
								CheckForNegativeValue(i.getSgstAmount()));
					}
					if (i.getCessAmountAdvalorem() != null) {
						i.setCessAmountAdvalorem(CheckForNegativeValue(
								i.getCessAmountAdvalorem()));
					}
					if (i.getCessAmountSpecific() != null) {
						i.setCessAmountSpecific(CheckForNegativeValue(
								i.getCessAmountSpecific()));
					}
				}
				isDocLocked(i);

				boolean isDuplicateCheck = false;
				String ansfromques = "B";
				ansfromques = commonUtility.getAnsFromQue(entityId,
						"Whether duplication check to be enabled for inward file processing ?");
				if (ansfromques.equalsIgnoreCase("A")) {
					isDuplicateCheck = true;
				} else if (ansfromques.equalsIgnoreCase("B")) {
					isDuplicateCheck = false;
				}
				i.setDuplicateCheck(isDuplicateCheck);
			}
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error(
					"Entered getDocuments method exception " + ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	/**
	 * This method is responsible for Saving Structurally Validated Error
	 * Document from UI
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/saveInwardSvErrDoc", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveInwardSvErrDoc(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered saveInwardSvErrDoc method");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<Anx2InwardErrorHeaderEntity>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<Anx2InwardErrorHeaderEntity> documents = gson.fromJson(json,
					listType);
			List<InwardDocSvErrSaveRespDto> saveResp = inwardSvErrDocSaveSvc
					.saveSvErrDocuments(documents);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(saveResp);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
		} catch (Exception ex) {
			LOGGER.error("Entered saveInwardSvErrDoc method exception "
					+ ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	/**
	 * This method searches for structurally validated error documents based on
	 * the input filter
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/inwardSvErrDocSearch", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInwardSvErrDocuments(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			// Gson gson = GsonUtil.gsonInstanceWithExpose();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			SearchCriteria dto = gson.fromJson(reqJson, DocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<InwardSvErrDocDto> searchResult = inwardSvErrDocSearch
					.find(dto, pageRequest, InwardSvErrDocDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Entered getInwardSvErrDocuments method exception "
					+ ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	@RequestMapping(value = "/ui/inwardListingApi", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInwardListing(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			// Gson gson = GsonUtil.gsonInstanceWithExpose();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			/*
			 * SearchCriteria dto = gson.fromJson(reqJson,
			 * InwardInvoiceFilterListingReqDto.class);
			 */
			SearchCriteria dto = gson.fromJson(reqJson,
					InwardListingReqDto.class);
			
			
			InwardListingReqDto req = gson.fromJson(reqJson, InwardListingReqDto.class);


			List<String> accVoucherNos = req.getAccVoucherNum(); // Assuming req.getAccVoucherNum() provides the list
			if (accVoucherNos != null) {
			    // Join non-null elements into a comma-separated string
			    String accVoucherNoString = accVoucherNos.stream()
			                                             .filter(Objects::nonNull) // Exclude null elements
			                                             .collect(Collectors.joining(","));

			    // Check if the length exceeds 2000
			    if (accVoucherNoString.length() > 2000) {
			        throw new AppException(
			                "Accounting Voucher Numbers have exceeded the limit of 2000 characters");
			    }
			} else {
			    // Handle the case where accVoucherNos is null if needed
			    LOGGER.warn("Accounting Voucher Numbers list is null");
			}

			
			List<String> docNumbers = req.getDocNums(); // Assuming reqDto.getDocNums() provides the list
			if (docNumbers != null) {
			    // Join non-null elements into a comma-separated string
			    String docNumberString = docNumbers.stream()
			                                       .filter(Objects::nonNull) // Exclude null elements
			                                       .collect(Collectors.joining(","));

			    if (LOGGER.isDebugEnabled()) {
			        LOGGER.debug("docNumberString length before: " + docNumberString.length());
			    }

			    // Check if the length exceeds 2000
			    if (docNumberString.length() > 2000) {
			        if (LOGGER.isDebugEnabled()) {
			            LOGGER.debug("docNumberString length: " + docNumberString.length());
			        }

			        // Prepare the response
			        APIRespDto respDto = new APIRespDto("Failed",
			                "Document Numbers have exceeded the limit of 2000 characters");
			        JsonObject response = new JsonObject();
			        JsonElement respBody = gson.toJsonTree(respDto);
			        String msg = "Document Numbers have exceeded the limit of 2000 characters";
			        response.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			        response.add("resp", respBody);

			        // Log the error message
			        LOGGER.error(msg);

			        // Return the response
			        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			    }
			} else {
			    // Handle the case where docNumbers is null if needed
			    LOGGER.warn("Document Numbers list is null");
			}

			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<InwardListingResponseDto> searchResult = basicInwardListingService
					.findList(dto, pageRequest, InwardListingResponseDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error(
					"Entered getDocuments method exception " + ex.getMessage());
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				/*return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));*/
				
				return new BigDecimal(
					    ((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0
					        ? "-" + value.toString()
					        : value.toString()
					);
			}
		}
		return null;
	}

	private String ChangeForNegativeValue(Object value) {
		if (value != null) {
			
			if (value instanceof BigDecimal) {
			    return (((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
			            ? "-" + value.toString()
			            : value.toString();
			} else if (value instanceof Integer) {
			    return (((Integer) value) > 0)
			            ? "-" + value.toString()
			            : value.toString();
			} else if (value instanceof Long) {
			    return (((Long) value) > 0)
			            ? "-" + value.toString()
			            : value.toString();
			} else if (value instanceof BigInteger) {
			    return (((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
			            ? "-" + value.toString()
			            : value.toString();
			}
			
			/*if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			}*/
			return "-" + value.toString();
		}
		return null;
	}

	public void isDocLocked(InwardDocumentDto i) {
		try {
			String invoiceKey = DefaultInwardDocSave240Service
					.reconDocKey2aprgeneration(i);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("invoiceKey isDocLocked {}", invoiceKey);
			}
			String islocked = gstr2ReconResultRespPsdRepository
					.islocked(invoiceKey);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("islocked isDocLocked {}", islocked);
			}
			if (islocked == null) {
				invoiceKey = DefaultInwardDocSave240Service
						.reconDocKeygeneration(i);
				islocked = gstr2BReconResultRespPsdRepository
						.islocked(invoiceKey);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("islocked isDocLocked {}", islocked);
			}
			if (islocked != null) {
				i.setLocked(true);
				i.setLockedReason("User can not edit any value for this record "
						+ "as it is currently locked under recon response."
						+ "Kindly unlock the record to edit any data.");
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error while checking record is locked/not condition for docKey{}",
					i.getDocKey(), e);
		}
	}
}
