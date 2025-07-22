package com.ey.advisory.controller.gstr8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.jobs.gstr8.Gstr8aFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class Gstr8aFileUploadController {

	@Autowired
	@Qualifier("Gstr8aFileUploadServiceImpl")
	private Gstr8aFileUploadService gstr8aFileUploadService;
	
	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * Gstr8a File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/gstr8aFileUploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr8aFileUploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null? (SecurityContext.getUser().getUserPrincipalName() != null? SecurityContext.getUser().getUserPrincipalName(): "SYSTEM"): "SYSTEM";
		String folderName = GSTConstants.GSTR8A_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR8A_FILE_NAME;
		String fileType = GSTConstants.GSTR8A;
		String dataType = APIConstants.GSTR8A_UPLOAD.toUpperCase();

		return gstr8aFileUploadService.uploadDocuments(files, folderName, uniqueName, userName, fileType, dataType);
	}

}
