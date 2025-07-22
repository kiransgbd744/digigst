/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
public interface Gstr6AspSummaryPDFGenerationReport {

	
	public JasperPrint generatePdfGstr6Report(Gstr2AProcessedRecordsReqDto annexure1SummaryRequest,String gstn);
	
}
