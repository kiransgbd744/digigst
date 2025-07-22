package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class GSTR2AAutoReconRespUploadDTO {

	private String suggestedResponse;
	private String userResponse;
	private String taxPeriodforGSTR3B;
	private String responseRemarks;
	private String matchingScoreOutof12;
	private String mismatchReason;
	private String reportType;
	private String erpReportType;
	private String approvalStatus;
	private String previousReportType2A;
	private String previousReportTypePR;
	private String taxPeriod2A;
	private String taxPeriodPR;
	private String calendarMonth;
	private String recipientGstin2A;
	private String recipientGstinPR;
	private String supplierPan2A;
	private String supplierPanPR;
	private String supplierGstin2A;
	private String supplierGstinPR;
	private String supplierLegalName2A;
	private String supplierNamePR;
	private String docType2A;
	private String docTypePR;
	private String documentNumber2A;
	private String documentNumberPR;
	private String documentDate2A;
	private String documentDatePR;
	// private String gSTPercent2A;
	// private String gSTPercentPR;
	private String pos2A;
	private String posPR;
	private String taxableValue2A;
	private String taxableValuePR;
	private String igst2A;
	private String igstPR;
	private String cgst2A;
	private String cgstPR;
	private String sgst2A;
	private String sgstPR;
	private String cess2A;
	private String cessPR;
	private String totalTax2A;
	private String totalTaxPR;
	private String invoiceValue2A;
	private String invoiceValuePR;
	private String availableIGST;
	private String availableCGST;
	private String availableSGST;
	private String availableCESS;
	private String tableType2A;
	private String cFSFlag2A;// removed
	private String reverseChargeFlag2A;
	private String reverseChargeFlagPR;
	private String differentialPercentage2A;
	private String differentialPercentagePR;
	private String orgDocNumber2A;
	private String orgDocNumberPR;
	private String orgDocDate2A;
	private String orgDocDatePR;
	private String orgSupplierGstinPR;
	private String orgSupplierNamePR;
	private String cRDRPreGST2A;
	private String cRDRPreGSTPR;
	private String userID;
	private String sourceFileName;
	private String profitCentre1;
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
	private String gLCodeAssessableValue;
	private String gLCodeIGST;
	private String gLCodeCGST;
	private String gLCodeSGST;
	private String gLCodeAdvaloremCess;
	private String gLCodeSpecificCess;
	private String gLCodeStateCessAdvalorem;
	private String supplyType2A;
	private String supplierType;
	private String supplierCode;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierLocation;
	private String supplierPincode;
	private String stateApplyingCess;
	private String portCodePR;
	private String billOfEntryPR;
	private String billOfEntryDatePR;
	private String cIFValue;
	private String customDuty;
	private String hSNorSAC;
	private String productCode;
	private String productDescription;
	private String categoryOfProduct;
	private String uqc;
	private String quantity;
	// private String advaloremCessRate;
	private String advaloremCessAmount;
	// private String specificCessRate;
	private String specificCessAmount;
	// private String stateCessAdvaloremRate;
	private String stateCessAdvaloremAmount;
	private String itemOtherCharges;
	private String claimRefundFlag;
	private String autoPopulateToRefund;
	private String adjustementReferenceNo;
	private String adjustementReferenceDate;

	private String taxableValueAdjusted;
	private String integratedTaxAmountAdjusted;
	private String centralTaxAmountAdjusted;
	private String stateUTTaxAmountAdjusted;
	private String advaloremCessAmountAdjusted;
	private String specificCessAmountAdjusted;
	private String stateCessAmountAdjusted;

	private String eligibilityIndicator;
	private String commonSupplyIndicator;
	private String iTCEntitlement;
	private String iTCReversalIdentifier;
	private String reasonForCreditDebitNote;
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String glPostingDate;
	private String customerPORefNumber;
	private String customerPORefDate;
	private String purchaseOrderValue;

	private String contractNumber;
	private String contractDate;
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
	
	
	private String eWayBillNumber;
	private String eWayBillDate;
	private String matchingID;
	private String requestID;
	private String iDPR;
	private String iD2A;
	private String invoiceKeyPR;
	private String invoiceKeyA2;
	private String invoiceKeyUploadPR;
	private String invoiceKeyUploadA2;
	private String referenceIDPR;
	private String referenceID2A;

	// new added
	private String gSTR1FilingStatus;
	private String gSTR1FilingDate;
	private String gSTR1FilingPeriod;
	private String gSTR3BFilingStatus;
	
	private String cancellationDate; //supplierGstinCancellationDate
	
	
	private String orgInvAmendmentPeriod;
	private String orgAmendmentType;
	private String cDNDelinkingFlag;
	private String supplyTypePR;
	
	private String reverseIntegratedDate;
	
	
	// new logic
	private String boeReferenceDate;
	private String portCode2A;
	private String billOfEntry2A;
	private String billOfEntryDate2A;
	private String boeAmended;
	private String matchReason;
	private String reportCategory;
	private String supplierTradeName2A;
	private String reconGeneratedDate;
	private String eInvoiceApplicability;
	private String supplierReturnFilingPeriodicity;
	private String recordStatus;
	private String keyDescription;

	// 115-240 format
	private String userDefinedField28;
	private String irn2A;
	private String irnPR;
	private String irnDate2A;
	private String irnDatePR;

	// 2B data in 2A
	private String taxPeriod2B;
	private String itcAvailability2B;
	private String reasonForItcUnavailability2B;
	private String sourceType;
	private String previousSourceType;
	private String generationDate2B;
	private String generationDate2A;
	private String sourceTypeofIrn;
	
	// new added for 197 column 
	private String vendorComplianceTrend;
	private String qrCodeCheck;
	private String qrCodeValidationResult;
	private String qrCodeMatchCount;
	private String qrCodeMismatchCount;
	private String qrMismatchAttributes;
	private String companyCode;
	private String sourceIdentifier;
	private String vendorType;
	private String vendorRiskCategory;
	private String vendorPaymentTerms_Days;
	private String vendorRemarks;
	
 
	private String gSTR3BFilingDate;
	private String supplierGSTINStatus;
	private String sysDefinedField1;
	private String sysDefinedField2;
	private String sysDefinedField3;
	private String sysDefinedField4;
	private String sysDefinedField5;
	private String sysDefinedField6;
	private String sysDefinedField7;
	private String sysDefinedField8;
	private String sysDefinedField9;
	private String sysDefinedField10;
	
	//2A_6A vs PR IMS new columns
 	private String userIMSResponse;
 	private String imsResponseRemarks;
 	private String actionGSTN;
 	private String pendingActionBlocked;
 	private String imsGetCallDateTime;
 	private String actionDigiGST;
 	private String actionDigiGSTDateTime;
 	private String savedtoGSTN;
 	private String activeinIMSGSTN;
 	private String imsUniqueID;
	
	
}
