package com.ey.advisory.controller.gl.recon;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.glrecon.GlReconProcessReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain 
 * 
 * GL Recon Azure api callback controller
 */

@Slf4j
@RestController
public class GLReconAzureApiCallbackController {

	@Autowired
	@Qualifier("GlReconProcessReportServiceImpl")
	private GlReconProcessReportService glReconReportProcessService;

	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;
	

	@PostMapping(value = "/glReconApi/glReconStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveVCReqData(
			@RequestHeader("groupCode") String groupCode,
			@RequestBody String jsonString) {
		Long requestId = null;
		String status = null;
		String respProcess = null;
		String errMsg = null;
		try {


			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject hdrObject = requestObject.getAsJsonObject("req");
			requestId = hdrObject.get("requestId").getAsLong();
			
			if (hdrObject.has("status")) {
				status = hdrObject.get("status").getAsString();
			}

			if (hdrObject.has("errMsg")) {
				errMsg = hdrObject.get("errMsg").getAsString();

			}

			// err message column for reference

			if (status != null && "Completed".equalsIgnoreCase(status)) {
				respProcess = glReconReportProcessService
						.processReconFiles(groupCode, requestId);
				if (respProcess == null) {
					String msg = "No Files are present";
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(APIRespDto.createSuccessResp()));
			LOGGER.debug("End GLReconAzureApiCallbackController.glReconStatus() before returning response : {}", resp.toString());

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			glReconReportConfig.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, null, LocalDateTime.now(), requestId);

			String msg = "Exception while calling downloadVendorJsonFiles ";
			LOGGER.error(msg, e);

			throw new AppException(e, msg);
		}
	}

}
