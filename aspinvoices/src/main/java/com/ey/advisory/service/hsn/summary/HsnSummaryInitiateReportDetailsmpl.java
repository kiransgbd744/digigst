package com.ey.advisory.service.hsn.summary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.HsnSummaryConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
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
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("HsnSummaryInitiateReportDetailsmpl")
public class HsnSummaryInitiateReportDetailsmpl
		implements HsnSummaryInitiateReportDetails {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnSummaryConfigRepository")
	HsnSummaryConfigRepository reconConfigRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	FileStatusDownloadReportRepository reportRepo;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	private static int CSV_BUFFER_SIZE = 8192;
	private static int serialNo = 1;

	@Override
	public void getInitiateReconReportData(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Long chunkSize = 10000L;
		Writer writer = null;

		if (LOGGER.isDebugEnabled()) {
			msg = String
					.format("Get Consolidated_SalesRegister Recon Report Details "
							+ "with configId:'%s'", configId);
			LOGGER.debug(msg);
		}
		String reportType = "GSTR1HSNSummaryReport";
		updateReconConfigStatus(ReconStatusConstants.RECON_INPROGRESS,
				configId);

		tempDir = createTempDir(configId);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timeMilli = dtf
				.format(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
		fullPath = tempDir.getAbsolutePath() + File.separator + reportType + "_"
				+ timeMilli + ".csv";

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
							"USP_HSN_SUMMARY_RPT_INS_CHUNK");

			storedProc.registerStoredProcedureParameter("RECON_CONFIG_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("RECON_CONFIG_ID", configId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_HSN_SUMMARY_RPT_INS_CHUNK: '%s'",
						configId.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;
			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_HSN_SUMMARY_RPT_INS_CHUNK: configId '%d', "
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
			msg = "No Data To Generate for Report " + reportType;
			LOGGER.error(msg);
			reportRepo.updateStatus(configId, "NO_DATA_FOUND", null,
					LocalDateTime.now());
		} else {

			Long j = 0L;
			try {

				writer = new BufferedWriter(new FileWriter(fullPath), 8192);
				StatefulBeanToCsv<HsnSummaryReportDto> beanWriter = getBeanWriter(
						writer);

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_HSN_SUMMARY_RPT_DISP_CHUNK");

					storedProc.registerStoredProcedureParameter(
							"RECON_CONFIG_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("RECON_CONFIG_ID", configId);

					storedProc.registerStoredProcedureParameter("P_CHUNK_VAL",
							Long.class, ParameterMode.IN);
					storedProc.setParameter("P_CHUNK_VAL", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"call stored proc with "
										+ "params {} Config ID is '%s', "
										+ " chunkNo is %d ",
								configId.toString(), j);
						LOGGER.debug(msg);
					}

					List<Object[]> records = storedProc.getResultList();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("no of records after proc call {} ",
								records.size());
					}

					if (records != null && !records.isEmpty()) {
						serialNo = 1;
						List<HsnSummaryReportDto> reconDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count and List: '%s' - '%s' - '%s'",
									reportType, reconDataList.size(),
									reconDataList);
							LOGGER.debug(msg);
						}
						if (reconDataList != null && !reconDataList.isEmpty()) {
							beanWriter.write(reconDataList);
						}

					}
				}

				if (writer != null) {
					try {
						writer.flush();
						writer.close();
						if (LOGGER.isDebugEnabled()) {
							msg = "Flushed writer successfully";
							LOGGER.debug(msg);
						}
					} catch (IOException e) {
						msg = "Exception while closing the file writer";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}

				String zipFileName = "";
				if (tempDir.list().length > 0) {

					if (LOGGER.isDebugEnabled()) {
						msg = "Creting Zip folder";
						LOGGER.debug(msg);
					}

					zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
							reportType, "", configId);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("ZipFile name : %s", zipFileName);
						LOGGER.debug(msg);
					}

					File zipFile = new File(tempDir, zipFileName);

					Pair<String, String> uploadedZipName = DocumentUtility
							.uploadFile(zipFile, "Anx1FileStatusReport");
					String uploadedDocName = uploadedZipName.getValue0();
					String docId = uploadedZipName.getValue1();
					if (LOGGER.isDebugEnabled()) {
						 msg = "Sucessfully uploaded zip file and updating the "
								+ "status as 'Report Generated'";
						LOGGER.debug(msg);
					}
					reportRepo.updateStatus(configId,
							ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
							docId);
				} else {

					LOGGER.error("No Data found for report id : %d", configId);
					reportRepo.updateStatus(configId,
							ReportStatusConstants.NO_DATA_FOUND, null,
							EYDateUtil.toUTCDateTimeFromLocal(
									LocalDateTime.now()));
				}
				updateReconConfigStatus(ReconStatusConstants.RECON_COMPLETED,
						configId);
			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for "
						+ "Report Type :{}", reportType, ex);
				updateReconConfigStatus(
						ReconStatusConstants.REPORT_GENERATION_FAILED,
						configId);

				String msz = String.format(
						"Exception occured while creating zip file for %d and reportType %s ",
						configId, reportType);

				reportRepo.updateStatus(configId, "REPORT_FAILED", null,
						LocalDateTime.now());
				throw new AppException(ex);
			} finally {

				if (tempDir != null && tempDir.exists()) {
					try {
						FileUtils.deleteDirectory(tempDir);
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(String.format(
									"Deleted the Temp directory/Folder '%s'",
									tempDir.getAbsolutePath()));
						}
					} catch (Exception ex) {
						msg = String.format(
								"Failed to remove the temp "
										+ "directory created for zip: '%s'. This will "
										+ "lead to clogging of disk space.",
								tempDir.getAbsolutePath());
						LOGGER.error(msg, ex);
					}
				}
			}
		}
	}

	private void updateReconConfigStatus(String status, Long configId) {
		reconConfigRepo.updateReconConfigStatusAndReportName(status,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
				configId);
	}

	private HsnSummaryReportDto convert(Object[] arr) {

		HsnSummaryReportDto obj = new HsnSummaryReportDto();

		obj.setSerialNo(DownloadReportsConstant.CSVCHARACTER
				.concat(String.valueOf(serialNo)));
		obj.setHsnCode((arr[0] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString())
				: null);
		obj.setHsnDescription((arr[1] != null) ? arr[1].toString() : null);
		obj.setGstRate((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		obj.setQuantity((arr[3] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setTaxableValue((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setIgst((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setCgst((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setSgst((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setCess((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setTotalValue((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		serialNo++;
		return obj;

	}

	private static File createTempDir(Long batchId) throws IOException {

		String tempFolderPrefix = "GSTR1HSNSummaryReport" + "_" + batchId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private StatefulBeanToCsv<HsnSummaryReportDto> getBeanWriter(Writer writer)
			throws Exception {

		String invoiceHeadersTemplate = commonUtility
				.getProp("hsn.summary.report.header");
		String[] columnMappings = commonUtility
				.getProp("hsn.summary.report.column.mapping").split(",");

		ColumnPositionMappingStrategy<HsnSummaryReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(HsnSummaryReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);

		StatefulBeanToCsvBuilder<HsnSummaryReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<HsnSummaryReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(invoiceHeadersTemplate);
		return beanWriter;
	}
}
