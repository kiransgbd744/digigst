package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Dropped2A6ARecordsReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gsr2ReverseChargeRegisterReports;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2CdnrInvRefRegGstin2ARecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2CdnrInvRefRegGstinPRRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2ConsolidatedPRRegister;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2DocumentNumberMismatch2Report;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2DroppedPRRecordsReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2ForceMatchRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2FuzzyMatchRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2LockedCFSNAmendedRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2LogicalMatchReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2MasterVendorGSTINWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2MasterVendorPANWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2Potential2MismatchReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2RecipientGSTINPeriodWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2RecipientGSTINWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2ReconItcTrackingReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SummaryCalenderPeriodRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SummaryTaxPeriodRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SupplierGSTINSummaryRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SupplierPANSummaryRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2TimeStampReport;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2VendorGSTINPeriodWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2VendorGSTINWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2VendorPANPeriodWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2VendorPANWiseRecords;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.ImpgReconReport;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateReconFetchReportDetailsImpl")
public class Gstr2InitiateReconFetchReportDetailsImpl
		implements Gstr2InitiateReconFetchReportDetails {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2ForceMatchRecords")
	Gstr2ForceMatchRecords fmRecords;

	@Autowired
	@Qualifier("Gstr2FuzzyMatchRecords")
	Gstr2FuzzyMatchRecords fuzzyRecords;

	@Autowired
	@Qualifier("Gstr2CdnrInvRefRegGstinPRRecords")
	Gstr2CdnrInvRefRegGstinPRRecords cdrnPRRecords;

	@Autowired
	@Qualifier("Gstr2CdnrInvRefRegGstin2ARecords")
	Gstr2CdnrInvRefRegGstin2ARecords cdrnA2Records;

	@Autowired
	@Qualifier("Gstr2RecipientGSTINPeriodWiseRecords")
	Gstr2RecipientGSTINPeriodWiseRecords gspwRecords;

	@Autowired
	@Qualifier("Gstr2RecipientGSTINWiseRecords")
	Gstr2RecipientGSTINWiseRecords gstinWiseRecords;

	@Autowired
	@Qualifier("Gstr2SummaryCalenderPeriodRecords")
	Gstr2SummaryCalenderPeriodRecords summyCalRecords;

	@Autowired
	@Qualifier("Gstr2SummaryTaxPeriodRecords")
	Gstr2SummaryTaxPeriodRecords summyTaxPeriodRecods;

	@Autowired
	@Qualifier("Gstr2SupplierGSTINSummaryRecords")
	Gstr2SupplierGSTINSummaryRecords supplierGstinSumryRecods;

	@Autowired
	@Qualifier("Gstr2SupplierPANSummaryRecords")
	Gstr2SupplierPANSummaryRecords supplierPanSumryRecods;

	@Autowired
	@Qualifier("Gstr2VendorGSTINPeriodWiseRecords")
	Gstr2VendorGSTINPeriodWiseRecords vendorGstinPeriodWiseRecords;

	@Autowired
	@Qualifier("Gstr2VendorGSTINWiseRecords")
	Gstr2VendorGSTINWiseRecords vendorGstinWiseRecords;

	@Autowired
	@Qualifier("Gstr2VendorPANPeriodWiseRecords")
	Gstr2VendorPANPeriodWiseRecords vendorPanPeriodWiseRecords;

	@Autowired
	@Qualifier("Gstr2VendorPANWiseRecords")
	Gstr2VendorPANWiseRecords vendorPanWiseRecords;

	@Autowired
	@Qualifier("Gstr2MasterVendorPANWiseRecords")
	Gstr2MasterVendorPANWiseRecords vendorMasterPanRecords;

	@Autowired
	@Qualifier("Gstr2MasterVendorGSTINWiseRecords")
	Gstr2MasterVendorGSTINWiseRecords vendorMasterGstinRecords;

	@Autowired
	@Qualifier("Gstr2TimeStampReport")
	Gstr2TimeStampReport timeStampReport;

	@Autowired
	@Qualifier("Gstr2ConsolidatedPRRegister")
	Gstr2ConsolidatedPRRegister consolidatedPRRegister;

	@Autowired
	@Qualifier("Gsr2ReverseChargeRegisterReports")
	Gsr2ReverseChargeRegisterReports reverseChargeRegister;

	@Autowired
	@Qualifier("Gstr2DroppedPRRecordsReport")
	Gstr2DroppedPRRecordsReport dropedPrReport;

	@Autowired
	@Qualifier("Gstr2LockedCFSNAmendedRecords")
	Gstr2LockedCFSNAmendedRecords cfsLockReport;

	@Autowired
	@Qualifier("Dropped2A6ARecordsReport")
	Dropped2A6ARecordsReport drop2a6aReport;

	@Autowired
	@Qualifier("Gstr2DocumentNumberMismatch2Report")
	Gstr2DocumentNumberMismatch2Report docNum2Report;

	@Autowired
	@Qualifier("Gstr2LogicalMatchReport")
	Gstr2LogicalMatchReport logicalReport;

	@Autowired
	@Qualifier("Gstr2Potential2MismatchReport")
	Gstr2Potential2MismatchReport potential2Report;

	@Autowired
	@Qualifier("ImpgReconReport")
	ImpgReconReport impgReport;

	@Autowired
	@Qualifier("Gstr2ReconItcTrackingReport")
	Gstr2ReconItcTrackingReport itcTrackingReport;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void getReconReportData(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		Long chunkSize = 10000L;
		Long startId = 0L;
		Long endId = 0L;
		int noOfChunk = 0;
		String msg = null;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Get Recon Report Details with configId:'%s'",
					configId.toString());
			LOGGER.debug(msg);
		}

		String[] reportType = { "Exact Match", "Match With Tolerance",
				"Value Mismatch", "POS Mismatch", "Doc Date Mismatch",
				"Doc Type Mismatch", "Doc No Mismatch I", "Multi-Mismatch",
				"Potential-I", "Addition in PR", "Addition in 2A_6A",
				"Consolidated PR 2A_6A Report" };

		for (int i = 0; i < reportType.length; i++) {

			tempDir = createTempDir(configId, reportType[i]);

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(reportType[i]) + ".csv";

			Gstr2ReconAddlReportsEntity chunkDetails = addlReportRepo
					.getChunckSizeforReportType(configId, reportType[i]);

			noOfChunk = chunkDetails.getChunkNums() != null
					? chunkDetails.getChunkNums().intValue() : 0;

			if (noOfChunk != 0) {

				startId = chunkDetails.getStartChunk() != null
						? chunkDetails.getStartChunk() : 0L;

				endId = chunkDetails.getEndChunk() != null
						? chunkDetails.getEndChunk() : 0L;

				chunkSize = ((chunkSize > startId) && (chunkSize > endId))
						? endId : startId + 10000L;

				chunkSize = (chunkSize > endId) ? endId : startId + 10000L;

				msg = String.format(
						"Get Recon Report Details with "
								+ "configId:'%s' Before proc call startId %d, "
								+ "endId %d, chunkSize %d ,reportType %s ",
						configId.toString(), startId, endId, chunkSize,
						reportType[i]);
				LOGGER.error(msg);

				try {

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Config ID is '%s',"
										+ " Created temporary directory to generate "
										+ "zip file: %s",
								configId.toString(), tempDir.getAbsolutePath());
						LOGGER.debug(msg);
					}
				} catch (Exception ex) {
					updateReconConfigStatus(
							ReconStatusConstants.REPORT_GENERATION_FAILED,
							configId);
					LOGGER.error(
							"Exception while creating temp Directory for config id {}",
							configId, ex);
				}

				// Save or Update file name and download flag
				addlReportRepo.updateAndSave(configId, reportType[i]);
				int j = 0;

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplate = commonUtility.getProp(
							"gstr2.initiate.recon.report.header.template");
					writer.append(invoiceHeadersTemplate);
					String[] columnMappings = commonUtility
							.getProp(
									"gstr2.initiate.recon.report.column.mapping")
							.split(",");

					while (j < noOfChunk) {
						j++;

						StoredProcedureQuery storedProc = entityManager
								.createStoredProcedureQuery(
										"USP_RECON_RPT_MASTER");

						storedProc.registerStoredProcedureParameter(
								"P_RECON_REPORT_CONFIG_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
								configId);

						storedProc.registerStoredProcedureParameter(
								"P_REPORT_TYPE", String.class,
								ParameterMode.IN);

						storedProc.setParameter("P_REPORT_TYPE", reportType[i]);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format("Query for Recon Reports: '%s'",
									reportType[i]);
							LOGGER.debug(msg);
						}

						// startId
						storedProc.registerStoredProcedureParameter(
								"P_RECON_LINK_ST_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_RECON_LINK_ST_ID", startId);

						// EndId
						storedProc.registerStoredProcedureParameter(
								"P_RECON_LINK_END_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_RECON_LINK_END_ID",
								chunkSize);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with "
											+ "params {} Config ID is '%s', "
											+ "reportType is %s, startId is %d,"
											+ " chunksize is %d ",
									configId.toString(), reportType[i], startId,
									chunkSize);
							LOGGER.debug(msg);
						}

						startId = chunkSize + 1L;
						Long tempValue = (endId - startId) > 10000 ? 10000
								: (endId - startId);

						LOGGER.error("tempValue", tempValue);
						chunkSize = startId + tempValue;
						LOGGER.error("chunkSize after proc call", chunkSize);

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> records = storedProc.getResultList();
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data"
											+ " from DB is '%d' millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									dbLoadTimeDiff, reportType[i],
									records.size());
							LOGGER.debug(msg);
						}

						if (records != null && !records.isEmpty()) {

							List<Gstr2InitiateReconReportDto> reconDataList = new ArrayList<>();

							reconDataList = records.stream()
									.map(o -> convert(o)).collect(Collectors
											.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Report Name and row count: '%s' - '%s'",
										reportType[i], reconDataList.size());
								LOGGER.debug(msg);
							}
							if (reconDataList != null
									&& !reconDataList.isEmpty()) {

								ColumnPositionMappingStrategy<Gstr2InitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(
										Gstr2InitiateReconReportDto.class);
								mappingStrategy
										.setColumnMapping(columnMappings);
								StatefulBeanToCsvBuilder<Gstr2InitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv<Gstr2InitiateReconReportDto> beanWriter = builder
										.withMappingStrategy(mappingStrategy)
										.withSeparator(
												CSVWriter.DEFAULT_SEPARATOR)
										.withLineEnd(CSVWriter.DEFAULT_LINE_END)
										.withEscapechar(
												CSVWriter.DEFAULT_ESCAPE_CHARACTER)
										.build();
								long generationStTime = System
										.currentTimeMillis();
								beanWriter.write(reconDataList);
								long generationEndTime = System
										.currentTimeMillis();
								long generationTimeDiff = (generationEndTime
										- generationStTime);
								if (LOGGER.isDebugEnabled()) {
									msg = String.format(
											"Total Time taken to"
													+ " Generate the report is '%d' "
													+ "millisecs,"
													+ " Report Name and Data count:"
													+ " '%s' - '%s'",
											generationTimeDiff, reportType[i],
											records.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);
				} catch (Exception ex) {
					LOGGER.error("Exception while executing the query for "
							+ "Report Type :{}", reportType[i], ex);
					updateReconConfigStatus(
							ReconStatusConstants.REPORT_GENERATION_FAILED,
							configId);
					ex.printStackTrace();
					throw new AppException(ex);
				}
			}
			// Zipping
			String zipFileName = null;
			if (tempDir.list().length > 0) {
				zipFileName = combineAndZipCsvFiles.zipfolder(configId, tempDir,
						reportType[i]);

				File zipFile = new File(tempDir, zipFileName);

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, zipFileName);
					LOGGER.debug(msg);
				}

				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"Gstr2ReconReports");
				addlReportRepo.updateReconFilePath(uploadedDocName, configId,
						reportType[i]);

			}
			// deleting dir
			deleteTemporaryDirectory(tempDir);
		}

		// Invoking for additional reports

		List<String> addnReportList = addlReportRepo
				.getAddlnReportTypeList(configId);

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"invoked all selected additional "
							+ " reports name %s for configId %d ",
					addlReportRepo, configId);
			LOGGER.debug(msg);
		}

		if (addnReportList.contains("Force_Match_Records"))
			fmRecords.getForceMatchRecords(configId);

		/*
		 * if (addnReportList.contains("Fuzzy_Match_Records"))
		 * fuzzyRecords.getFuzzyMatchRecords(configId);
		 */

		if (addnReportList.contains("CRD-INV_Ref_Reg_GSTR_2A_Records"))
			cdrnA2Records.getCdnrInvRefRegGstin2ARecords(configId);

		if (addnReportList.contains("CRD-INV_Ref_Reg_PR_Records"))
			cdrnPRRecords.getCdnrInvRefRegGstinPRRecords(configId);

		if (addnReportList.contains("Recipient_GSTIN_Period_Wise_Record"))
			gspwRecords.getRecipientGSTINPeriodWiseRecords(configId);

		if (addnReportList.contains("Recipient_GSTIN_Wise_Records"))
			gstinWiseRecords.getRecipientGSTINWiseRecords(configId);

		if (addnReportList.contains("Summary_CalendarPeriod_Records"))
			summyCalRecords.getSummaryCalendarPeriodRecords(configId);

		if (addnReportList.contains("Summary_TaxPeriod_Record"))
			summyTaxPeriodRecods.getSummaryTaxPeriodRecords(configId);

		if (addnReportList.contains("Supplier_GSTIN_Summary_Records"))
			supplierGstinSumryRecods.getSupplierGSTINSummaryRecords(configId);

		if (addnReportList.contains("Supplier_PAN_Summary_Records"))
			supplierPanSumryRecods.getSupplierPANSummaryRecords(configId);

		if (addnReportList.contains("Vendor_GSTIN_Period_Wise_Records"))
			vendorGstinPeriodWiseRecords
					.getVendorGSTINPeriodWiseRecords(configId);

		if (addnReportList.contains("Vendor_GSTIN_Wise_Records"))
			vendorGstinWiseRecords.getVendorGSTINWiseRecords(configId);

		if (addnReportList.contains("Vendor_PAN_Period_Wise_Records"))
			vendorPanPeriodWiseRecords.getVendorPANPeriodWiseRecords(configId);

		if (addnReportList.contains("Vendor_PAN_Wise_Records"))
			vendorPanWiseRecords.getVendorPANWiseRecords(configId);

		if (addnReportList.contains("Vendor_Records_PAN"))
			vendorMasterPanRecords.getMasterVendorPANWiseRecords(configId);

		if (addnReportList.contains("Vendor_Records_GSTIN"))
			vendorMasterGstinRecords.getMasterVendorGSTINWiseRecords(configId);

		if (addnReportList.contains("GSTR_2A_Time_Stamp_Report"))
			timeStampReport.getGstr2TimeStampReport(configId);

		if (addnReportList.contains("Consolidated_PR_Register"))
			consolidatedPRRegister.getGstr2ConsolidatedPRRegister(configId);

		if (addnReportList.contains("Reverse_Charge_Register"))
			reverseChargeRegister.getGsr2ReverseChargeRegisterReports(configId);

		if (addnReportList.contains("Dropped_PR_Records_Report"))
			dropedPrReport.getGstr2DroppedPRRecordsReport(configId);

		if (addnReportList.contains("Locked_CFS_N_Amended_Records"))
			cfsLockReport.getGstr2LockedCFSNAmendedRecords(configId);

		if (addnReportList.contains("Dropped 2A_6A Records Report"))
			drop2a6aReport.getGstr2Dropped2A6ARecordsReport(configId);

		if (addnReportList.contains("Doc No Mismatch II"))
			docNum2Report.getDocNum2MatchReport(configId);

		if (addnReportList.contains("Logical Match"))
			logicalReport.getLogicalMatchReport(configId);

		if (addnReportList.contains("Potential-II"))
			potential2Report.getDocNum2MatchReport(configId);

		if (addnReportList.contains("Consolidated IMPG Report"))
			impgReport.getGstr2ImpgReconReport(configId);

		if (addnReportList.contains("ITC Tracking Report"))
			itcTrackingReport.getGstr2ReconItcTrackingReport(configId);

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Generated all selected additional "
							+ " reports [name] %s for configId %d ",
					addlReportRepo, configId);
			LOGGER.debug(msg);
		}

		/*
		 * //common files Zipping String zipFileName = null; if (tempDir != null
		 * && tempDir.list() != null && tempDir.list().length > 0) { zipFileName
		 * = combineAndZipCsvFiles .zipAllfolder(configId, tempDir);
		 * 
		 * if (LOGGER.isDebugEnabled()) { String msg =
		 * String.format("Before uploading Zip Outer tempDir " +
		 * "Name %s and ZipFileName %s ",tempDir, zipFileName);
		 * LOGGER.debug(msg); }
		 * 
		 * File zipFile = new File(tempDir, zipFileName);
		 * 
		 * String uploadedDocName = DocumentUtility .uploadZipFile(zipFile,
		 * "Gstr2ReconReports");
		 * reconConfigRepo.updateReconConfigStatusAndReportName(
		 * ReconStatusConstants.REPORT_GENERATED, uploadedDocName,
		 * EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);
		 * 
		 * }
		 */
		reconConfigRepo.updateReconConfigStatusAndReportName(
				ReconStatusConstants.REPORT_GENERATED, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
				configId);

		deleteTemporaryDirectory(tempDir);
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
				LOGGER.error(msg);
				throw new AppException(msg, e);
			}
		}
	}

	private void deleteTemporaryDirectory(File tempFile) {

		if (tempFile != null && tempFile.exists()) {
			try {
				FileUtils.deleteDirectory(tempFile);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Deleted the Temp directory/Folder '%s'",
							tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format(
						"Failed to remove the temp "
								+ "directory created for zip: '%s'. This will "
								+ "lead to clogging of disk space.",
						tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private void updateReconConfigStatus(String status, Long configId) {
		reconConfigRepo.updateReconConfigStatusAndReportName(status, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
				configId);
	}

	private Gstr2InitiateReconReportDto convert(Object[] arr) {

		Gstr2InitiateReconReportDto obj = new Gstr2InitiateReconReportDto();

		obj.setITCEntitlement((arr[0] != null) ? arr[0].toString() : null);
		obj.setPreviousReportType2A(
				(arr[1] != null) ? arr[1].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[2] != null) ? arr[2].toString() : null);
		obj.setMismatchReason((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocumentNumberPR(
				(arr[4] != null) ? "`".concat(arr[4].toString()) : null);
		obj.setDocumentNumber2A(
				(arr[5] != null) ? "`".concat(arr[5].toString()) : null);
		obj.setRecipientGstinPR((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecipientGstin2A((arr[6] != null) ? arr[6].toString() : null);
		obj.setDocType2A((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocTypePR((arr[9] != null) ? arr[9].toString() : null);
		obj.setDocumentDate2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setDocumentDatePR((arr[11] != null) ? arr[11].toString() : null);
		obj.setTaxableValue2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setTaxableValuePR((arr[13] != null) ? arr[13].toString() : null);
		obj.setCGST2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setCGSTPR((arr[15] != null) ? arr[15].toString() : null);
		obj.setSGST2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setSGSTPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setIGST2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setIGSTPR((arr[19] != null) ? arr[19].toString() : null);
		obj.setCess2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setCessPR((arr[21] != null) ? arr[21].toString() : null);
		obj.setPos2A((arr[22] != null) ? "`".concat(arr[22].toString()) : null);
		obj.setPosPR((arr[23] != null) ? "`".concat(arr[23].toString()) : null);
		obj.setMatchingScoreOutof11(
				(arr[24] != null) ? (arr[24].toString()) : null);
		obj.setTaxPeriod2A(
				(arr[25] != null) ? "`".concat(arr[25].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[26] != null) ? "`".concat(arr[26].toString()) : null);
		obj.setCalendarMonth(
				(arr[27] != null) ? "`".concat(arr[27].toString()) : null);
		obj.setSupplierName2A((arr[28] != null) ? arr[28].toString() : null);
		obj.setSupplierNamePR((arr[29] != null) ? arr[29].toString() : null);
		obj.setTotalTax2A((arr[30] != null) ? arr[30].toString() : null);
		obj.setTotalTaxPR((arr[31] != null) ? arr[31].toString() : null);
		obj.setInvoiceValue2A((arr[32] != null) ? arr[32].toString() : null);
		obj.setInvoiceValuePR((arr[33] != null) ? arr[33].toString() : null);
		obj.setAvailableIGST((arr[34] != null) ? arr[34].toString() : null);
		obj.setAvailableCGST((arr[35] != null) ? arr[35].toString() : null);
		obj.setAvailableSGST((arr[36] != null) ? arr[36].toString() : null);
		obj.setAvailableCESS((arr[37] != null) ? arr[37].toString() : null);
		obj.setTableType2A((arr[38] != null) ? arr[38].toString() : null);
		obj.setGSTR1FilingStatus((arr[39] != null) ? arr[39].toString() : null);
		// obj.setCFSFlag2A((arr[39] != null) ? arr[39].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setDifferentialPercentage2A(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[42] != null) ? arr[42].toString() : null);
		obj.setOrgDocNumber2A((arr[43] != null) ? arr[43].toString() : null);
		obj.setOrgDocNumberPR((arr[44] != null) ? arr[44].toString() : null);
		obj.setOrgDocDate2A((arr[45] != null) ? arr[45].toString() : null);
		obj.setOrgDocDatePR((arr[46] != null) ? arr[46].toString() : null);
		obj.setSupplierGstinPR((arr[47] != null) ? arr[47].toString() : null);
		obj.setUserID((arr[48] != null) ? arr[48].toString() : null);
		obj.setSourceFileName((arr[49] != null) ? arr[49].toString() : null);
		obj.setProfitCentre((arr[50] != null) ? arr[50].toString() : null);
		obj.setPlant((arr[51] != null) ? arr[51].toString() : null);
		obj.setDivision((arr[52] != null) ? arr[52].toString() : null);
		obj.setLocation((arr[53] != null) ? arr[53].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setUserAccess1((arr[55] != null) ? arr[55].toString() : null);
		obj.setUserAccess2((arr[56] != null) ? arr[56].toString() : null);
		obj.setUserAccess3((arr[57] != null) ? arr[57].toString() : null);
		obj.setUserAccess4((arr[58] != null) ? arr[58].toString() : null);
		obj.setUserAccess5((arr[59] != null) ? arr[59].toString() : null);
		obj.setUserAccess6((arr[60] != null) ? arr[60].toString() : null);
		obj.setGLCodeTaxableValue(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setGLCodeIGST((arr[62] != null) ? arr[62].toString() : null);
		obj.setGLCodeCGST((arr[63] != null) ? arr[63].toString() : null);
		obj.setGLCodeSGST((arr[64] != null) ? arr[64].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[65] != null) ? arr[65].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setGLCodeStateCess((arr[67] != null) ? arr[67].toString() : null);
		obj.setSupplyTypePR((arr[68] != null) ? arr[68].toString() : null);
		obj.setCRDRPreGSTPR((arr[69] != null) ? arr[69].toString() : null);
		obj.setSupplierType((arr[70] != null) ? arr[70].toString() : null);
		obj.setSupplierCode((arr[71] != null) ? arr[71].toString() : null);
		obj.setSupplierAddress1((arr[72] != null) ? arr[72].toString() : null);
		obj.setSupplierAddress2((arr[73] != null) ? arr[73].toString() : null);
		obj.setSupplierAddress3((arr[74] != null) ? arr[74].toString() : null);
		obj.setSupplierAddress4((arr[75] != null) ? arr[75].toString() : null);
		obj.setStateApplyingCess((arr[76] != null) ? arr[76].toString() : null);
		obj.setPortCodePR((arr[77] != null) ? arr[77].toString() : null);
		obj.setBillOfEntryPR((arr[78] != null) ? arr[78].toString() : null);
		obj.setBillOfEntryDatePR((arr[79] != null) ? arr[79].toString() : null);
		obj.setCIFValue((arr[80] != null) ? arr[80].toString() : null);
		obj.setCustomDuty((arr[81] != null) ? arr[81].toString() : null);
		obj.setQuantity((arr[82] != null) ? arr[82].toString() : null);
		obj.setAdvaloremCessAmount(
				(arr[83] != null) ? arr[83].toString() : null);
		obj.setSpecificCessAmount(
				(arr[84] != null) ? arr[84].toString() : null);
		obj.setStateCessAmount((arr[85] != null) ? arr[85].toString() : null);
		obj.setOtherValue((arr[86] != null) ? arr[86].toString() : null);
		obj.setPurchaseVoucherDate(
				(arr[87] != null) ? arr[87].toString() : null);
		obj.setPurchaseVoucherNumber(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setPostingDate((arr[89] != null) ? arr[89].toString() : null);
		obj.setPaymentVoucherNumber(
				(arr[90] != null) ? arr[90].toString() : null);
		obj.setPaymentDate((arr[91] != null) ? arr[91].toString() : null);
		obj.setContractNumber((arr[92] != null) ? arr[92].toString() : null);
		obj.setContractValue((arr[93] != null) ? arr[93].toString() : null);
		obj.setEWayBillNumber((arr[94] != null) ? arr[94].toString() : null);
		obj.setEWayBillDate((arr[95] != null) ? arr[95].toString() : null);
		obj.setUserDefinedField1((arr[96] != null) ? arr[96].toString() : null);
		obj.setUserDefinedField2((arr[97] != null) ? arr[97].toString() : null);
		obj.setUserDefinedField3((arr[98] != null) ? arr[98].toString() : null);
		obj.setUserDefinedField4((arr[99] != null) ? arr[99].toString() : null);
		obj.setUserDefinedField5(
				(arr[100] != null) ? arr[100].toString() : null);
		obj.setUserDefinedField6(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setUserDefinedField7(
				(arr[102] != null) ? arr[102].toString() : null);
		obj.setUserDefinedField8(
				(arr[103] != null) ? arr[103].toString() : null);
		obj.setUserDefinedField9(
				(arr[104] != null) ? arr[104].toString() : null);
		obj.setUserDefinedField10(
				(arr[105] != null) ? arr[105].toString() : null);
		obj.setUserDefinedField11(
				(arr[106] != null) ? arr[106].toString() : null);
		obj.setUserDefinedField12(
				(arr[107] != null) ? arr[107].toString() : null);
		obj.setUserDefinedField13(
				(arr[108] != null) ? arr[108].toString() : null);
		obj.setUserDefinedField14(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setUserDefinedField15(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setMatchingID((arr[111] != null) ? arr[111].toString() : null);
		obj.setRequestID(
				(arr[112] != null) ? "`".concat(arr[112].toString()) : null);
		obj.setIDPR((arr[113] != null) ? arr[113].toString() : null);
		obj.setID2A((arr[114] != null) ? arr[114].toString() : null);
		obj.setInvoiceKeyPR((arr[115] != null) ? arr[115].toString() : null);
		obj.setInvoiceKeyA2((arr[116] != null) ? arr[116].toString() : null);
		obj.setSupplierGstin2A((arr[117] != null) ? arr[117].toString() : null);
		obj.setTaxPeriodforGSTR3B(
				(arr[118] != null) ? "`".concat(arr[118].toString()) : null);
		obj.setReportType((arr[119] != null) ? arr[119].toString() : null);
		obj.setGSTPercent2A((arr[120] != null) ? arr[120].toString() : null);
		obj.setGSTPercentPR((arr[121] != null) ? arr[121].toString() : null);
		obj.setReverseChargeFlag2A(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setCRDRPreGST2A((arr[124] != null) ? arr[124].toString() : null);
		obj.setHSNorSAC((arr[125] != null) ? arr[125].toString() : null);
		obj.setItemCode((arr[126] != null) ? arr[126].toString() : null);
		obj.setItemDescription((arr[127] != null) ? arr[127].toString() : null);
		obj.setCategoryOfItem((arr[128] != null) ? arr[128].toString() : null);
		obj.setUnitOfMeasurement(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setAdvaloremCessRate(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setStateCessRate((arr[131] != null) ? arr[131].toString() : null);
		obj.setClaimRefundFlag((arr[132] != null) ? arr[132].toString() : null);
		// missing value SpecificCessRate is not coming from db
		obj.setSpecificCessRate(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[135] != null) ? arr[135].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[136] != null) ? arr[136].toString() : null);
		obj.setTaxableValueAdjusted(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				(arr[139] != null) ? arr[139].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				(arr[140] != null) ? arr[140].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				(arr[141] != null) ? arr[141].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				(arr[142] != null) ? arr[142].toString() : null);
		obj.setStateCessAmountAdjusted(
				(arr[143] != null) ? arr[143].toString() : null);
		obj.setEligibilityIndicator(
				(arr[144] != null) ? arr[144].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setITCReversalIdentifier(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setContractDate((arr[148] != null) ? arr[148].toString() : null);
		obj.setReferenceIDPR((arr[149] != null) ? arr[149].toString() : null);
		obj.setReferenceID2A((arr[150] != null) ? arr[150].toString() : null);

		// Suggested Resp
		if ((arr[151] != null && (!arr[151].toString().isEmpty())
				&& (arr[151].toString().matches("[0-9]+")))) {

			obj.setSuggestedFMResponse(arr[151].toString().length() == 5
					? "`".concat(("0").concat(arr[151].toString()))
					: "`".concat((arr[151].toString())));
		} else {
			obj.setSuggestedFMResponse(
					(arr[151] != null) ? (arr[151].toString()) : null);
		}
		obj.setComments((arr[152] != null) ? arr[152].toString() : null);
		obj.setOrgSupplierGstinPR(
				(arr[153] != null) ? arr[153].toString() : null);
		obj.setOrgSupplierNamePR(
				(arr[154] != null) ? arr[154].toString() : null);
		obj.setForceMatchResponse(
				(arr[155] != null) ? "`".concat(arr[155].toString()) : null);

		obj.setGSTR1FilingDate((arr[156] != null) ? arr[156].toString() : null);
		obj.setGSTR1FilingPeriod(
				(arr[157] != null) ? "`".concat(arr[157].toString()) : null);
		obj.setGSTR3BFilingStatus(
				(arr[158] != null) ? arr[158].toString() : null);
		obj.setCancellationDate(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[160] != null) ? "`".concat(arr[160].toString()) : null);
		obj.setOrgAmendmentType(
				(arr[161] != null) ? arr[161].toString() : null);
		obj.setCDNDelinkingFlag(
				(arr[162] != null) ? arr[162].toString() : null);
		obj.setSupplyType2A((arr[163] != null) ? arr[163].toString() : null);

		// newlogic
		obj.setMatchReason((arr[165] != null) ? arr[165].toString() : null);
		obj.setReportCategory((arr[166] != null) ? arr[166].toString() : null);
		obj.setSupplierTradeName2A(
				(arr[167] != null) ? arr[167].toString() : null);
		obj.setBoeReferenceDate(
				(arr[168] != null) ? arr[168].toString() : null);
		obj.setBoeAmended((arr[169] != null) ? arr[169].toString() : null);
		obj.setPortCode2A((arr[170] != null) ? arr[170].toString() : null);
		obj.setBillOfEntry2A((arr[171] != null) ? arr[171].toString() : null);
		obj.setBillOfEntryDate2A(
				(arr[172] != null) ? arr[172].toString() : null);
		// 115-240 format
		obj.setUserDefinedField28(
				(arr[173] != null) ? arr[173].toString() : null);
		obj.setIrn2A((arr[174] != null) ? arr[174].toString() : null);
		obj.setIrnPR((arr[175] != null) ? arr[175].toString() : null);
		obj.setIrnDate2A((arr[176] != null) ? arr[176].toString() : null);
		obj.setIrnDatePR((arr[177] != null) ? arr[177].toString() : null);

		// 2B data in 2A
		obj.setTaxPeriod2B(
				(arr[178] != null) ? "`".concat(arr[178].toString()) : null);
		obj.setItcAvailability2B(
				(arr[179] != null) ? arr[179].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[180] != null) ? arr[180].toString() : null);
		obj.setSourceType((arr[181] != null) ? arr[181].toString() : null);
		obj.setGenerationDate2B(
				(arr[182] != null) ? "`".concat(arr[182].toString()) : null);
		obj.setGenerationDate2A(
				(arr[183] != null) ? "`".concat(arr[183].toString()) : null);

		return obj;

	}

	private static File createTempDir(Long configId, String reportType)
			throws IOException {

		String tempFolderPrefix = "ReconReports" + "_" + reportType + "_"
				+ configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		String fileNameWithTimeStamp = fileName + "_" + timeMilli;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;
	}

}
