package com.ey.advisory.controllers.vendorcommunication;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationService;
import com.ey.advisory.app.data.services.noncomplaintvendor.OverAllFilingStatusWithFinancialYearInfo;
import com.ey.advisory.app.data.services.noncomplaintvendor.OverallFilingStatusDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorGstinDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterReportService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadDaoImpl;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntityDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh NK
 *
 */
@RestController
@Slf4j
public class VendorMasterController {

	private static final String FINANCIAL_YEAR = "financialYear";
	private static final String REPORT_TYPE = "reportType";
	private static final String PARAMETER_FINANCIAL_YEAR_S_AND_REPORT_TYPE_IS_S = "Parameter financialYear %s:: and reportType is %s::";
	private static final String NON_VENDOR_COMMUNICATION_REPORT = "nonVendorCommunicationReport() ";
	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_OCTET_STREAM = "APPLICATION/OCTET-STREAM";
	private static final String ENTITY_ID = "entityId";
	private static final String VENDOR_GSTIN_LIST_NOT_FOUND = "Vendor gstin list not found";
	private static final String VENDOR_GSTIN = "vendorGstin";
	private static final String RECIPIENT_PAN = "recipientPan";
	private static final String FAILED = "Failed";

	@Autowired
	private VendorMasterUploadDaoImpl vendorMasterUploadDaoImpl;

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Autowired
	@Qualifier("VendorMasterReportServiceImpl")
	private VendorMasterReportService masterReportService;

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationService nonComplaintVendorCommunicationServiceImpl;

