package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

public interface EwbSummaryPDFGenerationReport {
	
	public JasperPrint generateSummaryPdfReport(String id);

}
