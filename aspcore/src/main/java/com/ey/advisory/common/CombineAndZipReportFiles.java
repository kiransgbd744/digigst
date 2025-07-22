package com.ey.advisory.common;

/**
 * @author Arun.KA
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

import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CombineAndZipReportFiles")
public class CombineAndZipReportFiles {

	public String zipfolder(File tempFile, String reportType, String status,
			Long id) {
		String zipFileName = "";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to start zipping ");
			}

			// Create the temp dir for downloading csvs and creating the zip
			zipFileName = zipFilesToOutputDir(tempFile, reportType, status, id);
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
			String status, Long id) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Generated files for zipping: %s",
					filesToZip);
			LOGGER.debug(msg);
		}

		String compressedFileName = getZipOutputFileName(reportType, status,
				id);

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
				return name.toLowerCase().endsWith(".csv")
						|| name.toLowerCase().endsWith(".xlsx");
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

	private static String getZipOutputFileName(String reportType, String status,
			Long id) {

		String fileName = null;
		try {
			String status1 = null;
			if (!Strings.isNullOrEmpty(status)) {

				if (status.equalsIgnoreCase("active")) {
					status1 = "Active";
				} else if (status.equalsIgnoreCase("inactive")) {
					status1 = "Inactive";
				}
			}
			switch (reportType) {

			case ReportTypeConstants.ERROR_BV:
				reportType = "Business Validation ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1 + "_" + id;
				break;

			case ReportTypeConstants.ERROR:
				reportType = "Error ";
				if (!Strings.isNullOrEmpty(status1))
					reportType += status1 + "_" + id;
				break;

			case ReportTypeConstants.PROCESSED_RECORDS:
				if (!Strings.isNullOrEmpty(status1))
					reportType = "Processed" + status1 + "Records" + "_" + id;
				break;

			case ReportTypeConstants.ERROR_SV:
				reportType = "Structural Validation" + "_" + id;
				break;

			case ReportTypeConstants.TOTAL_ERRORS:
				reportType = "Total Errors" + "_" + id;
				break;

			case ReportTypeConstants.TOTAL_RECORDS:
				reportType = "Total Records" + "_" + id;
				break;

			case ReportTypeConstants.AS_UPLOADED:
				reportType = "Gstr1 Processed Transactional Records" + "_" + id;
				break;

			case ReportTypeConstants.VENDOR_COMM:
				reportType = "Vendor Comm Report" + "_" + id;
				break;

			case ReportTypeConstants.EINVPROCESS:
				reportType = "EinvProcessed" + "_" + id;
				break;

			case ReportTypeConstants.EINVCONSOLIDATED:
				reportType = "Einv Consolidated" + "_" + id;
				break;

			case ReportTypeConstants.EINV_NOT_APPLICABLE:
				reportType = "Einv Not Applicable" + "_" + id;
				break;

			case ReportTypeConstants.EINV_APPLICABLE:
				reportType = "Einv Applicable" + "_" + id;
				break;
			case ReportTypeConstants.EINV_IRN_GENERATE:
				reportType = "Einv Irn Generate" + "_" + id;
				break;
			case ReportTypeConstants.EINV_IRN_CANCELED:
				reportType = "Einv IRN Canceled" + "_" + id;
				break;
			case ReportTypeConstants.EINV_ERROR:
				reportType = "Einv Error" + "_" + id;
				break;
			case ReportTypeConstants.EINV_ERROR_FROM_IRP:
				reportType = "Einv Error From IRP" + "_" + id;
				break;
			case ReportTypeConstants.NON_COMPLAINT_COM:
				reportType = "NonCompliant Report" + "_" + id;
				break;

			case ReportTypeConstants.COMPLAIN_HISTORY:
				reportType = "Complaince History Report" + "_" + id;
				break;

			case ReportTypeConstants.REV_RESP180_PROCESSED:
				reportType = "180 days Reversal_Response_Processed Records";
				break;
			case ReportTypeConstants.REV_RESP180_TOTAL:
				reportType = "180 days Reversal_Response_Total Records";
				break;
			case ReportTypeConstants.REV_RESP180_ERROR:
				reportType = "180 days Reversal_Response_Error Records";
				break;
			case ReportTypeConstants.GSTR9_DIGIGST_COMPUTE:
				reportType = ReportTypeConstants.GSTR9_DIGIGST_COMPUTE;
				break;

			case "GSTR8A":
				reportType = "GSTR8A" + "_" + id;
				break;
			case ReportTypeConstants.GSTR3B_180DAYS_REV_RESP:
				reportType = "180 Days Reversal response report";
				break;
			case ReportTypeConstants.VENDOR_COMM_2BPR:
				reportType = "Vendor_Comm_2BPR";
				break;

			case ReportTypeConstants.GSTR6_ISD_ANNEX:
				reportType = "Credit Distribution Annexure Report";
				break;

			case ReportTypeConstants.DAYS180_TOTAL:
				reportType = "180DaysPaymentReference_TotalRecords" + "_" + id;
				break;
			case ReportTypeConstants.DAYS180_ERROR:
				reportType = "180DaysPaymentReference_ErrorRecords" + "_" + id;
				break;
			case ReportTypeConstants.DAYS180_PROCESSED:
				reportType = "180DaysPaymentReference_ProcessedRecords" + "_"
						+ id;
				break;
			case "GSTR1HSNSummaryReport":
				reportType = "GSTR1HSNSummaryReport";
				break;
			case ReportTypeConstants.ITC_REVERSAL_RULE_42 + "_Inward":
				reportType = "Transactional_Inwa"
						+ "rd_ITC_Reversal_Rule_42_Report" + "_" + id;
				break;
			case ReportTypeConstants.ITC_REVERSAL_RULE_42 + "_Outward":
				reportType = "Transactional_Outward_ITC_Reversal_Rule_42_Report"
						+ "_" + id;
				break;
			case ReportTypeConstants.STOCK_TRACKING_REPORT + "_"
					+ APIConstants.ITC_04:
				reportType = "ITC04_Stock_Tracking_Report" + "_" + id;
				break;
			case APIConstants.GSTR8 + "_"
					+ ReportTypeConstants.GSTR8_PROCESSED_REPORT:
				reportType = "GSTR8_Processed_Records" + "_" + id;
				break;
			default:
				reportType = reportType;

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
