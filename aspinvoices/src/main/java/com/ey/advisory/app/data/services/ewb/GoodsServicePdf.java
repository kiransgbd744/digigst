package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

public interface GoodsServicePdf {
	
	public JasperPrint generatePdfReport(String id, String docNo,
			String sgstin);

}

