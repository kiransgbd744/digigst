
package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2SummaryCalenderPeriodRecords")
public class Gstr2SummaryCalenderPeriodRecords {
	
	//Summary_CalendarPeriod_Records
	

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

	public void getSummaryCalendarPeriodRecords(Long configId)
			throws IOException {

		String fullPath = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with configId:'%s'", configId);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("Summary Report Calendar Period");

		try {

			tempDir = createTempDir(configId);

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_RPT_CALMONTH_SMRY");

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
				List<Gstr2SummaryCalenderPeriodRecordsDto> reconDataList =
						new ArrayList<>();

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
								"gstr2.recon.summary.calender.period.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp(
								"gstr2.recon.summary.calender.period.column")
								.split(",");

						ColumnPositionMappingStrategy
						<Gstr2SummaryCalenderPeriodRecordsDto> 
						mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(
								Gstr2SummaryCalenderPeriodRecordsDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder
						<Gstr2SummaryCalenderPeriodRecordsDto> builder 
						= new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv
						<Gstr2SummaryCalenderPeriodRecordsDto> beanWriter = 
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
				String summaryCalenderPeriodRecordsFilePath = null;
				if (tempDir.list().length > 0) {
					zipFileName = combineAndZipCsvFiles.zipfolder(configId, 
							tempDir, reportType);

					File zipFile = new File(tempDir, zipFileName);

					summaryCalenderPeriodRecordsFilePath = DocumentUtility
							.uploadZipFile(zipFile, "Gstr2ReconReports");

				}
				if(summaryCalenderPeriodRecordsFilePath != null)
				addlReportRepo.updateReconFilePath(summaryCalenderPeriodRecordsFilePath,
						configId, "Summary_CalendarPeriod_Records");
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Update file path for Summary_CalendarPeriod_Records "
									+ ":%s : ", summaryCalenderPeriodRecordsFilePath);
					LOGGER.debug(msg);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the StoreProc for "
					+ "Report Type :{}", reportType, ex);

		}

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

	private Gstr2SummaryCalenderPeriodRecordsDto convert(Object[] arr) {

		Gstr2SummaryCalenderPeriodRecordsDto obj = 
				new Gstr2SummaryCalenderPeriodRecordsDto();
		
		
		obj.setCalendarPeriod((arr[0] != null) ? "`".concat(arr[0].toString()) : null);
		obj.setReportType((arr[1] != null) ? arr[1].toString() : null);
		obj.setRecordsCount2A((arr[2] != null) ? arr[2].toString() : null);
		obj.setTotalTax2A((arr[3] != null) ? arr[3].toString() : null);
		obj.setIGST2A((arr[4] != null) ? arr[4].toString() : null);
		obj.setCGST2A((arr[5] != null) ? arr[5].toString() : null);
		obj.setSGST2A((arr[6] != null) ? arr[6].toString() : null);
		obj.setCESS2A((arr[7] != null) ? arr[7].toString() : null);
		obj.setRecordsCountPR((arr[8] != null) ? arr[8].toString() : null);
		obj.setTotalTaxPR((arr[9] != null) ? arr[9].toString() : null);
		obj.setIGSTPR((arr[10] != null) ? arr[10].toString() : null);
		obj.setCGSTPR((arr[11] != null) ? arr[11].toString() : null);
		obj.setSGSTPR((arr[12] != null) ? arr[12].toString() : null);
		obj.setCESSPR((arr[13] != null) ? arr[13].toString() : null);
		
		
		obj.setTaxable2A((arr[14] != null) ? arr[14].toString() : null);
		obj.setTaxablePR((arr[15] != null) ? arr[15].toString() : null);
		
		

		return obj;
	}
		
	
	private static File createTempDir(Long configId) throws IOException {

		String tempFolderPrefix = "Summary_CalendarPeriod_Records" + "_" + configId;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private static String getUniqueFileName(String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getUniqueFileName() method";
			LOGGER.debug(msg);
		}
		LocalDateTime now = LocalDateTime.now();
		String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString(); 
		
		timeMilli = timeMilli.replace("." , "");
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
