package com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports;

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

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2SummaryTaxPeriodRecordsDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
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
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2APAndNonAPSummaryTaxPeriodRecords")
public class Gstr2APAndNonAPSummaryTaxPeriodRecords {

	// Summary_TaxPeriod_Record

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getSummaryTaxPeriodRecords(Long configId) throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String toTaxPeriodPR = null;
		String fromTaxPeriodPR = null;
		try {
			Gstr2ReconConfigEntity configDetails = reconConfigRepo
					.findByConfigId(configId);
			toTaxPeriodPR = configDetails.getToTaxPeriodPR().toString();
			fromTaxPeriodPR = configDetails.getFromTaxPeriodPR().toString();
		} catch (Exception ex) {
			LOGGER.error("Erorr while geting to and from Tax period PR {}", ex);
		}

		String reportType = getUniqueFileName("Summary Report Tax Period" + "_"
				+ fromTaxPeriodPR + "_" + toTaxPeriodPR);

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_AUTO_2APR_RPT_TAXPERIODS_SMRY");

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
				List<Gstr2SummaryTaxPeriodRecordsDto> reconDataList = new ArrayList<>();

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

						String invoiceHeadersTemplate = commonUtility.getProp(
								"gstr2.recon.summary.taxperiod.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2.recon.summary.taxperiod.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2SummaryTaxPeriodRecordsDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy
								.setType(Gstr2SummaryTaxPeriodRecordsDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2SummaryTaxPeriodRecordsDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2SummaryTaxPeriodRecordsDto> beanWriter = builder
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

				}
				String zipFileName = null;
				Pair<String, String> uploadedDocName = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId,
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					/*summaryTaxPeriodRecordsFilePath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");*/
					
					uploadedDocName = DocumentUtility
							.uploadFile(zipFile,"Gstr2ReconReports");

				}
				if (uploadedDocName != null)
					/*addlReportRepo.updateReconFilePath(
							summaryTaxPeriodRecordsFilePath, configId,
							"Summary_TaxPeriod_Record");*/
				
				addlReportRepo.updateReconFilePathAndDocIdByReportName(uploadedDocName
						.getValue0(), uploadedDocName.getValue1(), configId, 
						"Summary_TaxPeriod_Record");
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Update file path for Summary_TaxPeriod_Record "
									+ ":%s : ",
									uploadedDocName);
					LOGGER.debug(msg);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);

		}

		//updating flag in download table 
				addlReportRepo.updateIsReportProcExecuted(configId, 
						"Summary_TaxPeriod_Record");
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

	private Gstr2SummaryTaxPeriodRecordsDto convert(Object[] arr) {

		Gstr2SummaryTaxPeriodRecordsDto obj = new Gstr2SummaryTaxPeriodRecordsDto();

		obj.setRecipientsGSTIN((arr[0] != null) ? arr[0].toString() : null);
		obj.setDescription((arr[1] != null) ? arr[1].toString() : null);
		String docType = (arr[2] != null) ? arr[2].toString() : null;
		obj.setDocType(docType);
		obj.setRecordsCount2A((arr[3] != null) ? arr[3].toString() : null);
		obj.setRecordsPer2A((arr[4] != null) ? arr[4].toString() : null);
		obj.setTaxablePer2A((arr[6] != null) ? arr[6].toString() : null);
		obj.setTotalTaxPer2A((arr[12] != null) ? arr[12].toString() : null);
		obj.setRecordsCountPR((arr[15] != null) ? arr[15].toString() : null);
		obj.setRecordPerPR((arr[16] != null) ? arr[16].toString() : null);
		obj.setTaxablePerPR((arr[18] != null) ? arr[18].toString() : null);
		obj.setTotalTaxPerPR((arr[24] != null) ? arr[24].toString() : null);
		if (docType != null && (docType.equalsIgnoreCase("CR")
				|| docType.equalsIgnoreCase("C")
				|| docType.equalsIgnoreCase("RCR"))) {
			//obj.setTaxableValue2A(CheckForNegativeValue(arr[5]));
			//obj.setIGST2A(CheckForNegativeValue(arr[7]));
			//obj.setCGST2A(CheckForNegativeValue(arr[8]));
			//obj.setSGST2A(CheckForNegativeValue(arr[9]));
			//obj.setCESS2A(CheckForNegativeValue(arr[10]));
			//obj.setTotalTax2A(CheckForNegativeValue(arr[11]));
			//obj.setTotalTaxCfsY2A(CheckForNegativeValue(arr[13]));
			//obj.setTotalTaxCfsN2A(CheckForNegativeValue(arr[14]));
			obj.setTaxableValuePR(CheckForNegativeValue(arr[17]));
			obj.setIGSTPR(CheckForNegativeValue(arr[19]));
			obj.setCGSTPR(CheckForNegativeValue(arr[20]));
			obj.setSGSTPR(CheckForNegativeValue(arr[21]));
			obj.setCESSPR(CheckForNegativeValue(arr[22]));
			obj.setTotalTaxPR(CheckForNegativeValue(arr[23]));
		} else {
			obj.setTaxableValuePR(
					(arr[17] != null) ? arr[17].toString() : null);
			obj.setIGSTPR((arr[19] != null) ? arr[19].toString() : null);
			obj.setCGSTPR((arr[20] != null) ? arr[20].toString() : null);
			obj.setSGSTPR((arr[21] != null) ? arr[21].toString() : null);
			obj.setCESSPR((arr[22] != null) ? arr[22].toString() : null);
			obj.setTotalTaxPR((arr[23] != null) ? arr[23].toString() : null);
		}
		
		obj.setTaxableValue2A((arr[5] != null) ? arr[5].toString() : null);
		obj.setIGST2A((arr[7] != null) ? arr[7].toString() : null);
		obj.setCGST2A((arr[8] != null) ? arr[8].toString() : null);
		obj.setSGST2A((arr[9] != null) ? arr[9].toString() : null);
		obj.setCESS2A((arr[10] != null) ? arr[10].toString() : null);
		obj.setTotalTax2A((arr[11] != null) ? arr[11].toString() : null);
		obj.setTotalTaxCfsY2A(
				(arr[13] != null) ? arr[13].toString() : null);
		obj.setTotalTaxCfsN2A(
				(arr[14] != null) ? arr[14].toString() : null);
		
		return obj;
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

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Summary_TaxPeriod_Record" + "_" + configId;
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

}
