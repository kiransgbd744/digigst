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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.ewb.CEWBDownloadReportHandler;
import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportRequest;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.ConsolidatedEWBRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@RestController
public class CEWBScreenDownloadReportController {

	@Autowired
	@Qualifier("CEWBDownloadReportHandler")
	private CEWBDownloadReportHandler cewbDownloadReportHandler;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@PostMapping(value = "/ui/cewbDownloadReports")
	public void cewbDownloadReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		String fileName = null;
		InputStream inputStream = null;

		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		String reqJson = obj.get("req").getAsJsonObject().toString();
		Gson gsonEwb = GsonUtil.gsonInstanceWithEWBDateFormat();

		CEWBDownloadReportRequest req = gsonEwb.fromJson(reqJson.toString(),
				CEWBDownloadReportRequest.class);

		ConsolidatedEWBRequest cewb = new ConsolidatedEWBRequest();
		cewb.setEntityId(req.getEntityId());
		ConsolidatedEWBRequest request = basicCommonSecParam
				.setOutwardDataSecuritySearchParams(cewb);
		req.setDataSecAttrs(request.getDataSecAttrs());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request Json {}", reqJson);
		}

		try {
			File tempDir = createTempDir();

			String zipFileName = cewbDownloadReportHandler
					.generateCEWBCsvZip(tempDir, req);

			fileName = "CEWBCsvReports";
			LocalDateTime reqRTime = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
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
		    if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (IOException e) {
		            LOGGER.error("Error occurred while closing InputStream: ", e);
		        }
		    }
		}

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("PdfReports").toFile();
	}
}
