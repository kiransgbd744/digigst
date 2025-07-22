/**
 * 
 */
package com.ey.advisory.controller.gstr9;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.services.gstr9.GSTR9ReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class GSTR9ReportDownloadController {

	@Autowired
	@Qualifier("GSTR9ReportDownloadServiceImpl")
	private GSTR9ReportDownloadService service;

	@PostMapping(value = "/ui/gstr9ReportDownload", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public void getGstr9DownloadData(HttpServletResponse response,
			@RequestBody String jsonString) {
		
		JsonObject errorResp = new JsonObject();
		Workbook workBook = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE GSTR9ReportDownloadController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String gstin = requestObject.get("gstin").getAsString();
			
			String fyOld = requestObject.get("fy").getAsString();
			
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			
			String entityId = requestObject.get("entityId").getAsString();

			String taxPeriod = GenUtil.getFinancialPeriodFromFY(formattedFy);
			
			Integer fy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);


			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			workBook = service.getData(gstin, taxPeriod,  entityId, formattedFy, fy);

			String fileName = DocumentUtility.getUniqueFileName
					(ConfigConstants.GSTR9_SUMMARY_REPORT);

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