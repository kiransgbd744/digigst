package com.ey.advisory.controller;

import static com.ey.advisory.common.GSTConstants.GSTR6_FOLDER_NAME;
import static com.ey.advisory.common.GSTConstants.GSTR6_RAW_FILE_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.docs.services.gstr6.Gstr6DistrbnFileUploadServiceImpl;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Balakrishna.S
 *
 */

/*
 * This class is responsible for uploading the GSTR6 Distribution web uploads
 * into hana cloud database
 */

@RestController
public class Gstr6DistributionController {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	@Qualifier("Gstr6DistrbnFileUploadServiceImpl")
	Gstr6DistrbnFileUploadServiceImpl distrbnFileUploadServiceImpl;

	public final static Logger LOGGER = LoggerFactory.getLogger(Gstr6DistributionController.class);

	@PostMapping(value = "/ui/distributionWebUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> vendorMaster(@RequestParam("file") MultipartFile[] files) throws Exception {
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
		String folderName = GSTR6_FOLDER_NAME;
		String uniqueName = GSTR6_RAW_FILE_NAME;
		String fileType = GSTConstants.DISTRIBUTION;
		String dataType = GSTConstants.INWARD;
		/*
		 * return distrbnFileUploadServiceImpl.gstr6DistributionUpload(files,
		 * folderName, uniqueName, userName, fileType, dataType);
		 */

		return distrbnFileUploadServiceImpl.fileUploadsAnnexure1(files, fileType, folderName, userName, uniqueName,
				dataType);
	}

}
