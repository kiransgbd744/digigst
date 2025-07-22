/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
public interface Gstr7PDFGenerationReport {


	public JasperPrint generatePdfGstr7Report(Gstr2AProcessedRecordsReqDto request,String gstn);

	
	
}
