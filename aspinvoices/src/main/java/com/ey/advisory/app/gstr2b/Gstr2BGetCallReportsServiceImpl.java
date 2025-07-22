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
import java.time.format.DateTimeFormatter;
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

import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr2BGetCallReportsServiceImpl")
public class Gstr2BGetCallReportsServiceImpl
		implements Gstr2BGetCallReportsService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository requestRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Gstr2BGetCallReportsCommonService commonService;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CONF_KEY = "gstr2b.generate.report.chunk.size";
	private static final String CONF_CATEG = "GSTR2B_REPORTS_CHUNK";
	
	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void generateReport(Long requestId) {

		String fullPath = null;
		File tempDir = null;
		String msg = null;
		Writer writer = null;
		boolean isReportAvailable = false;
		Integer noOfChunk = 0;
		try {
			tempDir = createTempDir();
			String reportType = "";
			requestRepo.updateStatus(
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS,
					LocalDateTime.now(), null, requestId);

		 	Gstr2bGet2bRequestStatusEntity reqEntity = requestRepo
					.findByReqId(requestId);
			String rptType = reqEntity.getReportType();
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("ITC")) {
				reportType = "GSTR2B_ItcAvailable_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("NITC")) {
				reportType = "GSTR2B_ItcNonAvailable_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("ALL")) {
				reportType = "GSTR2B_CompleteReport_";
			}
			if (rptType != null && !rptType.isEmpty()
					&& rptType.equalsIgnoreCase("REJ")) {
				reportType = "GSTR2B_RejectedReport_";
			}
			Integer chunkValue = getChunkSize();
			fullPath = tempDir.getAbsolutePath()
					+ File.separator + reportType
					+ dtf.format(LocalDateTime.now()) + "_"
					+ "1.csv";
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GETGSTR2B_INS_CHUNK_RPT");

			storedProc.registerStoredProcedureParameter("REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("REPORT_DOWNLOAD_ID", requestId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkValue);

			@SuppressWarnings("unchecked")
			Integer chunksize = (Integer) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(" executed USP_GETGSTR2B_INS_CHUNK_RPT - "
						+ "chunksize %d", chunksize);
				LOGGER.debug(msg);
			}

			noOfChunk = chunksize;

			if (chunksize != 0) {
				int j = 0;
				try {
					writer = new BufferedWriter(new FileWriter(fullPath),
							CSV_BUFFER_SIZE);

					String invoiceHeadersTemplate = commonUtility
							.getProp("gstr2b.get.complete.report.header");
					writer.append(invoiceHeadersTemplate);
					String[] columnMappings = commonUtility
							.getProp("gstr2b.get.complete.report.column")
							.split(",");

					StatefulBeanToCsv<Gstr2BCompleteReportDto> beanWriter = getBeanWriter(
							columnMappings, writer);
					int fileIndex = 1;
					int count = 0;
					int maxLimitPerFile = commonService.getChunkSize();
					while (j < noOfChunk) {
						j++;

						StoredProcedureQuery dispProc = entityManager
								.createStoredProcedureQuery(
										"USP_GETGSTR2B_DISP_CHUNK_RPT");

						dispProc.registerStoredProcedureParameter(
								"REPORT_DOWNLOAD_ID", Long.class,
								ParameterMode.IN);

						dispProc.setParameter("REPORT_DOWNLOAD_ID", requestId);

						dispProc.registerStoredProcedureParameter("P_CHUNK_VAL",
								Integer.class, ParameterMode.IN);

						dispProc.setParameter("P_CHUNK_VAL", j);

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> records = dispProc.getResultList();
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data"
											+ " from DB is '%d' millisecs,"
											+ "  Data count: - '%s'",
									dbLoadTimeDiff, records.size());
							LOGGER.debug(msg);
						}

						if (records != null && !records.isEmpty()) {

							isReportAvailable = true;

							List<Gstr2BCompleteReportDto> dataList = new ArrayList<>();

							dataList = records.stream()
									.map(o -> convert(o, rptType))
									.collect(Collectors
											.toCollection(ArrayList::new));

							if (LOGGER.isDebugEnabled()) {
								msg = String.format("row count: - '%s'",
										dataList.size());
								LOGGER.debug(msg);
							}

							if (count >= maxLimitPerFile) {
								flushWriter(writer);

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking Gstr2BGetCallReportsCommonService to"
													+ " upload a zip file : "
													+ "requestId {}, ReportName"
													+ " {}, ",
											requestId);
								}

								// Zipping
								if (isReportAvailable) {

									commonService.chunkZipFiles(tempDir, requestId, requestId,
											reportType, 1);
								}
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking "
													+ "commonService to upload "
													+ "a zip file : requestId "
													+ "{}, deleted file "
													+ "from TempDir, ",
											requestId);
								}

								count = 0;
								fileIndex += 1;
								fullPath = tempDir.getAbsolutePath()
										+ File.separator + reportType
										+ dtf.format(LocalDateTime.now()) + "_"
										+ fileIndex + ".csv";
								writer = new BufferedWriter(
										new FileWriter(fullPath),
										CSV_BUFFER_SIZE);
								writer.append(invoiceHeadersTemplate);
								beanWriter = getBeanWriter(columnMappings,
										writer);
							}
							count += dataList.size();
							beanWriter.write(dataList);

							long generationStTime = System.currentTimeMillis();
							long generationEndTime = System.currentTimeMillis();
							long generationTimeDiff = (generationEndTime
									- generationStTime);
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs, and Data count:"
												+ "  - '%s'",
										generationTimeDiff, records.size());
								LOGGER.debug(msg);
							}
							if (noOfChunk == 1 || j == noOfChunk) {
								flushWriter(writer);
								commonService.chunkZipFiles(tempDir, requestId, requestId,
										reportType, 1);
								break;
							}

						}

					}

				} catch (Exception ex) {
					LOGGER.error("Exception while executing the proc for "
							+ "RequestID  :{}", requestId, ex);
					deleteTemporaryDirectory(tempDir);
					throw new AppException(ex);
				}

			} else {
				requestRepo.updateStatus(ReportStatusConstants.NO_DATA_FOUND,
						LocalDateTime.now(), null, requestId);
			}

			requestRepo.updateStatus(ReportStatusConstants.REPORT_GENERATED,
					LocalDateTime.now(), null, requestId);

			deleteTemporaryDirectory(tempDir);
		} catch (Exception ex) {
			LOGGER.error("Exception while executing the proc for "
					+ "RequestID  :{}", requestId, ex);
			deleteTemporaryDirectory(tempDir);
			throw new AppException(ex);
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private String addApostrophe(Object o) {
		if (o != null) {
			if (!(o.toString().isEmpty())) {
				return DownloadReportsConstant.CSVCHARACTER
						.concat(o.toString());
			} else
				return null;
		}
		return null;
	}

	private StatefulBeanToCsv<Gstr2BCompleteReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr2BCompleteReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr2BCompleteReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr2BCompleteReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr2BCompleteReportDto> beanWriter = builder
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
		// obj.setTableType((arr[40] != null) ? arr[40].toString() : null);

		if (!Strings.isNullOrEmpty(itc) && itc.equalsIgnoreCase("REJ")) {
			if (tableType != null && !tableType.isEmpty()
					&& !tableType.contains("Rejected")) {
				tableType = tableType + "_Rejected";
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
	
	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}
	
	
}
