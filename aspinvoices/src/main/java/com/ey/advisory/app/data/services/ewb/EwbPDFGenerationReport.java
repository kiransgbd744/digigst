package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

public interface EwbPDFGenerationReport {
	
	public JasperPrint generatePdfReport(String id);

}
