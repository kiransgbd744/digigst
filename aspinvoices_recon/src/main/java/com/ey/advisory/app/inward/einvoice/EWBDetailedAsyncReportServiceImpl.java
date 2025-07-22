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
 * @author Ravindra V S
 *
 */

@Component("EWBDetailedAsyncReportServiceImpl")
@Slf4j
public class EWBDetailedAsyncReportServiceImpl
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
					"Get EWB Detailed Report Download " + "with id:'%s'", id);
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
						.getProp("ewb.detailed.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("ewb.detailed.report.download.mapping")
						.split(",");

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcEwb = entityManager
							.createStoredProcedureQuery(
									"USP_EWB_DETAIL_RPRT_DISP_CHUNK");

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

						List<EWBDetailedReportDto> reconDataList = records
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

							ColumnPositionMappingStrategy<EWBDetailedReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
							mappingStrategy.setType(EWBDetailedReportDto.class);
							mappingStrategy.setColumnMapping(columnMappings);
							StatefulBeanToCsvBuilder<EWBDetailedReportDto> builder = new StatefulBeanToCsvBuilder<>(
									writer);
							StatefulBeanToCsv<EWBDetailedReportDto> beanWriter = builder
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

	private EWBDetailedReportDto convert(Object[] arr) {

		EWBDetailedReportDto obj = new EWBDetailedReportDto();

		obj.setEwbNo((arr[0] != null) ? arr[0].toString() + "\t" : null);
		obj.setEwbDate((arr[1] != null) ? arr[1].toString() : null);
		obj.setStatus((arr[2] != null) ? arr[2].toString() : null);
		obj.setValidUpto((arr[3] != null) ? arr[3].toString() : null);
		obj.setSupplyType((arr[4] != null) ? arr[4].toString() : null);
		obj.setTransactionType((arr[5] != null) ? arr[5].toString() : null);
		obj.setDocType((arr[6] != null) ? arr[6].toString() : null);
		obj.setSubSupplyType((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocNo((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocDate((arr[9] != null) ? arr[9].toString() : null);
		obj.setFromGstin((arr[10] != null) ? arr[10].toString() : null);
		obj.setFromTrdName((arr[11] != null) ? arr[11].toString() : null);
		obj.setFromAddr1((arr[12] != null) ? arr[12].toString() : null);
		obj.setFromAddr2((arr[13] != null) ? arr[13].toString() : null);
		obj.setFromPlace((arr[14] != null) ? arr[14].toString() : null);
		obj.setFromPincode((arr[15] != null) ? arr[15].toString() : null);
		obj.setFromStateCode((arr[16] != null) ? arr[16].toString() : null);
		obj.setToGstin((arr[17] != null) ? arr[17].toString() : null);
		obj.setToTrdName((arr[18] != null) ? arr[18].toString() : null);
		obj.setToAddr1((arr[19] != null) ? arr[19].toString() : null);
		obj.setToAddr2((arr[20] != null) ? arr[20].toString() : null);
		obj.setToPlace((arr[21] != null) ? arr[21].toString() : null);
		obj.setToPincode((arr[22] != null) ? arr[22].toString() : null);
		obj.setToStateCode((arr[23] != null) ? arr[23].toString() : null);
		obj.setDispatcherGstin((arr[24] != null) ? arr[24].toString() : null);
		obj.setDispatcherTrdName((arr[25] != null) ? arr[25].toString() : null);
		obj.setDispatcherAddr1((arr[26] != null) ? arr[26].toString() : null);
		obj.setDispatcherAddr2((arr[27] != null) ? arr[27].toString() : null);
		obj.setDispatcherPlace((arr[28] != null) ? arr[28].toString() : null);
		obj.setDispatcherPincode((arr[29] != null) ? arr[29].toString() : null);
		obj.setDispatcherStateCode(
				(arr[30] != null) ? arr[30].toString() : null);
		obj.setShipToGstin((arr[31] != null) ? arr[31].toString() : null);
		obj.setShipToTrdName((arr[32] != null) ? arr[32].toString() : null);
		obj.setShipToAddr1((arr[33] != null) ? arr[33].toString() : null);
		obj.setShipToAddr2((arr[34] != null) ? arr[34].toString() : null);
		obj.setShipToPlace((arr[35] != null) ? arr[35].toString() : null);
		obj.setShipToPincode((arr[36] != null) ? arr[36].toString() : null);
		obj.setShipToStateCode((arr[37] != null) ? arr[37].toString() : null);
		obj.setItemNo((arr[38] != null) ? arr[38].toString() : null);
		obj.setProductName((arr[39] != null) ? arr[39].toString() : null);
		obj.setProductDesc((arr[40] != null) ? arr[40].toString() : null);
		obj.setHsnCode((arr[41] != null) ? arr[41].toString() : null);
		obj.setQtyUnit((arr[42] != null) ? arr[42].toString() : null);
		obj.setQuantity((arr[43] != null) ? arr[43].toString() : null);
		obj.setTaxableAmount((arr[44] != null) ? arr[44].toString() : null);
		obj.setIgstRate((arr[45] != null) ? arr[45].toString() : null);
		obj.setCgstRate((arr[46] != null) ? arr[46].toString() : null);
		obj.setSgstRate((arr[47] != null) ? arr[47].toString() : null);
		obj.setCessRate((arr[48] != null) ? arr[48].toString() : null);
		obj.setCessNonAdvol((arr[49] != null) ? arr[49].toString() : null);
		obj.setOtherValue((arr[50] != null) ? arr[50].toString() : null);
		obj.setIgstValue((arr[51] != null) ? arr[51].toString() : null);
		obj.setCgstValue((arr[52] != null) ? arr[52].toString() : null);
		obj.setSgstValue((arr[53] != null) ? arr[53].toString() : null);
		obj.setCessValue((arr[54] != null) ? arr[54].toString() : null);
		obj.setTotInvValue((arr[55] != null) ? arr[55].toString() : null);
		obj.setTransMode((arr[56] != null) ? arr[56].toString() : null);
		obj.setTransporterId((arr[57] != null) ? arr[57].toString() : null);
		obj.setTransporterName((arr[58] != null) ? arr[58].toString() : null);
		obj.setTransDocNo((arr[59] != null) ? arr[59].toString() : null);
		obj.setTransDocDate((arr[60] != null) ? arr[60].toString() : null);
		obj.setActualDist((arr[61] != null) ? arr[61].toString() : null);
		obj.setVehicleNo((arr[62] != null) ? arr[62].toString() : null);
		obj.setVehicleType((arr[63] != null) ? arr[63].toString() : null);
		obj.setUserGstin((arr[64] != null) ? arr[64].toString() : null);
		obj.setExtendedTimes((arr[65] != null) ? arr[65].toString() : null);
		obj.setRejectStatus((arr[66] != null) ? arr[66].toString() : null);
		obj.setGenMode((arr[67] != null) ? arr[67].toString() : null);
		obj.setModeOfDataProcessing(
				(arr[68] != null) ? arr[68].toString() : null);

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
		String fileNameWithFromDateToDate = "EWB Detailed Report" + "_"
				+ entityName + "_" + fromDate + "_" + toDate;
		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithFromDateToDate);
		}

		return fileNameWithFromDateToDate;
	}

}
