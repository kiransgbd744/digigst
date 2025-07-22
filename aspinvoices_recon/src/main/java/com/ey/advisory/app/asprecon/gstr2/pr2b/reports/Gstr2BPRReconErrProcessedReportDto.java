package com.ey.advisory.app.asprecon.gstr2.pr2b.reports;

import lombok.Data;

/**
 * @author ashutosh.kar
 *
 */
@Data
public class Gstr2BPRReconErrProcessedReportDto {

	private String suggestedResponse;
	private String userResponse;
	private String taxPeriodGSTR3B;
	private String responseRemarks;
	private String matchingScoreOutof12;
	private String matchReason;
	private String mismatchReason;
	private String reportCategory;
	private String reportType;
	private String previousReportType2B;
	private String previousReportTypePR;
	private String taxPeriod2B;
	private String taxPeriodPR;
	private String calendarMonth;
	private String recipientGSTIN2B;
	private String recipientGSTINPR;
	private String supplierGSTIN2B;
	private String supplierGSTINPR;
	private String supplierLegalName2B;
	private String supplierTradeName2B;
	private String supplierNamePR;
	private String docType2B;
	private String docTypePR;
	private String documentNumber2B;
	private String documentNumberPR;
	private String documentDate2B;
	private String documentDatePR;
	private String gstPercentage2B;
	private String gstPercentagePR;
	private String pos2B;
	private String posPR;
	private String taxableValue2B;
	private String taxableValuePR;
	private String igst2B;
	private String igstPR;
	private String cgst2B;
	private String cgstPR;
	private String sgst2B;
	private String sgstPR;
	private String cess2B;
	private String cessPR;
	private String totalTax2B;
	private String totalTaxPR;
	private String invoiceValue2B;
	private String invoiceValuePR;
	private String availableIGST;
	private String availableCGST;
	private String availableSGST;
	private String availableCESS;
	private String tableType2B;
	private String gstr1FilingStatus;
	private String gstr1FilingDate;
	private String gstr1FilingPeriod;
	private String gstr3BFilingStatus;
	private String cancellationDate;
	private String orgInvAmendmentPeriod;
	private String orgAmendmentType;
	private String cdnDelinkingFlag;
	private String reverseChargeFlag2B;
	private String reverseChargeFlagPR;
	private String differentialPercentage2B;
	private String differentialPercentagePR;
	private String orgDocNumber2B;
	private String orgDocNumberPR;
	private String orgDocDate2B;
	private String orgDocDatePR;
	private String orgSupplierGSTINPR;
	private String orgSupplierNamePR;
	private String crdrPreGst2B;
	private String crdrPreGstPR;
	private String boeReferenceDate;
	private String portCode2B;
	private String billOfEntry2B;
	private String billOfEntryDate2B;
	private String boeAmended;
	private String userID;
	private String sourceFileName;
	private String profitCentre;
	private String plant;
	private String division;
	private String location;
	private String purchaseOrganisation;
	private String profitCentre2;
	private String profitCentre3;
	private String profitCentre4;
	private String profitCentre5;
	private String profitCentre6;
	private String profitCentre7;
	private String glCodeTaxableValue;
	private String glCodeIGST;
	private String glCodeCGST;
	private String glCodeSGST;
	private String glCodeAdvaloremCess;
	private String glCodeSpecificCess;
	private String glCodeStateCess;
	private String supplyType2B;
	private String supplyTypePR;
	private String supplierType;
	private String supplierCode;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierLocation;
	private String supplierPinCode;
	private String stateApplyingCess;
	private String portCodePR;
	private String billOfEntryPR;
	private String billOfEntryDatePR;
	private String cifValue;
	private String customDuty;
	private String hsnOrSac;
	private String itemCode;
	private String itemDescription;
	private String categoryOfItem;
	private String unitOfMeasurement;
	private String quantity;
	private String advaloremCessRate;
	private String advaloremCessAmount;
	private String specificCessRate;
	private String specificCessAmount;
	private String stateCessRate;
	private String stateCessAmount;
	private String itemOtherCharge;
	private String claimRefundFlag;
	private String autoPopulateToRefund;
	private String adjustementReferenceNo;
	private String adjustementReferenceDate;
	
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String customerPORefNumber;
	private String customerPORefDate;
	private String purchaseOrderValue;
	
	private String eligibilityIndicator;
	private String commonSupplyIndicator;
	private String itcEntitlement;
	private String iTCReversalIdentifier;
	private String reasonForCreditDebitNote;
	private String glPostingDate;
	
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String userDefinedField4;
	private String userDefinedField5;
	private String userDefinedField6;
	private String userDefinedField7;
	private String userDefinedField8;
	private String userDefinedField9;
	private String userDefinedField10;
	private String userDefinedField11;
	private String userDefinedField12;
	private String userDefinedField13;
	private String userDefinedField14;
	private String userDefinedField15;
	private String ewayBillNumber;
	private String ewayBillDate;
	private String matchingID;
	private String requestID;
	private String idPR;
	private String id2B;
	private String invoiceKeyPR;
	private String invoiceKey2B;
	private String referenceIDPR;
	private String referenceID2B;
	private String itcAvailability;
	private String reasonforItcUnavailability;
	private String sourceTypeofIrn;
	private String irnNumber;
	private String irnGenerationDate;
    private String gSTR2BGenerationDate;
    
 // 115-240 format
 	private String userDefinedField28;
 	private String irn2B;
 	private String irnPR;
 	private String irnDate2B;
 	private String irnDatePR;
 	
 	private String sourceIdentifier;
 	
 	//3B changes 
 	private String iTCReversedTaxPeriod;
 	private String iTCReclaimedTaxPeriod;
 	//private String itcReversalIdentifier;
 	private String gstr3bFilingDate;
 	private String supplierGSTINStatus;
 	private String billofEntryCreatedDate2B;
 	private String vendorTaxPaidVariance;
 	private String vendorType;
 	private String hSN;
 	private String vendorRiskCategory;
 	private String vendorPaymentTermsDays;
 	private String vendorRemarks;
 	private String systemDefinedField1;
 	private String systemDefinedField2;
 	private String systemDefinedField3;
 	private String systemDefinedField4;
 	private String systemDefinedField5;
 	
 	//IMS 
 	private String imsUserResponse;
 	private String imsResponseRemark;
 	private String actionGstn;
 	private String pendingActionbloacked;
 	private String imsGetCallDateTime;
 	private String actionDigi;
 	private String actionDigiDateTime;
 	private String imsSaveToGstn;
 	private String activeInIms;
 	private String imsTotalTaxDiff;
 	private String imsTotalTaxPRGreater2B;
 	private String imsTotalTax2BGreaterPR;
 	private String imsUniqueId;
 	private String amountSource2B;
 	
 	//ErrorDescription
 	private String errorDescription;
 	
}
