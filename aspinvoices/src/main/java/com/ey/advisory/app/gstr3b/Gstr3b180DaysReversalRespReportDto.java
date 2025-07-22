package com.ey.advisory.app.gstr3b;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class Gstr3b180DaysReversalRespReportDto {

	private String userRespTxPrdItcReversal;
	private String userRespTxPrdItcReclaim;
	private String actionType;
	private String customerGstin;
	private String supplierGstin;
	private String supplierName;
	private String supplierCode;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String invoiceVal;
	private String statutoryDednsAplcbl;
	private String statutoryDednsAmount;
	private String anyOthDednsAmount;
	private String remarksForDedns;
	private String dueDateOfPayment;
	private String paymentRefStatus;
	private String paymentRefNumber;
	private String paymentRefDate;
	private String paymentDesc;
	private String paymentStatus;
	private String paidAmtToSupp;
	private String currencyCode;
	private String exchangeRate;
	private String unpaidAmtToSupp;
	private String postingDate;
	private String plantCode;
	private String profitCentre;
	private String division;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String docDate180Days;
	private String returnPeriodPr;
	private String returnPeriod2aprResponse;
	private String igstTaxPaidPr;
	private String cgstTaxPaidPr;
	private String sgstTaxPaidPr;
	private String cessTaxpaidPr;
	private String availableIgstPr;
	private String availableCgstPr;
	private String availableSgstPr;
	private String availableCessPr;
	private String itcReversalOrReclaimStatus;
	private String itcReversalRetPeriod;
	private String reversalOfIgst;
	private String reversalOfCgst;
	private String reversalOfSgst;
	private String reversalOfCess;
	private String reclaimRetPeriod;
	private String reclaimOfIgst;
	private String reclaimOfCgst;
	private String reclaimOfSgst;
	private String reclaimOfCess;
	private String itcReversalCompDateAndTime;
	private String itcReversalComputeRequestID;
	private String reconciliationDateAndTime;
	private String reconciliationRequestID;
}
