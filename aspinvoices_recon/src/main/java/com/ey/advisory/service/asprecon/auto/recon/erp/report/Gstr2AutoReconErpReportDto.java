/**
 * 
 */
package com.ey.advisory.service.asprecon.auto.recon.erp.report;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2AutoReconErpReportDto {

	private String userResponse;
	private String taxPeriodGSTR3B;
	private String matchReason;
	private String mismatchReason;
	private String reportCategory;
	private String reportType;
	private String eRPReportType;
	private String taxPeriod2A;
	private String taxPeriod2B;
	private String taxPeriodPR;
	private String recipientGSTIN2A;
	private String recipientGSTINPR;
	private String supplierGSTIN2A;
	private String supplierGSTINPR;
	private String docType2A;
	private String docTypePR;
	private String documentNumber2A;
	private String documentNumberPR;
	private String documentDate2A;
	private String documentDatePR;
	private String pOS2A;
	private String pOSPR;
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
	private String invoiceValue2A;
	private String invoiceValuePR;
	private String iTCAvailability2B;
	private String reasonforITCUnavailability2B;
	private String gSTR1FilingStatus;
	private String gSTR1FilingDate;
	private String gSTR1FilingPeriod;
	private String gSTR3BFilingStatus;
	private String reverseChargeFlag2A;
	private String reverseChargeFlagPR;
	private String plantCode;
	private String division;
	private String purchaseOrganisation;
	private String tableType2A;
	private String supplyType2A;
	private String supplyTypePR;
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String approvalStatus;

	private String recordStatus;
	private String keyDescription;
	private String otherColumn1;
	private String otherColumn2;
	private String otherColumn22;
	private String reconDate;
	private String revIntDate;

	// new columns
	private String irn2A;
	private String irnDate2A;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String userDefinedField4;
	private String userDefinedField5;
	private String requestId;
	private String idPr;
	private String id2A;

	// new columns
	private String responseRemarks;
	private String eligibilityIndicator;
	private String availableIGST;
	private String availableCGST;
	private String availableSGST;
	private String availableCESS;
	private String returnFilingFrequency;
	private String supplierGSTINCancellationDate2A;
	private String vendorComplianceTrend;
	private String supplierCode;
	private String bOEReferenceDate2A;
	private String portCode2A;
	private String portCodePR;
	private String billOfEntry2A;
	private String billOfEntryPR;
	private String billOfEntryDate2A;
	private String billOfEntryDatePR;
	private String companyCode;
	private String sourceIdentifier;
	private String vendorType;
	private String hSN;
	private String vendorRiskCategory;
	private String vendorPaymentTermsDays;
	private String vendorRemarks;
	private String eInvoiceApplicability;
	private String qRCodeCheck;
	private String qRCodeValidationResult;
	private String qRCodeMatchCount;
	private String qRCodeMismatchCount;
	private String qRMismatchAttributes;

	// new columns
	private String gSTR3BFilingDate;
	private String suppGstinStatus;
	private String sysDefField1;
	private String sysDefField2;
	private String sysDefField3;
	private String sysDefField4;
	private String sysDefField5;
	private String sysDefField6;
	private String sysDefField7;
	private String sysDefField8;
	private String sysDefField9;
	private String sysDefField10;
	private String suggestedResponse;
	
	//IMS columns
	private String userIMSResponse;
	private String iMSResponseRemarks;
	private String actionGSTN;
	private String pendingActionBlocked;
	private String iMSGetCallDateTime;
	private String actionDigiGST;
	private String actionDigiGSTDateTime;
	private String savedtoGSTN;
	private String activeinIMSGSTN;
	private String supplierLegalName2A;
	private String supplierTradeName2A;
	private String supplierNamePR;
	private String totalTax2A;
	private String totalTaxPR;
	private String generationDate2a;
	private String generationDate2b;
	private String iTCReversalIdentifier;
	
	private String orgInvAmendmentPeriod;
	private String orgAmendmentType;
	private String orgDocNumber2A;
	private String orgDocDate2A;
	private String orgDocNumberPR;
	private String orgDocDatePR;
	private String userID;
	private String iMSUniqueID;

}
