package com.ey.advisory.app.gstr2b;

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

import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Slf4j
@Component("Gstr2bCompleteReport")
public class Gstr2bCompleteReport {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository configRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	public Pair<String, String> getGstr2bCompleteReport(List<String> gstins,
			List<String> taxPeriodList, String itc, Long configId)
			throws IOException {

		List<String> taxPeriods = new ArrayList<>();

		for (String tp : taxPeriodList) {
			String drvTxPrd = GenUtil.convertTaxPeriodToInt(tp).toString();
			taxPeriods.add(drvTxPrd);
		}

		String fullPath = null;
		File tempDir = null;
		Pair<String, String> uploadedDocName = null;
		String reportType = null;

		// "GSTR2B_ItcNonAvailable_ReqID_Timestamp"

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get GSTR2B Report Details with gstin:'%s'"
							+ ",taxperiod:'%s', itc:'%s'",
					gstins, taxPeriods, itc);
			LOGGER.debug(msg);
		}
		if (itc != null && !itc.isEmpty() && itc.equalsIgnoreCase("ITC")) {
			reportType = "GSTR2B_ItcAvailable_";
		}
		if (itc != null && !itc.isEmpty() && itc.equalsIgnoreCase("NITC")) {
			reportType = "GSTR2B_ItcNonAvailable_";
		}
		if (itc != null && !itc.isEmpty() && itc.equalsIgnoreCase("ALL")) {
			reportType = "GSTR2B_CompleteReport_";
		}
		if (itc != null && !itc.isEmpty() && itc.equalsIgnoreCase("REJ")) {
			reportType = "GSTR2B_Rejected_Report_";
		}

		try {

			tempDir = createTempDir();

			StoredProcedureQuery storedProc = null;

			storedProc = entityManager
					.createStoredProcedureQuery("USP_GETGSTR2B_RPT");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Excecuting proc USP_GETGSTR2B_RPT");
			}

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("P_RETPRD_LIST",
					String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("ITC", String.class,
					ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST", String.join(",", gstins));
			storedProc.setParameter("P_RETPRD_LIST",
					String.join(",", taxPeriods));
			storedProc.setParameter("ITC", itc);
			long dbLoadStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Total Time taken to load the Data"
						+ " from DB is '%d' millisecs," + "  Data count: '%s' ",
						dbLoadTimeDiff, records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ getUniqueFileName(reportType + configId) + ".csv";
				List<Gstr2BCompleteReportDto> gstr2bDataList = new ArrayList<>();

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {
					gstr2bDataList = records.stream().map(o -> convert(o,itc))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								reportType, gstr2bDataList.size());
						LOGGER.debug(msg);
					}
					if (gstr2bDataList != null && !gstr2bDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility
								.getProp("gstr2b.get.complete.report.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2b.get.complete.report.column")
								.split(",");
						ColumnPositionMappingStrategy<Gstr2BCompleteReportDto> mappingStrategy =

								new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(Gstr2BCompleteReportDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2BCompleteReportDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2BCompleteReportDto> beanWriter = builder
								.withMappingStrategy(mappingStrategy)
								.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
								.withLineEnd(CSVWriter.DEFAULT_LINE_END)
								.withEscapechar(
										CSVWriter.DEFAULT_ESCAPE_CHARACTER)
								.build();
						long generationStTime = System.currentTimeMillis();
						beanWriter.write(gstr2bDataList);
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format("Total Time taken to"
									+ " Generate the report is '%d' "
									+ " millisecs," + " Data count:" + " '%s' ",
									generationTimeDiff, records.size());
							LOGGER.debug(msg);
						}

					}
				} catch (Exception ex) {
					String msg = String
							.format("Exception while in Gstr2bCompleteReport while "
									+ "executing the Store Proc for %s", ex);
					LOGGER.error(msg);
					configRepo.updateStatus("No Records Found",
							LocalDateTime.now(), null, configId);
					throw new AppException(msg, ex);
				}

				// Zipping tge file

				String zipFileName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType + "1");

					File zipFile = new File(tempDir, zipFileName);

					/*
					 * uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
					 * ConfigConstants.GSTR2BREPORTS);
					 */
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2bCompleteReport fullPath : %s ", fullPath);
						LOGGER.debug(msg);
					}
					
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Gstr2bCompleteReport zipFile : %s ", zipFile);
						LOGGER.debug(msg);
					}
			
					uploadedDocName = DocumentUtility.uploadFile(zipFile,
							ConfigConstants.GSTR2BREPORTS);

					 if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Gstr2bCompleteReport uploadedDocName : %s ", uploadedDocName);
							LOGGER.debug(msg);
						}
					
				}

			}
		} catch (Exception ex) {
			String msg = String.format("Exception in Gstr2bCompleteReport "
					+ "while executing the Store Proc for %s", ex);
			LOGGER.error(msg, ex);
			configRepo.updateStatus("No Records Found",
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					null, configId);
			throw new AppException(msg, ex);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}

		return uploadedDocName;
	}

	private Gstr2BCompleteReportDto convert(Object[] arr, String itc) {

		Gstr2BCompleteReportDto obj = new Gstr2BCompleteReportDto();
		String tableType = (arr[40] != null) ? arr[40].toString() : null;
		String docType = (arr[4] != null) ? arr[4].toString() : null;
		obj.setBillOfEntryDate((arr[22] != null) ? arr[22].toString() : null);
		obj.setBillOfEntryNumber((arr[21] != null) ? arr[21].toString() : null);
		obj.setBoeAmendmentFlag((arr[23] != null) ? arr[23].toString() : null);
		obj.setBoeRecvDateGstin((arr[19] != null) ? arr[19].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR")
				|| docType.equalsIgnoreCase("ISDC"))) {
			obj.setCessAmount(CheckForNegativeValue(arr[13]));
			obj.setCgstAmount(CheckForNegativeValue(arr[11]));
			obj.setIgstAmount(CheckForNegativeValue(arr[10]));
			obj.setInvoiceValue(CheckForNegativeValue(arr[14]));
			obj.setSgstAmount(CheckForNegativeValue(arr[12]));
			obj.setTaxableValue(CheckForNegativeValue(arr[8]));
		} else {

			obj.setCessAmount((arr[13] != null) ? arr[13].toString() : null);
			obj.setCgstAmount((arr[11] != null) ? arr[11].toString() : null);
			obj.setIgstAmount((arr[10] != null) ? arr[10].toString() : null);
			obj.setInvoiceValue((arr[14] != null) ? arr[14].toString() : null);
			obj.setSgstAmount((arr[12] != null) ? arr[12].toString() : null);
			obj.setTaxableValue((arr[8] != null) ? arr[8].toString() : null);
		}
		obj.setItcAvailability((arr[35] != null) ? arr[35].toString() : null);
		obj.setReasonForItcAvailability(
				(arr[36] != null) ? arr[36].toString() : null);
		obj.setRecipientGSTIN((arr[1] != null) ? arr[1].toString() : null);
		obj.setSupplierGSTIN((arr[2] != null) ? arr[2].toString() : null);
		obj.setBoeRefDateIceGate((arr[18] != null) ? arr[18].toString() : null);
		obj.setDate2bGenerationDate(
				(arr[29] != null) ? "'".concat(arr[29].toString()) : null);
		obj.setDifferentialPercentage(
				(arr[32] != null) ? arr[32].toString() : null);
		obj.setDocumentDate((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocumentNumber(
				(arr[6] != null) ? "'".concat(arr[6].toString()) : null);
		obj.setDocumentType((arr[4] != null) ? arr[4].toString() : null);
		obj.setGstrFilingDate((arr[31] != null) ? arr[31].toString() : null);
		obj.setGstrFilingPeriod((arr[30] != null) ? arr[30].toString() : null);
		obj.setLineNumber((arr[17] != null) ? arr[17].toString() : null);
		obj.setOriginalDocumentDate(
				(arr[25] != null) ? arr[25].toString() : null);
		obj.setOriginalDocumentNumber(
				(arr[24] != null) ? arr[24].toString() : null);
		obj.setOriginalDocumentType(
				(arr[26] != null) ? arr[26].toString() : null);
		obj.setOriginalInvoiceDate(
				(arr[28] != null) ? arr[28].toString() : null);
		obj.setOriginalInvoiceNumber(
				(arr[27] != null) ? arr[27].toString() : null);
		obj.setPortCode((arr[20] != null) ? arr[20].toString() : null);
		obj.setPos((arr[15] != null) ? "'".concat(arr[15].toString()) : null);
		obj.setReturnPeriod(
				(arr[0] != null) ? "'".concat(arr[0].toString()) : null);
		obj.setReverseChargeFlag((arr[33] != null) ? arr[33].toString() : null);
		obj.setStateName((arr[16] != null) ? arr[16].toString() : null);
		obj.setSupplierName((arr[3] != null) ? arr[3].toString() : null);
		obj.setSupplyType((arr[5] != null) ? arr[5].toString() : null);
		obj.setTaxRate((arr[9] != null) ? arr[9].toString() : null);
		obj.setSourceTypeofIRN((arr[37] != null) ? arr[37].toString() : null);
		obj.setIrnNumber((arr[38] != null) ? arr[38].toString() : null);
		obj.setIrnGenerationDate((arr[39] != null) ? arr[39].toString() : null);
//		obj.setTableType((arr[40] != null) ? arr[40].toString() : null);
		
		if (!Strings.isNullOrEmpty(itc) && itc.equalsIgnoreCase("REJ")) {
			if (tableType != null && !tableType.isEmpty() && !tableType.contains("Rejected")){
				tableType=tableType+"_Rejected";
			}
		}
		obj.setTableType(tableType);
		obj.setRemarks((arr[41] != null) ? arr[41].toString() : null);
		obj.setImsStatus((arr[42] != null) ? arr[42].toString() : null);
		obj.setItcRedReq((arr[43] != null) ? arr[43].toString() : null);
		obj.setDeclaredIgst((arr[44] != null) ? arr[44].toString() : null);
		obj.setDeclaredCgst((arr[45] != null) ? arr[45].toString() : null);
		obj.setDeclaredSgst((arr[46] != null) ? arr[46].toString() : null);
		obj.setDeclaredCess((arr[47] != null) ? arr[47].toString() : null);
		
		return obj;
	}

	private static File createTempDir() throws IOException {

		String tempFolderPrefix = "GSTR2B Complete Report";
		return Files.createTempDirectory(tempFolderPrefix).toFile();
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

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if (!value.toString().isEmpty()) {
					return "-" + value.toString().replaceFirst("-", "");
				} else {
					return null;
				}
			}
		}
		return null;
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
