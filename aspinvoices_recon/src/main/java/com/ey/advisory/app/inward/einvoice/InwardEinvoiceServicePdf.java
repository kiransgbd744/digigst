package com.ey.advisory.app.inward.einvoice;

import net.sf.jasperreports.engine.JasperPrint;

public interface InwardEinvoiceServicePdf {
	
	public JasperPrint generatePdfReport(String irn, String irnStatus,
			String supplyType, String docType);

}

