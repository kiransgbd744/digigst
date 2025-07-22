package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asb.service.ASBProducerCommonService;
import com.ey.advisory.app.data.entities.client.OutwardPayloadEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.OutwardPayloadRepository;
import com.ey.advisory.app.docs.dto.EInvoiceDocumentDto;
import com.ey.advisory.app.docs.dto.PageReqDto;
import com.ey.advisory.app.docs.dto.TableNoDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceDocSaveRespDto;
import com.ey.advisory.app.docs.dto.einvoice.EInvoiceOutwardDocSaveRespDto;
import com.ey.advisory.app.services.docs.einvoice.EInvoiceDocSaveService;
import com.ey.advisory.app.services.docs.einvoice.OutwardPayloadService;
import com.ey.advisory.app.services.manageauthtoken.TableNoService;
import com.ey.advisory.app.services.search.docsearch.BasicEInvoiceDocSearchService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.einv.client.ErrorDetailsDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnERPReqDto;
import com.ey.advisory.ewb.dto.CancelEwbReqDto;
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
 * This class is responsible for APIs related to Save Document, Get Error
 * Documents for Correction, Update Corrected Document and other APIs which are
 * required for Error Correction UI screen
 *
 * @author Laxmi.Salukuti
 */
@RestController
@Slf4j
public class EInvoiceDocumentController {

	public static final String success = "Success";
	public static final String failed = "Failed";

	@Autowired
	@Qualifier("EInvoiceDefaultDocSaveService")
	private EInvoiceDocSaveService docService;

	@Autowired
	@Qualifier("BasicEInvoiceDocSearchService")
	private BasicEInvoiceDocSearchService docSearch;

	@Autowired
	@Qualifier("OutwardPayloadServiceImpl")
	private OutwardPayloadService outwardPayloadService;

	@Autowired
	private EinvJsonSchemaValidatorHelper jsonSchemaValidatorHelper;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompanyCodeMappingRepository;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("TableNoServiceImpl")
	TableNoService tableNoService;

	@Autowired
	@Qualifier("OutwardPayloadRepository")
	private OutwardPayloadRepository outwardPayloadRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private ASBProducerCommonService asbProducerService;

