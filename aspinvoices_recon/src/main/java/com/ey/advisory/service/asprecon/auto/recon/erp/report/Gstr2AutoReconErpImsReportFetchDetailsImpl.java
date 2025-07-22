/**
 * 
 */
package com.ey.advisory.service.asprecon.auto.recon.erp.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2Recon2APRReportCommonService;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */

@Slf4j
@Component("Gstr2AutoReconErpImsReportFetchDetailsImpl")
public class Gstr2AutoReconErpImsReportFetchDetailsImpl
		implements Gstr2AutoReconErpImsReportFetchDetails {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	@Autowired
	private EinvMasterGstinRepository einvMasterGstinRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("Gstr2Recon2APRReportCommonServiceImpl")
	private Gstr2Recon2APRReportCommonService commonService;

	static String date = null;

	public static List<String> distinctPan = null;

	private static int CSV_BUFFER_SIZE = 8192;

	private static final String CONF_KEY = "gstr2.recon.2apr.report.chunk.size";
	private static final String CONF_CATEG = "RECON_REPORTS";
	public static DateTimeFormatter dateFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	// ERP_EntityName_timestamp

	@Override
	public void generateImsReport(Long configId, Long entityId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		Integer chunkSize = getChunkSize();
		String entityName = null;
		String msg = null;
		Integer noOfChunk = null;
		String reportType = null;
		Writer writer = null;
		boolean isReportAvailable = false;
		try {

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityId);
			EntityInfoEntity entity = optional.get();
			entityName = entity.getEntityName();

		} catch (Exception e) {
			msg = String.format("Error while getting EntityName %d", entityId);
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Getting ERP Report for with configId:'%s'",
					configId.toString());
			LOGGER.debug(msg);
		}
		Pair<Integer, Integer> maxReportSizes = commonService
				.getChunkingSizes();

		Gstr2ReconAddlReportsEntity chunkDetails = addlReportRepo
				.getChunckSizeforReportType(configId, "ERP_Report");

		try {

			reportType = "ERP_Report_" + entityName;

			tempDir = createTempDir(configId, reportType);

			fullPath = tempDir.getAbsolutePath() + File.separator
					+ getUniqueFileName(reportType) + "_1" + ".csv";

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_AUTO_2APR_INS_CHUNK_ERP");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_AUTO_2APR_INS_CHUNK_ERP: '%d'",
						configId);
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			Integer chunksize = (Integer) storedProc.getSingleResult();

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_AUTO_2APR_INS_CHUNK_ERP: configId '%d', "
						+ "noOfChunk %d ", configId, noOfChunk);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {

			msg = String.format(
					"Error while executing chunking proc " + "configId %d",
					configId);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		noOfChunk = noOfChunk != null ? noOfChunk.intValue() : 0;

		if (noOfChunk != 0) {
			int j = 0;

			try {
				writer = new BufferedWriter(new FileWriter(fullPath),
						CSV_BUFFER_SIZE);

				String invoiceHeadersTemplate = commonUtility
						.getProp("gstr2.auto.recon.erp.ims.report.header");
				writer.append(invoiceHeadersTemplate);

				String[] columnMappings = commonUtility
						.getProp("gstr2.auto.recon.erp.ims.report.column")
						.split(",");
				StatefulBeanToCsv<Gstr2AutoReconErpReportDto> beanWriter = getBeanWriter(
						columnMappings, writer);
				distinctPan = einvMasterGstinRepo.getAllDistinctPan();

				Map<String, Config> configMap = configManager
						.getConfigs("EINVAPP", "einv.applicable.date");

				date = configMap.get("einv.applicable.date") == null ? "NA"
						: String.valueOf(configMap.get("einv.applicable.date")
								.getValue());
				int fileIndex = 1;
				int count = 0;
				int maxLimitPerFile = maxReportSizes.getValue0();
				while (j < noOfChunk) {
					j++;

					StoredProcedureQuery dispProc = entityManager
							.createStoredProcedureQuery(
									"USP_AUTO_2APR_DISP_CHUNK_ERP_IMS");

					dispProc.registerStoredProcedureParameter(
							"P_RECON_REPORT_CONFIG_ID", Long.class,
							ParameterMode.IN);

					dispProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

					dispProc.registerStoredProcedureParameter("P_CHUNK_VALUE",
							Integer.class, ParameterMode.IN);

					dispProc.setParameter("P_CHUNK_VALUE", j);

					if (LOGGER.isDebugEnabled()) {
						msg = String.format("Data proc Executed"
								+ " USP_AUTO_2APR_DISP_CHUNK_ERP: configId '%d', "
								+ "noOfChunk %d ", configId, j);
						LOGGER.debug(msg);
					}

					@SuppressWarnings("unchecked")
					List<Object[]> records = dispProc.getResultList();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"ERP Report + Ims values  no of records after proc "
										+ "call {} ",
								records.size());
					}

					if (records != null && !records.isEmpty()) {

						isReportAvailable = true;
						List<Gstr2AutoReconErpReportDto> reconDataList = records
								.stream().map(o -> convert(o))
								.collect(Collectors
										.toCollection(ArrayList::new));
						if (LOGGER.isDebugEnabled()) {
							msg = String.format(
									"Report Name and row count: '%s' - '%s'",
									reportType, reconDataList.size());
							LOGGER.debug(msg);
						}

						if (count >= maxLimitPerFile) {
							flushWriter(writer);

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"Invoking 2APR " + "commonService to"
												+ " upload a zip file : "
												+ "configId {}, ReportName"
												+ " {}, ",
										configId, reportType);
							}

							// Zipping
							if (isReportAvailable) {

								commonService.chunkZipFiles(tempDir,
										chunkDetails.getId(), configId,
										reportType, maxReportSizes.getValue1());
							}

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("After Invoking 2APR "
										+ "commonService to upload "
										+ "a zip file : configId "
										+ "{}, ReportName {} " + "deleted file "
										+ "from TempDir, ", configId,
										reportType);
							}

							count = 0;
							fileIndex += 1;
							fullPath = tempDir.getAbsolutePath()
									+ File.separator
									+ getUniqueFileName(reportType) + "_"
									+ fileIndex + ".csv";
							writer = new BufferedWriter(
									new FileWriter(fullPath), CSV_BUFFER_SIZE);
							writer.append(invoiceHeadersTemplate);
							beanWriter = getBeanWriter(columnMappings, writer);
						}
						count += reconDataList.size();
						beanWriter.write(reconDataList);

						long generationStTime = System.currentTimeMillis();
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
						if (noOfChunk == 1 || j == noOfChunk) {
							flushWriter(writer);
							commonService.chunkZipFiles(tempDir,
									chunkDetails.getId(), configId, reportType,
									maxReportSizes.getValue1());
							break;
						}
					}
				}

			} catch (Exception ex) {
				LOGGER.error("Exception while generating ERP report congigId : "
						+ "{},{} ", configId, ex);
				deleteTemporaryDirectory(tempDir);
				throw new AppException(ex);
			}
		}
		// updating flag in download table
		addlReportRepo.updateIsReportProcExecuted(configId, "ERP_Report");
		deleteTemporaryDirectory(tempDir);
	}

	private StatefulBeanToCsv<Gstr2AutoReconErpReportDto> getBeanWriter(
			String[] columnMappings, Writer writer) {
		ColumnPositionMappingStrategy<Gstr2AutoReconErpReportDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Gstr2AutoReconErpReportDto.class);
		mappingStrategy.setColumnMapping(columnMappings);
		StatefulBeanToCsvBuilder<Gstr2AutoReconErpReportDto> builder = new StatefulBeanToCsvBuilder<>(
				writer);
		StatefulBeanToCsv<Gstr2AutoReconErpReportDto> beanWriter = builder
				.withMappingStrategy(mappingStrategy)
				.withSeparator(CSVWriter.DEFAULT_SEPARATOR)
				.withLineEnd(CSVWriter.DEFAULT_LINE_END)
				.withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER).build();
		return beanWriter;
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

	private Gstr2AutoReconErpReportDto convert(Object[] arr) {

		Gstr2AutoReconErpReportDto obj = new Gstr2AutoReconErpReportDto();

		// obj.setUserResponse((arr[0] != null) ? arr[0].toString() : null);

		if ((arr[0] != null && (!arr[0].toString().isEmpty())
				&& (arr[0].toString().matches("[0-9]+")))) {

			obj.setUserResponse(arr[0].toString().length() == 5
					? "'".concat(("0").concat(arr[0].toString()))
					: "'".concat((arr[0].toString())));
		} else {
			obj.setUserResponse((arr[0] != null) ? (arr[0].toString()) : null);
		}

		obj.setTaxPeriodGSTR3B((arr[1] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setMatchReason((arr[2] != null) ? arr[2].toString() : null);
		obj.setMismatchReason((arr[3] != null) ? arr[3].toString() : null);
		obj.setReportCategory((arr[4] != null) ? arr[4].toString() : null);
		// based on doc no from number mismatch
		obj.setReportType((arr[5] != null) ? arr[5].toString() : null);
		obj.setERPReportType((arr[6] != null) ? arr[6].toString() : null);
		obj.setTaxPeriod2A((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setTaxPeriod2B((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);
		obj.setTaxPeriodPR((arr[9] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setRecipientGSTIN2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setRecipientGSTINPR((arr[11] != null) ? arr[11].toString() : null);
		obj.setSupplierGSTIN2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setSupplierGSTINPR((arr[13] != null) ? arr[13].toString() : null);
		String docType2A = (arr[14] != null) ? arr[14].toString() : null;
		obj.setDocType2A(docType2A);
		String docTypePR = (arr[15] != null) ? arr[15].toString() : null;
		obj.setDocTypePR(docTypePR);
		obj.setDocumentNumber2A(
				(arr[16] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[16].toString())
						: null);
		obj.setDocumentNumberPR(
				(arr[17] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[17].toString())
						: null);
		obj.setDocumentDate2A((arr[18] != null) ? arr[18].toString() : null);
		obj.setDocumentDatePR((arr[19] != null) ? arr[19].toString() : null);
		obj.setPOS2A((arr[20] != null) ? arr[20].toString() : null);
		obj.setPOSPR((arr[21] != null) ? arr[21].toString() : null);
		if (docType2A != null && (docType2A.equalsIgnoreCase("CR")
				|| docType2A.equalsIgnoreCase("C")
				|| docType2A.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValue2A(CheckForNegativeValue(arr[22]));
			obj.setIGST2A(CheckForNegativeValue(arr[24]));
			obj.setCGST2A(CheckForNegativeValue(arr[26]));
			obj.setSGST2A(CheckForNegativeValue(arr[28]));
			obj.setCess2A(CheckForNegativeValue(arr[30]));
			obj.setInvoiceValue2A(CheckForNegativeValue(arr[32]));
		} else {

			obj.setTaxableValue2A(
					(arr[22] != null) ? arr[22].toString() : null);
			obj.setIGST2A((arr[24] != null) ? arr[24].toString() : null);
			obj.setCGST2A((arr[26] != null) ? arr[26].toString() : null);
			obj.setSGST2A((arr[28] != null) ? arr[28].toString() : null);
			obj.setCess2A((arr[30] != null) ? arr[30].toString() : null);
			obj.setInvoiceValue2A(
					(arr[32] != null) ? arr[32].toString() : null);
		}
		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setTaxableValuePR(CheckForNegativeValue(arr[23]));
			obj.setIGSTPR(CheckForNegativeValue(arr[25]));
			obj.setCGSTPR(CheckForNegativeValue(arr[27]));
			obj.setSGSTPR(CheckForNegativeValue(arr[29]));
			obj.setCessPR(CheckForNegativeValue(arr[31]));
			obj.setInvoiceValuePR(CheckForNegativeValue(arr[33]));
		} else {
			obj.setTaxableValuePR(
					(arr[23] != null) ? arr[23].toString() : null);
			obj.setIGSTPR((arr[25] != null) ? arr[25].toString() : null);
			obj.setCGSTPR((arr[27] != null) ? arr[27].toString() : null);
			obj.setSGSTPR((arr[29] != null) ? arr[29].toString() : null);
			obj.setCessPR((arr[31] != null) ? arr[31].toString() : null);
			obj.setInvoiceValuePR(
					(arr[33] != null) ? arr[33].toString() : null);
		}

		obj.setITCAvailability2B((arr[34] != null) ? arr[34].toString() : null);
		obj.setReasonforITCUnavailability2B(
				(arr[35] != null) ? arr[35].toString() : null);
		obj.setGSTR1FilingStatus((arr[36] != null) ? arr[36].toString() : null);
		obj.setGSTR1FilingDate((arr[37] != null) ? arr[37].toString() : null);
		obj.setGSTR1FilingPeriod(
				(arr[38] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[38].toString())
						: null);
		obj.setGSTR3BFilingStatus(
				(arr[39] != null) ? arr[39].toString() : null);
		obj.setReverseChargeFlag2A(
				(arr[40] != null) ? arr[40].toString() : null);
		obj.setReverseChargeFlagPR(
				(arr[41] != null) ? arr[41].toString() : null);
		obj.setPlantCode((arr[42] != null) ? arr[42].toString() : null);
		obj.setDivision((arr[43] != null) ? arr[43].toString() : null);
		obj.setPurchaseOrganisation(
				(arr[44] != null) ? arr[44].toString() : null);
		obj.setTableType2A((arr[45] != null) ? arr[45].toString() : null);
		obj.setSupplyType2A((arr[46] != null) ? arr[46].toString() : null);
		obj.setSupplyTypePR((arr[47] != null) ? arr[47].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[48] != null) ? arr[48].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[49] != null) ? arr[49].toString() : null);
		obj.setApprovalStatus((arr[50] != null) ? arr[50].toString() : null);
		obj.setRecordStatus((arr[51] != null) ? arr[51].toString() : null);
		obj.setKeyDescription((arr[52] != null) ? arr[52].toString() : null);
		obj.setReconDate(
				(arr[53] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[53].toString())
						: null);
		obj.setRevIntDate(
				(arr[54] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[54].toString())
						: null);

		// new columns added
		obj.setIrn2A((arr[55] != null) ? arr[55].toString() : null);
		obj.setIrnDate2A(
				(arr[56] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[56].toString())
						: null);
		obj.setUserDefinedField1((arr[57] != null) ? arr[57].toString() : null);
		obj.setUserDefinedField2((arr[58] != null) ? arr[58].toString() : null);
		obj.setUserDefinedField3((arr[59] != null) ? arr[59].toString() : null);
		obj.setUserDefinedField4((arr[60] != null) ? arr[60].toString() : null);
		obj.setUserDefinedField5((arr[61] != null) ? arr[61].toString() : null);
		obj.setRequestId(
				(arr[62] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[62].toString())
						: null);
		obj.setIdPr((arr[63] != null) ? arr[63].toString() : null);
		obj.setId2A((arr[64] != null) ? arr[64].toString() : null);
		obj.setResponseRemarks((arr[65] != null) ? arr[65].toString() : null);
		obj.setEligibilityIndicator(
				(arr[66] != null) ? arr[66].toString() : null);

		if (docTypePR != null && (docTypePR.equalsIgnoreCase("CR")
				|| docTypePR.equalsIgnoreCase("C")
				|| docTypePR.equalsIgnoreCase("RCR"))) {
			obj.setAvailableIGST(CheckForNegativeValue(arr[67]));
			obj.setAvailableCGST(CheckForNegativeValue(arr[68]));
			obj.setAvailableSGST(CheckForNegativeValue(arr[69]));
			obj.setAvailableCESS(CheckForNegativeValue(arr[70]));
		} else {
			obj.setAvailableIGST((arr[67] != null) ? arr[67].toString() : null);
			obj.setAvailableCGST((arr[68] != null) ? arr[68].toString() : null);
			obj.setAvailableSGST((arr[69] != null) ? arr[69].toString() : null);
			obj.setAvailableCESS((arr[70] != null) ? arr[70].toString() : null);
		}

		obj.setReturnFilingFrequency(
				(arr[71] != null) ? arr[71].toString() : null);
		obj.setSupplierGSTINCancellationDate2A(
				(arr[72] != null) ? arr[72].toString() : null);
		obj.setVendorComplianceTrend(
				(arr[73] != null) ? arr[73].toString() : null);
		obj.setSupplierCode((arr[74] != null) ? arr[74].toString() : null);
		obj.setBOEReferenceDate2A(
				(arr[75] != null) ? arr[75].toString() : null);
		obj.setPortCode2A((arr[76] != null) ? arr[76].toString() : null);
		obj.setPortCodePR((arr[77] != null) ? arr[77].toString() : null);
		obj.setBillOfEntry2A((arr[78] != null) ? arr[78].toString() : null);
		obj.setBillOfEntryPR((arr[79] != null) ? arr[79].toString() : null);
		obj.setBillOfEntryDate2A((arr[80] != null) ? arr[80].toString() : null);
		obj.setBillOfEntryDatePR((arr[81] != null) ? arr[81].toString() : null);
		obj.setCompanyCode((arr[82] != null) ? arr[82].toString() : null);
		obj.setSourceIdentifier((arr[83] != null) ? arr[83].toString() : null);
		obj.setVendorType((arr[84] != null) ? arr[84].toString() : null);
		obj.setHSN((arr[85] != null) ? arr[85].toString() : null);
		obj.setVendorRiskCategory(
				(arr[86] != null) ? arr[86].toString() : null);
		obj.setVendorPaymentTermsDays(
				(arr[87] != null) ? arr[87].toString() : null);
		obj.setVendorRemarks((arr[88] != null) ? arr[88].toString() : null);

		String reportType = (arr[5] != null) ? arr[5].toString() : null;

		String gstinPR = (arr[13] != null) ? arr[13].toString() : "";

		if (gstinPR.length() == 15) {

			String panPR = gstinPR.substring(2, 12);

			if ("Addition in PR".equalsIgnoreCase(reportType)) {
				if (distinctPan.contains(panPR))
					obj.setEInvoiceApplicability(
							"Applicable as per NIC Master(Last Updated : "
									+ date + ")");
				else
					obj.setEInvoiceApplicability(
							"Not Applicable as per NIC Master(Last Updated : "
									+ date + ")");
			}
		} else {

			String gstin2A = (arr[12] != null) ? arr[12].toString() : "";

			if (gstin2A.length() == 15) {

				String pan2A = gstin2A.substring(2, 12);

				if (distinctPan.contains(pan2A))
					obj.setEInvoiceApplicability(
							"Applicable as per NIC Master(Last Updated : "
									+ date + ")");
				else
					obj.setEInvoiceApplicability(
							"Not Applicable as per NIC Master(Last Updated : "
									+ date + ")");

			}

		}
		/*
		 * obj.setEInvoiceApplicability( (arr[89] != null) ? arr[89].toString()
		 * : null);
		 */
		obj.setQRCodeCheck((arr[90] != null) ? arr[90].toString() : null);
		obj.setQRCodeValidationResult(
				(arr[91] != null) ? arr[91].toString() : null);
		obj.setQRCodeMatchCount((arr[92] != null) ? arr[92].toString() : null);
		obj.setQRCodeMismatchCount(
				(arr[93] != null) ? arr[93].toString() : null);
		obj.setQRMismatchAttributes(
				(arr[94] != null) ? arr[94].toString() : null);

		// new columns
		obj.setGSTR3BFilingDate((arr[95] != null) ? arr[95].toString() : null);
		obj.setSuppGstinStatus((arr[96] != null) ? arr[96].toString() : null);
		obj.setSysDefField1((arr[97] != null) ? arr[97].toString() : null);
		obj.setSysDefField2((arr[98] != null) ? arr[98].toString() : null);
		obj.setSysDefField3((arr[99] != null)
				? (new BigDecimal(arr[99].toString())).toString()
				: null);
		obj.setSysDefField4((arr[100] != null)
				? (new BigDecimal(arr[100].toString())).toString()
				: null);
		obj.setSysDefField5((arr[101] != null) ? arr[101].toString() : null);
		obj.setSysDefField6((arr[102] != null) ? arr[102].toString() : null);
		obj.setSysDefField7(
				(arr[103] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[103].toString())
						: null);
		obj.setSysDefField8(
				(arr[104] != null)
						? DownloadReportsConstant.CSVCHARACTER
								.concat(arr[104].toString())
						: null);
		// US 111198:Auto Recon 108DFs
		if (arr[105] != null) {
			LocalDateTime generationDate2B = LocalDateTime
					.parse(arr[105].toString(), dateTimeFormatter);
			obj.setSysDefField9(DownloadReportsConstant.CSVCHARACTER
					.concat(EYDateUtil.toLocalDateTimeFromUTC(generationDate2B)
							.toString().replace("T", " ")));
		} else {
			obj.setSysDefField9(null);
		}
		if (arr[106] != null) {
			LocalDateTime generationDate2A = LocalDateTime
					.parse(arr[106].toString(), dateTimeFormatter);
			obj.setSysDefField10(DownloadReportsConstant.CSVCHARACTER
					.concat(EYDateUtil.toLocalDateTimeFromUTC(generationDate2A)
							.toString().replace("T", " ")));
		} else {
			obj.setSysDefField10(null);
		}
		// Suggested Resp
		if ((arr[107] != null && (!arr[107].toString().isEmpty())
				&& (arr[107].toString().matches("[0-9]+")))) {
			obj.setSuggestedResponse(arr[107].toString().length() == 5
					? "'".concat(("0").concat(arr[107].toString()))
					: "'".concat((arr[107].toString())));
		} else {
			obj.setSuggestedResponse(
					(arr[107] != null) ? (arr[107].toString()) : null);
		}
		obj.setUserIMSResponse((arr[108] != null) ? arr[108].toString() : null);

		// ims reports

		if (arr[108] != null && !arr[108].toString().isEmpty()) {
			obj.setUserIMSResponse(arr[108].toString());
		} else {
			obj.setUserIMSResponse(null);
		}

		if (arr[109] != null && !arr[109].toString().isEmpty()) {
			obj.setIMSResponseRemarks(arr[109].toString());
		} else {
			obj.setIMSResponseRemarks(null);
		}

		if (arr[110] != null && !arr[110].toString().isEmpty()) {
			obj.setActionGSTN(arr[110].toString());
		} else {
			obj.setActionGSTN(null);
		}

		if (arr[111] != null && !arr[111].toString().isEmpty()) {
			obj.setPendingActionBlocked(arr[111].toString());
		} else {
			obj.setPendingActionBlocked(null);
		}

		if (arr[112] != null && !arr[112].toString().isEmpty()) {

			LocalDateTime getCallDateTime = LocalDateTime
					.parse(arr[112].toString(), dateTimeFormatter);
			obj.setIMSGetCallDateTime(DownloadReportsConstant.CSVCHARACTER
					.concat(EYDateUtil.toLocalDateTimeFromUTC(getCallDateTime)
							.toString().replace("T", " ")));
			// obj.setIMSGetCallDateTime(arr[112].toString());
		} else {
			obj.setIMSGetCallDateTime(null);
		}

		if (arr[113] != null && !arr[113].toString().isEmpty()) {
			obj.setActionDigiGST(arr[113].toString());
		} else {
			obj.setActionDigiGST(null);
		}

		if (arr[114] != null && !arr[114].toString().isEmpty()) {

			LocalDateTime actionDigiGSTDateTime = LocalDateTime
					.parse(arr[114].toString(), dateTimeFormatter);
			obj.setActionDigiGSTDateTime(
					DownloadReportsConstant.CSVCHARACTER.concat(EYDateUtil
							.toLocalDateTimeFromUTC(actionDigiGSTDateTime)
							.toString().replace("T", " ")));
			// obj.setActionDigiGSTDateTime(arr[114].toString());
		} else {
			obj.setActionDigiGSTDateTime(null);
		}

		if (arr[115] != null && !arr[115].toString().isEmpty()) {
			obj.setSavedtoGSTN(arr[115].toString());
		} else {
			obj.setSavedtoGSTN(null);
		}

		if (arr[116] != null && !arr[116].toString().isEmpty()) {
			obj.setActiveinIMSGSTN(arr[116].toString());
		} else {
			obj.setActiveinIMSGSTN(null);
		}

		if (arr[117] != null && !arr[117].toString().isEmpty()) {
			obj.setSupplierLegalName2A(arr[117].toString());
		} else {
			obj.setSupplierLegalName2A(null);
		}

		if (arr[118] != null && !arr[118].toString().isEmpty()) {
			obj.setSupplierTradeName2A(arr[118].toString());
		} else {
			obj.setSupplierTradeName2A(null);
		}

		if (arr[119] != null && !arr[119].toString().isEmpty()) {
			obj.setSupplierNamePR(arr[119].toString());
		} else {
			obj.setSupplierNamePR(null);
		}

		if (arr[120] != null && !arr[120].toString().isEmpty()) {
			obj.setTotalTax2A((new BigDecimal(arr[120].toString())).toString());
		} else {
			obj.setTotalTax2A(null);
		}

		if (arr[121] != null && !arr[121].toString().isEmpty()) {
			obj.setTotalTaxPR((new BigDecimal(arr[121].toString())).toString());
		} else {
			obj.setTotalTaxPR(null);
		}

		if (arr[122] != null && !arr[122].toString().isEmpty()) {
			LocalDateTime generationDate2a = LocalDateTime
					.parse(arr[114].toString(), dateTimeFormatter);
			obj.setGenerationDate2a(DownloadReportsConstant.CSVCHARACTER
					.concat(EYDateUtil.toLocalDateTimeFromUTC(generationDate2a)
							.toString().replace("T", " ")));
		} else {
			obj.setGenerationDate2a(null);
		}

		if (arr[123] != null && !arr[123].toString().isEmpty()) {
			LocalDateTime generationDate2b = LocalDateTime
					.parse(arr[114].toString(), dateTimeFormatter);
			obj.setGenerationDate2b(DownloadReportsConstant.CSVCHARACTER
					.concat(EYDateUtil.toLocalDateTimeFromUTC(generationDate2b)
							.toString().replace("T", " ")));
		} else {
			obj.setGenerationDate2b(null);
		}

		if (arr[124] != null && !arr[124].toString().isEmpty()) {
			obj.setITCReversalIdentifier(arr[124].toString());
		} else {
			obj.setITCReversalIdentifier(null);
		}

		if (arr[125] != null && !arr[125].toString().isEmpty()) {
			obj.setOrgInvAmendmentPeriod(arr[125].toString());
		} else {
			obj.setOrgInvAmendmentPeriod(null);
		}

		if (arr[126] != null && !arr[126].toString().isEmpty()) {
			obj.setOrgAmendmentType(arr[126].toString());
		} else {
			obj.setOrgAmendmentType(null);
		}

		if (arr[127] != null && !arr[127].toString().isEmpty()) {
			obj.setOrgDocNumber2A(
					arr[127] != null
							? DownloadReportsConstant.CSVCHARACTER
									.concat(arr[127].toString())
							: null);
		} else {
			obj.setOrgDocNumber2A(null);
		}

		if (arr[128] != null && !arr[128].toString().isEmpty()) {
			obj.setOrgDocDate2A(arr[128].toString());
		} else {
			obj.setOrgDocDate2A(null);
		}

		if (arr[129] != null && !arr[129].toString().isEmpty()) {
			obj.setOrgDocNumberPR(arr[129].toString());
		} else {
			obj.setOrgDocNumberPR(null);
		}

		if (arr[130] != null && !arr[130].toString().isEmpty()) {

			obj.setOrgDocDatePR(
					(arr[130] != null)
							? DownloadReportsConstant.CSVCHARACTER
									.concat(arr[130].toString())
							: null);
		} else {
			obj.setOrgDocDatePR(null);
		}

		if (arr[131] != null && !arr[131].toString().isEmpty()) {
			obj.setUserID(arr[131].toString());
		} else {
			obj.setUserID(null);
		}

		if (arr[132] != null && !arr[132].toString().isEmpty()) {
			obj.setIMSUniqueID(arr[132].toString());
		} else {
			obj.setIMSUniqueID(null);
		}

		return obj;
	}

	private String CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
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
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
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

		}
		return null;
	}

	private static File createTempDir(Long configId, String reportType)
			throws IOException {

		String tempFolderPrefix = "ReconReports" + "_" + reportType + "_"
				+ configId;
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

	private Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

}
