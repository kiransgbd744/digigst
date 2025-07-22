package com.ey.advisory.inwardEinvoice.initiateRecon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
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

import com.ey.advisory.app.asprecon.gstr2.pr2b.reports.Gstr2Recon2BPRReportCommonService;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
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
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("InwardEinvoiceAdditionalReportFetchDetails")
public class InwardEinvoiceAdditionalReportFetchDetails{

	// Doc No Mismatch II

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository addlInwardReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("InwardEinvoiceReconReportCommonServiceImpl")
	private Gstr2Recon2BPRReportCommonService commonService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final String CONF_KEY = "gstr2.recon.2bpr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";

	private static int CSV_BUFFER_SIZE = 8192;

	public void getAdditionalReport(Long configId, String reportType) throws IOException {

		String fullPath = null;
		File tempDir = null;
		Long startId = 0L;
		Long endId = 0L;
		int noOfChunk = 0;
		String msg = null;
		Writer writer = null;
		boolean isReportAvailable = false;
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Recon Report Details with configId:'%s' reportType: '%s'",
					configId.toString(), reportType);
			LOGGER.debug(msg);
		}

		//reportType = "Doc No Mismatch II";

		tempDir = createTempDir(configId, reportType);

		fullPath = tempDir.getAbsolutePath() + File.separator
				+ getUniqueFileName(reportType) + "_" + 1 + ".csv";

		InwardEinvoiceReconAddlReportsEntity chunkDetails = addlInwardReportRepo
				.getChunckSizeforReportType(configId, reportType);

		noOfChunk = chunkDetails.getChunkNums() != null
				? chunkDetails.getChunkNums().intValue()
				: 0;

		Long chunkValue = Long.valueOf(getChunkSize());
		Long chunkSize = Long.valueOf(getChunkSize());

		if (noOfChunk != 0) {

			startId = chunkDetails.getStartChunk() != null
					? chunkDetails.getStartChunk()
					: 0L;

			endId = chunkDetails.getEndChunk() != null
					? chunkDetails.getEndChunk()
					: 0L;

			LOGGER.debug("before calc chunkSize {} - ReportType {}", chunkSize,
					reportType);
			chunkSize = ((chunkSize > startId) && (chunkSize > endId)) ? endId
					: startId + (chunkSize - 1);

			LOGGER.debug("after calc chunkSize {} - ReportType {}", chunkSize,
					reportType);

			msg = String.format(
					"Get Recon Report Details with "
							+ "configId:'%s' Before proc call startId %d, "
							+ "endId %d, chunkSize %d ,reportType %s ",
					configId.toString(), startId, endId, chunkSize, reportType);
			LOGGER.error(msg);

			Pair<Integer, Integer> maxReportSizes = commonService
					.getChunkingSizes();

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				String invoiceHeadersTemplate = commonUtility
						.getProp("inward.einvoice.vs.pr.report.headers");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("inward.einvoice.vs.pr.report.mapping").split(",");
				StatefulBeanToCsv<InwardEInvoiceReconReportDto> beanWriter = getBeanWriter(
						columnMappings, writer);

				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_RECON_EINVPR_RPT_MASTER");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
							String.class, ParameterMode.IN);

					storedProc.setParameter("P_REPORT_TYPE", reportType);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Query for Recon Reports: '%s'",
								reportType);
						LOGGER.debug(msg);
					}

					// startId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_ST_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_ST_ID", startId);

					// EndId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_END_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_END_ID", chunkSize);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"call stored proc with "
										+ "params {} Config ID is '%s', "
										+ "reportType is %s, startId is %d,"
										+ " chunksize is %d ",
								configId.toString(), reportType, startId,
								chunkSize);
						LOGGER.debug(msg);
					}

					startId = chunkSize + 1L;
					Long tempValue = (endId - startId) > chunkValue ? chunkValue
							: (endId - startId);

					LOGGER.debug("tempValue {}", tempValue);
					if (j + 1 == noOfChunk) {
						chunkSize = startId + tempValue;
					} else {
						chunkSize = startId + (tempValue - 1);

					}

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
						List<InwardEInvoiceReconReportDto> reconDataList = new ArrayList<>();

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
										"Invoking 2BPR " + "commonService to"
												+ " upload a zip file : "
												+ "configId {}, ReportName"
												+ " {}, ",
										configId, reportType);
							}

							// Zipping
							if (isReportAvailable) {

								commonService.chunkZipFiles(tempDir,
										chunkDetails.getId(), configId,
										reportType, maxReportSizes.getValue1());
							}

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking 2BPR "
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
									chunkDetails.getId(), configId, reportType,
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

		addlInwardReportRepo.updateIsReportProcExecuted(configId, 
				reportType);
		deleteTemporaryDirectory(tempDir);
	}

	private StatefulBeanToCsv<InwardEInvoiceReconReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<InwardEInvoiceReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(InwardEInvoiceReconReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<InwardEInvoiceReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<InwardEInvoiceReconReportDto> beanWriter = builder
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

	private InwardEInvoiceReconReportDto convert(Object[] arr) {

		InwardEInvoiceReconReportDto obj = new InwardEInvoiceReconReportDto();

		// need to declare all the InwardEInvoiceReconReportDto dto values from
		// arr with null check and set the values to obj
		// need from first variable

		obj.setMatchingScore((arr[0] != null) ? arr[0].toString() : null);
		obj.setMatchReason((arr[1] != null) ? arr[1].toString() : null);
		obj.setMismatchReason((arr[2] != null) ? arr[2].toString() : null);
		obj.setReportCategory((arr[3] != null) ? arr[3].toString() : null);
		obj.setReportType((arr[4] != null) ? arr[4].toString() : null);
		obj.setPreviousReportTypeEINV(
				(arr[5] != null) ? arr[5].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[6] != null) ? arr[6].toString() : null);
		obj.setTaxPeriodEINV((arr[7] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString()) : null);
		obj.setTaxPeriodPR((arr[8] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString()) : null);
		obj.setCalendarMonth((arr[9] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString()) : null);
		obj.setRecipientGSTINEINV(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setRecipientGSTINPR((arr[11] != null) ? arr[11].toString() : null);
		obj.setSupplierGSTINEINV((arr[12] != null) ? arr[12].toString() : null);
		obj.setSupplierGSTINPR((arr[13] != null) ? arr[13].toString() : null);
		obj.setSupplierTradeNameEINV(
				(arr[14] != null) ? arr[14].toString() : null);
		obj.setSupplierLegalNameEINV(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setSupplierNamePR((arr[16] != null) ? arr[16].toString() : null);
		obj.setDocumentTypeEINV((arr[17] != null) ? arr[17].toString() : null);
		obj.setDocumentTypePR((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocumentNumberEINV(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setDocumentNumberPR((arr[20] != null) ? arr[20].toString() : null);
		obj.setDocumentDateEINV((arr[21] != null) ? arr[21].toString() : null);
		obj.setDocumentDatePR((arr[22] != null) ? arr[22].toString() : null);
		obj.setPosEINV((arr[23] != null) ? arr[23].toString() : null);
		obj.setPosPR((arr[24] != null) ? arr[24].toString() : null);
		obj.setReverseChargeFlagEINV(
				(arr[39] != null) ? arr[39].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setEligibilityIndicatorPR(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setItcReversalIdentifierPR(
				(arr[46] != null) ? arr[46].toString() : null);
		obj.setItcEntitlementPR((arr[47] != null) ? arr[47].toString() : null);
		obj.setIrnEINV((arr[48] != null) ? arr[48].toString() : null);
		obj.setIrnPR((arr[49] != null) ? arr[49].toString() : null);
		obj.setIrnDateEINV((arr[50] != null) ? arr[50].toString() : null);
		obj.setIrnDatePR((arr[51] != null) ? arr[51].toString() : null);
		obj.setIrnStatusEINV((arr[52] != null) ? arr[52].toString() : null);
		obj.setIrnCancellationDateEINV(
				(arr[53] != null) ? "'"+arr[53].toString() : null);
		obj.setCancellationReasonEINV(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setAcknowledgmentNumberEINV(
				(arr[55] != null) ? "'"+arr[55].toString() : null);
		obj.setIrpNameEINV((arr[56] != null) ? arr[56].toString() : null);
		obj.setIrnGetCallDateTimeEINV(
				(arr[57] != null) ? "'"+arr[57].toString() : null);
		obj.setSupplierGSTINStatus(
				(arr[58] != null) ? arr[58].toString() : null);
		obj.setSupplierGSTINCancellationDate(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setOrgDocNumberEINV((arr[60] != null) ? arr[60].toString() : null);
		obj.setOrgDocNumberPR((arr[61] != null) ? arr[61].toString() : null);
		obj.setOrgDocDateEINV((arr[62] != null) ? arr[62].toString() : null);
		obj.setOrgDocDatePR((arr[63] != null) ? arr[63].toString() : null);
		obj.setOrgSupplierGSTINPR(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setSupplyTypeEINV((arr[65] != null) ? arr[65].toString() : null);
		obj.setSupplyTypePR((arr[66] != null) ? arr[66].toString() : null);
		obj.setEwbNumberEINV((arr[67] != null) ? arr[67].toString() : null);
		obj.setEwbNumberPR((arr[68] != null) ? arr[68].toString() : null);
		obj.setEwbDateEINV((arr[69] != null) ? arr[69].toString() : null);
		obj.setEwbDatePR((arr[70] != null) ? arr[70].toString() : null);
		obj.setValidUptoEINV((arr[71] != null) ? arr[71].toString() : null);
		obj.setPortCodeEINV((arr[72] != null) ? arr[72].toString() : null);
		obj.setShippingBillNumberEINV(
				(arr[73] != null) ? arr[73].toString() : null);
		obj.setShippingBillDateEINV(
				(arr[74] != null) ? arr[74].toString() : null);
		obj.setCurrencyCodeEINV((arr[75] != null) ? arr[75].toString() : null);
		obj.setExportDutyEINV((arr[76] != null) ? arr[76].toString() : null);
		obj.setCountryCodeEINV((arr[77] != null) ? arr[77].toString() : null);
		obj.setUserIDPR((arr[78] != null) ? arr[78].toString() : null);
		obj.setSourceIdentifierPR(
				(arr[79] != null) ? arr[79].toString() : null);
		obj.setSourceFileNamePR((arr[80] != null) ? arr[80].toString() : null);
		obj.setCompanyCodePR((arr[81] != null) ? arr[81].toString() : null);
		obj.setPlantCodePR((arr[82] != null) ? arr[82].toString() : null);
		obj.setDivisionPR((arr[83] != null) ? arr[83].toString() : null);
		obj.setSubDivisionPR((arr[84] != null) ? arr[84].toString() : null);
		obj.setLocationPR((arr[85] != null) ? arr[85].toString() : null);
		obj.setPurchaseOrganisationPR(
				(arr[86] != null) ? arr[86].toString() : null);
		obj.setProfitCentre1PR((arr[87] != null) ? arr[87].toString() : null);
		obj.setProfitCentre2PR((arr[88] != null) ? arr[88].toString() : null);
		obj.setProfitCentre3PR((arr[89] != null) ? arr[89].toString() : null);
		obj.setProfitCentre4PR((arr[90] != null) ? arr[90].toString() : null);
		obj.setProfitCentre5PR((arr[91] != null) ? arr[91].toString() : null);
		obj.setProfitCentre6PR((arr[92] != null) ? arr[92].toString() : null);
		obj.setProfitCentre7PR((arr[93] != null) ? arr[93].toString() : null);
		obj.setProfitCentre8PR((arr[94] != null) ? arr[94].toString() : null);
		obj.setHsnEINV((arr[95] != null) ? arr[95].toString() : null);
		obj.setHsnPR((arr[96] != null) ? arr[96].toString() : null);
		obj.setProductCodePR((arr[97] != null) ? arr[97].toString() : null);
		obj.setProductDescriptionEINV(
				(arr[98] != null) ? arr[98].toString() : null);
		obj.setProductDescriptionPR(
				(arr[99] != null) ? arr[99].toString() : null);
		obj.setCategoryOfProductPR(
				(arr[100] != null) ? arr[100].toString() : null);
		obj.setUqcPR((arr[101] != null) ? arr[101].toString() : null);
		obj.setQuantityEINV((arr[102] != null) ? arr[102].toString() : null);
		obj.setQuantityPR((arr[103] != null) ? arr[103].toString() : null);
		obj.setAccountingVoucherNumberPR(
				(arr[104] != null) ? arr[104].toString() : null);
		obj.setAccountingVoucherDatePR(
				(arr[105] != null) ? arr[105].toString() : null);
		obj.setCustomerPOReferenceNumberEINV(
				(arr[106] != null) ? arr[106].toString() : null);
		obj.setCustomerPOReferenceNumberPR(
				(arr[107] != null) ? arr[107].toString() : null);
		obj.setCustomerPOReferenceDateEINV(
				(arr[108] != null) ? arr[108].toString() : null);
		obj.setCustomerPOReferenceDatePR(
				(arr[109] != null) ? arr[109].toString() : null);

		obj.setSupplierCodePR((arr[111] != null) ? arr[111].toString() : null);
		obj.setSupplierTypePR((arr[112] != null) ? arr[112].toString() : null);
		obj.setSupplierAddress1PR(
				(arr[113] != null) ? arr[113].toString() : null);
		obj.setSupplierAddress2PR(
				(arr[114] != null) ? arr[114].toString() : null);
		obj.setSupplierLocationPR(
				(arr[115] != null) ? arr[115].toString() : null);
		obj.setSupplierPincodePR(
				(arr[116] != null) ? arr[116].toString() : null);
		obj.setVendorType((arr[117] != null) ? arr[117].toString() : null);
		obj.setHsnVendorMaster((arr[118] != null) ? arr[118].toString() : null);
		obj.setVendorRiskCategory(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setVendorPaymentTermsDays(
				(arr[120] != null) ? arr[120].toString() : null);
		obj.setVendorRemarks((arr[121] != null) ? arr[121].toString() : null);
		obj.setUserDefinedField1PR(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setUserDefinedField2PR(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setUserDefinedField3PR(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setUserDefinedField4PR(
				(arr[125] != null) ? arr[125].toString() : null);
		obj.setUserDefinedField5PR(
				(arr[126] != null) ? arr[126].toString() : null);
		obj.setUserDefinedField6PR(
				(arr[127] != null) ? arr[127].toString() : null);
		obj.setUserDefinedField7PR(
				(arr[128] != null) ? arr[128].toString() : null);
		obj.setUserDefinedField8PR(
				(arr[129] != null) ? arr[129].toString() : null);
		obj.setUserDefinedField9PR(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setUserDefinedField10PR(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setUserDefinedField11PR(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setUserDefinedField12PR(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setUserDefinedField13PR(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setUserDefinedField14PR(
				(arr[135] != null) ? arr[135].toString() : null);
		obj.setUserDefinedField15PR(
				(arr[136] != null) ? arr[136].toString() : null);
		obj.setUserDefinedField28PR(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setRequestID((arr[138] != null) ? "'"+arr[138].toString() : null);
		obj.setIdPR((arr[139] != null) ? arr[139].toString() : null);
		obj.setIdEINV((arr[140] != null) ? arr[140].toString() : null);
		obj.setInvoiceKeyPR((arr[141] != null) ? arr[141].toString() : null);
		obj.setInvoiceKeyEINV((arr[142] != null) ? arr[142].toString() : null);
		obj.setReferenceIDPR((arr[143] != null) ? arr[143].toString() : null);
		obj.setReferenceIDEINV((arr[144] != null) ? arr[144].toString() : null);

		String docTypeEINV = (arr[17] != null) ? arr[17].toString() : null;
		String docTypePR = (arr[18] != null) ? arr[18].toString() : null;

		if ("CR".equals(docTypeEINV) || "RCR".equals(docTypeEINV) || "C".equals(docTypeEINV)) {
			obj.setTaxableValueEINV((arr[25] != null && isNumeric(arr[25]))
					? "-" + arr[25].toString() : null);
			obj.setIgstEINV((arr[27] != null && isNumeric(arr[27]))
					? "-" + arr[27].toString() : null);
			obj.setCgstEINV((arr[29] != null && isNumeric(arr[29]))
					? "-" + arr[29].toString() : null);
			obj.setSgstEINV((arr[31] != null && isNumeric(arr[31]))
					? "-" + arr[31].toString() : null);
			obj.setCessEINV((arr[33] != null && isNumeric(arr[33]))
					? "-" + arr[33].toString() : null);
			obj.setTotalTaxEINV((arr[35] != null && isNumeric(arr[35]))
					? "-" + arr[35].toString() : null);
			obj.setInvoiceValueEINV((arr[37] != null && isNumeric(arr[37]))
					? "-" + arr[37].toString() : null);
		} else {
			obj.setTaxableValueEINV((arr[25] != null && isNumeric(arr[25]))
					? arr[25].toString() : null);
			obj.setIgstEINV((arr[27] != null && isNumeric(arr[27]))
					? arr[27].toString() : null);
			obj.setCgstEINV((arr[29] != null && isNumeric(arr[29]))
					? arr[29].toString() : null);
			obj.setSgstEINV((arr[31] != null && isNumeric(arr[31]))
					? arr[31].toString() : null);
			obj.setCessEINV((arr[33] != null && isNumeric(arr[33]))
					? arr[33].toString() : null);
			obj.setTotalTaxEINV((arr[35] != null && isNumeric(arr[35]))
					? arr[35].toString() : null);
			obj.setInvoiceValueEINV((arr[37] != null && isNumeric(arr[37]))
					? arr[37].toString() : null);
		}

		if ("CR".equals(docTypePR) || "RCR".equals(docTypePR) || "C".equals(docTypePR)) {
			obj.setTaxableValuePR((arr[26] != null && isNumeric(arr[26]))
					? "-" + arr[26].toString() : null);
			obj.setIgstPR((arr[28] != null && isNumeric(arr[28]))
					? "-" + arr[28].toString() : null);
			obj.setCgstPR((arr[30] != null && isNumeric(arr[30]))
					? "-" + arr[30].toString() : null);
			obj.setSgstPR((arr[32] != null && isNumeric(arr[32]))
					? "-" + arr[32].toString() : null);
			obj.setCessPR((arr[34] != null && isNumeric(arr[34]))
					? "-" + arr[34].toString() : null);
			obj.setTotalTaxPR((arr[36] != null && isNumeric(arr[36]))
					? "-" + arr[36].toString() : null);
			obj.setInvoiceValuePR((arr[38] != null && isNumeric(arr[38]))
					? "-" + arr[38].toString() : null);
			obj.setAvailableIGSTPR((arr[42] != null && isNumeric(arr[42]))
					? "-" + arr[42].toString() : null);
			obj.setAvailableCGSTPR((arr[43] != null && isNumeric(arr[43]))
					? "-" + arr[43].toString() : null);
			obj.setAvailableSGSTPR((arr[44] != null && isNumeric(arr[44]))
					? "-" + arr[44].toString() : null);
			obj.setAvailableCessPR((arr[45] != null && isNumeric(arr[45]))
					? "-" + arr[45].toString() : null);
			obj.setPurchaseOrderValuePR(
					(arr[110] != null && isNumeric(arr[110]))
							? "-" + arr[110].toString() : null);
		} else {
			obj.setTaxableValuePR((arr[26] != null && isNumeric(arr[26]))
					? arr[26].toString() : null);
			obj.setIgstPR((arr[28] != null && isNumeric(arr[28]))
					? arr[28].toString() : null);
			obj.setCgstPR((arr[30] != null && isNumeric(arr[30]))
					? arr[30].toString() : null);
			obj.setSgstPR((arr[32] != null && isNumeric(arr[32]))
					? arr[32].toString() : null);
			obj.setCessPR((arr[34] != null && isNumeric(arr[34]))
					? arr[34].toString() : null);
			obj.setTotalTaxPR((arr[36] != null && isNumeric(arr[36]))
					? arr[36].toString() : null);
			obj.setInvoiceValuePR((arr[38] != null && isNumeric(arr[38]))
					? arr[38].toString() : null);
			obj.setAvailableIGSTPR((arr[42] != null && isNumeric(arr[42]))
					? arr[42].toString() : null);
			obj.setAvailableCGSTPR((arr[43] != null && isNumeric(arr[43]))
					? arr[43].toString() : null);
			obj.setAvailableSGSTPR((arr[44] != null && isNumeric(arr[44]))
					? arr[44].toString() : null);
			obj.setAvailableCessPR((arr[45] != null && isNumeric(arr[45]))
					? arr[45].toString() : null);
			obj.setPurchaseOrderValuePR(
					(arr[110] != null && isNumeric(arr[110]))
							? arr[110].toString() : null);
		}

		// need else condition also

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
	
	public static boolean isNumeric(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof BigDecimal || obj instanceof BigInteger) {
			return true;
		}
		try {
			new BigDecimal(obj.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
