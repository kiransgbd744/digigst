package com.ey.advisory.gstr9.jsontocsv.processing.tasks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.processing.messages.JsonFileArrivalMessage;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Component("CsvConvSapDocRepoStrategy")
@Scope("prototype")
@Slf4j
public class CsvConvSapDocRepoStrategy
		implements CsvConversionFileSystemStrategy {

	private static final String GSTR3B_SUMMARY_TYPE = "summary";
	private static final String GSTR3B_TAXPAYABLE_TYPE = "taxPayable";
	private static final String GSTR9_OUTINWARD_TYPE = "outInWard";
	private static final String GSTR9_TAXPAID_TYPE = "taxPaid";
	private static final String SUMMARY_REPORT = "Summary";
	private static final String DETAILED_REPORT = "Detailed";
	private static final String GSTR9_HSN_DETAILS = "hsnDetails";

	private List<String> csvAbsolutePaths;

	private String jsonAbsolutePath;

	@Autowired
	private GstinGetStatusRepository gstinStatusRepository;

	@Override
	public void performPreProcessing(JsonFileArrivalMessage msg) {

		String returnType = msg.getReturnType();

		String folderName = DashboardCommonUtility.getDashboardFolderName(
				returnType, JobStatusConstants.CSV_FILE);

		String[] fileNamesToBeDeleted = prepareFileNamesToBeUploaded(msg);
		for (String fileNameToBeDeleted : fileNamesToBeDeleted) {

			try {
				DocumentUtility.deleteDocument(fileNameToBeDeleted, folderName);
			} catch (Exception e) {
				String message = "Exception occured while deleting document "
						+ "from csv folder";
				LOGGER.error(message);
				throw new AppException(message, e);
			}

		}

	}

	@Override
	public List<? extends Object> listInputJsons(JsonFileArrivalMessage msg) {
		String jsonFilePrefix = getFilePrefix(msg);
		int fileNo = 0;
		String folderName = null;
		List<Document> jsonFiles = new ArrayList<>();
		String returnType = msg.getReturnType();

		folderName = DashboardCommonUtility.getDashboardFolderName(returnType,
				JobStatusConstants.JSON_FILE);

		while (true) {

			String fileName = jsonFilePrefix + fileNo + ".json";

			try {
				Document doc = DocumentUtility.downloadDocument(fileName,
						folderName);

				if (doc == null)
					break;

				jsonFiles.add(doc);
				fileNo++;
			} catch (Exception e) {
				String message = "Exception occured while reading json files from "
						+ "doc repository";
				throw new AppException(message, e);
			}
		}

		return jsonFiles;
	}

	@Override
	public List<BufferedWriter> createOutputCsvWriters(int writersCount,
			JsonFileArrivalMessage msg) {
		List<BufferedWriter> csvWritersList = new ArrayList<BufferedWriter>();
		csvAbsolutePaths = new ArrayList<>();
		for (int i = 0; i < writersCount; i++) {
			String tempFileName = generateTmpFileName(getFilePrefix(msg));
			try {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Before Creating the Temp Csv FileName : '%s'",
							tempFileName);
					LOGGER.debug(logMsg);
				}
				File tmpCsvFile = Files
						.createTempFile(tempFileName, "_" + i + "tmp.csv")
						.toFile();
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"After Creating the Temp Csv FileName : '%s',"
									+ " About to create Merged CSV writer ",
							tmpCsvFile.getName());
					LOGGER.debug(logMsg);
				}
				csvWritersList.add(new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpCsvFile), "UTF-8")));
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Created create Merged CSV writer ",
							tmpCsvFile.getName());
					LOGGER.debug(logMsg);
				}
				csvAbsolutePaths.add(tmpCsvFile.getAbsolutePath());

			} catch (Exception e) {
				String errMsg = String.format(
						"Error while creating the temporary"
								+ " Merged CSV file, FileName is '%s'",
						tempFileName);
				LOGGER.error(errMsg, e);
				throw new AppException(errMsg, e);
			}
		}
		return csvWritersList;
	}

	@Override
	public void flushOutputWriters(List<BufferedWriter> writers) {
		try {
			for (BufferedWriter writer : writers) {
				writer.flush();
			}
		} catch (IOException ex) {
			String errMsg = String
					.format("Failed to flush one or more Buffered writers");
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);
		}
	}

	@Override
	public JsonReader createJsonReader(Object fileHandle) {
		Document document = (Document) fileHandle;
		String docName = document.getName();
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Creating the reader" + " for Blob Name is '%s'", docName);
			LOGGER.debug(logMsg);
		}
		String tempFileName = generateTmpFileName(docName);
		try {
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Before Creating the Temp Json FileName : '%s'",
						tempFileName);
				LOGGER.debug(logMsg);
			}
			File tmpJsonFile = Files.createTempFile(tempFileName, "_tmp.json")
					.toFile();

			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), tmpJsonFile);
			
			jsonAbsolutePath = tmpJsonFile.getAbsolutePath();
			BufferedReader jsonReader = new BufferedReader(
					new FileReader(tmpJsonFile));
			return new JsonReader(jsonReader);
			

		} catch (IOException e) {
			String errMsg = String.format("Error while Reading the Json File"
					+ " From Blob, FilePath is '%s'", fileHandle);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}

	@Override
	public void cleanupInputJsonReader(JsonReader reader) {
		try {
			reader.close();
			File jsonTempFile = new File(jsonAbsolutePath);
			if (jsonTempFile.exists()) {
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Temporary Json File '%s' Deleted Successfully",
							jsonTempFile.getName());
					LOGGER.debug(logMsg);
				}
				boolean isDeleted = jsonTempFile.delete();
				if (!isDeleted) {
				    LOGGER.error("Failed to delete temporary file: {}", jsonTempFile.getAbsolutePath());
				} else {
				    LOGGER.info("Temporary file deleted successfully: {}", jsonTempFile.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			String errMsg = String
					.format("Error while closing the Json Reader");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
	}

	@Override
	public boolean cleanupOutputCsvWriters(List<BufferedWriter> writers) {
		try {
			for (BufferedWriter writer : writers) {
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			String errMsg = String
					.format("Error while closing the Buffered writer");
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
		return true;

	}

	@Override
	public void performPostProcessingOnSuccess(JsonFileArrivalMessage msg) {

		String folderName = null;
		String returnType = msg.getReturnType();

		folderName = DashboardCommonUtility.getDashboardFolderName(returnType,
				JobStatusConstants.CSV_FILE);

		String[] fileNamesToBeUploaded = prepareFileNamesToBeUploaded(msg);
		int i = 0;
		for (String fileNameToBeUploaded : fileNamesToBeUploaded) {

			String filePath = csvAbsolutePaths.get(i++);

			DocumentUtility.uploadFileWithFileName(new File(filePath),
					folderName, fileNameToBeUploaded);

		}
		gstinStatusRepository.updateCsvGenerationStatus(true,
				LocalDateTime.now(), msg.getGstin(), msg.getTaxPeriod(),
				msg.getReturnType(), msg.getInvoiceType());
	}

	@Override
	public void performPostProcessingOnError() {
		for (String csvAbsolutePath : csvAbsolutePaths) {
			File csvTempFile = new File(csvAbsolutePath);
			if (csvTempFile.exists()) {
				boolean isDeleted = csvTempFile.delete();
				if (LOGGER.isDebugEnabled()) {
					String logMsg = String.format(
							"Temporary CSV File '%s' Deleted Successfully",
							csvTempFile.getName());
					if (!isDeleted) {
					    LOGGER.error("Failed to delete temporary file: {}", csvTempFile.getName());
					} else {
					    LOGGER.info("Temporary file deleted successfully: {}", csvTempFile.getName());
					}

					LOGGER.debug(logMsg);
				}
			}
		}
	}

	private String getFilePrefix(JsonFileArrivalMessage msg) {
		return msg.getReturnType() + "_" + msg.getGstin() + "_"
				+ msg.getTaxPeriod() + "_" + msg.getInvoiceType() + "_";
	}

	private String[] prepareFileNamesToBeUploaded(JsonFileArrivalMessage msg) {
		String returnType = msg.getReturnType();
		String invoiceType = msg.getInvoiceType();
		String taxPeriod = msg.getTaxPeriod();
		String gstin = msg.getGstin();
		if (returnType.equals("GSTR3B")) {
			return new String[] {
					returnType + "_" + GSTR3B_SUMMARY_TYPE + "_" + taxPeriod
							+ "_" + gstin + ".csv",
					returnType + "_" + GSTR3B_TAXPAYABLE_TYPE + "_" + taxPeriod
							+ "_" + gstin + ".csv" };
		} else if (returnType.equals("GSTR9")) {
			return new String[] {
					returnType + "_" + GSTR9_OUTINWARD_TYPE + "_" + taxPeriod
							+ "_" + gstin + ".csv",
					returnType + "_" + GSTR9_TAXPAID_TYPE + "_" + taxPeriod
							+ "_" + gstin + ".csv",
					returnType + "_" + GSTR9_HSN_DETAILS + "_" + taxPeriod + "_"
							+ gstin + ".csv" };

		} else if (returnType.equals("GSTR2X")) {
			return new String[] {
					returnType + "_" + SUMMARY_REPORT + "_" + taxPeriod + "_"
							+ gstin + ".csv",
					returnType + "_" + DETAILED_REPORT + "_" + taxPeriod + "_"
							+ gstin + ".csv" };

		} else
		{
			return new String[] { returnType + "_" + invoiceType + "_"
					+ taxPeriod + "_" + gstin + ".csv" };
		}
	}

	private String generateTmpFileName(String FileName) {
		// Get the current time stamp string to attach to the file
		// upto millisecond precision.
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss-SSS");
		String suffixStr = now.format(formatter);
		String tmpFileName = StringUtils.strip(FileName.concat(suffixStr));
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format("Generated Temp FileName '%s'",
					tmpFileName);
			LOGGER.debug(logMsg);
		}
		return tmpFileName;
	}

}