	@Autowired
	@Qualifier("ReturnDataStorageStatusRepository")
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	private FileStatusDownloadReportRepository repo;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;
	
	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@PostMapping(value = "/ui/getVendorMasterData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorMasterData(
			@RequestBody String jsonString) {

		JsonObject errorResp = new JsonObject();
		JsonArray vendorGstins = new JsonArray();
		JsonArray recipientPan = new JsonArray();

		Gson googleJson = new Gson();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<String> vendorGstinsList = null;
		List<String> recipientPanList = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorMasterController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(RECIPIENT_PAN)
					&& reqObject.getAsJsonArray(RECIPIENT_PAN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("recipientPan PAN are provided in request");
				}
				recipientPan = reqObject.getAsJsonArray(RECIPIENT_PAN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				recipientPanList = googleJson.fromJson(recipientPan, listType);
			}

			if (reqObject.has(VENDOR_GSTIN)
					&& reqObject.getAsJsonArray(VENDOR_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Vendor gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				vendorGstins = reqObject.getAsJsonArray(VENDOR_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstinsList = googleJson.fromJson(vendorGstins, listType);
			}

			if (recipientPanList == null && vendorGstinsList == null) {

				Long entityId = reqObject.get(ENTITY_ID).getAsLong();

				recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				if (recipientPanList.isEmpty() || recipientPanList == null) {

					String msg = "No Data found";
					JsonObject resp = new JsonObject();
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.creatErrorResp()));
					resp.add("resp", gson.toJsonTree(msg));
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
				}

			}

			Pair<List<VendorMasterUploadEntityDto>, Integer> masterUploadResponsePair = vendorMasterUploadDaoImpl
					.getReconResult(recipientPanList, vendorGstinsList,
							pageSize, pageNum);

			String responseData = gson
					.toJson(masterUploadResponsePair.getValue0());

			JsonElement jsonElement = new JsonParser().parse(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("ewbMasterData", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					masterUploadResponsePair.getValue1(), pageNum, pageSize,
					"S",
					"Successfully fetched Vendor Master Processed records")));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping(value = "/ui/getVendorMasterErrorReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void vendorInformationDownload(@RequestBody String jsonString,
			HttpServletResponse response) throws IOException {

		JsonObject errorResp = new JsonObject();
		Long fileId = null;
		String refId = null;
		Workbook workbook = null;

		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			fileId = request.has("fileId") ? request.get("fileId").getAsLong()
					: null;
			refId = request.has("refId") ? request.get("refId").getAsString()
					: null;
			String typeOfRecord = request.get("flagofRecord").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"reconSummary() " + "Parameter batchId %s:: ", fileId);
				LOGGER.debug(msg);
			}

			workbook = masterReportService.getVendorMasterErrorReport(fileId,
					refId, typeOfRecord);
			LocalDateTime now = LocalDateTime.now();
			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
			timeMilli = timeMilli.replace(".", "");
			timeMilli = timeMilli.replace("-", "");
			timeMilli = timeMilli.replace(":", "");
			if (workbook != null) {
				response.setContentType(APPLICATION_OCTET_STREAM);
				if (typeOfRecord.equalsIgnoreCase("errorrecords")) {
					response.setHeader(CONTENT_DISPOSITION,
							String.format(ATTACHMENT_FILENAME
									+ "VendorMasterErrorReport" + "_"
									+ timeMilli + ".xlsx"));
				} else {
					response.setHeader(CONTENT_DISPOSITION,
							String.format(ATTACHMENT_FILENAME
									+ "VendorMasterInformationReport" + "_"
									+ timeMilli + ".xlsx"));

				}
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}

		} catch (Exception ex) {
			String msg = "Error occured while generating VendorMasterErrorReport ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
	}

	@PostMapping(value = "/ui/getRecipientPanInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientPanInfoList(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();

		List<String> recipientPanList = null;
		LOGGER.debug(
				"The selected criteria for fetching recipient gstin list is : {}",
				requestObject.get("req"));
		try {
			Long entityId = json.get(ENTITY_ID).getAsLong();

			recipientPanList = entityInfoRepository.findPanByEntityId(entityId);

			List<GstinDto> listOfRecipientPan = masterReportService
					.getListOfRecipientPan(recipientPanList);

			if (!CollectionUtils.isEmpty(listOfRecipientPan)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(listOfRecipientPan);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorGstInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorGstInfoList(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();
		LOGGER.debug(
				"The selected criteria for fetching vendor gstin list is : {}",
				requestObject.get("req"));
		try {
			Long entityId = json.get(ENTITY_ID).getAsLong();
			List<VendorGstinDto> gstr1FileStatusEntities = masterReportService
					.getListOfvendorGstinList(entityId);
			if (!CollectionUtils.isEmpty(gstr1FileStatusEntities)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(gstr1FileStatusEntities);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	/**
	 * @author Malatesh
	 * @param listOfRecipientGstIn
	 *            is required to fetch only those vendor pan which are related
	 *            to selected RecipientGstin
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/ui/getVendorPanInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorPanInfoList(
			@RequestBody String jsonString) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray recipientGstin = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfRecipientGstIn;
			recipientGstin = requestObject.getAsJsonArray(RECIPIENT_PAN);
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfRecipientGstIn = googleJson.fromJson(recipientGstin,
					listType);

			List<String> gstr1FileStatusEntities = masterReportService
					.getListOfvendorGstinList(listOfRecipientGstIn, true);
			if (!CollectionUtils.isEmpty(gstr1FileStatusEntities)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(gstr1FileStatusEntities);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorNameInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorName(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray recipientGstin = new JsonArray();
		JsonArray vendorPanArray = new JsonArray();
		JsonArray vendorGstinArray = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfRecipientGstIn;
			List<String> listOfVendorGstIn;
			List<String> listOfVendorPan;
			recipientGstin = requestObject.getAsJsonArray(RECIPIENT_PAN);
			vendorPanArray = requestObject.getAsJsonArray("vendorPan");
			vendorGstinArray = requestObject.getAsJsonArray("vendorGstIn");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfRecipientGstIn = googleJson.fromJson(recipientGstin,
					listType);
			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);
			listOfVendorPan = googleJson.fromJson(vendorPanArray, listType);

			List<String> gstr1FileStatusEntities = masterReportService
					.getListOfvendorName(listOfRecipientGstIn, listOfVendorPan,
							listOfVendorGstIn);
			if (!CollectionUtils.isEmpty(gstr1FileStatusEntities)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(gstr1FileStatusEntities);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/initateGetVendorFilingStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String financialYear = request.get(FINANCIAL_YEAR).getAsString();
			String complianceType = request.get("complianceType").getAsString();
			Long entityId = null;
			if (request.has("entityId")) {
				entityId = request.get("entityId").getAsLong();
			}
			List<String> vendorGstinList = new ArrayList<>();
			List<String> vendorPanList = new ArrayList<>();
			if (request.has("vendorGstins")) {
				JsonArray vendorGstinArray = request
						.getAsJsonArray("vendorGstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				vendorGstinList = gson.fromJson(vendorGstinArray, listType);
			}
			if (request.has("vendorPans")) {
				JsonArray vendorPanArray = request
						.getAsJsonArray("vendorPans");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				vendorPanList = gson.fromJson(vendorPanArray, listType);
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("insithe the method getVendorsReturnFileStatus "
						+ " of controller VendorMasterController ");

			String userName = SecurityContext.getUser().getUserPrincipalName();
			ReturnDataStorageStatusEntity returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findByFinancialYear(financialYear);

			if (returnDataStorageStatusEntity == null) {
				returnDataStorageStatusEntity = new ReturnDataStorageStatusEntity(
						financialYear, "SUBMITTED", userName, userName);
				returnDataStorageStatusRepo.save(returnDataStorageStatusEntity);
			} else {
				if (returnDataStorageStatusEntity.getStatus()
						.equalsIgnoreCase("SUBMITTED")
						|| returnDataStorageStatusEntity.getStatus()
								.equalsIgnoreCase("InProgress")) {
					String errorMessage = String.format(
							"Generation of Non Compliant Vendor report for"
									+ " FY %s is in progress.",
							financialYear);
					LOGGER.error(errorMessage);
					throw new AppException(errorMessage);
				}
			}
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			
			Long requestId = returnDataStorageStatusEntity.getId();
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("requestId", requestId);
			jsonParams.addProperty(FINANCIAL_YEAR, financialYear);
			jsonParams.addProperty("complianceType", complianceType);
			if (!vendorGstinList.isEmpty())
				jsonParams.addProperty("vendorGstins",
						vendorGstinList.toString());
			if (!vendorPanList.isEmpty())
				jsonParams.addProperty("vendorPans",
						vendorPanList.toString());
			if (entityId != null)
				jsonParams.addProperty("entityId", entityId);
			
			entity.setReqPayload(jsonParams.toString());
			entity = repo.save(entity);
			
			JsonObject params = new JsonObject();
			params.addProperty("id", entity.getId());
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.InitiateGetVendorFilingStatus,
					params.toString(), userName, 5L, 0L, 0L);
			LocalDateTime istCreatedDate = EYDateUtil.toISTDateTimeFromUTC(
					returnDataStorageStatusEntity.getModifiedOn());

			String formatDateTime = istCreatedDate.format(format);

			JsonObject resps = new JsonObject();
			resps.addProperty("initiatedOn", formatDateTime);
			resps.addProperty("status",
					returnDataStorageStatusEntity.getStatus());
			JsonElement respBody = gson.toJsonTree(resps);
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating vendor filing status: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "/ui/getNonVendorReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> nonVendorCommunicationReport(
			@RequestBody String jsonString)
			throws IOException {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject jobParams = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside downloadVendorRatingAsyncReport"
								+ " method in VendorComplianceRatingController "
								+ "with request: %s", request);
				LOGGER.debug(msg);
			}
			String financialYear = request.get(FINANCIAL_YEAR).getAsString();
			String reportType = request.get(REPORT_TYPE).getAsString();
			Long entityId = request.get(ENTITY_ID).getAsLong();			
			User user = SecurityContext.getUser();
			String requiredReportType = "";
			if(reportType.equalsIgnoreCase("All")){
				requiredReportType = "All Vendor";
			} else if(reportType.equalsIgnoreCase("Compliant")){
				requiredReportType = "Compliance Vendor";
			} else if(reportType.equalsIgnoreCase("Non-Compliant")){
				requiredReportType = "Non-Compliance Vendor";
			}
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setCreatedBy(userName);
			entity.setEntityId(entityId);
			entity.setReportCateg(APIConstants.Compliance_Report);
			entity.setReportType(requiredReportType);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setDataType(APIConstants.VENDOR_COMPLIANCE);
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);
			jobParams.addProperty("financialYear", financialYear);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.VENDOR_COMPLIANCE_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while generating async report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getVendorRatingTableReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorRatingTableReport(
			@RequestBody String jsonString)
			throws IOException {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject jobParams = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonObject request = requestObject.getAsJsonObject("req");
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside downloadVendorRatingAsyncReport"
								+ " method in VendorComplianceRatingController "
								+ "with request: %s", request);
				LOGGER.debug(msg);
			}
			String financialYear = request.get("fy").getAsString();
			Long entityId = request.get(ENTITY_ID).getAsLong();	
			String returnType = request.get("returnType").getAsString();
			String source = request.get("source").getAsString();
			User user = SecurityContext.getUser();
			String reportType = "";
			if(source.equalsIgnoreCase("vendor")){
				reportType = "Vendor Compliance Table Report";
			} else if(source.equalsIgnoreCase("customer")){
				reportType = "Customer Compliance Table Report";
			} else {
				reportType = "My Compliance Table Report";
			}
			String userName = user.getUserPrincipalName() != null
					? user.getUserPrincipalName() : "SYSTEM";
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setCreatedBy(userName);
			entity.setEntityId(entityId);
			entity.setReportCateg("GSTR1,GSTR3B");
			entity.setReportType(reportType);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setDataType("All");
			entity.setReqPayload(jsonString);
			entity = downloadRepository.save(entity);
			Long id = entity.getId();
			jobParams.addProperty("id", id);
			jobParams.addProperty("reportType", reportType);
			asyncJobsService.createJob(TenantContext.getTenantId(),
					JobConstants.COMPLIANCE_TABLE_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto("Failed", ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while generating async report";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "/ui/getNonVendorTableReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> nonVendorCommunicationTableReport(
			@RequestBody String jsonString, HttpServletResponse response)
			throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String financialYear = null;
		String returnType = null;
		String reportType = null;
		Workbook workbook = null;

		try {
			JsonObject request = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			financialYear = request.get(FINANCIAL_YEAR).getAsString();
			returnType = request.get("returnType").getAsString();
			reportType = request.get(REPORT_TYPE).getAsString();
			Long entityId = request.get(ENTITY_ID).getAsLong();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						NON_VENDOR_COMMUNICATION_REPORT
								+ "Parameter financialYear %s:: ",
						financialYear);
				LOGGER.debug(msg);
			}

			workbook = nonComplaintVendorCommunicationServiceImpl
					.getNonComplaintVendorTableReport(financialYear, returnType,
							reportType, entityId);

			LocalDateTime now = LocalDateTime.now();
			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
			timeMilli = timeMilli.replace(".", "");
			timeMilli = timeMilli.replace("-", "");
			timeMilli = timeMilli.replace(":", "");

			if (workbook != null) {
				response.setContentType(APPLICATION_OCTET_STREAM);
				if (reportType.equalsIgnoreCase("all")) {
					response.setHeader(CONTENT_DISPOSITION,
							ATTACHMENT_FILENAME + "Vendors_Compliance_"
									+ "Report_" + timeMilli + ".xlsx");
				} else {
					response.setHeader(CONTENT_DISPOSITION,
							ATTACHMENT_FILENAME + reportType
									+ "_Vendors_Report_" + timeMilli + ".xlsx");
				}
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto(FAILED, ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while geneting file";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/ui/getVendorReturnFilingData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorReturnsFilingData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String financialYear = null;
		String returnType = null;
		String reportType = null;
		JsonArray vendorPanArray = new JsonArray();
		JsonArray vendorGstinArray = new JsonArray();

		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject request = requestObject.getAsJsonObject("req");
			financialYear = request.get(FINANCIAL_YEAR).getAsString();
			returnType = request.get("returnType").getAsString();
			reportType = request.get(REPORT_TYPE).getAsString();
			Long entityId = request.get(ENTITY_ID).getAsLong();
			List<String> listOfVendorGstIn;
			List<String> listOfVendorPan;
			vendorPanArray = request.getAsJsonArray("vendorPan");
			vendorGstinArray = request.getAsJsonArray("vendorGstin");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);
			listOfVendorPan = googleJson.fromJson(vendorPanArray, listType);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						NON_VENDOR_COMMUNICATION_REPORT
								+ "Parameter financialYear %s:: ",
						financialYear);
				LOGGER.debug(msg);
			}

			OverAllFilingStatusWithFinancialYearInfo overallFilingStatusDtos = nonComplaintVendorCommunicationServiceImpl
					.getOverallReturnFilingStatus(financialYear, returnType,
							reportType, entityId, listOfVendorPan,
							listOfVendorGstIn);

			List<OverallFilingStatusDto> overallFilingStatusDtosList = overallFilingStatusDtos
					.getOverallFilingStatusDtos();
			List<List<OverallFilingStatusDto>> paginatedList = null;
			int totalCount = 0;
			if (overallFilingStatusDtosList != null
					&& !overallFilingStatusDtosList.isEmpty()) {
				Collections.sort(overallFilingStatusDtosList,
						new Comparator<OverallFilingStatusDto>() {
							public int compare(OverallFilingStatusDto o1,
									OverallFilingStatusDto o2) {
								return (o1.getVendorPan()
										.compareTo(o2.getVendorPan()));
							}
						});
				paginatedList = Lists.partition(overallFilingStatusDtosList,
						pageSize);
				totalCount = overallFilingStatusDtosList.size();
				int pages = 0;
				if (totalCount % pageSize == 0) {
					pages = totalCount / pageSize;
				} else {
					pages = totalCount / pageSize + 1;
				}
				List<OverallFilingStatusDto> requiredList = null;
				if (pageNum <= pages) {
					requiredList = paginatedList.get(pageNum);
				} else {
					throw new AppException("Invalid Page Number");
				}
				overallFilingStatusDtos
						.setOverallFilingStatusDtos(requiredList);
			}

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(overallFilingStatusDtos);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					totalCount, pageNum, pageSize, "S",
					"Successfully fetched Non-Compliant Vendor records")));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			APIRespDto dto = new APIRespDto(FAILED, ex.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the return files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}

	}

	@PostMapping(value = "/ui/getRecipientGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientGstin(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstanceWithDateFmt();

		LOGGER.debug(
				"The selected criteria for fetching recipient gstin list is : {}",
				requestObject.get("req"));
		try {
			Long entityId = json.get(ENTITY_ID).getAsLong();

			List<GstinDto> listOfRecipientGstin = masterReportService
					.getListOfRecipientGstin(entityId);

			if (!CollectionUtils.isEmpty(listOfRecipientGstin)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(listOfRecipientGstin);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Recipient Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getVendorPanDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorPanDetailsForVendorComm(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			Long entityId = json.get(ENTITY_ID).getAsLong();

			List<GstinDto> listOfVendorPans = masterReportService
					.getListOfVendorPans(entityId);

			if (!CollectionUtils.isEmpty(listOfVendorPans)) {
				String jsonEINV = gson.toJson(listOfVendorPans);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorPans", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor PAN list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getVendorPan", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorPanForVendorComm(
			@RequestBody String jsonString) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray recipientGstin = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfRecipientGstIn;
			recipientGstin = requestObject.getAsJsonArray("vendorGStin");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfRecipientGstIn = googleJson.fromJson(recipientGstin,
					listType);

			List<VendorGstinDto> vendorGstinDtoList = masterReportService
					.getListOfvendorPan(listOfRecipientGstIn, true);

			if (!CollectionUtils.isEmpty(vendorGstinDtoList)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(vendorGstinDtoList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching vendor pan";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorPans = new JsonArray();
		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorPans = requestObject.getAsJsonArray("vendorPan");
			List<String> listOfVendorPans;
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfVendorPans = googleJson.fromJson(vendorPans, listType);
			List<GstinDto> vendorGstinList = masterReportService
					.getListOfVendorGstin(listOfVendorPans, entityId);
			if (!CollectionUtils.isEmpty(vendorGstinList)) {
				String jsonEINV = gson.toJson(vendorGstinList);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorGstins", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorName", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorNameForVendorComm(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorGstinArray = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfVendorGstIn;
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorGstinArray = requestObject.getAsJsonArray("vendorGstIn");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);

			List<VendorGstinDto> vendorGstinDtoList = masterReportService
					.getvendorNameForGstin(listOfVendorGstIn, entityId);

			if (!CollectionUtils.isEmpty(vendorGstinDtoList)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(vendorGstinDtoList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching Vendor name";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorCode", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorCode(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorNameArray = new JsonArray();
		JsonArray vendorGstinArray = new JsonArray();
		Gson googleJson = new Gson();
		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			List<String> listOfVendorGstIn;
			List<String> listOfvendorName;
			List<VendorGstinDto> vendorGstinDtoList = new ArrayList<>();
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorNameArray = requestObject.getAsJsonArray("vendorName");
			vendorGstinArray = requestObject.getAsJsonArray("vendorGstIn");

			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();

			listOfvendorName = googleJson.fromJson(vendorNameArray, listType);
			listOfVendorGstIn = googleJson.fromJson(vendorGstinArray, listType);

			List<String> vendorCodeList = masterReportService
					.getListOfvendorCode(listOfvendorName, listOfVendorGstIn,
							entityId);

			vendorCodeList.forEach(eachObje -> {
				VendorGstinDto vendorGstinDto = new VendorGstinDto();
				vendorGstinDto.setVendorCode(eachObje);
				vendorGstinDtoList.add(vendorGstinDto);
			});

			if (!CollectionUtils.isEmpty(vendorGstinDtoList)) {

				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(vendorGstinDtoList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			}
			throw new AppException(VENDOR_GSTIN_LIST_NOT_FOUND);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while vendor code";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getVendorMasterReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getVendorMasterReport(@RequestBody String jsonString,
			HttpServletResponse response) throws IOException {

		JsonObject errorResp = new JsonObject();
		Workbook workbook = null;
		JsonArray vendorGstins = new JsonArray();
		JsonArray recipientPan = new JsonArray();

		Gson googleJson = new Gson();
		List<String> vendorGstinsList = null;
		List<String> recipientPanList = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorMasterController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			if (reqObject.has(RECIPIENT_PAN)
					&& reqObject.getAsJsonArray(RECIPIENT_PAN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("recipientPan PAN are provided in request");
				}
				recipientPan = reqObject.getAsJsonArray(RECIPIENT_PAN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				recipientPanList = googleJson.fromJson(recipientPan, listType);
			}

			if (reqObject.has(VENDOR_GSTIN)
					&& reqObject.getAsJsonArray(VENDOR_GSTIN).size() > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As Vendor gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
				vendorGstins = reqObject.getAsJsonArray(VENDOR_GSTIN);
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();
				vendorGstinsList = googleJson.fromJson(vendorGstins, listType);
			}

			if (recipientPanList == null && vendorGstinsList == null) {

				Long entityId = reqObject.get(ENTITY_ID).getAsLong();

				recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);
				if (recipientPanList.isEmpty() || recipientPanList == null) {

					String msg = "No Data found";
					throw new AppException(msg);
				}

			}

			workbook = vendorMasterUploadDaoImpl
					.downloadReconResult(recipientPanList, vendorGstinsList);

			if (workbook != null) {
				response.setContentType(APPLICATION_OCTET_STREAM);

				response.setHeader(CONTENT_DISPOSITION, String
						.format(ATTACHMENT_FILENAME + "VendorMasterData.xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
		} catch (Exception ex) {
			String msg = "Error occured while generating VendorMasterReport ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));

		}

	}

	@SuppressWarnings("unused")
	@PostMapping(value = "/ui/getNonCompVendorPan", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompVendorPan(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			Long entityId = json.get(ENTITY_ID).getAsLong();

			if (entityId == null)
				throw new AppException("Entity Id cannot be null");

			List<GstinDto> listOfVendorPans = masterReportService
					.getListOfNonComplaintVendorPans(entityId);

			if (!CollectionUtils.isEmpty(listOfVendorPans)) {
				String jsonEINV = gson.toJson(listOfVendorPans);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorPans", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor PAN list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getNonCompalintVGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getNonCompalintVGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorPans = new JsonArray();
		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			Long entityId = requestObject.get("entityId").getAsLong();
			vendorPans = requestObject.getAsJsonArray("vendorPan");
			List<String> listOfVendorPans;
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			listOfVendorPans = googleJson.fromJson(vendorPans, listType);
			if (listOfVendorPans.isEmpty()) {
				throw new AppException("Please select atleast one Vendor PAN");
			}
			List<GstinDto> vendorGstinList = masterReportService
					.getListOfNonComplaintVendorGstin(listOfVendorPans,
							entityId);
			if (!CollectionUtils.isEmpty(vendorGstinList)) {
				String jsonEINV = gson.toJson(vendorGstinList);
				JsonElement einvJsonElement = new JsonParser().parse(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("vendorGstins", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				String msg = "No Data found";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);

			}

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching the Vendor Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

}
