package com.ey.advisory.app.services.vendorcomm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.ey.advisory.app.data.entities.client.asprecon.NonCompVendorRequestEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.NonCompVendorVGstinRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NCVendorCommHelperService;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorReportDto;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.app.report.convertor.VendorCommReportConverter;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AsyncNonCompVendorComReportUploadServiceImpl")
public class AsyncNonCompVendorComReportUploadServiceImpl
		implements AsyncNonCompVendorComReportUploadService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("NonCompVendorRequestRepository")
	private NonCompVendorRequestRepository nonCompVendorRequestRepository;

	@Autowired
	@Qualifier("NonCompVendorVGstinRepository")
	private NonCompVendorVGstinRepository nonCompVendorVGstinRepository;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	@Qualifier("VendorCommunicationReportConverterImpl")
	VendorCommReportConverter vendorCommReportConverter;

	@Autowired
	@Qualifier("VendorGstinDetailsRepository")
	private VendorGstinDetailsRepository vendorGstinDetailsRepository;

	@Autowired
	VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("VendorCommDAOImpl")
	private VendorCommDAO vendorCommDAO;

	@Autowired
	private NCVendorCommHelperService helperService;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	private static final String REPORT_TYPE = "Non-Compliant";

	@Override
	public void generateNonComplaintVendorCommReports(Long reqId) {

		File tempDir = null;
		try {
			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Map<String, VendorMasterUploadEntity> vGstinNameVendUploadMap = new HashMap<>();
			NonCompVendorRequestEntity nonCompVendorRequest = nonCompVendorRequestRepository
					.findByRequestId(reqId);
			String financialYear = nonCompVendorRequest.getFinancialYear();
			Long entityId = nonCompVendorRequest.getEntityId();
			List<String> vendorGstinList = nonCompVendorVGstinRepository
					.getVendorGstin(reqId);

			List<String> recipientPans = entityInfoRepository
					.findPanByEntityId(entityId);

			List<VendorMasterUploadEntity> uploadEntities = vendorMasterUploadEntityRepository
					.findByIsDeleteFalseAndRecipientPANInAndVendorGstinInAndIsNonComplaintComTrue(
							recipientPans, vendorGstinList);
			vGstinNameVendUploadMap = convert(uploadEntities);

			for (String vGstin : vendorGstinList) {
				try {
					VendorMasterUploadEntity vendorEntity = vGstinNameVendUploadMap
							.get(vGstin);

					if (!Strings.isNullOrEmpty(vendorEntity.getVendorName())) {
						vendorEntity.setVendorName(
								vendorEntity.getVendorName().replace(".", "_"));
					}
					List<VendorMasterUploadEntity> vendorUploadEntity = new ArrayList<>();
					vendorUploadEntity.add(vendorEntity);

					List<NonComplaintVendorReportDto> nonVendorReports = helperService
							.getOverallNonComplaintsDataBasedOnGstin(
									vendorUploadEntity, financialYear,
									REPORT_TYPE);

					String returnType = nonCompVendorVGstinRepository
							.getReturnType(reqId, vGstin);

					String fileName = String.format("%s_%s_%s_%s",
							"NonCompliant", vGstin, financialYear, returnType);
					String excelFilePath = tempDir.getAbsolutePath()
							+ File.separator + fileName + ".xlsx";

					if (nonVendorReports.size() > 0) {
						nonVendorReports.forEach(dto -> {
							if (dto.getReturnType().equalsIgnoreCase("ITC04")) {
								dto.setTaxPeriod(itc04TaxPeriod(dto.getTaxPeriod()));
							} else {
								dto.setTaxPeriod(dto.getTaxPeriod());
							}
						});
						writeToExcel(nonVendorReports, excelFilePath);

						String zipPath = tempDir.getAbsolutePath()
								+ File.separator + fileName + ".zip";

						String uploadedDocName = zipAndUploadVendorExcel(
								excelFilePath, zipPath);

						nonCompVendorVGstinRepository.updateReportStatus(reqId,
								ReportStatusConstants.REPORT_GENERATED,
								uploadedDocName, LocalDateTime.now(), vGstin);

					} else {
						nonCompVendorVGstinRepository.updateReportStatus(reqId,
								ReportStatusConstants.NO_DATA_FOUND, null,
								LocalDateTime.now(), vGstin);
					}
				} catch (Exception e) {

					nonCompVendorVGstinRepository.updateReportStatus(reqId,
							ReportStatusConstants.REPORT_GENERATION_FAILED,
							null, EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							vGstin);
					String msg = "Exception occued while generating excel file for:"
							+ vGstin;
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
						ReportTypeConstants.NON_COMPLAINT_COM, null, reqId);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"NonCompVendorCommReport");

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				nonCompVendorRequestRepository.updateStatus(reqId,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						LocalDateTime.now());
			} else {

				LOGGER.error("No Data found for report id : %d", reqId);
				nonCompVendorRequestRepository.updateStatus(reqId,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
			}

		} catch (Exception e) {

			nonCompVendorRequestRepository.updateStatus(reqId,
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
		return Files.createTempDirectory("DownloadNonCompVendorReports")
				.toFile();
	}

	private Workbook writeToExcel(
			List<NonComplaintVendorReportDto> nonComplaintData,
			String excelFilePath) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (nonComplaintData != null && !nonComplaintData.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp("itc.noncompliant.vendor.master.process.data")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "Non-CompliantVendorsReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "AsyncNonCompVendorComReportUploadServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(nonComplaintData, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					nonComplaintData.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "AsyncNonCompVendorComReportUploadServiceImpl.writeToExcel "
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
				"NonCompVendorCommReport");

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

	public String itc04TaxPeriod(String month){
		String taxPeriod = "";
		if(month.startsWith("13")){
			taxPeriod = month.replace("13", "Q1");
		} else if(month.startsWith("14")){
			taxPeriod = month.replace("14", "Q2");
		} else if(month.startsWith("15")){
			taxPeriod = month.replace("15", "Q3");
		} else if(month.startsWith("16")){
			taxPeriod = month.replace("16", "Q4");
		} else if(month.startsWith("17")){
			taxPeriod = month.replace("17", "H1");
		} else if(month.startsWith("18")){
			taxPeriod = month.replace("18", "H2");
		} else{
			taxPeriod = month;
		}
			
		return taxPeriod;	
	}
}