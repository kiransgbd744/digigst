/**
 * 
 */
package com.ey.advisory.app.data.returns.compliance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceClientEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplainceClientRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplianceRequestRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@Slf4j
@Component("ComplianceHistoryReportServiceImpl")
public class ComplianceHistoryReportServiceImpl
		implements ComplianceHistoryService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	private ReturnComplianceRequestRepository returnComplianceRequestRepository;

	@Autowired
	private ReturnComplainceClientRepository returnComplainceClientRepository;

	@Autowired
	private ComplainceReportServiceImpl complainceReportServiceImpl;


	@Override
	public void generateComplianceHistoryReports(Long reqId) {

		File tempDir = null;
		try {
			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			ReturnComplianceRequestEntity nonCompRequest = returnComplianceRequestRepository
					.findByRequestId(reqId);
			String financialYear = nonCompRequest.getFinancialYear();
			Long entityId = nonCompRequest.getEntityId();
			List<ReturnComplianceClientEntity> childtableentity = returnComplainceClientRepository
					.getGstin(reqId);

			for (ReturnComplianceClientEntity dto : childtableentity) {
				try {
					Gstr2aProcessedDataRecordsReqDto criteria = new Gstr2aProcessedDataRecordsReqDto();
					criteria.setEntity(entityId.toString());
					criteria.setFinancialYear(financialYear);
					criteria.setReturnType(dto.getReturnType());
					
					Map<String, List<String>> dataSecAttrs=new HashMap<>();
		            dataSecAttrs.put(OnboardingConstant.GSTIN, Arrays.asList(dto.getClientGstin()));
		            criteria.setDataSecAttrs(dataSecAttrs);

					List<ReturnComplianceDto> dataresultset = complainceReportServiceImpl
							.dataresultset(criteria);


					String fileName = String.format("%s_%s_%s_%s", "Compliance",
							dto.getClientGstin(), financialYear, dto.getReturnType());
					String excelFilePath = tempDir.getAbsolutePath()
							+ File.separator + fileName + ".xlsx";

					if (dataresultset.size() > 0) {

						writeToExcel(dataresultset, excelFilePath);

						String zipPath = tempDir.getAbsolutePath()
								+ File.separator + fileName + ".zip";

						String uploadedDocName = zipAndUploadVendorExcel(
								excelFilePath, zipPath);

						returnComplainceClientRepository.updateReportStatus(reqId,
								ReportStatusConstants.REPORT_GENERATED,
								uploadedDocName, LocalDateTime.now(), dto.getClientGstin());

					} else {
						returnComplainceClientRepository.updateReportStatus(reqId,
								ReportStatusConstants.NO_DATA_FOUND, null,
								LocalDateTime.now(),  dto.getClientGstin());
					}
				} catch (Exception e) {

					returnComplainceClientRepository.updateReportStatus(reqId,
							ReportStatusConstants.REPORT_GENERATION_FAILED,
							null, EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							dto.getClientGstin());
					String msg = "Exception occued while generating excel file for:"
							+ dto.getClientGstin();
					LOGGER.error(msg, e);
					throw new AppException(msg, e);

				}

			}

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						ReportTypeConstants.COMPLAIN_HISTORY, null, reqId);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"ComplianceHistoryReport");

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				returnComplianceRequestRepository.updateStatus(reqId,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now());
			} else {

				LOGGER.error("No Data found for report id : %d", reqId);
				returnComplianceRequestRepository.updateStatus(reqId,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
			}

		} catch (Exception e) {

			returnComplianceRequestRepository.updateStatus(reqId,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());

			String msg = "Exception occued while generating excel file for reqID"
					+ reqId;
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		} finally {

			if (tempDir != null && tempDir.exists()) {
				try {
					FileUtils.deleteDirectory(tempDir);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								tempDir.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadComplianceHistoryReport")
				.toFile();
	}

	private Workbook writeToExcel(
			List<ReturnComplianceDto> nonComplaintData,
			String excelFilePath) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (nonComplaintData != null && !nonComplaintData.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("comp.report.headers").split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "ComplianceReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "ComplianceHistoryReportServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(nonComplaintData, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					nonComplaintData.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "ComplianceHistoryReportServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}

				workbook.save(excelFilePath, SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	private Map<String, VendorMasterUploadEntity> convert(
			List<VendorMasterUploadEntity> vendorGstinNameVendMasterList) {

		Map<String, VendorMasterUploadEntity> vGstinMap = new HashMap<>();
		vendorGstinNameVendMasterList.forEach(o -> {
			vGstinMap.put((String) o.getVendorGstin(), o);
		});

		return vGstinMap;
	}

	private String zipAndUploadVendorExcel(String excelPath, String zipPath) {

		File vendorCSV = new File(excelPath);

		try (FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
				ZipOutputStream zipOutputStream = new ZipOutputStream(
						fileOutputStream);
				FileInputStream fileInputStream = new FileInputStream(
						excelPath);) {

			ZipEntry zipEntry = new ZipEntry(vendorCSV.getName());
			zipOutputStream.putNextEntry(zipEntry);

			byte[] buf = new byte[1024];
			int bytesRead;

			while ((bytesRead = fileInputStream.read(buf)) > 0) {
				zipOutputStream.write(buf, 0, bytesRead);
			}

			zipOutputStream.closeEntry();

		} catch (IOException ex) {
			String msg = String.format(
					"IO Error while creating the compressed file '%s' : ",
					zipPath);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		File zipFile = new File(zipPath);

		String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
				"ComplianceHistoryReport");

		if (zipFile != null && zipFile.exists()) {
			try {
				FileUtils.forceDelete(zipFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							zipFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						zipFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

		return uploadedDocName;
	}

}
