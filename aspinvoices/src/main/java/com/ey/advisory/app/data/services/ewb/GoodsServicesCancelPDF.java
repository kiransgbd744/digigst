/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Sujith.Nanga
 *
 */
public interface GoodsServicesCancelPDF {
	
	public JasperPrint generateCancelPdfReport(String id, String docNo,
			String sgstin);

}

