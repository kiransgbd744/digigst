package com.ey.advisory.service.days.revarsal180;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */

@Data
public class Reversal180DaysResponseUploadDto {

	private String userResponseRevTaxPeriod;
	private String userResponseReclaimTaxPeriod;
	private String actionType;
	private String custGstin;
	private String supplierGstin;
	private String supplierName;
	private String supplierCode;
	private String documentType;
	private String documentNum;
	private String documentDate;
	private String invoiceValue;
	private String statDeductionApplicable;
	private String statDeductionAmt;
	private String anyOtherDeductionAmt;
	private String remarksForDeductions;
	private String paymentDueDate;
	private String paymentRefStatus;
	private String paymentRefNumber;
	private String paymentRefDate;
	private String paymentDesc;
	private String paymentStatus;
	private String paidAmtToSupplier;
	private String currencyCode;
	private String exchangeRate;
	private String unpaidAmtToSupplier;
	private String postingDate;
	private String plantCode;
	private String profitCentre;
	private String division;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String docDate180Days;
	private String retPeriodPR;
	private String retPeriodReconResp;
	private String igstTaxPaidPR;
	private String cgstTaxPaidPR;
	private String sgstTaxPaidPR;
	private String cessTaxPaidPR;
	private String availableIgstPR;
	private String availableCgstPR;
	private String availableSgstPR;
	private String availableCessPR;
	private String itcRevReclaimStatusDigi;
	private String itcRevRetPrdDigiIndicative;
	private String revIgstDigiIndicative;
	private String revCgstDigiIndicative;
	private String revSgstDigiIndicative;
	private String revCessDigiIndicative;
	private String reclaimRetPrdDigiIndicative;
	private String reclaimIgstIndicative;
	private String reclaimCgstIndicative;
	private String reclaimSgstIndicative;
	private String reclaimCessIndicative;
	private String itcRevComputeDateTime;
	// newly added
	private String computeId;
	private String reconDateTime;
	private String reconReportConfigID;

	private String docKey;
	private String prDocKey;

}
