/**
 * 
 */
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.ewb.CEWBPDFGenerationReportService;
import com.ey.advisory.app.data.services.ewb.CEWBPDFReport;
import com.ey.advisory.app.docs.dto.PdfPrintReqDto;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

/**
 * @author Sujith.Nanga
 *
 */

	@Slf4j
	@RestController
	public class CEWBPDFController {

		@Autowired
		@Qualifier("CEWBPDFGenerationReportImpl")
		CEWBPDFReport cEWBPDFReport;

		@Autowired
		@Qualifier("CEWBPDFGenerationReportService")
		CEWBPDFGenerationReportService cEWBPDFGenerationReportService;

		@Autowired
		CombineAndZipXlsxFiles combineAndZipXlsxFiles;

		@Autowired
		@Qualifier("Anx1CsvDownloadUtil")
		private Anx1CsvDownloadUtil anx1CsvDownloadUtil;
		
		@PostMapping(value = "/ui/cewbPdfReports")
		public void generateEinvSummaryReport(@RequestBody String jsonString,
				HttpServletResponse response) throws Exception {

			String fileName = null;
			InputStream inputStream = null;

			Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<PdfPrintReqDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json);
			}
			List<PdfPrintReqDto> criteria = gsonEwb.fromJson(json, listType);
			try {
				File tempDir = createTempDir();

				String zipFileName = cEWBPDFGenerationReportService
						.generateEinvoicePdfZip(tempDir, criteria);
				
				fileName = "CEWBPDFReports";
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
				anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);
			} catch (IOException | JRException ex) {
				LOGGER.error("Exception while generating CEWB PDF ", ex);
			}finally {
			    if (inputStream != null) {
			        try {
			            inputStream.close();
			        } catch (IOException e) {
			            LOGGER.error("Failed to close InputStream: ", e);
			        }
			    }
			}

		}

		private static File createTempDir() throws IOException {
			return Files.createTempDirectory("PdfReports").toFile();
		}

	}

