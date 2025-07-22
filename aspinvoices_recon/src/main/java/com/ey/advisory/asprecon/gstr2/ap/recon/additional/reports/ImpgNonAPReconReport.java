package com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2ImpgReconDto;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2Recon2APRReportCommonService;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImpgNonAPReconReport")
public class ImpgNonAPReconReport {

	// "Consolidated IMPG Report"

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2Recon2APRReportCommonServiceImpl")
	private Gstr2Recon2APRReportCommonService commonService;

	private static int CSV_BUFFER_SIZE = 8192;

	private static final String CONF_KEY = "gstr2.recon.2bpr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	public void getGstr2ImpgReconReport(Long configId) throws IOException {
		
		String fullPath = null;
		File tempDir = null;
		Long startId = 0L;
		Long endId = 0L;
		int noOfChunk = 0;
		String msg = null;
		Writer writer = null;
		boolean isReportAvailable = false;
		
		if (LOGGER.isDebugEnabled()) {
			 msg = String.format("Get Consolidated IMPG Report Details "
					+ "with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = "Imports SEZG Matching Report";
		String reportTypeUi = "Consolidated IMPG Report";

		tempDir = createTempDir(configId, "Imports SEZG Matching Report");

		tempDir = createTempDir(configId, reportTypeUi);
		
		fullPath = tempDir.getAbsolutePath() + File.separator
				+ getUniqueFileName(reportType) + "_" + 1 + ".csv";

		Gstr2ReconAddlReportsEntity chunkDetails = addlReportRepo
				.getChunckSizeforReportType(configId,reportTypeUi);

		noOfChunk = chunkDetails.getChunkNums() != null
				? chunkDetails.getChunkNums().intValue() : 0;

		Long chunkValue = Long.valueOf(getChunkSize());
		Long chunkSize = Long.valueOf(getChunkSize());

		if (noOfChunk != 0) {

			startId = chunkDetails.getStartChunk() != null
					? chunkDetails.getStartChunk() : 0L;

			endId = chunkDetails.getEndChunk() != null
					? chunkDetails.getEndChunk() : 0L;

			LOGGER.debug("before calc chunkSize {} - ReportType {}", chunkSize,
					reportType);
			chunkSize = ((chunkSize > startId) && (chunkSize > endId)) ? endId
					: startId + (chunkSize - 1);

			LOGGER.debug("after calc chunkSize {} - ReportType {}", chunkSize,
					reportType);

			Pair<Integer, Integer> maxReportSizes = commonService
					.getChunkingSizes();

			msg = String.format(
					"Get Details with "
							+ "configId:'%s' Before proc call startId %d, "
							+ "endId %d, chunkSize %d ,reportType %s ",
					configId.toString(), startId, endId, chunkSize, reportType);
			LOGGER.error(msg);

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				
				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr2.impg.report.header");
				
				writer.append(invoiceHeadersTemplate);
				
				String[] columnMappings = commonUtility
						.getProp("gstr2.impg.report.column").split(",");
				
				StatefulBeanToCsv<Gstr2ImpgReconDto> beanWriter = getBeanWriter(
						columnMappings, writer);
				
				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_AUTO_2APR_RPT_IMPG_MASTER");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
							String.class, ParameterMode.IN);

					storedProc.setParameter("P_REPORT_TYPE",
							"Consolidated IMPG Report");

					// startId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_ST_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_ST_ID", startId);

					// EndId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_END_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_END_ID", chunkSize);
					
					// default value Zero
					storedProc.registerStoredProcedureParameter(
							"P_CHUNK_VALUE", Integer.class,
							ParameterMode.IN);

					storedProc.setParameter("P_CHUNK_VALUE", 0);

					if (LOGGER.isDebugEnabled()) {
						 msg = String.format(
								"call stored proc with "
										+ "params {} Config ID is '%s', "
										+ "reportType is %s, startId is %d, "
										+ "chunksize is %d",
								configId.toString(), "Consolidated IMPG Report",
								startId, chunkSize);
						LOGGER.debug(msg);
					}

					startId = chunkSize + 1L;
					Long tempValue = (endId - startId) > chunkValue
							? chunkValue
							: (endId - startId);

					if (j + 1 == noOfChunk) {
						chunkSize = startId + tempValue;
					} else {
						chunkSize = startId + (tempValue - 1);

					}
					LOGGER.debug("chunkSize after proc call {}", chunkSize);


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
								dbLoadTimeDiff, reportType, records.size());
						LOGGER.debug(msg);
					}

