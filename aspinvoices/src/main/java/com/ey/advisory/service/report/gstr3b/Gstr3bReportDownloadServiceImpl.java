package com.ey.advisory.service.report.gstr3b;

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
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr3b.dto.Gstr3BInwardReportDownloadDto;
import com.ey.advisory.app.gstr3b.dto.Gstr3BOutwardReportDownloadDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfig;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportConvertor;
import com.ey.advisory.common.ReportStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N k
 *
 */

@Component("Gstr3bReportDownloadServiceImpl")
@Slf4j
public class Gstr3bReportDownloadServiceImpl
		implements Gstr3bReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BGetReportDownloadImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	@Autowired
	private Environment env;

	@Override
	public void generateOutwardReport(Long id) {

		Writer writer = null;
		File tempDir = null;

		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory for GSTR3B report download";
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
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ reportCateg + "_" + reportType + "_" + timeMilli + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			ReportConfig reportConfig = reportConfigFactory
					.getReportConfig(reportType, reportCateg, dataType);

			ReportConvertor reportConvertor = reportConfig.getReportConvertor();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped GSTR3B OutwardOrInward Reportconvertor";
				LOGGER.debug(msg);
			}

			if ("OUTWARD".equalsIgnoreCase(reportType)) {
				StatefulBeanToCsv<Gstr3BOutwardReportDownloadDto> beanWriter = getBeanWriterforOutward(
						reportConfig, writer);

				WritetoCsvOuward(chunkCount, id, reportConvertor, reportType,
						beanWriter, reportCateg);
			} else if ("INWARD".equalsIgnoreCase(reportType)) {
				StatefulBeanToCsv<Gstr3BInwardReportDownloadDto> beanWriter = getBeanWriterforInward(
						reportConfig, writer);

				WritetoCsvInward(chunkCount, id, reportConvertor, reportType,
						beanWriter, reportCateg);
			}

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

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combinAndZipReportFiles.zipfolder(tempDir,
						reportType, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "Anx1FileStatusReport");
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);
			} else {

				LOGGER.error("No Data found for report id : %d", id);
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}
		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
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
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void WritetoCsvOuward(Integer chunkCount, Long id,
			ReportConvertor reportConvertor, String reportType,
			StatefulBeanToCsv<Gstr3BOutwardReportDownloadDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<Gstr3BOutwardReportDownloadDto> responseFromDao = null;

		String procName = "Gstr3bOutwardChunkData";

		try {
			for (int i = 1; i <= chunkCount; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d", list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream()
							.map(o -> (Gstr3BOutwardReportDownloadDto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Record count after converting object array to DTO ",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {
						beanWriter.write(responseFromDao);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Successfully writing into csv for chunk count: %d ",
									i);
							LOGGER.debug(msg);
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

	@SuppressWarnings("unchecked")
	private void WritetoCsvInward(Integer chunkCount, Long id,
			ReportConvertor reportConvertor, String reportType,
			StatefulBeanToCsv<Gstr3BInwardReportDownloadDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<Gstr3BInwardReportDownloadDto> responseFromDao = null;

		String procName = "Gstr3bInwardChunkData";

		try {
			for (int i = 1; i <= chunkCount; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d", list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream()
							.map(o -> (Gstr3BInwardReportDownloadDto) reportConvertor
									.convert(o, reportType))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Record count after converting object array to DTO ",
								responseFromDao.size());
						LOGGER.debug(msg);
					}

					if (responseFromDao != null && !responseFromDao.isEmpty()) {
						beanWriter.write(responseFromDao);

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Successfully writing into csv for chunk count: %d ",
									i);
							LOGGER.debug(msg);
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

	// GSTR3B outward
	private StatefulBeanToCsv<Gstr3BOutwardReportDownloadDto> getBeanWriterforOutward(
			ReportConfig reportConfig, Writer writer) throws Exception {
		if (!env.containsProperty(reportConfig.getColumnMappingsKey())
				|| !env.containsProperty(reportConfig.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr3BOutwardReportDownloadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr3BOutwardReportDownloadDto.class);
		mappingStrategy.setColumnMapping(env
				.getProperty(reportConfig.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr3BOutwardReportDownloadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr3BOutwardReportDownloadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(reportConfig.getHeadersKey()));
		return beanWriter;
	}

	// gstr3B inward
	private StatefulBeanToCsv<Gstr3BInwardReportDownloadDto> getBeanWriterforInward(
			ReportConfig reportConfig, Writer writer) throws Exception {
		if (!env.containsProperty(reportConfig.getColumnMappingsKey())
				|| !env.containsProperty(reportConfig.getHeadersKey())) {
			String msg = "HeaderKey or ColumnMapping key Not found";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		ColumnPositionMappingStrategy<Gstr3BInwardReportDownloadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr3BInwardReportDownloadDto.class);
		mappingStrategy.setColumnMapping(env
				.getProperty(reportConfig.getColumnMappingsKey()).split(","));

		StatefulBeanToCsvBuilder<Gstr3BInwardReportDownloadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr3BInwardReportDownloadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(env.getProperty(reportConfig.getHeadersKey()));
		return beanWriter;
	}

	private Integer getChunkCount(String reportType, Long id) {
		// TODO Auto-generated method stub

		String procName = null;
		if ("Outward".equalsIgnoreCase(reportType)) {
			procName = "Gstr3bOutwardChunkCount";
		} else if ("Inward".equalsIgnoreCase(reportType)) {
			procName = "Gstr3bInwardChunkCount";
		}

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(procName);

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		Integer response = (Integer) counterProc.getSingleResult();

		return response;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

}
