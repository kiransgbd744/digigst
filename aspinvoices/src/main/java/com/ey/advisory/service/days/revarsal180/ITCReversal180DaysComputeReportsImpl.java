package com.ey.advisory.service.days.revarsal180;

import static com.ey.advisory.common.FormatValidationUtil.isPresent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Config180DaysComputeRepository;
import com.ey.advisory.app.data.repositories.client.ITCReversal180ComputeRptDownloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.ey.advisory.core.api.APIConstants;
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
@Component("ITCReversal180DaysComputeReportsImpl")
public class ITCReversal180DaysComputeReportsImpl
		implements ITCReversal180DaysComputeReports {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	@Autowired
	@Qualifier("Config180DaysComputeRepository")
	Config180DaysComputeRepository configRepo;
	
	@Autowired
	@Qualifier("ITCReversal180ComputeRptDownloadRepository")
	ITCReversal180ComputeRptDownloadRepository pathRepo;

	private static int CSV_BUFFER_SIZE = 8192;

	public void generateReport(Long computeId)  throws IOException {

		
		String fullPath = null;
		File tempDir = null;
		Long chunkSize = 10000L;
		Long startId = 0L;
		Long endId = 0L;
		int noOfChunk = 0;
		String msg = null;
		
		if (LOGGER.isDebugEnabled()) {
			 msg = String.format(
					"Get Reversal Report Details with computeId:'%s'", computeId);
			LOGGER.debug(msg);
		}
		//“Reversal Report (Payment info. available & Reversal Applicable + Not Applicable)”
		String[] reportType = { 
				"Payment Info Available - Reversal Applicable",
				"Payment Info Unavailable", 
				"Payment Info Available - Reversal and Not Applicable" };
		

			for (int i = 0; i < reportType.length; i++) {

			tempDir = createTempDir(computeId, reportType[i]);
			
			// Save or Update file name and download flag
			pathRepo.updateAndSave(computeId, reportType[i]);

			if (reportType[i].equalsIgnoreCase(
					"Payment Info Available - Reversal and Not Applicable")) {
				fullPath = tempDir.getAbsolutePath() + File.separator
						+ getUniqueFileName(
								"Reversal Report (Payment info. available & Reversal Applicable + Not Applicable)")
						+ ".csv";
			}else{
			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(reportType[i]) + ".csv";
			}

			ITCReversal180ComputeRptDownloadEntity chunkDetails = pathRepo
					.getChunckSizeforReportType(computeId, reportType[i]);

			noOfChunk = chunkDetails.getChunkNums() != null
					? chunkDetails.getChunkNums().intValue() : 0;

			if (noOfChunk != 0) {

				startId = chunkDetails.getStartChunk() != null
						? chunkDetails.getStartChunk() : 0L;

				endId = chunkDetails.getEndChunk() != null
						? chunkDetails.getEndChunk() : 0L;

				chunkSize = ((chunkSize > startId) && (chunkSize > endId))
						? endId : startId + 10000L;

				chunkSize = ((chunkSize > endId) && (chunkSize.equals(endId))) 
						? endId : startId + 10000L;

				msg = String.format(
						"Get Reversal Report Details with "
								+ "computeId:'%s' Before proc call startId %d, "
								+ "endId %d, chunkSize %d ,reportType %s ",
						computeId.toString(), startId, endId, chunkSize,
						reportType[i]);
				LOGGER.error(msg);

				try {

					if (LOGGER.isDebugEnabled()) {
						msg = String.format(
								"Compute ID is '%s',"
								+ " Created temporary directory to generate "
										+ "zip file: %s",
							computeId.toString(), tempDir.getAbsolutePath());
						LOGGER.debug(msg);
					}
				

				
				int j = 0;

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {

					String invoiceHeadersTemplate = commonUtility.getProp(
							"itc.reversal.180.days.report.headers");
					writer.append(invoiceHeadersTemplate);
					String[] columnMappings = commonUtility
							.getProp(
									"itc.reversal.180.days.report.column")
							.split(",");

					while (j < noOfChunk) {
						j++;

						StoredProcedureQuery storedProc = entityManager
								.createStoredProcedureQuery(
										"USP_180_DAYS_REVERSAL_REPORT");

						storedProc.registerStoredProcedureParameter(
								"P_COMPUTE_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_COMPUTE_ID",
								computeId);

						storedProc.registerStoredProcedureParameter(
								"P_REPORT_TYPE", String.class,
								ParameterMode.IN);

						storedProc.setParameter("P_REPORT_TYPE", reportType[i]);

						// startId
						storedProc.registerStoredProcedureParameter(
								"P_ST_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_ST_ID", startId);

						// EndId
						storedProc.registerStoredProcedureParameter(
								"P_END_ID", Long.class,
								ParameterMode.IN);

						storedProc.setParameter("P_END_ID",
								chunkSize);

						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"call stored proc with "
											+ "params {} Compute ID is '%s', "
											+ "reportType is %s, startId is %d,"
											+ " chunksize is %d ",
									computeId.toString(), reportType[i], startId,
									chunkSize);
							LOGGER.debug(msg);
						}

						startId = chunkSize + 1L;
						Long tempValue = (endId - startId) > 10000 ? 10000
								: (endId - startId);

						LOGGER.error("tempValue {} : ", tempValue);
						chunkSize = startId + tempValue;
						LOGGER.error("chunkSize after proc call {}: ", chunkSize);

						long dbLoadStTime = System.currentTimeMillis();

						@SuppressWarnings("unchecked")

						List<Object[]> records = storedProc.getResultList();
						long dbLoadEndTime = System.currentTimeMillis();
						long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Total Time taken to load the Data"
											+ " from DB is '%d' millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									dbLoadTimeDiff, reportType[i],
									records.size());
							LOGGER.debug(msg);
						}

						if (records != null && !records.isEmpty()) {

							List<ITCReversal180ReportDownloadDto> dataList 
							= new ArrayList<>();

							dataList = records.stream()
									.map(o -> convert(o)).collect(Collectors
											.toCollection(ArrayList::new));
							if (LOGGER.isDebugEnabled()) {
								msg = String.format(
									"Report Name and row count: '%s' - '%s'",
										reportType[i], dataList.size());
								LOGGER.debug(msg);
							}
							if (dataList != null
									&& !dataList.isEmpty()) {

								ColumnPositionMappingStrategy
								<ITCReversal180ReportDownloadDto>
								mappingStrategy = new 
								ColumnPositionMappingStrategy<>();
								mappingStrategy.setType(
										ITCReversal180ReportDownloadDto.class);
								mappingStrategy
										.setColumnMapping(columnMappings);
								StatefulBeanToCsvBuilder
								<ITCReversal180ReportDownloadDto> builder = 
								new StatefulBeanToCsvBuilder<>(
										writer);
								StatefulBeanToCsv
								<ITCReversal180ReportDownloadDto> beanWriter
								= builder
										.withMappingStrategy(mappingStrategy)
										.withSeparator(
												CSVWriter.DEFAULT_SEPARATOR)
										.withLineEnd(CSVWriter.DEFAULT_LINE_END)
										.withEscapechar(
											CSVWriter.DEFAULT_ESCAPE_CHARACTER)
										.build();
								long generationStTime = System
										.currentTimeMillis();
								beanWriter.write(dataList);
								long generationEndTime = System
										.currentTimeMillis();
								long generationTimeDiff = (generationEndTime
										- generationStTime);
								if (LOGGER.isDebugEnabled()) {
									msg = String.format(
											"Total Time taken to"
											+ " Generate the report is '%d' "
													+ "millisecs,"
											+ " Report Name and Data count:"
													+ " '%s' - '%s'",
											generationTimeDiff, reportType[i],
											records.size());
									LOGGER.debug(msg);
								}

							}
						}

					}

					flushWriter(writer);
				} catch (Exception ex) {
					LOGGER.error("Exception while executing proc "
							+ "Report Type :{} : {}", reportType[i], ex);
					throw new AppException(ex);
				}
				} 	
				catch (Exception ex) {
					LOGGER.error(
					"Exception while creating temp Directory for Compute id {}"
					+ " :: {} ",computeId, ex);
					throw new AppException(ex);
				}
				
			}
			// Zipping
			String zipFileName = null;
			if (tempDir.list().length > 0) {
				zipFileName = zipUtil.zipfolder("ITC_Reversal",computeId, 
						tempDir, reportType[i]);

				File zipFile = new File(tempDir, zipFileName);

				if (LOGGER.isDebugEnabled()) {
					msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, zipFileName);
					LOGGER.debug(msg);
				}

			
				//String uploadedDocName = "test";
