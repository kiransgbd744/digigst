/**
 * 
 */
package com.ey.advisory.controller.days.revarsal180;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.ITCReversal180ComputeRptDownloadRepository;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.service.days.revarsal180.ITCReversal180ComputeRptDownloadEntity;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class ITCReversal180ReportDownload {

	@Autowired
	@Qualifier("ITCReversal180ComputeRptDownloadRepository")
	ITCReversal180ComputeRptDownloadRepository repo;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@RequestMapping(value = "/ui/itcReversalReportDownload", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr2ReconReports");

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileName = null;
		String docId = null;
		String configId = json.get("configId").getAsString();
		String reportType = json.get("reportType").getAsString();

		reportType = reportType.equalsIgnoreCase(
				"Reversal Report (Payment info. available & Reversal Applicable + Not Applicable)")
						? "Payment Info Available - Reversal and Not Applicable"
						: reportType;

		if (reportType != null && !reportType.isEmpty()) {

			ITCReversal180ComputeRptDownloadEntity entity = repo
					.findBycomputeId(Long.valueOf(configId), reportType);
			fileName = entity != null ? entity.getFilePath() : null;
			docId = entity != null ? entity.getDocId() : null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ITCReversal180ReportDownload ReportType {}, "
						+ "file path {} : ", reportType, fileName);
			}
		}

		if (fileName == null) {
			return;
		}

		String fileFolder = APIConstants.ITC_REVESAL_180_FOLDER;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Downloading Document with fileName : %s and Folder Name: %s",
						fileName, fileFolder);
				LOGGER.debug(msg);
			}
			Document document = null;
			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"ITCReversal180ReportDownload ReportType {}, "
								+ "file path {}, document{} : ",
						reportType, fileName, document);
			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"ITCReversal180ReportDownload ReportType {}, "
								+ "file path {}, document {}, inputStream {} : ",
						reportType, fileName, document, inputStream);
			}
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = " + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"ITCReversal180ReportDownload ReportType {}, "
									+ "file path {}, document {}, inputStream {}, "
									+ "outputStream {} : ",
							reportType, fileName, document, inputStream,
							outputStream);
				}
			}

	}

}
