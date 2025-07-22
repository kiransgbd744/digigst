package com.ey.advisory.service.vendor.compliance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationService;
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
@Component("VendorComplianceReportDownloadServiceImpl")
public class VendorComplianceReportDownloadServiceImpl
		implements VendorComplianceReportDownloadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationService nonComplaintVendorCommunicationServiceImpl;

	@Override
	public void getData(Long batchId, String reportType, String financialYear) {
		Workbook workbook = null;
		File tempDir = null;
		try {
			tempDir = createTempDir(batchId);
		} catch (IOException e) {
			LOGGER.error("error while creating Temp Dir");
			throw new AppException();
		}

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Get Error Report Details with batchId:'%s'", batchId);
				LOGGER.debug(msg);
			}
			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(batchId);

			FileStatusDownloadReportEntity entity = optEntity.get();
			workbook = nonComplaintVendorCommunicationServiceImpl
					.getNonComplaintVendorReport(financialYear, reportType,
							entity.getEntityId(),tempDir,batchId);

			if (workbook != null) {

				String zipFileName = CommonUtility.getReportZipFiles(tempDir,
						null, "Vendors_Compliance_Report_FY-" + financialYear
								+ "_" + batchId);

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
