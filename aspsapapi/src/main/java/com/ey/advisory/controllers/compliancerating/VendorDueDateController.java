package com.ey.advisory.controllers.compliancerating;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorDueDateDownloadService;
import com.ey.advisory.app.data.services.compliancerating.VendorDueDateUploadServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@RestController
public class VendorDueDateController {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("VendorDueDateDownloadServiceImpl")
	private VendorDueDateDownloadService dwnldService;

	@Autowired
	VendorDueDateUploadServiceImpl impl;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList
			.of("application/x-tika-ooxml", "application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("xlsx");

	@PostMapping(value = "/ui/uploadVendorDueDateMaster", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> uploadVendorMasterDueDate(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") Long entityId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {

			if (entityId == null) {
				throw new AppException("EntityID is empty");
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			MultipartFile file = files[0];

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file.getInputStream());
			LOGGER.debug("Got mimeType {} at::{} ", mimeType,
					LocalDateTime.now());

			if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
				String msg = "Invalid content in the uploaded file";
				LOGGER.error(msg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String ext = GenUtil.getFileExt(file);

			LOGGER.debug("Uploaded filename {} and ext {} at::{} ",
					file.getOriginalFilename(), ext, LocalDateTime.now());

			if (!EXT_LIST.contains(ext)) {
				String msg = "Invalid file type.";
				LOGGER.error(msg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());
			String groupCode = TenantContext.getTenantId();
			Pair<String, String> downloadFileDetails = DocumentUtility
					.uploadDocumentAndReturnDocID(workbook,
							ConfigConstants.VENDOR_DUE_DATE, "XLSX");
			String uniqueFileName = downloadFileDetails.getValue0();

			if (uniqueFileName == null) {
				throw new AppException(
						"Unexpected " + "error while uploading  file");
			}

			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(uniqueFileName);
			fileStatus.setFileType("VENDOR_DUE_DATE");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("VENDOR");
			fileStatus.setEntityId(entityId);
			fileStatus.setDocId(downloadFileDetails.getValue1());
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.VENDOR_DUE_DATE);
			jsonParams.addProperty("entityId", entityId);

			asyncJobsService.createJob(groupCode,
					JobConstants.VENDOR_DUE_DATE_UPLOAD, jsonParams.toString(),
					userName, 1L, null, null);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson
					.toJsonTree("Due dates have been uploaded successfully "));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Due dates Details";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}
	}

	@GetMapping(value = "/ui/downloadVendorDueDateReport")
	public void downloadVendorDueDateReport(HttpServletResponse response,
			@RequestParam Long entityId) {

		Workbook workbook = null;
		LOGGER.debug("inside downloadVendorDueDateReport");
		try {
			workbook = dwnldService.getVendorDueDateData(entityId);
			if (workbook != null) {
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

				response.setContentType("APPLICATION/OCTET-STREAM");

				response.setHeader("Content-Disposition",
						"attachment; filename=Vendors_Due_Date" + "_"
								+ timeMilli + ".xlsx");

				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
		} catch (Exception e) {
			LOGGER.error("Error in downloadVendorDueDateReport", e);
		}
	}

}
