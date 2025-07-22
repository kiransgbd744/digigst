package com.ey.advisory.app.data.services.pdf;

import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Jithendra Kumar B
 *
 */
public interface Gstr3BSummaryPDFGenerationReport {

	public JasperPrint generateGstr3BSummaryPdfReport(String gstin,
			String taxPeriod,Boolean isDigigst, String isVerified);

	public String generateBulkGstr3BSummaryPdfReport(JsonArray gstinArray,
			HttpServletResponse response, String taxPeriod, String isVerified);

}
