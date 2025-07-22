package com.ey.advisory.app.controllers.ims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.service.ims.ImsFileUploadService;
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
public class ImsFileUploadController {

	@Autowired
	@Qualifier("ImsFileUploadServiceImpl")
	private ImsFileUploadService imsFileUploadService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * IMS File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/imsFileUploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> imsFileUploadDocuments(@RequestParam("file") MultipartFile[] files) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.IMS_FOLDER_NAME;
		String uniqueName = GSTConstants.IMS_FILE_NAME;
		String fileType = GSTConstants.IMS;
		String dataType = APIConstants.IMS_UPLOAD.toUpperCase();

		return imsFileUploadService.uploadDocuments(files, folderName, uniqueName, userName, fileType, dataType);
	}

}