//				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
//						APIConstants.ITC_REVESAL_180_FOLDER);
				
				Pair<String, String> uploadedDocName = DocumentUtility
						.uploadFile(zipFile, APIConstants.ITC_REVESAL_180_FOLDER);
				String uploadedFileName = uploadedDocName.getValue0();
				String docId = uploadedDocName.getValue1();
				
				pathRepo.updateReconFilePathDocId(uploadedFileName, computeId,
						reportType[i], docId);

				LOGGER.error("uploadedDocName {}: ",uploadedDocName);
			}
			
			// deleting dir
			deleteTemporaryDirectory(tempDir);
		}

		configRepo.updateComputeStatus("REPORT GENERATED", computeId);

		deleteTemporaryDirectory(tempDir);
	}
	
	private void flushWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
				if (LOGGER.isDebugEnabled()) {
					String msg = "Flushed writer " + "successfully";
					LOGGER.debug(msg);
				}
			} catch (IOException e) {
				String msg = "Exception while " + "closing the file writer";
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

	private ITCReversal180ReportDownloadDto convert(Object[] arr) {

		ITCReversal180ReportDownloadDto obj = new ITCReversal180ReportDownloadDto();

		obj.setUserResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setUserResponseTaxPeriod(
				(arr[1] != null) ? "'".concat(arr[1].toString()) : null);
		obj.setActionType((arr[2] != null) ? arr[2].toString() : null);
		obj.setCustomerGSTIN((arr[3] != null) ? arr[3].toString() : null);
		obj.setSupplierGSTIN((arr[4] != null) ? arr[4].toString() : null);
		obj.setSupplierName((arr[5] != null) ? arr[5].toString() : null);
		obj.setSupplierCode((arr[6] != null) ? arr[6].toString() : null);
		obj.setDocumentType((arr[7] != null) ? arr[7].toString() : null);
		obj.setDocumentNumber((arr[8] != null) ? arr[8].toString() : null);
		obj.setDocumentDate((arr[9] != null) ? arr[9].toString() : null);
		obj.setInvoiceValue((arr[10] != null) ? appendDecimalDigit(arr[10]) : null);
		obj.setStatutoryDeductionsApplicable(
				(arr[11] != null) ? arr[11].toString() : null);
		obj.setStatutoryDeductionAmount(
				(arr[12] != null) ? appendDecimalDigit(arr[12]) : null);
		obj.setAnyOtherDeductionAmount(
				(arr[13] != null) ? appendDecimalDigit(arr[13]) : null);
		obj.setRemarksforDeductions(
				(arr[14] != null) ? arr[14].toString() : null);
		obj.setDueDateofPayment((arr[15] != null) ? arr[15].toString() : null);
		obj.setPaymentReferenceStatus(
				(arr[16] != null) ? arr[16].toString() : null);
		obj.setPaymentReferenceNumber(
				(arr[17] != null) ? arr[17].toString() : null);
		obj.setPaymentReferenceDate(
				(arr[18] != null) ? arr[18].toString() : null);
		obj.setPaymentDescription(
				(arr[19] != null) ? arr[19].toString() : null);
		obj.setPaymentStatusFullorPartial(
				(arr[20] != null) ? arr[20].toString() : null);
		obj.setPaidAmounttoSupplier(
				(arr[21] != null) ? appendDecimalDigit(arr[21]) : null);
		obj.setCurrencyCode((arr[22] != null) ? arr[22].toString() : null);
		obj.setExchangeRate((arr[23] != null) ? (!arr[23].toString().isEmpty() 
				? (arr[23].toString()) : arr[23].toString()) : null);
		obj.setUnpaidAmounttoSupplier(
				(arr[24] != null) ? appendDecimalDigit(arr[24]) : null);
		obj.setPostingDate((arr[25] != null) ? arr[25].toString() : null);
		obj.setPlantCode((arr[26] != null) ? arr[26].toString() : null);
		obj.setProfitCentre((arr[27] != null) ? arr[27].toString() : null);
		obj.setDivision((arr[28] != null) ? arr[28].toString() : null);
		obj.setUserDefinedField1((arr[29] != null) ? arr[29].toString() : null);
		obj.setUserDefinedField2((arr[30] != null) ? arr[30].toString() : null);
		obj.setUserDefinedField3((arr[31] != null) ? arr[31].toString() : null);
		obj.setDocDate180Days((arr[32] != null) ? arr[32].toString() : null);
		obj.setReturnPeriodPR(
				(arr[33] != null) ? "'".concat(arr[33].toString()) : null);
		obj.setReturnPeriod2APRResponse(
				(arr[34] != null) ? "'".concat(arr[34].toString()) : null);
		obj.setIGSTTaxPaidPR(
				(arr[35] != null) ? appendDecimalDigit(arr[35]) : null);
		obj.setCGSTTaxPaidPR(
				(arr[36] != null) ? appendDecimalDigit(arr[36]) : null);
		obj.setSGSTTaxPaidPR(
				(arr[37] != null) ? appendDecimalDigit(arr[37]) : null);
		obj.setCessTaxPaidPR(
				(arr[38] != null) ? appendDecimalDigit(arr[38]) : null);
		obj.setAvailableIGSTPR((arr[39] != null) ? appendDecimalDigit(arr[39]) : null);
		obj.setAvailableCGSTPR((arr[40] != null) ? appendDecimalDigit(arr[40]) : null);
		obj.setAvailableSGSTPR((arr[41] != null) ? appendDecimalDigit(arr[41]) : null);
		obj.setAvailableCessPR((arr[42] != null) ? appendDecimalDigit(arr[42]) : null);
		obj.setITCReversalReclaimStatusDigiGST(
				(arr[43] != null) ? arr[43].toString() : null);
		obj.setITCReversalReturnPeriodDigiGSTIndicative(
				(arr[44] != null) ? "'".concat(arr[44].toString()) : null);
		obj.setReversalofIGSTDigiGSTIndicative(
				(arr[45] != null) ? appendDecimalDigit(arr[45]) : null);
		obj.setReversalofCGSTDigiGSTIndicative(
				(arr[46] != null) ? appendDecimalDigit(arr[46]) : null);
		obj.setReversalofSGSTDigiGSTIndicative(
				(arr[47] != null) ? appendDecimalDigit(arr[47]) : null);
		obj.setReversalofCessDigiGSTIndicative(
				(arr[48] != null) ? appendDecimalDigit(arr[48]) : null);
		obj.setReClaimReturnPeriodDigiGSTIndicative(
				(arr[49] != null) ? "'".concat(arr[49].toString()) : null);
		obj.setReClaimofIGSTIndicative(
				(arr[50] != null) ? appendDecimalDigit(arr[50]) : null);
		obj.setReClaimofCGSTIndicative(
				(arr[51] != null) ? appendDecimalDigit(arr[51]) : null);
		obj.setReClaimofSGSTIndicative(
				(arr[52] != null) ? appendDecimalDigit(arr[52]) : null);
		obj.setReClaimofCessIndicative(
				(arr[53] != null) ? appendDecimalDigit(arr[53]) : null);
		
		if(arr[54] != null){
			Timestamp date = (Timestamp) arr[54];
			LocalDateTime dt = date.toLocalDateTime();
			obj.setITCReversalComputeDateTime("'".concat(removingMilliSec(
					EYDateUtil.toISTDateTimeFromUTC(dt).toString())));
		}
		
		obj.setITCReversalComputeRequestID(
				(arr[55] != null) ? "'".concat(arr[55].toString()) : null);
		
		if(arr[56] != null){
			Timestamp date = (Timestamp) arr[56];
			LocalDateTime dt = date.toLocalDateTime();
			obj.setReconciliationDateAndTime("'".concat(removingMilliSec(
					EYDateUtil.toISTDateTimeFromUTC(dt).toString())));
		}
		
		obj.setReconciliationRequestID(
				(arr[57] != null) ? "'".concat(arr[57].toString()) : null);

		return obj;

	}

	private static File createTempDir(Long computeId, String reportType)
			throws IOException {

		String tempFolderPrefix = "ITCReversal180Days" + "_" + reportType + "_"
				+ computeId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
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

	private String removingMilliSec(String status) {

		String[] statusArray = status.split("T");
		String time = statusArray[1].substring(0, 8);

		status = statusArray[0] + " " + time;

		return status;
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
							val =  (s[0] + "." + s[1] + "0");
							LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method val : {} 1st if",
									val);
							return val;
						} else {
							LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method  val : {} 2nd if",
									val);
							return val;
						}
					} else {
						val = (val + ".00");
						LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method val : {} 3rd else",
								val);
						return val;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method {}",
					obj);
			return obj != null ? obj.toString() : null;
		}
		LOGGER.error("Reversal180DaoImpl AppendDecimalDigit method val : {} before final return ",
				obj.toString());
		return obj.toString();
	}
	
}