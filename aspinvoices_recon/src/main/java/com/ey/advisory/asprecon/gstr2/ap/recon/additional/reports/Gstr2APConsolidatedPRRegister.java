package com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports;

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
import java.util.Date;
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

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2Recon2APRReportCommonService;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ibm.icu.text.SimpleDateFormat;
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
@Component("Gstr2APConsolidatedPRRegister")
public class Gstr2APConsolidatedPRRegister {

	// Consolidated_PR_Register

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
	
	private static final String CONF_KEY = "gstr2.recon.2bpr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getGstr2ConsolidatedPRRegister(Long configId,
			Pair<List<String>, String> distinctPanAndDate) throws IOException {
		
		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = null;
		String msg = null;
		Writer writer = null;
		boolean isReportAvailable = false;

		if (LOGGER.isDebugEnabled()) {
			msg = String
					.format("Get Consolidated_PR_Register Recon Report Details "
							+ "with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = "Consolidated PR Register";
		String reportTypeUi = "Consolidated_PR_Register";

		tempDir = createTempDir(configId, reportTypeUi);

		Integer chunkSize = getChunkSize();

		fullPath = tempDir.getAbsolutePath() + File.separator
				+ getUniqueFileName(reportType) + "_1" + ".csv";

		Gstr2ReconAddlReportsEntity chunkDetails = addlReportRepo
				.getChunckSizeforReportType(configId, reportTypeUi);

		Pair<Integer, Integer> maxReportSizes = commonService
				.getChunkingSizes();

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Config ID is '%s',"
							+ " Created temporary directory to generate "
							+ "zip file: %s",
					configId.toString(), tempDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		
		Integer chunks = 0;

		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_INS_CHUNK_AP_PR_REGISTER");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_REPORT_TYPE",
					"Consolidated_PR_Register");

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_AUTO_2APR_INS_CHUNK_AP_PR_REGISTER: '%s'",
						configId.toString());
				LOGGER.debug(msg);
			}

			 chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_AUTO_2APR_INS_CHUNK_AP_PR_REGISTER: configId '%d', "
						+ "noOfChunk %d ", configId, noOfChunk);
				LOGGER.debug(msg);
			}

		} catch (Exception ex) {

			msg = String.format(
					"Error while executing chunking proc for "
							+ "Consolidated_PR_Register  configId %d",
					configId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}
		
		noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

		if (noOfChunk > 0) {

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr2.generate.recon.report.header.template");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("gstr2.generate.recon.report.column.mapping")
						.split(",");
				StatefulBeanToCsv<Gstr2InitiateReconReportDto> beanWriter 
				= getBeanWriter(
						columnMappings, writer);
				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_AUTO_2APR_RPT_CONS_PR_REGISTER");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					// startId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_ST_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_ST_ID", 0L);

					// EndId
					storedProc.registerStoredProcedureParameter(
							"P_RECON_LINK_END_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_LINK_END_ID", 0L);

					storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
							Integer.class, ParameterMode.IN);
					storedProc.setParameter("P_CHUNK_VALUE", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ "reportType is %s, " + " chunkNo is %d ",
								configId.toString(), reportType, j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")

					List<Object[]> records = storedProc.getResultList();

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

						isReportAvailable = true;
						List<Gstr2InitiateReconReportDto> reconDataList = records
								.stream()
								.map(o -> convert(o,distinctPanAndDate))
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
										reportTypeUi, 
										maxReportSizes.getValue1());
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
									chunkDetails.getId(), configId, 
									reportTypeUi,
									maxReportSizes.getValue1());
							break;
						}

					}

				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the proc for "
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

	private StatefulBeanToCsv<Gstr2InitiateReconReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr2InitiateReconReportDto> 
		mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr2InitiateReconReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr2InitiateReconReportDto> builder = 
				new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr2InitiateReconReportDto> beanWriter = builder
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

	private Gstr2InitiateReconReportDto convert(Object[] arr,
			Pair<List<String>, String> distinctPanAndDate) {

		Gstr2InitiateReconReportDto obj = new Gstr2InitiateReconReportDto();

		obj.setSuggestedFMResponse((arr[0] != null)
				? (NumberFomatUtil.isNumber(arr[0])
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[0].toString())
						: arr[0].toString())
				: null);
		obj.setForceMatchResponse((arr[1] != null)
				? (NumberFomatUtil.isNumber(arr[1])
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[1].toString())
						: arr[1].toString())
				: null);
		obj.setTaxPeriodforGSTR3B((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);

		obj.setComments((arr[3] != null) ? arr[3].toString() : null);
		obj.setMatchingScoreOutof11(
				(arr[4] != null) ? arr[4].toString() : null);
		obj.setMatchReason((arr[5] != null) ? arr[5].toString() : null);
		obj.setMismatchReason((arr[6] != null) ? arr[6].toString() : null);
		obj.setReportCategory((arr[7] != null) ? arr[7].toString() : null);
		obj.setReportType((arr[8] != null) ? arr[8].toString() : null);
		obj.setErpReportType((arr[9] != null) ? arr[9].toString() : null);
		obj.setPreviousReportType2A(
				(arr[10] != null) ? arr[10].toString() : null);
		obj.setPreviousReportTypePR(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setTaxPeriod2A(
				(arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setTaxPeriod2B(
				(arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setTaxPeriodPR(
				(arr[14] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[14].toString()) : null);
		obj.setCalendarMonth(
				(arr[15] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[15].toString()) : null);
		obj.setRecipientGstin2A((arr[16] != null) ? arr[16].toString() : null);
		obj.setRecipientGstinPR((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplierPan2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setSupplierPanPR((arr[19] != null) ? arr[19].toString() : null);
		obj.setSupplierGstin2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setSupplierGstinPR((arr[21] != null) ? arr[21].toString() : null);
		obj.setSupplierName2A((arr[22] != null) ? arr[22].toString() : null);
		obj.setSupplierTradeName2A(
				(arr[23] != null) ? arr[23].toString() : null);
		obj.setSupplierNamePR((arr[24] != null) ? arr[24].toString() : null);
		String docType2A = (arr[25] != null) ? arr[25].toString() : null;
		obj.setDocType2A(docType2A);
		String docTypePR = (arr[26] != null) ? arr[26].toString() : null;
		obj.setDocTypePR(docTypePR);
		obj.setDocumentNumber2A((arr[27] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[27].toString()) : null);
		obj.setDocumentNumberPR((arr[28] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[28].toString()) : null);
		obj.setDocumentDate2A((arr[29] != null) ? arr[29].toString() : null);
		obj.setDocumentDatePR((arr[30] != null) ? arr[30].toString() : null);
		obj.setPos2A((arr[31] != null) ? arr[31].toString() : null);
		obj.setPosPR((arr[32] != null) ? arr[32].toString() : null);

		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValue2A(CheckForNegativeValue(arr[33]));
			obj.setIGST2A(CheckForNegativeValue(arr[35]));
			obj.setCGST2A(CheckForNegativeValue(arr[37]));
			obj.setSGST2A(CheckForNegativeValue(arr[39]));
			obj.setCess2A(CheckForNegativeValue(arr[41]));
			obj.setTotalTax2A(CheckForNegativeValue(arr[43]));
			obj.setInvoiceValue2A(CheckForNegativeValue(arr[45]));
		} else {
			obj.setTaxableValue2A(
					(arr[33] != null) ? arr[33].toString() : null);
			obj.setIGST2A((arr[35] != null) ? arr[35].toString() : null);
			obj.setCGST2A((arr[37] != null) ? arr[37].toString() : null);
			obj.setSGST2A((arr[39] != null) ? arr[39].toString() : null);
			obj.setCess2A((arr[41] != null) ? arr[41].toString() : null);
			obj.setTotalTax2A((arr[43] != null) ? arr[43].toString() : null);
			obj.setInvoiceValue2A(
					(arr[45] != null) ? arr[45].toString() : null);
		}

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValuePR(CheckForNegativeValue(arr[34]));
			obj.setIGSTPR(CheckForNegativeValue(arr[36]));
			obj.setCGSTPR(CheckForNegativeValue(arr[38]));
			obj.setSGSTPR(CheckForNegativeValue(arr[40]));
			obj.setCessPR(CheckForNegativeValue(arr[42]));
			obj.setTotalTaxPR(CheckForNegativeValue(arr[44]));
			obj.setInvoiceValuePR(CheckForNegativeValue(arr[46]));
		} else {
			obj.setTaxableValuePR(
					(arr[34] != null) ? arr[34].toString() : null);
			obj.setIGSTPR((arr[36] != null) ? arr[36].toString() : null);
			obj.setCGSTPR((arr[38] != null) ? arr[38].toString() : null);
			obj.setSGSTPR((arr[40] != null) ? arr[40].toString() : null);
			obj.setCessPR((arr[42] != null) ? arr[42].toString() : null);
			obj.setTotalTaxPR((arr[44] != null) ? arr[44].toString() : null);
			obj.setInvoiceValuePR(
					(arr[46] != null) ? arr[46].toString() : null);
		}
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setAvailableIGST(CheckForNegativeValue(arr[47]));
			obj.setAvailableCGST(CheckForNegativeValue(arr[48]));
			obj.setAvailableSGST(CheckForNegativeValue(arr[49]));
			obj.setAvailableCESS(CheckForNegativeValue(arr[50]));
		} else {
			obj.setAvailableIGST((arr[47] != null) ? arr[47].toString() : null);
			obj.setAvailableCGST((arr[48] != null) ? arr[48].toString() : null);
			obj.setAvailableSGST((arr[49] != null) ? arr[49].toString() : null);
			obj.setAvailableCESS((arr[50] != null) ? arr[50].toString() : null);
		}


		obj.setItcAvailability2B((arr[51] != null) ? arr[51].toString() : null);
		obj.setReasonForItcUnavailability2B(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setSourceType((arr[53] != null) ? arr[53].toString() : null);
		obj.setGenerationDate2B(
				(arr[54] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[54].toString()) : null);
		obj.setGenerationDate2A(
				(arr[55] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[55].toString()) : null);
		obj.setReconDate(
				(arr[56] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[56].toString()) : null);

		String pan2A = (arr[18] != null) ? arr[18].toString() : null;
		List<String> distinctPan = distinctPanAndDate.getValue0();
		String einvDate = distinctPanAndDate.getValue1();
		if (distinctPan.contains(pan2A))
			obj.setEinvoiceApplicability(
					"Applicable as per NIC Master(Last Updated : " + einvDate
							+ ")");
		else
			obj.setEinvoiceApplicability(
					"Not Applicable as per NIC Master(Last Updated : "
							+ einvDate + ")");
		obj.setSupplierReturnFilingPeriodicity(
				(arr[58] != null) ? arr[58].toString() : "NA");
		obj.setGSTR1FilingStatus((arr[59] != null) ? arr[59].toString() : null);
		String gstr1Date = (arr[60] != null) ? arr[60].toString() : null;
		String newGstr1Format = null;
		if (gstr1Date != null) {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yy");
			Date date;
			try {
				date = format1.parse(gstr1Date);
				newGstr1Format = format2.format(date);
				obj.setGSTR1FilingDate(DownloadReportsConstant.CSVCHARACTER
						.concat(newGstr1Format));
			} catch (Exception e) {
				LOGGER.error("exception oocured while parsing date ", e);
			}

		} else {
			obj.setGSTR1FilingDate(newGstr1Format);
		}
		obj.setGSTR1FilingPeriod((arr[61] != null) ? arr[61].toString() : null);
		obj.setGSTR3BFilingStatus(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setCancellationDate((arr[63] != null) ? arr[63].toString() : null);
		obj.setOrgInvAmendmentPeriod(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setOrgAmendmentType((arr[65] != null) ? arr[65].toString() : null);
		obj.setCDNDelinkingFlag((arr[66] != null) ? arr[66].toString() : null);
		obj.setReverseChargeFlag2A(
				(arr[67] != null) ? arr[67].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setDifferentialPercentage2A(
				(arr[69] != null) ? arr[69].toString() : null);
		obj.setDifferentialPercentagePR(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setOrgDocNumber2A(addApostrophe(arr[71]));
		obj.setOrgDocNumberPR(addApostrophe(arr[72]));
		obj.setOrgDocDate2A((arr[73] != null) ? arr[73].toString() : null);
		obj.setOrgDocDatePR((arr[74] != null) ? arr[74].toString() : null);
		obj.setOrgSupplierGstinPR(
				(arr[75] != null) ? arr[75].toString() : null);
		obj.setOrgSupplierNamePR((arr[76] != null) ? arr[76].toString() : null);
		obj.setCRDRPreGST2A((arr[77] != null) ? arr[77].toString() : null);
		obj.setCRDRPreGSTPR((arr[78] != null) ? arr[78].toString() : null);
		obj.setBoeReferenceDate((arr[79] != null) ? arr[79].toString() : null);
		obj.setPortCode2A((arr[80] != null) ? arr[80].toString() : null);
		obj.setBillOfEntry2A((arr[81] != null) ? arr[81].toString() : null);
		obj.setBillOfEntryDate2A((arr[82] != null) ? arr[82].toString() : null);
		obj.setBoeAmended((arr[83] != null) ? arr[83].toString() : null);
		obj.setTableType2A((arr[84] != null) ? arr[84].toString() : null);
		obj.setSupplyType2A((arr[85] != null) ? arr[85].toString() : null);
		obj.setSupplyTypePR((arr[86] != null) ? arr[86].toString() : null);
		obj.setUserID((arr[87] != null) ? arr[87].toString() : null);
		obj.setSourceFileName((arr[88] != null) ? arr[88].toString() : null);
		obj.setProfitCentre((arr[89] != null) ? arr[89].toString() : null);
		obj.setPlant((arr[90] != null) ? arr[90].toString() : null);
		obj.setDivision((arr[91] != null) ? arr[91].toString() : null);
		obj.setLocation((arr[92] != null) ? arr[92].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[93] != null) ? arr[93].toString() : null);
		obj.setUserAccess1((arr[94] != null) ? arr[94].toString() : null);
		obj.setUserAccess2((arr[95] != null) ? arr[95].toString() : null);
		obj.setUserAccess3((arr[96] != null) ? arr[96].toString() : null);
		obj.setUserAccess4((arr[97] != null) ? arr[97].toString() : null);
		obj.setUserAccess5((arr[98] != null) ? arr[98].toString() : null);
		obj.setUserAccess6((arr[99] != null) ? arr[99].toString() : null);

		obj.setGLCodeTaxableValue(
				(arr[100] != null) ? arr[100].toString() : null);

		obj.setGLCodeIGST((arr[101] != null) ? arr[101].toString() : null);
		obj.setGLCodeCGST((arr[102] != null) ? arr[102].toString() : null);
		obj.setGLCodeSGST((arr[103] != null) ? arr[103].toString() : null);
		obj.setGLCodeAdvaloremCess(
				(arr[104] != null) ? arr[104].toString() : null);
		obj.setGLCodeSpecificCess(
				(arr[105] != null) ? arr[105].toString() : null);
		obj.setGLCodeStateCess((arr[106] != null) ? arr[106].toString() : null);

		obj.setSupplierType((arr[107] != null) ? arr[107].toString() : null);
		obj.setSupplierCode((arr[108] != null) ? arr[108].toString() : null);
		obj.setSupplierAddress1(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setSupplierAddress2(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setSupplierAddress3(
				(arr[111] != null) ? arr[111].toString() : null);
		obj.setSupplierAddress4(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setStateApplyingCess(
				(arr[113] != null) ? arr[113].toString() : null);
		obj.setPortCodePR((arr[114] != null) ? arr[114].toString() : null);
		obj.setBillOfEntryPR((arr[115] != null) ? arr[115].toString() : null);
		obj.setBillOfEntryDatePR(
				(arr[116] != null) ? arr[116].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setCIFValue(CheckForNegativeValue(arr[117]));
			obj.setCustomDuty(CheckForNegativeValue(arr[118]));
		} else {
			obj.setCIFValue((arr[117] != null) ? arr[117].toString() : null);
			obj.setCustomDuty((arr[118] != null) ? arr[118].toString() : null);
		}
		obj.setHSNorSAC((arr[119] != null) ? arr[119].toString() : null);
		obj.setItemCode((arr[120] != null) ? arr[120].toString() : null);
		obj.setItemDescription((arr[121] != null) ? arr[121].toString() : null);
		obj.setCategoryOfItem((arr[122] != null) ? arr[122].toString() : null);
		obj.setUnitOfMeasurement(
				(arr[123] != null) ? arr[123].toString() : null);
		obj.setQuantity((arr[124] != null) ? arr[124].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setAdvaloremCessAmount(CheckForNegativeValue(arr[125]));
			obj.setSpecificCessAmount(CheckForNegativeValue(arr[126]));
			obj.setStateCessAmount(CheckForNegativeValue(arr[127]));
			obj.setOtherValue(CheckForNegativeValue(arr[128]));
		} else {
			obj.setAdvaloremCessAmount(
					(arr[125] != null) ? arr[125].toString() : null);
			obj.setSpecificCessAmount(
					(arr[126] != null) ? arr[126].toString() : null);
			obj.setStateCessAmount(
					(arr[127] != null) ? arr[127].toString() : null);
			obj.setOtherValue((arr[128] != null) ? arr[128].toString() : null);
		}
		obj.setClaimRefundFlag((arr[129] != null) ? arr[129].toString() : null);
		obj.setAutoPopulateToRefund(
				(arr[130] != null) ? arr[130].toString() : null);
		obj.setAdjustementReferenceNo(
				(arr[131] != null) ? arr[131].toString() : null);
		obj.setAdjustementReferenceDate(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setEligibilityIndicator(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setCommonSupplyIndicator(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setITCEntitlement((arr[135] != null) ? arr[135].toString() : null);
		obj.setITCReversalIdentifier(
				(arr[136] != null) ? arr[136].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[137] != null) ? arr[137].toString() : null);
		obj.setPurchaseVoucherNumber(
				(arr[138] != null) ? arr[138].toString() : null);
		obj.setPurchaseVoucherDate(
				(arr[139] != null) ? arr[139].toString() : null);
		obj.setPostingDate((arr[140] != null) ? arr[140].toString() : null);
		obj.setContractNumber((arr[141] != null) ? arr[141].toString() : null);
		obj.setContractDate((arr[142] != null) ? arr[142].toString() : null);
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setContractValue(CheckForNegativeValue(arr[143]));
		} else {
			obj.setContractValue(
					(arr[143] != null) ? arr[143].toString() : null);
		}
		obj.setUserDefinedField1(
				(arr[144] != null) ? arr[144].toString() : null);
		obj.setUserDefinedField2(
				(arr[145] != null) ? arr[145].toString() : null);
		obj.setUserDefinedField3(
				(arr[146] != null) ? arr[146].toString() : null);
		obj.setUserDefinedField4(
				(arr[147] != null) ? arr[147].toString() : null);
		obj.setUserDefinedField5(
				(arr[148] != null) ? arr[148].toString() : null);
		obj.setUserDefinedField6(
				(arr[149] != null) ? arr[149].toString() : null);
		obj.setUserDefinedField7(
				(arr[150] != null) ? arr[150].toString() : null);
		obj.setUserDefinedField8(
				(arr[151] != null) ? arr[151].toString() : null);
		obj.setUserDefinedField9(
				(arr[152] != null) ? arr[152].toString() : null);
		obj.setUserDefinedField10(
				(arr[153] != null) ? arr[153].toString() : null);
		obj.setUserDefinedField11(
				(arr[154] != null) ? arr[154].toString() : null);
		obj.setUserDefinedField12(
				(arr[155] != null) ? arr[155].toString() : null);
		obj.setUserDefinedField13(
				(arr[156] != null) ? arr[156].toString() : null);
		obj.setUserDefinedField14(
				(arr[157] != null) ? arr[157].toString() : null);
		obj.setUserDefinedField15(
				(arr[158] != null) ? arr[158].toString() : null);
		obj.setUserDefinedField28(
				(arr[159] != null) ? arr[159].toString() : null);
		obj.setEWayBillNumber((arr[160] != null) ? arr[160].toString() : null);
		obj.setEWayBillDate(
				(arr[161] != null) ? "'".concat(arr[161].toString()) : null);
		obj.setMatchingID((arr[162] != null) ? arr[162].toString() : null);
		obj.setRequestID(
				(arr[163] != null) ? "'".concat(arr[163].toString()) : null);
		obj.setIDPR((arr[164] != null) ? arr[164].toString() : null);
		obj.setID2A((arr[165] != null) ? arr[165].toString() : null);
		obj.setInvoiceKeyPR((arr[166] != null) ? arr[166].toString() : null);
		obj.setInvoiceKeyA2((arr[167] != null) ? arr[167].toString() : null);
		obj.setReferenceIDPR((arr[168] != null) ? arr[168].toString() : null);
		obj.setReferenceID2A((arr[169] != null) ? arr[169].toString() : null);
		obj.setIrn2A((arr[170] != null) ? arr[170].toString() : null);
		obj.setIrnPR((arr[171] != null) ? arr[171].toString() : null);
		obj.setIrnDate2A((arr[172] != null) ? arr[172].toString() : null);
		obj.setIrnDatePR((arr[173] != null) ? arr[173].toString() : null);
		obj.setApprovalStatus((arr[174] != null) ? arr[174].toString() : null);
		obj.setRecordStatus((arr[175] != null) ? arr[175].toString() : null);
		obj.setKeyDescription((arr[176] != null) ? arr[176].toString() : null);

		obj.setVendorComplianceTrend(
				(arr[177] != null) ? arr[177].toString() : "NA");
		obj.setSourceIdentifier(
				(arr[178] != null) ? arr[178].toString() : null);
		obj.setCompanyCode((arr[179] != null) ? arr[179].toString() : null);
		obj.setVendorType((arr[180] != null) ? arr[180].toString() : null);
		obj.setVendorRiskCategory(
				(arr[181] != null) ? arr[181].toString() : null);
		obj.setVendorPaymentTerms(
				(arr[182] != null) ? arr[182].toString() : null);
		obj.setVendorRemarks((arr[183] != null) ? arr[183].toString() : null);
		obj.setReverseIntegratedDate(
				(arr[184] != null) ? arr[184].toString() : null);
		obj.setQrCodeCheck((arr[185] != null) ? arr[185].toString() : null);
		obj.setQrCodeValidationResult(
				(arr[186] != null) ? arr[186].toString() : null);
		obj.setQrCodeMatchCount(
				(arr[187] != null) ? arr[187].toString() : null);
		obj.setQrCodeMismatchCount(
				(arr[188] != null) ? arr[188].toString() : null);
		obj.setQrMismatchAttrs((arr[189] != null) ? arr[189].toString() : null);
		obj.setGstr3bFilingDate(
				(arr[190] != null) ? arr[190].toString() : null);
		obj.setSupplierGstinStatus(
				(arr[191] != null) ? arr[191].toString() : null);
		obj.setSystemDefinedField1(
				(arr[192] != null) ? arr[192].toString() : null);
		obj.setSystemDefinedField2(
				(arr[193] != null) ? arr[193].toString() : null);
		obj.setSystemDefinedField3(
				(arr[194] != null) ? arr[194].toString() : null);
		obj.setSystemDefinedField4(
				(arr[195] != null) ? arr[195].toString() : null);
		obj.setSystemDefinedField5(
				(arr[196] != null) ? arr[196].toString() : null);
		obj.setSystemDefinedField6(
				(arr[197] != null) ? arr[197].toString() : null);
		obj.setSystemDefinedField7(
				(arr[198] != null) ? arr[198].toString() : null);
		obj.setSystemDefinedField8(
				(arr[199] != null) ? arr[199].toString() : null);
		obj.setSystemDefinedField9(
				(arr[200] != null) ? arr[200].toString() : null);
		obj.setSystemDefinedField10(
				(arr[201] != null) ? arr[201].toString() : null);
		return obj;

	}

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null
						? (((Integer) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof Long) {
				return (value != null
						? (((Long) value > 0) ? "-" + value.toString()
								: value.toString())
						: null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString()
								: value.toString())
						: null);
			} else {
				if(!value.toString().isEmpty()){
					return "-" + value.toString().replaceFirst("-", "");
				} else{
					return null;
				}
			}
		}
		return null;
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
