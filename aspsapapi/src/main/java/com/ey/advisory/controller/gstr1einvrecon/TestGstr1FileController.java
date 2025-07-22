/**
 * 
 */
package com.ey.advisory.controller.gstr1einvrecon;

import java.time.LocalDateTime;

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
import com.ey.advisory.app.gstr1.einv.Gstr1GSTINDeleteDataServiceImpl;
import com.ey.advisory.app.gstr1.einv.Gstr1VsEInvEecSaveArrivalService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@RestController
@Slf4j
public class TestGstr1FileController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;
	
	@Autowired
	@Qualifier("Gstr1GSTINDeleteDataServiceImpl")
	Gstr1GSTINDeleteDataServiceImpl deleteService;
	
	@Autowired
	@Qualifier("Gstr1VsEInvEecSaveArrivalService")
	private Gstr1VsEInvEecSaveArrivalService getFile;

	@PostMapping(value = "/ui/gstr1FileTest", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr1RespFileUpload(
			@RequestParam("file") MultipartFile file) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			LOGGER.debug(
					"Inside Gstr1VsEInvRespUploadController and file type is {}"
							+ " foldername is {} Gstr1EInvRespUpload",
					ConfigConstants.GSTR1EINVRESPUPLOAD);

			LoadOptions options = new LoadOptions(FileFormatType.CSV);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			// workbook.setFileName(file.getOriginalFilename());

			String fileName = DocumentUtility
					.getUniqueFileName(file.getOriginalFilename());

			workbook.setFileName("Total_Report.csv");

			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			String uniqueFileName = DocumentUtility.uploadDocument(workbook,
					ConfigConstants.GSTR1EINVRESPUPLOAD, "CSV");

			if (uniqueFileName == null) {
				throw new AppException(
						"Unexpected " + "error while uploading  file");
			}

			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(fileName);
			fileStatus.setFileType("EXCLUSIVE_SAVE_FILE");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("gstr1");
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.GSTR1EINVRESPUPLOAD);

			/*asyncJobsService.createJob(groupCode,
					JobConstants.GSTR1VsEINV_RECON_USER_UPLOAD,
					jsonParams.toString(), userName, 1L, null, null);
			*/
			
			/*deleteService.validateandSaveFileData(fileStatus.getId(), 
					uniqueFileName, ConfigConstants.GSTR1EINVRESPUPLOAD);
			*/
			getFile.validateResponseFile(fileStatus.getId(), 
					uniqueFileName, ConfigConstants.GSTR1EINVRESPUPLOAD);
			
			APIRespDto dto = new APIRespDto("Sucess",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			JsonObject resp = new JsonObject();
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
