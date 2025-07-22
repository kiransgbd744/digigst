package com.ey.advisory.common;

/**
 * @author vishal.verma
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CombineAndZipCsvFiles")
public class CombineAndZipCsvFiles {

	public String zipfolder(Long configId, File tempFile, String reportName) {
		String zipFileName = "";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to start zipping for configId {}",
						configId);
			}

			// Create the temp dir for downloading csvs and creating the zip
			zipFileName = zipFilesToOutputDir(tempFile, configId, reportName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Generated the Zip File - '%s'.",
						zipFileName));
			}

		} catch (Exception e) {
			throw new AppException(
					"Error occurred during CombineAnd Zip Conversion.", e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

		return zipFileName;
	}

	private static String zipFilesToOutputDir(File tempDir, Long configId,
			String reportName) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Generated files for zipping: %s",
					filesToZip);
			LOGGER.debug(msg);
		}

		String compressedFileName = getZipOutputFileName(configId, reportName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}
		// Compress the files and write the zip file to the destination.
		compressFiles(tempDir.getAbsolutePath(), compressedFileName + ".zip",
				filesToZip);

		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}

		// Return the file name of the zip file.
		return zipFileName;
	}

	/**
	 * Get the list of all files to be zipped
	 * 
	 * @return
	 */
	private static List<String> getAllFilesToBeZipped(File csvDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", csvDir.getAbsolutePath());
			LOGGER.debug(msg);
		}

		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File xlsxDir, String name) {
				if (name.toLowerCase().endsWith(".csv")) {
					return name.toLowerCase().endsWith(".csv");
				} else {
					return name.toLowerCase().endsWith(".txt");
				}

			}
		};

		File[] files = csvDir.listFiles(csvFilter);
		List<String> retFileNames = Arrays.stream(files)
				.map(f -> f.getAbsolutePath())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List of files to be zipped %s",
					retFileNames);
			LOGGER.debug(msg);
		}
		// Return the list of files.
		return retFileNames;
	}

	private static String getZipOutputFileName(Long configId,
			String reportName) {
		if (reportName.contains("(Recon Result)")) {
			return reportName + "_" + configId;
		} else if (reportName.equalsIgnoreCase("Detailed_Line_Item")) {
			return "Inward E-invoice_Detailed (Line Item) Report" + "_"
					+ configId;
		}else if (reportName.contains("Inward E-invoice_Summary Report")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "Inward E-invoice_Summary Report" + "_"
					+ configId + "_" + index;
		}else if (reportName.contains("Inward E-invoice_Detailed (Invoice Level) Report")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "Inward E-invoice_Detailed (Invoice Level) Report" + "_"
					+ configId + "_" + index;
		} else if (reportName.contains("IMS Records Report")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "IMS Records Report" + "_" + index;
		}else if(reportName.contains("AIM_ReconReport")) {
			return reportName;
		} else if (reportName.contains("IMS Records Active Report")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "IMS Records Active Report" + "_" + index;
		} else if (reportName.contains("IMS Records Active+Inactive Report")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "IMS Records Active+Inactive Report" + "_" + index;
		}else if (reportName.contains("Supplier IMS_ConsolidatedReportActionWise")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return "Supplier IMS_ConsolidatedReportActionWise" + "_" + index;
		}  else if (reportName.contains("GSTR2B")) {
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			return reportName + configId + "_" + index;
		}else if (reportName.contains("B2B_SECTION4_SECTION6B_SECTION6C")) {
			
			int index = Arrays.stream(reportName.split("_")).map(String::trim)
					.reduce((first, second) -> second).map(Integer::parseInt)
					.orElseThrow(() -> new IllegalArgumentException(
							"Unable to extract a valid index"));
			
		
			    String[] tokens = reportName.split("_");
			    String lastToken = tokens[tokens.length - 1].trim();

			    boolean endsWithIndex = false;
			    try {
			        Integer.parseInt(lastToken);
			        endsWithIndex = true;
			    } catch (NumberFormatException e) {
			        endsWithIndex = false;
			    }

			    String baseName;
			    if (endsWithIndex) {
			        // Remove the trailing index token
			        baseName = String.join("_", Arrays.copyOf(tokens, tokens.length - 1));
			    } else {
			        baseName = reportName;
			    }

			    // Append new index
			    return baseName + "_" + index;
			

			
		}
		else if(reportName.contains("Detailed Report"))
		{
			return "GSTNNotices" + "_" + "DetailedDownloadReport" + "_" + configId;
			
		}
		
		else {
			return "ReconReports" + "_" + reportName + "_" + configId;
		}

	}

	public static void compressFiles(String outputDir,
			String compressedFileName, List<String> inputFiles) {

		final int ZIP_BUFFER_SIZE = 1024;

		// Check if each of the files in the specified file paths exist,
		// Otherwise throw an exception.
		List<String> filePaths = validateInputs(outputDir, inputFiles);

		// Create the full output file path.
		String outFilePath = outputDir + File.separator + compressedFileName;

		byte[] buffer = new byte[ZIP_BUFFER_SIZE];

		try (FileOutputStream fos = new FileOutputStream(outFilePath);
				ZipOutputStream zos = new ZipOutputStream(fos);) {

			// Iterate over the specified individual files and write them
			// the zip output stream.
			for (String aFile : filePaths) {

				File srcFile = new File(aFile);

				// Open an input stream to the file to be read for zipping.
				FileInputStream fis = new FileInputStream(srcFile);
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				// Close the file entry within the zip output stream.
				zos.closeEntry();
				fis.close(); // Close the intermediate zip file stream.
			}
		} catch (IOException ex) {
			String msg = String.format(
					"IO Error while creating the compressed file '%s'",
					outFilePath);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
	}

	/**
	 * Filters the input list for valid paths.
	 * 
	 * @param outputDir
	 * @param filePaths
	 * @return
	 */
	private static List<String> validateInputs(String outputDir,
			List<String> filePaths) {

		// check if the output directory exists. Otherwise throw an exception.
		File outDir = new File(outputDir);
		if (!outDir.exists() || !outDir.isDirectory()) {
			String msg = String.format("The output dir '%s' does not exist "
					+ "OR is not a directory", outputDir);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		if (filePaths.isEmpty()) {
			String msg = "One or more files are required as input for "
					+ "compressing";
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		// Get all the valid files within the list of input files.
		List<String> validFilePaths = filePaths.stream()
				.map(filePath -> new File(filePath))
				.filter(file -> file.exists() && file.isFile())
				.map(file -> file.getPath())
				.collect(Collectors.toCollection(ArrayList::new));

		// Print an error to display number of invalid files.
		if (validFilePaths.size() != filePaths.size()) {
			String msg = String.format(
					"%d file(s) specified as input for "
							+ "creating the zip do(es) not exist.",
					filePaths.size() - validFilePaths.size());
			LOGGER.warn(msg);

		}

		return validFilePaths;
	}
	
/*
	public static void main(String[] args) {
		String reportName = "Condolidated 2A-6A/PR_(Recon Result)";
		if (reportName.contains("(Recon Result)")) {
			System.out.println(reportName);
		}
	}*/
}
