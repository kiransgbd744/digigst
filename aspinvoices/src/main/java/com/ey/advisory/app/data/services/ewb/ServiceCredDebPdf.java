package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

public interface ServiceCredDebPdf {
	
	public JasperPrint generateServiceCredPdfReport(String id, String docNo,
			String sgstin);

}
