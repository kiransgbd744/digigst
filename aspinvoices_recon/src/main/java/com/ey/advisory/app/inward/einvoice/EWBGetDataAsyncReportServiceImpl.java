package com.ey.advisory.app.inward.einvoice;

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
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.gstr2.initiaterecon.EWBDownloadReportDto;
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
 * @author Sakshi.jain
 *
 */

@Component("EWBGetDataAsyncReportServiceImpl")
@Slf4j
public class EWBGetDataAsyncReportServiceImpl
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

	@Autowired
	HsnCache hsnCache;

	@Override
	public void generateReports(Long id) {

		String fullPath = null;
		File tempDir = null;
		Integer noOfChunk = 0;
		String msg = null;
		Integer chunkSize = 10000;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Get Summary Download " + "with id:'%s'", id);
			LOGGER.debug(msg);
		}

		Optional<FileStatusDownloadReportEntity> opt = downloadRepository
				.findById(id);

		FileStatusDownloadReportEntity entity = opt.get();

		String toDate = entity.getRequestToDate().toString().substring(0, 10);
		String fromDate = entity.getRequestFromDate().toString().substring(0,
				10);

		String entityName = entityInfo
				.findEntityNameByEntityId(entity.getEntityId());

		String reportType = entity.getReportType();

		try {
			tempDir = createTempDir(entityName, toDate, fromDate);

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(entityName, toDate, fromDate) + ".csv";

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
					.createStoredProcedureQuery("USP_EWB_DOWN_RPRT_INS_CHUNK");

			storedProc.registerStoredProcedureParameter("P_REPORT_DOWNLOAD_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_EWB_DOWN_RPRT_INS_CHUNK: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_EWB_DOWN_RPRT_INS_CHUNK: id '%d', "
						+ "noOfChunk %d ", id, noOfChunk);
				LOGGER.debug(msg);
			}

			noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

			if (noOfChunk <= 0) {
				msg = "No Data To Generate Report";
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
						.getProp("ewb.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("ewb.report.download.mapping").split(",");

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc1 = entityManager
							.createStoredProcedureQuery(
									"USP_EWB_DOWN_RPRT_DISP_CHUNK");

					storedProc1.registerStoredProcedureParameter(
							"P_REPORT_DOWNLOAD_ID", Long.class,
							ParameterMode.IN);

					storedProc1.setParameter("P_REPORT_DOWNLOAD_ID", id);

					storedProc1.registerStoredProcedureParameter(
							"P_CHUNK_VALUE", Integer.class, ParameterMode.IN);
					storedProc1.setParameter("P_CHUNK_VALUE", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")

					List<Object[]> records = storedProc1.getResultList();

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

						List<EWBDownloadReportDto> reconDataList = records
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

							ColumnPositionMappingStrategy<EWBDownloadReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
							mappingStrategy.setType(EWBDownloadReportDto.class);
							mappingStrategy.setColumnMapping(columnMappings);
							StatefulBeanToCsvBuilder<EWBDownloadReportDto> builder = new StatefulBeanToCsvBuilder<>(
									writer);
							StatefulBeanToCsv<EWBDownloadReportDto> beanWriter = builder
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

				downloadRepository.updateStatus(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				throw new AppException(ex);
			}
			// Zipping
			String zipFileName = null;
			if (tempDir.list().length > 0) {
				zipFileName = combineAndZipCsvFiles.zipfolder(id, tempDir,
						"EWB Data", entityName, "");
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
				// downloadRepository.updateReportFilePath(uploadedDocName, id);

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

	private EWBDownloadReportDto convert(Object[] arr) {

		EWBDownloadReportDto obj = new EWBDownloadReportDto();

		obj.setEwbNo((arr[0] != null) ? arr[0].toString() + "\t" : null);
		obj.setEwbDate((arr[1] != null) ? arr[1].toString() : null);
		obj.setSupplyType((arr[2] != null) ? arr[2].toString() : null);
		obj.setDocNo((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocDate((arr[4] != null) ? (arr[4].toString()) : null);
		obj.setOtherPartyGstin((arr[5] != null) ? arr[5].toString() : null);
		obj.setTransporterDetails((arr[6] != null) ? arr[6].toString() : null);
		obj.setFromGstinInfo((arr[7] != null) ? arr[7].toString() : null);
		obj.setToGstinInfo((arr[8] != null) ? arr[8].toString() : null);
		obj.setStatus((arr[9] != null) ? arr[9].toString() : null);
		obj.setNoOfItems((arr[10] != null) ? arr[10].toString() : null);

		obj.setMainHsnCode((arr[11] != null) ? arr[11].toString() : null);

		if (arr[11] != null) {
			String hsnCode = arr[11].toString();

			String hsnDesc = hsnCache.findHsnDescription(hsnCode);
			obj.setMainHsnDesc(hsnDesc != null ? hsnDesc : null);

		}
		// obj.setMainHsnDesc((arr[12] != null) ? arr[12].toString() : null);
		obj.setAssessableValue(
				(arr[13] != null) ? arr[13].toString() + "\t" : null);

		obj.setSgstValue((arr[14] != null) ? arr[14].toString() + "\t" : null);
		obj.setCgstValue((arr[15] != null) ? arr[15].toString() + "\t" : null);
		obj.setIgstValue((arr[16] != null) ? arr[16].toString() + "\t" : null);
		obj.setCessValue((arr[17] != null) ? arr[17].toString() + "\t" : null);
		obj.setCessNonAdvValue(
				(arr[18] != null) ? arr[18].toString() + "\t" : null);
		obj.setOtherValue((arr[19] != null) ? arr[19].toString() + "\t" : null);
		obj.setTotalInvoiceValue(
				(arr[20] != null) ? arr[20].toString() + "\t" : null);
		obj.setValidTillDate((arr[21] != null) ? arr[21].toString() : null);

		String modeOfGen = (arr[22] != null) ? arr[22].toString() : null;
		if ("EXC".equalsIgnoreCase(modeOfGen)) {
			obj.setModeOfGeneration("EXCEL");
			obj.setModeOfDataProcessing("Excel Upload");
		} else {
			obj.setModeOfGeneration(modeOfGen);
			obj.setModeOfDataProcessing(
					(arr[25] != null) ? arr[25].toString() : null);
		}
		obj.setCancelledBy((arr[23] != null) ? arr[23].toString() : null);
		obj.setCancelledDate((arr[24] != null) ? arr[24].toString() : null);

		return obj;

	}

	private static File createTempDir(String entityName, String toDate,
			String fromDate) throws IOException {

		String tempFolderPrefix = "EWB data" + "_" + entityName + "_" + fromDate
				+ "_" + toDate;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private static String getUniqueFileName(String entityName, String toDate,
			String fromDate) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		String fileNameWithFromDateToDate = "EWB data" + "_" + entityName + "_"
				+ fromDate + "_" + toDate;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithFromDateToDate);
		}

		return fileNameWithFromDateToDate;
	}

}
