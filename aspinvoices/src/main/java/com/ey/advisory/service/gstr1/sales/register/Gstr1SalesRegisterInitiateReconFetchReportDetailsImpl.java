package com.ey.advisory.service.gstr1.sales.register;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.SalesRegisterConfigRepository;
import com.ey.advisory.app.data.repositories.client.SalesRegisterDownloadReconReportsRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr1SalesRegisterInitiateReconFetchReportDetailsImpl")
public class Gstr1SalesRegisterInitiateReconFetchReportDetailsImpl
		implements Gstr1SalesRegisterInitiateReconFetchReportDetails {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("SalesRegisterConfigRepository")
	SalesRegisterConfigRepository reconConfigRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("SalesRegisterDownloadReconReportsRepository")
	SalesRegisterDownloadReconReportsRepository reportRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void getInitiateReconReportData(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Long chunkSize = 10000L;

		if (LOGGER.isDebugEnabled()) {
			msg = String
					.format("Get Consolidated_SalesRegister Recon Report Details "
							+ "with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String[] reportType = { "Exact Match", "Mismatch",
				"Additional in Sales Register", "Additional in DigiGST",
				"Consolidated SRVSDIGI Report" };

		updateReconConfigStatus(ReconStatusConstants.RECON_INPROGRESS,
				configId);
		for (int i = 0; i < reportType.length; i++) {

			saveORUpdate(configId, reportType[i]);
			tempDir = createTempDir(configId, reportType[i]);

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(reportType[i]) + ".csv";

			tempDir = createTempDir(configId, reportType[i]);

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
						.createStoredProcedureQuery("USP_SRVSDIGI_INS_CHUNK");

				storedProc.registerStoredProcedureParameter(
						"P_RECON_REPORT_CONFIG_ID", Long.class,
						ParameterMode.IN);

				storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

				storedProc.registerStoredProcedureParameter("P_REPORT_NAME",
						String.class, ParameterMode.IN);

				storedProc.setParameter("P_REPORT_NAME", reportType[i]);

				storedProc.registerStoredProcedureParameter("P_CHUNK_SIZE",
						Long.class, ParameterMode.IN);

				storedProc.setParameter("P_CHUNK_SIZE", chunkSize);

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Executing chunking proc"
									+ " USP_SRVSDIGI_INS_CHUNK: '%s'",
							configId.toString());
					LOGGER.debug(msg);
				}

				Integer chunks = (Integer) storedProc.getSingleResult();

				noOfChunk = chunks;

				// Optional<SalesRegisterReconDownloadReportsEntity> entity =
				// reportRepo.findByConfigIdAndReportType(configId,reportType[i]);
				// noOfChunk = entity.get().getChunkSize();
				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Chunking proc Executed"
							+ " USP_SRVSDIGI_INS_CHUNK: configId '%d', "
							+ "noOfChunk %d ", configId, noOfChunk);
					LOGGER.debug(msg);
				}

			} catch (Exception ex) {

				msg = String.format("Error while executing chunking proc for "
						+ "Consolidated  configId %d", configId);
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);

			}
			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate for Report " + reportType[i];
				LOGGER.error(msg);

				// updateReconConfigStatus(ReconStatusConstants.NO_DATA_FOUND,
				// configId);

				// throw new AppException(msg);
			}

			Long j = 0L;

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("sales.register.recon.report.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("sales.register.recon.report.column.mapping")
						.split(",");

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_SRVSDIGI_RPT_MASTER");

					storedProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					storedProc.setParameter("P_RECON_REPORT_CONFIG_ID",
							configId);

					storedProc.registerStoredProcedureParameter("P_REPORT_TYPE",
							String.class, ParameterMode.IN);
					storedProc.setParameter("P_REPORT_TYPE", reportType[i]);

					storedProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
							Long.class, ParameterMode.IN);
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

						List<SalesRegisterInitiateReconReportDto> reconDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, reconDataList.size());
							LOGGER.debug(msg);
						}
						if (reconDataList != null && !reconDataList.isEmpty()) {

							ColumnPositionMappingStrategy<SalesRegisterInitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
							mappingStrategy.setType(
									SalesRegisterInitiateReconReportDto.class);
							mappingStrategy.setColumnMapping(columnMappings);
							StatefulBeanToCsvBuilder<SalesRegisterInitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
									writer);
							StatefulBeanToCsv<SalesRegisterInitiateReconReportDto> beanWriter = builder
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

					String uploadedDocName = DocumentUtility
							.uploadZipFile(zipFile, "SalesRegisterReconReport");
					reportRepo.updateReconFilePath(uploadedDocName, configId,
							reportType[i]);

				}
				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.REPORT_GENERATED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						configId);
			} catch (Exception e) {
				String msz = String.format(
						"Exception occured while creating zip file for %d and reportType %s ",
						configId, reportType);
				LOGGER.error(msz, e);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.REPORT_GENERATION_FAILED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						configId);

			} finally {
				deleteTemporaryDirectory(tempDir);
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

	private SalesRegisterInitiateReconReportDto convert(Object[] arr) {

		SalesRegisterInitiateReconReportDto obj = new SalesRegisterInitiateReconReportDto();

		obj.setReturnPeriod((arr[0] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString())
				: null);
		obj.setSupplierGSTIN((arr[1] != null) ? arr[1].toString() : null);
		obj.setBusinessPlace((arr[2] != null) ? arr[2].toString() : null);
		obj.setDocumentType((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocumentNumber((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setDocumentDate((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setCustomerGSTINSR((arr[6] != null) ? arr[6].toString() : null);
		obj.setInvoiceValueSR((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setTaxablevalueSR((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setIgstSR((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setCgstSR((arr[10] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[10].toString()) : null);
		obj.setSgstSR((arr[11] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[11].toString()) : null);
		obj.setAdvaloremCessSR(
				(arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setSpecificCessSR(
				(arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setTransactionTypeSR((arr[14] != null) ? arr[14].toString() : null);
		obj.setCustomerGSTINDigiGST(
				(arr[15] != null) ? arr[15].toString() : null);
		obj.setInvoiceValueDigiGST(
				(arr[16] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[16].toString()) : null);
		obj.setTaxablevalueDigiGST(
				(arr[17] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[17].toString()) : null);
		obj.setIgstDigiGST(
				(arr[18] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[18].toString()) : null);
		obj.setCgstDigiGST(
				(arr[19] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[19].toString()) : null);
		obj.setSgstDigiGST(
				(arr[20] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[20].toString()) : null);
		obj.setAdvaloremCessDigiGST(
				(arr[21] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[21].toString()) : null);
		obj.setSpecificCessDigiGST(
				(arr[22] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[22].toString()) : null);
		obj.setTransactionTypeDigiGST(
				(arr[23] != null) ? arr[23].toString() : null);
		obj.setInvoiceValueDifference(
				(arr[24] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[24].toString()) : null);
		obj.setTaxablevalueDifference(
				(arr[25] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[25].toString()) : null);
		obj.setIgstDifference(
				(arr[26] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[26].toString()) : null);
		obj.setCgstDifference(
				(arr[27] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[27].toString()) : null);
		obj.setSgstDifference(
				(arr[28] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[28].toString()) : null);
		obj.setAdvaloremCessDifference(
				(arr[29] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[29].toString()) : null);
		obj.setSpecificCessDifference(
				(arr[30] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[30].toString()) : null);
		obj.setReportType((arr[31] != null) ? arr[31].toString() : null);
		obj.setMismatchReason((arr[32] != null) ? arr[32].toString() : null);

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
		SalesRegisterReconDownloadReportsEntity obj = new SalesRegisterReconDownloadReportsEntity();
		obj.setConfigId(configId);
		obj.setIsDownloadable(false);
		obj.setReportType(reportType);

		reportRepo.save(obj);

	}

}
