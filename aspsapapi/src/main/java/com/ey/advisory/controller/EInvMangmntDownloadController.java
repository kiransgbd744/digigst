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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.docs.einvoice.EinvoiceBasicDocSearchDataSecParams;
import com.ey.advisory.app.services.einvoice.reports.EInvManagmntDownloadHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class EInvMangmntDownloadController {

	@Autowired
	@Qualifier("EInvManagmntDownloadHandler")
	private EInvManagmntDownloadHandler eInvManagmntDownloadHandler;

	@Autowired
	@Qualifier("EinvoiceBasicDocSearchDataSecParams")
	private EinvoiceBasicDocSearchDataSecParams basicDocSearchDataSecParams;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@RequestMapping(value = "/ui/einvoiceMangtDownloadReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadEinvMangmntReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String fileName = null;
		String fullPath = null;
		InputStream inputStream = null;
		String reportType = null;
		try {
			File tempDir = createTempDir();
			LocalDateTime reqRTime = LocalDateTime.now();
			LocalDateTime istTime = EYDateUtil
					.toISTDateTimeFromUTC(reqRTime);
			String recTime = istTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");

			EInvoiceDocSearchReqDto criteria = gson.fromJson(json,
					EInvoiceDocSearchReqDto.class);

			reportType = reqReceivedTime + "_InvoiceManagementRecords";

			fullPath = tempDir.getAbsolutePath() + File.separator + reportType
					+ ".csv";

			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			EInvoiceDocSearchReqDto setDataSecurity = basicDocSearchDataSecParams
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");

			eInvManagmntDownloadHandler.downloadEInvMngmtScreen(setDataSecurity,
					fullPath);
			fileName = "InvoiceManagementDownloadReport";

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
					
				}
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}
}
