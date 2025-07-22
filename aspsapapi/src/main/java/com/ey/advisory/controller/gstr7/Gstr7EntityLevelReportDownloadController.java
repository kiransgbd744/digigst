/**
 * 
 */
package com.ey.advisory.controller.gstr7;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.service.report.gstr7.Gstr7EntityLevelReportDownloadService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr7EntityLevelReportDownloadController {

	@Autowired
	@Qualifier("Gstr7EntityLevelReportDownloadServiceImpl")
	private Gstr7EntityLevelReportDownloadService service;

	@PostMapping(value = "/ui/gstr7EntityLevelReportDownload", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public void getGstr9DownloadData(HttpServletResponse response,
			@RequestBody String jsonString) {
		
		JsonObject errorResp = new JsonObject();
		Workbook workBook = null;
		Gson googleJson = new Gson();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr7EntityLevelReportDownloadController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			JsonArray gstins = requestObject.getAsJsonArray("gstins");
			
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			ArrayList<String> gstinlist = googleJson.fromJson(gstins, listType);

			String taxPeriod = requestObject.get("taxPeriod").getAsString();
			
			Long entityId = requestObject.get("entityId").getAsLong();

			workBook = service.getGstr7ReportData(entityId, gstinlist, taxPeriod);

			String fileName = DocumentUtility.getUniqueFileName
					(ConfigConstants.GSTR7_SUMMARY_REPORT);

			if (workBook != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();

			}

		} catch (Exception ex) {
			String msg = "Error occured while generating reconSummary report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			try {
				response.flushBuffer();
			} catch (IOException e) {
				msg = "Error occured while invoking flushBuffer() ";
				LOGGER.error(msg, ex);
			}

		}
	}
}