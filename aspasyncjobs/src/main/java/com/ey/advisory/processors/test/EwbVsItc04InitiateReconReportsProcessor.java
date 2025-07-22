/**
 * 
 */
package com.ey.advisory.processors.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItc04ConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItc04ErrorRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItco4DownloadReconReportsRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04InitiateReconReportDto;
import com.ey.advisory.app.reconewbvsitc04.EwbvsItc04ReconDownloadReportsEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("EwbVsItc04InitiateReconReportsProcessor")
public class EwbVsItc04InitiateReconReportsProcessor implements TaskProcessor {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EwbvsItc04ConfigRepository")
	EwbvsItc04ConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("EwbvsItco4DownloadReconReportsRepository")
	private EwbvsItco4DownloadReconReportsRepository reportRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("EwbvsItc04ErrorRepository")
	EwbvsItc04ErrorRepository errorRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void execute(Message message, AppExecContext context) {

		String fullPath = null;
		File tempDir = null;
		String fullPathDropOut = null;
		File tempDirDropOut = null;
		Integer noOfChunk = 0;
		Integer noOfChunkDropOut = 0;
		String msg = null;
		BigInteger chunkSize = BigInteger.valueOf(10000);

		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long configId = json.get("configId").getAsLong();

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Recon Report Details " + "with configId:'%s'",
					configId);
			LOGGER.debug(msg);
		}
		String[] reportType = { "Consolidated Recon Report",
				"Drop Out Records" };

		for (int i = 0; i < reportType.length; i++) {
			boolean isReportAvailable = false;
			if (reportType[i].equalsIgnoreCase("Consolidated Recon Report")) {

				try {
					tempDir = createTempDir(configId, reportType[i]);
				} catch (IOException ex) {
					msg = String
							.format("Error while creating temparory directory for "
									+ "Consolidated  configId %d", configId);
					LOGGER.error(msg, ex);
					throw new AppException(msg, ex);
				}

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ getUniqueFileName(reportType[i]) + ".csv";

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Config ID is '%s',"
									+ " Created temporary directory to generate "
									+ "zip file: %s",
							configId.toString(), tempDir.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try {
					// chunk value

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_EWBVSITC04_INS_CHUNK");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
							BigInteger.class, ParameterMode.IN);

					storedProc.setParameter("P_CHUNK_SIZE", chunkSize);

					storedProc.registerStoredProcedureParameter("P_REPORT_NAME",
							String.class, ParameterMode.IN);

					storedProc.setParameter("P_REPORT_NAME",
							"Consolidated EWBVSITC04 Report");

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Executing chunking proc"
										+ " USP_EWBVSITC04_INS_CHUNK: '%s'",
								configId.toString());
						LOGGER.debug(msg);
					}
					Integer chunks = (Integer) storedProc.getSingleResult();

					noOfChunk = chunks;

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Chunking proc Executed"
								+ " gettingChunkDataByConfigIdAndReport: configId '%d', "
								+ "noOfChunk %d ", configId, noOfChunk);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {

					msg = String.format(
							"Error while executing chunking proc for "
									+ "Consolidated Recon Report configId %d",
							configId);
					LOGGER.error(msg, ex);

					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_FAILED,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							configId);
					throw new AppException(msg, ex);

				}
				noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

				if (noOfChunk <= 0) {
					msg = "No Data To Generate Report";
					LOGGER.error(msg);

					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.NO_DATA_FOUND,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							configId);
					reportRepo.updateAndSave(configId,
							"Consolidated Recon Report");
				}
				saveORUpdate(configId, "Consolidated Recon Report");

				int j = 0;

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplate = commonUtility
							.getProp("ewb.itc04.recon.report.header");
					writer.append(invoiceHeadersTemplate);

					String[] columnMappings = commonUtility
							.getProp("ewb.itc04.recon.report.column.mapping")
							.split(",");

					while (j < noOfChunk) {
						j++;

						StoredProcedureQuery storedProc = entityManager
								.createStoredProcedureQuery(
										"USP_EWBVSITC04_RPT_MASTER");

						storedProc.registerStoredProcedureParameter(
								"P_RECON_REPORT_CONFIG_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
								configId);

						storedProc.registerStoredProcedureParameter(
								"P_REPORT_TYPE", String.class,
								ParameterMode.IN);

						storedProc.setParameter("P_REPORT_TYPE",
								"Consolidated EWBVSITC04 Report");

						storedProc.registerStoredProcedureParameter(
								"P_CHUNK_VALUE", Integer.class,
								ParameterMode.IN);
						storedProc.setParameter("P_CHUNK_VALUE", j);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with "
											+ "params {} Config ID is '%s', "
											+ " chunkNo is %d ",
									configId.toString(), j);
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

							List<EwbVsItc04InitiateReconReportDto> reconDataList = records
									.stream().map(o -> convert(o))
									.collect(Collectors
											.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Report Name and row count: '%s' - '%s'",
										reportType, reconDataList.size());
								LOGGER.debug(msg);
							}
							if (reconDataList != null
									&& !reconDataList.isEmpty()) {

								ColumnPositionMappingStrategy<EwbVsItc04InitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(
										EwbVsItc04InitiateReconReportDto.class);
								mappingStrategy
										.setColumnMapping(columnMappings);
								StatefulBeanToCsvBuilder<EwbVsItc04InitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv<EwbVsItc04InitiateReconReportDto> beanWriter = builder
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
											generationTimeDiff, reportType,
											records.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);

				} catch (Exception ex) {
					LOGGER.error("Exception while executing the query for "
							+ "Report Type :{}", reportType, ex);
					updateReconConfigStatus(
							ReconStatusConstants.REPORT_GENERATION_FAILED,
							configId);
					throw new AppException(ex);
				}
	
				if (noOfChunk > 0){
					try {
						// Zipping
						String zipFileName = null;
						if (tempDir.list().length > 0) {
							zipFileName = combineAndZipCsvFiles.zipfolder(configId,
									tempDir, "Recon_Report");

							File zipFile = new File(tempDir, zipFileName);

							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Before uploading "
												+ "Zip Inner Mandatory files, tempDir "
												+ "Name %s and ZipFileName %s ",
										tempDir, zipFileName);
								LOGGER.debug(msg);
							}

							Pair<String,String> uploadedZipName = DocumentUtility.uploadFile(
									zipFile, "EwbVsItc04ReconReport");
												
							String uploadedDocName = uploadedZipName.getValue0();
							String docId = uploadedZipName.getValue1();
							
							reportRepo.updateReconFilePath(uploadedDocName,
									configId, reportType[i], docId);

						}
						reconConfigRepo.updateReconConfigStatusAndReportName(
								ReconStatusConstants.REPORT_GENERATED,
								EYDateUtil.toUTCDateTimeFromLocal(
										LocalDateTime.now()),
								configId);
					} catch (Exception e) {
						String msz = String.format(
								"Exception occured while creating zip file for %d and reportType %s ",
								configId, reportType);
						LOGGER.error(msz, e);

						reconConfigRepo.updateReconConfigStatusAndReportName(
								ReconStatusConstants.REPORT_GENERATION_FAILED,
								EYDateUtil.toUTCDateTimeFromLocal(
										LocalDateTime.now()),
								configId);

					} finally {
						deleteTemporaryDirectory(tempDir);
					}
				}
				
			} else if (reportType[i].equalsIgnoreCase("Drop Out Records")) {
				// Drop Out Records
				try {

					tempDirDropOut = createTempDir(configId, reportType[i]);

					fullPathDropOut = tempDirDropOut.getAbsolutePath()
							+ File.separator + getUniqueFileName(reportType[i])
							+ ".csv";

					tempDirDropOut = createTempDir(configId, "EWB_DROP_OUT");

					fullPathDropOut = tempDirDropOut.getAbsolutePath()
							+ File.separator + getUniqueFileName(reportType[i])
							+ ".csv";

					// chunk value

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_EWBVSITC04_INS_CHUNK");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
							BigInteger.class, ParameterMode.IN);

					storedProc.setParameter("P_CHUNK_SIZE", chunkSize);

					storedProc.registerStoredProcedureParameter("P_REPORT_NAME",
							String.class, ParameterMode.IN);

					storedProc.setParameter("P_REPORT_NAME",
							"Dropped EWBVSITC04 Records Report");

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Executing chunking proc"
										+ " USP_EWBVSITC04_INS_CHUNK: '%s'",
								configId.toString());
						LOGGER.debug(msg);
					}
					Integer chunksDropOut = (Integer) storedProc
							.getSingleResult();

					noOfChunkDropOut = chunksDropOut;

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Chunking proc Executed"
										+ " USP_EWB_DROP_OUT_INS_CHUNK: configId '%d', "
										+ "noOfChunkDropOut %d ",
								configId, noOfChunkDropOut);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {

					msg = String
							.format("Error while executing chunking proc for "
									+ "DropOut Report  configId %d", configId);
					LOGGER.error(msg, ex);
					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_FAILED,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							configId);

				}
				noOfChunkDropOut = noOfChunkDropOut != null
						? noOfChunkDropOut.intValue() : 0;

				if (noOfChunkDropOut <= 0 && noOfChunk <= 0) {
					msg = "No Data To Generate Report for Droupout Report" + "-"
							+ configId;
					LOGGER.error(msg);
					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.NO_DATA_FOUND,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()),
							configId);
					reportRepo.updateAndSave(configId, "Drop Out Records");
				}
				saveORUpdate(configId, reportType[i]);

				int k = 0;

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPathDropOut), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplateDropOut = commonUtility
							.getProp("ewb.itc04.recon.report.header");
					writer.append(invoiceHeadersTemplateDropOut);

					String[] columnMappingsDropOut = commonUtility
							.getProp("ewb.itc04.recon.report.column.mapping")
							.split(",");

					while (k < noOfChunkDropOut) {
						k++;

						StoredProcedureQuery storedProcDropOutRecords = entityManager
								.createStoredProcedureQuery(
										"USP_EWBVSITC04_DROPOUTRPT_MASTER");

						storedProcDropOutRecords
								.registerStoredProcedureParameter(
										"P_RECON_REPORT_CONFIG_ID", Long.class,
										ParameterMode.IN);

						storedProcDropOutRecords.setParameter(
								"P_RECON_REPORT_CONFIG_ID", configId);

						storedProcDropOutRecords
								.registerStoredProcedureParameter(
										"P_CHUNK_VALUE", Integer.class,
										ParameterMode.IN);
						storedProcDropOutRecords.setParameter("P_CHUNK_VALUE",
								k);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with "
											+ "params {} Config ID is '%s', "
											+ " chunkNo is %d ",
									configId.toString(), k);
							LOGGER.debug(msg);
						}

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> recordsDropOut = storedProcDropOutRecords
								.getResultList();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("no of records after proc call {} ",
									recordsDropOut.size());
						}
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data"
											+ " from DB is '%d' millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									dbLoadTimeDiff, "Drop Out Records",
									recordsDropOut.size());
							LOGGER.debug(msg);
						}

						if (recordsDropOut != null
								&& !recordsDropOut.isEmpty()) {
							isReportAvailable = true;

							List<EwbVsItc04InitiateReconReportDto> reconDataDropOutList = recordsDropOut
									.stream().map(o -> convert(o))
									.collect(Collectors
											.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
										"Report Name and row count: '%s' - '%s'",
										reportType[i],
										reconDataDropOutList.size());
								LOGGER.debug(msg);
							}
							if (reconDataDropOutList != null
									&& !reconDataDropOutList.isEmpty()) {

								ColumnPositionMappingStrategy<EwbVsItc04InitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(
										EwbVsItc04InitiateReconReportDto.class);
								mappingStrategy.setColumnMapping(
										columnMappingsDropOut);
								StatefulBeanToCsvBuilder<EwbVsItc04InitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv<EwbVsItc04InitiateReconReportDto> beanWriter = builder
										.withMappingStrategy(mappingStrategy)
										.withSeparator(
												CSVWriter.DEFAULT_SEPARATOR)
										.withLineEnd(CSVWriter.DEFAULT_LINE_END)
										.withEscapechar(
												CSVWriter.DEFAULT_ESCAPE_CHARACTER)
										.build();
								long generationStTime = System
										.currentTimeMillis();
								beanWriter.write(reconDataDropOutList);
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
											generationTimeDiff,
											"Drop Out Records",
											recordsDropOut.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);

				} catch (Exception ex) {
					LOGGER.error(
							"Exception while executing the query for "
									+ "Report Type :{}",
							"Drop Out Records", ex);
					updateReconConfigStatus(
							ReconStatusConstants.REPORT_GENERATION_FAILED,
							configId);
					throw new AppException(ex);
				}
				
				if (noOfChunkDropOut > 0) {
					try {
						// Zipping
						if (isReportAvailable) {
							String zipFileName = null;
							if (tempDirDropOut.list().length > 0) {
								zipFileName = combineAndZipCsvFiles.zipfolder(
										configId, tempDirDropOut, "Recon_Report");

								File zipFile = new File(tempDirDropOut,
										zipFileName);

								if (LOGGER.isDebugEnabled()) {
									msg = String.format(
											"Before uploading "
													+ "Zip Inner Mandatory files, tempDir "
													+ "Name %s and ZipFileName %s ",
											tempDirDropOut, zipFileName);
									LOGGER.debug(msg);
								}

								Pair<String,String>	uploadedZipName = DocumentUtility
										.uploadFile(zipFile,
												"EwbVsItc04ReconReport");
													
								String uploadedDocName = uploadedZipName.getValue0();
								String docId = uploadedZipName.getValue1();

								
								reportRepo.updateReconFilePath(uploadedDocName,
										configId, reportType[i], docId);

							}
							reconConfigRepo.updateReconConfigStatusAndReportName(
									ReconStatusConstants.REPORT_GENERATED,
									EYDateUtil.toUTCDateTimeFromLocal(
											LocalDateTime.now()),
									configId);
						}

					} catch (Exception e) {
						String msz = String.format(
								"Exception occured while creating zip file for %d and Drop Out Records %s ",
								configId, reportType[i]);
						LOGGER.error(msz, e);

						reconConfigRepo.updateReconConfigStatusAndReportName(
								ReconStatusConstants.REPORT_GENERATION_FAILED,
								EYDateUtil.toUTCDateTimeFromLocal(
										LocalDateTime.now()),
								configId);

					} finally {
						deleteTemporaryDirectory(tempDirDropOut);
					}
				}
				
			}
		}

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
		reconConfigRepo.updateReconConfigStatusAndReportName(status,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
				configId);
	}

	private EwbVsItc04InitiateReconReportDto convert(Object[] arr) {

		EwbVsItc04InitiateReconReportDto obj = new EwbVsItc04InitiateReconReportDto();

		obj.setReconReportType((arr[0] != null) ? arr[0].toString() : null);
		obj.setReasonformismatch((arr[1] != null) ? arr[1].toString() : null);
		obj.setTaxPeriod_EWB((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		obj.setTaxPeriod_ITC04((arr[3] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setSupplierGSTIN_EWB((arr[4] != null) ? arr[4].toString() : null);
		obj.setSupplierGSTIN_DigiGSTITC04(
				(arr[5] != null) ? arr[5].toString() : null);
		obj.setCustomerGSTIN_EWB((arr[6] != null) ? arr[6].toString() : null);
		obj.setCustomerGSTIN_DigiGSTITC04(
				(arr[7] != null) ? arr[7].toString() : null);
		obj.setDocType_EWB((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocType_DigiGSTITC04(
				(arr[9] != null) ? arr[9].toString() : null);
		obj.setSupplyType_EWB((arr[10] != null) ? arr[10].toString() : null);
		obj.setSupplyType_DigiGSTITC04(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setDocumentNumber_EWB(
				(arr[12] != null) ? arr[12].toString() : null);
		obj.setDocumentNumber_DigiGSTITC04(
				(arr[13] != null) ? arr[13].toString() : null);
		obj.setDocumentDate_EWB((arr[14] != null) ? arr[14].toString() : null);
		obj.setDocumentDate_DigiGSTITC04(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setTaxableValue_EWB((arr[16] != null) ? arr[16].toString() : null);
		obj.setTaxableValue_DigiGSTITC04(
				(arr[17] != null) ? arr[17].toString() : null);
		obj.setIGST_EWB((arr[18] != null) ? arr[18].toString() : null);
		obj.setIGST_DigiGSTITC04((arr[19] != null) ? arr[19].toString() : null);
		/*
		 * obj.setCGST_EINV( (arr[20] != null) ? arr[20].toString() : null);
		 */
		obj.setCGST_EWB((arr[20] != null) ? arr[20].toString() : null);
		obj.setCGST_DigiGSTITC04((arr[21] != null) ? arr[21].toString() : null);
		obj.setSGST_EWB((arr[22] != null) ? arr[22].toString() : null);
		obj.setSGST_DigiGSTITC04((arr[23] != null) ? arr[23].toString() : null);
		obj.setCess_EWB((arr[24] != null) ? arr[24].toString() : null);
		obj.setCess_DigiGSTITC04((arr[25] != null) ? arr[25].toString() : null);
		obj.setTotalTax_EWB((arr[26] != null) ? arr[26].toString() : null);
		obj.setTotalTax_DigiGSTITC04(
				(arr[27] != null) ? arr[27].toString() : null);
		obj.setInvoiceValue_EWB((arr[28] != null) ? arr[28].toString() : null);
		/*
		 * obj.setInvoiceValue_DigiGSTITC04( (arr[29] != null) ?
		 * arr[29].toString() : null);
		 */
		obj.setTableType_DigiGSTITC04(
				(arr[29] != null) ? arr[29].toString() : null);
		obj.setEWBNumber_EWB(
				(arr[30] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[30].toString()) : null);
		obj.setEWBDate_EWB((arr[31] != null) ? arr[31].toString() : null);
		obj.setEWBValidupto_EWB((arr[32] != null) ? arr[32].toString() : null);
		obj.setEWBRejectStatus_EWB(
				(arr[33] != null) ? arr[33].toString() : null);
		obj.setEWBExtendedtimes_EWB(
				(arr[34] != null) ? arr[34].toString() : null);
		obj.setTransactionType_EWB(
				(arr[35] != null) ? arr[35].toString() : null);
		obj.setTransactionType_DigiGSTITC04(
				(arr[36] != null) ? arr[36].toString() : null);
		obj.setDocCategory_EWB((arr[37] != null) ? arr[37].toString() : null);
		obj.setSupplierTradeName_EWB(
				(arr[38] != null) ? arr[38].toString() : null);
		obj.setSupplierAddress1_EWB(
				(arr[39] != null) ? arr[39].toString() : null);
		obj.setSupplierAddress2_EWB(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setSupplierLocation_EWB(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setSupplierPincode_EWB(
				(arr[42] != null) ? arr[42].toString() : null);
		obj.setSupplierStateCode_EWB(
				(arr[43] != null) ? arr[43].toString() : null);
		obj.setCustomerTradeName_EWB(
				(arr[44] != null) ? arr[44].toString() : null);
		obj.setCustomerAddress1_EWB(
				(arr[45] != null) ? arr[45].toString() : null);
		obj.setCustomerAddress2_EWB(
				(arr[46] != null) ? arr[46].toString() : null);
		obj.setCustomerLocation_EWB(
				(arr[47] != null) ? arr[47].toString() : null);
		obj.setCustomerPincode_EWB(
				(arr[48] != null) ? arr[48].toString() : null);
		obj.setCustomerStateCode_EWB(
				(arr[49] != null) ? arr[49].toString() : null);
		obj.setDispatcherTradeName_EWB(
				(arr[50] != null) ? arr[50].toString() : null);
		obj.setDispatcherAddress1_EWB(
				(arr[51] != null) ? arr[51].toString() : null);
		obj.setDispatcherAddress2_EWB(
				(arr[52] != null) ? arr[52].toString() : null);
		obj.setDispatcherLocation_EWB(
				(arr[53] != null) ? arr[53].toString() : null);
		obj.setDispatcherPincode_EWB(
				(arr[54] != null) ? arr[54].toString() : null);
		obj.setDispatcherStateCode_EWB(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setShipToTradeName_EWB(
				(arr[56] != null) ? arr[56].toString() : null);
		obj.setShipToAddress1_EWB(
				(arr[57] != null) ? arr[57].toString() : null);
		obj.setShipToAddress2_EWB(
				(arr[58] != null) ? arr[58].toString() : null);
		obj.setShipToLocation_EWB(
				(arr[59] != null) ? arr[59].toString() : null);
		obj.setShipToPincode_EWB((arr[60] != null) ? arr[60].toString() : null);
		obj.setShipToStateCode_EWB(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setModeofTransport_EWB(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setTransporterID_EWB((arr[63] != null) ? arr[63].toString() : null);
		obj.setTransporterName_EWB(
				(arr[64] != null) ? arr[64].toString() : null);
		obj.setTransportDocNo_EWB(
				(arr[65] != null) ? arr[65].toString() : null);
		obj.setTransportDocDate_EWB(
				(arr[66] != null) ? arr[66].toString() : null);
		obj.setDistance_EWB((arr[67] != null) ? arr[67].toString() : null);
		obj.setVehicleNo_EWB((arr[68] != null) ? arr[68].toString() : null);
		obj.setVehicleType_EWB((arr[69] != null) ? arr[69].toString() : null);
		obj.setAccountingVoucherNumber_DigiGSTITC04(
				(arr[70] != null) ? arr[70].toString() : null);
		obj.setAccountingVoucherDate_DigiGSTITC04(
				(arr[71] != null) ? arr[71].toString() : null);
		obj.setCompanyCode_DigiGSTITC04(
				(arr[72] != null) ? arr[72].toString() : null);
		obj.setRecordStatus_DigiGSTITC04(
				(arr[73] != null) ? arr[73].toString() : null);
		obj.setEWBGetCallDate((arr[74] != null) ? arr[74].toString() : null);
		obj.setEWBGetCallTime((arr[75] != null) ? arr[75].toString() : null);
		obj.setProcessingDateofDigiGSTITC04(
				(arr[76] != null) ? arr[76].toString() : null);
		obj.setProcessingTimeofDigiGSTITC04(
				(arr[77] != null) ? arr[77].toString() : null);
		obj.setSource_EWB((arr[78] != null) ? arr[78].toString() : null);
		obj.setSource_DigiGSTITC04(
				(arr[79] != null) ? arr[79].toString() : null);
		obj.setEWBStatusatDigiGST(
				(arr[80] != null) ? arr[80].toString() : null);
		obj.setITC04StatusatDigiGST(
				(arr[81] != null) ? arr[81].toString() : null);

		obj.setReconID((arr[82] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[82].toString()) : null);
		obj.setReconDate((arr[83] != null) ? arr[83].toString() : null);
		obj.setReconTime(
				(arr[84] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[84].toString()) : null);
		obj.setReconType((arr[85] != null) ? arr[85].toString() : null);
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

	private void saveORUpdate(Long configId, String reportType) {
		EwbvsItc04ReconDownloadReportsEntity obj = new EwbvsItc04ReconDownloadReportsEntity();
		obj.setConfigId(configId);
		obj.setIsDownloadable(false);
		obj.setReportType(reportType);

		reportRepo.save(obj);

	}

}
