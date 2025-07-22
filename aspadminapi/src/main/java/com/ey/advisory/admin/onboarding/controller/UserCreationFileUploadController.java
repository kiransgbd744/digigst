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

import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Sasidhar Reddy
 *
 */
/*
 * This Class represent controller for uploading files from various sources and
 * upload into Document Repository
 */
@RestController
public class UserCreationFileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserCreationFileUploadController.class);

	@Autowired
	@Qualifier("UserCreationFileUploadHelper")
	private UserCreationFileUploadHelper uploadHelper;

	/**
	 * userCreationFileUpload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */
	@PostMapping(value = "/userCreationFileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> usercreation(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) {
		
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("UserCreationFileUploadController usercreation begin: {}");
		}
		try {
			String groupCode = TenantContext.getTenantId();
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("GroupCode:   {}",groupCode);
			}
			String[] entityIdAndName = filedata.split("-");
			// First element is Entity Id.
			Long entityId = Long.parseLong(entityIdAndName[0]);
			return uploadHelper.userCreation(files, groupCode, entityId);

		} catch (Exception ex) {

			String msg = "Error occurred while processing "
					+ "the user creation file Upload";
			LOGGER.error(msg, ex);
			return uploadHelper.createGstinRegFailureResp();
		}
	}

}
