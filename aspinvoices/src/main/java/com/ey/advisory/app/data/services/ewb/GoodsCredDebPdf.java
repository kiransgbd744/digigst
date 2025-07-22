package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

	public interface GoodsCredDebPdf {
		
		public JasperPrint generateCredDebPdfReport(String id, String docNo,
				String sgstin);

	}
