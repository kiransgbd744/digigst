package com.ey.advisory.app.inward.einvoice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr2ReconResultReportCommonService;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
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
 * @author vishal.verma
 *
 */

@Component("EinvoiceDetailedLineItemAndNestedReportSFTPService")
@Slf4j
public class EinvoiceDetailedLineItemAndNestedReportSFTPService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("InwardEInvoiceERPRequestRepository")
	InwardEInvoiceERPRequestRepository revIntCheckRepo;

	@Autowired
	@Qualifier("EinvoiceDetailLineItemReportCommonServiceImpl")
	private Gstr2ReconResultReportCommonService commonService;

	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	private static final String INWARD_EINVOICE_SFTP_DESTINATION = "ey.internal.get.inward.einvoice.sftp.destination";
	
	private static final String CONF_KEY = "einvoice.detailed.lineitem.report.chunk.size";
	private static final String CONF_CATEG = "DETAILED_LINEITEM_REPORTS";

	private static int CSV_BUFFER_SIZE = 8192;
	
	

	public int generateAndPushReports(Long batchId) {

		File tempDir = null;
		String msg = null;
		List<String> filePaths = new ArrayList<>();

		Map<String, Config> configMap = configManager.getConfigs("SFTP",
				"ey.internal.get.inward.einvoice.sftp.destination");

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Get Einvoice sftp with BatchId:'%d'",
					batchId);
			LOGGER.debug(msg);
		}

		InwardEInvoiceERPRequestEntity entity = revIntCheckRepo
				.findByBatchId(batchId);

		try {
			tempDir = createTempDir();

			createLineItemReport(batchId, tempDir, entity, filePaths);

			createPreceedingReport(batchId, tempDir, entity, filePaths);

			// comressing folder

			String compressedFileName = getUniqueFileName(entity.getGstin(),
					entity.getSupplyType(), "_Inward EInvoice Reports_")
					+ ".zip";

			compressor.compressFiles(tempDir.getAbsolutePath(),
					compressedFileName, filePaths);

			String zipPath = tempDir.getAbsolutePath() + File.separator
					+ compressedFileName;

			// File zipFile = new File(zipPath);

			boolean isReverseInt = sftpService.uploadFiles(
					Arrays.asList(zipPath),
					configMap.get(INWARD_EINVOICE_SFTP_DESTINATION).getValue());
			if (isReverseInt) {
				return 200;
			} else {
				return 0;
			}

		} catch (Exception e) {
			String msz = String.format(
					"Exception occured while creating csv file for batchId %d",
					batchId);
			LOGGER.error(msz, e);

			revIntCheckRepo.updateStatusByBatchId(batchId,
					ReconStatusConstants.FAILED, false);
			throw new AppException(e);

		} finally {
			deleteTemporaryDirectory(tempDir);
		}
		
	}

	private void createPreceedingReport(Long batchId, File tempDir,
			InwardEInvoiceERPRequestEntity entity, List<String> filePaths) {

		String fullPath;
		Integer noOfChunk;
		String msg;
		Writer writer;
		fullPath = tempDir.getAbsolutePath()
				+ File.separator + getUniqueFileName(entity.getGstin(),
						entity.getSupplyType(), "_Preceding Details Report_")
				+ ".csv";
		filePaths.add(fullPath);

		Integer chunkSize = getChunkSize();

		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Config ID is '%s',"
							+ " Created temporary directory to generate "
							+ "zip file: %s",
					batchId.toString(), tempDir.getAbsolutePath());
			LOGGER.debug(msg);
		}

		// chunk value

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP");

		storedProc.registerStoredProcedureParameter("P_BATCH_ID", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_BATCH_ID", batchId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);
		
		storedProc.registerStoredProcedureParameter("P_SOURCE_TYPE",
				String.class, ParameterMode.IN);

		storedProc.setParameter("P_SOURCE_TYPE", "SFTP");
		
		

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Executing chunking proc"
					+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP: '%s'",
					batchId.toString());
			LOGGER.debug(msg);
		}

		Integer chunks = (Integer) storedProc.getSingleResult();

		noOfChunk = chunks;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Chunking proc Executed"
					+ " USP_INSERT_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP: id '%d', "
					+ "noOfChunk %d ", batchId, noOfChunk);
			LOGGER.debug(msg);
		}

		noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

		if (noOfChunk <= 0) {
			msg = "No Data To Generate Report";
			LOGGER.error(msg);

			revIntCheckRepo.updateStatusByBatchId(batchId,
					ReconStatusConstants.NO_DATA_FOUND, true);

			return;
		}

		int j = 0;

		try {
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);
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
								"USP_DISPLAY_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP");

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_BATCH_ID", Long.class, ParameterMode.IN);

				storedProcNestedReport.setParameter("P_BATCH_ID", batchId);

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
				storedProcNestedReport.setParameter("P_CHUNK_NUM", j);
				
				storedProcNestedReport.registerStoredProcedureParameter("P_SOURCE_TYPE",
						String.class, ParameterMode.IN);

				storedProcNestedReport.setParameter("P_SOURCE_TYPE", "SFTP");
				

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("call stored proc with "
							+ "params {} Config ID is '%s', "
							+ " chunkNo is %d ", batchId.toString(), j);
					LOGGER.debug(msg);
				}

				long dbLoadStTime = System.currentTimeMillis();

				@SuppressWarnings("unchecked")
				List<Object[]> records = storedProcNestedReport.getResultList();

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
									+ " Report Name and Data count:" + "  '%s'",
							dbLoadTimeDiff, records.size());
					LOGGER.debug(msg);
				}

				if (records != null && !records.isEmpty()) {

					List<EinvoiceNestedReportDto> reconDataList = new ArrayList<>();
					reconDataList = records.stream()
							.map(o -> convertToNested(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						msg = String.format("ERP report Size  %d ",
								reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						ColumnPositionMappingStrategy<EinvoiceNestedReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(EinvoiceNestedReportDto.class);
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
						beanWriter.write(reconDataList);
						long generationEndTime = System.currentTimeMillis();
						long generationTimeDiff = (generationEndTime
								- generationStTime);
						if (LOGGER.isDebugEnabled()) {
							msg = String.format("Total Time taken to"
									+ " Generate the report is '%d'"
									+ "millisecs,"
									+ " Report Name and Data count:" + " '%d'",
									generationTimeDiff, records.size());
							LOGGER.debug(msg);
						}

					}
				}
				flushWriter(writer);
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while creating report for "
					+ "sftp USP_DISPLAY_CHUNK_IRN_NESTED_DETAIL_REPORT_SFTP :{}",
					ex);

			revIntCheckRepo.updateStatusByBatchId(batchId,
					ReconStatusConstants.FAILED, false);
			throw new AppException(ex);
		}

	}

	/**
	 * @param batchId
	 * @param tempDir
	 * @param entity
	 */
	private void createLineItemReport(Long batchId, File tempDir,
			InwardEInvoiceERPRequestEntity entity, List<String> filePaths) {
		String fullPath;
		Integer noOfChunk;
		String msg;
		Writer writer;
		fullPath = tempDir.getAbsolutePath() + File.separator
				+ getUniqueFileName(entity.getGstin(), entity.getSupplyType(),
						"_Detailed Report_")
				+ ".csv";
		
		if (LOGGER.isDebugEnabled()) {
			msg = String.format(
					"Config ID is '%s',"
							+ " Created temporary directory to generate "
							+ "zip file: %s",
					batchId.toString(), tempDir.getAbsolutePath());
			LOGGER.debug(msg);
		}

		filePaths.add(fullPath);

		// chunk value
		Integer chunkSize = getChunkSize();

		

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(
						"USP_INSERT_CHUNK_IRN_DETAIL_LINE_ITEM_REPORT_SFTP");

		storedProc.registerStoredProcedureParameter("P_BATCH_ID", Long.class,
				ParameterMode.IN);

		storedProc.setParameter("P_BATCH_ID", batchId);

		storedProc.registerStoredProcedureParameter("P_CHUNK_SPLIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("P_CHUNK_SPLIT_VAL", chunkSize);

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Executing chunking proc"
					+ " USP_INSERT_CHUNK_IRN_DETAIL_LINE_ITEM_REPORT_SFTP: '%s'",
					batchId.toString());
			LOGGER.debug(msg);
		}

		Integer chunks = (Integer) storedProc.getSingleResult();

		noOfChunk = chunks;

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Chunking proc Executed"
					+ " USP_INSERT_CHUNK_IRN_DETAIL_LINE_ITEM_REPORT_SFTP: id '%d', "
					+ "noOfChunk %d ", batchId, noOfChunk);
			LOGGER.debug(msg);
		}

		noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

		if (noOfChunk <= 0) {
			msg = "No Data To Generate Report";
			LOGGER.error(msg);

			revIntCheckRepo.updateStatusByBatchId(batchId,
					ReconStatusConstants.NO_DATA_FOUND, true);

			return;
		}

		int j = 0;

		try {
			writer = new BufferedWriter(new FileWriter(fullPath),
					CSV_BUFFER_SIZE);
			String invoiceHeadersTemplate = commonUtility
					.getProp("einvoice.detailed.report.download.header");
			writer.append(invoiceHeadersTemplate);

			String[] columnMappings = commonUtility
					.getProp("einvoice.detailed.report.download.mapping")
					.split(",");

			while (j < noOfChunk) {

				j++;

				StoredProcedureQuery storedProcNestedReport = entityManager
						.createStoredProcedureQuery(
								"USP_DISPLAY_CHUNK_IRN_DETAIL_LINE_ITEM_REPORT_SFTP");

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_BATCH_ID", Long.class, ParameterMode.IN);

				storedProcNestedReport.setParameter("P_BATCH_ID", batchId);

				storedProcNestedReport.registerStoredProcedureParameter(
						"P_CHUNK_NUM", Integer.class, ParameterMode.IN);
				storedProcNestedReport.setParameter("P_CHUNK_NUM", j);

				if (LOGGER.isDebugEnabled()) {
					msg = String.format("call stored proc with "
							+ "params {} Config ID is '%s', "
							+ " chunkNo is %d ", batchId.toString(), j);
					LOGGER.debug(msg);
				}

				long dbLoadStTime = System.currentTimeMillis();

				@SuppressWarnings("unchecked")
				List<Object[]> records = storedProcNestedReport.getResultList();

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
									+ " Report Name and Data count:" + "  '%s'",
							dbLoadTimeDiff, records.size());
					LOGGER.debug(msg);
				}

				if (records != null && !records.isEmpty()) {

					List<EinvoiceDetailedLineItemReportDto> reconDataList = new ArrayList<>();
					reconDataList = records.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						msg = String.format("ERP report Size  %d ",
								reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						ColumnPositionMappingStrategy<EinvoiceDetailedLineItemReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(
								EinvoiceDetailedLineItemReportDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<EinvoiceDetailedLineItemReportDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<EinvoiceDetailedLineItemReportDto> beanWriter = builder
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
							msg = String.format("Total Time taken to"
									+ " Generate the report is '%d'"
									+ "millisecs,"
									+ " Report Name and Data count:" + " '%d'",
									generationTimeDiff, records.size());
							LOGGER.debug(msg);
						}

					}
				}

				flushWriter(writer);
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while creating report for "
					+ "sftp USP_DISPLAY_CHUNK_IRN_DETAIL_LINE_ITEM_REPORT_SFTP :{}",
					ex);

			revIntCheckRepo.updateStatusByBatchId(batchId,
					ReconStatusConstants.FAILED, false);
			throw new AppException(ex);
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

	public String removeMilliseconds(String dateTimeString) {
		int dotIndex = dateTimeString.lastIndexOf('.');
		if (dotIndex != -1) {
			return dateTimeString.substring(0, dotIndex);
		} else {
			return dateTimeString;
		}
	}

	private EinvoiceNestedReportDto convertToNested(Object[] arr) {

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
				(arr[10] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[10].toString())
						: null);
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
				(arr[19] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[19].toString())
						: null);
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

	private EinvoiceDetailedLineItemReportDto convert(Object[] arr) {

		EinvoiceDetailedLineItemReportDto obj = new EinvoiceDetailedLineItemReportDto();

		obj.setIRNGenerationPeriod((arr[1] != null) ? arr[1].toString() : null);
		if (arr[2] != null && ("ACT".equalsIgnoreCase(arr[2].toString()))
				|| ("CNL".equalsIgnoreCase(arr[2].toString()))) {
			if ("ACT".equalsIgnoreCase(arr[2].toString())) {
				obj.setIRNStatus("Active");
			} else {
				obj.setIRNStatus("Cancelled");
			}
		}
		obj.setIRNNumber((arr[3] != null) ? arr[3].toString() : null);
		obj.setIRNDate((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setAcknowledgmentNumber((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setIRNCancellationDate(
				(arr[6] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(removeMilliseconds(arr[6].toString()))
						: null);
		obj.setCancellationReason((arr[7] != null) ? arr[7].toString() : null);
		obj.setCancellationRemarks((arr[8] != null) ? arr[8].toString() : null);
		obj.setEWayBillNumber((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setEWayBillDate(
				(arr[10] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(removeMilliseconds(arr[10].toString()))
						: null);
		obj.setValidUpto(
				(arr[11] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(removeMilliseconds(arr[11].toString()))
						: null);
		obj.setTaxScheme((arr[12] != null) ? arr[12].toString() : null);
		obj.setSupplyType((arr[13] != null) ? arr[13].toString() : null);
		obj.setDocumentType((arr[14] != null) ? arr[14].toString() : null);

		String docType = (arr[14] != null) ? arr[14].toString() : null;

		obj.setDocumentNumber(
				(arr[15] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[15].toString())
						: null);
		obj.setDocumentDate(
				(arr[16] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[16].toString())
						: null);
		obj.setReverseChargeFlag((arr[17] != null) ? arr[17].toString() : null);
		obj.setSupplierGSTIN((arr[18] != null) ? arr[18].toString() : null);
		obj.setSupplierTradeName((arr[19] != null) ? arr[19].toString() : null);
		obj.setSupplierLegalName((arr[20] != null) ? arr[20].toString() : null);
		obj.setSupplierAddress1((arr[21] != null) ? arr[21].toString() : null);
		obj.setSupplierAddress2((arr[22] != null) ? arr[22].toString() : null);
		obj.setSupplierLocation((arr[23] != null) ? arr[23].toString() : null);
		obj.setSupplierPincode((arr[24] != null) ? arr[24].toString() : null);
		obj.setSupplierStateCode((arr[25] != null) ? arr[25].toString() : null);
		obj.setSupplierPhone(
				(arr[26] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[26].toString())
						: null);
		obj.setSupplierEmail((arr[27] != null) ? arr[27].toString() : null);
		obj.setCustomerGSTIN((arr[28] != null) ? arr[28].toString() : null);
		obj.setCustomerTradeName((arr[29] != null) ? arr[29].toString() : null);
		obj.setCustomerLegalName((arr[30] != null) ? arr[30].toString() : null);
		obj.setCustomerAddress1((arr[31] != null) ? arr[31].toString() : null);
		obj.setCustomerAddress2((arr[32] != null) ? arr[32].toString() : null);
		obj.setCustomerLocation((arr[33] != null) ? arr[33].toString() : null);
		obj.setCustomerPincode((arr[34] != null) ? arr[34].toString() : null);
		obj.setCustomerStateCode((arr[35] != null) ? arr[35].toString() : null);
		obj.setBillingPOS((arr[36] != null) ? arr[36].toString() : null);
		obj.setCustomerPhone(
				(arr[37] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[37].toString())
						: null);
		obj.setCustomerEmail((arr[38] != null) ? arr[38].toString() : null);
		obj.setDispatcherTradeName(
				(arr[39] != null) ? arr[39].toString() : null);
		obj.setDispatcherAddress1(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setDispatcherAddress2(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setDispatcherLocation(
				(arr[42] != null) ? arr[42].toString() : null);
		obj.setDispatcherPincode((arr[43] != null) ? arr[43].toString() : null);
		obj.setDispatcherStateCode(
				(arr[44] != null) ? arr[44].toString() : null);
		obj.setShipToGSTIN((arr[45] != null) ? arr[45].toString() : null);
		obj.setShipToTradeName((arr[46] != null) ? arr[46].toString() : null);
		obj.setShipToLegalName((arr[47] != null) ? arr[47].toString() : null);
		obj.setShipToAddress1((arr[48] != null) ? arr[48].toString() : null);
		obj.setShipToAddress2((arr[49] != null) ? arr[49].toString() : null);
		obj.setShipToLocation((arr[50] != null) ? arr[50].toString() : null);
		obj.setShipToPincode((arr[51] != null) ? arr[51].toString() : null);
		obj.setShipToStateCode((arr[52] != null) ? arr[52].toString() : null);
		obj.setItemSerialNumber(
				(arr[53] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[53].toString())
						: null);
		obj.setProductSerialNumber(
				(arr[54] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[54].toString())
						: null);
		obj.setProductDescription(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setIsService((arr[56] != null) ? arr[56].toString() : null);
		obj.setHSN(
				(arr[57] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[57].toString())
						: null);
		obj.setBarcode(
				(arr[58] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[58].toString())
						: null);
		obj.setBatchName((arr[59] != null) ? arr[59].toString() : null);
		obj.setBatchExpiryDate((arr[60] != null) ? arr[60].toString() : null);
		obj.setWarrantyDate((arr[61] != null) ? arr[61].toString() : null);
		obj.setOrderLineReference(
				(arr[62] != null) ? arr[62].toString() : null);
		obj.setAttributeName((arr[63] != null) ? arr[63].toString() : null);
		obj.setAttributeValue((arr[64] != null) ? arr[64].toString() : null);
		obj.setOriginCountry((arr[65] != null) ? arr[65].toString() : null);
		obj.setUQC((arr[66] != null) ? arr[66].toString() : null);
		obj.setQuantity((arr[67] != null) ? arr[67].toString() : null);
		obj.setFreeQuantity((arr[68] != null) ? arr[68].toString() : null);
		obj.setUnitPrice(
				(arr[69] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[69].toString())
						: null);
		obj.setItemAmount(
				(arr[70] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[70], docType))
						: null);
		obj.setItemDiscount(
				(arr[71] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[71], docType))
						: null);
		obj.setPreTaxAmount(
				(arr[72] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[72], docType))
						: null);
		obj.setItemAssessableAmount(
				(arr[73] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[73], docType))
						: null);
		obj.setIGSTRate((arr[74] != null) ? arr[74].toString() : null);
		obj.setIGSTAmount(
				(arr[75] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[75], docType))
						: null);
		obj.setCGSTRate((arr[76] != null) ? arr[76].toString() : null);
		obj.setCGSTAmount(
				(arr[77] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[77], docType))
						: null);
		obj.setSGSTRate(
				(arr[78] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[78].toString())
						: null);
		obj.setSGSTAmount(
				(arr[79] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[79], docType))
						: null);
		obj.setCessAdvaloremRate((arr[80] != null) ? arr[80].toString() : null);
		obj.setCessAdvaloremAmount(
				(arr[81] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[81], docType))
						: null);
		obj.setCessSpecificAmount(
				(arr[82] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[82], docType))
						: null);
		obj.setStateCessAdvaloremRate(
				(arr[83] != null) ? arr[83].toString() : null);
		obj.setStateCessAdvaloremAmount(
				(arr[84] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[84], docType))
						: null);
		obj.setStateCessSpecificAmount(
				(arr[85] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[85], docType))
						: null);
		obj.setItemOtherCharges(
				(arr[86] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[86], docType))
						: null);
		obj.setTotalItemAmount(
				(arr[87] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[87], docType))
						: null);
		obj.setInvoiceOtherCharges(
				(arr[88] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[88], docType))
						: null);
		obj.setInvoiceAssessableAmount(
				(arr[89] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[89], docType))
						: null);
		obj.setInvoiceIGSTAmount(
				(arr[90] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[90], docType))
						: null);
		obj.setInvoiceCGSTAmount(
				(arr[91] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[91], docType))
						: null);
		obj.setInvoiceSGSTAmount(
				(arr[92] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[92], docType))
						: null);
		obj.setInvoiceCessAdvaloremAmount(
				(arr[93] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[93], docType))
						: null);
		obj.setInvoiceCessSpecificAmount(
				(arr[94] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[94], docType))
						: null);
		obj.setInvoiceStateCessAdvaloremAmount(
				(arr[95] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[95], docType))
						: null);
		obj.setInvoiceStateCessSpecificAmount(
				(arr[96] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[96], docType))
						: null);
		obj.setInvoiceDiscount(
				(arr[97] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[97], docType))
						: null);
		obj.setInvoiceValue(
				(arr[98] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(CheckForNegativeValue(arr[98], docType))
						: null);
		obj.setRoundOff(
				(arr[99] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[99].toString())
						: null);
		obj.setCurrencyCode((arr[100] != null) ? arr[100].toString() : null);
		obj.setCountryCode((arr[101] != null) ? arr[101].toString() : null);
		obj.setInvoiceValueFC((arr[102] != null)
				? DownloadReportsConstant.CSVCHARACTER
						.concat(CheckForNegativeValue(arr[102], docType))
				: null);
		obj.setPortCode((arr[103] != null) ? arr[103].toString() : null);
		obj.setShippingBillNumber(
				(arr[104] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[104].toString())
						: null);
		obj.setShippingBillDate(
				(arr[105] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[105].toString())
						: null);
		obj.setInvoiceRemarks((arr[106] != null) ? arr[106].toString() : null);
		obj.setInvoicePeriodStartDate(
				(arr[107] != null) ? arr[107].toString() : null);
		obj.setInvoicePeriodEndDate(
				(arr[108] != null) ? arr[108].toString() : null);
		obj.setPreceedingInvoiceNumber(
				(arr[109] != null) ? arr[109].toString() : null);
		obj.setPreceedingInvoiceDate(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setOtherReference((arr[111] != null) ? arr[111].toString() : null);
		obj.setReceiptAdviceReference(
				(arr[112] != null) ? arr[112].toString() : null);
		obj.setReceiptAdviceDate(
				(arr[113] != null) ? arr[113].toString() : null);
		obj.setTenderReference((arr[114] != null) ? arr[114].toString() : null);
		obj.setContractReference(
				(arr[115] != null) ? arr[115].toString() : null);
		obj.setExternalReference(
				(arr[116] != null) ? arr[116].toString() : null);
		obj.setProjectReference(
				(arr[117] != null) ? arr[117].toString() : null);
		obj.setCustomerPOReferenceNumber(
				(arr[118] != null) ? arr[118].toString() : null);
		obj.setCustomerPOReferenceDate(
				(arr[119] != null) ? arr[119].toString() : null);
		obj.setPayeeName((arr[120] != null) ? arr[120].toString() : null);
		obj.setModeOfPayment((arr[121] != null) ? arr[121].toString() : null);
		obj.setBranchOrIFSCCode(
				(arr[122] != null) ? arr[122].toString() : null);
		obj.setPaymentTerms((arr[123] != null) ? arr[123].toString() : null);
		obj.setPaymentInstruction(
				(arr[124] != null) ? arr[124].toString() : null);
		obj.setCreditTransfer((arr[125] != null) ? arr[125].toString() : null);
		obj.setDirectDebit((arr[126] != null) ? arr[126].toString() : null);
		obj.setCreditDays((arr[127] != null) ? arr[127].toString() : null);
		obj.setPaidAmount(
				(arr[128] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[128].toString())
						: null);
		obj.setBalanceAmount(
				(arr[129] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[129].toString())
						: null);
		obj.setAccountDetail((arr[130] != null) ? arr[130].toString() : null);
		obj.setEcomGSTIN((arr[131] != null) ? arr[131].toString() : null);
		obj.setSupportingDocURL(
				(arr[132] != null) ? arr[132].toString() : null);
		obj.setSupportingDocument(
				(arr[133] != null) ? arr[133].toString() : null);
		obj.setAdditionalInformation(
				(arr[134] != null) ? arr[134].toString() : null);
		obj.setTransporterID((arr[135] != null) ? arr[135].toString() : null);
		obj.setTransporterName((arr[136] != null) ? arr[136].toString() : null);
		obj.setTransportMode((arr[137] != null) ? arr[137].toString() : null);
		obj.setTransportDocNo(
				(arr[138] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[138].toString())
						: null);
		obj.setTransportDocDate(
				(arr[139] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[139].toString())
						: null);
		obj.setDistance((arr[140] != null) ? arr[140].toString() : null);
		obj.setVehicleNo(
				(arr[141] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[141].toString())
						: null);
		obj.setVehicleType((arr[142] != null) ? arr[142].toString() : null);
		obj.setSection7OfIGSTFlag(
				(arr[143] != null) ? arr[143].toString() : null);
		obj.setClaimRefundFlag((arr[144] != null) ? arr[144].toString() : null);
		obj.setExportDuty((arr[145] != null) ? arr[145].toString() : null);
		obj.setIRNGetCallDateTime((arr[146] != null)
				? DownloadReportsConstant.CSVCHARACTER
						.concat(removeMilliseconds(arr[146].toString()))
				: null);
		obj.setIrpName((arr[147] != null) ? arr[147].toString() : null);
		return obj;

	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("DownloadReports").toFile();
	}

	private static String getUniqueFileName(String gstin, String supplyType,
			String reportType) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();

		timeMilli = timeMilli.replace(".", "");
		timeMilli = timeMilli.replace("-", "");
		timeMilli = timeMilli.replace(":", "");

		String fileNameWithTimeStamp = gstin + "_" + supplyType + reportType
				+ timeMilli;

		if (LOGGER.isDebugEnabled()) {
			String msg = "UniqueFileName created ";
			LOGGER.debug(msg, fileNameWithTimeStamp);
		}

		return fileNameWithTimeStamp;
	}

	private String CheckForNegativeValue(Object value, String docType) {

		if (value != null && !Strings.isNullOrEmpty(docType)) {
			if ("CR".equalsIgnoreCase(docType)) {
				if (value instanceof BigDecimal) {
					return (value != null
							? ((((BigDecimal) value)
									.compareTo(BigDecimal.ZERO) > 0)
											? "-" + value.toString()
											: value.toString())
							: null);
				} else if (value instanceof Integer) {
					return (value != null
							? (((Integer) value > 0) ? "-" + value.toString()
									: value.toString())
							: null);
				} else if (value instanceof Long) {
					return (value != null
							? (((Long) value > 0) ? "-" + value.toString()
									: value.toString())
							: null);
				} else if (value instanceof BigInteger) {
					return (value != null
							? ((((BigInteger) value)
									.compareTo(BigInteger.ZERO) > 0)
											? "-" + value.toString()
											: value.toString())
							: null);
				} else {
					if (!value.toString().isEmpty()) {
						return "-" + value.toString().replaceFirst("-", "");
					} else {
						return null;
					}
				}
			} else
				return value.toString();
		}
		return null;
	}

	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
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
}
