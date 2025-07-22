package com.ey.advisory.gstr9.jsontocsv.processing.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.dashboard.mergefiles.APICallDashboardReportMerger;
import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.zip.DirAndFilesCompressor;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.processing.messages.MergeCsvFilesMessageMonthwise;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("CombineAndZipCsvsMonthwiseProcessor")
@Slf4j
public class CombineAndZipCsvsMonthwiseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("DirAndFilesCompressorImpl")
	private DirAndFilesCompressor compressor;

	@Autowired
	@Qualifier("DefaultAPICallDashboardReportMerger")
	private APICallDashboardReportMerger defaultReportMerger;

	@Autowired
	private ZipGenStatusRepository monthlyZipGenRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		MergeCsvFilesMessageMonthwise msg = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Extracting and validating the message.");
			}

			msg = extractAndValidateMessage(message);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Extracted and validated the message.");
			}
			String groupCode = message.getGroupCode();
			cleanUpExistingZipAndUpdateStatus(groupCode, msg);

			/*
			 * // Create the temp dir for downloading csvs and creating the zip
			 * tempFile = createTempDir(msg); // Create the zip file, get
			 * reference to the blob name String zipFileName =
			 * zipFilesToOutputDir(groupCode, msg, tempFile);
			 * 
			 * String folderName =
			 * DashboardCommonUtility.getDashboardFolderName(
			 * msg.getReturnType(), JobStatusConstants.ZIP_FILE);
			 * DocumentUtility.uploadFileWithFileName(new File(
			 * tempFile.getAbsolutePath() + File.separator + zipFileName),
			 * folderName, zipFileName);
			 */
			Triplet<String, String, String> triplet = new Triplet<>(
					msg.getGstin(), msg.getTaxPeriod(), msg.getReturnType());
			String zipFileName = defaultReportMerger
					.mergeReport(Arrays.asList(triplet), null);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Generated the Zip File - '%s'.",
						zipFileName));
			}

			// Update the control table with the zip generation info.
			updateZipGenStatus(zipFileName, msg, "ZIP_COMPLETED");

		} catch (Exception e) {

			if (msg != null)
				updateZipGenStatus(null, msg, "ZIP_FAILED");
			throw new AppException(
					"Error occurred during CombineAnd Zip Conversion.", e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private MergeCsvFilesMessageMonthwise extractAndValidateMessage(
			Message message) {

		MergeCsvFilesMessageMonthwise msg = JsonUtil.newGsonInstance(false)
				.fromJson(message.getParamsJson(),
						MergeCsvFilesMessageMonthwise.class);

		if (msg.getGstin() == null || msg.getReturnType() == null
				|| msg.getTaxPeriod() == null) {
			String errMsg = "Invalid FilePath Details received in msg";
			LOGGER.error(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Group Code: '%s'",
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}

		return msg;
	}

	private void cleanUpExistingZipAndUpdateStatus(String groupCode,
			MergeCsvFilesMessageMonthwise message) {
		String compressedFileName = getZipOutputFileName(message);
		String blobName = compressedFileName + ".zip";
		String returnType = message.getReturnType();
		deleteExistingFiles(groupCode, blobName, returnType);
		updateZipGenStatus(null, message, "ZIP_STARTED");
	}

	private String getZipOutputFileName(MergeCsvFilesMessageMonthwise message) {
		return new StringJoiner("_").add(message.getReturnType())
				.add(message.getGstin()).add(message.getTaxPeriod()).toString();
	}

	private void deleteExistingFiles(String groupCode, String docToBeDeleted,
			String returnType) {
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Before deleting the existing" + " ZIP blob : '%s'",
					docToBeDeleted);
			LOGGER.debug(logMsg);
		}
		String folderName = DashboardCommonUtility.getDashboardFolderName(
				returnType, JobStatusConstants.ZIP_FILE);

		try {
			boolean isDeleted = DocumentUtility.deleteDocument(docToBeDeleted,
					folderName);

			if (isDeleted)
				LOGGER.debug("Document deleted successfully, Doc name is : {}",
						docToBeDeleted);
			else
				LOGGER.debug("Document doesn't exist , Doc name is : {}",
						docToBeDeleted);
		} catch (Exception e) {
			String msg = "Exception occcured while deleting document";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}

	}

	private void updateZipGenStatus(String zipPath,
			MergeCsvFilesMessageMonthwise msg, String jobStatus) {

		// Update the Zip Generation status to the DB.
		int noOfRowsUpdated = monthlyZipGenRepo.updateMonthlyZipGenStatus(
				msg.getReturnType(), msg.getGstin(), msg.getTaxPeriod(),
				zipPath, LocalDateTime.now(), jobStatus);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format(
							"Updated the Monthly Zip Gen Status Table. "
									+ "No Of Rows updated = %d",
							noOfRowsUpdated));
		}
	}

	private File createTempDir(MergeCsvFilesMessageMonthwise msg)
			throws IOException {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss-SSS");
		String suffixStr = now.format(formatter);
		String returnType = msg.getReturnType();
		String taxPeriod = msg.getTaxPeriod();
		String gstin = msg.getGstin();
		String tempFolderPrefix = returnType + "_" + gstin + "_" + taxPeriod
				+ "_" + suffixStr;
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private String zipFilesToOutputDir(String groupCode,
			MergeCsvFilesMessageMonthwise message, File tempDir)
			throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(message, tempDir,
				groupCode);

		String compressedFileName = getZipOutputFileName(message);
		// Compress the files and write the zip file tothe destination.
		compressor.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);

		String zipFileName = compressedFileName + ".zip";

		// Return the file name of the zip file.
		return zipFileName;
	}

	/**
	 * Get the list of all files to be zipped, for the specified gstin, invoice
	 * type and csv combination.
	 * 
	 * @return
	 */
	private List<String> getAllFilesToBeZipped(
			MergeCsvFilesMessageMonthwise message, File csvDir,
			String groupCode) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Getting all the full file paths "
					+ "for a tax periods  and all invoiceTypes"
					+ " creating the zip"));
		}

		List<String> invTypes = getRelevantInvoiceTypes(
				message.getReturnType());

		// Get the possible list of files that can be downloaded from the Doc
		// Repo

		List<String> filesToDownload = new ArrayList<>();

		invTypes.forEach(section -> filesToDownload
				.add(createDocumentName(message, section)));

		downloadCsvFilesFromDocRepoToTmpDir(groupCode, filesToDownload, csvDir,
				message);

		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		};
		// Create the full path of all the files to be read. If some of
		// these files are not present, while merging, generate a warning
		// message and proceed. There can be scenarios where there are no
		// invoices genrated for a specific type during a tax period.
		File[] files = csvDir.listFiles(csvFilter);

		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Obtained all the full file paths "
					+ " and" + "  for creating the zip"));
		}
		// Return the list of files.
		return retFileNames;
	}

	private List<String> getRelevantInvoiceTypes(String returnType) {
		final List<String> gstr1Types = ImmutableList.of("B2B", "B2CL", "B2CS",
				"B2BA", "NIL_RATED", "CDNR", "CDNRA", "TXP", "AT", "EXP",
				"CDNUR", "B2CSA", "B2CLA", "EXPA", "ATA", "TXPA", "CDNURA",
				"HSN_SUMMARY", "DOC_ISSUE");

		// For GSTR 3b there'll be only one json. But we cretae 2 separate
		// csv files out out of it. When we do this we can use 3b1 and 3b2 as
		// invoice types, so that the zipping process can happen correctly.
		final List<String> gstr3bTypes = ImmutableList.of("taxPayable",
				"summary");

		final List<String> gstr2aTypes = ImmutableList.of("B2B", "B2BA", "CDN",
				"CDNA", "ISD");

		switch (returnType.toUpperCase()) {
		case "GSTR1":
			return gstr1Types;
		case "GSTR3B":
			return gstr3bTypes;
		case "GSTR2A":
			return gstr2aTypes;
		default: {
			String msg = String.format(
					"Invalid GSTR Invoice Type encountered: " + "'%s'",
					returnType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		}
	}

	private String createDocumentName(MergeCsvFilesMessageMonthwise message,
			String section) {
		String fileName = new StringJoiner("_").add(message.getReturnType())
				.add(section).add(message.getTaxPeriod())
				.add(message.getGstin()).toString();
		return fileName + ".csv";
	}

	private void downloadCsvFilesFromDocRepoToTmpDir(String groupCode,
			List<String> fileNames, File csvDir,
			MergeCsvFilesMessageMonthwise message) throws Exception {
		// Download all the CSVs specified from the Doc Repo to the temp
		// directory.

		String returnType = message.getReturnType();
		String folderName = DashboardCommonUtility.getDashboardFolderName(
				returnType, JobStatusConstants.CSV_FILE);

		for (String fileName : fileNames) {

			downloadCsvFileToTmpFileFromDocRepo(csvDir, folderName, fileName);

		}

	}

	private void downloadCsvFileToTmpFileFromDocRepo(File tmpDir,
			String folderName, String fileName) {

		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Before downloading the existing"
							+ " csv file : '%s' from container : %s",
					fileName, folderName);
			LOGGER.debug(logMsg);
		}

		File tmpCsvFile = new File(tmpDir.toPath() + File.separator + fileName);

		try {
			Document document = DocumentUtility.downloadDocument(fileName,
					folderName);

			if (document == null) {
				LOGGER.error(
						"Document doesn't exist, doc Name {} , folderName {}",
						fileName, folderName);
				return;
			}

			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), tmpCsvFile);

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Downloaded the document : '%s'"
								+ " Successfully from the folder : '%s'",
						fileName, folderName);
				LOGGER.debug(logMsg);
			}
		} catch (Exception e) {
			String msg = "Exception ocured while downloading csv";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

}
