/**
 * 
 *//*
package com.ey.advisory.app.data.services.ewb;

*//**
 * @author Arun
 *
 *//*

import java.io.OutputStream;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@Slf4j
public class JasperUtil {
	
	JasperUtil() {
	}
	
	public static void getPDFReportBytesArray(List<JasperPrint> 
													jasperPrintList,
											OutputStream outputStream) {
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(SimpleExporterInput
				.getInstance(jasperPrintList));

		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
				outputStream));
		SimplePdfExporterConfiguration configuration = 
				new SimplePdfExporterConfiguration();
		configuration.setCreatingBatchModeBookmarks(true);
		exporter.setConfiguration(configuration);
		try {
			exporter.exportReport();
		} catch (JRException ex) {
			LOGGER.error("Error while exporting report", ex);
		}	
		
	}

}

*/