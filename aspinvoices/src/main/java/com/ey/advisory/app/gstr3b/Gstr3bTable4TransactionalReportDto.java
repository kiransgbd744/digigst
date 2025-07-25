package com.ey.advisory.app.gstr3b;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Data
public class Gstr3bTable4TransactionalReportDto {

	private String suggestedResponse;
	private String userResponse;
	private String taxPeriodforGSTR3B;
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
	private String itcReversedTaxPeriod;
	private String itcReclaimedTaxPeriod;
	private String calendarMonth;
	private String recipientGstin2B;
	private String recipientGstinPR;
	private String supplierGstin2B;
	private String supplierGstinPR;
	private String supplierLegalName2B;
	private String supplierTradeName2B;
	private String supplierNamePR;
	private String docType2B;
	private String docTypePR;
	private String documentNumber2B;
	private String documentNumberPR;
	private String documentDate2B;
	private String documentDatePR;
	private String gSTPercent2B;
	private String gSTPercentPR;
	private String pos2B;
	private String posPR;
	private String taxableValue2B;
	private String taxableValuePR;
	private String iGST2B;
	private String iGSTPR;
	private String cGST2B;
	private String cGSTPR;
	private String sGST2B;
	private String sGSTPR;
	private String cess2B;
	private String cessPR;
	private String totalTax2B;
	private String totalTaxPR;
	private String invoiceValue2B;
	private String invoiceValuePR;
	private String reverseChargeFlag2B;
	private String reverseChargeFlagPR;
	private String eligibilityIndicator;
	private String availableIGST;
	private String availableCGST;
	private String availableSGST;
	private String availableCESS;
	private String iTCReversalIdentifier;
	private String iTCAvailability;
	private String reasonForITCUnAvailability;
	private String gSTR1FilingStatus;
	private String gSTR1FilingDate;
	private String gSTR1FilingPeriod;
	private String gSTR3BFilingStatus;
	private String gSTR3BFilingDate;
	private String supplierGSTINCancellationDate;
	private String gstr2BGenerationDate;
	private String orgInvAmendmentPeriod;
	private String orgAmendmentType;
	private String cDNDelinkingFlag;
	private String differentialPercentage2B;
	private String differentialPercentagePR;
	private String orgDocNumber2B;
	private String orgDocNumberPR;
	private String orgDocDate2B;
	private String orgDocDatePR;
	private String orgSupplierGstinPR;
	private String orgSupplierNamePR;
	private String cRDRPreGST2B;
	private String cRDRPreGSTPR;
	private String boeReferenceDate2B;
	private String billOfEntryCreatedDate2B;
	private String portCode2B;
	private String portCodePR;
	private String billOfEntry2B;
	private String billOfEntryPR;
	private String billOfEntryDate2B;
	private String billOfEntryDatePR;
	private String bOEAmended;
	private String tableType2B;
	private String supplyType2B;
	private String supplyTypePR;
	private String userID;
	private String sourceIdentifier;
	private String sourceFileName;
	private String profitCentre1;
	private String plantCode;
	private String division;
	private String location;
	private String purchaseOrganisation;
	private String profitCentre2;
	private String profitCentre3;
	private String profitCentre4;
	private String profitCentre5;
	private String profitCentre6;
	private String profitCentre7;
	private String gLAssessableValue;
	private String gLIGST;
	private String gLCGST;
	private String gLSGST;
	private String gLAdvaloremCess;
	private String gLSpecificCess;
	private String gLStateCessAdvalorem;
	private String supplierType;
	private String supplierCode;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierLocation;
	private String supplierPincode;
	private String stateApplyingCess;
	private String cIF;
	private String customDuty;
	private String hSN;
	private String productCode;
	private String productDescription;
	private String categoryOfProduct;
	private String uQC;
	private String Quantity;
	private String cessAdvaloremRate;
	private String cessAdvaloremAmount;
	private String cessSpecificRate;
	private String cessSpecificAmount;
	private String stateCessAdvaloremRate;
	private String stateCessAdvaloremAmount;
	private String itemOtherCharges;
	private String claimRefundFlag;
	private String autoPopulateToRefund;
	private String adjustementReferenceNo;
	private String adjustementReferenceDate;
	private String commonSupplyIndicator;
	private String iTCEntitlement;
	private String reasonForCreditDebitNote;	
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String gLPostingDate;
	private String customerPOReferenceNumber;
	private String customerPOReferenceDate;
	private String purchaseOrderValue;
	private String vendorTaxPaidPercentage;
	private String VendorType;
	private String hsnValue;
	private String vendorRiskCategory;
	private String vendorPaymentTerms;
	private String vendorRemarks;
	private String sourceTypeOfIRN;
	private String iRN2B;
	private String iRNPR;
	private String iRNDate2B;
	private String iRNDatePR;
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
	private String userDefinedField28;
	private String systemDefinedField1;
	private String systemDefinedField2;
	private String systemDefinedField3;
	private String systemDefinedField4;
	private String systemDefinedField5;
	private String eWBNumber;
	private String eWBDate;
	private String matchingID;
	private String requestID;
	private String iDPR;
	private String iD2B;
	private String invoiceKeyPR;
	private String invoiceKey2B;
	private String referenceIDPR;
	private String referenceID2B;
	private String source;
	private String amendedParameters;
	private String gstr3bTableNo;
	
	private String table4AValueIgst;
	private String table4AValueCgst;
	private String table4AValueSgst;
	private String table4AValueCess;
	private String table4BValueIgst;
	private String table4BValueCgst;
	private String table4BValueSgst;
	private String table4BValueCess;
	
	private String table4DValueIgst;
	private String table4DValueCgst;
	private String table4DValueSgst;
	private String table4DValueCess;
}
