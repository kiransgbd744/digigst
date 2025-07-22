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

import com.ey.advisory.admin.fileupload.AppPermissionsFileUploadHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
public class AppPermissionsFileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppPermissionsFileUploadController.class);
	@Autowired
	@Qualifier("AppPermissionsFileUploadHandler")
	private AppPermissionsFileUploadHandler handler;

	@PostMapping(value = "/appPermissionFileUpload", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> appPermissionFileUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String fileData) {
		try {
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Group Code:{}", groupCode);
			}
			String[] data = fileData.split("-");
			Long entityId = new Long(data[0]);
			String errorMsg = handler.appPermFileUpload(files, groupCode,
					entityId);

			if (errorMsg != null && !errorMsg.isEmpty()) {
				return createGstinRegFailureResp(errorMsg);
			} else {
				return createGstinRegSuccessResp();
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			return createInvalidTemplateGstinRegFailureResp();
		}
	}

	public ResponseEntity<String> createGstinRegSuccessResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Success",
				"File uploaded successfully check your status in App Permission..");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinRegFailureResp(
			String errors) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = "File uploaded failed " + errors;
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createInvalidTemplateGstinRegFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto(" Upload Failed ", "Invalid template,"
				+ " Please upload again with correct template.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading  files";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}
