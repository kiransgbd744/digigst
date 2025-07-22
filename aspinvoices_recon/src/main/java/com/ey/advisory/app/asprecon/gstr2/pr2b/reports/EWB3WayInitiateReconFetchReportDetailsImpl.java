package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

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

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.EWB3WayReconDownloadReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EWB3WayDownloadReconReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon3WayConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconDropOutRecordsDto;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconReportDto;
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
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("EWB3WayInitiateReconFetchReportDetailsImpl")
public class EWB3WayInitiateReconFetchReportDetailsImpl implements EWB3WayInitiateReconFetchReportDetails {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Recon3WayConfigRepository")
	Recon3WayConfigRepository reconConfigRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EWB3WayDownloadReconReportsRepository")
	EWB3WayDownloadReconReportsRepository reportRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void getInitiateReconReportData(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		String fullPathDropOut = null;
		File tempDirDropOut = null;
		Integer noOfChunk = 0;
		Integer noOfChunkDropOut = 0;
		String msg = null;
		Integer chunkSize = 10000;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Get Consolidated_3Way Recon Report Details " + "with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String[] reportType = { "Consolidated Recon Report", "Drop Out Records" };

		for (int i = 0; i < reportType.length; i++) {
			boolean isReportAvailable = false;
			if (reportType[i].equalsIgnoreCase("Consolidated Recon Report")) {

				tempDir = createTempDir(configId, reportType[i]);

				fullPath = tempDir.getAbsolutePath() + File.separator + getUniqueFileName(reportType[i]) + ".csv";

				tempDir = createTempDir(configId, reportType[i]);

				fullPath = tempDir.getAbsolutePath() + File.separator + getUniqueFileName(reportType[i]) + ".csv";

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Config ID is '%s'," + " Created temporary directory to generate " + "zip file: %s",
							configId.toString(), tempDir.getAbsolutePath());
					LOGGER.debug(msg);
				}

				try {
					// chunk value

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery("USP_3WAY_RPT_INS_CHUNK");

					storedProc.registerStoredProcedureParameter("P_RECON_CONFIG_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("P_RECON_CONFIG_ID", configId);

					storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL", Integer.class, ParameterMode.IN);

					storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Executing chunking proc" + " USP_3WAY_RPT_INS_CHUNK: '%s'",
								configId.toString());
						LOGGER.debug(msg);
					}

					Integer chunks = (Integer) storedProc.getSingleResult();

					noOfChunk = chunks;

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Chunking proc Executed" + " USP_3WAY_RPT_INS_CHUNK: configId '%d', " + "noOfChunk %d ",
								configId, noOfChunk);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {

					msg = String.format("Error while executing chunking proc for " + "Consolidated  configId %d",
							configId);
					LOGGER.error(msg, ex);
					throw new AppException(msg, ex);

				}
				noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

				if (noOfChunk <= 0) {
					msg = "No Data To Generate Report";
					LOGGER.error(msg);

					updateReconConfigStatus(ReconStatusConstants.NO_DATA_FOUND, configId);

					// throw new AppException(msg);
				}
				saveORUpdate(configId, reportType[i]);

				int j = 0;

