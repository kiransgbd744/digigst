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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.LogoResponseDto;
import com.ey.advisory.core.dto.TemplateSelectionReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class LogoFileUploadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LogoFileUploadController.class);

	@Autowired
	@Qualifier("LogoFileUploadHelper")
	private LogoFileUploadHelper helper;

	@PostMapping(value = "/logoFileUpload.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> logoFileUpload(
			@RequestParam("file") MultipartFile[] files,
			@RequestParam("file-data") String fileData) {
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			String groupCode = TenantContext.getTenantId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GroupCode:  {}", groupCode);
			}
			if (fileData != null && !fileData.isEmpty()) {
				String[] entities = fileData.split("-");
				Long entityId = new Long(String.valueOf(entities[0]));
				helper.logFileUpload(files, groupCode, entityId);
				resp.add("resp", gson.toJsonTree(
						new APIRespDto("S", "Uploaded Successfully")));
			} else {
				resp.add("resp", gson
						.toJsonTree(new APIRespDto("S", "Uploaded failed")));
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			resp.add("resp",
					gson.toJsonTree(new APIRespDto("R", "Uploaded failed")));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/getLogoFileUpload.do", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLogoFileUpload(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonObject();
			TemplateSelectionReqDto reqDto = gson.fromJson(reqObj,
					TemplateSelectionReqDto.class);
			List<LogoResponseDto> getLogoFiles = helper.getLogoFile(reqDto);
			resp.add("resp", gson.toJsonTree(getLogoFiles));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}
}
