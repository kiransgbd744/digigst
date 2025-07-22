package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

public interface ServiceTaxPdf {
	
	public JasperPrint generateServiceTaxPdfReport(String id, String docNo,
			String sgstin);

}

