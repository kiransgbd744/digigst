package com.ey.advisory.controller.pdf.reader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.pdfreader.PDFUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.pdfreader.PDFUploadFileStatusRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRCodeFileDetailsRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.services.pdfreader.PDFReaderServiceImpl;
import com.ey.advisory.app.data.services.pdfreader.dto.PDFUploadFileStatusDto;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
public class PDFReaderController {

	@Autowired
	AsyncJobsService asyncJobsService;

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
	@Qualifier("PDFReaderServiceImpl")
	PDFReaderServiceImpl pdfReadService;

	@Autowired
	PDFUploadFileStatusRepo pdfUploadFileStatusRepo;

	@Autowired
	PDFResponseSummaryRepo pdfResponseSummaryRepo;

	@Autowired
	QRCommonUtility qrcommonUtility;

	private static final String FAILED = "Failed";

	@PostMapping(value = "/ui/uploadPdfReaderFile", produces = {
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
				LOGGER.debug("INSIDE uploadPdfFile");
			}

			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());

			if (Strings.isNullOrEmpty(extension)) {
				String msg = String.format("Invalid File/File is missing.");
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			if (!("pdf".equalsIgnoreCase(extension)
					|| "zip".equalsIgnoreCase(extension))) {
				String msg = String.format("File is not pdf and zip");
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			Long id = retrieveFileId(inputfile, "WEBUPLOAD", entityId);

			String groupCode = TenantContext.getTenantId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("fileType", extension);
			jobParams.addProperty("entityId", entityId);

			
			asyncJobsService.createJob(groupCode,
					JobConstants.PDF_UPLOAD_READER, jobParams.toString(),
					userName, 1L, null, null);
			

			//pdfReadService.saveAndPersistPdfSummary(id, extension);

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

	@GetMapping(value = "/ui/getPdfUploadStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUploadStatus(HttpServletResponse response,
			HttpServletRequest request) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		try {
			String entityId = request.getParameter("entityId");
			List<PDFUploadFileStatusDto> fileStatusData = null;

			if (!Strings.isNullOrEmpty(entityId)) {
				fileStatusData = pdfReadService.fetchPdfUploadData(entityId);
			}

			jsonObj.add("data", gson.toJsonTree(fileStatusData));
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObj);
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

	@PostMapping(value = "/ui/generatePdfReaderReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateQRReport(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObj = new JsonObject();
		Workbook workBook = null;
		try {

			// pdf.reader.summary.report.header
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE generatePdfReaderReport");
			}
			JsonObject respObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Long fileId = respObject.get("fileId").getAsLong();
			Optional<PDFUploadFileStatusEntity> fileDetails = pdfUploadFileStatusRepo
					.findById(fileId);
			List<PDFResponseSummaryEntity> pdfReaderList = pdfResponseSummaryRepo
					.findByFileId(fileId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Generating Pdf Reader Report for file ID {}",
						fileId);
			}

			workBook = pdfReadService.generatePdfReaderReport(pdfReaderList);

			String fileName = DocumentUtility
					.getUniqueFileName(ConfigConstants.PDF_READER_SUMMARY);
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

	private Long retrieveFileId(MultipartFile inputfile, String source,
			String strEntityId) {

		Pair<String, String> uploadedDocDetails = new Pair<String, String>("",
				"");
		String uploadedDocName = null;
		long entityId = Long.parseLong(strEntityId);

		File tempDir = Files.createTempDir();
		PDFUploadFileStatusEntity entity = new PDFUploadFileStatusEntity();
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE UploadPDFFile");
			}
			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());

			entity.setUploadedBy(userName);
			entity.setCreatedBy(userName);
			if (extension.equalsIgnoreCase(QRCodeValidatorConstants.PDF)
					|| extension
							.equalsIgnoreCase(QRCodeValidatorConstants.ZIP)) {
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
			entity.setEntityId(entityId);
			entity.setDateOfUpload(LocalDate.now());
			entity.setFileName(inputfile.getOriginalFilename());
			entity.setFileStatus(QRCodeValidatorConstants.UPLOADED);
			entity.setFilePath(uploadedDocName);
			entity.setCreatedOn(LocalDateTime.now());
			entity.setSource(source);
			entity.setDocId(uploadedDocDetails.getValue1());
			entity = pdfUploadFileStatusRepo.save(entity);
			Long id = entity.getId();
			return id;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(ex.getMessage());
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

}
