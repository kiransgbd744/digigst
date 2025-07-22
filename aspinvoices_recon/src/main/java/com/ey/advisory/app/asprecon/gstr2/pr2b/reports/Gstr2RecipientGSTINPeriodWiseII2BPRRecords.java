package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

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

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2RecipientGSTINPeriodWiseIIRecordDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRAddlReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("Gstr2RecipientGSTINPeriodWiseII2BPRRecords")
public class Gstr2RecipientGSTINPeriodWiseII2BPRRecords {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRReportCommonServiceImpl")
	private Gstr2Recon2BPRReportCommonService commonService;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getRecipientGSTINPeriodWiseIIRecords(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName(
				"Recipient GSTIN Tax Period Wise Report II");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_RECON_RPT_2B_RGSTIN_TXPRD2");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);

			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			long dbLoadStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " Report Name and Data count: '%s' - '%s'",
						dbLoadTimeDiff, reportType, records.size());
				LOGGER.debug(msg);
			}

			if (records != null && !records.isEmpty()) {

				fullPath = tempDir.getAbsolutePath() + File.separator
						+ reportType + ".csv";
				List<Gstr2RecipientGSTINPeriodWiseIIRecordDto> reconDataList = new ArrayList<>();

				try (Writer writer = new BufferedWriter(
						new FileWriter(fullPath), CSV_BUFFER_SIZE)) {
					reconDataList = records.stream().map(o -> convert(o))
							.collect(Collectors.toCollection(ArrayList::new));
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Report Name and row count: '%s' - '%s'",
								reportType, reconDataList.size());
						LOGGER.debug(msg);
					}
					if (reconDataList != null && !reconDataList.isEmpty()) {

						String invoiceHeadersTemplate = commonUtility
								.getProp("gstr2bpr.recon.rgpwr2.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2bpr.recon.rgpwr2.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2RecipientGSTINPeriodWiseIIRecordDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(
								Gstr2RecipientGSTINPeriodWiseIIRecordDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2RecipientGSTINPeriodWiseIIRecordDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2RecipientGSTINPeriodWiseIIRecordDto> beanWriter = builder
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
							String msg = String.format(
									"Total Time taken to"
											+ " Generate the report is '%d' "
											+ " millisecs,"
											+ " Report Name and Data count:"
											+ " '%s' - '%s'",
									generationTimeDiff, reportType,
									records.size());
							LOGGER.debug(msg);
						}
					}
				} catch (Exception ex) {
					LOGGER.error("Exception while executing the Store Proc for "
							+ "Report Type :{}", reportType, ex);
					deleteTemporaryDirectory(tempDir);

				}
				String zipFileName = null;
				Pair<String, String> uploadedDocName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);
					
					uploadedDocName = DocumentUtility
							.uploadFile(zipFile,"Gstr2ReconReports");

				}
				if (uploadedDocName != null) {
					Gstr2Recon2BPRAddlReportsEntity chunkDetails = addlReportRepo
							.getChunckSizeforReportType(configId,
									"Recipient_GSTIN_Period_Wise_Record_II");

					commonService.saveReportChunks(chunkDetails.getId(),
							"Recipient_GSTIN_Period_Wise_Record_II",
							uploadedDocName.getValue0(), true, 
							uploadedDocName.getValue1());
					if (LOGGER.isDebugEnabled()) {
						String msg = String.format("Update file path for "
								+ "Recipient_GSTIN_Period_Wise_Record_II "
								+ ":%s : ", uploadedDocName);
						LOGGER.debug(msg);
					}
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);
			deleteTemporaryDirectory(tempDir);

		}
		addlReportRepo.updateIsReportProcExecuted(configId, 
				"Recipient_GSTIN_Period_Wise_Record_II");

		deleteTemporaryDirectory(tempDir);

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

	private Gstr2RecipientGSTINPeriodWiseIIRecordDto convert(Object[] arr) {

		Gstr2RecipientGSTINPeriodWiseIIRecordDto obj = new Gstr2RecipientGSTINPeriodWiseIIRecordDto();
		 	obj.setRecipientsGstin2B((arr[0] != null) ? arr[0].toString() : null);
	        obj.setReportType((arr[1] != null) ? arr[1].toString() : null);
	        obj.setTotalTaxComparison((arr[2] != null) ? arr[2].toString() : null);
	        obj.setTaxPeriod2B((arr[3] != null) ? "`".concat(arr[3].toString()) : null);
	        obj.setReverseChargeFlag2B((arr[4] != null) ? arr[4].toString() : null);
	        obj.setDocType2B((arr[5] != null) ? arr[5].toString() : null);
	        obj.setRecordsCount2B((arr[6] != null) ? arr[6].toString() : null);
	        obj.setTaxableValue2B((arr[7] != null) ? arr[7].toString() : null);
	        obj.setIgst2B((arr[8] != null) ? arr[8].toString() : null);
	        obj.setCgst2B((arr[9] != null) ? arr[9].toString() : null);
	        obj.setSgst2B((arr[10] != null) ? arr[10].toString() : null);
	        obj.setCess2B((arr[11] != null) ? arr[11].toString() : null);
	        obj.setTotalTax2B((arr[12] != null) ? arr[12].toString() : null);

	        obj.setRecordsCountPR((arr[13] != null) ? arr[13].toString() : null);
	        obj.setTaxableValuePR((arr[14] != null) ? arr[14].toString() : null);
	        obj.setIgstPR((arr[15] != null) ? arr[15].toString() : null);
	        obj.setCgstPR((arr[16] != null) ? arr[16].toString() : null);
	        obj.setSgstPR((arr[17] != null) ? arr[17].toString() : null);
	        obj.setCessPR((arr[18] != null) ? arr[18].toString() : null);
	        obj.setTotalTaxPR((arr[19] != null) ? arr[19].toString() : null);
	        obj.setAvailableIgstPR((arr[20] != null) ? arr[20].toString() : null);
	        obj.setAvailableCgstPR((arr[21] != null) ? arr[21].toString() : null);
	        obj.setAvailableSgstPR((arr[22] != null) ? arr[22].toString() : null);
	        obj.setAvailableCessPR((arr[23] != null) ? arr[23].toString() : null);
	        obj.setTotalAvailableTaxPR((arr[24] != null) ? arr[24].toString() : null);
	        obj.setIneligibleIgstPR((arr[25] != null) ? arr[25].toString() : null);
	        obj.setIneligibleCgstPR((arr[26] != null) ? arr[26].toString() : null);
	        obj.setIneligibleSgstPR((arr[27] != null) ? arr[27].toString() : null);
	        obj.setIneligibleCessPR((arr[28] != null) ? arr[28].toString() : null);
	        obj.setTotalIneligibleTaxPR((arr[29] != null) ? arr[29].toString() : null);
	
		

		return obj;
	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Recipient_GSTIN_Period_Wise_Record_II" + "_"
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

	private String CheckForNegativeValue(Object value) {
		if (value != null) {
			if (value instanceof BigDecimal) {
				return (value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else if (value instanceof Integer) {
				return (value != null ? (((Integer) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof Long) {
				return (value != null ? (((Long) value > 0)
						? "-" + value.toString() : value.toString()) : null);
			} else if (value instanceof BigInteger) {
				return (value != null
						? ((((BigInteger) value).compareTo(BigInteger.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null);
			} else {
				if(!value.toString().isEmpty()){
					return "-" + value.toString().replaceFirst("-", "");
				} else{
					return null;
				}
			}
		}
		return null;
	}
}
