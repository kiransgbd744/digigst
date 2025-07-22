/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

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

import com.ey.advisory.admin.services.onboarding.gstinfileupload.OnboardingFileUploadException;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class OrganizationFileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OrganizationFileUploadController.class);

	@Autowired
	@Qualifier("OrganizationFileUploadHelper")
	private OrganizationFileUploadHelper uploadHelper;

	/**
	 * organizationFileUpload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */
	@PostMapping(value = "/organizationFileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> orgUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) {
		try {
			// Get the group code of the currenlty logged in user.
			String groupCode = TenantContext.getTenantId();

			// We assume that the file data is in the format EntityId-EntityName
			// where EntityId is a numeric value and Entity Name is the actual
			// name
			// of the entity. If this value is null or any other non-numeric
			// value
			// comes here, the following splitting and parsing code will fail
			String[] entityIdAndName = filedata.split("-");
			// First element is Entity Id.
			Long entityId = Long.parseLong(entityIdAndName[0]);

			uploadHelper.orgUpload(files, groupCode, entityId);
			return uploadHelper.createGstinRegSuccessResp();

		} catch (OnboardingFileUploadException ex) {
			LOGGER.error(ex.getMessage(), ex);
			return uploadHelper.createGstinRegFailureResp();
		} catch (Exception ex) {
			String msg = "Error occurred while processing"
					+ " the organisation file upload";

			LOGGER.error(msg, ex);
			return uploadHelper.createGstinRegFailureResp();
		}
	}

}
