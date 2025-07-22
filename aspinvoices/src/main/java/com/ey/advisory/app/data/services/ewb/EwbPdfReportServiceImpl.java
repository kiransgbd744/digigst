/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CombineAndZipXlsxFiles;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Arun.KA
 *
 */
@Component("EwbPdfReportServiceImpl")
@Slf4j
public class EwbPdfReportServiceImpl implements EwbPdfReportService {

	@Autowired
	@Qualifier("EwbPDFGenerationReportImpl")
	EwbPDFGenerationReport ewbPDFGenerationReport;

	@Autowired
	@Qualifier("EwbSummaryPDFGenerationReportImpl")
	EwbSummaryPDFGenerationReport ewbSummaryPDFGenerationReport;

	public String generateEwbDetailedPdfZip(File tempDir,
			List<String> ewbNoList) {

		for (String ewbNo : ewbNoList) {

			String fileName = "EwayBillDetailedReport_" + ewbNo;

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".pdf";
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {

				JasperPrint jasperPrint = ewbPDFGenerationReport
						.generatePdfReport(ewbNo);

				JasperExportManager.exportReportToPdfStream(jasperPrint,
						outStream);

				outStream.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String zipFileName = zipEwbPdfFiles(tempDir, "EwayBillDetailedPdfReports");
		return zipFileName;
	}

	@Override
	public String generateEwbSummaryPdfZip(File tempDir,
			List<String> ewbNoList) {
		for (String ewbNo : ewbNoList) {

			String fileName = "EwayBillSummaryReport_" + ewbNo;

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".pdf";
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {

				JasperPrint jasperPrint = ewbSummaryPDFGenerationReport
						.generateSummaryPdfReport(ewbNo);

				JasperExportManager.exportReportToPdfStream(jasperPrint,
						outStream);

				outStream.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String zipFileName = zipEwbPdfFiles(tempDir, "EwayBillSummaryPdfReports");
		return zipFileName;
	}

	private String zipEwbPdfFiles(File tempDir, String fileName) {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String compressedFileName = fileName;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("compressedFileName : %s",
					compressedFileName);
			LOGGER.debug(msg);
		}

		CombineAndZipXlsxFiles.compressFiles(tempDir.getAbsolutePath(),
				compressedFileName + ".zip", filesToZip);
		String zipFileName = compressedFileName + ".zip";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("zipFileName : %s", zipFileName);
			LOGGER.debug(msg);
		}
		return zipFileName;

	}

	private static List<String> getAllFilesToBeZipped(File tmpDir) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Temporary directory containing files to "
							+ "be zipped: %s", tmpDir.getAbsolutePath());
			LOGGER.debug(msg);
		}
		FilenameFilter pdfFilter = new FilenameFilter() {
			public boolean accept(File tmpDir, String name) {
				return name.toLowerCase().endsWith(".pdf");
			}
		};
		File[] files = tmpDir.listFiles(pdfFilter);
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

}
