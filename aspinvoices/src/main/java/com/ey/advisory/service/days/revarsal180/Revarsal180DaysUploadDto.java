/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Revarsal180DaysUploadDto {

	private String actionType;
	private String customerGSTIN;
	private String supplierGSTIN;
	private String supplierName;
	private String supplierCode;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String invoiceValue;
	private String statutoryDeductionsApplicable;
	private String statutoryDeductionAmount;
	private String anyOtherDeductionAmount;
	private String remarksforDeductions;
	private String dueDateofPayment;
	private String paymentReferenceNumber;
	private String paymentReferenceDate;
	private String paymentDescription;
	private String paymentStatus;
	private String paidAmounttoSupplier;
	private String currencyCode;
	private String exchangeRate;
	private String unpaidAmounttoSupplier;
	private String postingDate;
	private String plantCode;
	private String profitCentre;
	private String division;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String errorCode;
	private String errorDesc;

	private String docKey;
	private String inwardDocKey;
	private boolean isPsd;
	private Long fileId;
	private String source;

}
