/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
public class SalesRegisterInitiateReconReportDto {

	private String returnPeriod;
	private String supplierGSTIN;
	private String businessPlace;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String customerGSTINSR;
	private String invoiceValueSR;
	private String taxablevalueSR;
	private String igstSR;
	private String cgstSR;
	private String sgstSR;
	private String advaloremCessSR;
	private String specificCessSR;
	private String transactionTypeSR;
	private String customerGSTINDigiGST;
	private String invoiceValueDigiGST;
	private String taxablevalueDigiGST;
	private String igstDigiGST;
	private String cgstDigiGST;
	private String sgstDigiGST;
	private String advaloremCessDigiGST;
	private String specificCessDigiGST;
	private String transactionTypeDigiGST;
	private String invoiceValueDifference;
	private String taxablevalueDifference;
	private String igstDifference;
	private String cgstDifference;
	private String sgstDifference;
	private String advaloremCessDifference;
	private String specificCessDifference;
	private String reportType;
	private String mismatchReason;
}