				try (Writer writer = new BufferedWriter(new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplate = commonUtility.getProp("ewb.3way.recon.report.header");
					writer.append(invoiceHeadersTemplate);

					String[] columnMappings = commonUtility.getProp("ewb.3way.recon.report.column.mapping").split(",");

					while (j < noOfChunk) {
						j++;

						StoredProcedureQuery storedProc = entityManager
								.createStoredProcedureQuery("USP_3WAY_EWB_GSTR1_EINV_RPT_MASTER");

						storedProc.registerStoredProcedureParameter("P_RECON_CONFIG_ID", Long.class, ParameterMode.IN);

						storedProc.setParameter("P_RECON_CONFIG_ID", configId);

						storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
						storedProc.setParameter("P_CHUNK_VALUE", j);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with " + "params {} Config ID is '%s', " + " chunkNo is %d ",
									configId.toString(), j);
							LOGGER.debug(msg);
						}

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> records = storedProc.getResultList();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("no of records after proc call {} ", records.size());
						}
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data" + " from DB is '%d' millisecs,"
											+ " Report Name and Data count:" + " '%s' - '%s'",
									dbLoadTimeDiff, reportType, records.size());
							LOGGER.debug(msg);
						}

						if (records != null && !records.isEmpty()) {

							List<EWB3WayInitiateReconReportDto> reconDataList = records.stream().map(o -> convert(o))
									.collect(Collectors.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format("Report Name and row count: '%s' - '%s'", reportType,
										reconDataList.size());
								LOGGER.debug(msg);
							}
							if (reconDataList != null && !reconDataList.isEmpty()) {

								ColumnPositionMappingStrategy<EWB3WayInitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(EWB3WayInitiateReconReportDto.class);
								mappingStrategy.setColumnMapping(columnMappings);
								StatefulBeanToCsvBuilder<EWB3WayInitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv<EWB3WayInitiateReconReportDto> beanWriter = builder
										.withMappingStrategy(mappingStrategy).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
										.withLineEnd(CSVWriter.DEFAULT_LINE_END)
										.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
								long generationStTime = System.currentTimeMillis();
								beanWriter.write(reconDataList);
								long generationEndTime = System.currentTimeMillis();
								long generationTimeDiff = (generationEndTime - generationStTime);
								if (LOGGER.isDebugEnabled()) {
									msg = String.format(
											"Total Time taken to" + " Generate the report is '%d' " + "millisecs,"
													+ " Report Name and Data count:" + " '%s' - '%s'",
											generationTimeDiff, reportType, records.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);

				} catch (Exception ex) {
					LOGGER.error("Exception while executing the query for " + "Report Type :{}", reportType, ex);
					updateReconConfigStatus(ReconStatusConstants.REPORT_GENERATION_FAILED, configId);
					throw new AppException(ex);
				}
				try {
					// Zipping
					String zipFileName = null;
					if (tempDir.list().length > 0) {
						zipFileName = combineAndZipCsvFiles.zipfolder(configId, tempDir, "Recon_Report");

						File zipFile = new File(tempDir, zipFileName);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format("Before uploading " + "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ", tempDir, zipFileName);
							LOGGER.debug(msg);
						}

						/*String uploadedDocName = DocumentUtility.uploadZipFile(zipFile, "3WayReconReport");*/
						Pair<String, String> uploadedZipName = DocumentUtility
								.uploadFile(zipFile, "3WayReconReport");
						String uploadedDocName = uploadedZipName.getValue0();
						String docId = uploadedZipName.getValue1();
						reportRepo.updateReconFilePath(uploadedDocName, configId, reportType[i],docId);

					}
					reconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.REPORT_GENERATED,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);
				} catch (Exception e) {
					String msz = String.format("Exception occured while creating zip file for %d and reportType %s ",
							configId, reportType);
					LOGGER.error(msz, e);

					reconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.REPORT_GENERATION_FAILED,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);

				} finally {
					deleteTemporaryDirectory(tempDir);
				}
			} else {
				// Drop Out Records
				try {

					tempDirDropOut = createTempDir(configId, reportType[i]);

					fullPathDropOut = tempDirDropOut.getAbsolutePath() + File.separator
							+ getUniqueFileName(reportType[i]) + ".csv";

					tempDirDropOut = createTempDir(configId, "EWB_DROP_OUT");

					fullPathDropOut = tempDirDropOut.getAbsolutePath() + File.separator
							+ getUniqueFileName(reportType[i]) + ".csv";

					// chunk value

					StoredProcedureQuery storedProcDropOutRecords = entityManager
							.createStoredProcedureQuery("USP_EWB_DROP_OUT_INS_CHUNK");

					storedProcDropOutRecords.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID", Long.class,
							ParameterMode.IN);

					storedProcDropOutRecords.setParameter("P_REPORT_DOWNLOAD_ID", configId);

					storedProcDropOutRecords.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL", Integer.class,
							ParameterMode.IN);

					storedProcDropOutRecords.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Executing chunking proc" + " USP_EWB_DROP_OUT_INS_CHUNK: '%s'",
								configId.toString());
						LOGGER.debug(msg);
					}

					Integer chunksDropOut = (Integer) storedProcDropOutRecords.getSingleResult();

					noOfChunkDropOut = chunksDropOut;

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Chunking proc Executed" + " USP_EWB_DROP_OUT_INS_CHUNK: configId '%d', "
								+ "noOfChunkDropOut %d ", configId, noOfChunkDropOut);
						LOGGER.debug(msg);
					}

				} catch (Exception ex) {

					msg = String.format("Error while executing chunking proc for " + "Consolidated  configId %d",
							configId);
					LOGGER.error(msg, ex);
					throw new AppException(msg, ex);

				}
				noOfChunkDropOut = noOfChunkDropOut != null ? noOfChunkDropOut.intValue() : 0;

				if (noOfChunkDropOut <= 0) {
					msg = "No Data To Generate Report";
					LOGGER.error(msg);

					// updateReconConfigStatus(ReconStatusConstants.NO_DATA_FOUND,
					// configId);
					reportRepo.updateAndSave(configId, reportType[i]);
					// throw new AppException(msg);
				}
				saveORUpdate(configId, reportType[i]);

				int k = 0;

				try (Writer writer = new BufferedWriter(new FileWriter(fullPathDropOut), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplateDropOut = commonUtility
							.getProp("ewb.3way.recon.dropout.records.header");
					writer.append(invoiceHeadersTemplateDropOut);

					String[] columnMappingsDropOut = commonUtility
							.getProp("ewb.3way.recon.dropout.records.column.mapping").split(",");

					while (k < noOfChunkDropOut) {
						k++;

						StoredProcedureQuery storedProcDropOutRecords = entityManager
								.createStoredProcedureQuery("USP_EWB_DROP_OUT_DISP_CHUNK");

						storedProcDropOutRecords.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID", Long.class,
								ParameterMode.IN);

						storedProcDropOutRecords.setParameter("P_REPORT_DOWNLOAD_ID", configId);

						storedProcDropOutRecords.registerStoredProcedureParameter("P_CHUNK_VALUE", Integer.class,
								ParameterMode.IN);
						storedProcDropOutRecords.setParameter("P_CHUNK_VALUE", k);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with " + "params {} Config ID is '%s', " + " chunkNo is %d ",
									configId.toString(), k);
							LOGGER.debug(msg);
						}

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> recordsDropOut = storedProcDropOutRecords.getResultList();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("no of records after proc call {} ", recordsDropOut.size());
						}
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data" + " from DB is '%d' millisecs,"
											+ " Report Name and Data count:" + " '%s' - '%s'",
									dbLoadTimeDiff, "Drop Out Records", recordsDropOut.size());
							LOGGER.debug(msg);
						}

						if (recordsDropOut != null && !recordsDropOut.isEmpty()) {
							isReportAvailable = true;
							List<EWB3WayInitiateReconDropOutRecordsDto> reconDataDropOutList = recordsDropOut.stream()
									.map(o -> convertDropOut(o)).collect(Collectors.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format("Report Name and row count: '%s' - '%s'", reportType[i],
										reconDataDropOutList.size());
								LOGGER.debug(msg);
							}
							if (reconDataDropOutList != null && !reconDataDropOutList.isEmpty()) {

								ColumnPositionMappingStrategy<EWB3WayInitiateReconDropOutRecordsDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(EWB3WayInitiateReconDropOutRecordsDto.class);
								mappingStrategy.setColumnMapping(columnMappingsDropOut);
								StatefulBeanToCsvBuilder<EWB3WayInitiateReconDropOutRecordsDto> builder = new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv<EWB3WayInitiateReconDropOutRecordsDto> beanWriter = builder
										.withMappingStrategy(mappingStrategy).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
										.withLineEnd(CSVWriter.DEFAULT_LINE_END)
										.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
								long generationStTime = System.currentTimeMillis();
								beanWriter.write(reconDataDropOutList);
								long generationEndTime = System.currentTimeMillis();
								long generationTimeDiff = (generationEndTime - generationStTime);
								if (LOGGER.isDebugEnabled()) {
									msg = String.format(
											"Total Time taken to" + " Generate the report is '%d' " + "millisecs,"
													+ " Report Name and Data count:" + " '%s' - '%s'",
											generationTimeDiff, "Drop Out Records", recordsDropOut.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);

				} catch (Exception ex) {
					LOGGER.error("Exception while executing the query for " + "Report Type :{}", "Drop Out Records",
							ex);
					updateReconConfigStatus(ReconStatusConstants.REPORT_GENERATION_FAILED, configId);
					throw new AppException(ex);
				}
				try {
					// Zipping
					if (isReportAvailable) {
						String zipFileName = null;
						if (tempDirDropOut.list().length > 0) {
							zipFileName = combineAndZipCsvFiles.zipfolder(configId, tempDirDropOut, "Recon_Report");

							File zipFile = new File(tempDirDropOut, zipFileName);

							if (LOGGER.isDebugEnabled()) {
								msg = String.format("Before uploading " + "Zip Inner Mandatory files, tempDir "
										+ "Name %s and ZipFileName %s ", tempDirDropOut, zipFileName);
								LOGGER.debug(msg);
							}

							String uploadedDocName = DocumentUtility.uploadZipFile(zipFile, "3WayReconReport");
							reportRepo.updateReconFilePath(uploadedDocName, configId, reportType[i]);

						}
						reconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.REPORT_GENERATED,
								EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);
					}

				} catch (Exception e) {
					String msz = String.format(
							"Exception occured while creating zip file for %d and Drop Out Records %s ", configId,
							reportType[i]);
					LOGGER.error(msz, e);

					reconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.REPORT_GENERATION_FAILED,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);

				} finally {
					deleteTemporaryDirectory(tempDirDropOut);
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
					LOGGER.debug(String.format("Deleted the Temp directory/Folder '%s'", tempFile.getAbsolutePath()));
				}
			} catch (Exception ex) {
				String msg = String.format("Failed to remove the temp " + "directory created for zip: '%s'. This will "
						+ "lead to clogging of disk space.", tempFile.getAbsolutePath());
				LOGGER.error(msg, ex);
			}
		}

	}

	private void updateReconConfigStatus(String status, Long configId) {
		reconConfigRepo.updateReconConfigStatusAndReportName(status,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()), configId);
	}

	private EWB3WayInitiateReconDropOutRecordsDto convertDropOut(Object[] arr) {
		EWB3WayInitiateReconDropOutRecordsDto obj = new EWB3WayInitiateReconDropOutRecordsDto();

		obj.setEwbNo((arr[0] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString()) : null);
		obj.setEwbDate((arr[1] != null) ? arr[1].toString() : null);
		obj.setSupplyType((arr[2] != null) ? arr[2].toString() : null);
		//added apostrophe
		obj.setDocNo((arr[3] != null) ? "'".concat(arr[3].toString()) : null);
		obj.setDocDate((arr[4] != null) ? arr[4].toString() : null);
		obj.setOtherPartyGstin((arr[5] != null) ? arr[5].toString() : null);
		obj.setTransporterDetails((arr[6] != null) ? arr[6].toString() : null);
		obj.setFromGstinInfo((arr[7] != null) ? arr[7].toString() : null);
		obj.setToGstinInfo((arr[8] != null) ? arr[8].toString() : null);
		obj.setStatus((arr[9] != null) ? arr[9].toString() : null);
		obj.setNoOfItems((arr[10] != null) ? arr[10].toString() : null);
		obj.setHsnCode((arr[11] != null) ? arr[11].toString() : null);
		obj.setHsnDescription((arr[12] != null) ? arr[12].toString() : null);
		obj.setAssessableValue((arr[13] != null) ? arr[13].toString() : null);
		obj.setSgstValue((arr[14] != null) ? arr[14].toString() : null);
		obj.setCgstValue((arr[15] != null) ? arr[15].toString() : null);
		obj.setIgstValue((arr[16] != null) ? arr[16].toString() : null);
		obj.setCessValue((arr[17] != null) ? arr[17].toString() : null);
		obj.setCessNonAdvolValue((arr[18] != null) ? arr[18].toString() : null);
		obj.setOtherValue((arr[19] != null) ? arr[19].toString() : null);
		obj.setTotalInvoiceValue((arr[20] != null) ? arr[20].toString() : null);
		obj.setValidTillDate((arr[21] != null) ? arr[21].toString() : null);
		obj.setModeOfGeneration((arr[22] != null) ? arr[22].toString() : null);
		obj.setCancelledBy((arr[23] != null) ? arr[23].toString() : null);
		obj.setCancelledDate((arr[24] != null) ? arr[24].toString() : null);
		obj.setModeOfDataProcessing((arr[25] != null) ? arr[25].toString() : null);

		return obj;
	}

	private EWB3WayInitiateReconReportDto convert(Object[] arr) {

		EWB3WayInitiateReconReportDto obj = new EWB3WayInitiateReconReportDto();

		obj.setEINV_EWB_GSTR1_Status((arr[0] != null) ? arr[0].toString() : null);
		obj.setReconReportType((arr[1] != null) ? arr[1].toString() : null);
		obj.setReasonForMismatch((arr[2] != null) ? arr[2].toString() : null);
		obj.setTaxPeriod_EINV((arr[3] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString()) : null);
		obj.setTaxPeriod_EWB((arr[4] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString()) : null);
		obj.setTaxPeriod_DigiGST_GSTR1(
				(arr[5] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString()) : null);
		obj.setSupplierGSTIN_EINV((arr[6] != null) ? arr[6].toString() : null);
		obj.setSupplierGSTIN_EWB((arr[7] != null) ? arr[7].toString() : null);
		obj.setSupplierGSTIN_DigiGST_GSTR1((arr[8] != null) ? arr[8].toString() : null);
		obj.setCustomerGSTIN_EINV((arr[9] != null) ? arr[9].toString() : null);
		obj.setCustomerGSTIN_EWB((arr[10] != null) ? arr[10].toString() : null);
		obj.setCustomerGSTIN_DigiGST_GSTR1((arr[11] != null) ? arr[11].toString() : null);
		obj.setCustomerName_DigiGST_GSTR1((arr[12] != null) ? arr[12].toString() : null);
		obj.setDocType_EINV((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocType_EWB((arr[14] != null) ? arr[14].toString() : null);
		obj.setDocType_DigiGST_GSTR1((arr[15] != null) ? arr[15].toString() : null);
		obj.setSupplyType_EINV((arr[16] != null) ? arr[16].toString() : null);
		obj.setSupplyType_EWB((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplyType_DigiGST_GSTR1((arr[18] != null) ? arr[18].toString() : null);
		//added apostrophe 
		obj.setDocumentNumber_EINV((arr[19] != null) ? "'".concat(arr[19].toString()) : null);
		obj.setDocumentNumber_EWB((arr[20] != null) ? "'".concat(arr[20].toString()) : null);
		obj.setDocumentNumber_DigiGST_GSTR1((arr[21] != null) ? "'".concat(arr[21].toString()) : null);
		//added apostrophe
		obj.setDocumentDate_EINV((arr[22] != null) ? arr[22].toString() : null);
		obj.setDocumentDate_EWB((arr[23] != null) ? arr[23].toString() : null);
		obj.setDocumentDate_DigiGST_GSTR1((arr[24] != null) ? arr[24].toString() : null);
		obj.setTaxableValue_EINV((arr[25] != null) ? arr[25].toString() : null);
		obj.setTaxableValue_EWB((arr[26] != null) ? arr[26].toString() : null);
		obj.setTaxableValue_DigiGST_GSTR1((arr[27] != null) ? arr[27].toString() : null);
		obj.setIGST_EINV((arr[28] != null) ? arr[28].toString() : null);
		obj.setIGST_EWB((arr[29] != null) ? arr[29].toString() : null);
		obj.setIGST_DigiGST_GSTR1((arr[30] != null) ? arr[30].toString() : null);
		obj.setCGST_EINV((arr[31] != null) ? arr[31].toString() : null);
		obj.setCGST_EWB((arr[32] != null) ? arr[32].toString() : null);
		obj.setCGST_DigiGST_GSTR1((arr[33] != null) ? arr[33].toString() : null);
		obj.setSGST_EINV((arr[34] != null) ? arr[34].toString() : null);
		obj.setSGST_EWB((arr[35] != null) ? arr[35].toString() : null);
		obj.setSGST_DigiGST_GSTR1((arr[36] != null) ? arr[36].toString() : null);
		obj.setCess_EINV((arr[37] != null) ? arr[37].toString() : null);
		obj.setCess_EWB((arr[38] != null) ? arr[38].toString() : null);
		obj.setCess_DigiGST_GSTR1((arr[39] != null) ? arr[39].toString() : null);
		obj.setTotalTax_EINV((arr[40] != null) ? arr[40].toString() : null);
		obj.setTotalTax_EWB((arr[41] != null) ? arr[41].toString() : null);
		obj.setTotalTax_DigiGST_GSTR1((arr[42] != null) ? arr[42].toString() : null);
		obj.setInvoiceValue_EINV((arr[43] != null) ? arr[43].toString() : null);
		obj.setInvoiceValue_EWB((arr[44] != null) ? arr[44].toString() : null);
		obj.setInvoiceValue_DigiGST_GSTR1((arr[45] != null) ? arr[45].toString() : null);
		obj.setPOS_EINV((arr[46] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[46].toString()) : null);
		obj.setPOS_DigiGST_GSTR1(
				(arr[47] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[47].toString()) : null);
		obj.setReverseChargeFlag_EINV((arr[48] != null) ? arr[48].toString() : null);
		obj.setReverseChargeFlag_EWB((arr[49] != null) ? arr[49].toString() : null);
		obj.setReverseChargeFlag_DigiGST_GSTR1((arr[50] != null) ? arr[50].toString() : null);
		obj.setPortCode_EINV((arr[51] != null) ? arr[51].toString() : null);
		obj.setPortCode_EWB((arr[52] != null) ? arr[52].toString() : null);
		obj.setPortCode_DigiGST_GSTR1((arr[53] != null) ? arr[53].toString() : null);
		obj.setShippingBillNumber_EINV((arr[54] != null) ? arr[54].toString() : null);
		obj.setShippingBillNumber_EWB((arr[55] != null) ? "'".concat(arr[55].toString()) : null);
		obj.setShippingBillNumber_DigiGST_GSTR1((arr[56] != null) ? "'".concat(arr[56].toString()) : null);
		obj.setShippingBillDate_EINV((arr[57] != null) ? arr[57].toString() : null);
		obj.setShippingBillDate_EWB((arr[58] != null) ? arr[58].toString() : null);
		obj.setShippingBillDate_DigiGST_GSTR1((arr[59] != null) ? arr[59].toString() : null);
		obj.setIRN_EINV((arr[60] != null) ? arr[60].toString() : null);
		obj.setIRN_DigiGST_GSTR1((arr[61] != null) ? arr[61].toString() : null);
		obj.setIRNGenDate_EINV((arr[62] != null) ? arr[62].toString() : null);
		obj.setIRNGenDate_DigiGST_GSTR1((arr[63] != null) ? arr[63].toString() : null);
		obj.setStatus_EINV((arr[64] != null) ? arr[64].toString() : null);
		obj.setAutoDraftstatus_EINV((arr[65] != null) ? arr[65].toString() : null);
		obj.setAutoDraftedDate_EINV((arr[66] != null) ? arr[66].toString() : null);
		obj.setErrorCode_EINV((arr[67] != null) ? arr[67].toString() : null);
		obj.setErrorMessage_EINV((arr[68] != null) ? arr[68].toString() : null);
		obj.setTableType_EINV((arr[69] != null) ? arr[69].toString() : null);
		obj.setTableType_DigiGST_GSTR1((arr[70] != null) ? arr[70].toString() : null);
		obj.setEWBNumber_EWB((arr[71] != null) ? "'".concat(arr[71].toString()) + "\t" : null);
		obj.setEWBNumber_DigiGST_GSTR1((arr[72] != null) ? "'".concat(arr[72].toString()) + "\t" : null);
		obj.setEWBDate_EWB((arr[73] != null) ? arr[73].toString() : null);
		obj.setEWBDate_DigiGST_GSTR1(
				(arr[74] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[74].toString()) : null);
		obj.setEWBValidupto_EWB((arr[75] != null) ? arr[75].toString() : null);
		obj.setEWBValidupto_DigiGST_GSTR1((arr[76] != null) ? arr[76].toString() : null);
		obj.setEWBRejectStatus_EWB((arr[77] != null) ? arr[77].toString() : null);
		obj.setEWBExtendedtimes_EWB((arr[78] != null) ? arr[78].toString() : null);
		obj.setTransactionType_EWB((arr[79] != null) ? arr[79].toString() : null);
		obj.setTransactionType_DigiGST_GSTR1((arr[80] != null) ? arr[80].toString() : null);
		obj.setDocCategory_EWB((arr[81] != null) ? arr[81].toString() : null);
		obj.setDocCategory_DigiGST_GSTR1((arr[82] != null) ? arr[82].toString() : null);
		obj.setSupplierTradeName_EWB((arr[83] != null) ? arr[83].toString() : null);
		obj.setSupplierTradeName_DigiGST_GSTR1((arr[84] != null) ? arr[84].toString() : null);
		obj.setSupplierAddress1_EWB((arr[85] != null) ? arr[85].toString() : null);
		obj.setSupplierAddress1_DigiGST_GSTR1((arr[86] != null) ? arr[86].toString() : null);
		obj.setSupplierAddress2_EWB((arr[87] != null) ? arr[87].toString() : null);
		obj.setSupplierAddress2_DigiGST_GSTR1((arr[88] != null) ? arr[88].toString() : null);
		obj.setSupplierLocation_EWB((arr[89] != null) ? arr[89].toString() : null);
		obj.setSupplierLocation_DigiGST_GSTR1((arr[90] != null) ? arr[90].toString() : null);
		obj.setSupplierPincode_EWB((arr[91] != null) ? arr[91].toString() : null);
		obj.setSupplierPincode_DigiGST_GSTR1((arr[92] != null) ? arr[92].toString() : null);
		obj.setSupplierStateCode_EWB((arr[93] != null) ? arr[93].toString() : null);
		obj.setSupplierStateCode_DigiGST_GSTR1((arr[94] != null) ? arr[94].toString() : null);
		obj.setCustomerTradeName_EWB((arr[95] != null) ? arr[95].toString() : null);
		obj.setCustomerTradeName_DigiGST_GSTR1((arr[96] != null) ? arr[96].toString() : null);
		obj.setCustomerAddress1_EWB((arr[97] != null) ? arr[97].toString() : null);
		obj.setCustomerAddress1_DigiGST_GSTR1((arr[98] != null) ? arr[98].toString() : null);
		obj.setCustomerAddress2_EWB((arr[99] != null) ? arr[99].toString() : null);
		obj.setCustomerAddress2_DigiGST_GSTR1((arr[100] != null) ? arr[100].toString() : null);
		obj.setCustomerLocation_EWB((arr[101] != null) ? arr[101].toString() : null);
		obj.setCustomerLocation_DigiGST_GSTR1((arr[102] != null) ? arr[102].toString() : null);
		obj.setCustomerPincode_EWB((arr[103] != null) ? arr[103].toString() : null);
		obj.setCustomerPincode_DigiGST_GSTR1((arr[104] != null) ? arr[104].toString() : null);
		obj.setCustomerStateCode_EWB((arr[105] != null) ? arr[105].toString() : null);
		obj.setCustomerStateCode_DigiGST_GSTR1((arr[106] != null) ? arr[106].toString() : null);
		obj.setDispatcherTradeName_EWB((arr[107] != null) ? arr[107].toString() : null);
		obj.setDispatcherTradeName_DigiGST_GSTR1((arr[108] != null) ? arr[108].toString() : null);
		obj.setDispatcherAddress1_EWB((arr[109] != null) ? arr[109].toString() : null);
		obj.setDispatcherAddress1_DigiGST_GSTR1((arr[110] != null) ? arr[110].toString() : null);
		obj.setDispatcherAddress2_EWB((arr[111] != null) ? arr[111].toString() : null);
		obj.setDispatcherAddress2_DigiGST_GSTR1((arr[112] != null) ? arr[112].toString() : null);
		obj.setDispatcherLocation_EWB((arr[113] != null) ? arr[113].toString() : null);
		obj.setDispatcherLocation_DigiGST_GSTR1((arr[114] != null) ? arr[114].toString() : null);
		obj.setDispatcherPincode_EWB((arr[115] != null) ? arr[115].toString() : null);
		obj.setDispatcherPincode_DigiGST_GSTR1((arr[116] != null) ? arr[116].toString() : null);
		obj.setDispatcherStateCode_EWB((arr[117] != null) ? arr[117].toString() : null);
		obj.setDispatcherStateCode_DigiGST_GSTR1((arr[118] != null) ? arr[118].toString() : null);
		obj.setShipToTradeName_EWB((arr[119] != null) ? arr[119].toString() : null);
		obj.setShipToTradeName_DigiGST_GSTR1((arr[120] != null) ? arr[120].toString() : null);
		obj.setShipToAddress1_EWB((arr[121] != null) ? arr[121].toString() : null);
		obj.setShipToAddress1_DigiGST_GSTR1((arr[122] != null) ? arr[122].toString() : null);
		obj.setShipToAddress2_EWB((arr[123] != null) ? arr[123].toString() : null);
		obj.setShipToAddress2_DigiGST_GSTR1((arr[124] != null) ? arr[124].toString() : null);
		obj.setShipToLocation_EWB((arr[125] != null) ? arr[125].toString() : null);
		obj.setShipToLocation_DigiGST_GSTR1((arr[126] != null) ? arr[126].toString() : null);
		obj.setShipToPincode_EWB((arr[127] != null) ? arr[127].toString() : null);
		obj.setShipToPincode_DigiGST_GSTR1((arr[128] != null) ? arr[128].toString() : null);
		obj.setShipToStateCode_EWB((arr[129] != null) ? arr[129].toString() : null);
		obj.setShipToStateCode_DigiGST_GSTR1((arr[130] != null) ? arr[130].toString() : null);
		obj.setModeofTransport_EWB((arr[131] != null) ? arr[131].toString() : null);
		obj.setModeofTransport_DigiGST_GSTR1((arr[132] != null) ? arr[132].toString() : null);
		obj.setTransporterID_EWB((arr[133] != null) ? arr[133].toString() : null);
		obj.setTransporterID_DigiGST_GSTR1((arr[134] != null) ? arr[134].toString() : null);
		obj.setTransporterName_EWB((arr[135] != null) ? arr[135].toString() : null);
		obj.setTransporterName_DigiGST_GSTR1((arr[136] != null) ? arr[136].toString() : null);
		obj.setTransportDocNo_EWB((arr[137] != null) ? "'".concat(arr[137].toString()) : null);
		obj.setTransportDocNo_DigiGST_GSTR1((arr[138] != null) ? "'".concat(arr[138].toString()) : null);
		obj.setTransportDocDate_EWB((arr[139] != null) ? arr[139].toString() : null);
		obj.setTransportDocDate_DigiGST_GSTR1((arr[140] != null) ? arr[140].toString() : null);
		obj.setDistance_EWB((arr[141] != null) ? arr[141].toString() : null);
		obj.setDistance_DigiGST_GSTR1((arr[142] != null) ? arr[142].toString() : null);
		obj.setVehicleNo_EWB((arr[143] != null) ? arr[143].toString() : null);
		obj.setVehicleNo_DigiGST_GSTR1((arr[144] != null) ? arr[144].toString() : null);
		obj.setVehicleType_EWB((arr[145] != null) ? arr[145].toString() : null);
		obj.setVehicleType_DigiGST_GSTR1((arr[146] != null) ? arr[146].toString() : null);
		obj.setCustomerType_DigiGST_GSTR1((arr[147] != null) ? arr[147].toString() : null);
		obj.setCustomerCode_DigiGST_GSTR1((arr[148] != null) ? arr[148].toString() : null);
		obj.setAccountingVoucherNumber_DigiGST_GSTR1((arr[149] != null) ? arr[149].toString() : null);
		obj.setAccountingVoucherDate_DigiGST_GSTR1((arr[150] != null) ? arr[150].toString() : null);
		obj.setCompanyCode_DigiGST_GSTR1((arr[151] != null) ? arr[151].toString() : null);
		obj.setRecordStatus_DigiGST_GSTR1((arr[152] != null) ? arr[152].toString() : null);
		obj.setE_InvoiceGetCallDate((arr[153] != null) ? arr[153].toString() : null);
		obj.setE_InvoiceGetCallTime((arr[154] != null) ? arr[154].toString() : null);
		obj.setEWBGetCallDate((arr[155] != null) ? arr[155].toString() : null);
		obj.setEWBGetCallTime((arr[156] != null) ? arr[156].toString() : null);
		obj.setProcessingDateofDigiGST_GSTR1((arr[157] != null) ? arr[157].toString() : null);
		obj.setProcessingTimeofDigiGST_GSTR1((arr[158] != null) ? arr[158].toString() : null);
		obj.setSource_EINV((arr[159] != null) ? arr[159].toString() : null);
		obj.setSource_EWB((arr[160] != null) ? arr[160].toString() : null);
		obj.setSource_DigiGST_GSTR1((arr[161] != null) ? arr[161].toString() : null);
		obj.setEINV_StatusatDigiGST((arr[162] != null) ? arr[162].toString() : null);
		obj.setEWB_StatusatDigiGST((arr[163] != null) ? arr[163].toString() : null);

		String value = (arr[164] != null) ? arr[164].toString() : null;
		if ("1".equalsIgnoreCase(value))
			obj.setGSTR1_StatusatDigiGST("DigiGST Processed");

		obj.setReconID((arr[165] != null) ? DownloadReportsConstant.CSVCHARACTER.concat(arr[165].toString()) : null);
		obj.setReconDate((arr[166] != null) ? arr[166].toString() : null);
		obj.setReconTime((arr[167] != null) ? arr[167].toString() : null);
		

		return obj;

	}

	private static File createTempDir(Long configId, String reportType) throws IOException {

		String tempFolderPrefix = "ReconReports" + "_" + reportType + "_" + configId;
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
		EWB3WayReconDownloadReportsEntity obj = new EWB3WayReconDownloadReportsEntity();
		obj.setConfigId(configId);
		obj.setIsDownloadable(false);
		obj.setReportType(reportType);

		reportRepo.save(obj);

	}

}
