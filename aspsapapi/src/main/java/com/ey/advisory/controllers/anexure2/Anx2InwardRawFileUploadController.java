package com.ey.advisory.controllers.anexure2;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.dms.DmsGstr1FileUploadServiceImpl;
import com.ey.advisory.app.services.annexure2fileupload.Anx2InwardRawFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
@Slf4j
public class Anx2InwardRawFileUploadController {
	
	@Autowired
	@Qualifier("Anx2InwardRawFileUploadServiceImpl")
	private Anx2InwardRawFileUploadService anx2InwardRawFileUploadService;
	
	@Autowired
	@Qualifier("DmsGstr1FileUploadServiceImpl")
	private DmsGstr1FileUploadServiceImpl dmsGstr1FileUploadService;

	private static final String CLASS_NAME = "Anx2InwardRawFileUploadController";

	/**
	 * Anx2 Raw File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = { "/ui/anx2InwardRawFileUploadDocuments",
			"/api/anx2InwardRawFileUploadDocumentsApi",
			"/ui/inwardRawfileUploads" ,
			"/api/inwardRawfileUploads"}, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> anx2InwardRawFileUploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request, 
			@RequestParam(value = "file-data", required = false) String dmsRuleId)
			throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}
		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.ANN2_FOLDER_NAME;
		String uniqueName = GSTConstants.ANN2_RAW_FILE_NAME;
		String fileType = GSTConstants.RAW;
		String dataType = GSTConstants.INWARD;
		String path = request.getServletPath();
		boolean isDmsFile=false;
		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INWARD_RAW_FILE_UPLOAD_START", CLASS_NAME,
				"anx2InwardRawFileUploadDocuments", null);
		ResponseEntity<String> resp = null;
		String source = null;
		if ("/api/anx2InwardRawFileUploadDocumentsApi.do".equals(path)) {
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
			resp = anx2InwardRawFileUploadService.uploadDocuments(files,
					folderName, uniqueName, userName, fileType, dataType,
					source);
		}
		else if ("/ui/anx2InwardRawFileUploadDocuments.do".equals(path)) {
			resp = anx2InwardRawFileUploadService.uploadDocuments(files, 
					folderName, uniqueName, userName, fileType,
					dataType, source);
		} 
		else if ("/api/inwardRawfileUploads.do".equals(path) 
				|| "/ui/inwardRawfileUploads.do".equals(path)) {
			source = "/api/inwardRawfileUploads.do".equals(path) 
					? JobStatusConstants.SFTP_WEB_UPLOAD: JobStatusConstants.WEB_UPLOAD;
			fileType = GSTConstants.COMPREHENSIVE_INWARD_RAW;
			uniqueName = GSTConstants.INWARD;

			if ("/api/inwardRawfileUploads.do".equals(path)) {
				MultipartFile file = files[0];
				String fileName = FilenameUtils.getName(file.getOriginalFilename());
				isDmsFile = fileName.toLowerCase().startsWith("map_");
			}

			if ((dmsRuleId != null && !dmsRuleId.isEmpty()) || isDmsFile) {
				resp = dmsGstr1FileUploadService.uploadDocuments(files, 
						folderName, uniqueName, userName, fileType,
						dataType, source, dmsRuleId);
				LOGGER.error("INWARD_DMSPATH-IF Block executed");
			} else {
				resp = anx2InwardRawFileUploadService.uploadDocuments(files, 
						folderName, uniqueName, userName, fileType,
						dataType, source);
				LOGGER.error("INWARD_DIGIPATH-ELSE Block executed");
			}
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
				"INWARD_RAW_FILE_UPLOAD_END", CLASS_NAME,
				"anx2InwardRawFileUploadDocuments", null);
		return resp;
	}
	
	/**
	 * Cross ITC
	 * @param file
	 * @return ResponseEntity<String>
	 * @throws Exception
	 */

	@PostMapping(value = "/ui/crossItcWebUploads", produces = {
			MediaType.APPLICATION_XML_VALUE })

	public ResponseEntity<String> crossItcWebUploads(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.GSTR2X_FOLDER_NAME;
		String uniqueName = GSTConstants.CROSS_ITC;
		String fileType = GSTConstants.CROSS_ITC;
		String dataType = GSTConstants.INWARD;
		return anx2InwardRawFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}


}
