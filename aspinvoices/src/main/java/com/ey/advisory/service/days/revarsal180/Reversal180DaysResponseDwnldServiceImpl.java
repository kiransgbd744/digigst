package com.ey.advisory.service.days.revarsal180;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipReportFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ReportConfigFactory;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.ReportTypeConstants;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("Reversal180DaysResponseDwnldServiceImpl")
public class Reversal180DaysResponseDwnldServiceImpl
		implements Reversal180DaysResponseDwnldService {

	@Autowired
	private FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	private ReportConfigFactory reportConfigFactory;

	@Autowired
	private CombineAndZipReportFiles combineAndZipReportFiles;

	private static final String ERR_HEADERS = "reversal.180.days.resp.error.headers";
	private static final String ERR_COLUMNS = "reversal.180.days.resp.error.column";

	private static final String PSD_HEADERS = "reversal.180.days.resp.psd.headers";
	private static final String PSD_COLUMNS = "reversal.180.days.resp.psd.column";

	@SuppressWarnings("unchecked")
	@Override
	public void generateReports(Long id) {
		List<Object[]> list = null;
		Writer writer = null;
		File tempDir = null;
		String columns = null;
		String headers = null;
		String reportTypeProc = null;
		String fileType = null;
		String zipType = null;
		List<Reversal180DaysResponseErrorDto> dtoList = null;
		String procName = "rev180RespRptDwnld";
		try {

			tempDir = createTempDir();

			if (LOGGER.isDebugEnabled()) {
				String msg = "Created temporary directory";
				LOGGER.debug(msg);
			}

			Optional<FileStatusDownloadReportEntity> optEntity = fileStatusDownloadReportRepo
					.findById(id);
			FileStatusDownloadReportEntity entity = null;

			if (optEntity.isPresent()) {
				entity = optEntity.get();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Fetched file entity based on report ID : %s",
							entity.getId().toString());

					LOGGER.debug(msg);
				}

			}

			String reportType = entity.getReportType();
			String status = entity.getStatus();

			if (ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)) {
				reportTypeProc = "ERROR";
				columns = ERR_COLUMNS;
				headers = ERR_HEADERS;
				fileType = "Error Records";
				zipType = ReportTypeConstants.REV_RESP180_ERROR;
			}

			if (ReportTypeConstants.REV_RESP180_PROCESSED
					.equalsIgnoreCase(reportType)) {

				reportTypeProc = "PROCESSED";
				columns = PSD_COLUMNS;
				headers = PSD_HEADERS;
				fileType = "Processed Records";
				zipType = ReportTypeConstants.REV_RESP180_PROCESSED;
			}

			if (ReportTypeConstants.TOTAL_RECORDS
					.equalsIgnoreCase(reportType)) {

				reportTypeProc = "TOTAL";
				columns = ERR_COLUMNS;
				headers = ERR_HEADERS;
				fileType = "Total Records";
				zipType = ReportTypeConstants.REV_RESP180_TOTAL;

			}

			StoredProcedureQuery reportDataProc = entityManager
					.createNamedStoredProcedureQuery(procName);

			reportDataProc.setParameter("P_FILE_ID", entity.getFileId());

			reportDataProc.setParameter("P_REPORT_TYPE", reportTypeProc);

			list = reportDataProc.getResultList();

			dtoList = list.stream().map(o -> convertObjToDto(o, reportType))
					.collect(Collectors.toList());

			if (dtoList.isEmpty()) {
				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.NO_DATA_FOUND, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				return;
			}

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyyMMddHHmmss");
			LocalDateTime istTimeStamp = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
			String timeMilli = dtf.format(istTimeStamp);
			String filename = "180 days Reversal_Response_" + fileType
					+ timeMilli;
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ filename + ".csv";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);

			StatefulBeanToCsv<Reversal180DaysResponseErrorDto> beanWriter = getRev180BeanWriter(
					headers, columns, writer);

			writetoCsv(dtoList, beanWriter);

			closeIOWriter(writer);

			String zipFileName = "";
			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combineAndZipReportFiles.zipfolder(tempDir,
						zipType, status, id);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}

				File zipFile = new File(tempDir, zipFileName);
