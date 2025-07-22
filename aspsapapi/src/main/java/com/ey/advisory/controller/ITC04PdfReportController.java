/**
 * 
 */
package com.ey.advisory.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.pdf.ITC04MultiplePdfZipReportService;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

/**
 * @author Balakrishna.S
 *
 */
@RestController
@Slf4j
public class ITC04PdfReportController {

	
/*	@Autowired
	@Qualifier("ITC04PDFGenerationReportImpl")
	ITC04PDFGenerationReportImpl itc04ReportImpl;
*/	
	@Autowired
	@Qualifier("ITC04MultiplePdfZipReportService")
	ITC04MultiplePdfZipReportService zipReportService;
	
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	
	@PostMapping(value = "/ui/itc04PdfReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateItc04PdfReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
	//	JsonObject resp = new JsonObject();

		JsonObject reqObj = (new JsonParser()).parse(jsonString)
				.getAsJsonObject().get("req").getAsJsonObject();

		ITC04RequestDto reqDto = gson.fromJson(reqObj,
				ITC04RequestDto.class);

		try {
			File tempDir = createTempDir();

			
			String zipFileName = zipReportService
					.generateEinvoicePdfZip(tempDir, reqDto);
			
			fileName = "ITC-04_PDF_Reports";
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
			LOGGER.error("Exception while generating Einvoice PDF ", ex);
		}finally {
			//sonar obs
		    if (inputStream != null) {
		        try {
					inputStream.close();
		        } catch (IOException e) {
		            LOGGER.debug("An error occurred while closing inputStream", e);

		        }
		    }
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}
	
}
