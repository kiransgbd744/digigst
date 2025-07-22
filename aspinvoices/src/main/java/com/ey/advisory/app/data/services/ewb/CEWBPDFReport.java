/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Sujith.Nanga
 *
 */
public interface CEWBPDFReport {
	
	public JasperPrint generateEinvoiceSummaryPdfReport(
			String consolidatedEwbNum);

}

