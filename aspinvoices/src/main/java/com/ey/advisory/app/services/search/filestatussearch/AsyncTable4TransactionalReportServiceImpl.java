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

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr3b.Gstr3bTable4TransactionalReportDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("AsyncTable4TransactionalReportServiceImpl")
public class AsyncTable4TransactionalReportServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BTable4TransactionalReportDownloadImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	private Environment env;
	
	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private Gstr1AsUploadedTnxSplitReportCommonService commonService;
	
	private static final String GSTR3B_CHUNK_ROWS = "gstr3b.table4.report.chunk.rows";
	private static final String CONF_CATEG = "GSTR3B_REPORTS";

	public Integer getChunkingSizes() {

		Config config = configManager.getConfig(CONF_CATEG, GSTR3B_CHUNK_ROWS);
		String chunkSize = config != null ?(config.getValue() != null ? config.getValue()
				: "500000"): "500000";
		return (Integer.valueOf(chunkSize));
	}

	@Override
	public void generateReports(Long id) {

		String fullPath = null;
		File tempDir = null;

		Writer writer = null;

		String fileName = "GSTR3B_Table4_Transactional_Report";

		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory for GSTR3B Table4 Transactional report download";
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

			String reportType = entity.getReportType();
			String reportCateg = entity.getReportCateg();
			String dataType = null;

			Integer chunkCount = getChunkCount(reportType, id);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invoking ChuckCount StoreProc and got response as : %d",
						chunkCount);
				LOGGER.debug(msg);
			}

			if (chunkCount == 0) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						LocalDateTime.now());
				return;
			}

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(fileName) + "_1.csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped GSTR3B Table4 Transactional Reportconvertor";
				LOGGER.debug(msg);
			}
			/*
			 * StatefulBeanToCsv<Gstr3bTable4TransactionalReportDto> beanWriter
			 * = getBeanWriter( reportConfig, writer);
			 */

			WritetoCsv(chunkCount, id, reportConvertor, reportType, reportCateg,
					reportConfig, writer, tempDir, fileName);

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, null,
					LocalDateTime.now());

		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());
			String msg = "Exception occued while while generating GSTR3B csv file";
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
	private void WritetoCsv(Integer chunkSize, Long id,
			ReportConvertor reportConvertor, String reportType,
			String reportCateg, ReportConfig reportConfig, Writer writer,
			File tempDir, String fileName) {

		List<Object[]> list = null;
		List<Gstr3bTable4TransactionalReportDto> responseFromDao = null;

		String procName = "Gstr3bTable4TransactionalChunkData";

		try {
			int fileIndex = 1;
			int count = 0;
			int maxLimitPerFile = getChunkingSizes(); // 200000;

			StatefulBeanToCsv<Gstr3bTable4TransactionalReportDto> beanWriter = getBeanWriter(
					reportConfig, writer);

			for (int noOfChunk = 1; noOfChunk <= chunkSize; noOfChunk++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							noOfChunk);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", noOfChunk);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream().map(
							o -> (Gstr3bTable4TransactionalReportDto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Record count after converting object array to DTO ",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {

						if (count >= maxLimitPerFile) {
							flush(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Invoking  commonService to"
										+ " upload a zip file : " + "id {} ",
										id);
							}

							// Zipping

							commonService.chunkZipFiles(tempDir, id, id,
									reportType,
									1);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking "
										+ "commonService to upload "
										+ "a zip file : id "
										+ "{}, deleted file "
										+ "from TempDir, ", id);
							}

							count = 0;
							fileIndex += 1;

							String fullPath = tempDir.getAbsolutePath()
									+ File.separator +  getUniqueFileName(fileName)
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
							String msg = String.format(
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
									reportType,
									1);
							break;
						}

					}

				}
			}

		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from get ChunkData";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private StatefulBeanToCsv<Gstr3bTable4TransactionalReportDto> getBeanWriter(
			ReportConfig reportConfig, Writer writer) throws Exception {
		if (!env.containsProperty(reportConfig.getColumnMappingsKey())
				|| !env.containsProperty(reportConfig.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr3bTable4TransactionalReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr3bTable4TransactionalReportDto.class);
		mappingStrategy.setColumnMapping(env
				.getProperty(reportConfig.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr3bTable4TransactionalReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr3bTable4TransactionalReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(reportConfig.getHeadersKey()));
		return beanWriter;
	}

	private Integer getChunkCount(String reportType, Long id) {
		// TODO Auto-generated method stub
		Integer chunkSize = 10000;
		String procName = "Gstr3bTable4TransactionalChunkCount";
		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);
		counterProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);
		Integer response = (Integer) counterProc.getSingleResult();

		return response;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

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
