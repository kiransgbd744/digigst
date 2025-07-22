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
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.einvoice.EwbEinvDownloadRequestDto;
import com.ey.advisory.app.search.reports.EinvEwbcommonSecParam;
import com.ey.advisory.app.services.reports.EwbEinvReportHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.Anx1CsvDownloadReportsController;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Sujith.Nanga
 *
 * 
 */
@RestController
public class InvoiceEwbEinvDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1CsvDownloadReportsController.class);

	@Autowired
	@Qualifier("EinvEwbcommonSecParam")
	EinvEwbcommonSecParam einvEwbcommonSecParam;

	@Autowired
	@Qualifier("EwbEinvReportHandler")
	private EwbEinvReportHandler ewbEinvReportHandler;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@RequestMapping(value = "/ui/downloadEinvEwbReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadApiCsvReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String fileName = null;
		String fullPath = null;
		InputStream inputStream = null;
		String reportType = null;
		try {
			File tempDir = createTempDir();
			LocalDateTime reqRTime = LocalDateTime.now();
			String recTime = reqRTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");

			EwbEinvDownloadRequestDto criteria = gson.fromJson(json,
					EwbEinvDownloadRequestDto.class);
			reportType = reqReceivedTime + "_EINV_EWB";
			fileName = "EinvEwbDownloadReport";

			fullPath = tempDir.getAbsolutePath() + File.separator + reportType
					+ ".csv";
			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			EwbEinvDownloadRequestDto setDataSecurity = einvEwbcommonSecParam
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */
			ewbEinvReportHandler.generateCsvForCriteira(setDataSecurity,
					fullPath);
			
			String zipFileName = "";
			if (tempDir.list().length > 0) {
				Long randomLong = new Random().nextLong();
				zipFileName = combineAndZipXlsxFiles.zipfolder(randomLong,
						tempDir);
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			//inputStream.close();
		
			anx1CsvDownloadUtil.deleteTemporaryDirectory(tempDir);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ADD LOGGER TODO
				}
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
}