	@PostMapping(value = "/ui/einvoicesaveDocUI", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDocumentsFromUI(
			@RequestBody String jsonString) {
		return saveDocumentsForUI(jsonString);
	}

	@PostMapping(value = "/api/saveDocuments", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDocumentsFromERP(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");

		return saveDocuments(payloadId, checksum, docCount, pushType,
				companyCode, null, jsonString, "ConsolidateJsonSchema.json",
				false, null);
	}

	@PostMapping(value = "/api/v2/saveDocuments", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveDocumentsV2(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");
		String headerSourceId = req.getHeader("sourceId");
		String region = req.getHeader("region");

		return saveDocuments(payloadId, checksum, docCount, pushType,
				companyCode, headerSourceId, jsonString,
				"ConsolidateJsonV2Schema.json", true, region);
	}

	@PostMapping(value = "/api/saveCanEwbDocuments", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveCanEwbDocuments(ServletRequest request,
			@RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");
		String headerSourceId = req.getHeader("sourceId");
		String region = req.getHeader("region");

		return saveCanEwbDocuments(payloadId, checksum, docCount, pushType,
				companyCode, headerSourceId, jsonString, region);
	}

	@PostMapping(value = "/api/saveGenEwbbyIrnDocuments", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGenEwbbyIrnDocuments(
			ServletRequest request, @RequestBody String jsonString) {

		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		String checksum = req.getHeader("checksum");
		String docCount = req.getHeader("docCount");
		String pushType = req.getHeader("pushType");
		String companyCode = req.getHeader("companyCode");
		String headerSourceId = req.getHeader("sourceId");
		String region = req.getHeader("region");

		return saveGenEwbbyIrnDocuments(payloadId, checksum, docCount, pushType,
				companyCode, headerSourceId, jsonString, region);
	}

	/**
	 * This method persists Document header and Document line item from SCI
	 * 
	 * @method saveDocuments
	 * @param jsonString
	 * @return ResponseEntity
	 */
	private ResponseEntity<String> saveDocumentsForUI(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			LocalDateTime reqReceivedTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<OutwardTransDocument>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}
			List<OutwardTransDocument> documents = gsonEwb.fromJson(json,
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
			List<AsyncExecJob> jobList = new ArrayList<>();
			GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
					GstnApi.class);
			CommonContext.setDelinkingFlagContext(gstnApi
					.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
			EInvoiceOutwardDocSaveRespDto finalResp = docService
					.saveDocuments(documents, null, null, jobList);

			LOGGER.debug("Einvoice and  Ewb JobList : {} ", jobList.size());

			if (!jobList.isEmpty()) {
				LOGGER.debug("Inside einv or ewb job ");
				persistenceMngr.createJobs(jobList);
			}
			List<EInvoiceDocSaveRespDto> docSaveResponse = finalResp
					.getSavedDocsResp();

			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			LOGGER.error("saveOutwardDoc method exception ", ex);
			return InputValidationUtil.createJsonErrResponse(ex);
		} finally {
			CommonContext.clearContext();
		}
	}

	private ResponseEntity<String> saveDocuments(String payloadId,
			String checksum, String docCount, String pushType,
			String companyCode, String headerSourceId, String jsonString,
			String schemaName, boolean isBCApi, String region) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		List<String> revIntjobParamsList = new ArrayList<>();
		JsonObject revIntjobjsonParams = new JsonObject();
		boolean isRevIntgReq = true;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			if (!Strings.isNullOrEmpty(checksum)
					&& !APIConstants.NOCHECKSUM.equalsIgnoreCase(checksum)
					&& !checksum.equalsIgnoreCase(genCheckSum)) {
				String msg = "CheckSum is not matching";
				String errorCode = "ER7777";
				LOGGER.error(errorCode, msg);
				throw new AppException(msg, errorCode);
			}

			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			String sourceId = null;
			if (!sourceIds.isEmpty()) {
				sourceId = headerSourceId != null ? headerSourceId
						: String.valueOf(sourceIds.get(0));
			}
			Optional<OutwardPayloadEntity> isRecordPres = outwardPayloadRepository
					.findByCloudCheckSumAndStatus(genCheckSum, APIConstants.IP);
			if (isRecordPres.isPresent()) {
				outwardPayloadService.create(payloadId, companyCode, docCount,
						checksum, pushType, genCheckSum, sourceId,
						APIConstants.DUP);
				isRevIntgReq = false;
				String errMsg = "Record is already in InProgress.";
				LOGGER.error(errMsg);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("P", errMsg)));
				responseEntity = new ResponseEntity<>(resp.toString(),
						HttpStatus.OK);
				return responseEntity;
			}
			revIntjobjsonParams.addProperty("payloadId", payloadId);
			revIntjobjsonParams.addProperty("sourceId", sourceId);
			revIntjobjsonParams.addProperty("scenarioName",
					isBCApi ? APIConstants.BC_API_PAYLOAD_REV_INT
							: APIConstants.OUTWARD_PAYLOAD_METADATA_REV_INTG);
			revIntjobParamsList.add(revIntjobjsonParams.toString());

			outwardPayloadService.create(payloadId, companyCode, docCount,
					checksum, pushType, genCheckSum, sourceId, APIConstants.IP);

			List<ErrorDetailsDto> errorList = jsonSchemaValidatorHelper
					.validateInptJson(jsonString, schemaName);

			if (errorList.isEmpty()) {
				LocalDateTime reqReceivedTime = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				JsonObject requestObject = (new JsonParser()).parse(jsonString)
						.getAsJsonObject();

				Type listType = new TypeToken<List<OutwardTransDocument>>() {
				}.getType();

				JsonArray json = requestObject.get("req").getAsJsonArray();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Request Json {}", json);
				}

				List<OutwardTransDocument> documents = gsonEwb.fromJson(json,
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

				List<AsyncExecJob> jobList = new ArrayList<>();
				GstnApi gstnApi = StaticContextHolder.getBean("GstnApi",
						GstnApi.class);
				CommonContext.setDelinkingFlagContext(gstnApi
						.isDelinkingEligible(APIConstants.GSTR1.toUpperCase()));
				EInvoiceOutwardDocSaveRespDto finalResp = docService
						.saveDocuments(documents, sourceId, payloadId, jobList);
				LOGGER.debug("Einvoice and  Ewb JobList : {} ", jobList.size());
				if (!jobList.isEmpty()) {
					persistenceMngr.createJobs(jobList);
				}
				if (!isBCApi) {
					createMetaDataJobs(revIntjobParamsList);
				} else {
					createBCAPIJobs(revIntjobParamsList);
				}
				Integer errorCount = finalResp.getErrors();
				Integer totalCount = finalResp.getTotalRecords();
				List<EInvoiceDocSaveRespDto> docSaveResponse = finalResp
						.getSavedDocsResp();

				if (!isBCApi) {
					asbProducerService.produceMessage("SAP_SR_DOC2",
							"DTLDSAPSRDOC2", "DigiSAP",
							TenantContext.getTenantId(), null, payloadId);
					asbProducerService.produceMessage("SAP_SR_ERROR2",
							"DTLDSAPSRERROR2", "DigiSAP",
							TenantContext.getTenantId(), null, payloadId);

				}
				JsonElement respBody = gson.toJsonTree(docSaveResponse);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
				if (errorCount > 0) {
					outwardPayloadService.update(payloadId, APIConstants.PE,
							errorCount, totalCount);
				} else {
					outwardPayloadService.update(payloadId, APIConstants.P,
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
				String errorCode = "ER7778";
				String msg = respBody.toString();
				if (msg.length() > 500) {
					msg = msg.substring(0, 500);
				}
				outwardPayloadService.updateError(payloadId, APIConstants.E,
						errorCode, msg);
				if (!isBCApi) {
					createMetaDataJobs(revIntjobParamsList);
				} else {
					createBCAPIJobs(revIntjobParamsList);
				}
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
				outwardPayloadService.updateErrorExc(payloadId, APIConstants.E,
						errorCode.length() > 500 ? errorCode.substring(0, 500)
								: errorCode,
						errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
								: errorMsg,
						9);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server", e);
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			if (!isBCApi && isRevIntgReq) {
				createMetaDataJobs(revIntjobParamsList);
			}
			return responseEntity;
		} finally {
			CommonContext.clearContext();
		}
	}

	private ResponseEntity<String> saveCanEwbDocuments(String payloadId,
			String checksum, String docCount, String pushType,
			String companyCode, String headerSourceId, String jsonString,
			String region) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		List<String> bcApiRevIntjobParamsList = new ArrayList<>();
		JsonObject bcApiRevIntjobjsonParams = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			String sourceId = null;
			if (sourceIds != null && !sourceIds.isEmpty()) {
				sourceId = headerSourceId != null ? headerSourceId
						: String.valueOf(sourceIds.get(0));
			}

			bcApiRevIntjobjsonParams.addProperty("payloadId", payloadId);
			bcApiRevIntjobjsonParams.addProperty("sourceId", sourceId);
			bcApiRevIntjobjsonParams.addProperty("scenarioName",
					APIConstants.BC_API_PAYLOAD_REV_INT);
			bcApiRevIntjobParamsList.add(bcApiRevIntjobjsonParams.toString());

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			outwardPayloadService.create(payloadId, companyCode, docCount,
					checksum, pushType, genCheckSum, sourceId, APIConstants.IP);

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

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<CancelEwbReqDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}

			List<CancelEwbReqDto> documents = gsonEwb.fromJson(json, listType);

			LOGGER.debug("Document Payload {}", documents);

			EInvoiceOutwardDocSaveRespDto finalResp = docService
					.saveCanEwbDocuments(documents, sourceId, payloadId);

			Integer errorCount = finalResp.getErrors();
			Integer totalCount = finalResp.getTotalRecords();
			List<EInvoiceDocSaveRespDto> docSaveResponse = finalResp
					.getSavedDocsResp();
			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (errorCount > 0) {
				outwardPayloadService.update(payloadId, APIConstants.PE,
						errorCount, totalCount);
			} else {
				outwardPayloadService.update(payloadId, APIConstants.P,
						errorCount, totalCount);
			}

			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.CREATED);
			createBCAPIJobs(bcApiRevIntjobParamsList);
			// callBCAPI(resp, success, payloadId, TenantContext.getTenantId(),
			// region);
			return responseEntity;

		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode()
					: "ER8888";
			String errorMsg = ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			try {
				outwardPayloadService.updateError(payloadId, APIConstants.E,
						errorCode, errorMsg);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			// callBCAPI(resp, success, payloadId, TenantContext.getTenantId(),
			// region);
			return responseEntity;
		}
	}

