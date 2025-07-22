package com.ey.advisory.app.services.search.filestatussearch;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.docs.dto.simplified.Gstr1RateLevelReportDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */

@Component("Gstr1RateLevelReportDownloadServiceImpl")
@Slf4j
public class Gstr1RateLevelReportDownloadServiceImpl
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

	@Override
	public void generateReports(Long id) {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Integer chunkSize = 10000;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Gstr1 Rate Level Report Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

		Optional<FileStatusDownloadReportEntity> opt = downloadRepository
				.findById(id);

		FileStatusDownloadReportEntity entity = opt.get();

		String entityName = entityInfo
				.findEntityNameByEntityId(entity.getEntityId());

		String reportType = entity.getReportType();

		try {
			tempDir = createTempDir(entityName);

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(entityName) + ".csv";

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Config ID is '%s',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						id.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}

			// chunk value

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_INS_CHUNK_RATE_LEVEL_REPORT");

			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_INS_CHUNK_RATE_LEVEL_REPORT: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_EWB_DOWN_RPRT_INS_CHUNK: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk == 0) {
				msg = "No Data To Generate Report - Gstr1 Rate Level Report ";
				LOGGER.error(msg);

				downloadRepository.updateStatus(id,
						ReconStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

				return;
			}

			int j = 0;

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					8192)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.rate_level.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("gstr1.rate_levelreport.download.mapping")
						.split(",");

				ColumnPositionMappingStrategy<Gstr1RateLevelReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Gstr1RateLevelReportDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Gstr1RateLevelReportDto> builder = new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<Gstr1RateLevelReportDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcEwb = entityManager
							.createStoredProcedureQuery(
									"USP_DISP_CHUNK_RATE_LEVEL_REPORT");

					storedProcEwb.registerStoredProcedureParameter(
							"P_REPORT_DOWNLOAD_ID", Long.class,
							ParameterMode.IN);

					storedProcEwb.setParameter("P_REPORT_DOWNLOAD_ID", id);

					storedProcEwb.registerStoredProcedureParameter(
							"P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
					storedProcEwb.setParameter("P_CHUNK_VALUE", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					List<Object[]> records = storedProcEwb.getResultList();

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

						List<Gstr1RateLevelReportDto> dataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, dataList.size());
							LOGGER.debug(msg);
						}
						if (dataList != null && !dataList.isEmpty()) {

							long generationStTime = System.currentTimeMillis();
							beanWriter.write(dataList);
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

				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				throw new AppException(ex);
			}
			// Zipping
			String zipFileName = null;
			if (tempDir.list().length > 0) {
				zipFileName = combineAndZipCsvFiles.zipfolder(id, tempDir,
						"Outward - Rate Level (Limited Column)", entityName,
						"");
				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Before uploading zipFileName %s ",
							zipFileName);
				}

				File zipFile = new File(tempDir, zipFileName);

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("Before uploading zipFile %s ",
							zipFile);
				}

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, zipFileName);
					LOGGER.debug(msg);
				}

				Pair<String, String> uploadedZipName = DocumentUtility
						.uploadFile(zipFile, "Anx1FileStatusReport");
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				if (LOGGER.isDebugEnabled()) {
					msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}
				downloadRepository.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						docId);
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

	private Gstr1RateLevelReportDto convert(Object[] arr) {

		Gstr1RateLevelReportDto obj = new Gstr1RateLevelReportDto();

		obj.setReturnPeriod((arr[0] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[0].toString())
				: null);
		obj.setTransactionType((arr[1] != null) ? arr[1].toString() : null);
		obj.setGSTR1TableNumber((arr[2] != null) ? arr[2].toString() : null);
		obj.setSupplierGSTIN((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocumentType((arr[4] != null) ? arr[4].toString() : null);
		obj.setDocumentNumber((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setDocumentDate((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setCustomerGSTIN((arr[7] != null) ? arr[7].toString() : null);
		obj.setBillingPOS((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setGSTRate((arr[9] != null) ? arr[9].toString() : null);
		obj.setItemAssessableAmount(
				(arr[10] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[10].toString()) : null);
		obj.setItemIGSTAmount(
				(arr[11] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[11].toString()) : null);
		obj.setItemCGSTAmount(
				(arr[12] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[12].toString()) : null);
		obj.setItemSGSTAmount(
				(arr[13] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[13].toString()) : null);
		obj.setTotalTax((arr[14] != null) ? DownloadReportsConstant.CSVCHARACTER
				.concat(arr[14].toString()) : null);
		obj.setInvoiceValue(
				(arr[15] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[15].toString()) : null);

		return obj;

	}

	private static File createTempDir(String entityName) throws IOException {

		String tempFolderPrefix = "Gstr1 Report" + "_" + entityName;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private static String getUniqueFileName(String entityName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}

		String date = null;
		String time = null;
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		LocalDateTime istDateTimeFromUTC = EYDateUtil.toISTDateTimeFromUTC(now);
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

		DateTimeFormatter FOMATTER1 = DateTimeFormatter.ofPattern("HHmmssms");

		date = FOMATTER.format(istDateTimeFromUTC);
		time = FOMATTER1.format(istDateTimeFromUTC);
		String fileNameWithFromDateToDate = "Outward - Rate Level (Limited Column)"
				+ "_" + entityName + "_" + date + time.substring(0, 6);
		;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithFromDateToDate);
		}

		return fileNameWithFromDateToDate;
	}

}
