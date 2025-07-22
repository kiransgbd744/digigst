package com.ey.advisory.app.services.ims;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
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

@Component("ImsRecordsAsyncReportServiceImpl")
@Slf4j
public class ImsRecordsAsyncReportServiceImpl
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
	@Qualifier("ImsRecordsChunkReportServiceImpl")
	private EinvoiceSummaryChunkReportService chunkService;

	private static final String CONF_KEY = "ims.record.report.chunk.size";
	private static final String CONF_CATEG = "IMS_RECORD_REPORTS";
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
		
		String procRptType = "";
		String fileName = "";

		try {
			tempDir = createTempDir();
		/*	DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());*/
			
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");

		    ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));

	        ZonedDateTime istNow = utcNow.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

	        String timeMilli = istNow.format(formatter);

			/*if ("IMSSummary".equalsIgnoreCase(type)) {
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "IMS Summary Report_" + id + "_1"
						+ ".csv";
			} else {*/
//			fullPath = tempDir.getAbsolutePath() + File.separator
//						+ "IMS Records Report_"
//						 + timeMilli + "_1" + ".csv";
				
           if ("IMS Records Report".equalsIgnoreCase(reportType)) {
				
				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"ImsRecordsAsyncReportServiceImpl: IMS Records Report");
					LOGGER.debug(msg);
				}
				
				procRptType = "ALL";
				fileName="IMS Records Report";
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "IMS Records Report_"
						 + timeMilli + "_1" + ".csv";
				
			} else if ("IMS Records Active+Inactive Report".equalsIgnoreCase(reportType)) {
				
				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"ImsRecordsAsyncReportServiceImpl: IMS Records Active+Inactive Report");
					LOGGER.debug(msg);
				}
				
				procRptType = "ALL";
				fileName="IMS Records Active+Inactive Report";
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "IMS Records Active+Inactive Report_"
						 + timeMilli + "_1" + ".csv";
				
			} else if ("IMS Records Active Report".equalsIgnoreCase(reportType)) {
				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"ImsRecordsAsyncReportServiceImpl: IMS Records Active Report");
					LOGGER.debug(msg);
				}
				procRptType = "TRUE";
				fileName="IMS Records Active Report";
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ "IMS Records Active Report_"
						 + timeMilli + "_1" + ".csv";
				
			} else if ("INACTIVE".equalsIgnoreCase(reportType)) {
				procRptType = "FALSE";
				//IMS Records InActive Report
			} else {
				LOGGER.error("Invalid report type");
				throw new Exception("invalid request");
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
			
			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"fileName : %s ,"
								+ " Created with "
								+ "fullPath: %s ",
								fileName, fullPath);
				LOGGER.debug(msg);
			}

			// chunk value

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_IMS_RECORDS_INS_CHUNK");

			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);
			
			storedProc.registerStoredProcedureParameter(
					"RPT_TYPE", String.class, ParameterMode.IN);
			storedProc.setParameter("RPT_TYPE", procRptType);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_IMS_RECORDS_INS_CHUNK: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_IMS_RECORDS_INS_CHUNK: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate Report";
				LOGGER.error(msg);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,LocalDateTime.now());

				return;
			}

			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);
				String invoiceHeadersTemplate = commonUtility
						.getProp("ims.processed.headers");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("ims.processed.column.mapping")
						.split(",");

				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				StatefulBeanToCsv<ImsFileStatusReportDto> beanWriter = getBeanWriter(
						columnMappings, writer);

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcSummaryReport = entityManager
							.createStoredProcedureQuery(
									"USP_IMS_RECORDS_DISP_CHUNK");

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_REPORT_DOWNLOAD_ID", Long.class, ParameterMode.IN);

					storedProcSummaryReport.setParameter("P_REPORT_DOWNLOAD_ID", id);

					storedProcSummaryReport.registerStoredProcedureParameter(
							"P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
					storedProcSummaryReport.setParameter("P_CHUNK_VALUE", j);
					

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
						List<ImsFileStatusReportDto> einvoiceDataList = records
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
							/*if ("IMSSummary".equalsIgnoreCase(type)) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking Ims Summary "
													+ "chunkService to"
													+ " upload a zip file : "
													+ "configId {}, ReportName"
													+ " {}, ",
											id,
											"IMS Records Report");
								}
							} else {*/
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Invoking  Summary "
													+ "chunkService to"
													+ " upload a zip file : "
													+ "configId {}, ReportName"
													+ " {}, ",
											id,
											fileName);
									// "IMS Records Report"
								//}
							}
							// Zipping
							if (isReportAvailable) {
								/*if ("IMSSummary".equalsIgnoreCase(type)) {
									chunkService.chunkZipFiles(tempDir, id, id,
											"IMS Summary Report",
											maxReportSizes.getValue1(),
											fileIndex);
								} else {*/
									chunkService.chunkZipFiles(tempDir, id, id,
											fileName,
											maxReportSizes.getValue1(),
											fileIndex);
									// "IMS Records Report"
								//}
							}
							/*if ("IMSSummary".equalsIgnoreCase(type)) {

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking Summary "
													+ "chunkService to upload "
													+ "a zip file : configId "
													+ "{}, ReportName {} "
													+ "deleted file "
													+ "from TempDir, ",
											id,
											"IMS Summary Report");
								}
							}else {*/
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"After Invoking Summary "
													+ "chunkService to upload "
													+ "a zip file : configId "
													+ "{}, ReportName {} "
													+ "deleted file "
													+ "from TempDir, ",
											id,
											fileName);
									//"IMS Records Report"
								//}
							}
							count = 0;
							fileIndex += 1;
							/*if ("IMSSummary".equalsIgnoreCase(type)) {
								fullPath = tempDir.getAbsolutePath()
										+ File.separator
										+ getUniqueFileName(
												"IMS Summary Report")
										+ "_" + fileIndex + ".csv";
							} else {*/
								fullPath = tempDir.getAbsolutePath()
										+ File.separator
										+ getUniqueFileName(
												fileName)
										+ "_" + fileIndex + ".csv";
								//"IMS Records Report"
							//}
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"fullPath is : %s ",fullPath);
								LOGGER.debug(msg);
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
						if ("IMSSummary".equalsIgnoreCase(type)) {

							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs,"
												+ " Report Name and Data count:"
												+ " '%s' - '%s'",
										generationTimeDiff,
										"IMS Summary Report",
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
										"IMS Records Report",
										records.size());
								LOGGER.debug(msg);
							}
						}
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
							/*if ("IMSSummary".equalsIgnoreCase(type)) {
								chunkService.chunkZipFiles(tempDir, id, id,
										"IMS Summary Report",
										maxReportSizes.getValue1(), fileIndex);
							} else {*/
								chunkService.chunkZipFiles(tempDir, id, id,
										fileName,
										maxReportSizes.getValue1(), fileIndex);
								//"IMS Records Report"
							//}
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

	private ImsFileStatusReportDto convert(Object[] arr) {

        ImsFileStatusReportDto obj = new ImsFileStatusReportDto();
		
		obj.setActionResponse(arr[0] != null ? arr[0].toString() : null);
		obj.setResponseRemarks(arr[1] != null ? arr[1].toString() : null);
		obj.setGstnAction(arr[2] != null ? arr[2].toString() : null);
		obj.setDigiGstAction(arr[3] != null ? arr[3].toString() : null);
		
		if (arr[4] != null && !arr[4].toString().isEmpty()) {

			String timestamp = arr[4].toString();
			if(timestamp.length() > 19){
				 timestamp = timestamp.substring(0,19);
				}
			DateTimeFormatter utcFormatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
					utcFormatter);

			ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
					ZoneId.of("UTC"));

			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istDateTime = utcDateTime
					.withZoneSameInstant(istZone);

			DateTimeFormatter istFormatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String istTime = istDateTime.format(istFormatter);

			obj.setDigiGstActionDateTime(
					DownloadReportsConstant.CSVCHARACTER.concat(istTime));
		}
		
		obj.setSavedToGstn(
				arr[5] != null ? arr[5].toString() : null);
		obj.setAvailableInImsGstn(
				arr[6] != null ? arr[6].toString() : null);
		obj.setTableType(arr[7] != null ? arr[7].toString() : null);
		obj.setRecipientGstin(
				arr[8] != null ? arr[8].toString() : null);
		obj.setSupplierGstin(arr[9] != null ? arr[9].toString() : null);
		obj.setSupplierLegalName(
				arr[10] != null ? arr[10].toString() : null);
		obj.setSupplierTradeName(arr[11] != null ? arr[11].toString() : null);
		obj.setDocumentType(arr[12] != null ? arr[12].toString() : null);
		obj.setDocumentNumber(
				arr[13] != null && !arr[13].toString().isEmpty()
				? "'" + arr[13].toString() : null);
		obj.setDocumentDate(arr[14] != null ? arr[14].toString() : null);
		obj.setTaxableValue(arr[15] != null ? arr[15].toString() : null);
		obj.setIgst(arr[16] != null ? arr[16].toString() : null);
		obj.setCgst(arr[17] != null ? arr[17].toString() : null);
		obj.setSgst(arr[18] != null ? arr[18].toString() : null);
		obj.setCess(arr[19] != null ? arr[19].toString() : null);
		obj.setTotalTax(arr[20] != null ? arr[20].toString() : null);
		obj.setInvoiceValue(arr[21] != null ? arr[21].toString() : null);
		obj.setPos(arr[22] != null ? arr[22].toString() : null);
		obj.setFormType(arr[23] != null ? arr[23].toString() : null);
		obj.setGstr1FilingStatus(arr[24] != null ? arr[24].toString() : null);
		obj.setGstr1FilingPeriod(arr[25] != null ? arr[25].toString() : null);
		obj.setOriginalDocNo(arr[26] != null && !arr[26].toString().isEmpty()
				? "'" + arr[26].toString() : null);
		obj.setOriginalDocDate(arr[27] != null ? arr[27].toString() : null);
		obj.setPendingActionBlocked(arr[28] != null ? arr[28].toString() : null);
		obj.setCheckSum(arr[29] != null ? arr[29].toString() : null);
		obj.setItcRedReq(arr[32] != null ? arr[32].toString() : null);
		obj.setDeclIgst(arr[33] != null ? arr[33].toString() : null);
		obj.setDeclCgst(arr[34] != null ? arr[34].toString() : null);
		obj.setDeclSgst(arr[35] != null ? arr[35].toString() : null);
		obj.setDeclCess(arr[36] != null ? arr[36].toString() : null);
		
		if (arr[30] != null && !arr[30].toString().isEmpty()) {

			String timestamp = arr[30].toString();
			if(timestamp.length() > 19){
				 timestamp = timestamp.substring(0,19);
				}
			DateTimeFormatter utcFormatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			LocalDateTime localDateTime = LocalDateTime.parse(timestamp,
					utcFormatter);

			ZonedDateTime utcDateTime = ZonedDateTime.of(localDateTime,
					ZoneId.of("UTC"));

			ZoneId istZone = ZoneId.of("Asia/Kolkata");
			ZonedDateTime istDateTime = utcDateTime
					.withZoneSameInstant(istZone);

			DateTimeFormatter istFormatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String istTime = istDateTime.format(istFormatter);

			obj.setGetCallDateTime(
					DownloadReportsConstant.CSVCHARACTER.concat(istTime));

		}
		
		//obj.setGetCallDateTime(arr[30] != null ? arr[30].toString() : null);
		obj.setImsUniqueId(arr[31] != null ? arr[31].toString() : null);
		
		
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

	private StatefulBeanToCsv<ImsFileStatusReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<ImsFileStatusReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(ImsFileStatusReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<ImsFileStatusReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<ImsFileStatusReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
	}
 public static void main(String[] args) {
	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");

	    ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));

     ZonedDateTime istNow = utcNow.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

     String timeMilli = istNow.format(formatter);
     
     System.out.println(timeMilli);
}
}
