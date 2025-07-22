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

import com.ey.advisory.app.docs.dto.PdfPrintReqDto;
import com.ey.advisory.common.CombineAndZipXlsxFiles;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Component("ServiceTaxService")
@Slf4j
public class ServiceTaxService {

	@Autowired
	@Qualifier("ServiceTaxInvoicePDFDaoImpl")
	ServiceTaxPdf servicePdf;

	public String generateServicetaxPdfZip(File tempDir,
			List<PdfPrintReqDto> request) throws Exception {

		for (PdfPrintReqDto req : request) {
			String id = req.getId();
			String docNo = req.getDocNo();
			String sgstin = req.getSgstin();

			String fileName = "Tax Invoice_" + id + "_" + sgstin;

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".pdf";
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {

				JasperPrint jasperPrint = servicePdf
						.generateServiceTaxPdfReport(id, docNo, sgstin);

				JasperExportManager.exportReportToPdfStream(jasperPrint,
						outStream);

				outStream.flush();
				outStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String zipFileName = zipEinvoicePdfFiles(tempDir);
		return zipFileName;
	}

	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "TaxInvoice";
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

	private static List<String> getAllFilesToBeZipped(File tmpDir)
			throws Exception {

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
