package com.ey.advisory.app.gstr1a.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
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
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.ey.advisory.core.async.domain.master.CustomisedReportMasterEntity;
import com.ey.advisory.core.async.repositories.master.CustomisedReportMasterRepo;
import com.ey.advisory.core.config.ConfigConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

@Component("AsyncProcessedGstr1aReportDownloadServiceImpl")
@Slf4j
public class AsyncProcessedGstr1aReportDownloadServiceImpl
		implements AsyncReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;

	@Autowired
	CustomisedFieldSelRepo custFieldSeleRepo;

	@Autowired
	CustomisedReportMasterRepo custRetMasterRepo;

	@Autowired
	@Qualifier("AspProcessedAsUploadedReportConvertor")
	AspProcessedAsUploadedReportConvertor aspUploadReptConverter;

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
			// String dataType = entity.getDataType();
			Long entityId = entity.getEntityId();

			/* String status = entity.getStatus(); */

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

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			String timeMilli = dtf.format(LocalDateTime.now());

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ reportCateg + "_" + "asUploaded" + "_" + timeMilli + ".csv";

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

			StatefulBeanToCsv<DataStatusEinvoiceDto> beanWriter = getBeanWriter(
					writer, isActiveFieldSel);

			WritetoCsv(chunkCount, id, reportType, beanWriter, reportCateg,
					isActiveFieldSel);

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
						.uploadFile(zipFile, ConfigConstants.ASYNC_GSTR1A_REPORT);
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
	private void WritetoCsv(Integer chunkNo, Long id, String reportType,
			StatefulBeanToCsv<DataStatusEinvoiceDto> beanWriter,
			String reportCateg,
			Optional<CustomisedFieldSelEntity> isActiveFieldSel) {

		List<Object[]> list = null;
		List<DataStatusEinvoiceDto> responseFromDao = null;

		String procName = null;

		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)) {

			procName = "Gstr1aCustReportData";
		}
		try {
			List<String> selectedList = new ArrayList<String>(Arrays.asList(
					isActiveFieldSel.get().getHeaderMapping().split(",")));

			Map<String, Object> ap = new HashMap<>();
			ap.put("selectedList", selectedList);

			LOGGER.debug("Selected List {} ", selectedList);

			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(procName);

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VALUE", i);
				reportDataProc.setParameter("P_COLUMN_ID",
						isActiveFieldSel.get().getId());

				LOGGER.debug("Column Id  {} ", isActiveFieldSel.get().getId());
				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d", list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream()
							.map(o -> (DataStatusEinvoiceDto) aspUploadReptConverter
									.convert1(ap, o))
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

	private Integer getChunkCount(String reportCateg, Long id) {

		String procName = null;

		if (ReportTypeConstants.PROCESSED_SUMMARY.equalsIgnoreCase(reportCateg)
				|| ReportTypeConstants.REVIEW_SUMMARY
						.equalsIgnoreCase(reportCateg)) {
			procName = "processedGstr1aChunkCount";
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

}
