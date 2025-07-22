package com.ey.advisory.service.vendor.compliance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingRequestDto;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorTableReportDownloadServiceImpl")
public class VendorTableReportDownloadServiceImpl
		implements VendorTableReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("VendorComplianceRatingServiceImpl")
	private VendorComplianceRatingService ratingService;

	@Override
	public void getData(VendorComplianceRatingRequestDto requestDto,
			Long batchId) {
		Workbook workbook = null;
		File tempDir = null;

		try {
			tempDir = createTempDir(batchId);

			LocalDateTime now = LocalDateTime.now();
			String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
			timeMilli = timeMilli.replace(".", "");
			timeMilli = timeMilli.replace("-", "");
			timeMilli = timeMilli.replace(":", "");

			String fileName = null;
			if ("vendor".equalsIgnoreCase(requestDto.getSource())) {
				fileName = "Vendors_Compliance_Table_data";
			} else if ("customer".equalsIgnoreCase(requestDto.getSource())) {
				fileName = "Customers_Compliance_Table_data";

			} else if ("my".equalsIgnoreCase(requestDto.getSource())) {
				fileName = "My_Compliance_Table_data";

			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Get Error Report Details with batchId:'%s'", batchId);
				LOGGER.debug(msg);
			}

			DateTimeFormatter format = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");

			LocalDateTime timeOfGeneration = LocalDateTime.now();
			LocalDateTime convertISDDate = EYDateUtil
					.toISTDateTimeFromUTC(timeOfGeneration);

			String workbookFileName = tempDir.getAbsolutePath() + File.separator
					+ fileName + "_" + batchId + "_"
					+ format.format(convertISDDate) + ".xlsx";
			workbook = ratingService.getComplianceRatingTableReport(requestDto,
					workbookFileName);

			if (workbook != null) {

				String zipFileName = CommonUtility.getReportZipFiles(tempDir,
						null, fileName + "_" + batchId);

				File zipFile = new File(tempDir, zipFileName);

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile,
								ConfigConstants.VENDOR_COMPLIANCE_DOWNLOAD_REPORT);
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(batchId,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);
			} else {
				LOGGER.error("No Data found for report id : {} ", batchId);
				fileStatusDownloadReportRepo.updateStatus(batchId,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
		} catch (Exception ex) {
			String errMsg = String
					.format("Error while creating Error report {} ", batchId);

			LOGGER.error(errMsg, ex);
			fileStatusDownloadReportRepo.updateStatus(batchId,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private static File createTempDir(Long batchId) throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

}
