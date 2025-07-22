package com.ey.advisory.admin.onboarding.controller;

import java.util.List;

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

import com.ey.advisory.admin.fileupload.DataSecurityFileUploadHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
public class DataSecurityFileUploadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSecurityFileUploadController.class);

	@Autowired
	@Qualifier("DataSecurityFileUploadHandler")
	private DataSecurityFileUploadHandler handler;

	@PostMapping(value = "/dataSecuFileUpload", produces = { MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> dataSecuFileUpload(@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String fileData) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GroupCode: {}", groupCode);
			}
			String[] data = fileData.split("-");
			Long entityId = Long.valueOf(data[0]);
			List<String> errorMsg = handler.dataStatusFileUpload(files, groupCode, entityId);
			if (errorMsg != null && !errorMsg.isEmpty()) {
				resp.add("resp", gson.toJsonTree(errorMsg));
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			} else {
				APIRespDto dto = new APIRespDto("Success",
						"File uploaded successfully check your status in Data Security Screen.");
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			resp.add("resp", gson.toJsonTree("File Failed"));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
