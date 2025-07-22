package com.ey.advisory.controller.qrcodevalidator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRCodeFileDetailsEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusDTO;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusUIDTO;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRCodeFileDetailsRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorService;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.data.services.qrcodevalidator.QRGstinDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRSearchParams;
import com.ey.advisory.app.data.services.qrcodevalidator.QrUploadStatsDto;
import com.ey.advisory.app.data.services.qrvspdf.QrvsPdfValidatorFinalRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@RestController
@Slf4j
public class QRValidatorController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	QRResponseSummaryRepo qrRespSummRepo;

	@Autowired
	QRPDFResponseSummaryRepo qrPDFRespSummRepo;

	@Autowired
	QRCodeFileDetailsRepo qrFileDtlsRepo;

	@Autowired
	@Qualifier("QRPDFJSONResponseSummaryRepo")
	QRPDFJSONResponseSummaryRepo qrPdfJSONRespSummRepo;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	QRCommonUtility qrcommonUtility;

	private static final String FAILED = "Failed";

	@PostMapping(value = "/ui/uploadQRCodeFile", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadQRCodeFileUI(
			@RequestParam("file") MultipartFile inputfile,
			@RequestParam("entityId") String entityId) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		File tempDir = Files.createTempDir();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE UploadQRCodeFile");
			}

			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());

			String optionOpted = qrcommonUtility
					.optedOption(Long.valueOf(entityId));
			Long id = retrieveFileId(inputfile, "WEBUPLOAD", optionOpted, Long.parseLong(entityId));

			String groupCode = TenantContext.getTenantId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("fileType", extension);
			jobParams.addProperty("entityId", entityId);
			jobParams.addProperty("optedAns", optionOpted);
			jobParams.addProperty("uploadType", "webUpload");

			asyncJobsService.createJob(groupCode,
					JobConstants.QR_CODE_VALIDATOR, jobParams.toString(),
					userName, 1L, null, null);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Exception while Uploading the File";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	@PostMapping(value = "/api/uploadQRCodeFile", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> uploadQRCodeFileApi(
			@RequestParam("file") MultipartFile inputfile) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_START", "QRValidatorController",
				"uploadQRCodeFileApi", "");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		File tempDir = Files.createTempDir();
		JsonObject resp = new JsonObject();
		try {
			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());
			if (Strings.isNullOrEmpty(extension)) {
				String msg = String
						.format("Invalid Request, File upload is mandatory.");
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_SAVEFILEDTLS_START", "QRValidatorController",
					"uploadQRCodeFileApi", "");
			Long id = retrieveFileId(inputfile, "APICALL", "A", null);
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_SAVEFILEDTLS_END", "QRValidatorController",
					"uploadQRCodeFileApi", "");
			qrCodeService.saveAndPersistQRReports(id, extension, "A", "apiUpload" , null);
			
			Optional<QRResponseSummaryEntity> retList = qrRespSummRepo
					.retrieveById(id);
			LOGGER.debug("RetList is: {} ",retList);
			if (retList.isPresent()) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				qrCodeService
						.setMatchandMisMatchCount(Arrays.asList(retList.get()));
				resp.add("resp", gson.toJsonTree(retList.get()));
			}
			else {
				String msg = String.format("No Data Available for File Id %s ",
						id);
				LOGGER.error(msg);
				QRResponseSummaryEntity errDto = new QRResponseSummaryEntity();
				errDto.setErrMsg("No data available for uploaded file");
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(errDto));
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Uploading the File";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			QRResponseSummaryEntity errDto = new QRResponseSummaryEntity();
			errDto.setErrMsg(ex.getMessage());
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(errDto));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	@PostMapping(value = "/api/uploadQRPdfReconFile", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> uploadQRPdfReconFile(
			@RequestParam("file") MultipartFile inputfile) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_START", "QRValidatorController",
				"uploadQRCodeFileApi", "");
		File tempDir = Files.createTempDir();
		try {
			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());
			if (Strings.isNullOrEmpty(extension)
					|| !"pdf".equalsIgnoreCase(extension)) {
				String msg = String.format("Invalid File/File is missing.");
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			QRUploadFileStatusEntity entity = new QRUploadFileStatusEntity();
			entity.setCreatedBy("API");
			entity.setUploadedBy("API");
			entity.setDateOfUpload(LocalDate.now());
			entity.setFileName(inputfile.getOriginalFilename());
			entity.setFileStatus(QRCodeValidatorConstants.UPLOADED);
			entity.setFilePath(inputfile.getOriginalFilename());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setSource("APICALL");
			entity.setIsReverseInt(false);
			entity.setOptionOpted("B");
			entity = qrUploadFileStatusRepo.save(entity);
			Long id = entity.getId();

			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_SAVEFILEDTLS_START", "QRValidatorController",
					"uploadQRCodeFileApi", "");
			
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ inputfile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			inputfile.transferTo(tempFile);
			PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
					"QRCODEAPI_SAVEFILEDTLS_END", "QRValidatorController",
					"uploadQRCodeFileApi", "");
			JsonObject resp = qrCodeService.reconQrAndPdf(tempFile, "QRVSPDF",
					id , "apiUpload" , null);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Exception while Uploading the File.";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			QrvsPdfValidatorFinalRespDto errDto = new QrvsPdfValidatorFinalRespDto();
			errDto.setErrMsg(ex.getMessage());
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(errDto));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private Long retrieveFileId(MultipartFile inputfile, String source,
			String optionOpted, Long entityId) {
		Pair<String, String> uploadedDocDetails = new Pair<String, String>("",
				"");
		String uploadedDocName = null;
		File tempDir = Files.createTempDir();
		QRUploadFileStatusEntity entity = new QRUploadFileStatusEntity();
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE UploadQRCodeFile");
			}
			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());
			if ("APICALL".equalsIgnoreCase(source)) {
				entity.setCreatedBy("API");
				entity.setUploadedBy("API");
				if (extension.equalsIgnoreCase(QRCodeValidatorConstants.PDF)) {
					uploadedDocDetails = QRCommonUtility.uploadFile(inputfile,
							tempDir, extension);
					uploadedDocName = uploadedDocDetails.getValue0();
					LOGGER.debug("Uploaded FileName {}", uploadedDocName);
				} else {
					String msg = String
							.format("Uploaded file extension not supported");
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			} else {
				entity.setUploadedBy(userName);
				entity.setCreatedBy(userName);
				entity.setEntityId(entityId);
				if (extension.equalsIgnoreCase(QRCodeValidatorConstants.PDF)
						|| extension.equalsIgnoreCase(
								QRCodeValidatorConstants.ZIP)) {
					uploadedDocDetails = QRCommonUtility.uploadFile(inputfile,
							tempDir, extension);
					uploadedDocName = uploadedDocDetails.getValue0();
				} else {
					String msg = String.format(
							"Uploaded file extension not supported",
							inputfile.getOriginalFilename());
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			entity.setDateOfUpload(LocalDate.now());
			entity.setFileName(inputfile.getOriginalFilename());
			entity.setFileStatus(QRCodeValidatorConstants.UPLOADED);
			entity.setFilePath(uploadedDocName);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setSource(source);
			entity.setIsReverseInt(false);
			entity.setDocId(uploadedDocDetails.getValue1());
			entity.setOptionOpted(optionOpted);
			entity = qrUploadFileStatusRepo.save(entity);
			LOGGER.debug("RetrieveFileId Method {}", entity);
			Long id = entity.getId();
			return id;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(ex.getMessage());
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	@PostMapping(value = "/ui/generateReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateQRReport(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		Workbook workBook = null;
		try {
			JsonObject respObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Long fileId = respObject.get("fileId").getAsLong();
			Optional<QRUploadFileStatusEntity> fileDetails = qrUploadFileStatusRepo
					.findById(fileId);
			String optionOpted = null;
			if (fileDetails.isPresent()) {
				optionOpted = Strings
						.isNullOrEmpty(fileDetails.get().getOptionOpted()) ? "A"
								: fileDetails.get().getOptionOpted();
			}
			if (optionOpted != null) {
			
			if (optionOpted.equalsIgnoreCase("A")) {
				List<QRResponseSummaryEntity> retList = qrRespSummRepo
						.findByFileId(fileId);
				workBook = qrCodeService.generateQrReport(retList, null, null);
			} else if (optionOpted.equalsIgnoreCase("B")) {
				List<QRPDFResponseSummaryEntity> retList = qrPDFRespSummRepo
						.findByFileId(fileId);
				workBook = qrCodeService.generateQrReport(null, retList, null);
			} else if (optionOpted.equalsIgnoreCase("C")) {
				List<QRPDFJSONResponseSummaryEntity> retList = qrPdfJSONRespSummRepo
						.findByFileId(fileId);
				workBook = qrCodeService.generateQrReport(null, null, retList);
			} else {
				LOGGER.debug("Invalid Option Selected {} for File ID {}",
						optionOpted, fileId);
			}
			}
			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.QRVALIDATESUMMARY);
			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generating report for QR Code";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the response buffer";
				LOGGER.error(errMsg, ex);

			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/generateErroredReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateErroredQRReport(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		try {
			JsonObject respObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Long fileId = respObject.get("fileId").getAsLong();
			List<QRCodeFileDetailsEntity> retList = qrFileDtlsRepo
					.retrieveFileNameById(Long.valueOf(fileId));
			if (retList.isEmpty()) {
				Optional<QRUploadFileStatusEntity> getPdfFileName = qrUploadFileStatusRepo
						.findById(Long.valueOf(fileId));
				if (getPdfFileName.isPresent()) {
					int i = getPdfFileName.get().getFileName().lastIndexOf('.');
					if (i > 0) {
						if (getPdfFileName.get().getFileName().substring(i + 1)
								.equalsIgnoreCase("pdf")) {
							QRCodeFileDetailsEntity data = new QRCodeFileDetailsEntity();
							data.setFileName(
									getPdfFileName.get().getFileName());
							data.setCreatedOn(
									getPdfFileName.get().getCreatedOn());
							retList.add(data);
						}
					}
				}
			}
			Workbook workBook = qrCodeService.generateErroredQrReport(retList);
			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.QRVALIDATESUMMARY);

			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("ddMMyyyy");
			LocalDateTime createdOn = retList.get(0).getCreatedOn();
			String format = FOMATTER.format(createdOn);
			fileName = "ErroredOutInv" + "_" + fileId + "_" + format;
			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				jsonObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generating report for QR Code";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the response buffer";
				LOGGER.error(errMsg, ex);

			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getUploadStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUploadStatus(HttpServletResponse response,
			HttpServletRequest request,@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		//JsonObject jsonObj = new JsonObject();
		try {
			
			JsonObject requestObject=(new JsonParser()).parse(jsonString).getAsJsonObject();
			JsonObject hdrObject =requestObject.getAsJsonObject("hdr");
			int pageNum =hdrObject.get("pageNum").getAsInt();
			int pageSize=hdrObject.get("pageSize").getAsInt();
			
			JsonObject reqJson=requestObject.getAsJsonObject("req");
			Long entityId =reqJson.get("entityId").getAsLong();
			
			int recordsToStart = pageNum;
			int noOfRowstoFetch = pageSize;
			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			
		//	String entityIdApi = request.getParameter("entityId");
			
			Pair<List<QRUploadFileStatusDTO>, Integer> ListOffileStatusData = qrCodeService
					.fetchQrUploadData(entityId,pageReq);
			String flagAnswer=ListOffileStatusData.getValue0().stream()
		            .map(QRUploadFileStatusDTO::getFlagAnswer)
		            .filter(Objects::nonNull)
		            .distinct()
		            .collect(Collectors.joining("/"));
			QRUploadFileStatusUIDTO responseDto=new QRUploadFileStatusUIDTO();
			responseDto.setData(ListOffileStatusData.getValue0());
			responseDto.setFeature(flagAnswer);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					ListOffileStatusData.getValue1(), pageNum, pageSize, "S",
					"Successfully fetched QRFileStatusData records")));
			resp.add("resp", gson.toJsonTree(responseDto));
			LOGGER.debug("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching the Data", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getViewSummDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getViewSummDetails(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Long fileId = reqObject.get("fileId").getAsLong();
			Optional<QRUploadFileStatusEntity> fileDetails = qrUploadFileStatusRepo
					.findById(fileId);
			String optionOpted = null;
			if (fileDetails.isPresent()) {
				optionOpted = Strings
						.isNullOrEmpty(fileDetails.get().getOptionOpted()) ? "A"
								: fileDetails.get().getOptionOpted();
			}

			QrUploadStatsDto uploadStats = null;

			if ("A".equalsIgnoreCase(optionOpted)) {
				List<QRResponseSummaryEntity> responseSummaryList = qrRespSummRepo
						.findByFileId(fileId);
				uploadStats = qrCodeService.getStatsDetails(responseSummaryList,
						null, null);

			} else if ("B".equalsIgnoreCase(optionOpted)) {
				List<QRPDFResponseSummaryEntity> responseSummaryList = qrPDFRespSummRepo
						.findByFileId(fileId);
				uploadStats = qrCodeService.getStatsDetails(null,
						responseSummaryList, null);

			} else if ("C".equalsIgnoreCase(optionOpted)) {
				List<QRPDFJSONResponseSummaryEntity> responseSummaryList = qrPdfJSONRespSummRepo
						.findByFileId(fileId);
				uploadStats = qrCodeService.getStatsDetails(null, null,
						responseSummaryList);
			} else {
				LOGGER.debug("Invalid Option Selected {} for File ID {}",
						optionOpted, fileId);
			}

			dataObj.add("data", gson.toJsonTree(uploadStats));
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
					optionOpted);
			resp.add("resp", dataObj);

			LOGGER.debug("response {}", uploadStats);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the Data for View Summary",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getViewTableSummDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getViewTableSummDetails(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			QrUploadStatsDto uploadStats = new QrUploadStatsDto();

			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			Long fileId = reqObject.get("fileId").getAsLong();

			Optional<QRUploadFileStatusEntity> fileDetails = qrUploadFileStatusRepo
					.findById(fileId);
			String optionOpted = null;
			if (fileDetails.isPresent()) {
				optionOpted = Strings
						.isNullOrEmpty(fileDetails.get().getOptionOpted()) ? "A"
								: fileDetails.get().getOptionOpted();
			}

			if ("A".equalsIgnoreCase(optionOpted)) {
				Pair<List<QRResponseSummaryEntity>, Integer> viewFilterList = qrCodeService
						.getViewFilterLisDtls(fileId, pageSize, pageNum);
				uploadStats.setQrSummaryData(viewFilterList.getValue0());
				qrCodeService
						.setMatchandMisMatchCount(viewFilterList.getValue0());
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
						viewFilterList.getValue1(), pageNum, pageSize, "S",
						"Successfully fetched Vendor Master Processed records")));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if ("B".equalsIgnoreCase(optionOpted)) {
				Pair<List<QRPDFResponseSummaryEntity>, Integer> viewFilterList = qrCodeService
						.getqrPdfViewFilterLisDtls(fileId, pageSize, pageNum);
				uploadStats.setQrpdfSummaryData(viewFilterList.getValue0());
				// qrCodeService
				// .setMatchandMisMatchCount(viewFilterList.getValue0());
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
						viewFilterList.getValue1(), pageNum, pageSize, "S",
						"Successfully fetched QR records")));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if ("C".equalsIgnoreCase(optionOpted)) {
				Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> viewFilterList = qrCodeService
						.getqrPdfJsonViewFilterLisDtls(fileId, pageSize,
								pageNum);
				uploadStats.setQrpdfJsonSummaryData(viewFilterList.getValue0());
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
						viewFilterList.getValue1(), pageNum, pageSize, "S",
						"Successfully fetched QR records")));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				LOGGER.debug("Invalid Option Selected {} for File ID {} ",
						optionOpted, fileId);
				JsonObject resp = new JsonObject();
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the Data for View Summary",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getConsolidatedData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getConsolidatedData(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getConsolidatedData Request received from UI as {} ",
						jsonString);
			}
			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String entityId = reqJson.get("entityId").getAsString();
			String optionOpted = qrcommonUtility
					.optedOption(Long.valueOf(entityId));
			QRSearchParams qrSearchParams = gson.fromJson(reqJson,
					QRSearchParams.class);

			if ("A".equalsIgnoreCase(optionOpted)) {
				List<QRResponseSummaryEntity> filteredPair = qrCodeService
						.getConsFilterList(qrSearchParams);
				QrUploadStatsDto uploadStats = qrCodeService
						.getConsStatsDetails(filteredPair, null, null,
								qrSearchParams);
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if ("B".equalsIgnoreCase(optionOpted)) {
				List<QRPDFResponseSummaryEntity> filteredPair = qrCodeService
						.getConsFilterListqrPDF(qrSearchParams);
				QrUploadStatsDto uploadStats = qrCodeService
						.getConsStatsDetails(null, filteredPair, null,
								qrSearchParams);
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if ("C".equalsIgnoreCase(optionOpted)) {
				List<QRPDFJSONResponseSummaryEntity> filteredPair = qrCodeService
						.getConsFilterListqrPDFJson(qrSearchParams);
				QrUploadStatsDto uploadStats = qrCodeService
						.getConsStatsDetails(null, null, filteredPair,
								qrSearchParams);
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				JsonObject hdrObject = resp.getAsJsonObject("hdr");
				hdrObject.addProperty("optionOpted", optionOpted);
				resp.add("hdr", hdrObject);

				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else {
				LOGGER.debug("Invalid Option Selected {}", optionOpted);
				JsonObject resp = new JsonObject();
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while fetching the Data for Consolidated Summary",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/ui/getConsolidatedTableData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getConsTableData(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getConsolidatedData Request received from UI as {} ",
						jsonString);
			}
			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject hdrObject = reqObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String entityId = reqJson.get("entityId").getAsString();
			String optionOpted = qrcommonUtility
					.optedOption(Long.valueOf(entityId));
			QRSearchParams qrSearchParams = gson.fromJson(reqJson,
					QRSearchParams.class);
			if ("A".equalsIgnoreCase(optionOpted)) {
				Pair<List<QRResponseSummaryEntity>, Integer> filteredPair = qrCodeService
						.getConsFilterTableList(qrSearchParams, pageSize,
								pageNum);
				QrUploadStatsDto uploadStats = new QrUploadStatsDto();
				uploadStats.setQrSummaryData(filteredPair.getValue0());
				qrCodeService
						.setMatchandMisMatchCount(filteredPair.getValue0());

				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(
								filteredPair.getValue1(), pageNum, pageSize,
								"S",
								"Successfully fetched QR Validator Records")));
				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			} else if ("B".equalsIgnoreCase(optionOpted)) {

				Pair<List<QRPDFResponseSummaryEntity>, Integer> filteredPair = qrCodeService
						.getConsFilterTableListqrPdf(qrSearchParams, pageSize,
								pageNum);
				QrUploadStatsDto uploadStats = new QrUploadStatsDto();
				uploadStats.setQrpdfSummaryData(filteredPair.getValue0());
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(
								filteredPair.getValue1(), pageNum, pageSize,
								"S",
								"Successfully fetched QR Validator Records")));

				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			} else {
				Pair<List<QRPDFJSONResponseSummaryEntity>, Integer> filteredPair = qrCodeService
						.getConsFilterTableListqrPdfJson(qrSearchParams,
								pageSize, pageNum);
				QrUploadStatsDto uploadStats = new QrUploadStatsDto();
				uploadStats.setQrpdfJsonSummaryData(filteredPair.getValue0());
				dataObj.add("data", gson.toJsonTree(uploadStats));
				JsonObject resp = new JsonObject();
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp(
								filteredPair.getValue1(), pageNum, pageSize,
								"S",
								"Successfully fetched QR Validator Records")));

				resp.get("hdr").getAsJsonObject().addProperty("optionOpted",
						optionOpted);
				resp.add("resp", dataObj);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while fetching the Data for Consolidated Summary",
					ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@PostMapping(value = "/ui/generateConsReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateConsReport(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		Workbook workBook = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generateConsReport Request received from UI as {} ",
						jsonString);
			}
			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String entityId = reqJson.get("entityId").getAsString();
			QRSearchParams qrSearchParams = gson.fromJson(reqJson,
					QRSearchParams.class);
			String optionOpted = qrcommonUtility
					.optedOption(Long.valueOf(entityId));

			if (optionOpted.equalsIgnoreCase("A")) {
				List<QRResponseSummaryEntity> finalList = qrCodeService
						.getConsFilterList(qrSearchParams);
				workBook = qrCodeService.generateQrReport(finalList, null,
						null);
			} else if (optionOpted.equalsIgnoreCase("B")) {
				List<QRPDFResponseSummaryEntity> finalList = qrCodeService
						.getConsFilterListqrPDF(qrSearchParams);
				workBook = qrCodeService.generateQrReport(null, finalList,
						null);
			} else if (optionOpted.equalsIgnoreCase("C")) {
				List<QRPDFJSONResponseSummaryEntity> finalList = qrCodeService
						.getConsFilterListqrPDFJson(qrSearchParams);
				workBook = qrCodeService.generateQrReport(null, null,
						finalList);
			} else {
				LOGGER.debug("Invalid Option Selected {}", optionOpted);
			}
			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.QRVALIDATESUMMARY);
			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
				return new ResponseEntity<>(null, HttpStatus.OK);
			} else {
				dataObj.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				dataObj.addProperty("resp", "No Data Available");
				return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
			}

		} catch (Exception ex) {
			dataObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			dataObj.addProperty("resp", ex.getMessage());
			String msg = "Error occured while generateConsReport ";
			LOGGER.error(msg, ex);
			try {
				response.flushBuffer();
			} catch (IOException e) {
				String errMsg = "Exception occurred while flushing the response buffer";
				LOGGER.error(errMsg, ex);
			}
			return new ResponseEntity<>(dataObj.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getQRRecipientGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getQRRecipientGstin(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("entityId") String entityId) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject dataObj = new JsonObject();
		try {
			List<QRGstinDto> recipientGstinList = qrCodeService
					.getListOfRecipientGstin(entityId);
			dataObj.add("data", gson.toJsonTree(recipientGstinList));
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", dataObj);
			LOGGER.debug("response {}", recipientGstinList);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getFileDetailsbyId", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFileDetails(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "fileId", required = true) String fileId) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			List<QRCodeFileDetailsEntity> fileDtls = qrFileDtlsRepo
					.retrieveFileNameById(Long.valueOf(fileId));

			if (!fileDtls.isEmpty()) {
				String jsonEINV = gson.toJson(fileDtls);
				JsonElement einvJsonElement = JsonParser.parseString(jsonEINV);
				JsonObject jsonObject = new JsonObject();
				jsonObject.add("fileNames", einvJsonElement);
				JsonObject resps = new JsonObject();
				JsonElement respBody = gson.toJsonTree(jsonObject);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				QRGstinDto qrGstin = new QRGstinDto();
				qrGstin.setErrMsg("No Data found");
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(qrGstin));
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/ui/getQRVendorPan", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getQRVendorPan(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("entityId") String entityId) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			List<QRGstinDto> vendorPanList = qrCodeService
					.getListOfQRVendorPan(entityId);
			if (!CollectionUtils.isEmpty(vendorPanList)) {
				String jsonEINV = gson.toJson(vendorPanList);
				JsonElement einvJsonElement = JsonParser.parseString(jsonEINV);
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
		} catch (Exception ex) {
			LOGGER.error("Message", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getQRVendorGstin", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorGstin(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray vendorPans = new JsonArray();
		Gson googleJson = new Gson();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String entityId = requestObject.get("entityId").getAsString();
			vendorPans = requestObject.getAsJsonArray("vendorPan");
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listOfVendorPans = googleJson.fromJson(vendorPans,
					listType);
			List<QRGstinDto> vendorGstinList = qrCodeService
					.getListOfVendorGstin(listOfVendorPans, entityId);

			if (!CollectionUtils.isEmpty(vendorGstinList)) {
				String jsonEINV = gson.toJson(vendorGstinList);
				JsonElement einvJsonElement = JsonParser.parseString(jsonEINV);
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
			String msg = "Unexpected error while fetching the QR Vendor Gstin list";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
