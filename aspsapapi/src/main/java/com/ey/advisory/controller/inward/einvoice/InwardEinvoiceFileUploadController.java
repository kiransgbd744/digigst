package com.ey.advisory.controller.inward.einvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.docs.einvoice.InwardEinvoiceFileUploadService;
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
public class InwardEinvoiceFileUploadController {

	@Autowired
	@Qualifier("InwardEinvoiceFileUploadServiceImpl")
	private InwardEinvoiceFileUploadService inwardEinvoiceFileUploadService;

	@PostMapping(value = "/ui/inwardEinvoiceFileUploadDocument", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> inwardEinvocieFileUploadDocuments(
			@RequestParam("file") MultipartFile[] files) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining of Inward E-invoice uploadDocuments");
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
		String folderName = GSTConstants.INWARD_EINVOICE_FOLDER_NAME;
		String uniqueName = GSTConstants.INWARD_EINVOICE_FILE_NAME;
		String fileType = GSTConstants.INWARD_EINVOICE;
		String dataType = APIConstants.INWARD_EINVOICE_UPLOAD.toUpperCase();

		return inwardEinvoiceFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType);
	}

}
