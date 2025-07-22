package com.ey.advisory.controller.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.gstr2afileupload.Gstr2aB2bFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */

@Slf4j
@RestController
public class Gstr2aFileUploadController {

	@Autowired
	@Qualifier("Gstr2aB2bFileUploadServiceImpl")
	private Gstr2aB2bFileUploadService gstr2aB2bFileUploadService;
	
	

	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * Gstr2a File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/gstr2aB2bFileUploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr2aB2bFileUploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null? (SecurityContext.getUser().getUserPrincipalName() != null? SecurityContext.getUser().getUserPrincipalName(): "SYSTEM"): "SYSTEM";
		String folderName = GSTConstants.GSTR2A_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2A_B2B_FILE_NAME;
		String fileType = GSTConstants.B2B;
		String dataType = APIConstants.GSTR2A_UPLOAD.toUpperCase();

		return gstr2aB2bFileUploadService.uploadDocuments(files, folderName, uniqueName, userName, fileType, dataType);
	}

}
