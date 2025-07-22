package com.ey.advisory.app.controllers.gl.master;

import jakarta.servlet.http.HttpServletRequest;

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
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.common.SecurityContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author kiran s
 *
 */

@Slf4j
@RestController
public class GlMasterFileUploadController {

	@Autowired
	@Qualifier("GlMasterFileUploadServiceImpl")
	private GlMasterFileUploadServiceImpl glMasterFileUploadService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@PostMapping(value = "/ui/glMasterFileUploadDocuments", produces = {
			MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> imsFileUploadDocuments(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String fileData) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begining from uploadDocuments");
		}

		String tenantId = TenantContext.getTenantId();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("tenantId: " + tenantId);
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		String folderName = GSTConstants.GL_MASTER_FOLDER_NAME;
		String uniqueName = GSTConstants.GL_MASTER_FILE_NAME;
		String fileType = extractDocumentType(fileData);
		if ("Business Unit".equalsIgnoreCase(fileType)) {
		    fileType = "Business_Unit_code";
		} else if ("Document Type".equalsIgnoreCase(fileType)) {
		    fileType = "Document_type";
		} else if ("Supply Type".equalsIgnoreCase(fileType)) {
		    fileType = "Supply_Type";
		} else if ("GL Code Master".equalsIgnoreCase(fileType)) {
		    fileType = "GL_Code_Mapping_Master_GL";
		} else if ("Tax Code Master".equalsIgnoreCase(fileType)) {
		    fileType = "Tax_code";
		}

		Long entityId = extractEntityId(fileData);
		LOGGER.info("Extracted entityId: {}", entityId);
		String dataType = APIConstants.GL_MASTER_UPLOAD.toUpperCase();

		LOGGER.info("Extracted Document Type: {}", fileType);

		// Optionally pass it down
		return glMasterFileUploadService.uploadDocuments(files, folderName,
				uniqueName, userName, fileType, dataType, entityId);
	}
	private String extractDocumentType(String fileData) {
		if (fileData == null || fileData.trim().isEmpty()) {
			return "UNKNOWN";
		}

		String[] parts = fileData.split("\\*");
		if (parts.length > 1) {
			return parts[1].replace("'", "").trim(); // Removes quotes around
														// 'Gl Master'
		}
		return "UNKNOWN";
	}

	private Long extractEntityId(String fileData) {
		if (fileData == null || fileData.trim().isEmpty()) {
			return null;
		}

		String[] parts = fileData.split("\\*");
		if (parts.length > 0) {
			try {
				return Long.parseLong(parts[0].trim());
			} catch (NumberFormatException e) {
				LOGGER.warn("Invalid entityId format in fileData: {}",
						fileData);
			}
		}
		return null;
	}

}
