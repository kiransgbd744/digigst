package com.ey.advisory.controller.gstr2b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.gstr2bfileupload.Gstr2bB2bFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class Gstr2bFileUploadController {

	@Autowired
	@Qualifier("Gstr2bB2bFileUploadServiceImpl")
	private Gstr2bB2bFileUploadService gstr2bB2bFileUploadService;


	/**
	 * Gstr2b File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/gstr2bB2bFileUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr2bB2bFileUploadDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining of 2b uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String folderName = GSTConstants.GSTR2B_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR2B_B2B_FILE_NAME;
		String fileType = GSTConstants.B2B;
		String dataType = APIConstants.GSTR2B_UPLOAD.toUpperCase();

		return gstr2bB2bFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

}