					if (records != null && !records.isEmpty()) {

						isReportAvailable = true;
						List<Gstr2ImpgReconDto> reconDataList = new ArrayList<>();
						reconDataList = records.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, reconDataList.size());
							LOGGER.debug(msg);
						}

						if (count >= maxLimitPerFile) {
							flushWriter(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Invoking 2APR commonService to"
												+ " upload a zip file : "
												+ "configId {}, ReportName"
												+ " {}, ",
										configId, reportType);
							}

							// Zipping
							if (isReportAvailable) {

								commonService.chunkZipFiles(tempDir,
										chunkDetails.getId(), configId,
										reportTypeUi, maxReportSizes.getValue1());
							}

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking 2APR "
										+ "commonService to upload "
										+ "a zip file : configId "
										+ "{}, ReportName {} " + "deleted file "
										+ "from TempDir, ", configId,
										reportType);
							}

							count = 0;
							fileIndex += 1;
							fullPath = tempDir.getAbsolutePath()
									+ File.separator
									+ getUniqueFileName(reportType) + "_"
									+ fileIndex + ".csv";
							writer = new BufferedWriter(
									new FileWriter(fullPath), CSV_BUFFER_SIZE);
							writer.append(invoiceHeadersTemplate);
							beanWriter = getBeanWriter(columnMappings, writer);
						}
						count += reconDataList.size();
						beanWriter.write(reconDataList);

						long generationStTime = System.currentTimeMillis();
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' "
											+ "millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType,
									records.size());
							LOGGER.debug(msg);
						}
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
							commonService.chunkZipFiles(tempDir,
									chunkDetails.getId(), configId, reportTypeUi,
									maxReportSizes.getValue1());
							break;
						}

					}

				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for "
						+ "Report Type :{}", reportType, ex);

				deleteTemporaryDirectory(tempDir);
				throw new AppException(ex);
			}

		}
		//updating flag in download table 
		addlReportRepo.updateIsReportProcExecuted(configId, 
				reportTypeUi);
		deleteTemporaryDirectory(tempDir);
	
	}

	private StatefulBeanToCsv<Gstr2ImpgReconDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr2ImpgReconDto> mappingStrategy = 
				new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr2ImpgReconDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr2ImpgReconDto> builder = 
				new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr2ImpgReconDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
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


	private Gstr2ImpgReconDto convert(Object[] arr) {

		Gstr2ImpgReconDto obj = new Gstr2ImpgReconDto();

		obj.setMatchingScoreOutof8((arr[0] != null) ? arr[0].toString() : null);
		obj.setMatchReason((arr[1] != null) ? arr[1].toString() : null);
		obj.setMismatchReason((arr[2] != null) ? arr[2].toString() : null);
		obj.setReportCategory((arr[3] != null) ? arr[3].toString() : null);
		obj.setReportType((arr[4] != null) ? (arr[4].toString()) : null);
		obj.setTaxPeriod2A(
				(arr[5] != null) ? "`".concat(arr[5].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[6] != null) ? "`".concat(arr[6].toString()) : null);
		obj.setRecipientGSTIN2A((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecipientGSTINPR((arr[8] != null) ? arr[8].toString() : null);
		obj.setSupplierGSTIN2A((arr[9] != null) ? arr[9].toString() : null);
		obj.setSupplierGSTINPR((arr[10] != null) ? arr[10].toString() : null);
		obj.setSupplierTradeName2A(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setSupplierNamePR((arr[12] != null) ? arr[12].toString() : null);
		obj.setPortCode2A((arr[13] != null) ? arr[13].toString() : null);
		obj.setPortCodePR((arr[14] != null) ? arr[14].toString() : null);
		obj.setBillofEntryNo2A((arr[15] != null) ? arr[15].toString() : null);
		obj.setBillofEntryNoPR((arr[16] != null) ? arr[16].toString() : null);
		obj.setBillofEntryDate2A((arr[17] != null) ? arr[17].toString() : null);
		obj.setBillofEntryDatePR((arr[18] != null) ? arr[18].toString() : null);
		obj.setBillofEntryReferenceDate2A(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setBillofEntryCreatedDate2A(
				(arr[20] != null) ? arr[20].toString() : null);
		obj.setBillofEntryAmendedFlag2A(
				(arr[21] != null) ? arr[21].toString() : null);
		obj.setOrgRecipientGSTIN2A(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setOrgPortCode2A((arr[23] != null) ? arr[23].toString() : null);
		obj.setOrgBillofEntryNo2A(
				(arr[24] != null) ? (arr[24].toString()) : null);
		obj.setOrgBillofEntryCreatedDate2A(
				(arr[25] != null) ? arr[25].toString() : null);
		obj.setTaxableValue2A((arr[26] != null) ? arr[26].toString() : null);
		obj.setTaxableValuePR((arr[27] != null) ? arr[27].toString() : null);
		obj.setIGST2A((arr[28] != null) ? arr[28].toString() : null);
		obj.setIGSTPR((arr[29] != null) ? arr[29].toString() : null);
		obj.setCess2A((arr[30] != null) ? arr[30].toString() : null);
		obj.setCessPR((arr[31] != null) ? arr[31].toString() : null);
		obj.setTotalTax2A((arr[32] != null) ? arr[32].toString() : null);
		obj.setTotalTaxPR((arr[33] != null) ? arr[33].toString() : null);
		obj.setInvoiceValuePR((arr[34] != null) ? arr[34].toString() : null);
		obj.setAvailableIGST((arr[35] != null) ? arr[35].toString() : null);
		obj.setAvailableCESS((arr[36] != null) ? arr[36].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[37] != null) ? arr[37].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[38] != null) ? arr[38].toString() : null);
		obj.setOrgDocNumberPR(addApostrophe(arr[39]));
		obj.setOrgDocDatePR((arr[40] != null) ? arr[40].toString() : null);
		obj.setOrgSupplierGSTINPR(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setOrgSupplierNamePR((arr[42] != null) ? arr[42].toString() : null);
		obj.setUserID((arr[43] != null) ? arr[43].toString() : null);
		obj.setSourceFileName((arr[44] != null) ? arr[44].toString() : null);
		obj.setProfitCentre((arr[45] != null) ? arr[45].toString() : null);
		obj.setPlant((arr[46] != null) ? arr[46].toString() : null);
		obj.setDivision((arr[47] != null) ? arr[47].toString() : null);
		obj.setLocation((arr[48] != null) ? arr[48].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[49] != null) ? arr[49].toString() : null);
		obj.setUserAccess1((arr[50] != null) ? arr[50].toString() : null);
		obj.setUserAccess2((arr[51] != null) ? arr[51].toString() : null);
		obj.setUserAccess3((arr[52] != null) ? arr[52].toString() : null);
		obj.setUserAccess4((arr[53] != null) ? arr[53].toString() : null);
		obj.setUserAccess5((arr[54] != null) ? arr[54].toString() : null);
		obj.setUserAccess6((arr[55] != null) ? arr[55].toString() : null);
		obj.setGLCodeTaxableValue(
				(arr[56] != null) ? arr[56].toString() : null);
		obj.setGLCodeIGST((arr[57] != null) ? arr[57].toString() : null);
		obj.setGLCodeCGST((arr[58] != null) ? arr[58].toString() : null);
		obj.setGLCodeSGST((arr[59] != null) ? arr[59].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[60] != null) ? arr[60].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setGLCodeStateCess((arr[62] != null) ? arr[62].toString() : null);
		obj.setSupplierType((arr[63] != null) ? arr[63].toString() : null);
		obj.setSupplierCode((arr[64] != null) ? arr[64].toString() : null);
		obj.setSupplierAddress1((arr[65] != null) ? arr[65].toString() : null);
		obj.setSupplierAddress2((arr[66] != null) ? arr[66].toString() : null);
		obj.setSupplierAddress3((arr[67] != null) ? arr[67].toString() : null);
		obj.setSupplierAddress4((arr[68] != null) ? arr[68].toString() : null);
		obj.setStateApplyingCess((arr[69] != null) ? arr[69].toString() : null);
		obj.setCIFValue((arr[70] != null) ? arr[70].toString() : null);
		obj.setCustomDuty((arr[71] != null) ? arr[71].toString() : null);
		obj.setHSNorSAC((arr[72] != null) ? arr[72].toString() : null);
		obj.setItemCode((arr[73] != null) ? arr[73].toString() : null);
		obj.setItemDescription((arr[74] != null) ? arr[74].toString() : null);
		obj.setCategoryOfItem((arr[75] != null) ? arr[75].toString() : null);
		obj.setUnitOfMeasurement((arr[76] != null) ? arr[76].toString() : null);
		obj.setQuantity((arr[77] != null) ? arr[77].toString() : null);
		obj.setAdvaloremCessRate((arr[78] != null) ? arr[78].toString() : null);
		obj.setAdvaloremCessAmount(
				(arr[79] != null) ? arr[79].toString() : null);
		obj.setSpecificCessRate((arr[80] != null) ? arr[80].toString() : null);
		obj.setSpecificCessAmount(
				(arr[81] != null) ? arr[81].toString() : null);
		obj.setStateCessRate((arr[82] != null) ? arr[82].toString() : null);
		obj.setStateCessAmount((arr[83] != null) ? arr[83].toString() : null);
		obj.setOtherValue((arr[84] != null) ? arr[84].toString() : null);
		obj.setClaimRefundFlag((arr[85] != null) ? arr[85].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[86] != null) ? arr[86].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[87] != null) ? arr[87].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setTaxableValueAdjusted(
				(arr[89] != null) ? arr[89].toString() : null);
		obj.setIntegratedTaxAmountAdjusted(
				(arr[90] != null) ? arr[90].toString() : null);
		obj.setCentralTaxAmountAdjusted(
				(arr[91] != null) ? arr[91].toString() : null);
		obj.setStateUTTaxAmountAdjusted(
				(arr[92] != null) ? arr[92].toString() : null);
		obj.setAdvaloremCessAmountAdjusted(
				(arr[93] != null) ? arr[93].toString() : null);
		obj.setSpecificCessAmountAdjusted(
				(arr[94] != null) ? arr[94].toString() : null);
		obj.setStateCessAmountAdjusted(
				(arr[95] != null) ? arr[95].toString() : null);
		obj.setEligibilityIndicator(
				(arr[96] != null) ? arr[96].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[97] != null) ? arr[97].toString() : null);
		obj.setITCEntitlement((arr[98] != null) ? arr[98].toString() : null);
		obj.setITCReversalIdentifier(
				(arr[99] != null) ? arr[99].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[100] != null) ? arr[100].toString() : null);
		obj.setPurchaseVoucherNumber(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setPurchaseVoucherDate(
				(arr[102] != null) ? arr[102].toString() : null);
		obj.setPostingDate((arr[103] != null) ? arr[103].toString() : null);
		obj.setPaymentVoucherNumber(
				(arr[104] != null) ? arr[104].toString() : null);
		obj.setPaymentDate((arr[105] != null) ? arr[105].toString() : null);
		obj.setContractNumber((arr[106] != null) ? arr[106].toString() : null);
		obj.setContractDate((arr[107] != null) ? arr[107].toString() : null);
		obj.setContractValue((arr[108] != null) ? arr[108].toString() : null);
		obj.setUserDefinedField1(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setUserDefinedField2(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setUserDefinedField3(
				(arr[111] != null) ? arr[111].toString() : null);
		obj.setUserDefinedField4(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setUserDefinedField5(
				(arr[113] != null) ? arr[113].toString() : null);
		obj.setUserDefinedField6(
				(arr[114] != null) ? arr[114].toString() : null);
		obj.setUserDefinedField7(
				(arr[115] != null) ? arr[115].toString() : null);
		obj.setUserDefinedField8(
				(arr[116] != null) ? arr[116].toString() : null);
		obj.setUserDefinedField9(
				(arr[117] != null) ? arr[117].toString() : null);
		obj.setUserDefinedField10(
				(arr[118] != null) ? arr[118].toString() : null);
		obj.setUserDefinedField11(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setUserDefinedField12(
				(arr[120] != null) ? arr[120].toString() : null);
		obj.setUserDefinedField13(
				(arr[121] != null) ? arr[121].toString() : null);
		obj.setUserDefinedField14(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUserDefinedField15(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setEWayBillNumber((arr[124] != null) ? arr[124].toString() : null);
		obj.setEWayBillDate((arr[125] != null) ? arr[125].toString() : null);
		obj.setRequestID(
				(arr[126] != null) ? "`".concat(arr[126].toString()) : null);
		obj.setSupplyTypePR((arr[127] != null) ? arr[127].toString() : null);

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

	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}
	private String addApostrophe(Object o){
		if(o != null){
			if(!(o.toString().isEmpty())){
				return DownloadReportsConstant.CSVCHARACTER.concat(o.toString());
			}
			else
				return null;
		}
		return null;
	}
}
