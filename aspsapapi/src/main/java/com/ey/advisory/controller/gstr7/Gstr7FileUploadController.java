package com.ey.advisory.controller.gstr7;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.services.gstr7fileupload.Gstr7FileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@RestController
public class Gstr7FileUploadController {
	
	@Autowired
	@Qualifier("Gstr7FileUploadServiceImpl")
	private Gstr7FileUploadService gstr7FileUploadService;

	/**
	 * Gstr7 File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = { "/ui/gstr7FileUploadDocuments",
			"/api/gstr7FileUploadDocumentsApi" }, produces = {
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr7FileUploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			HttpServletRequest request) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}

		String source = null;
		String path = request.getServletPath();
		if ("/api/gstr7FileUploadDocumentsApi.do".equals(path)) {
			source = JobStatusConstants.SFTP_WEB_UPLOAD;
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		String folderName = GSTConstants.GSTR7_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR7_FILE_NAME;
		String fileType = GSTConstants.GSTR7_TDS;
		String dataType = GSTConstants.OTHERS;

		return gstr7FileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, source);
	}

}
