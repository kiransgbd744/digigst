/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class Gstr1RateLevelReportDto {

	private String returnPeriod;
	private String transactionType;
	private String gSTR1TableNumber;
	private String supplierGSTIN;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String customerGSTIN;
	private String billingPOS;
	private String gSTRate;
	private String itemAssessableAmount;
	private String itemIGSTAmount;
	private String itemCGSTAmount;
	private String itemSGSTAmount;
	private String totalTax;
	private String invoiceValue;

}
