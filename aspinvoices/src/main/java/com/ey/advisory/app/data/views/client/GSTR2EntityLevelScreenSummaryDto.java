package com.ey.advisory.app.data.views.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSTR2EntityLevelScreenSummaryDto {

	
	private String GSTIN;
	private String TableDescription;
	private String Count;
	private String invoiceValue;
	private String taxableValue;
	private String totalTax;
	private String IGSTTax;
	private String CGSTTax;
	private String SGSTTax;
	private String CessTax;
	private String TotalCreditEligible;
	private String EligibleIGST;
	private String EligibleCGST;
	private String EligibleSGST;
	private String EligibleCess;
	
}