	private ResponseEntity<String> saveGenEwbbyIrnDocuments(String payloadId,
			String checksum, String docCount, String pushType,
			String companyCode, String headerSourceId, String jsonString,
			String region) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		ResponseEntity<String> responseEntity = null;
		List<String> bcApiRevIntjobParamsList = new ArrayList<>();
		JsonObject bcApiRevIntjobjsonParams = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("jsonString ", jsonString);
			}

			List<Object> sourceIds = erpCompanyCodeMappingRepository
					.findSourceIdByCompanyCode(companyCode);
			String sourceId = null;
			if (sourceIds != null && !sourceIds.isEmpty()) {
				sourceId = headerSourceId != null ? headerSourceId
						: String.valueOf(sourceIds.get(0));
			}

			bcApiRevIntjobjsonParams.addProperty("payloadId", payloadId);
			bcApiRevIntjobjsonParams.addProperty("sourceId", sourceId);
			bcApiRevIntjobjsonParams.addProperty("scenarioName",
					APIConstants.BC_API_PAYLOAD_REV_INT);
			bcApiRevIntjobParamsList.add(bcApiRevIntjobjsonParams.toString());

			byte[] hashVal = Hashing.sha1()
					.hashString(jsonString, StandardCharsets.UTF_8).asBytes();
			String genCheckSum = Base64.getEncoder().encodeToString(hashVal);

			outwardPayloadService.create(payloadId, companyCode, docCount,
					checksum, pushType, genCheckSum, sourceId, APIConstants.IP);

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

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<GenerateEWBByIrnERPReqDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}

			List<GenerateEWBByIrnERPReqDto> documents = gsonEwb.fromJson(json,
					listType);

			EInvoiceOutwardDocSaveRespDto finalResp = docService
					.saveGenEwbIrnDocuments(documents, payloadId);

			Integer errorCount = finalResp.getErrors();
			Integer totalCount = finalResp.getTotalRecords();
			List<EInvoiceDocSaveRespDto> docSaveResponse = finalResp
					.getSavedDocsResp();
			JsonElement respBody = gson.toJsonTree(docSaveResponse);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (errorCount > 0) {
				outwardPayloadService.update(payloadId, APIConstants.PE,
						errorCount, totalCount);
			} else {
				outwardPayloadService.update(payloadId, APIConstants.P,
						errorCount, totalCount);
			}

			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.CREATED);
			createBCAPIJobs(bcApiRevIntjobParamsList);
			// callBCAPI(resp, success, payloadId, TenantContext.getTenantId(),
			// region);
			return responseEntity;

		} catch (Exception ex) {
			// We reserve 2 error codes for special cases
			// ER8888 - System Excetion
			// ER8887 - App Exception without an error code.
			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode()
					: "ER8888";
			String errorMsg = ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			try {
				outwardPayloadService.updateError(payloadId, APIConstants.E,
						errorCode, errorMsg);
			} catch (Exception e) {
				LOGGER.error("Exception while connecting server");
			}
			LOGGER.error("saveOutwardDoc method exception ", ex);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("P", errorMsg)));
			responseEntity = new ResponseEntity<>(resp.toString(),
					HttpStatus.OK);
			// callBCAPI(resp, success, payloadId, TenantContext.getTenantId(),
			// region);

			return responseEntity;
		}
	}

	/*
	 * private void createJobs(List<String> jobParamsList) { if (jobParamsList
	 * == null || jobParamsList.isEmpty()) return;
	 * 
	 * String jobCategory = groupCode.equalsIgnoreCase("sp0005") ?
	 * JobConstants.JSON_ASP_ERR_ERP_PUSH_BACK :
	 * JobConstants.ERROR_DOCS_REV_INTG; String userName =
	 * SecurityContext.getUser().getUserPrincipalName(); for (String jobParams :
	 * jobParamsList) { asyncJobsService.createJob(TenantContext.getTenantId(),
	 * jobCategory, jobParams, userName, JobConstants.PRIORITY,
	 * JobConstants.PARENT_JOB_ID, JobConstants.SCHEDULE_AFTER_IN_MINS); } }
	 */

	@GetMapping(value = "/api/manageStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> manageStatus(ServletRequest request) {
		JsonObject respObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		HttpServletRequest req = (HttpServletRequest) request;
		String payloadId = req.getHeader("payloadId");
		try {
			OutwardPayloadEntity entity = outwardPayloadRepository
					.getOutwardPayload(payloadId);
			if (entity != null) {
				String jsonErrorResponse = entity.getJsonErrorResponse();
				String errorCode = entity.getErrorCode();
				String status = entity.getStatus();
				LocalDateTime createdOnTime = entity.getCreatedOn();
				LocalDateTime currTime = LocalDateTime.now();
				if (!Strings.isNullOrEmpty(status)
						&& status.equalsIgnoreCase(APIConstants.E)) {
					if (!Strings.isNullOrEmpty(jsonErrorResponse)
							&& (jsonErrorResponse
									.contains("GenericJDBCException")
									|| jsonErrorResponse.contains(
											"LockAcquisitionException"))) {
						respObj.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respObj.addProperty("status", APIConstants.E);
					} else if (!Strings.isNullOrEmpty(errorCode)
							&& (errorCode.contains("GenericJDBCException")
									|| errorCode.contains(
											"LockAcquisitionException"))) {
						respObj.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respObj.addProperty("status", APIConstants.E);
					} else {
						respObj.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						respObj.addProperty("status", APIConstants.P);
					}
				} else if (!Strings.isNullOrEmpty(status)
						&& status.equalsIgnoreCase(APIConstants.IP)) {
					Duration duration = Duration.between(createdOnTime,
							currTime);
					long hoursDifference = duration.toHours();
					if (hoursDifference >= 2) {
						respObj.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						respObj.addProperty("status", APIConstants.E);
					}
				} else {
					respObj.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					respObj.addProperty("status", APIConstants.P);
				}
			} else {
				respObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				respObj.addProperty("status", APIConstants.E);
				LOGGER.error(
						"Could not find the Payload Id {} in MetaData Table for Group {} ",
						payloadId, TenantContext.getTenantId());
			}
			return new ResponseEntity<>(respObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error while updating the payload Id %s", payloadId);
			respObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respObj.addProperty("status", APIConstants.E);
			return new ResponseEntity<>(respObj.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void createMetaDataJobs(List<String> metaDataRevIntjobjsonParams) {
		if (metaDataRevIntjobjsonParams == null
				|| metaDataRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.OUTWARD_PAYLOAD_METADATA_REV_INTG;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : metaDataRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	private void createBCAPIJobs(List<String> bcApiRevIntjobjsonParams) {
		if (bcApiRevIntjobjsonParams == null
				|| bcApiRevIntjobjsonParams.isEmpty())
			return;

		String jobCategory = APIConstants.BC_API_PAYLOAD_REV_INT;
		String userName = SecurityContext.getUser().getUserPrincipalName();
		for (String jobParams : bcApiRevIntjobjsonParams) {
			asyncJobsService.createJob(TenantContext.getTenantId(), jobCategory,
					jobParams, userName, JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		}
	}

	/*
	 * This Method Search for the document based on the filter
	 */
	@PostMapping(value = "/ui/eInvoiceDocSearch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEInvoiceDocument(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			String entityId = obj.getAsJsonObject("req")
					.getAsJsonArray("entityId").get(0).getAsString();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			SearchCriteria dto = gson.fromJson(reqJson,
					EInvoiceDocSearchReqDto.class);
			
			EInvoiceDocSearchReqDto req = gson.fromJson(reqJson, EInvoiceDocSearchReqDto.class);

			// Retrieve the docNums field from the DTO
			//List<String> docNums = req.getDocNums();
			
			List<String> docNumbers = req.getDocNums();
			int docNumberLength = 0;

			if (docNumbers != null) {
			    // Filter out null elements and calculate the total length
			    docNumberLength = docNumbers.stream()
			                                .filter(Objects::nonNull) // Exclude null elements
			                                .mapToInt(String::length)
			                                .sum();

			    if (LOGGER.isDebugEnabled()) {
			        LOGGER.debug("docNumberLength before: " + docNumberLength);
			    }
			}

			if (docNumberLength > 2000) {
			    if (LOGGER.isDebugEnabled()) {
			        LOGGER.debug("docNumberLength: " + docNumberLength);
			    }

			    APIRespDto respDto = new APIRespDto("Failed", 
			            "Document Numbers have exceeded the limit of 2000 characters");
			    JsonObject response = new JsonObject();
			    JsonElement respBody = gson.toJsonTree(respDto);
			    String msg = "Document Numbers have exceeded the limit of 2000 characters";
			    response.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			    response.add("resp", respBody);

			    // Log the error message
			    LOGGER.error(msg);
			    return new ResponseEntity<>(response.toString(), HttpStatus.OK);
			}

			
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Hdr Json " + hdrJson);
			}
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<EInvoiceDocumentDto> searchResult = docSearch
					.findNew(dto, pageRequest, EInvoiceDocumentDto.class);
			boolean optedImpl = invLvlSaveOpted(Long.valueOf(entityId));
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.getAsJsonObject("hdr").addProperty("isInvLvlSaveApplicable",
					optedImpl);
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
		} catch (Exception e) {
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error("Exception Occured:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	private boolean invLvlSaveOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"Enablement of save/delete to GSTN option on Invoice management screen");
		return "A".equalsIgnoreCase(optAns) ? true : false;
	}

	@PostMapping(value = "/ui/getDistinctTableNo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getDistinctTableNo(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		try {
			Long docId = null;

			JsonObject obj = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			EInvoiceDocumentDto reqDto = gson.fromJson(obj,
					EInvoiceDocumentDto.class);

			if (reqDto.getId() != null) {
				docId = reqDto.getId();
			}

			TableNoDto tableNumbers = tableNoService.getTableNumbersById(docId);

			List<String> tableNoList = tableNumbers.getTableNoList();

			JsonObject newResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(tableNoList);
			newResp.add("tableNumberList", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", newResp);

		} catch (Exception e) {
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error("Exception Occured:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