/*
				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						GSTConstants.REVERSAL_180_DAYS_RESPONSE_FOLDER);*/
				
				Pair<String, String> uploadedDocName =  DocumentUtility
						.uploadFile(zipFile,GSTConstants.REVERSAL_180_DAYS_RESPONSE_FOLDER);

				if (LOGGER.isDebugEnabled()) {
					String msg = "Sucessfully uploaded zip file and updating the "
							+ "status as 'Report Generated'";
					LOGGER.debug(msg);
				}

				fileStatusDownloadReportRepo.updateStatus(id,
						ReportStatusConstants.REPORT_GENERATED, uploadedDocName
						.getValue0(), LocalDateTime.now(), uploadedDocName
						.getValue1());
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
			String msg = "Exception occued while generating Revarsal180DaysResponse csv file";
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

	private StatefulBeanToCsv<Reversal180DaysResponseErrorDto> getRev180BeanWriter(
			String headers, String columns, Writer writer) {
		try {
			if (!env.containsProperty(columns)
					|| !env.containsProperty(headers)) {
				String msg = "HeaderKey or ColumnMapping key Not found";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			ColumnPositionMappingStrategy<Reversal180DaysResponseErrorDto>
			mappingStrategy = new ColumnPositionMappingStrategy<>();
			mappingStrategy.setType(Reversal180DaysResponseErrorDto.class);
			mappingStrategy
					.setColumnMapping(env.getProperty(columns).split(","));

			StatefulBeanToCsvBuilder<Reversal180DaysResponseErrorDto> builder
			= new StatefulBeanToCsvBuilder<>(
					writer);
			StatefulBeanToCsv<Reversal180DaysResponseErrorDto> beanWriter = builder
					.withMappingStrategy(mappingStrategy)
					.withSeparator(com.opencsv.ICSVWriter.DEFAULT_SEPARATOR)
					.withLineEnd(com.opencsv.ICSVWriter.DEFAULT_LINE_END)
					.withEscapechar(
							com.opencsv.ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
					.build();

			writer.append(env.getProperty(headers));

			return beanWriter;

		} catch (Exception e) {

			String msg = "Exception while csv mapping";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage());

		}

	}

	private void writetoCsv(List<Reversal180DaysResponseErrorDto> dtoList,
			StatefulBeanToCsv<Reversal180DaysResponseErrorDto> beanWriter) {

		try {

			if (dtoList != null && !dtoList.isEmpty()) {
				beanWriter.write(dtoList);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Successfully writing into  count: %d ",
							dtoList.size());
					LOGGER.debug(msg);
				}
			}
		} catch (Exception ex) {
			String msg = "Exception occured while Writing to CSV";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("180DaysRespReports").toFile();
	}

	private Reversal180DaysResponseErrorDto convertObjToDto(Object[] arr,
			String reportType) {
		Reversal180DaysResponseErrorDto dto = new Reversal180DaysResponseErrorDto();

		dto.setUserResponse(convertToStringAndConcat(arr[0], true));
		dto.setUserResponseTaxPeriod(convertToStringAndConcat(arr[1], true));
		dto.setActionType(convertToStringAndConcat(arr[2], false));
		dto.setCustomerGSTIN(convertToStringAndConcat(arr[3], false));
		dto.setSupplierGSTIN(convertToStringAndConcat(arr[4], false));
		dto.setSupplierName(convertToStringAndConcat(arr[5], false));
		dto.setSupplierCode(convertToStringAndConcat(arr[6], false));
		dto.setDocumentType(convertToStringAndConcat(arr[7], false));
		dto.setDocumentNumber(convertToStringAndConcat(arr[8], true));
		dto.setDocumentDate(convertToStringAndConcat(arr[9], false));
		dto.setInvoiceValue(convertToStringAndConcat(arr[10], false));
		dto.setStatutoryDeductionsApplicable(
				convertToStringAndConcat(arr[11], false));
		dto.setStatutoryDeductionAmount(
				convertToStringAndConcat(arr[12], false));
		dto.setAnyOtherDeductionAmount(
				convertToStringAndConcat(arr[13], false));
		dto.setRemarksforDeductions(convertToStringAndConcat(arr[14], false));
		dto.setDueDateofPayment(convertToStringAndConcat(arr[15], false));
		dto.setPaymentReferenceStatus(convertToStringAndConcat(arr[16], false));
		dto.setPaymentReferenceNumber(convertToStringAndConcat(arr[17], false));
		dto.setPaymentReferenceDate(convertToStringAndConcat(arr[18], false));
		dto.setPaymentDescription(convertToStringAndConcat(arr[19], false));
		dto.setPaymentStatusFullorPartial(
				convertToStringAndConcat(arr[20], false));
		dto.setPaidAmounttoSupplier(convertToStringAndConcat(arr[21], false));
		dto.setCurrencyCode(convertToStringAndConcat(arr[22], false));
		dto.setExchangeRate(convertToStringAndConcat(arr[23], true));
		dto.setUnpaidAmounttoSupplier(convertToStringAndConcat(arr[24], false));
		dto.setPostingDate(convertToStringAndConcat(arr[25], false));
		dto.setPlantCode(convertToStringAndConcat(arr[26], false));
		dto.setProfitCentre(convertToStringAndConcat(arr[27], false));
		dto.setDivision(convertToStringAndConcat(arr[28], false));
		dto.setUserDefinedField1(convertToStringAndConcat(arr[29], false));
		dto.setUserDefinedField2(convertToStringAndConcat(arr[30], false));
		dto.setUserDefinedField3(convertToStringAndConcat(arr[31], false));
		dto.setDocDate180Days(convertToStringAndConcat(arr[32], false));
		dto.setReturnPeriodPR(convertToStringAndConcat(arr[33], true));
		dto.setReturnPeriod2APRResponse(
				convertToStringAndConcat(arr[34], true));
		dto.setIGSTTaxPaidPR(convertToStringAndConcat(arr[35], false));
		dto.setCGSTTaxPaidPR(convertToStringAndConcat(arr[36], false));
		dto.setSGSTTaxPaidPR(convertToStringAndConcat(arr[37], false));
		dto.setCessTaxPaidPR(convertToStringAndConcat(arr[38], false));
		dto.setAvailableIGSTPR(convertToStringAndConcat(arr[39], false));
		dto.setAvailableCGSTPR(convertToStringAndConcat(arr[40], false));
		dto.setAvailableSGSTPR(convertToStringAndConcat(arr[41], false));
		dto.setAvailableCessPR(convertToStringAndConcat(arr[42], false));
		dto.setITCReversalReclaimStatusDigiGST(
				convertToStringAndConcat(arr[43], false));
		dto.setITCReversalReturnPeriodDigiGSTIndicative(
				convertToStringAndConcat(arr[44], true));
		dto.setReversalofIGSTDigiGSTIndicative(
				convertToStringAndConcat(arr[45], false));
		dto.setReversalofCGSTDigiGSTIndicative(
				convertToStringAndConcat(arr[46], false));
		dto.setReversalofSGSTDigiGSTIndicative(
				convertToStringAndConcat(arr[47], false));
		dto.setReversalofCessDigiGSTIndicative(
				convertToStringAndConcat(arr[48], false));
		dto.setReClaimReturnPeriodDigiGSTIndicative(
				convertToStringAndConcat(arr[49], true));
		dto.setReClaimofIGSTIndicative(
				convertToStringAndConcat(arr[50], false));
		dto.setReClaimofCGSTIndicative(
				convertToStringAndConcat(arr[51], false));
		dto.setReClaimofSGSTIndicative(
				convertToStringAndConcat(arr[52], false));
		dto.setReClaimofCessIndicative(
				convertToStringAndConcat(arr[53], false));
		dto.setITCReversalComputeDateTime(
				convertToStringAndConcat(arr[54], true));
		dto.setITCReversalComputeRequestID(
				convertToStringAndConcat(arr[55], true));
		dto.setReconciliationDateAndTime(
				convertToStringAndConcat(arr[56], true));
		dto.setReconciliationRequestID(convertToStringAndConcat(arr[57], true));
		if (ReportTypeConstants.ERROR.equalsIgnoreCase(reportType)
				|| ReportTypeConstants.TOTAL_RECORDS
						.equalsIgnoreCase(reportType)) {
			dto.setErrorCode(convertToStringAndConcat(arr[58], false));
			dto.setErrorDescription(convertToStringAndConcat(arr[59], false));

		}
		return dto;
	}

	private void closeIOWriter(Writer writer) {
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

	private String convertToStringAndConcat(Object obj, boolean isConcat) {

		if (!isPresent(obj)) {
			return null;
		}
		if (isConcat) {
			return "'" + obj.toString().trim();
		}
		return obj.toString().trim();
	}

}
