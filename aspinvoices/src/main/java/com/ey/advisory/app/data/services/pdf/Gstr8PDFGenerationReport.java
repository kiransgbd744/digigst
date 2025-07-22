/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Ravindra V S
 *
 */
public interface Gstr8PDFGenerationReport {


	public JasperPrint generatePdfGstr8Report(Gstr2AProcessedRecordsReqDto request,String gstn);

	
	
}
