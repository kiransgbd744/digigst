package com.ey.advisory.controllers.vendorcommunication;

import java.time.LocalDateTime;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */
@RestController
@Slf4j
public class VendorUploadController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;

	private static final List<String> CSV_CONTENT_TYPE = ImmutableList
			.of("application/x-tika-ooxml", "application/vnd.ms-excel");

	private static final List<String> EXT_LIST = ImmutableList.of("xlsx");

	@PostMapping(value = "/ui/vendorfileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> vendorFileUpload(
			@RequestParam("file") MultipartFile file) throws Exception {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {

			String userName = SecurityContext.getUser() != null
					? extrectedMethodForLineWrapping() : "SYSTEM";

			LOGGER.debug(
					"Inside vendorFileUpload and file type is {}  "
							+ "foldername is {} ",
					"file", ConfigConstants.VENDORDATAUPLOAD);

			LOGGER.debug("About to get mimeType at ::{}", LocalDateTime.now());

			String mimeType = GenUtil.getMimeType(file.getInputStream());
			LOGGER.debug("Got mimeType {} at::{} ", mimeType,
					LocalDateTime.now());

			if (mimeType != null && !CSV_CONTENT_TYPE.contains(mimeType)) {
				String msg = "Invalid content in the uploaded file";
				APIRespDto dto = new APIRespDto("Failed", msg);
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				LOGGER.error(msg);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String ext = GenUtil.getFileExt(file);

			LOGGER.debug("Uploaded filename {} and ext {} at::{} ",
					file.getOriginalFilename(), ext, LocalDateTime.now());

			if (!EXT_LIST.contains(ext)) {
				String msg = "Invalid file type.";
				APIRespDto dto = new APIRespDto("Failed", msg);
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				resp.add("resp", respBody);
				LOGGER.error(msg);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			String uniqueFileName = DocumentUtility.uploadDocument(workbook,
					ConfigConstants.VENDORDATAUPLOAD, "XLSX");

			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(uniqueFileName);
			fileStatus.setFileType("VENDOR_UPLOAD");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("VENDOR");
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.VENDORDATAUPLOAD);

			asyncJobsService.createJob(groupCode,
					JobConstants.VENDOR_DATA_FILE_UPLOAD, jsonParams.toString(),
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
	@PostMapping(value = "ui/getVendorUploadedInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUploadedInformation(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE VendorUploadController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			JsonObject reqObject = requestObject.getAsJsonObject("req");

			JsonObject hdrObject = requestObject.getAsJsonObject("hdr");
			int pageNum = hdrObject.get("pageNum").getAsInt();
			int pageSize = hdrObject.get("pageSize").getAsInt();

			String fileType = reqObject.get("vendorUpload").getAsString();

			int recordsToStart = pageNum;

			int noOfRowstoFetch = pageSize;

			int totalCount = 0;

			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			List<Gstr1FileStatusEntity> gstr1FileStatusEntities = fileStatusRepository
					.findByFileType(fileType, pageReq);

			totalCount = fileStatusRepository
					.findCountByFileType("VENDOR_UPLOAD");

			Pair<List<Gstr1FileStatusEntity>, Integer> respList = new Pair<>(
					gstr1FileStatusEntities, totalCount);

			String responseData = gson.toJson(respList.getValue0());

			JsonElement jsonElement = new JsonParser().parse(responseData);
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("VendorUploadedStatus", jsonElement);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp(
					respList.getValue1(), pageNum, pageSize, "S",
					"Successfully fetched Vendor File Status records")));
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
}
