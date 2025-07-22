/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.DistributionErrorReportHandler;
import com.ey.advisory.app.services.reports.DistributionProcessedInfoReportHandler;
import com.ey.advisory.app.services.reports.DistributionProcessedReportHandler;
import com.ey.advisory.app.services.reports.DistributionTotalReportHandler;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
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
public class DistributionDownloadReprotController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DistributionDownloadReprotController.class);

	/*
	 * @Autowired
	 * 
	 * @Qualifier("EntityConfigPrmtRepository") private
	 * EntityConfigPrmtRepository entityConfigPrmtRepository;
	 */

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("DistributionProcessedReportHandler")
	private DistributionProcessedReportHandler distributionProcessedReportHandler;
	
	@Autowired
	@Qualifier("DistributionProcessedInfoReportHandler")
	private DistributionProcessedInfoReportHandler distributionProcessedInfoReportHandler;
	
	@Autowired
	@Qualifier("DistributionErrorReportHandler")
	private DistributionErrorReportHandler distributionErrorReportHandler;
	
	@Autowired
	@Qualifier("DistributionTotalReportHandler")
	private DistributionTotalReportHandler distributionTotalReportHandler;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@RequestMapping(value = "/ui/downloaddistributionReports", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadFileStatusCsvReport(@RequestBody String jsonString,
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
			Anx1FileStatusReportsReqDto criteria = gson.fromJson(json, Anx1FileStatusReportsReqDto.class);
			Long fileId = criteria.getFileId();
			if (fileId == null && fileId == 0L) {
				LOGGER.error("File Id not found in the request and it is invalid");
				throw new Exception("File Id not found in the request and it is invalid");
			}

			if (StringUtils.isNotEmpty(criteria.getType())
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				reportType = "DistributionProcessedReport";
				fileName = "DistributionProcessedReport";
				
				fullPath = tempDir.getAbsolutePath() + File.separator + reportType + ".csv";
				distributionProcessedReportHandler.generateProcessedFileStatus(criteria, fullPath);
			}

			if (StringUtils.isNotEmpty(criteria.getType())
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				reportType = "DistributionErrorReport";
				fileName = "DistributionErrorReport";
				
				fullPath = tempDir.getAbsolutePath() + File.separator + reportType + ".csv";
				distributionErrorReportHandler.generateCsvForFileStatus(criteria, fullPath);
			}

			if (StringUtils.isNotEmpty(criteria.getType())
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.TOTALERECORDS)) {
				reportType = "DistributionTotalReport";
				fileName = "DistributionTotalReport";

				fullPath = tempDir.getAbsolutePath() + File.separator + reportType + ".csv";
				distributionTotalReportHandler.generateCsvForFileStatus(criteria, fullPath);
			}
			
			if (StringUtils.isNotEmpty(criteria.getType())
					&& criteria.getType().equalsIgnoreCase(DownloadReportsConstant.PROCESSEDINFO)) {
				reportType = "DistributionProcessedInfoReport";
				fileName = "DistributionProcessedInfoReport";

				fullPath = tempDir.getAbsolutePath() + File.separator + reportType + ".csv";
				distributionProcessedInfoReportHandler.generateCsvForFileStatus(criteria, fullPath);
			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {
				Long randomLong = new Random().nextLong();
				zipFileName = combineAndZipXlsxFiles.zipfolder(randomLong, tempDir);
				File zipFile = new File(tempDir, zipFileName);
				int read = 0;
				byte[] bytes = new byte[1024];
				inputStream = FileUtils.openInputStream(zipFile);
				response.setHeader("Content-Disposition", String.format("attachment; filename=" + fileName + ".zip"));

				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				response.getOutputStream().flush();
			}
			deleteTemporaryDirectory(tempDir);

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

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("Deleted the Temp directory/Folder '%s'", tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format("Failed to remove the temp " + "directory created for zip: '%s'. This will "
						+ "lead to clogging of disk space.", tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}
}