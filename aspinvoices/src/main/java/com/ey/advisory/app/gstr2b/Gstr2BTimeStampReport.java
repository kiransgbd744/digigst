package com.ey.advisory.app.gstr2b;

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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.config.ConfigConstants;
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
@Component("Gstr2BTimeStampReport")
public class Gstr2BTimeStampReport {

	// GSTR_2B_Time_Stamp_Report

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	private static int CSV_BUFFER_SIZE = 8192;

	public String getGstr2BTimeStampReport(List<String> gstnsList,
			int derivedStartPeriod, int derivedEndPeriod) throws IOException {

		String fullPath = null;
		String uploadedDocName = null;
		File tempDir = null;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Get Recon Report Details with gstnsList:'%s', "
							+ "derivedStartPeriod %d, derivedEndPeriod %d ",
					gstnsList, derivedStartPeriod, derivedEndPeriod);
			LOGGER.debug(msg);
		}

		String reportType = getUniqueFileName("GSTR 2B Time Stamp Report");

		try {

			tempDir = createTempDir();

			String querystr = querySting(gstnsList, derivedStartPeriod,
					derivedEndPeriod);

			Query q = entityManager.createNativeQuery(querystr);
			q.setParameter("gstins", gstnsList);
			q.setParameter("derivedStartPeriod", derivedStartPeriod);
			q.setParameter("derivedEndPeriod", derivedEndPeriod);

			long dbLoadStTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = q.getResultList();
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
				List<Gstr2BTimeStampDto> reconDataList = new ArrayList<>();

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
								.getProp("gstr2b.get.time.stamp.report.header");
						writer.append(invoiceHeadersTemplate);
						String[] columnMappings = commonUtility
								.getProp("gstr2b.get.time.stamp.report.column")
								.split(",");

						ColumnPositionMappingStrategy<Gstr2BTimeStampDto> mappingStrategy = new ColumnPositionMappingStrategy<>();
						mappingStrategy.setType(Gstr2BTimeStampDto.class);
						mappingStrategy.setColumnMapping(columnMappings);
						StatefulBeanToCsvBuilder<Gstr2BTimeStampDto> builder = new StatefulBeanToCsvBuilder<>(
								writer);
						StatefulBeanToCsv<Gstr2BTimeStampDto> beanWriter = builder
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
					LOGGER.error("Exception while executing the query for "
							+ "Report Type :{}", reportType, ex);

				}
				File fPathFile = new File(fullPath);

				uploadedDocName = DocumentUtility.uploadZipFile(fPathFile,
						ConfigConstants.GSTR2BREPORTS);
			}

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the query for "
					+ "Report Type :{}", reportType, ex);

		}

		deleteTemporaryDirectory(tempDir);
		return uploadedDocName;

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

	private Gstr2BTimeStampDto convert(Object[] arr) {

		Gstr2BTimeStampDto obj = new Gstr2BTimeStampDto();

		obj.setRecipientGSTIN((arr[1] != null) ? (arr[1].toString()) : null);

		Timestamp dt = (arr[0] != null) ? (Timestamp) arr[0] : null;

		String ldt = dt != null
				? "'".concat(EYDateUtil
						.toISTDateTimeFromUTC(dt.toLocalDateTime()).toString())
				: null;

		obj.setRecipientGSTIN((arr[1] != null) ? (arr[1].toString()) : null);
		obj.setTaxPeriod((arr[2] != null) ? (arr[2].toString()) : null);
		obj.setB2b(ldt);
		obj.setB2bAmendment(ldt);
		obj.setCreditOrDebitNotes(ldt);
		obj.setCreditOrDebitNoteAmendments(ldt);
		obj.setIsd(ldt);
		obj.setIsdAmendment(ldt);
		obj.setImpg(ldt);
		obj.setImpgsez(ldt);
		return obj;

	}

	private static File createTempDir() throws IOException {

		String tempFolderPrefix = "GSTR_2B_Time_Stamp_Report";
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

	private String querySting(List<String> gstins, int derivedStartPeriod,
			int derivedEndPeriod) {

		return "select CREATED_ON,GSTIN,RETURN_PERIOD from "
				+ " GETANX1_BATCH_TABLE WHERE "
				+ " GET_TYPE = 'GSTR2B_GET_ALL' AND GSTIN IN(:gstins) AND "
				+ " CONCAT(RIGHT(Return_Period,4),LEFT(Return_Period,2)) "
				+ " between :derivedStartPeriod AND :derivedEndPeriod "
				+ " AND IS_DELETE = FALSE " + " ORDER BY GSTIN,RETURN_PERIOD ";

	}
}
