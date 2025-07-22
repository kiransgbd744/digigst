package com.ey.advisory.app.services.search.filestatussearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.customisedreport.CustomisedFieldSelEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.customisedreport.CustomisedFieldSelRepo;
import com.ey.advisory.app.docs.dto.einvoice.DataStatusEinvoiceDto;
import com.ey.advisory.app.report.convertor.AspProcessedAsUploadedReportConvertor;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.core.async.domain.master.CustomisedReportMasterEntity;
import com.ey.advisory.core.async.repositories.master.CustomisedReportMasterRepo;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

@Component("AsyncProcessedReportDownloadServiceImpl")
@Slf4j
public class AsyncProcessedReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("ProcessedReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	CustomisedReportMasterRepo custRetMasterRepo;

	@Autowired
	@Qualifier("AspProcessedAsUploadedReportConvertor")
	AspProcessedAsUploadedReportConvertor aspUploadReptConverter;

	@Autowired
	private Gstr1AsUploadedTnxSplitReportCommonService commonService;

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

			Optional<CustomisedReportMasterEntity> getReportDtls = custRetMasterRepo
					.findByReportNameAndIsActiveTrue("GSTR1_TRANS_LEVEL");

			FileStatusDownloadReportEntity entity = optEntity.get();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetched file entity based on report ID : %s",
						entity.toString());
				LOGGER.debug(msg);
			}

			String reportType = entity.getReportType();
			String reportCateg = entity.getReportCateg();
			Long entityId = entity.getEntityId();

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
						LocalDateTime.now());
				return;
			}

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ reportCateg + "_" + reportType + "_1.csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			Optional<CustomisedFieldSelEntity> isActiveFieldSel = custFieldSeleRepo
					.findByReportIdAndEntityIdAndIsActiveTrue(
							getReportDtls.get().getId(), entityId);

			if (isActiveFieldSel == null || !isActiveFieldSel.isPresent()) {

				String msg = String.format(
						"OnBoarding Configuration is missing for Entity Id %s "
								+ " and ReportType %s invoking default reports ",
						entityId, reportType);
				isActiveFieldSel = custFieldSeleRepo
						.findByReportIdAndEntityIdAndIsActiveTrue(
								getReportDtls.get().getId(), 0L);
				LOGGER.debug(msg);
			}

			WritetoCsv(chunkCount, id, reportType, reportCateg,
					isActiveFieldSel, writer, tempDir);

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATED, null,
					LocalDateTime.now(), null);

		} catch (Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now());
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
	private void WritetoCsv(Integer chunkSize, Long id, String reportType,
			String reportCateg,
			Optional<CustomisedFieldSelEntity> isActiveFieldSel, Writer writer,
			File tempDir) {

		List<Object[]> list = null;
		List<DataStatusEinvoiceDto> responseFromDao = null;
		String procName = null;
		String msg = null;

		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)) {

			procName = "Gstr1CustReportData";
		}

		try {

			int fileIndex = 1;
			int count = 0;
			int maxLimitPerFile = commonService.getChunkingSizes(); // 200000;

			List<String> selectedList = new ArrayList<String>(Arrays.asList(
					isActiveFieldSel.get().getHeaderMapping().split(",")));

			Map<String, Object> ap = new HashMap<>();
			ap.put("selectedList", selectedList);

			LOGGER.debug("Selected List {} ", selectedList);

			StatefulBeanToCsv<DataStatusEinvoiceDto> beanWriter = getBeanWriter(
					writer, isActiveFieldSel);

			for (int noOfChunk = 1; noOfChunk <= chunkSize; noOfChunk++) {

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Inside for loop with count: %d",
							noOfChunk);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", noOfChunk);
				reportDataProc.setParameter("P_COLUMN_ID",
						isActiveFieldSel.get().getId());

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {
					responseFromDao = list.stream().map(
							o -> (DataStatusEinvoiceDto) aspUploadReptConverter
									.convert1(ap, o))
							.collect(Collectors.toCollection(ArrayList::new));

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Record count after converting object array to DTO %d",
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
									getUniqueFileName(
											reportCateg + "_" + reportType),
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
									+ File.separator + reportCateg + "_"
									+ reportType + "_" + fileIndex + ".csv";

							writer = new BufferedWriter(
									new FileWriter(fullPath), 8192);

							beanWriter = getBeanWriter(writer,
									isActiveFieldSel);
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

									reportCateg + "_" + reportType, 1);
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

	private Integer getChunkCount(String reportCateg, Long id) {

		String procName = null;

		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)) {
			procName = "processedChunkCount";
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

	private StatefulBeanToCsv<DataStatusEinvoiceDto> getBeanWriter(
			Writer writer, Optional<CustomisedFieldSelEntity> isActiveFieldSel)
			throws Exception {

		ColumnPositionMappingStrategy<DataStatusEinvoiceDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(DataStatusEinvoiceDto.class);
		mappingStrategy.setColumnMapping(
				isActiveFieldSel.get().getJavaMapp().split(","));

		StatefulBeanToCsvBuilder<DataStatusEinvoiceDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<DataStatusEinvoiceDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(isActiveFieldSel.get().getHeaderMapping() + "\n");
		return beanWriter;
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
