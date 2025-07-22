package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */

@Slf4j
@Component("Gstr2ReconItcTrackingReport")
public class Gstr2ReconItcTrackingReport {

	// "ITC Tracking Report"

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getGstr2ReconItcTrackingReport(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("ITC Tracking Report");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RPT_ITC10");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			long dbLoadStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " Report Name and Data count: '%s' - '%s'",
						dbLoadTimeDiff, reportType, records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ reportType + ".csv";
				List<Gstr2ReconItcTrackingReportDto> reconDataList = new ArrayList<>();

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {
					reconDataList = records.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								reportType, reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility.getProp(
								"gstr2.recon.itctracking.report.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp(
										"gstr2.recon.itctracking.report.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2ReconItcTrackingReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy
								.setType(Gstr2ReconItcTrackingReportDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2ReconItcTrackingReportDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2ReconItcTrackingReportDto> beanWriter = builder
								.withMappingStrategy(mappingStrategy)
								.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
								.withLineEnd(CSVWriter.DEFAULT_LINE_END)
								.withEscapechar(
										CSVWriter.DEFAULT_ESCAPE_CHARACTER)
								.build();
						long generationStTime = System.currentTimeMillis();
						beanWriter.write(reconDataList);
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' "
											+ " millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType,
									records.size());
							LOGGER.debug(msg);
						}
					}
				} catch (Exception ex) {
					LOGGER.error("Exception while executing the Store Proc for "
							+ "Report Type :{}", reportType, ex);

				}
				String zipFileName = null;
				String newTrackingReportFilePath = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					newTrackingReportFilePath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");

				}
				if (newTrackingReportFilePath != null)
					addlReportRepo.updateReconFilePath(
							newTrackingReportFilePath, configId,
							"ITC Tracking Report");
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Update file path for ITC Tracking Report "
									+ ":%s : ", newTrackingReportFilePath);
					LOGGER.debug(msg);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);

		}

		deleteTemporaryDirectory(tempDir);

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

	private Gstr2ReconItcTrackingReportDto convert(Object[] arr) {

		Gstr2ReconItcTrackingReportDto obj = new Gstr2ReconItcTrackingReportDto();

		obj.setItcEntitlement((arr[0] != null) ? arr[0].toString() : null);
		obj.setPreviousReportType2A(
				(arr[1] != null) ? arr[1].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[2] != null) ? arr[2].toString() : null);
		obj.setMismatchReason((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocumentNumberPR((arr[4] != null) ? (arr[4].toString()) : null);
		obj.setDocumentNumber2A((arr[5] != null) ? arr[5].toString() : null);
		obj.setRecipientGSTIN2A((arr[6] != null) ? arr[6].toString() : null);
		obj.setRecipientGSTINPR((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocType2A((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocTypePR((arr[9] != null) ? arr[9].toString() : null);
		obj.setDocumentDate2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setDocumentDatePR((arr[11] != null) ? arr[11].toString() : null);
		obj.setTaxableValue2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setTaxableValuePR((arr[13] != null) ? arr[13].toString() : null);
		obj.setCgst2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setCgstPR((arr[15] != null) ? arr[15].toString() : null);
		obj.setSgst2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setSgstPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setIgst2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setIgstPR((arr[19] != null) ? arr[19].toString() : null);
		obj.setCess2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setCessPR((arr[21] != null) ? arr[21].toString() : null);
		obj.setPos2A((arr[22] != null) ? arr[22].toString() : null);
		obj.setPosPR((arr[23] != null) ? arr[23].toString() : null);
		obj.setMatchingScoreOutof12(
				(arr[24] != null) ? (arr[24].toString()) : null);
		obj.setTaxPeriod2A(
				(arr[25] != null) ? "`".concat(arr[25].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[26] != null) ? "`".concat(arr[26].toString()) : null);
		obj.setCalendarMonth(
				(arr[27] != null) ? "`".concat(arr[27].toString()) : null);
		obj.setSupplierLegalName2A(
				(arr[28] != null) ? arr[28].toString() : null);
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
		obj.setGstr1FilingStatus((arr[39] != null) ? arr[39].toString() : null);
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
		obj.setSupplierGSTINPR((arr[47] != null) ? arr[47].toString() : null);
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
		obj.setGlCodeTaxableValue(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setGlCodeIGST((arr[62] != null) ? arr[62].toString() : null);
		obj.setGlCodeCGST((arr[63] != null) ? arr[63].toString() : null);
		obj.setGlCodeSGST((arr[64] != null) ? arr[64].toString() : null);
		obj.setGlCodeAdvaloremCess(
				(arr[65] != null) ? arr[65].toString() : null);
		obj.setGlCodeSpecificCess(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setGlCodeStateCess((arr[67] != null) ? arr[67].toString() : null);
		obj.setSupplyType2A((arr[68] != null) ? arr[68].toString() : null);
		obj.setCrdrPreGSTPR((arr[69] != null) ? arr[69].toString() : null);
		obj.setSupplierType((arr[70] != null) ? arr[70].toString() : null);
		obj.setSupplierCode((arr[71] != null) ? arr[71].toString() : null);
		obj.setSupplierAddress1((arr[72] != null) ? arr[72].toString() : null);
		obj.setSupplierAddress2((arr[73] != null) ? arr[73].toString() : null);
		obj.setSupplierAddress3((arr[74] != null) ? arr[74].toString() : null);
		obj.setSupplierAddress4((arr[75] != null) ? arr[75].toString() : null);
		obj.setStateApplyingCess((arr[76] != null) ? arr[76].toString() : null);
		obj.setPortCode2A((arr[77] != null) ? arr[77].toString() : null);
		obj.setBillOfEntryPR((arr[78] != null) ? arr[78].toString() : null);
		obj.setBillOfEntryDatePR((arr[79] != null) ? arr[79].toString() : null);
		obj.setCifValue((arr[80] != null) ? arr[80].toString() : null);
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
		obj.setMatchingId((arr[111] != null) ? arr[111].toString() : null);
		obj.setRequestId((arr[112] != null) ? arr[112].toString() : null);
		obj.setIdPR((arr[113] != null) ? arr[113].toString() : null);
		obj.setId2A((arr[114] != null) ? arr[114].toString() : null);
		obj.setInvoiceKeyPR((arr[115] != null) ? arr[115].toString() : null);
		obj.setInvoiceKeyA2((arr[116] != null) ? arr[116].toString() : null);
		obj.setSupplierGSTIN2A((arr[117] != null) ? arr[117].toString() : null);
		obj.setTaxPeriodGSTR3B((arr[118] != null) ? arr[118].toString() : null);
		obj.setReportType((arr[119] != null) ? arr[119].toString() : null);
		obj.setGstPercentage2A((arr[120] != null) ? arr[120].toString() : null);
		obj.setGstPercentagePR((arr[121] != null) ? arr[121].toString() : null);
		obj.setReverseChargeFlag2A(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setCrdrPreGST2A((arr[124] != null) ? arr[124].toString() : null);
		obj.setHsnOrSac((arr[125] != null) ? arr[125].toString() : null);
		obj.setItemCode((arr[126] != null) ? arr[126].toString() : null);
		obj.setItemDescription((arr[127] != null) ? arr[127].toString() : null);
		obj.setCategoryOfItem((arr[128] != null) ? arr[128].toString() : null);
		obj.setUnitOfMeasurement(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setAdvaloremCessRate(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setStateCessRate((arr[131] != null) ? arr[131].toString() : null);
		obj.setClaimRefundFlag((arr[132] != null) ? arr[132].toString() : null);
		obj.setSpecificCessRate(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[134] != null) ? arr[134].toString() : null);
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
		obj.setItcReversalIdentifier(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setContractDate((arr[148] != null) ? arr[148].toString() : null);
		obj.setReferenceIdPR((arr[149] != null) ? arr[149].toString() : null);
		obj.setReferenceId2A((arr[150] != null) ? arr[150].toString() : null);

		// Suggested Resp
		if ((arr[151] != null && (!arr[151].toString().isEmpty())
				&& (arr[151].toString().matches("[0-9]+")))) {

			obj.setSuggestedResponse(arr[151].toString().length() == 5
					? "`".concat(("0").concat(arr[151].toString()))
					: "`".concat((arr[151].toString())));
		} else {
			obj.setSuggestedResponse(
					(arr[151] != null) ? (arr[151].toString()) : null);
		}
		obj.setResponseRemarks((arr[152] != null) ? arr[152].toString() : null);
		obj.setOrgSupplierGSTINPR(
				(arr[153] != null) ? arr[153].toString() : null);
		obj.setOrgSupplierNamePR(
				(arr[154] != null) ? arr[154].toString() : null);
		obj.setUserResponse((arr[155] != null) ? arr[155].toString() : null);
		obj.setGstr1FilingDate((arr[156] != null) ? arr[156].toString() : null);
		obj.setGstr1FilingPeriod(
				(arr[157] != null) ? arr[157].toString() : null);
		obj.setGstr3BFilingStatus(
				(arr[158] != null) ? arr[158].toString() : null);
		obj.setCancellationDate(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[160] != null) ? arr[160].toString() : null);
		obj.setOrgAmendmentType(
				(arr[161] != null) ? arr[161].toString() : null);
		obj.setCdnDelinkingFlag(
				(arr[162] != null) ? arr[162].toString() : null);
		obj.setSupplyType2A((arr[163] != null) ? arr[163].toString() : null);
		obj.setSupplyTypePR((arr[164] != null) ? arr[164].toString() : null);
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

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "ITC Tracking Report" + "_" + configId;
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
