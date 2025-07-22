/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1SummaryPDFGenerationReport {
	
	public JasperPrint generatePdfGstr1Report(Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult);
	
	public JasperPrint generatePdfGstr1ReportNew(Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult, String sgstin);

}
