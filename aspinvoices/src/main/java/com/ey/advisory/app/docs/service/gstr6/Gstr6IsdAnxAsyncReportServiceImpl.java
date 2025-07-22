package com.ey.advisory.app.docs.service.gstr6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdReportdto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
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
 * @author Siva.Reddy
 *
 */

@Component("Gstr6IsdAnxAsyncReportServiceImpl")
@Slf4j
public class Gstr6IsdAnxAsyncReportServiceImpl
		implements AsyncReportDownloadService {

	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	EntityInfoRepository entityInfo;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Autowired
	private Gstr6IsdDistributionReportCommonService commonService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CONF_KEY = "gstr6.isd.anx.report.chunk.size";
	private static final String CONF_CATEG = "ISD_ANX_REPORTS";

	@Override
	public void generateReports(Long id) {

		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Integer chunkSize = getChunkSize();
		String fullPath = null;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("GSTR6 ISD ANNEX Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

		String reportType = "Credit Distribution Annexure Report";

		try {
			tempDir = createTempDir();

			String timeMilli = dtf.format(LocalDateTime.now());

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ "Credit Distribution Annexure Report" + "_" + timeMilli
					+ "_" + 1 + ".csv";

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Config ID is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						id.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
			Integer maxReportSizes = commonService.getChunkingSizes();
			// chunk value

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_ISD_ANX_RPT_INS_CHUNK");

			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);
			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_ISD_ANX_RPT_INS_CHUNK: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_ISD_ANX_RPT_INS_CHUNK: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate Report";
				LOGGER.error(msg);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());

				return;
			}

			int j = 0;

			try {
				Writer writer = new BufferedWriter(new FileWriter(fullPath),
						8192);
				int count = 0;
				int fileIndex = 1;
				StatefulBeanToCsv<Gstr6IsdReportdto> beanWriter = getBeanWriter(
						writer);
				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc1 = entityManager
							.createStoredProcedureQuery(
									"USP_ISD_ANX_RPT_DISP_CHUNK");

					storedProc1.registerStoredProcedureParameter(
							"P_REPORT_DOWNLOAD_ID", Long.class,
							ParameterMode.IN);

					storedProc1.setParameter("P_REPORT_DOWNLOAD_ID", id);

					storedProc1.registerStoredProcedureParameter(
							"P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
					storedProc1.setParameter("P_CHUNK_VALUE", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")

					List<Object[]> records = storedProc1.getResultList();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("no of records after proc call {} ",
								records.size());
					}
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

						List<Gstr6IsdReportdto> reconDataList = records.stream()
								.map(o -> convert(o)).collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, reconDataList.size());
							LOGGER.debug(msg);
						}
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"maxReportSizes and Count: '%s' - '%s'",
									maxReportSizes, count);
							LOGGER.debug(msg);
						}

						if (count >= maxReportSizes) {
							flushWriter(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Invoking GSTR6 ISD Anx commonService to"
												+ " upload a zip file : "
												+ "id {} ",
										id);
							}

							// Zipping

							commonService.chunkZipFiles(tempDir, id, id,
									reportType, 1);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking ISD  Anx"
										+ "commonService to upload "
										+ "a zip file : id "
										+ "{}, deleted file "
										+ "from TempDir, ", id);
							}
							count = 0;
							fileIndex += 1;
							fullPath = tempDir.getAbsolutePath()
									+ File.separator
									+ "Credit Distribution Annexure Report"
									+ "_" + dtf.format(LocalDateTime.now())
									+ "_" + fileIndex + ".csv";
							writer = new BufferedWriter(
									new FileWriter(fullPath), 8192);

							beanWriter = getBeanWriter(writer);
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
									reconDataList.size());
							LOGGER.debug(msg);
						}
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"maxReportSizes and Count: '%s' - '%s'",
									maxReportSizes, count);
							LOGGER.debug(msg);
						}
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
							commonService.chunkZipFiles(tempDir, id, id,
									reportType, 1);
							break;
						}
					}
				}
				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATED, null,
						LocalDateTime.now());
			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for "
						+ "Report Type :{}", reportType, ex);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						LocalDateTime.now());
				throw new AppException(ex);
			}

		} catch (Exception e) {
			String errMsg = String.format(
					"Exception occured in GSTR6 ISD ANNEX while creating csv file for %d and reportType %s ",
					id, reportType);
			LOGGER.error(errMsg, e);

			downloadRepository.updateStatus(id,
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while closing the file writer";
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

	public Gstr6IsdReportdto convert(Object[] arr) {

		Gstr6IsdReportdto obj = new Gstr6IsdReportdto();

		obj.setIrn(arr[0] != null ? arr[0].toString() : null);
		obj.setIrnDate(arr[1] != null ? arr[1].toString() : null);
		obj.setTaxScheme(arr[2] != null ? arr[2].toString() : null);
		obj.setSupplyType(arr[3] != null ? arr[3].toString() : null);
		obj.setDocCategory(arr[4] != null ? arr[4].toString() : null);
		obj.setDocumentType(arr[5] != null ? arr[5].toString() : null);
		obj.setDocumentNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setDocumentDate(arr[7] != null ? arr[7].toString() : null);
		obj.setReverseChargeFlag(arr[8] != null ? arr[8].toString() : null);
		obj.setSupplierGSTIN(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierTradeName(arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierLegalName(arr[11] != null ? arr[11].toString() : null);
		obj.setSupplierAddress1(arr[12] != null ? arr[12].toString() : null);
		obj.setSupplierAddress2(arr[13] != null ? arr[13].toString() : null);
		obj.setSupplierLocation(arr[14] != null ? arr[14].toString() : null);
		obj.setSupplierPincode(arr[15] != null ? arr[15].toString() : null);
		obj.setSupplierStateCode(
				arr[16] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setSupplierPhone(arr[17] != null ? arr[17].toString() : null);
		obj.setSupplierEmail(arr[18] != null ? arr[18].toString() : null);
		obj.setCustomerGSTIN(arr[19] != null ? arr[19].toString() : null);
		obj.setCustomerTradeName(arr[20] != null ? arr[20].toString() : null);
		obj.setCustomerLegalName(arr[21] != null ? arr[21].toString() : null);
		obj.setCustomerAddress1(arr[22] != null ? arr[22].toString() : null);
		obj.setCustomerAddress2(arr[23] != null ? arr[23].toString() : null);
		obj.setCustomerLocation(arr[24] != null ? arr[24].toString() : null);
		obj.setCustomerPincode(arr[25] != null ? arr[25].toString() : null);
		obj.setCustomerStateCode(
				arr[26] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[26].toString()) : null);
		obj.setBillingPOS(arr[27] != null ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[27].toString()) : null);
		obj.setCustomerPhone(arr[28] != null ? arr[28].toString() : null);
		obj.setCustomerEmail(arr[29] != null ? arr[29].toString() : null);
		obj.setDispatcherGSTIN(arr[30] != null ? arr[30].toString() : null);
		obj.setDispatcherTradeName(arr[31] != null ? arr[31].toString() : null);
		obj.setDispatcherAddress1(arr[32] != null ? arr[32].toString() : null);
		obj.setDispatcherAddress2(arr[33] != null ? arr[33].toString() : null);
		obj.setDispatcherLocation(arr[34] != null ? arr[34].toString() : null);
		obj.setDispatcherPincode(arr[35] != null ? arr[35].toString() : null);
		obj.setDispatcherStateCode(
				arr[36] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[36].toString()) : null);
		obj.setShipToGSTIN(arr[37] != null ? arr[37].toString() : null);
		obj.setShipToTradeName(arr[38] != null ? arr[38].toString() : null);
		obj.setShipToLegalName(arr[39] != null ? arr[39].toString() : null);
		obj.setShipToAddress1(arr[40] != null ? arr[40].toString() : null);
		obj.setShipToAddress2(arr[41] != null ? arr[41].toString() : null);
		obj.setShipToLocation(arr[42] != null ? arr[42].toString() : null);
		obj.setShipToPincode(arr[43] != null ? arr[43].toString() : null);
		obj.setShipToStateCode(
				arr[44] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[44].toString()) : null);
		obj.setItemSerialNumber(arr[45] != null ? arr[45].toString() : null);
		obj.setProductSerialNumber(arr[46] != null ? arr[46].toString() : null);
		obj.setProductName(arr[47] != null ? arr[47].toString() : null);
		obj.setProductDescription(arr[48] != null ? arr[48].toString() : null);
		obj.setIsService(arr[49] != null ? arr[49].toString() : null);
		obj.setHsn(arr[50] != null ? arr[50].toString() : null);
		obj.setBarcode(arr[51] != null ? arr[51].toString() : null);
		obj.setBatchName(arr[52] != null ? arr[52].toString() : null);
		obj.setBatchExpiryDate(arr[53] != null ? arr[53].toString() : null);
		obj.setWarrantyDate(arr[54] != null ? arr[54].toString() : null);
		obj.setOrderlineReference(arr[55] != null ? arr[55].toString() : null);
		obj.setAttributeName(arr[56] != null ? arr[56].toString() : null);
		obj.setAttributeValue(arr[57] != null ? arr[57].toString() : null);
		obj.setOriginCountry(arr[58] != null ? arr[58].toString() : null);
		obj.setUQC(arr[59] != null ? arr[59].toString() : null);
		obj.setQuantity(arr[60] != null ? arr[60].toString() : null);
		obj.setFreeQuantity(arr[61] != null ? arr[61].toString() : null);
		obj.setUnitPrice(arr[62] != null ? arr[62].toString() : null);
		obj.setItemAmount(arr[63] != null ? arr[63].toString() : null);
		obj.setItemDiscount(arr[64] != null ? arr[64].toString() : null);
		obj.setPreTaxAmount(arr[65] != null ? arr[65].toString() : null);
		obj.setItemAssessableAmount(
				arr[66] != null ? arr[66].toString() : null);
		obj.setIGSTRate(arr[67] != null ? arr[67].toString() : null);
		obj.setIGSTAmount(arr[68] != null ? arr[68].toString() : null);
		obj.setCGSTRate(arr[69] != null ? arr[69].toString() : null);
		obj.setCGSTAmount(arr[70] != null ? arr[70].toString() : null);
		obj.setSGSTRate(arr[71] != null ? arr[71].toString() : null);
		obj.setSGSTAmount(arr[72] != null ? arr[72].toString() : null);
		obj.setCessAdvaloremRate(arr[73] != null ? arr[73].toString() : null);
		obj.setCessAdvaloremAmount(arr[74] != null ? arr[74].toString() : null);
		obj.setCessSpecificRate(arr[75] != null ? arr[75].toString() : null);
		obj.setCessSpecificAmount(arr[76] != null ? arr[76].toString() : null);
		obj.setStateCessAdvaloremRate(
				arr[77] != null ? arr[77].toString() : null);
		obj.setStateCessAdvaloremAmount(
				arr[78] != null ? arr[78].toString() : null);
		obj.setStateCessSpecificRate(
				arr[79] != null ? arr[79].toString() : null);
		obj.setStateCessSpecificAmount(
				arr[80] != null ? arr[80].toString() : null);
		obj.setItemOtherCharges(arr[81] != null ? arr[81].toString() : null);
		obj.setTotalItemAmount(arr[82] != null ? arr[82].toString() : null);
		obj.setInvoiceOtherCharges(arr[83] != null ? arr[83].toString() : null);
		obj.setInvoiceAssessableAmount(
				arr[84] != null ? arr[84].toString() : null);
		obj.setInvoiceIGSTAmount(arr[85] != null ? arr[85].toString() : null);
		obj.setInvoiceCGSTAmount(arr[86] != null ? arr[86].toString() : null);
		obj.setInvoiceSGSTAmount(arr[87] != null ? arr[87].toString() : null);
		obj.setInvoiceCessAdvaloremAmount(
				arr[88] != null ? arr[88].toString() : null);
		obj.setInvoiceCessSpecificAmount(
				arr[89] != null ? arr[89].toString() : null);
		obj.setInvoiceStateCessAdvaloremAmount(
				arr[90] != null ? arr[90].toString() : null);
		obj.setInvoiceStateCessSpecificAmount(
				arr[91] != null ? arr[91].toString() : null);
		obj.setInvoiceValue(arr[92] != null ? arr[92].toString() : null);
		obj.setRoundOff(arr[93] != null ? arr[93].toString() : null);
		obj.setTotalInvoiceValue(arr[94] != null ? arr[94].toString() : null);
		obj.setEligibilityIndicator(
				arr[95] != null ? arr[95].toString() : null);
		obj.setCommonSupplyIndicator(
				arr[96] != null ? arr[96].toString() : null);
		obj.setAvailableIgst(arr[97] != null ? arr[97].toString() : null);
		obj.setAvailableCgst(arr[98] != null ? arr[98].toString() : null);
		obj.setAvailableSgst(arr[99] != null ? arr[99].toString() : null);
		obj.setAvailableCess(arr[100] != null ? arr[100].toString() : null);
		obj.setITCEntitlement(arr[101] != null ? arr[101].toString() : null);
		obj.setITCReversalIdentifier(
				arr[102] != null ? arr[102].toString() : null);
		obj.setTCSFlagIncomeTax(arr[103] != null ? arr[103].toString() : null);
		obj.setTCSRateIncomeTax(arr[104] != null ? arr[104].toString() : null);
		obj.setTCSAmountIncomeTax(
				arr[105] != null ? arr[105].toString() : null);
		obj.setCurrencyCode(arr[106] != null ? arr[106].toString() : null);
		obj.setCountryCode(arr[107] != null ? arr[107].toString() : null);
		obj.setInvoiceValueFC(arr[108] != null ? arr[108].toString() : null);
		obj.setPortCode(arr[109] != null ? arr[109].toString() : null);
		obj.setBillofEntry(arr[110] != null ? arr[110].toString() : null);
		obj.setBillofEntryDate(arr[111] != null ? arr[111].toString() : null);
		obj.setInvoiceRemarks(arr[112] != null ? arr[112].toString() : null);
		obj.setInvoicePeriodStartDate(
				arr[113] != null ? arr[113].toString() : null);
		obj.setInvoicePeriodEndDate(
				arr[114] != null ? arr[114].toString() : null);
		obj.setPreceedingInvoiceNumber(
				arr[115] != null ? arr[115].toString() : null);
		obj.setPreceedingInvoiceDate(
				arr[116] != null ? arr[116].toString() : null);
		obj.setOtherReference(arr[117] != null ? arr[117].toString() : null);
		obj.setReceiptAdviceReference(
				arr[118] != null ? arr[118].toString() : null);
		obj.setReceiptAdviceDate(arr[119] != null ? arr[119].toString() : null);
		obj.setTenderReference(arr[120] != null ? arr[120].toString() : null);
		obj.setContractReference(arr[121] != null ? arr[121].toString() : null);
		obj.setExternalReference(arr[122] != null ? arr[122].toString() : null);
		obj.setProjectReference(arr[123] != null ? arr[123].toString() : null);
		obj.setCustomerPOReferenceNumber(
				arr[124] != null ? arr[124].toString() : null);
		obj.setCustomerPOReferenceDate(
				arr[125] != null ? arr[125].toString() : null);
		obj.setPayeeName(arr[126] != null ? arr[126].toString() : null);
		obj.setModeOfPayment(arr[127] != null ? arr[127].toString() : null);
		obj.setBranchOrIFSCCode(arr[128] != null ? arr[128].toString() : null);
		obj.setPaymentTerms(arr[129] != null ? arr[129].toString() : null);
		obj.setPaymentInstruction(
				arr[130] != null ? arr[130].toString() : null);
		obj.setCreditTransfer(arr[131] != null ? arr[131].toString() : null);
		obj.setDirectDebit(arr[132] != null ? arr[132].toString() : null);
		obj.setCreditDays(arr[133] != null ? arr[133].toString() : null);
		obj.setPaidAmount(arr[134] != null ? arr[134].toString() : null);
		obj.setBalanceAmount(arr[135] != null ? arr[135].toString() : null);
		obj.setPaymentDueDate(arr[136] != null ? arr[136].toString() : null);
		obj.setAccountDetail(arr[137] != null ? arr[137].toString() : null);
		obj.setEcomGSTIN(arr[138] != null ? arr[138].toString() : null);
		obj.setSupportingDocURL(arr[139] != null ? arr[139].toString() : null);
		obj.setSupportingDocument(
				arr[140] != null ? arr[140].toString() : null);
		obj.setAdditionalInformation(
				arr[141] != null ? arr[141].toString() : null);
		obj.setTransactionType(arr[142] != null ? arr[142].toString() : null);
		obj.setSubSupplyType(arr[143] != null ? arr[143].toString() : null);
		obj.setOtherSupplyTypeDescription(
				arr[144] != null ? arr[144].toString() : null);
		obj.setTransporterID(arr[145] != null ? arr[145].toString() : null);
		obj.setTransporterName(arr[146] != null ? arr[146].toString() : null);
		obj.setTransportMode(arr[147] != null ? arr[147].toString() : null);
		obj.setTransportDocNo(arr[148] != null ? arr[148].toString() : null);
		obj.setTransportDocDate(arr[149] != null ? arr[149].toString() : null);
		obj.setDistance(arr[150] != null ? arr[150].toString() : null);
		obj.setVehicleNo(arr[151] != null ? arr[151].toString() : null);
		obj.setVehicleType(arr[152] != null ? arr[152].toString() : null);
		obj.setReturnPeriod(
				arr[153] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[153].toString()) : null);
		obj.setOriginalDocumentType(
				arr[154] != null ? arr[154].toString() : null);
		obj.setOriginalSupplierGSTIN(
				arr[155] != null ? arr[155].toString() : null);
		obj.setDifferentialPercentageFlag(
				arr[156] != null ? arr[156].toString() : null);
		obj.setSec7ofIGSTFlag(arr[157] != null ? arr[157].toString() : null);
		obj.setClaimRefndFlag(arr[158] != null ? arr[158].toString() : null);
		obj.setAutoPopltToRefund(arr[159] != null ? arr[159].toString() : null);
		obj.setCRDRPreGST(arr[160] != null ? arr[160].toString() : null);
		obj.setSupplierType(arr[161] != null ? arr[161].toString() : null);
		obj.setSupplierCode(arr[162] != null ? arr[162].toString() : null);
		obj.setProductCode(arr[163] != null ? arr[163].toString() : null);
		obj.setCategoryOfProduct(arr[164] != null ? arr[164].toString() : null);
		obj.setStateApplyingCess(arr[165] != null ? arr[165].toString() : null);
		obj.setCif(arr[166] != null ? arr[166].toString() : null);
		obj.setCustomDuty(arr[167] != null ? arr[167].toString() : null);
		obj.setExchangeRate(arr[168] != null ? arr[168].toString() : null);
		obj.setReasonForCreditDebitNote(
				arr[169] != null ? arr[169].toString() : null);
		obj.setTCSFlagGST(arr[170] != null ? arr[170].toString() : null);
		obj.setTCSIGSTAmount(arr[171] != null ? arr[171].toString() : null);
		obj.setTCSCGSTAmount(arr[172] != null ? arr[172].toString() : null);
		obj.setTCSSGSTAmount(arr[173] != null ? arr[173].toString() : null);
		obj.setTDSFlagGST(arr[174] != null ? arr[174].toString() : null);
		obj.setTDSIGSTAmount(arr[175] != null ? arr[175].toString() : null);
		obj.setTDSCGSTAmount(arr[176] != null ? arr[176].toString() : null);
		obj.setTDSSGSTAmount(arr[177] != null ? arr[177].toString() : null);
		obj.setUserID(arr[178] != null ? arr[178].toString() : null);
		obj.setCompanyCode(arr[179] != null ? arr[179].toString() : null);
		obj.setSourceIdentifier(arr[180] != null ? arr[180].toString() : null);
		obj.setSourceFileName(arr[181] != null ? arr[181].toString() : null);
		obj.setPlantCode(arr[182] != null ? arr[182].toString() : null);
		obj.setDivision(arr[183] != null ? arr[183].toString() : null);
		obj.setSubDivision(arr[184] != null ? arr[184].toString() : null);
		obj.setLocation(arr[185] != null ? arr[185].toString() : null);
		obj.setPurchaseOrganisation(
				arr[186] != null ? arr[186].toString() : null);
		obj.setProfitCentre1(arr[187] != null ? arr[187].toString() : null);
		obj.setProfitCentre2(arr[188] != null ? arr[188].toString() : null);
		obj.setProfitCentre3(arr[189] != null ? arr[189].toString() : null);
		obj.setProfitCentre4(arr[190] != null ? arr[190].toString() : null);
		obj.setProfitCentre5(arr[191] != null ? arr[191].toString() : null);
		obj.setProfitCentre6(arr[192] != null ? arr[192].toString() : null);
		obj.setProfitCentre7(arr[193] != null ? arr[193].toString() : null);
		obj.setProfitCentre8(arr[194] != null ? arr[194].toString() : null);
		obj.setGlAssessableValue(arr[195] != null ? arr[195].toString() : null);
		obj.setGlIGST(arr[196] != null ? arr[196].toString() : null);
		obj.setGlCGST(arr[197] != null ? arr[197].toString() : null);
		obj.setGlSGST(arr[198] != null ? arr[198].toString() : null);
		obj.setGlAdvaloremCess(arr[199] != null ? arr[199].toString() : null);
		obj.setGlSpecificCess(arr[200] != null ? arr[200].toString() : null);
		obj.setGLStateCessAdvalorem(
				arr[201] != null ? arr[201].toString() : null);
		obj.setGLStateCessSpecific(
				arr[202] != null ? arr[202].toString() : null);
		obj.setGlPostingDate(arr[203] != null ? arr[203].toString() : null);
		obj.setPurchaseOrderValue(
				arr[204] != null ? arr[204].toString() : null);
		obj.setEWBNumber(arr[205] != null ? arr[205].toString() : null);
		obj.setEWBDate(arr[206] != null ? arr[206].toString() : null);
		obj.setAccountingVoucherNumber(
				arr[207] != null ? arr[207].toString() : null);
		obj.setAccountingVoucherDate(
				arr[208] != null ? arr[208].toString() : null);
		obj.setDocumentReferenceNumber(
				arr[209] != null ? arr[209].toString() : null);
		obj.setUserDefField1(arr[210] != null ? arr[210].toString() : null);
		obj.setUserDefField2(arr[211] != null ? arr[211].toString() : null);
		obj.setUserDefField3(arr[212] != null ? arr[212].toString() : null);
		obj.setUserDefField4(arr[213] != null ? arr[213].toString() : null);
		obj.setUserDefField5(arr[214] != null ? arr[214].toString() : null);
		obj.setUserDefField6(arr[215] != null ? arr[215].toString() : null);
		obj.setUserDefField7(arr[216] != null ? arr[216].toString() : null);
		obj.setUserDefField8(arr[217] != null ? arr[217].toString() : null);
		obj.setUserDefField9(arr[218] != null ? arr[218].toString() : null);
		obj.setUserDefField10(arr[219] != null ? arr[219].toString() : null);
		obj.setUserDefField11(arr[220] != null ? arr[220].toString() : null);
		obj.setUserDefField12(arr[221] != null ? arr[221].toString() : null);
		obj.setUserDefField13(arr[222] != null ? arr[222].toString() : null);
		obj.setUserDefField14(arr[223] != null ? arr[223].toString() : null);
		obj.setUserDefField15(arr[224] != null ? arr[224].toString() : null);
		obj.setUserDefField16(arr[225] != null ? arr[225].toString() : null);
		obj.setUserDefField17(arr[226] != null ? arr[226].toString() : null);
		obj.setUserDefField18(arr[227] != null ? arr[227].toString() : null);
		obj.setUserDefField19(arr[228] != null ? arr[228].toString() : null);
		obj.setUserDefField20(arr[229] != null ? arr[229].toString() : null);
		obj.setUserDefField21(arr[230] != null ? arr[230].toString() : null);
		obj.setUserDefField22(arr[231] != null ? arr[231].toString() : null);
		obj.setUserDefField23(arr[232] != null ? arr[232].toString() : null);
		obj.setUserDefField24(arr[233] != null ? arr[233].toString() : null);
		obj.setUserDefField25(arr[234] != null ? arr[234].toString() : null);
		obj.setUserDefField26(arr[235] != null ? arr[235].toString() : null);
		obj.setUserDefField27(arr[236] != null ? arr[236].toString() : null);
		obj.setUserDefField28(arr[237] != null ? arr[237].toString() : null);
		obj.setUserDefField29(arr[238] != null ? arr[238].toString() : null);
		obj.setUserDefField30(arr[239] != null ? arr[239].toString() : null);
		obj.setReturnType(arr[240] != null ? arr[240].toString() : null);
		obj.setDataCategory(arr[241] != null ? arr[241].toString() : null);
		obj.setTableNumber(arr[242] != null ? arr[242].toString() : null);

		obj.setGstinForDist(arr[243] != null ? arr[243].toString() : null);
		obj.setTurnoverforDist(arr[244] != null ? arr[244].toString() : null);
		obj.setTotTurnoverforDist(
				arr[245] != null ? arr[245].toString() : null);
		obj.setCreditDisIGSTasIGST(
				arr[246] != null ? arr[246].toString() : null);
		obj.setCreditDisIGSTasSGST(
				arr[247] != null ? arr[247].toString() : null);

		obj.setCreditDisIGSTasCGST(
				arr[248] != null ? arr[248].toString() : null);
		obj.setCreditDisSGSTasSGST(
				arr[249] != null ? arr[249].toString() : null);

		obj.setCreditDisSGSTasIGST(
				arr[250] != null ? arr[250].toString() : null);
		obj.setCreditDisCGSTasCGST(
				arr[251] != null ? arr[251].toString() : null);

		obj.setCreditDisCGSTasIGST(
				arr[252] != null ? arr[252].toString() : null);
		obj.setCreditDisCessAmt(arr[253] != null ? arr[253].toString() : null);

		obj.setRequestId(arr[254] != null ? arr[254].toString() : null);

		if (arr[255] != null) {
			String strdate = arr[255].toString();
			DateTimeFormatter f = new DateTimeFormatterBuilder()
					.appendPattern("dd-MM-yyyy HH:mm:ss").toFormatter();
			LocalDateTime parsedDate = LocalDateTime.parse(strdate, f);
			obj.setCreditDisCompDt(DownloadReportsConstant.CSVCHARACTER
					.concat(parsedDate.format(f)));
		} else {
			obj.setCreditDisCompDt(null);
		}
		return obj;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

	private StatefulBeanToCsv<Gstr6IsdReportdto> getBeanWriter(Writer writer)
			throws Exception {

		String invoiceHeadersTemplate = commonUtility
				.getProp("gstr6.isd.annex.report.column.mapping");

		String[] columnMappings = commonUtility
				.getProp("gstr6.isd.annex.report.data.mapping").split(",");

		ColumnPositionMappingStrategy<Gstr6IsdReportdto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr6IsdReportdto.class);
		mappingStrategy.setColumnMapping(columnMappings);

		StatefulBeanToCsvBuilder<Gstr6IsdReportdto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr6IsdReportdto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(invoiceHeadersTemplate);
		return beanWriter;
	}
}
