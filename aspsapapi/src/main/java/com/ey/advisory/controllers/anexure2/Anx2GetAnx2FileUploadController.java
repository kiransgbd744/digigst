package com.ey.advisory.controllers.anexure2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.annexure2fileupload.Anx2InwardRawFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class Anx2GetAnx2FileUploadController {

	@Autowired
	@Qualifier("Anx2GetAnx2FileUploadServiceImpl")
	private Anx2InwardRawFileUploadService anx2InwardRawFileUploadService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * Get-Anx2 File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/anx2GetAnx2FileUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> anx2GetAnx2FileUploadDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
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
		String folderName = GSTConstants.GET_ANX2_FOLDER_NAME;
		String uniqueName = GSTConstants.GET_ANX2_FILE_NAME;
		String fileType = GSTConstants.RAW;
		String dataType = GSTConstants.INWARD;

		return anx2InwardRawFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, null);
	}

}
