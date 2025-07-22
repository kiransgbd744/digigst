/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2InitiateReconReportDto {

	
	private String forceMatchResponse;
	private String taxPeriodforGSTR3B;// wipro
	private String comments;
	private String matchingScoreOutof11;
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
	private String supplierGstin2A;
	private String supplierGstinPR;
	private String supplierPan2A;
	private String supplierPanPR;
	private String supplierName2A;
	private String supplierNamePR;
	private String docType2A;
	private String docTypePR;
	private String documentNumber2A;
	private String documentNumberPR;
	private String documentDate2A;
	private String documentDatePR;
	private String gSTPercent2A;
	private String gSTPercentPR;
	private String pos2A;
	private String posPR;
	private String taxableValue2A;
	private String taxableValuePR;
	private String iGST2A;
	private String iGSTPR;
	private String cGST2A;
	private String cGSTPR;
	private String sGST2A;
	private String sGSTPR;
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
	private String profitCentre;
	private String plant;
	private String division;
	private String location;
	private String purchaseOrganisation;
	private String userAccess1;
	private String userAccess2;
	private String userAccess3;
	private String userAccess4;
	private String userAccess5;
	private String userAccess6;
	private String gLCodeTaxableValue;
	private String gLCodeIGST;
	private String gLCodeCGST;
	private String gLCodeSGST;
	private String gLCodeAdvaloremCess;
	private String gLCodeSpecificCess;
	private String gLCodeStateCess;
	private String supplyType2A;
	private String supplierType;
	private String supplierCode;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierAddress3;
	private String supplierAddress4;
	private String stateApplyingCess;
	private String portCodePR;
	private String billOfEntryPR;
	private String billOfEntryDatePR;
	private String cIFValue;
	private String customDuty;
	private String hSNorSAC;
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
	private String otherValue;
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
	private String purchaseVoucherNumber;
	private String purchaseVoucherDate;
	private String postingDate;
	private String paymentVoucherNumber;
	private String paymentDate;
	private String contractNumber;
	private String contractDate;
	private String contractValue;
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
	private String referenceIDPR;
	private String referenceID2A;

	private String suggestedFMResponse;

	// new added
	private String gSTR1FilingStatus;
	private String gSTR1FilingDate;
	private String gSTR1FilingPeriod;
	private String gSTR3BFilingStatus;
	private String cancellationDate;
	private String orgInvAmendmentPeriod;
	private String orgAmendmentType;
	private String cDNDelinkingFlag;
	private String supplyTypePR;
	private String reconDate;
	private String einvoiceApplicability;
	private String supplierReturnFilingPeriodicity;
	private String recordStatus;
	private String keyDescription;
	
	private String gstr3bFilingDate;
	private String supplierGstinStatus;
	private String systemDefinedField1;
	private String systemDefinedField2;
	private String systemDefinedField3;
	private String systemDefinedField4;
	private String systemDefinedField5;
	private String systemDefinedField6;
	private String systemDefinedField7;
	private String systemDefinedField8;
	private String systemDefinedField9;
	private String systemDefinedField10;
	

	// new logic
	private String boeReferenceDate;
	private String portCode2A;
	private String billOfEntry2A;
	private String billOfEntryDate2A;
	private String BoeAmended;
	private String matchReason;
	private String reportCategory;
	private String supplierTradeName2A;

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
	
	private String returnFilingFrequency;
	private String supplierGstinCancelDate2A;
	private String vendorComplianceTrend;
	private String sourceIdentifier;
	private String companyCode;
	private String profitCentre2;
	private String profitCentre3;
	private String profitCentre4;
	private String profitCentre5;
	private String profitCentre6;
	private String profitCentre7;
    private String vendorType;
	private String vendorRiskCategory;
	private String vendorPaymentTerms;
	private String vendorRemarks;
	private String reverseIntegratedDate;
	private String qrCodeCheck;
	private String qrCodeValidationResult;
	private String qrCodeMatchCount;
	private String qrCodeMismatchCount;
	private String qrMismatchAttrs;
	
	//3B changes 
	 	private String iTCReversedTaxPeriod;
	 	private String iTCReclaimedTaxPeriod;
	 	//private String itcReversalIdentifier;
	 	private String gSTR3BFilingDate;//gSTR3BFilingDate
	 //	private String supplierGSTINStatus;
	 	private String billofEntryCreatedDate2B;
	 	private String vendorTaxPaidVariance;
	 //	private String vendorType;
	 	private String hSN;
	 //	private String vendorRiskCategory;
	 	private String vendorPaymentTermsDays;
	 //	private String vendorRemarks;
	 	
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

