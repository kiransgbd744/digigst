package com.ey.advisory.app.services.search.filestatussearch;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
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
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.service.days.revarsal180.Revarsal180DaysUploadDto;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("Reversal180DaysFileDownloadServiceImpl")
public class Reversal180DaysFileDownloadServiceImpl
		implements Reversal180DaysFileDownloadService {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipReportFiles combineAndZipReportFiles;

	@Override
	public void generateInwardReports(Long id) {

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

			String reportType = entity.getReportType();
			String reportCateg = entity.getReportCateg();
			String dataType = entity.getDataType();
			String status = entity.getStatus();

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

			if (LOGGER.isDebugEnabled()) {
				String msg = "Mapped Reportconvertor based on report type and "
						+ "Report Category";
				LOGGER.debug(msg);
			}

			String fullPath = null;

			if ("vendor_payment".equalsIgnoreCase(dataType)) {
				if ("totalrecords".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "VendorPaymentReference_API_TotalRecords"
							+ "_" + timeMilli + ".csv";

					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = 
							getBeanWriterForTotalRecords(
							writer);
					WritetoCsvTotalRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				} else if ("processed".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "VendorPaymentReference_API_ProcessedRecords"
							+ "_" + timeMilli + ".csv";

					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter 
					= getBeanWriterForProcessed(
							writer);
					WritetoCsvProcessedRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				} else if ("error".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "VendorPaymentReference_API_ErrorRecords"
							+ timeMilli + ".csv";
					
					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter 
					= getBeanWriterForProcessed(
							writer);
					WritetoCsvProcessedRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				}
			} else {

				if ("totalrecords180days".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "180DaysPaymentReference" + "_" + "TotalRecords"
							+ "_" + timeMilli + ".csv";

					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter =
							getBeanWriterForTotalRecords(
							writer);
					WritetoCsvTotalRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				} else if ("processed180days".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "180DaysPaymentReference" + "_"
							+ "ProcessedRecords" + "_" + timeMilli + ".csv";

					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = 
							getBeanWriterForProcessed(
							writer);
					WritetoCsvProcessedRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				} else if ("error180days".equalsIgnoreCase(reportType)) {
					fullPath = tempDir.getAbsolutePath() + File.separator
							+ "180DaysPaymentReference" + "_" + "ErrorRecords"
							+ timeMilli + ".csv";
					writer = new BufferedWriter(new FileWriter(fullPath), 8192);
					StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = 
							getBeanWriterErrorRecords(
							writer);
					WritetoCsvErrorRecords(chunkCount, id, reportType,
							beanWriter, reportCateg);
				}
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
				if (reportType.equalsIgnoreCase("totalrecords180days")
						|| reportType.equalsIgnoreCase("totalrecords")) {
					reportType = "totalrecords";
				} else if (reportType.equalsIgnoreCase("processed180days")
						|| reportType.equalsIgnoreCase("processed")) {
					reportType = "processed";
				} else if (reportType.equalsIgnoreCase("error180days")
						|| reportType.equalsIgnoreCase("error")) {
					reportType = "error";
				}
				if ("vendor_payment".equalsIgnoreCase(dataType)) {
					zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
							"VendorPaymentReference_API_" + reportType, status,
							id);
				} else {
					zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
							"180DaysPaymentReference" + "_" + reportType,
							status, id);
				}

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
		} catch (

		Exception e) {

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occued while while generating Inward csv file";
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

	private void WritetoCsvTotalRecords(Integer chunkNo, Long id,
			String reportType,
			StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<Revarsal180DaysUploadDto> responseFromDao = null;
		try {
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(
								"180daysPaymentReferenceDataTotal");

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VAL", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream().map(o -> convert(o))
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

	private Revarsal180DaysUploadDto convert(Object[] arr) {

		Revarsal180DaysUploadDto dto = new Revarsal180DaysUploadDto();

		dto.setErrorCode((String) arr[0]);
		dto.setErrorDesc((String) arr[1]);
		dto.setActionType((String) arr[2]);
		dto.setCustomerGSTIN((String) arr[3]);
		dto.setSupplierGSTIN((String) arr[4]);
		dto.setSupplierName((String) arr[5]);
		dto.setSupplierCode((String) arr[6]);
		dto.setDocumentType((String) arr[7]);
		dto.setDocumentNumber((String) arr[8]);
		dto.setDocumentDate(
				arr[9] != null ? localDateStart(arr[9].toString()) : null);
		dto.setInvoiceValue(
				arr[10] != null ? appendDecimalDigit(arr[10]) : null);
		dto.setStatutoryDeductionsApplicable((String) arr[11]);
		dto.setStatutoryDeductionAmount(
				arr[12] != null ? appendDecimalDigit(arr[12]) : null);
		dto.setAnyOtherDeductionAmount(
				arr[13] != null ? appendDecimalDigit(arr[13]) : null);
		dto.setRemarksforDeductions((String) arr[14]);
		dto.setDueDateofPayment(
				arr[15] != null ? localDateStart(arr[15].toString()) : null);
		dto.setPaymentReferenceNumber((String) arr[16]);
		dto.setPaymentReferenceDate(
				arr[17] != null ? localDateStart(arr[17].toString()) : null);
		dto.setPaymentDescription((String) arr[18]);
		dto.setPaymentStatus((String) arr[19]);
		dto.setPaidAmounttoSupplier(
				arr[20] != null ? appendDecimalDigit(arr[20]) : null);
		dto.setCurrencyCode((String) arr[21]);
		dto.setExchangeRate((String) arr[22]);
		dto.setUnpaidAmounttoSupplier(
				arr[23] != null ? appendDecimalDigit(arr[23]) : null);
		dto.setPostingDate(
				arr[24] != null ? localDateStart(arr[24].toString()) : null);
		dto.setPlantCode((String) arr[25]);
		dto.setProfitCentre((String) arr[26]);
		dto.setDivision((String) arr[27]);
		dto.setUserDefinedField1((String) arr[28]);
		dto.setUserDefinedField2((String) arr[29]);
		dto.setUserDefinedField3((String) arr[30]);

		return dto;
	}

	private String localDateStart(String datetime) {
		String[] datetimeArray = datetime != null ? datetime.split("T") : null;
		String dateStart = datetimeArray != null ? datetimeArray[0] : null;
		return dateStart;
	}

	private String appendDecimalDigit(Object obj) {

		try {
			if (isPresent(obj)) {
				if (NumberFomatUtil.isNumber(obj)) {

					BigDecimal b = new BigDecimal(obj.toString());
					String val = b.setScale(2, BigDecimal.ROUND_HALF_UP)
							.toPlainString();

					String[] s = val.split("\\.");
					if (s.length == 2) {
						if (s[1].length() == 1) {
							val = "'" + (s[0] + "." + s[1] + "0");
							return val;
						} else {
							val = "'" + val;
							return val;
						}
					} else {
						val = "'" + (val + ".00");
						return val;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method {}",
					obj);
			return obj != null ? "'".concat(obj.toString()) : null;
		}
		return obj.toString();
	}

	private void WritetoCsvProcessedRecords(Integer chunkNo, Long id,
			String reportType,
			StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<Revarsal180DaysUploadDto> responseFromDao = null;

		try {
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(
								"180daysPaymentReferenceDataProcessed");

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VAL", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream().map(o -> convertPSD(o))
							.collect(Collectors.toList());

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

	private Revarsal180DaysUploadDto convertPSD(Object[] arr) {

		Revarsal180DaysUploadDto dto = new Revarsal180DaysUploadDto();

		dto.setActionType((String) arr[2]);
		dto.setCustomerGSTIN((String) arr[3]);
		dto.setSupplierGSTIN((String) arr[4]);
		dto.setSupplierName((String) arr[5]);
		dto.setSupplierCode((String) arr[6]);
		dto.setDocumentType((String) arr[7]);
		dto.setDocumentNumber((String) arr[8]);
		dto.setDocumentDate(
				arr[9] != null ? localDateStart(arr[9].toString()) : null);
		dto.setInvoiceValue(
				arr[10] != null ? appendDecimalDigit(arr[10]) : null);
		dto.setStatutoryDeductionsApplicable((String) arr[11]);
		dto.setStatutoryDeductionAmount(
				arr[12] != null ? appendDecimalDigit(arr[12]) : null);
		dto.setAnyOtherDeductionAmount(
				arr[13] != null ? appendDecimalDigit(arr[13]) : null);
		dto.setRemarksforDeductions((String) arr[14]);
		dto.setDueDateofPayment(
				arr[15] != null ? localDateStart(arr[15].toString()) : null);
		dto.setPaymentReferenceNumber((String) arr[16]);
		dto.setPaymentReferenceDate(
				arr[17] != null ? localDateStart(arr[17].toString()) : null);
		dto.setPaymentDescription((String) arr[18]);
		dto.setPaymentStatus((String) arr[19]);
		dto.setPaidAmounttoSupplier(
				arr[20] != null ? appendDecimalDigit(arr[20]) : null);
		dto.setCurrencyCode((String) arr[21]);
		dto.setExchangeRate((String) arr[22]);
		dto.setUnpaidAmounttoSupplier(
				arr[23] != null ? appendDecimalDigit(arr[23]) : null);
		dto.setPostingDate(
				arr[24] != null ? localDateStart(arr[24].toString()) : null);
		dto.setPlantCode((String) arr[25]);
		dto.setProfitCentre((String) arr[26]);
		dto.setDivision((String) arr[27]);
		dto.setUserDefinedField1((String) arr[28]);
		dto.setUserDefinedField2((String) arr[29]);
		dto.setUserDefinedField3((String) arr[30]);
		return dto;

	}

	private void WritetoCsvErrorRecords(Integer chunkNo, Long id,
			String reportType,
			StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter,
			String reportCateg) {

		List<Object[]> list = null;
		List<Revarsal180DaysUploadDto> responseFromDao = null;

		try {
			for (int i = 1; i <= chunkNo; i++) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Inside for loop with count: %d",
							i);
					LOGGER.debug(msg);
				}

				StoredProcedureQuery reportDataProc = entityManager
						.createNamedStoredProcedureQuery(
								"180daysPaymentReferenceDataError");

				reportDataProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

				reportDataProc.setParameter("P_CHUNK_VAL", i);

				list = reportDataProc.getResultList();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Executed Stored proc to get ChunckData and "
									+ "got resultset of size: %d",
							list.size());
					LOGGER.debug(msg);
				}

				if (list.size() > 0) {

					responseFromDao = list.stream().map(o -> convert(o))
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

		StoredProcedureQuery counterProc = entityManager
				.createNamedStoredProcedureQuery(
						"180daysPaymentReferenceChunkCount");

		counterProc.setParameter("P_REPORT_DOWNLOAD_ID", id);

		Integer response = (Integer) counterProc.getSingleResult();

		return response;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private StatefulBeanToCsv<Revarsal180DaysUploadDto> getBeanWriterErrorRecords(
			Writer writer) throws Exception {

		String invoiceHeadersTemplate = commonUtility
				.getProp("reversal.180.days.error.report.header");

		String[] columnMappings = commonUtility
				.getProp("reversal.180.days.error.report.column").split(",");

		ColumnPositionMappingStrategy<Revarsal180DaysUploadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Revarsal180DaysUploadDto.class);
		mappingStrategy.setColumnMapping(columnMappings);

		StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(invoiceHeadersTemplate);
		return beanWriter;
	}

	private StatefulBeanToCsv<Revarsal180DaysUploadDto> getBeanWriterForTotalRecords(
			Writer writer) throws Exception {

		String invoiceHeadersTemplate = commonUtility
				.getProp("reversal.180.days.total.report.header");
		String[] columnMappings = commonUtility
				.getProp("reversal.180.days.total.report.column").split(",");

		ColumnPositionMappingStrategy<Revarsal180DaysUploadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Revarsal180DaysUploadDto.class);
		mappingStrategy.setColumnMapping(columnMappings);

		StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(invoiceHeadersTemplate);
		return beanWriter;
	}

	private StatefulBeanToCsv<Revarsal180DaysUploadDto> getBeanWriterForProcessed(
			Writer writer) throws Exception {

		String invoiceHeadersTemplate = commonUtility
				.getProp("reversal.180.days.report.header");

		String[] columnMappings = commonUtility
				.getProp("reversal.180.days.report.column").split(",");

		ColumnPositionMappingStrategy<Revarsal180DaysUploadDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Revarsal180DaysUploadDto.class);
		mappingStrategy.setColumnMapping(columnMappings);

		StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();

		writer.append(invoiceHeadersTemplate);
		return beanWriter;
	}

}
