package com.ey.advisory.controller.recipientmaster;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.recipientmasterupload.RecipientMasterUploadService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@RestController
@Slf4j
public class RecipientMasterUploadController {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String APPLICATION_OCTET_STREAM = "APPLICATION/OCTET-STREAM";

	@Autowired
	private RecipientMasterUploadService recipientMasterService;
	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;

	@PostMapping(value = "/ui/recipientMasterUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> recipientMasterUpload(
			@RequestParam("file") MultipartFile file) throws Exception {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {

			String userName = SecurityContext.getUser() != null
					? extrectedMethodForLineWrapping() : "SYSTEM";

			LOGGER.debug(
					"Inside recipientMasterUpload and file type is {}  "
							+ "foldername is {} ",
					"file", ConfigConstants.RECIPIENTMASTERUPLOAD);

			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			Pair<String, String> downloadFileDetails = DocumentUtility.uploadDocumentAndReturnDocID(workbook,
					ConfigConstants.RECIPIENTMASTERUPLOAD, "XLSX");
			String uniqueFileName = downloadFileDetails.getValue0();
			
			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(uniqueFileName);
			fileStatus.setFileType("RECIPIENT_MASTER");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("INWARD");
			fileStatus.setDocId(downloadFileDetails.getValue1());
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.RECIPIENTMASTERUPLOAD);

			asyncJobsService.createJob(groupCode,
					JobConstants.RECIPIENTMASTER_UPLOAD, jsonParams.toString(),
					userName, 1L, null, null);
			APIRespDto dto = new APIRespDto("Sucess",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");

			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Begining from uploadDocuments:{} ");
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed" + e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	private String extrectedMethodForLineWrapping() {
		return SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName() : "SYSTEM";
	}

	@PostMapping(value = "/ui/getRecipientUploadedInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getRecipientUploadedInformation(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE RecipientUploadController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			FileStatusReqDto fileReqDto = gson.fromJson(reqObject,
					FileStatusReqDto.class);

			List<Gstr1FileStatusEntity> fileStatus = fileStatusRepository
					.getRecipientMasterStatus(fileReqDto.getFileType(),
							fileReqDto.getDataRecvFrom(),
							fileReqDto.getDataRecvTo());
			if (fileStatus.isEmpty()) {
				throw new AppException("No Recipient upload records found");
			}

			JsonElement jsonElement = gson.toJsonTree(fileStatus);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("RecipientMasterStatus", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", jsonObject);
			LOGGER.info("response {}", resp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getRecipientMasterErrReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getRecipientMasterErrorReport(@RequestBody String jsonString,
			HttpServletResponse response) throws IOException {

		Long fileId = null;
		Workbook workbook = null;

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject request = requestObject.get("req").getAsJsonObject();
			fileId = request.get("fileId").getAsLong();
			String typeOfFlag = request.get("flagofRecord").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Requested %s Report for fileId %s:: ", typeOfFlag,
						fileId);
				LOGGER.debug(msg);
			}

			workbook = recipientMasterService
					.getRecipientMasterErrorReport(fileId, typeOfFlag);

			LocalDateTime now = LocalDateTime.now();
			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
			timeMilli = timeMilli.replace(".", "");
			timeMilli = timeMilli.replace("-", "");
			timeMilli = timeMilli.replace(":", "");

			if (workbook != null) {
				response.setContentType(APPLICATION_OCTET_STREAM);
				if (typeOfFlag.equalsIgnoreCase("errorrecords")) {
					response.setHeader(CONTENT_DISPOSITION,
							String.format(ATTACHMENT_FILENAME
									+ "RecipientMasterErrorReport" + "_"
									+ timeMilli + ".xlsx"));
				} else {
					response.setHeader(CONTENT_DISPOSITION,
							String.format(ATTACHMENT_FILENAME
									+ "RecipientMasterInformationReport" + "_"
									+ timeMilli + ".xlsx"));
				}
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}

		} catch (Exception ex) {
			JsonObject errorResp = new JsonObject();
			String msg = "Error occured while generating Recipient Master"
					+ " Error Report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
	}

}
