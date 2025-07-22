package com.ey.advisory.controllers.anexure1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
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

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.reports.Anx1CsvReportHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Anx1CsvDownloadReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1CsvDownloadReportsController.class);

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	BasicCommonSecParamReports basicCommonSecParamReports;

	@Autowired
	@Qualifier("Anx1CsvReportHandler")
	private Anx1CsvReportHandler anx1CsvReportHandler;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@RequestMapping(value = "/ui/downloadcsvReportsOld", method = RequestMethod.POST, produces = {
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
			String reqReceivedTime = recTime.replaceAll("[-T:.]","");
			
			Anx1ReportSearchReqDto criteria = gson.fromJson(json,
					Anx1ReportSearchReqDto.class);
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
				if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
					reportType = reqReceivedTime + "_OutwardApiProcessedActiveRecords";
				} else if (criteria.getStatus() != null
						&& criteria.getStatus().equalsIgnoreCase(
								DownloadReportsConstant.INACTIVE)) {
					reportType = reqReceivedTime + "_OutwardApiProcessedInactiveRecords";
				}
			}
			if (criteria.getType() != null && criteria.getType()
					.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
				if (criteria.getStatus() != null && criteria.getStatus()
						.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
					reportType = reqReceivedTime + "_OutwardApiErrorActiveReport";
				} else if (criteria.getStatus() != null
						&& criteria.getStatus().equalsIgnoreCase(
								DownloadReportsConstant.INACTIVE)) {
					reportType = reqReceivedTime + "_OutwardApiErrorInactiveReport";
				}
			}
			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.TOTALERECORDS)) {
				reportType = reqReceivedTime + "_OutwardApiTotalRecordsReport";
			}


			fullPath = tempDir.getAbsolutePath() + File.separator + reportType
					+ ".csv";
			List<Object> cutoffPeriods = entityConfigPrmtRepository
					.findByEntityQtnCode(criteria.getEntityId());
			if (cutoffPeriods != null && !cutoffPeriods.isEmpty()) {
				Integer cutoffPeriod = Integer
						.parseInt(String.valueOf(cutoffPeriods.get(0)));
				criteria.setAnswer(cutoffPeriod);
			}else{
				criteria.setAnswer(202010);
			}
			List<Object> serviceOptions = entityConfigPrmtRepository
					.findByEntityServiceCode(criteria.getEntityId());
			if (serviceOptions != null && !serviceOptions.isEmpty()) {
				String serviceOption = (String) serviceOptions.get(0);
				criteria.setServiceOption(serviceOption);
			}else{
				criteria.setServiceOption(null);
			}
			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			Anx1ReportSearchReqDto setDataSecurity = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				anx1CsvReportHandler.generateCsvForCriteira(setDataSecurity,
						fullPath);
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.PROCESSED)) {
					if (criteria.getStatus() != null && criteria.getStatus()
							.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
						fileName = "ApiProcessedActiveRecordsReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.INACTIVE)) {
						fileName = "ApiProcessedInactiveRecordsReport";
					}
				}
				if (criteria.getType() != null && criteria.getType()
						.equalsIgnoreCase(DownloadReportsConstant.ERROR)) {
					if (criteria.getStatus() != null && criteria.getStatus()
							.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
						fileName = "ApiErrorActiveRecordsReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.INACTIVE)) {
						fileName = "ApiErrorInactiveRecordsReport";
					}
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.PROCESSEDINFO)) {
					if (criteria.getStatus() != null && criteria.getStatus()
							.equalsIgnoreCase(DownloadReportsConstant.ACTIVE)) {
						fileName = "ApiProcessedInfoActiveRecordsReport";
					} else if (criteria.getStatus() != null
							&& criteria.getStatus().equalsIgnoreCase(
									DownloadReportsConstant.INACTIVE)) {
						fileName = "ApiProcessedInfoInactiveRecordsReport";
					}
				}
				if (criteria.getType() != null
						&& criteria.getType().equalsIgnoreCase(
								DownloadReportsConstant.TOTALERECORDS)) {
					fileName = "ApiTotalRecordsReport";
				}
			}

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
