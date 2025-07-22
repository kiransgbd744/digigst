/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Reversal180DaysErrorRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysPSDRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysStageRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Reversal180DaysDownloadService")
public class Reversal180DaysDownloadService {

	private static int CSV_BUFFER_SIZE = 8192;

	@Autowired
	@Qualifier("Reversal180DaysPSDRepository")
	private Reversal180DaysPSDRepository psdRepo;

	@Autowired
	@Qualifier("Reversal180DaysErrorRepository")
	private Reversal180DaysErrorRepository errRepo;

	@Autowired
	@Qualifier("Reversal180DaysStageRepository")
	private Reversal180DaysStageRepository stgRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Reversal180DaoImpl")
	private Reversal180DaoImpl reversal180DaoImpl;

	@Autowired
	CombineAndZipXlsxFiles combineAndZipXlsxFiles;
	private static final DateTimeFormatter dtf = DateTimeFormatter
			.ofPattern("yyyyMMddHHmmss");

	public Pair<String, File> generateErrorReport(Integer fileId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		String errFileName = null;
		File fPathFile = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Error Report Details with fileId:'%d'", fileId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(fileId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"fileId  is '%d',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						fileId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					fileId, ex);
		}

		List<Reversal180DaysErrorEntity> entityList = errRepo
				.findByFileId(fileId.longValue());

		List<Reversal180DaysPSDEntity> psdErrorEntityList = psdRepo
				.findByFileIdAndIsPsdFalseAndIsActiveTrue(fileId.longValue());

		List<Revarsal180DaysUploadDto> psdErrorRecords = psdErrorEntityList
				.stream().map(o -> convertToPsdErrorList(o))
				.collect(Collectors.toList());

		List<Revarsal180DaysUploadDto> records = entityList.stream()
				.map(o -> convertToErrorList(o)).collect(Collectors.toList());

		records.addAll(psdErrorRecords);
		
		records.sort(
				Comparator.comparing(Revarsal180DaysUploadDto::getDocumentNumber));
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime istTimeStamp = EYDateUtil.toISTDateTimeFromUTC(now);
		String timeMilli = dtf.format(istTimeStamp);

		if (records != null && !records.isEmpty()) {

			errFileName = "180 days Reversal_Payment reference_Error Records"
					+ timeMilli;

			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("reversal.180.days.error.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("reversal.180.days.error.report.column")
						.split(",");

				ColumnPositionMappingStrategy<Revarsal180DaysUploadDto>

				mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Revarsal180DaysUploadDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = 
						new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(records);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Total Time taken to "
									+ "Generate the report is '%d' millisecs, "
									+ "Data count: , '%s'",
							generationTimeDiff, records.size());
					LOGGER.debug(msg);
				}
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
						if (LOGGER.isDebugEnabled()) {
							String msg = "Flushed writer " + "successfully";
							LOGGER.debug(msg);
						}
					} catch (IOException e) {
						String msg = "Exception while "
								+ "closing the file writer";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for ", ex);

			}
			fPathFile = new File(fullPath);
		}

		return new Pair<>(errFileName, fPathFile);
	}

	private Revarsal180DaysUploadDto convertToPsdErrorList(
			Reversal180DaysPSDEntity entity) {

		Revarsal180DaysUploadDto dto = new Revarsal180DaysUploadDto();

		dto.setActionType(entity.getActionType());
		dto.setCustomerGSTIN(entity.getCustomerGSTIN());
		dto.setSupplierGSTIN(entity.getSupplierGSTIN());
		dto.setSupplierName(entity.getSupplierName());
		dto.setSupplierCode(entity.getSupplierCode());
		dto.setDocumentType(entity.getDocumentType());
		dto.setDocumentNumber(entity.getDocumentNumber() != null
				&& !entity.getDocumentNumber().isEmpty()
						? "'".concat(entity.getDocumentNumber()) : null);
		String docDate = entity.getDocumentDate() != null
				? localDateStart(entity.getDocumentDate().toString()) : null;
		dto.setDocumentDate(docDate);
		String invoiceValue = convertBigDecimalToString(
				entity.getInvoiceValue());
		dto.setInvoiceValue(invoiceValue != null && !invoiceValue.isEmpty()
				? appendDecimalDigit(invoiceValue) : null);
		dto.setStatutoryDeductionsApplicable(
				entity.getStatutoryDeductionsApplicable());
		String statutoryDeductionAmount = convertBigDecimalToString(
				entity.getStatutoryDeductionAmount());
		dto.setStatutoryDeductionAmount(statutoryDeductionAmount != null
				&& !statutoryDeductionAmount.isEmpty()
						? appendDecimalDigit(statutoryDeductionAmount) : null);
		String anyOtherDeductionAmount = convertBigDecimalToString(
				entity.getAnyOtherDeductionAmount());
		dto.setAnyOtherDeductionAmount(anyOtherDeductionAmount != null
				&& !anyOtherDeductionAmount.isEmpty()
						? appendDecimalDigit(anyOtherDeductionAmount) : null);
		dto.setRemarksforDeductions(entity.getRemarksforDeductions());
		String dueDate = entity.getDueDateofPayment() != null
				? localDateStart(entity.getDueDateofPayment().toString())
				: null;
		dto.setDueDateofPayment(dueDate);
		dto.setPaymentReferenceNumber(entity.getPaymentReferenceNumber());
		String payRefDate = entity.getPaymentReferenceDate() != null
				? localDateStart(entity.getPaymentReferenceDate().toString())
				: null;
		dto.setPaymentReferenceDate(payRefDate);
		dto.setPaymentDescription(entity.getPaymentDescription());
		dto.setPaymentStatus(entity.getPaymentStatus());
		dto.setPaidAmounttoSupplier(
				entity.getPaidAmounttoSupplier() != null
						? appendDecimalDigit(
								entity.getPaidAmounttoSupplier().toString())
						: null);
		dto.setCurrencyCode(entity.getCurrencyCode());
		dto.setExchangeRate(entity.getExchangeRate() != null
				&& !entity.getExchangeRate().isEmpty()
						? "'".concat(entity.getExchangeRate()) : null);
		dto.setUnpaidAmounttoSupplier(
				entity.getUnpaidAmounttoSupplier() != null
						? appendDecimalDigit(
								entity.getUnpaidAmounttoSupplier().toString())
						: null);
		String postingDate = entity.getPostingDate() != null
				? localDateStart(entity.getPostingDate().toString()) : null;
		dto.setPostingDate(postingDate);
		dto.setPlantCode(entity.getPlantCode());
		dto.setProfitCentre(entity.getProfitCentre());
		dto.setDivision(entity.getDivision());
		dto.setUserDefinedField1(entity.getUserDefinedField1());
		dto.setUserDefinedField2(entity.getUserDefinedField2());
		dto.setUserDefinedField3(entity.getUserDefinedField3());
		dto.setErrorCode(entity.getErrorCode());
		dto.setErrorDesc(entity.getErrorDesc());

		return dto;

	}

	private String convertBigDecimalToString(BigDecimal amt) {

		if (amt != null) {
			return amt.toString();
		}
		return null;
	}

	private Revarsal180DaysUploadDto convertToErrorList(
			Reversal180DaysErrorEntity entity) {

		Revarsal180DaysUploadDto dto = new Revarsal180DaysUploadDto();

		dto.setActionType(entity.getActionType());
		dto.setCustomerGSTIN(entity.getCustomerGSTIN());
		dto.setSupplierGSTIN(entity.getSupplierGSTIN());
		dto.setSupplierName(entity.getSupplierName());
		dto.setSupplierCode(entity.getSupplierCode());
		dto.setDocumentType(entity.getDocumentType());
		dto.setDocumentNumber(entity.getDocumentNumber() != null
				&& !entity.getDocumentNumber().isEmpty()
						? "'".concat(entity.getDocumentNumber()) : null);
		String docDate = entity.getDocumentDate() != null
				? localDateStart(entity.getDocumentDate().toString()) : null;
		dto.setDocumentDate(docDate);
		dto.setInvoiceValue(entity.getInvoiceValue() != null
				&& !entity.getInvoiceValue().isEmpty()
						? appendDecimalDigit(entity.getInvoiceValue()) : null);
		dto.setStatutoryDeductionsApplicable(
				entity.getStatutoryDeductionsApplicable());
		dto.setStatutoryDeductionAmount(
				entity.getStatutoryDeductionAmount() != null
						&& !entity.getStatutoryDeductionAmount().isEmpty()
								? appendDecimalDigit(
										entity.getStatutoryDeductionAmount())
								: null);
		dto.setAnyOtherDeductionAmount(
				entity.getAnyOtherDeductionAmount() != null
						&& !entity.getAnyOtherDeductionAmount().isEmpty()
								? appendDecimalDigit(
										entity.getAnyOtherDeductionAmount())
								: null);
		dto.setRemarksforDeductions(entity.getRemarksforDeductions());
		String dueDate = entity.getDueDateofPayment() != null
				? localDateStart(entity.getDueDateofPayment().toString())
				: null;
		dto.setDueDateofPayment(dueDate);
		dto.setPaymentReferenceNumber(entity.getPaymentReferenceNumber());
		String payRefDate = entity.getPaymentReferenceDate() != null
				? localDateStart(entity.getPaymentReferenceDate().toString())
				: null;
		dto.setPaymentReferenceDate(payRefDate);
		dto.setPaymentDescription(entity.getPaymentDescription());
		dto.setPaymentStatus(entity.getPaymentStatus());
		dto.setPaidAmounttoSupplier(entity.getPaidAmounttoSupplier() != null
				&& !entity.getPaidAmounttoSupplier().isEmpty()
						? appendDecimalDigit(entity.getPaidAmounttoSupplier())
						: null);
		dto.setCurrencyCode(entity.getCurrencyCode());
		dto.setExchangeRate(entity.getExchangeRate() != null
				&& !entity.getExchangeRate().isEmpty()
						? "'".concat(entity.getExchangeRate()) : null);
		dto.setUnpaidAmounttoSupplier(entity.getUnpaidAmounttoSupplier() != null
				&& !entity.getUnpaidAmounttoSupplier().isEmpty()
						? appendDecimalDigit(entity.getUnpaidAmounttoSupplier())
						: null);
		String postingDate = entity.getPostingDate() != null
				? localDateStart(entity.getPostingDate().toString()) : null;
		dto.setPostingDate(postingDate);
		dto.setPlantCode(entity.getPlantCode());
		dto.setProfitCentre(entity.getProfitCentre());
		dto.setDivision(entity.getDivision());
		dto.setUserDefinedField1(entity.getUserDefinedField1());
		dto.setUserDefinedField2(entity.getUserDefinedField2());
		dto.setUserDefinedField3(entity.getUserDefinedField3());
		dto.setErrorCode(entity.getErrorCode());
		dto.setErrorDesc(entity.getErrorDesc());

		return dto;

	}

	public Pair<String, File> generatePSDReport(Integer fileId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		String errFileName = null;
		File fPathFile = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Get PSD Report Details with fileId:'%d'", fileId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(fileId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"fileId  is '%d',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						fileId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					fileId, ex);
		}

		List<Reversal180DaysPSDEntity> entityList = psdRepo
				.findByFileIdAndIsPsdTrueAndIsActiveTrue(fileId.longValue());

		List<Revarsal180DaysUploadDto> records = entityList.stream()
				.map(o -> convertPSD(o)).collect(Collectors.toList());
		
		records.sort(
				Comparator.comparing(Revarsal180DaysUploadDto::getDocumentNumber));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime istTimeStamp = EYDateUtil.toISTDateTimeFromUTC(now);
		String timeMilli = dtf.format(istTimeStamp);

		if (records != null && !records.isEmpty()) {

			errFileName = "180 days Reversal_Payment reference_Processed Records"
					+ timeMilli;

			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("reversal.180.days.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("reversal.180.days.report.column").split(",");

				ColumnPositionMappingStrategy<Revarsal180DaysUploadDto>

				mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Revarsal180DaysUploadDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = 
						new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(records);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Total Time taken to "
									+ "Generate the report is '%d' millisecs, "
									+ "Data count: , '%s'",
							generationTimeDiff, records.size());
					LOGGER.debug(msg);
				}
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
						if (LOGGER.isDebugEnabled()) {
							String msg = "Flushed writer " + "successfully";
							LOGGER.debug(msg);
						}
					} catch (IOException e) {
						String msg = "Exception while "
								+ "closing the file writer";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for ", ex);

			}
			fPathFile = new File(fullPath);
		}

		return new Pair<>(errFileName, fPathFile);
	}

	private static File createTempDir(Integer fileId) throws IOException {

		String tempFolderPrefix = "180DaysReversal" + "_" + fileId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private Revarsal180DaysUploadDto convertPSD(
			Reversal180DaysPSDEntity entity) {

		Revarsal180DaysUploadDto dto = new Revarsal180DaysUploadDto();

		dto.setActionType(entity.getActionType());
		dto.setCustomerGSTIN(entity.getCustomerGSTIN());
		dto.setSupplierGSTIN(entity.getSupplierGSTIN());
		dto.setSupplierName(entity.getSupplierName());
		dto.setSupplierCode(entity.getSupplierCode());
		dto.setDocumentType(entity.getDocumentType());
		dto.setDocumentNumber(entity.getDocumentNumber() != null
				&& !entity.getDocumentNumber().isEmpty()
						? "'".concat(entity.getDocumentNumber()) : null);

		String docDate = entity.getDocumentDate() != null
				? localDateStart(entity.getDocumentDate().toString()) : null;
		dto.setDocumentDate(docDate);
		String totalInvAmt = entity.getInvoiceValue() != null
				? entity.getInvoiceValue().toString() : null;
		dto.setInvoiceValue(
				totalInvAmt != null ? "'".concat(totalInvAmt) : null);
		dto.setStatutoryDeductionsApplicable(
				entity.getStatutoryDeductionsApplicable());

		String statutoryDeductionAmount = entity
				.getStatutoryDeductionAmount() != null
						? entity.getStatutoryDeductionAmount().toString()
						: null;
		dto.setStatutoryDeductionAmount(statutoryDeductionAmount != null
				? "'".concat(statutoryDeductionAmount) : null);

		String anyOtherDeductionAmount = entity
				.getAnyOtherDeductionAmount() != null
						? entity.getAnyOtherDeductionAmount().toString() : null;
		dto.setAnyOtherDeductionAmount(anyOtherDeductionAmount != null
				? "'".concat(anyOtherDeductionAmount) : null);

		dto.setRemarksforDeductions(entity.getRemarksforDeductions());

		String dueDate = entity.getDueDateofPayment() != null
				? localDateStart(entity.getDueDateofPayment().toString())
				: null;
		dto.setDueDateofPayment(dueDate);
		dto.setPaymentReferenceNumber(entity.getPaymentReferenceNumber());

		String payRefDate = entity.getPaymentReferenceDate() != null
				? localDateStart(entity.getPaymentReferenceDate().toString())
				: null;
		dto.setPaymentReferenceDate(payRefDate);
		dto.setPaymentDescription(entity.getPaymentDescription());

		dto.setPaymentStatus(entity.getPaymentStatus());
		String paidAmt = entity.getPaidAmounttoSupplier() != null
				? entity.getPaidAmounttoSupplier().toString() : null;
		dto.setPaidAmounttoSupplier(
				paidAmt != null ? "'".concat(paidAmt) : null);
		dto.setCurrencyCode(entity.getCurrencyCode());
		dto.setExchangeRate(entity.getExchangeRate() != null
				? "'".concat(entity.getExchangeRate()) : null);
		String balAmt = entity.getUnpaidAmounttoSupplier() != null
				? entity.getUnpaidAmounttoSupplier().toString() : null;
		dto.setUnpaidAmounttoSupplier(
				balAmt != null ? "'".concat(balAmt) : null);

		String postingDate = entity.getPostingDate() != null
				? localDateStart(entity.getPostingDate().toString()) : null;
		dto.setPostingDate(postingDate);
		dto.setPlantCode(entity.getPlantCode());
		dto.setProfitCentre(entity.getProfitCentre());
		dto.setDivision(entity.getDivision());
		dto.setUserDefinedField1(entity.getUserDefinedField1());
		dto.setUserDefinedField2(entity.getUserDefinedField2());
		dto.setUserDefinedField3(entity.getUserDefinedField3());
		return dto;

	}

	public Pair<String, File> generateTotalReport(Integer fileId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		String errFileName = null;
		File fPathFile = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Get PSD Report Details with fileId:'%d'", fileId);
			LOGGER.debug(msg);
		}

		try {
			tempDir = createTempDir(fileId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"fileId  is '%d',"
								+ " Created temporary directory to generate "
								+ "zip file: %s",
						fileId.toString(), tempDir.getAbsolutePath());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			LOGGER.error(
					"Exception while creating temp Directory for config id {}",
					fileId, ex);
		}

		List<Revarsal180DaysUploadDto> records = reversal180DaoImpl
				.getTotalRecords(fileId);

		records.sort(
				Comparator.comparing(Revarsal180DaysUploadDto::getDocumentNumber));
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime istTimeStamp = EYDateUtil.toISTDateTimeFromUTC(now);
		String timeMilli = dtf.format(istTimeStamp);

		if (records != null && !records.isEmpty()) {

			errFileName = "180 days Reversal_Payment reference_Total Records"
					+ timeMilli;

			fullPath = tempDir.getAbsolutePath() + File.separator + errFileName
					+ ".csv";

			try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE)) {

				String invoiceHeadersTemplate = commonUtility
						.getProp("reversal.180.days.total.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("reversal.180.days.total.report.column")
						.split(",");

				ColumnPositionMappingStrategy<Revarsal180DaysUploadDto>

				mappingStrategy = new ColumnPositionMappingStrategy<>();
				mappingStrategy.setType(Revarsal180DaysUploadDto.class);
				mappingStrategy.setColumnMapping(columnMappings);
				StatefulBeanToCsvBuilder<Revarsal180DaysUploadDto> builder = 
						new StatefulBeanToCsvBuilder<>(
						writer);
				StatefulBeanToCsv<Revarsal180DaysUploadDto> beanWriter = builder
						.withMappingStrategy(mappingStrategy)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
						.withLineEnd(CSVWriter.DEFAULT_LINE_END)
						.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
						.build();
				long generationStTime = System.currentTimeMillis();
				beanWriter.write(records);
				long generationEndTime = System.currentTimeMillis();
				long generationTimeDiff = (generationEndTime
						- generationStTime);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Total Time taken to "
									+ "Generate the report is '%d' millisecs, "
									+ "Data count: , '%s'",
							generationTimeDiff, records.size());
					LOGGER.debug(msg);
				}
				if (writer != null) {
					try {
						writer.flush();
						writer.close();
						if (LOGGER.isDebugEnabled()) {
							String msg = "Flushed writer " + "successfully";
							LOGGER.debug(msg);
						}
					} catch (IOException e) {
						String msg = "Exception while "
								+ "closing the file writer";
						LOGGER.error(msg);
						throw new AppException(msg, e);
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while executing the query for ", ex);

			}
			fPathFile = new File(fullPath);
		}

		return new Pair<>(errFileName, fPathFile);
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

}
