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
public class ITCReversal180ReportDownloadDto {

	private String userResponse;
	private String userResponseTaxPeriod;
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
	private String paymentReferenceStatus;
	private String paymentReferenceNumber;
	private String paymentReferenceDate;
	private String paymentDescription;
	private String paymentStatusFullorPartial;
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
	private String docDate180Days;
	private String returnPeriodPR;
	private String returnPeriod2APRResponse;
	private String iGSTTaxPaidPR;
	private String cGSTTaxPaidPR;
	private String sGSTTaxPaidPR;
	private String cessTaxPaidPR;
	private String availableIGSTPR;
	private String availableCGSTPR;
	private String availableSGSTPR;
	private String availableCessPR;
	private String iTCReversalReclaimStatusDigiGST;
	private String iTCReversalReturnPeriodDigiGSTIndicative;
	private String reversalofIGSTDigiGSTIndicative;
	private String reversalofCGSTDigiGSTIndicative;
	private String reversalofSGSTDigiGSTIndicative;
	private String reversalofCessDigiGSTIndicative;
	private String reClaimReturnPeriodDigiGSTIndicative;
	private String reClaimofIGSTIndicative;
	private String reClaimofCGSTIndicative;
	private String reClaimofSGSTIndicative;
	private String reClaimofCessIndicative;
	private String iTCReversalComputeDateTime;
	
	//new columns
	private String iTCReversalComputeRequestID;
	private String reconciliationDateAndTime;
	private String reconciliationRequestID;

}
