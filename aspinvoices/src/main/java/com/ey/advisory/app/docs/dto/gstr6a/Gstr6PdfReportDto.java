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
public class Gstr6PdfReportDto {

//	private String taxablevalue;
	private String noofRecords;
	private String totalInvoiceValue;
	private String totalIntigratedValue;
	private String totalCentralTax;
	private String totalStateTax;
	private String aspCess;
	private String totalIneligibleITC;
	private String totalEligibleITC;
	private String totalITC;
	
	
		
	
}
