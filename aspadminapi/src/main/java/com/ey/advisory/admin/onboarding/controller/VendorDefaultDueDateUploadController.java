package com.ey.advisory.admin.onboarding.controller;

import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorDueDateDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@RestController
public class VendorDefaultDueDateUploadController {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("VendorDueDateDownloadServiceImpl")
	private VendorDueDateDownloadService dwnldService;

	@Autowired
	private EntityInfoRepository entityRepo;

	@PostMapping(value = "/uploadVendorDefaultDueDateMaster", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> uploadVendorDefaultDueDateMaster(
			@RequestParam("file") MultipartFile file) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {

			if (entityRepo.findActiveEntityIds().isEmpty()) {
				String errMsg = String.format(
						"No entities found to upload Due dates for group %s",
						TenantContext.getTenantId());
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());

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
			fileStatus.setDocId(downloadFileDetails.getValue1());
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.VENDOR_DUE_DATE);

			asyncJobsService.createJob(TenantContext.getTenantId(),
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

}
