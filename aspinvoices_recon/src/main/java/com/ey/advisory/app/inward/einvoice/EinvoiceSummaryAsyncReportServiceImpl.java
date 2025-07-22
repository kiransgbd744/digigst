
package com.ey.advisory.app.inward.einvoice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.ims.EinvoiceSummaryChunkReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Component("EinvoiceSummaryAsyncReportServiceImpl")
@Slf4j
public class EinvoiceSummaryAsyncReportServiceImpl
		implements AsyncReportDownloadService {

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
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("EinvoiceSummaryChunkReportServiceImpl")
	private EinvoiceSummaryChunkReportService chunkService;

	private static final String CONF_KEY = "einvoice.detailed.lineitem.report.chunk.size";
	private static final String CONF_CATEG = "DETAILED_LINEITEM_REPORTS";
	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void generateReports(Long id) {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Writer writer = null;
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Einvoice summary Report Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

		Optional<FileStatusDownloadReportEntity> opt = downloadRepository
				.findById(id);

		FileStatusDownloadReportEntity entity = opt.get();

		String reportType = entity.getReportType();

		String type = entity.getType();

		Pair<Integer, Integer> maxReportSizes = chunkService.getChunkingSizes();

		boolean isReportAvailable = false;

		try {
			tempDir = createTempDir();

			if ("SUMMARY".equalsIgnoreCase(type)) {
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "Inward E-invoice_Summary Report_" + id + "_1"
						+ ".csv";
			} else {
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "Inward E-invoice_Detailed (Invoice Level) Report_"
						+ id + "_1" + ".csv";
			}
			Integer chunkSize = getChunkSize();

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Config ID is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						id.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}

			// chunk value

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_INSERT_CHUNK_IRN_SUMMARY_REPORT");

			storedProc.registerStoredProcedureParameter("P_REQUEST_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REQUEST_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_INSERT_CHUNK_IRN_SUMMARY_REPORT: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_INSERT_CHUNK_IRN_SUMMARY_REPORT: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate Report";
				LOGGER.error(msg);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				return;
			}

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				String invoiceHeadersTemplate = commonUtility
						.getProp("einvoice.summary.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("einvoice.summary.report.download.mapping")
						.split(",");

				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				StatefulBeanToCsv<EinvoiceSummaryReportDto> beanWriter = getBeanWriter(
						columnMappings, writer);

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcSummaryReport = entityManager
							.createStoredProcedureQuery(
									"USP_DISPLAY_CHUNK_IRN_SUMMARY_REPORT");

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_REQUEST_ID", Long.class, ParameterMode.IN);

					storedProcSummaryReport.setParameter("P_REQUEST_ID", id);

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
					storedProcSummaryReport.setParameter("P_CHUNK_NUM", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")
					List<Object[]> records = storedProcSummaryReport
							.getResultList();

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
						List<EinvoiceSummaryReportDto> einvoiceDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, einvoiceDataList.size());
							LOGGER.debug(msg);
						}

						if (count >= maxLimitPerFile) {
							flushWriter(writer);
							if ("SUMMARY".equalsIgnoreCase(type)) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking Einvoice Summary "
													+ "chunkService to"
													+ " upload a zip file : "
													+ "configId {}, ReportName"
													+ " {}, ",
											id,
											"Inward E-invoice_Summary Report");
								}
							} else {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking Einvoice Summary "
													+ "chunkService to"
													+ " upload a zip file : "
													+ "configId {}, ReportName"
													+ " {}, ",
											id,
											"Inward E-invoice_Detailed (Invoice Level) Report");
								}
							}
							// Zipping
							if (isReportAvailable) {
								if ("SUMMARY".equalsIgnoreCase(type)) {
									chunkService.chunkZipFiles(tempDir, id, id,
											"Inward E-invoice_Summary Report",
											maxReportSizes.getValue1(),
											fileIndex);
								} else {
									chunkService.chunkZipFiles(tempDir, id, id,
											"Inward E-invoice_Detailed (Invoice Level) Report",
											maxReportSizes.getValue1(),
											fileIndex);
								}
							}
							if ("SUMMARY".equalsIgnoreCase(type)) {

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking Summary "
													+ "chunkService to upload "
													+ "a zip file : configId "
													+ "{}, ReportName {} "
													+ "deleted file "
													+ "from TempDir, ",
											id,
											"Inward E-invoice_Summary Report");
								}
							} else {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking Summary "
													+ "chunkService to upload "
													+ "a zip file : configId "
													+ "{}, ReportName {} "
													+ "deleted file "
													+ "from TempDir, ",
											id,
											"Inward E-invoice_Detailed (Invoice Level) Report");
								}
							}
							count = 0;
							fileIndex += 1;
							if ("SUMMARY".equalsIgnoreCase(type)) {
								fullPath = tempDir.getAbsolutePath()
										+ File.separator
										+ getUniqueFileName(
												"Inward E-invoice_Summary Report")
										+ "_" + fileIndex + ".csv";
							} else {
								fullPath = tempDir.getAbsolutePath()
										+ File.separator
										+ getUniqueFileName(
												"Inward E-invoice_Detailed (Invoice Level) Report")
										+ "_" + fileIndex + ".csv";
							}
							writer = new BufferedWriter(
									new FileWriter(fullPath), CSV_BUFFER_SIZE);
							writer.append(invoiceHeadersTemplate);
							beanWriter = getBeanWriter(columnMappings, writer);
						}
						count += einvoiceDataList.size();
						beanWriter.write(einvoiceDataList);

						long generationStTime = System.currentTimeMillis();
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if ("SUMMARY".equalsIgnoreCase(type)) {

							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs,"
												+ " Report Name and Data count:"
												+ " '%s' - '%s'",
										generationTimeDiff,
										"Inward E-invoice_Summary Report",
										records.size());
								LOGGER.debug(msg);
							}
						} else {
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs,"
												+ " Report Name and Data count:"
												+ " '%s' - '%s'",
										generationTimeDiff,
										"Inward E-invoice_Detailed (Invoice Level) Report",
										records.size());
								LOGGER.debug(msg);
							}
						}
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
							if ("SUMMARY".equalsIgnoreCase(type)) {
								chunkService.chunkZipFiles(tempDir, id, id,
										"Inward E-invoice_Summary Report",
										maxReportSizes.getValue1(), fileIndex);
							} else {
								chunkService.chunkZipFiles(tempDir, id, id,
										"Inward E-invoice_Detailed (Invoice Level) Report",
										maxReportSizes.getValue1(), fileIndex);
							}
							break;
						}
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for "
						+ "Report Type :{}", reportType, ex);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				throw new AppException(ex);
			}

			if (noOfChunk == 0) {
				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			} else {

				downloadRepository.updateStatusAndCompltdOn(id,
						ReconStatusConstants.REPORT_GENERATED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}

		} catch (Exception e) {
			String msz = String.format(
					"Exception occured while creating csv file for %d and reportType %s ",
					id, reportType);
			LOGGER.error(msz, e);

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

	private EinvoiceSummaryReportDto convert(Object[] arr) {

		EinvoiceSummaryReportDto obj = new EinvoiceSummaryReportDto();

		obj.setMonth((arr[1] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setRecipientGSTIN((arr[2] != null) ? arr[2].toString() : null);
		obj.setSupplierGSTIN((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocNo((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setDocDate((arr[5] != null) ? arr[5].toString() : null);
		obj.setDocType((arr[6] != null) ? arr[6].toString() : null);

		String docType = (arr[6] != null) ? arr[6].toString() : null;

		obj.setSupplyType((arr[7] != null) ? arr[7].toString() : null);
		obj.setTaxableValue((arr[8] != null)
				? CheckForNegativeValue(arr[8], docType) : null);
		obj.setTotalTax((arr[9] != null)
				? CheckForNegativeValue(arr[9], docType) : null);
		obj.setIgst((arr[10] != null) ? CheckForNegativeValue(arr[10], docType)
				: null);
		obj.setCgst((arr[11] != null) ? CheckForNegativeValue(arr[11], docType)
				: null);
		obj.setSgst((arr[12] != null) ? CheckForNegativeValue(arr[12], docType)
				: null);
		obj.setCess((arr[13] != null) ? CheckForNegativeValue(arr[13], docType)
				: null);
		obj.setTotalInvoiceValue((arr[14] != null)
				? CheckForNegativeValue(arr[14], docType) : null);
		obj.setIrnNum((arr[15] != null) ? arr[15].toString() : null);
		if (arr[16] != null && ("ACT".equalsIgnoreCase(arr[16].toString()))
				|| ("CNL".equalsIgnoreCase(arr[16].toString()))) {
			if ("ACT".equalsIgnoreCase(arr[16].toString())) {
				obj.setIrnStatus("Active");
			} else {
				obj.setIrnStatus("Cancelled");
			}
		}
		obj.setAcknowledgmentNumber(
				(arr[17] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[17].toString()) : null);

		if (arr[18] != null) {

			Timestamp timeStamp = (Timestamp) arr[18];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			obj.setAcknowledgmentDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newdate));
		}

		obj.setEwayBillNumber(
				(arr[19] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[19].toString()) : null);

		if (arr[20] != null) {

			Timestamp timeStamp = (Timestamp) arr[20];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			obj.setEwayBillDate(newdate);

		}
		if (arr[21] != null) {

			Timestamp timeStamp = (Timestamp) arr[21];
			LocalDateTime dt = timeStamp.toLocalDateTime();
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			String newdate = FOMATTER.format(dt);

			obj.setCancellationDate(
					DownloadReportsConstant.CSVCHARACTER.concat(newdate));
		}
		
		obj.setIrpName((arr[22] != null) ? arr[22].toString() : null);
		
		
		return obj;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
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

	private StatefulBeanToCsv<EinvoiceSummaryReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<EinvoiceSummaryReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(EinvoiceSummaryReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<EinvoiceSummaryReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<EinvoiceSummaryReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}

	private String CheckForNegativeValue(Object value, String docType) {

		if (value != null && !Strings.isNullOrEmpty(docType)) {
			if ("CR".equalsIgnoreCase(docType)) {
				if (value instanceof BigDecimal) {
					return (value != null ? ((((BigDecimal) value)
							.compareTo(BigDecimal.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Integer) {
					return (value != null ? (((Integer) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof Long) {
					return (value != null ? (((Long) value > 0)
							? "-" + value.toString() : value.toString())
							: null);
				} else if (value instanceof BigInteger) {
					return (value != null ? ((((BigInteger) value)
							.compareTo(BigInteger.ZERO) > 0)
									? "-" + value.toString() : value.toString())
							: null);
				} else {
					if (!value.toString().isEmpty()) {
						return "-" + value.toString().replaceFirst("-", "");
					} else {
						return null;
					}
				}
			} else
				return value.toString();
		}
		return value.toString();
	}

}
