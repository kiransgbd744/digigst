package com.ey.advisory.asprecon.gstr2.ap.recon.additional.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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

import com.ey.advisory.app.asprecon.gstr2.a2pr.reports.Gstr2TimeStampReportDto;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
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
 * @author Vishal.Verma
 *
 */

@Slf4j
@Component("Gstr2APNonAPTimeStampReport")
public class Gstr2APNonAPTimeStampReport {
	
	//GSTR_2A_Time_Stamp_Report

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	private static int CSV_BUFFER_SIZE = 8192;

	public void getGstr2TimeStampReport(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("GSTR 2A_6A Time Stamp Report");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_AUTO_2APR_RPT_2ATIMESTAMP");

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
				List<Gstr2TimeStampReportDto> reconDataList = new ArrayList<>();

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
								.getProp("gstr2.recon.time.stamp.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2.recon.time.stamp.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2TimeStampReportDto> 
						mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(Gstr2TimeStampReportDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2TimeStampReportDto> 
						builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2TimeStampReportDto> beanWriter = 
								builder
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

					/*cdr2AFilePath = DocumentUtility.uploadZipFile(zipFile,
							"Gstr2ReconReports");*/
					
					uploadedDocName = DocumentUtility
							.uploadFile(zipFile,"Gstr2ReconReports");

				}
				if (uploadedDocName != null)
					/*addlReportRepo.updateReconFilePath(cdr2AFilePath, configId,
							"GSTR_2A_Time_Stamp_Report");*/
				
				addlReportRepo.updateReconFilePathAndDocIdByReportName(uploadedDocName
						.getValue0(), uploadedDocName.getValue1(), configId, 
						"GSTR_2A_Time_Stamp_Report");
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Update file path for GSTR_2A_Time_Stamp_Report :%s : ",
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
				"GSTR_2A_Time_Stamp_Report");	
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

	private Gstr2TimeStampReportDto convert(Object[] arr) {

		Gstr2TimeStampReportDto obj = new Gstr2TimeStampReportDto();
		
		obj.setRecipientGSTIN((arr[0] != null) ? (arr[0].toString()) : null);
		obj.setTaxPeriod((arr[1] != null) ? "`".concat(arr[1].toString()) : null);
		
		Timestamp dt2 = (arr[2] != null) ? (Timestamp) arr[2] : null;
		String ldt2 = dt2 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt2.toLocalDateTime())
				.toString()) : null;
		obj.setB2B(ldt2);
		
		Timestamp dt3 = (arr[3] != null) ? (Timestamp) arr[3] : null;
		String ldt3 = dt3 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt3.toLocalDateTime())
				.toString()) : null;
		obj.setB2BAmendments(ldt3);
		
		Timestamp dt4 = (arr[4] != null) ? (Timestamp) arr[4] : null;
		String ldt4 = dt4 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt4.toLocalDateTime())
				.toString()) : null;
		obj.setCreditDebitNotes(ldt4);
		
		Timestamp dt5 = (arr[5] != null) ? (Timestamp) arr[5] : null;
		String ldt5 = dt5 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt5.toLocalDateTime())
				.toString()) : null;
		obj.setCreditDebitNotesAmendments(ldt5);
		
		Timestamp dt6 = (arr[6] != null) ? (Timestamp) arr[6] : null;
		String ldt6 = dt6 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt6.toLocalDateTime())
				.toString()) : null;
		obj.setISD(ldt6);
		
		Timestamp dt7 = (arr[7] != null) ? (Timestamp) arr[7] : null;
		String ldt7 = dt7 != null ? 
				"'".concat(EYDateUtil.toISTDateTimeFromUTC(dt7.toLocalDateTime())
				.toString()) : null;
		obj.setISDAmendments(ldt7);
		return obj;
	}

	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "GSTR_2A_Time_Stamp_Report" + "_"
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
	
	
	}

