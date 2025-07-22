/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr6a.Itc04PdfReportDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.CombineAndZipXlsxFiles;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.ITC04RequestDto;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
@Component("ITC04MultiplePdfZipReportService")
@Slf4j
public class ITC04MultiplePdfZipReportService {

	
	@Autowired
	@Qualifier("ITC04PDFGenerationReportImpl")
	ITC04PDFGenerationReportImpl itc04ReportImpl;
	
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	
	public String generateEinvoicePdfZip(File tempDir,
			ITC04RequestDto reqDto) throws Exception {
				
		

		ITC04RequestDto request = processedRecordsCommonSecParam
					.setItc04DataSecuritySearchParams(reqDto);
		Boolean isDigigst = request.getIsDigigst();
		String taxPeriod = request.getTaxPeriod();
		String quarter = taxPeriod.substring(0, 2);
		
	
		

		String qr = null;

		if (quarter.equalsIgnoreCase("13")) {
			qr = "Apr-Jun";
		} else if (quarter.equalsIgnoreCase("14")) {
			qr = "Jul-Sep";
		} else if (quarter.equalsIgnoreCase("15")) {
			qr = "Oct-Dec";
		} else if (quarter.equalsIgnoreCase("16")) {
			qr = "Jan-Mar";
		} else if (quarter.equalsIgnoreCase("17")) {
			qr = "Apr-Sep";
		} else if (quarter.equalsIgnoreCase("18")) {
			qr = "Oct-Mar";
		}
		
		
		
	//	String sgstin = null;
		List<String> gstinList = null;
		
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		
		gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		

		for (String gstn : gstinList) {
			String fileName = null;
			if (isDigigst == true) {
							
			 fileName = "ITC-04_"+gstn+"_"+qr+"_"+"DIGIGST";
			}
			else{
				fileName = "ITC-04_"+gstn+"_"+qr+"_"+"GSTN";
			}

			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".pdf";
			try (OutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(fullPath), 8192)) {


			
			JasperPrint jasperPrint = itc04ReportImpl
					.generatePdfGstr6Report(request,gstn);
			
			JasperExportManager.exportReportToPdfStream(jasperPrint,
					outStream);

			outStream.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
			}
		String zipFileName = zipEinvoicePdfFiles(tempDir);
		return zipFileName;	
		}
		
		
		
	private String zipEinvoicePdfFiles(File tempDir) throws Exception {

		List<String> filesToZip = getAllFilesToBeZipped(tempDir);
		String fileName = "Itc04PdfReports";
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
