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

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Component("EinvoiceNestedAsyncReportServiceImpl")
@Slf4j
public class EinvoiceNestedAsyncReportServiceImpl
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
					"Get Einvoice Nested Report Download " + "with id:'%s'",
					id);
			LOGGER.debug(msg);
		}

		Optional<FileStatusDownloadReportEntity> opt = downloadRepository
				.findById(id);

		FileStatusDownloadReportEntity entity = opt.get();

		String reportType = entity.getReportType();

		String entityName = entityInfo
				.findEntityNameByEntityId(entity.getEntityId());

		try {
			tempDir = createTempDir();

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(id) + ".csv";

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
							"USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT");

			storedProc.registerStoredProcedureParameter("P_REQUEST_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REQUEST_ID", id);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT: '%s'",
						id.toString());
				LOGGER.debug(msg);
			}

			Integer chunks = (Integer) storedProc.getSingleResult();

			noOfChunk = chunks;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT: id '%d', "
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
						.getProp("einvoice.nested.report.download.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("einvoice.nested.report.download.mapping")
						.split(",");

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProcNestedReport = entityManager
							.createStoredProcedureQuery(
									"USP_DISPLAY_CHUNK_IRN_NESTED_DETAIL_REPORT");

					storedProcNestedReport.registerStoredProcedureParameter(
							"P_REQUEST_ID", Long.class, ParameterMode.IN);

					storedProcNestedReport.setParameter("P_REQUEST_ID", id);

					storedProcNestedReport.registerStoredProcedureParameter(
							"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
					storedProcNestedReport.setParameter("P_CHUNK_NUM", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("call stored proc with "
								+ "params {} Config ID is '%s', "
								+ " chunkNo is %d ", id.toString(), j);
						LOGGER.debug(msg);
					}

					long dbLoadStTime = System.currentTimeMillis();

					List<Object[]> records = storedProcNestedReport
							.getResultList();

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

						List<EinvoiceNestedReportDto> einvoiceDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, einvoiceDataList.size());
							LOGGER.debug(msg);
						}
						if (einvoiceDataList != null
								&& !einvoiceDataList.isEmpty()) {

							ColumnPositionMappingStrategy<EinvoiceNestedReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
							mappingStrategy
									.setType(EinvoiceNestedReportDto.class);
							mappingStrategy.setColumnMapping(columnMappings);
							StatefulBeanToCsvBuilder<EinvoiceNestedReportDto> builder = new StatefulBeanToCsvBuilder<>(
									writer);
							StatefulBeanToCsv<EinvoiceNestedReportDto> beanWriter = builder
									.withMappingStrategy(mappingStrategy)
									.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
									.withLineEnd(CSVWriter.DEFAULT_LINE_END)
									.withEscapechar(
											CSVWriter.DEFAULT_ESCAPE_CHARACTER)
									.build();
							long generationStTime = System.currentTimeMillis();
							beanWriter.write(einvoiceDataList);
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
						"Einvoice Data", entityName, "Nested");
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

	private EinvoiceNestedReportDto convert(Object[] arr) {

		EinvoiceNestedReportDto obj = new EinvoiceNestedReportDto();

		obj.setIrnGenerationPeriod((arr[1] != null) ? arr[1].toString() : null);
		obj.setIrnNumber((arr[3] != null) ? arr[3].toString() : null);
		obj.setIrnDate((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		if (arr[2] != null && ("ACT".equalsIgnoreCase(arr[2].toString()))
				|| ("CNL".equalsIgnoreCase(arr[2].toString()))) {
			if ("ACT".equalsIgnoreCase(arr[2].toString())) {
				obj.setIrnStatus("Active");
			} else {
				obj.setIrnStatus("Cancelled");
			}
		}
		obj.setSupplierGSTIN((arr[5] != null) ? arr[5].toString() : null);
		obj.setDocumentNumber((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setDocumentType((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocumentDate((arr[8] != null) ? arr[8].toString() : null);
		obj.setCustomerGSTIN((arr[9] != null) ? arr[9].toString() : null);
		obj.setPreceedingInvoiceNumber(
				(arr[10] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[10].toString()) : null);
		obj.setPreceedingInvoiceDate(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setOtherReference((arr[12] != null) ? arr[12].toString() : null);
		obj.setReceiptAdviceReference(
				(arr[13] != null) ? arr[13].toString() : null);
		obj.setReceiptAdviceDate((arr[14] != null) ? arr[14].toString() : null);
		obj.setTenderReference((arr[15] != null) ? arr[15].toString() : null);
		obj.setContractReference((arr[16] != null) ? arr[16].toString() : null);
		obj.setExternalReference((arr[17] != null) ? arr[17].toString() : null);
		obj.setProjectReference((arr[18] != null) ? arr[18].toString() : null);
		obj.setCustomerPOReferenceNumber(
				(arr[19] != null) ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[19].toString()) : null);
		obj.setCustomerPOReferenceDate(
				(arr[20] != null) ? arr[20].toString() : null);
		obj.setSupportingDocURL((arr[21] != null) ? arr[21].toString() : null);
		obj.setSupportingDocument(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setAdditionalInformation(
				(arr[23] != null) ? arr[23].toString() : null);
		obj.setAttributeName((arr[24] != null) ? arr[24].toString() : null);
		obj.setAttributeValue((arr[25] != null) ? arr[25].toString() : null);
		return obj;

	}

	private static String getUniqueFileName(Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		String fileNameWithFromDateToDate = "Inward E-invoice_Preceding Doc & Other details_"
				+ id;

		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithFromDateToDate);
		}

		return fileNameWithFromDateToDate;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

}
