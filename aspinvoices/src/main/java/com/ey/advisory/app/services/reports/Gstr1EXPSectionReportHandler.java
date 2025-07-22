package com.ey.advisory.app.services.reports;

import java.io.File;
import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.common.Gstr1CommonUtility;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1EXPSectionReportHandler")
public class Gstr1EXPSectionReportHandler {

	@Autowired
	@Qualifier("Gstr1EXPSectionReportServiceImpl")
	private Gstr1ReviewSummReportsService gstr1ReviewSummReportsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	Gstr1CommonUtility gstr1CommonUtility;

	public Workbook downloadRSProcessedReport(
			Gstr1ReviwSummReportsReqDto criteria, Long id) {

		Pair<String, String> downloadFileDetails = null;
		String folderName = "GSTR1UploadFolder";
		Workbook workbook = null;
		String docId = null;
		String uploadedDocName = null;

		String finalGstinString = "";
		String fileName = null;
		File tempDir = null;
		String reportType = "";

		try {

			if (criteria.getReturnType().equalsIgnoreCase(APIConstants.GSTR1)) {
				reportType = APIConstants.GSTR_1;
			}
			if (criteria.getReturnType()
					.equalsIgnoreCase(APIConstants.GSTR1A)) {
				reportType = APIConstants.GSTR_1A;
			} else {
				// Provide valid return type
			}
			workbook = gstr1ReviewSummReportsService
					.findGstr1ReviewSummRecords(criteria, null);

			if (workbook != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 EXPORTS template file name is : {}",
							workbook.getFileName());
				}

				tempDir = gstr1CommonUtility.createTempDir(id);
				finalGstinString = gstr1CommonUtility
						.getFinalGstinString(criteria);
				fileName = tempDir.getAbsolutePath() + File.separator
						+ reportType + finalGstinString + "_"
						+ criteria.getTaxperiod() + "_EXPORTS_SECTION6A"
						+ ".xlsx";

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Gstr1 EXPORTS file name is : {}", fileName);
				}

				workbook.save(fileName, SaveFormat.XLSX);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " Gstr1 EXPORTS response list in the directory : {}",
							workbook.getAbsolutePath());
				}

				if (id != null) {
					try {

						downloadFileDetails = DocumentUtility
								.uploadDocumentAndReturnDocID(workbook,
										folderName, "XLSX");

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Gstr1EXPSectionReportHandler - "
											+ "Workbook has been generated successfully in and"
											+ "Uploaded in folder name : %s) ",
									folderName);

						}

						uploadedDocName = downloadFileDetails.getValue0();
						docId = downloadFileDetails.getValue1();

						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATED,
								uploadedDocName, LocalDateTime.now(), docId);
						if (LOGGER.isDebugEnabled()) {
							String msg = "Sucessfully uploaded  file and updating the "
									+ "status as 'Report Generated'";
							LOGGER.debug(msg);
						}

					} catch (Exception e) {

						String msg = String
								.format("Gstr1EXPSectionReportHandler - "
										+ "Exception occured while "
										+ "Uploading into DocRepo, %s", e);
						LOGGER.error(msg);
						fileStatusDownloadReportRepo.updateStatus(id,
								ReportStatusConstants.REPORT_GENERATION_FAILED,
								null, LocalDateTime.now(), null);
						throw new AppException(msg);
					}
				}
			} else {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr1 EXPORTS No Data found for report id : {} ",
							id);
				}

				if (id != null) {

					fileStatusDownloadReportRepo.updateStatus(id,
							ReportStatusConstants.NO_DATA_FOUND, null,
							LocalDateTime.now(), null);
				}

			}
		} catch (Exception e) {

			String msg = String.format("Gstr1EXPSectionReportHandler - "
					+ "Exception occured while "
					+ "invoking report service, %s", e);
			LOGGER.error(msg);
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), null);
			throw new AppException(msg);
		} finally {
			if (tempDir != null)
				GenUtil.deleteTempDir(tempDir);
		}

		return workbook;
	}

}
