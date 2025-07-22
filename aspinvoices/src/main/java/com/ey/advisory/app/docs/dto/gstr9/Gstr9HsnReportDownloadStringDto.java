package com.ey.advisory.app.docs.dto.gstr9;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr9HsnReportDownloadStringDto {
	
	private String Gstin;

	private String Fy;

	private String TableNumber;

	private String Hsn;

	private String Description;

	private String RateofTax;

	private String Uqc;
	
	private String TotalQuantity;

	private String TaxableValue;

	private String ConcessionalRateFlag;

	private String Igst;

	private String Cgst;

	private String Sgst;

	private String Cess;

}
