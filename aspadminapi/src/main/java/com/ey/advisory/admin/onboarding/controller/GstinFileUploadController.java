
package com.ey.advisory.admin.onboarding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Sasidhar Reddy
 */

/*
 * This Class represent controller for uploading files from various sources and
 * upload into Document Repository
 */

@RestController
public class GstinFileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstinFileUploadController.class);

	@Autowired
	@Qualifier("GstinRegFileUploadHelper")
	private GstinRegFileUploadHelper uploadHelper;

	/**
	 * RegistrationFileUpload
	 * 
	 * @param files
	 * @return ResponseEntity<String>
	 */
	@PostMapping(value = "/registrationFileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> gstinRegUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String filedata) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("GstinFileUploadController gstinRegUpload begin");
		}
		try {
			String groupCode = TenantContext.getTenantId();
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("GroupCode:  {}",groupCode);
			}
			String[] entityIdAndName = filedata.split("-");
			// First element is Entity Id.
			Long entityId = Long.parseLong(entityIdAndName[0]);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("GstinFileUploadController gstinRegUpload end");
			}
			return uploadHelper.gstinRegUpload(files, groupCode, entityId);

		} catch (Exception ex) {
			String msg = "Error occurred while processing"
					+ " the GSTIN Registration Upload";
			LOGGER.error(msg, ex);
			return createGstinRegFailureResp();
		}
		
	}

	private ResponseEntity<String> createGstinRegFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = "File uploaded failed, invalid/same template. ";
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}
}
