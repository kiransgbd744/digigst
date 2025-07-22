package com.ey.advisory.controller.gstr9;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.pdf.Gstr9SummaryPDFGenerationReport;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@RestController
public class Gstr9SummaryPdfController {

	@Autowired
	@Qualifier("Gstr9SummaryPDFGenerationReportImpl")
	private Gstr9SummaryPDFGenerationReport gstr9SummaryPDFGenerationReport;

	@PostMapping(value = "/ui/generateGstr9SummaryPDFReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadGstr9SummaryPDFReport(HttpServletRequest request,
			HttpServletResponse response , @RequestBody String jsonString) throws IOException {
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();
			String gstin = reqJson.get("gstin").getAsString();
			String fy = reqJson.get("fy").getAsString();
			String isDigigst = reqJson.get("isDigigst").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
						"Gstin and Financial year is Mandatory")));
				response.getWriter().println(resp.toString());
				return;
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Begin download in GSTR9 pdf with gstin : %s ", gstin);
				LOGGER.debug(msg);
			}

			String fyGstn = fy.split("-")[0] + fy.split("-")[1];
			
			JasperPrint jasperPrint = gstr9SummaryPDFGenerationReport
					.generateGstr9SummaryPdfReport(gstin, fy, isDigigst);

			if (jasperPrint != null) {
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						"attachment; filename=" + "GSTR9_" + gstin + "_" + fyGstn
								+ "_" + timeMilli + ".pdf");

				OutputStream out;
				out = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			}
		} catch (Exception ex) {
			String msg = " Exception while Downloading the Gstr9 PDF ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E",
						"No User data available to generate PDF")));
			response.getWriter().println(resp.toString());
		}
	}

}
