/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import com.ey.advisory.core.dto.ITC04RequestDto;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Balakrishna.S
 *
 */
public interface ITC04PDFGenerationReport {


	public JasperPrint generatePdfGstr6Report(ITC04RequestDto annexure1SummaryRequest,String gstn);
	
	
}
