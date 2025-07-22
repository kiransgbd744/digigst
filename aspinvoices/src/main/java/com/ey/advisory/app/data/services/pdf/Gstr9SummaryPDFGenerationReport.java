package com.ey.advisory.app.data.services.pdf;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Jithendra.B
 *
 */
public interface Gstr9SummaryPDFGenerationReport {
	
	public JasperPrint generateGstr9SummaryPdfReport(String gstin, String fy, String isDigigst);

}
