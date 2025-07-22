package com.ey.advisory.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.ewb.EwbPDFGenerationReport;
import com.ey.advisory.app.data.services.ewb.EwbPdfReportService;
import com.ey.advisory.app.data.services.ewb.EwbSummaryPDFGenerationReport;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Arun KA
 *
 */

@Slf4j
@RestController
public class EwbPDFGenerationController {

	@Autowired
	@Qualifier("EwbPDFGenerationReportImpl")
	EwbPDFGenerationReport ewbPDFGenerationReport;

	@Autowired
	@Qualifier("EwbSummaryPDFGenerationReportImpl")
	EwbSummaryPDFGenerationReport ewbSummaryPDFGenerationReport;

	@Autowired
	@Qualifier("EwbPdfReportServiceImpl")
	EwbPdfReportService ewbPdfReportService;

	@GetMapping(value = "/ui/generateEWBDetailedReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generatePdfReport(HttpServletRequest request,
			HttpServletResponse response) {

		String ewbNo = request.getParameter("ewbNo");
		if (ewbNo == null || ewbNo.isEmpty()) {
			String msg = "EWB No is mandatory to generate PDF Report";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		LOGGER.debug("EWB PDF Generation process intialised {}", ewbNo);
		JasperPrint jasperPrint = ewbPDFGenerationReport
				.generatePdfReport(ewbNo);

		response.setContentType("application/x-download");
		response.addHeader("Content-disposition",
				"attachment; filename=EwayBillDetailed" + "_" + ewbNo + ".pdf");
		OutputStream out;
		try {
			out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
		} catch (IOException | JRException ex) {
			LOGGER.error("Exception while generating EWB PDF ", ex);
		}
	}

	@GetMapping(value = "/ui/generateEWBSummaryReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateEwbSummaryPdfReport(HttpServletRequest request,
			HttpServletResponse response) {

		String ewbNo = request.getParameter("ewbNo");
		if (ewbNo == null || ewbNo.isEmpty()) {
			String msg = "EWB No is mandatory to generate PDF Report";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		LOGGER.debug("EWB PDF Generation process intialised {}", ewbNo);
		JasperPrint jasperPrint = ewbSummaryPDFGenerationReport
				.generateSummaryPdfReport(ewbNo);

		response.setContentType("application/x-download");
		response.addHeader("Content-disposition",
				"attachment; filename=EwayBillSummary" + "_" + ewbNo + ".pdf");
		OutputStream out;
		try {
			out = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, out);
		} catch (IOException | JRException ex) {
			LOGGER.error("Exception while generating EWB PDF ", ex);
		}
	}

	@PostMapping(value = "/ui/ewbDetailedMultiplePDFReports")
	public void generateEwbDetailedReports(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;

		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Type listType = new TypeToken<List<String>>() {
		}.getType();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json);
		}
		JsonArray ewbNos = json.get("ewbNoList").getAsJsonArray();
		List<String> ewbNoList = gsonEwb.fromJson(ewbNos, listType);
		try {
			File tempDir = createTempDir();

			String zipFileName = ewbPdfReportService
					.generateEwbDetailedPdfZip(tempDir, ewbNoList);

			fileName = "EwayBillDetailedPDFReports";
			LocalDateTime reqRTime = LocalDateTime.now();
			String recTime = reqRTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");


			if (tempDir.list().length > 0) {
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName + "_"
								+ reqReceivedTime + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			//inputStream.close();
			// anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			throw new AppException(
					"Exception while generating EwbDetailedPDF multile reports ",
					ex);
		}finally {
			//sonar obs
		    if (inputStream != null) {
		        try {
					inputStream.close();
		        } catch (IOException e) {
		        	 LOGGER.error("Failed to close InputStream", e);
		        }
		    }
		}
	}

	@PostMapping(value = "/ui/ewbSummaryMultiplePdfReports")
	public void generateEwbSummaryReports(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;

		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Type listType = new TypeToken<List<String>>() {
		}.getType();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", json);
		}
		JsonArray ewbNos = json.get("ewbNoList").getAsJsonArray();
		List<String> ewbNoList = gsonEwb.fromJson(ewbNos, listType);
		try {
			File tempDir = createTempDir();

			String zipFileName = ewbPdfReportService
					.generateEwbSummaryPdfZip(tempDir, ewbNoList);

			fileName = "EwayBillSummaryPDFReports";
			LocalDateTime reqRTime = LocalDateTime.now();
			String recTime = reqRTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");


			if (tempDir.list().length > 0) {
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName + "_"
								+ reqReceivedTime + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			//inputStream.close();
			// anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
			throw new AppException(
					"Exception while generating EwbDetailedPDF multile reports ",
					ex);
		}finally {
			//sonar obs
		    if (inputStream != null) {
		        try {
					inputStream.close();
		        } catch (IOException e) {
		        	 LOGGER.error("Failed to close InputStream", e);
		        }
		    }
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}

}
