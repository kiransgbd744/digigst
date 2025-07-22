/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Sujith.Nanga
 *
 */
public interface GoodsCredDebCancelPDF {
	
	public JasperPrint generateCredDebCancelPdfReport(String id, String docNo,
			String sgstin);

}
