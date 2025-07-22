package com.ey.advisory.app.services.search.filestatussearch;

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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.views.client.GSTR2ProcessedInwarddto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Component("Gstr2ConsolidatedDigistErrorReportServiceImpl")
@Slf4j
public class Gstr2ConsolidatedDigistErrorReportServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	private PRSummarySplitReportCommonService commonService;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	@Override
	public void generateReports(Long id) {

		Writer writer = null;
		File tempDir = null;

		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);

			FileStatusDownloadReportEntity entity = optEntity.get();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}

			String reportType = entity.getReportType();//Consolidated DigiGST Error
			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();

			Integer chunkCount = getChunkCount(reportCateg, id);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking ChuckCount StoreProc and got response as : %d",
						chunkCount);
				LOGGER.debug(msg);
			}

			if (chunkCount == 0) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			// Consolidated DigiGST Error, ProcessedSummary, Inward
			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);
			

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped Reportconvertor based on report type and "
						+ "Report Category";
				LOGGER.debug(msg);
			}
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ "GSTR2_Consolidated_ASP_Error_Report" + "_" + 1 + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			writetoCsv(chunkCount, id, reportConvertor, reportType,
					reportConfig, reportCateg, tempDir, writer);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Sucessfully uploaded zip file and updating the "
						+ "status as 'Report Generated'";
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating csv file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

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
					String msg = String.format("Failed to remove the temp "
							+ "directory created for zip: '%s'. This will "
							+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void writetoCsv(Integer chunkSize, Long id,
			ReportConvertor reportConvertor, String reportType,
			ReportConfig reportConfig, String reportCateg, File tempDir,
			Writer writer) {

		List<Object[]> list = null;
		List<GSTR2ProcessedInwarddto> responseFromDao = null;
		String msg = null;

		String procName = null;

		procName = "gstr2ConslidatedErrorChunkData";

		try {
			int fileIndex = 1;
			int count = 0;
			int maxLimitPerFile = commonService.getChunkingSizes();// 200000

			StatefulBeanToCsv<GSTR2ProcessedInwarddto> beanWriter = getBeanWriter(
					reportConfig, writer);

			for (int noOfChunk = 1; noOfChunk <= chunkSize; noOfChunk++) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Inside for loop with count: %d / %d",
							noOfChunk, chunkSize);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", noOfChunk);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {
					responseFromDao = list.stream()
							.map(o -> (GSTR2ProcessedInwarddto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Record count after converting object array to DTO  %d",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {
						if (count >= maxLimitPerFile) {
							flush(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Invoking PR Summary commonService to"
												+ " upload a zip file : "
												+ "id {} ",
										id);
							}

							// Zipping

							commonService.chunkZipFiles(tempDir, id, id,
									"GSTR2_Consolidated_ASP_Error_Report", 1);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking PR Summary "
										+ "commonService to upload "
										+ "a zip file : id "
										+ "{}, deleted file "
										+ "from TempDir, ", id);
							}

							count = 0;
							fileIndex += 1;

							String fullPath = tempDir.getAbsolutePath()
									+ File.separator
									+ "GSTR2_Consolidated_ASP_Error_Report"
									+ "_" + fileIndex + ".csv";

							writer = new BufferedWriter(
									new FileWriter(fullPath), 8192);

							beanWriter = getBeanWriter(reportConfig, writer);
						}
						count += responseFromDao.size();
						beanWriter.write(responseFromDao);

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
									responseFromDao.size());
							LOGGER.debug(msg);
						}
						if (chunkSize == 1 || noOfChunk == chunkSize) {
							flush(writer);
							commonService.chunkZipFiles(tempDir, id, id,
									"GSTR2_Consolidated_ASP_Error_Report", 1);
							break;
						}

					}

				}
			}

		} catch (Exception ex) {
			msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	/**
	 * @param writer
	 */
	private void flush(Writer writer) {
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

	private Integer getChunkCount(String reportCateg, Long id) {

		String procName = null;

		procName = "gstr2ConslidatedErrorChunkCount";

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		Integer response = (Integer) counterProc.getSingleResult();

		return response;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<GSTR2ProcessedInwarddto> getBeanWriter(
			ReportConfig config, Writer writer) throws Exception {

		if (!env.containsProperty(config.getColumnMappingsKey())
				|| !env.containsProperty(config.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<GSTR2ProcessedInwarddto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(GSTR2ProcessedInwarddto.class);
		mappingStrategy.setColumnMapping(
				env.getProperty(config.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<GSTR2ProcessedInwarddto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<GSTR2ProcessedInwarddto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(config.getHeadersKey()));
		return beanWriter;
	}

}
