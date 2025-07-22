package com.ey.advisory.common;

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
@Component("CombinAndZipReportFiles")
public class CombinAndZipReportFiles {

	public String zipfolder(File tempFile, String reportType, Long id) {
		String zipFileName = "";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to start zipping ");
			}

			// Create the temp dir for downloading csvs and creating the zip
			zipFileName = zipFilesToOutputDir(tempFile, reportType, id);
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

	private static String zipFilesToOutputDir(File tempDir, String reportType,
			Long id) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Generated files for zipping: %s",
					filesToZip);
			LOGGER.debug(msg);
		}

		String compressedFileName = getZipOutputFileName(reportType, id);

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
	private static List<String> getAllFilesToBeZipped(File xlsxDir)
			throws Exception {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", xlsxDir.getAbsolutePath());
			LOGGER.debug(msg);
		}

		FilenameFilter csvFilter = new FilenameFilter() {
			public boolean accept(File xlsxDir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		};

		File[] files = xlsxDir.listFiles(csvFilter);

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

	private static String getZipOutputFileName(Long id) {

		String fileName = "InvoiceManangement" + "_" + id;
		return fileName;
	}

	private static String getZipOutputFileName(String reportType, Long id) {

		String fileName = null;
		try {
			switch (reportType) {

			case ReportTypeConstants.AS_UPLOADED:
				reportType = "GSTR1 Processed Transactional Records" + "_" + id;
				break;

			case ReportTypeConstants.ASPERROR:
				reportType = "GSTR1 Consolidated Asp Error" + "_" + id;
				break;

			case ReportTypeConstants.GSTNERROR:
				reportType = "GSTR1 Consolidated Gstn Error" + "_" + id;
				break;
				
			case ReportTypeConstants.GSTR1A_AS_UPLOADED:
				reportType = "GSTR1A Processed Transactional Records" + "_" + id;
				break;

			case ReportTypeConstants.GSTR1A_ASPERROR:
				reportType = "GSTR1A Consolidated Asp Error" + "_" + id;
				break;

			case ReportTypeConstants.GSTR1A_GSTNERROR:
				reportType = "GSTR1A Consolidated Gstn Error" + "_" + id;
				break;
				
			case ReportTypeConstants.GSTR1ENTITYLEVEL:
				reportType = "GSTR1 EntityLevel Summary" + "_" + id;
				break;
				
			case ReportTypeConstants.GSTR1AENTITYLEVEL:
				reportType = "GSTR1A EntityLevel Summary" + "_" + id;
				break;


			case ReportTypeConstants.GSTR2PROCESS:
				reportType = "PR Processed Records" + "_" + id;
				break;

			case ReportTypeConstants.GSTR6PROCESS:
				reportType = "GSTR6 Processed Records" + "_" + id;
				break;

			case ReportTypeConstants.GSTR1ENVPROCESS:
				reportType = "GSTR1_E_Invoice_Records" + "_" + id;
				break;

			case ReportTypeConstants.INWARD:
				reportType = "GSTR3B Inward Report" + "_" + id;
				break;

			case ReportTypeConstants.OUTWARD:
				reportType = "GSTR3B Outward Report" + "_" + id;
				break;
			case "GSTR-2A (Compete)":
				reportType = "ConsolidatedGstr2A_report" + "_" + id;
				break;
			case "RET N":
				reportType = "RET Not Applicable" + "_" + id;
				break;
			case "RET P":
				reportType = "RET Processed" + "_" + id;
				break;
			case "RET I":
				reportType = "RET Information" + "_" + id;
				break;
			case "RET E":
				reportType = "RET Errors" + "_" + id;
				break;
			case "RET A":
				reportType = "RET Applicable" + "_" + id;
				break;

			case "EWB N":
				reportType = "EWB Not Applicable" + "_" + id;
				break;
			case "EWB C":
				reportType = "EWB Cancelled" + "_" + id;
				break;
			case "EWB EN":
				reportType = "EWB Errors from NIC" + "_" + id;
				break;
			case "EWB E":
				reportType = "EWB Errors" + "_" + id;
				break;
			case "EWB A":
				reportType = "EWB Applicable" + "_" + id;
				break;
			case "EWB G":
				reportType = "EWB Generated" + "_" + id;
				break;

			case "GSTR-2A (popUp)":
				reportType = "Gstr2A_report" + "_" + id;
				break;
				
			case "ProcessedRecords":
				reportType = "InvoiceMgt_ProcessedRecords" + "_" + id;
				break;
				
			case "ErrorRecords":
				reportType = "InvoiceMgt_ErrorRecords" + "_" + id;
				break;
				
			case "ProcessedInfoData":
				reportType = "InvoiceMgt_ProcessedInfoData" + "_" + id;
				break;
				
			case "data":
				reportType = "InvoiceMgt_data" + "_" + id;
				break;
				
			case "Gstr1Adata":
				reportType = "InvoiceMgt_Gstr1Adata" + "_" + id;
				break;
				
			case "InProcRecords":
				reportType = "Inward_InvoiceMgt_ProcessedRecords" + "_" + id;
				break;
				
			case "InErrRecords":
				reportType = "Inward_InvoiceMgt_ErrorRecords" + "_" + id;
				break;
				
			case "InProcInfoData":
				reportType = "Inward_InvoiceMgt_ProcessedInfoData" + "_" + id;
				break;
				
			case "InwardData":
				reportType = "Inward_InvoiceMgt_data" + "_" + id;
				break;
				
			case ReportTypeConstants.STOCK_TRANSFER:
				reportType = "Stock_Transfer" + "_" + id;
				break;

			case ReportTypeConstants.GSTR6_ISD_ANNEX:
				reportType = "Credit Distribution Annexure Report";
				break;
				
			case ReportTypeConstants.SHIPPING_BILL:
				reportType = "Missing Shipping Bill" + "_" + id;
				break;
			
			case ReportTypeConstants.GSTR1ASHIPPING_BILL:
				reportType = "GSTR1A Missing Shipping Bill" + "_" + id;
				break;
				
			case "GSTR6Process":
				reportType = "GSTR6 Processed Records" + "_" + id;
				break;
				
			case "PRProcessedRecords":
				reportType = "GSTR2_GSTR6 Processed Records" + "_" + id;
				break;	
				
			case ReportTypeConstants.GSTR3B_Table4_Transactional:
				reportType = "GSTR3B Table4 Transactional Report";
				break;
				
			default:
				reportType =  reportType+ "_"+ id;

			}
		} catch (Exception e) {
			LOGGER.error("Invalid report type");
		}

		return reportType;

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

	public String zipfolder(File tempFile, Long id) {
		String zipFileName = "";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to start zipping ");
			}

			// Create the temp dir for downloading csvs and creating the zip
			zipFileName = zipFilesToOutputDir(tempFile, id);
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

	private static String zipFilesToOutputDir(File tempDir, Long id)
			throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Generated files for zipping: %s",
					filesToZip);
			LOGGER.debug(msg);
		}

		String compressedFileName = getZipOutputFileName(id);

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
}
