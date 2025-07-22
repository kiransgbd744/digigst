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

import com.ey.advisory.app.docs.dto.einvoice.EInvMangmntScreenDownloadReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.InvMnagmntCommonSecParams;
import com.ey.advisory.app.services.einvoice.reports.EInvManagmntScreenDownloadHandler;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.GSTConstants.ProcessingStatus;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class EInvMangmntScreenDownloadController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvMangmntScreenDownloadController.class);

	@Autowired
	@Qualifier("EInvManagmntScreenDownloadHandler")
	private EInvManagmntScreenDownloadHandler einvManagmntScreenDownloadHandler;

	@Autowired
	@Qualifier("InvMnagmntCommonSecParams")
	InvMnagmntCommonSecParams invMnagmntCommonSecParams;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@RequestMapping(value = "/ui/einvoiceMangtScreenDownload", method = RequestMethod.POST, produces = {
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
			String recTime = reqRTime.toString();
			String reqReceivedTime = recTime.replaceAll("[-T:.]", "");

			EInvMangmntScreenDownloadReqDto criteria = gson.fromJson(json,
					EInvMangmntScreenDownloadReqDto.class);
			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				if (criteria.getProcessingStatus() != null
						&& criteria.getProcessingStatus().length() > 0) {
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.PROCESSED.getStatus())) {
						reportType = reqReceivedTime
								+ "_OutwardInvMngmtProcessedRecords";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.ERROR.getStatus())) {
						reportType = reqReceivedTime
								+ "_OutwardInvMngmtErrorRecords";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.INFORMATION.getStatus())) {
						reportType = reqReceivedTime
								+ "_OutwardInvMngmtProcessedInfoRecords";
					}
				} else {
					reportType = reqReceivedTime + "_OutwardInvMngmtAllRecords";
				}
			} else {
				if (criteria.getProcessingStatus() != null
						&& criteria.getProcessingStatus().length() > 0) {
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.PROCESSED.getStatus())) {
						reportType = reqReceivedTime
								+ "_InwardInvMngmtProcessedRecords";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.ERROR.getStatus())) {
						reportType = reqReceivedTime
								+ "_InwardInvMngmtErrorRecords";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.INFORMATION.getStatus())) {
						reportType = reqReceivedTime
								+ "_InwardInvMngmtProcessedInfoRecords";
					}
				} else {
					reportType = reqReceivedTime + "_InwardInvMngmtAllRecords";
				}
			}

			fullPath = tempDir.getAbsolutePath() + File.separator + reportType
					+ ".csv";
			/**
			 * Start - Set Data Security Attributes
			 */
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");

			EInvMangmntScreenDownloadReqDto setDataSecurity = invMnagmntCommonSecParams
					.setDataSecuritySearchParams(criteria);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			einvManagmntScreenDownloadHandler
					.downloadEInvMngmtScreen(setDataSecurity, fullPath);

			if (criteria.getDataType() != null && criteria.getDataType()
					.equalsIgnoreCase(DownloadReportsConstant.OUTWARD)) {
				if (criteria.getProcessingStatus() != null
						&& criteria.getProcessingStatus().length() > 0) {
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.PROCESSED.getStatus())) {
						fileName = "OutwardInvoiceMangementProcessedReport";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.ERROR.getStatus())) {
						fileName = "OutwardInvoiceMangementErrorReport";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.INFORMATION.getStatus())) {
						fileName = "OutwardInvoiceMangementInformationReport";
					}
				} else {
					reportType = fileName = "OutwardInvoiceMangementAllReport";
				}
			} else {
				if (criteria.getProcessingStatus() != null
						&& criteria.getProcessingStatus().length() > 0) {
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.PROCESSED.getStatus())) {
						fileName = "InwardInvoiceMangementProcessedReport";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.ERROR.getStatus())) {
						fileName = "InwardInvoiceMangementErrorReport";
					}
					if (criteria.getProcessingStatus().equalsIgnoreCase(
							ProcessingStatus.INFORMATION.getStatus())) {
						fileName = "InwardInvoiceMangementInformationReport";
					}
				} else {
					fileName = "InwardInvoiceMangementAllReport";
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
