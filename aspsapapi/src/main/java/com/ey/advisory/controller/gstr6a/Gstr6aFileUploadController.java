package com.ey.advisory.controller.gstr6a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.jobs.gstr6.Gstr6aFileUploadService;
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
public class Gstr6aFileUploadController {

	@Autowired
	@Qualifier("Gstr6aFileUploadServiceImpl")
	private Gstr6aFileUploadService gstr6aFileUploadService;
	
	

	@Autowired
	private HttpServletRequest httpServletRequest;


	@PostMapping(value = "/ui/gstr6aFileUploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr6aFileUploadDocuments(@RequestParam("file") MultipartFile[] file)
			throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from Gstr6a uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null? (SecurityContext.getUser().getUserPrincipalName() != null? SecurityContext.getUser().getUserPrincipalName(): "SYSTEM"): "SYSTEM";
		String folderName = GSTConstants.GSTR6A_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR6A_FILE_NAME;
		String fileType = GSTConstants.B2B;
		String dataType = APIConstants.GSTR6A_UPLOAD.toUpperCase();

		return gstr6aFileUploadService.uploadDocuments(file, folderName, uniqueName, userName, fileType, dataType);
	}

}
