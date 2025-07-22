package com.ey.advisory.controller.gstr9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.services.gstr9.Gstr9HsnFileUploadService;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@RestController
public class Gstr9HsnFileUploadController {

	@Autowired
	@Qualifier("Gstr9HsnFileUploadService")
	private Gstr9HsnFileUploadService gstr9HsnFileUploadService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/**
	 * Gstr9 hsn File Upload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/gstr9HsnFileUploadDocuments", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstr2aB2bFileUploadDocuments(@RequestParam("file") MultipartFile[] files)
			throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Beginning from uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
				? SecurityContext.getUser().getUserPrincipalName()
				: "SYSTEM") : "SYSTEM";
		String folderName = GSTConstants.GSTR9_FOLDER_NAME;
		String uniqueName = GSTConstants.GSTR9_FILE_NAME;
		String fileType = GSTConstants.GSTR9_HSN;
		String dataType = GSTConstants.GSTR9;

		return gstr9HsnFileUploadService.uploadDocuments(files, folderName, uniqueName, userName, fileType, dataType);
	}

}
