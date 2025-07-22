/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr6a;

import lombok.Data;

/**
 * @author Balakrishna.S
 *
 */
@Data
public class Itc04PdfReportDto {
	
	private String noofRecords;
	private String taxableValue;
	private String totalIntigratedValue;
	private String totalCentralTax;
	private String totalStateTax;
	private String aspCess;
				
	
}
