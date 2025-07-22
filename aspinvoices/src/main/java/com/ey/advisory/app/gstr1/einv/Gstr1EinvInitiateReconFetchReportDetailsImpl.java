package com.ey.advisory.app.gstr1.einv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EinvReconReportDownloadEntity;
import com.ey.advisory.app.data.entities.client.Gstr1EInvReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconReportDownloadRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ReconStatusConstants;
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
@Component("Gstr1EinvInitiateReconFetchReportDetailsImpl")
public class Gstr1EinvInitiateReconFetchReportDetailsImpl
		implements Gstr1EinvInitiateReconFetchReportDetails {

	@Autowired
	@Qualifier("EinvReconReportDownloadRepository")
	EinvReconReportDownloadRepository einvchunkrepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository configRepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconConfigRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	private static int CSV_BUFFER_SIZE = 8192;

	@Override
	public void getReconReportData(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		Long chunkSize = 10000L;
		Long startId = 0L;
		Long endId = 0L;
		int noOfChunk = 0;
		String msg = null;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Gstr1 vs Einv Initiate Report Details with configId:'%s'",
					configId.toString());
			LOGGER.debug(msg);
		}

		String reportType = "Gstr1_Einv_Records_Report";

		Optional<Gstr1EInvReconConfigEntity> configData = gstr1InvReconConfigRepo
				.findById(configId);
		String taxPeriod = configData.get().getTaxPeriod();
		String updatedReportName = "Consolidated DigiGST vs EInvoice " + "Data"
				+ "_" + taxPeriod + "_" + configId.toString();

		tempDir = createTempDir(configId, reportType);

		fullPath = tempDir.getAbsolutePath() + File.separator
				+ updatedReportName + ".csv";
		try (Writer writer = new BufferedWriter(new FileWriter(fullPath),
				CSV_BUFFER_SIZE)) {

			EinvReconReportDownloadEntity chunkDetails = einvchunkrepo
					.findByConfigIdAndReportType(configId, reportType);

			noOfChunk = chunkDetails.getChunkNums() != null
					? chunkDetails.getChunkNums().intValue() : 0;

			if (noOfChunk != 0) {

				startId = chunkDetails.getStartChunk() != null
						? chunkDetails.getStartChunk() : 0L;

				endId = chunkDetails.getEndChunk() != null
						? chunkDetails.getEndChunk() : 0L;

				chunkSize = ((chunkSize > startId) && (chunkSize > endId))
						? endId : startId + 10000L;

				chunkSize = (chunkSize > endId) ? endId : startId + 10000L;

				String logMsg = String.format(
						"Get Gstr1 vs Einv Recon Report Details with "
								+ "configId:'%s' Before proc call startId %d, "
								+ "endId %d, chunkSize %d,"
								+ " USP_EINV_RECON_RPT_MASTER ",
						configId.toString(), startId, endId, chunkSize);
				LOGGER.error(logMsg);

				if (LOGGER.isDebugEnabled()) {
					String message = String.format(
							"Config ID is '%s',"
									+ " Created temporary directory to generate "
									+ "zip file: %s",
							configId.toString(), tempDir.getAbsolutePath());
					LOGGER.debug(message);
				}

				einvchunkrepo.updateAndSave(configId, reportType);

				int j = 0;

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr1.einvoice.recon.report.header");
				writer.append(invoiceHeadersTemplate);
				String[] columnMappings = commonUtility
						.getProp("gstr1.einvoice.recon.report.column")
						.split(",");

				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(
									"USP_EINV_RECON_RPT_MASTER");

					storedProc.registerStoredProcedureParameter(
							"RECON_CONFIG_ID", Long.class, ParameterMode.IN);

					storedProc.setParameter("RECON_CONFIG_ID", configId);

					if (LOGGER.isDebugEnabled()) {
						String mesg = String.format(
								"Stored Procedure for GSTR1 Recon Reports: '%s'",
								reportType);
						LOGGER.debug(mesg);
					}

					// startId
					storedProc.registerStoredProcedureParameter("P_START_ID",
							Long.class, ParameterMode.IN);

					storedProc.setParameter("P_START_ID", startId);

					// EndId
					storedProc.registerStoredProcedureParameter("P_END_ID",
							Long.class, ParameterMode.IN);

					storedProc.setParameter("P_END_ID", chunkSize);

					if (LOGGER.isDebugEnabled()) {
						String mesg = String.format(
								"call stored proc USP_EINV_RECON_RPT_MASTER with "
										+ "params {} Config ID is '%s', "
										+ "reportType is %s, startId is %d, "
										+ "chunksize is %d",
								configId.toString(),
								"Gstr1_Einv_Records_Report", startId,
								chunkSize);
						LOGGER.debug(mesg);
					}

					startId = chunkSize + 1L;
					Long tempValue = (endId - startId) > 10000 ? 10000
							: (endId - startId);

					LOGGER.error("tempValue", tempValue);
					chunkSize = startId + tempValue;
					LOGGER.error("chunkSize after proc call", chunkSize);

					long dbLoadStTime = System.currentTimeMillis();

					@SuppressWarnings("unchecked")

					List<Object[]> records = storedProc.getResultList();
					long dbLoadEndTime = System.currentTimeMillis();
					long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
					if (LOGGER.isDebugEnabled()) {
						String mesg = String.format(
								"Total Time taken to load the Data"
										+ " from DB is '%d' millisecs,"
										+ " Report Name and Data count:"
										+ " '%s' - '%s'",
								dbLoadTimeDiff, reportType, records.size());
						LOGGER.debug(mesg);
					}

					if (records != null && !records.isEmpty()) {

						List<Gstr1EinvInitiateReconReportDto> reconDataList = new ArrayList<>();

						reconDataList = records.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							String mesg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, reconDataList.size());
							LOGGER.debug(mesg);
						}
						if (reconDataList != null && !reconDataList.isEmpty()) {

							ColumnPositionMappingStrategy<Gstr1EinvInitiateReconReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
							mappingStrategy.setType(
									Gstr1EinvInitiateReconReportDto.class);
							mappingStrategy.setColumnMapping(columnMappings);
							StatefulBeanToCsvBuilder<Gstr1EinvInitiateReconReportDto> builder = new StatefulBeanToCsvBuilder<>(
									writer);
							StatefulBeanToCsv<Gstr1EinvInitiateReconReportDto> beanWriter = builder
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
								String message = String.format(
										"Total Time taken to"
												+ " Generate the report is '%d' "
												+ "millisecs,"
												+ " Report Name and Data count:"
												+ " '%s' - '%s'",
										generationTimeDiff, reportType,
										records.size());
								LOGGER.debug(message);
							}

						}
					}

				}

				flushWriter(writer);

				// Zipping
				String zipFileName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					if (LOGGER.isDebugEnabled()) {
						String mesg = String.format(
								"Before uploading "
										+ "Zip Inner Mandatory files, tempDir "
										+ "Name %s and ZipFileName %s ",
								tempDir, zipFileName);
						LOGGER.debug(mesg);
					}

					Pair<String, String> uploadedZipName = DocumentUtility
							.uploadFile(zipFile, "Gstr1ReconReports");
					String uploadedDocumentName = uploadedZipName.getValue0();
					String docId = uploadedZipName.getValue1();
					
					einvchunkrepo.updateReconFilePath(uploadedDocumentName,
							configId, reportType,docId);

					gstr1InvReconConfigRepo
							.updateReconConfigStatusAndReportName(
									ReconStatusConstants.REPORT_GENERATED, null,
									EYDateUtil.toUTCDateTimeFromLocal(
											LocalDateTime.now()),
									configId);

				}
			} else {
				String message = String.format(
						"chunk size is zero for " + "Report %s ", reportType);
				LOGGER.debug(message);
				updateReconConfigStatus(ReconStatusConstants.NO_DATA_FOUND,
						configId);
			}
		} catch (Exception ex) {
			String message = String.format(
					"Exception while executing the query for " + "Report %s ",
					reportType);
			LOGGER.error(message, ex);
			updateReconConfigStatus(
					ReconStatusConstants.REPORT_GENERATION_FAILED, configId);
			ex.printStackTrace();
			throw new AppException(ex);
		} finally {
			// deleting dir
			deleteTemporaryDirectory(tempDir);
		}
	}

	private void updateReconConfigStatus(String status, Long configId) {
		configRepo.updateReconConfigStatusAndReportName(status, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
				configId);
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

	private static File createTempDir(Long configId, String reportType)
			throws IOException {

		String tempFolderPrefix = "Consolidated DigiGST vs EInvoice Data" + "_"
				+ configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
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

	private Gstr1EinvInitiateReconReportDto convert(Object[] arr) {

		Gstr1EinvInitiateReconReportDto dto = new Gstr1EinvInitiateReconReportDto();

		dto.setResponse(arr[0] != null ? arr[0].toString() : null);
		dto.setPreviousResponse(arr[1] != null ? arr[1].toString() : null);
		dto.setRemarks(arr[2] != null ? arr[2].toString() : null);
		dto.setMismatchReason(arr[3] != null ? arr[3].toString() : null);
		dto.setScoreOutof11(arr[4] != null ? arr[4].toString() : null);
		dto.setReportType(arr[5] != null ? arr[5].toString() : null);
		dto.setPreviousReport(arr[6] != null ? arr[6].toString() : null);

		if (arr[7] != null && (!arr[7].toString().trim().isEmpty())) {
			dto.setTaxPeriodGSTN(
					(arr[7] != null) ? "'".concat(arr[7].toString()) : null);
		}
		if (arr[8] != null && (!arr[8].toString().trim().isEmpty())) {
			dto.setTaxPeriodDigiGST(
					(arr[8] != null) ? "'".concat(arr[8].toString()) : null);
		}

		dto.setCalenderMonthDigiGST(
				arr[9] != null ? "'".concat(arr[9].toString()) : null);
		dto.setSupplierGSTINGSTN(arr[10] != null ? arr[10].toString() : null);
		dto.setSupplierGSTINDigiGST(
				arr[11] != null ? arr[11].toString() : null);
		dto.setRecipientGSTINGSTN(arr[12] != null ? arr[12].toString() : null);
		dto.setRecipientGSTINDigiGST(
				arr[13] != null ? arr[13].toString() : null);
		dto.setRecipientNameGSTN(arr[14] != null ? arr[14].toString() : null);
		dto.setRecipientNameDigiGST(
				arr[15] != null ? arr[15].toString() : null);
		dto.setDocTypeGSTN(arr[16] != null ? arr[16].toString() : null);
		dto.setDocTypeDigiGST(arr[17] != null ? arr[17].toString() : null);
		dto.setSupplyTypeGSTN(arr[18] != null ? arr[18].toString() : null);
		dto.setSupplyTypeDigiGST(arr[19] != null ? arr[19].toString() : null);
		dto.setDocumentNumberGSTN(arr[20] != null ? "'".concat(arr[20].toString()) : null);
		dto.setDocumentNumberDigiGST(
				arr[21] != null ? "'".concat(arr[21].toString()) : null);
		dto.setDocumentDateGSTN(arr[22] != null ? arr[22].toString() : null);
		dto.setDocumentDateDigiGST(arr[23] != null ? arr[23].toString() : null);
		dto.setTaxableValueGSTN(arr[24] != null ? arr[24].toString() : null);
		dto.setTaxableValueDigiGST(arr[25] != null ? arr[25].toString() : null);
		dto.setIgstGSTN(arr[26] != null ? arr[26].toString() : null);
		dto.setIgstDigiGST(arr[27] != null ? arr[27].toString() : null);
		dto.setCgstGSTN(arr[28] != null ? arr[28].toString() : null);
		dto.setCgstDigiGST(arr[29] != null ? arr[29].toString() : null);
		dto.setSgstGSTN(arr[30] != null ? arr[30].toString() : null);
		dto.setSgstDigiGST(arr[31] != null ? arr[31].toString() : null);
		dto.setCessGSTN(arr[32] != null ? arr[32].toString() : null);
		dto.setCessDigiGST(arr[33] != null ? arr[33].toString() : null);
		dto.setTotalTaxGSTN(arr[34] != null ? arr[34].toString() : null);
		dto.setTotalTaxDigiGST(arr[35] != null ? arr[35].toString() : null);
		dto.setInvoiceValueGSTN(arr[36] != null ? arr[36].toString() : null);
		dto.setInvoiceValueDigiGST(arr[37] != null ? arr[37].toString() : null);
		dto.setPosGSTN(arr[38] != null ? "'".concat(arr[38].toString()) : null);
		dto.setPosDigiGST(
				arr[39] != null ? "'".concat(arr[39].toString()) : null);
		dto.setReverseChargeFlagGSTN(
				arr[40] != null ? arr[40].toString() : null);
		dto.setReverseChargeFlagDigiGST(
				arr[41] != null ? arr[41].toString() : null);
		dto.setEcomGSTINGSTN(arr[42] != null ? arr[42].toString() : null);
		dto.setEcomGSTINDigiGST(arr[43] != null ? arr[43].toString() : null);
		dto.setPortCodeGSTN(arr[44] != null ? arr[44].toString() : null);
		dto.setPortCodeDigiGST(arr[45] != null ? arr[45].toString() : null);
		dto.setShippingBillNumberGSTN(
				arr[46] != null ? arr[46].toString() : null);
		dto.setShippingBillNumberDigiGST(
				arr[47] != null ? arr[47].toString() : null);
		dto.setShippingBillDateGSTN(
				arr[48] != null ? arr[48].toString() : null);
		dto.setShippingBillDateDigiGST(
				arr[49] != null ? arr[49].toString() : null);
		dto.setSourceTypeGSTN(arr[50] != null ? arr[50].toString() : null);
		dto.setIrnGSTN(arr[51] != null ? arr[51].toString() : null);
		dto.setIrnDigiGST(arr[52] != null ? arr[52].toString() : null);
		dto.setIrnGenDateGSTN(arr[53] != null ? arr[53].toString() : null);
		dto.setIrnGenDateDigiGST(arr[54] != null ? arr[54].toString() : null);
		dto.setEInvoiceStatus(arr[55] != null ? arr[55].toString() : null);
		dto.setAutoDraftstatus(arr[56] != null ? arr[56].toString() : null);
		dto.setAutoDraftedDate(arr[57] != null ? arr[57].toString() : null);
		dto.setErrorCode(arr[58] != null ? arr[58].toString() : null);
		dto.setErrorMessage(arr[59] != null ? arr[59].toString() : null);
		dto.setTableTypeGSTN(arr[60] != null ? arr[60].toString() : null);
		dto.setTableTypeDigiGST(arr[61] != null ? arr[61].toString() : null);
		dto.setCustomerType(arr[62] != null ? arr[62].toString() : null);
		dto.setCustomerCode(arr[63] != null ? arr[63].toString() : null);
		dto.setAccountingVoucherNumber(
				arr[64] != null ? arr[64].toString() : null);
		dto.setAccountingVoucherDate(
				arr[65] != null ? arr[65].toString() : null);
		dto.setCompanyCode(arr[66] != null ? arr[66].toString() : null);
		dto.setRecordStatusDigiGST(arr[67] != null ? arr[67].toString() : null);
		dto.setEInvoiceGetCallDate(arr[68] != null ? arr[68].toString() : null);
		dto.setEInvoiceGetCallTime(arr[69] != null ? arr[69].toString() : null);
		dto.setReconID(arr[70] != null ? "'".concat(arr[70].toString()) : null);
		dto.setReconDate(arr[71] != null ? arr[71].toString() : null);
		dto.setReconTime(
				arr[72] != null ? "'".concat(arr[72].toString()) : null);
		dto.setDocHeaderId(arr[73] != null ? arr[73].toString() : null);
		dto.setGetCallId(arr[74] != null ? arr[74].toString() : null);
		dto.setDocKeyDigiGST(arr[75] != null ? arr[75].toString() : null);
		dto.setDocKeyEINV(arr[76] != null ? arr[76].toString() : null);
		dto.setReportCategory(arr[77] != null ? arr[77].toString() : null);

		dto.setSubCategory(arr[78] != null ? arr[78].toString() : null);
		dto.setReasonForMismatch(arr[79] != null ? arr[79].toString() : null);
		//new columns added
		dto.setPlantCode(arr[80] != null ? arr[80].toString() : null);
		dto.setDivision(arr[81] != null ? arr[81].toString() : null);
		dto.setSubDivision(arr[82] != null ? arr[82].toString() : null);
		dto.setLocation(arr[83] != null ? arr[83].toString() : null);
		dto.setProfitCentre1(arr[84] != null ? arr[84].toString() : null);
		dto.setProfitCentre2(arr[85] != null ? arr[85].toString() : null);
		dto.setProfitCentre3(arr[86] != null ? arr[86].toString() : null);

		return dto;
	}

	
}
