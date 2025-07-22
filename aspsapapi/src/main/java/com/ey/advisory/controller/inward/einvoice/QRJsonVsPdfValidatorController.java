package com.ey.advisory.controller.inward.einvoice;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRUploadFileStatusEntity;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseLogRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorService;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.inward.einvoice.JsonPdfValidatorFinalRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.QRCodeValidatorConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@RestController
@Slf4j
public class QRJsonVsPdfValidatorController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	QRResponseSummaryRepo qrRespSummRepo;
	
	@Autowired
	QRPDFJSONResponseSummaryRepo qrPDFJSONRespSummRepo;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	private QRResponseLogRepo qrResponseLogRepo;
	
	@Autowired
	QRCommonUtility qrcommonUtility;

	@PostMapping(value = "/api/uploadQRJsonPdfReconFile", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> uploadQRJsonPdfReconFile(
			@RequestParam("file") MultipartFile inputfile) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		PerfUtil.logEventToFile(PerfamanceEventConstants.QRCODEVALIDATOR,
				"QRCODEAPI_START", "QRValidatorController",
				"uploadQRCodeFileApi", "");
		File tempDir = Files.createTempDir();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		try {
			String extension = FilenameUtils
					.getExtension(inputfile.getOriginalFilename());
			if (Strings.isNullOrEmpty(extension)
					|| !"pdf".equalsIgnoreCase(extension)) {
				String msg = String.format("Invalid File/File is missing.");
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			Long id = retrieveFileId(inputfile, "APICALL", "C");
			
		/*	qrCodeService.saveAndPersistQRReports(id, extension, "C" , "apiUpload" , null);
		*/	
			String groupCode = TenantContext.getTenantId();
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("fileType", extension);
			jobParams.addProperty("optedAns", "C");
			jobParams.addProperty("uploadType", "apiUpload");

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.QRCODEVALIDATOR,
					"Processor is calling",
					"QRJsonVsPdfValidatorController", "reconQrAndPdf",
					"");

			asyncJobsService.createJob(groupCode,
					JobConstants.QR_CODE_VALIDATOR, jobParams.toString(),
					userName, 1L, null, null);

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.QRCODEVALIDATOR,
					"Processor called",
					"QRJsonVsPdfValidatorController", "reconQrAndPdf",
					"");

			JsonObject hdr = new JsonObject();
			hdr.addProperty("status", "S");

			JsonObject resp = new JsonObject();
			resp.addProperty("fileId", id);
			resp.addProperty("msg", "File has been successfully uploaded");

			JsonObject finalResponse = new JsonObject();
			finalResponse.add("hdr", hdr);
			finalResponse.add("resp", resp);

			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
					.body(finalResponse.toString());

		} catch (Exception ex) {
			String msg = "Exception while Uploading the File.";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			JsonPdfValidatorFinalRespDto errDto = new JsonPdfValidatorFinalRespDto();
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
			String optionOpted) {
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
			Long id = entity.getId();
			return id;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(ex.getMessage());
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	@PostMapping(value = "/api/QRJsonVsPdfResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> QRJsonVsPdfResponse(
			@RequestBody String jsonReq,
			HttpServletResponse response) {
		Long fileId = null;
		Gson gson = new Gson();
		try {

			String optedOption = "C";
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			QrPdfVsJsonDownloadDto dto = gson.fromJson(reqObject,
					QrPdfVsJsonDownloadDto.class);
			fileId = Long.valueOf(dto.getFileId());

			String fileStatus = qrUploadFileStatusRepo
					.findFileStatusByFileIdAndOptionOpted(fileId, optedOption);
			if (fileStatus != null) {
				if (fileStatus.equalsIgnoreCase("UPLOADED")
						|| fileStatus.equalsIgnoreCase("INPROGRESS")) {
					JsonObject errorResponse = new JsonObject();
					JsonObject hdr = new JsonObject();
					hdr.addProperty("status", "S");
					JsonObject resp = new JsonObject();
					String msg = String.format(
							"Recon is in Progress for File ID %s", fileId);
					resp.addProperty("msg", msg);
					errorResponse.add("hdr", hdr);
					errorResponse.add("resp", resp);
					/*
					 * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					 * .body(gson.toJson(errorResponse));
					 */

					return ResponseEntity.ok(gson.toJson(errorResponse));
				}
				List<String> findJsonPdfReconResponseByFileId = qrResponseLogRepo
						.findJsonPdfReconResponseByFileId(fileId);
				// Assuming there is only one record, as it returns a List
				String jsonResponseString = findJsonPdfReconResponseByFileId
						.isEmpty()
								? "" : findJsonPdfReconResponseByFileId.get(0);

				if (fileStatus.equalsIgnoreCase("FAILED")
						|| fileStatus.equalsIgnoreCase("COMPLETED")) {
					// Parse the JSON response string
					JsonObject jsonResponseObject = JsonParser
							.parseString(jsonResponseString).getAsJsonObject();

					String finalResponseString = gson
							.toJson(jsonResponseObject);
					return ResponseEntity.ok(finalResponseString);
				}

			}

			JsonObject errorResponse = new JsonObject();
			JsonObject hdr = new JsonObject();
			hdr.addProperty("status", "E");
			JsonObject resp = new JsonObject();
			resp.addProperty("errMsg", "Invalid file id");
			errorResponse.add("hdr", hdr);
			errorResponse.add("resp", resp);
			String finalResponseString = gson.toJson(errorResponse);
			return ResponseEntity.ok(finalResponseString);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			JsonObject errorResponse = new JsonObject();
			JsonObject hdr = new JsonObject();
			hdr.addProperty("status", "E");
			JsonObject resp = new JsonObject();
			resp.addProperty("errMsg",
					ex.getMessage() + "file id is %s " + fileId);
			errorResponse.add("hdr", hdr);
			errorResponse.add("resp", resp);
			String finalResponseString = gson.toJson(errorResponse);
			return ResponseEntity.ok(finalResponseString);
		}
	}

}
