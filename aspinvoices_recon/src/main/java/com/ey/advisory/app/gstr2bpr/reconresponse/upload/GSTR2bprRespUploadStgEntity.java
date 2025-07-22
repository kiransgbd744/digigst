package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
@Entity
@Table(name = "TBL_2BPR_STG_RECON_RESPONSE")
public class GSTR2bprRespUploadStgEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_2BPR_STG_RECON_RESPONSE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCHID")
	private String batchID;

	@Column(name = "INVOICEKEYPR")
	private String invoicekeyPR;

	@Column(name = "INVOICEKEYB2")
	private String invoicekey2B;

	@Column(name = "FMRESPONSE")
	private String fMResponse;

	@Column(name = "ISFM")
	private String isFM;

	@Column(name = "RSPTAXPERIOD3B")
	private String rspTaxPeriod3B;

	@Column(name = "TAXPERIOD2B")
	private String taxPeriod2B;

	@Column(name = "TAXPERIODPR")
	private String taxPeriodPR;

	@Column(name = "CALENDAR_MONTH_PR")
	private String calanderMonthPR;

	@Column(name = "RGSTIN2B")
	private String rGSTIN2B;

	@Column(name = "RGSTINPR")
	private String rGSTINPR;

	@Column(name = "SGSTIN2B")
	private String sGSTIN2B;

	@Column(name = "SGSTINPR")
	private String sGSTINPR;

	@Column(name = "DOCTYPE2B")
	private String docType2B;

	@Column(name = "DOCTYPEPR")
	private String docTypePR;

	@Column(name = "DOCUMENTNUMBERPR")
	private String documentNumberPR;

	@Column(name = "DOCUMENTNUMBER2B")
	private String documentNumber2B;

	@Column(name = "DOCDATE2B")
	private String docDate2B;

	@Column(name = "GST_2B")
	private String gstPerc2B;

	@Column(name = "GST_PR")
	private String gstPercPR;

	@Column(name = "DOCDATEPR")
	private String docDatePR;

	@Column(name = "CFSFLAG")
	private String cfsFlag;

	@Column(name = "IGSTPR")
	private String iGSTPR;

	@Column(name = "CGSTPR")
	private String cGSTPR;

	@Column(name = "SGSTPR")
	private String sGSTPR;

	@Column(name = "CESSPR")
	private String cESSPR;

	@Column(name = "TAXABLEPR")
	private String taxablePR;

	@Column(name = "UPLDAVBLIGSTPR")
	private String upldAvblIGSTPR;

	@Column(name = "UPLDAVBLCGSTPR")
	private String upldAvblCGSTPR;

	@Column(name = "UPLDAVBLSGSTPR")
	private String upldAvblSGSTPR;

	@Column(name = "UPLDAVBLCESSPR")
	private String upldAvblCessPR;

	@Column(name = "AVBLIGSTPR")
	private String avblIGSTPR;

	@Column(name = "AVBLCGSTPR")
	private String avblCGSTPR;

	@Column(name = "AVBLSGSTPR")
	private String avblSGSTPR;

	@Column(name = "AVBLCESSPR")
	private String avblCessPR;

	@Column(name = "IGST2B")
	private String iGST2B;

	@Column(name = "CGST2B")
	private String cGST2B;

	@Column(name = "SGST2B")
	private String sGST2B;

	@Column(name = "CESS2B")
	private String cESS2B;

	@Column(name = "TAXABLE2B")
	private String taxable2B;

	@Column(name = "TABLETYPE")
	private String tableType;

	@Column(name = "IDPR")
	private String iDPR;

	@Column(name = "ID2B")
	private String iD2B;

	@Column(name = "CREATEDBY")
	private String createdBy;

	@Column(name = "CREATEDTM")
	private LocalDateTime createDTM;

	@Column(name = "UPDATEDTM")
	private LocalDateTime updateDTM;

	@Column(name = "DELETEDTM")
	private LocalDateTime deleteDtm;

	@Column(name = "ENDDTM")
	private String endDtm;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private String configId;

	@Column(name = "FMCOMMENTS")
	private String fmComment;

	@Column(name = "SUGGESTEDRESPONSE")
	private String suggestedResponse;

	@Column(name = "USERRESPONSE")
	private String userResponse;

	@Column(name = "REVERSE_CHARGE_PR")
	private String reversChargeReg;

	@Column(name = "TAX_PERIOD_3B")
	private String taxPeriod3B;

	@Column(name = "MATCHING_SCORE")
	private String matchingScore;

	@Column(name = "MATCH_REASON")
	private String matchReason;

	@Column(name = "MISMATCH_REASON")
	private String misMatchReason;

	@Column(name = "REPORT_CATEGORY")
	private String reportCategory;

	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "ERP_REPORT_TYPE")
	private String erpReportType;

	@Column(name = "PREVIOUS_REPORT_TYPE_2B")
	private String previousReportType2B;

	@Column(name = "PREVIOUS_REPORT_TYPE_PR")
	private String previousReportTypePR;

	@Column(name = "SUPPLIER_LEGAL_NAME_2B")
	private String supplierLegalName2B;

	@Column(name = "SUPPLIER_TRADE_NAME_2B")
	private String supplierTradeName2B;

	@Column(name = "SUPPLIER_NAME_PR")
	private String supplierNamePR;

	@Column(name = "POS_2B")
	private String pos2B;

	@Column(name = "POS_PR")
	private String posPR;

	@Column(name = "TOTAL_TAX_2B")
	private String totalTax2B;

	@Column(name = "TOTAL_TAX_PR")
	private String totalTaxPR;

	@Column(name = "INVOICE_VALUE_2B")
	private String invoiceValue2B;

	@Column(name = "INVOICE_VALUE_PR")
	private String invoiceValuePR;

	@Column(name = "ITC_AVAILABILITY_2B")
	private String itcAvailability2B;

	@Column(name = "REASON_FOR_ITC_UNAVAILABILITY_2B")
	private String reasonForITCUnavailabilty2B;

	@Column(name = "SOURCE_TYPE")
	private String sourceType;

	@Column(name = "GENERATION_DATE_2B")
	private String generationDate2B;

	@Column(name = "GENERATION_DATE_2A")
	private String generationDate2A;

	@Column(name = "GSTR1_FILING_DATE")
	private String gstr1FilingDate;

	@Column(name = "GSTR1_FILING_PERIOD")
	private String gstr1FilingPeriod;

	@Column(name = "GSTR3B_FILING_STATUS")
	private String gstr3BFilingStatus;

	@Column(name = "CANCELLATION_DATE")
	private String cancellationDate;

	@Column(name = "ORG_INV_AMENDMENT_PERIOD")
	private String orgInvAmendmentPeriod;

	@Column(name = "ORG_AMENDMENT_TYPE")
	private String orgInvAmendmentType;

	@Column(name = "CDN_DELINKING_FLAG")
	private String cdnDelinkingFlag;

	@Column(name = "REVERSE_CHARGEFLAG_2B")
	private String reverseChargeFlag2B;

	@Column(name = "DIFFERENTIAL_PERCENTAGE_2B")
	private String differentialPercentage2B;

	@Column(name = "DIFFERENTIAL_PERCENTAGE_PR")
	private String differentialPercentagePR;

	@Column(name = "ORG_DOC_NUMBER_2B")
	private String orgDocNumber2B;

	@Column(name = "ORG_DOC_NUMBER_PR")
	private String orgDocNumberPR;

	@Column(name = "ORG_DOC_DATE_2B")
	private String orgDocDate2B;

	@Column(name = "ORG_DOC_DATE_PR")
	private String orgDocDatePR;

	@Column(name = "ORG_SUPPLIER_GSTIN_PR")
	private String orgSupplierGstinPR;

	@Column(name = "ORG_SUPPLIER_NAME_PR")
	private String orgSupplierNamePR;

	@Column(name = "CRDR_PRE_GST_2B")
	private String crdrPreGst2B;

	@Column(name = "CRDR_PRE_GST_PR")
	private String crdrPreGstPR;

	@Column(name = "BOE_REFERENCE_DATE")
	private String boeReferenceDate;

	@Column(name = "PORT_CODE_2B")
	private String portCode2B;

	@Column(name = "BILL_OF_ENTRY_2B")
	private String billOfEntry2B;

	@Column(name = "BILL_OF_ENTRY_DATE_2B")
	private String billOfEntryDate2B;

	@Column(name = "BOE_AMENDED")
	private String boeAmended;

	@Column(name = "USER_ID")
	private String userID;

	@Column(name = "SOURCE_FILE_NAME")
	private String sourceFileName;

	@Column(name = "SOURCE_IDENTIFIER")
	private String sourceIdentifier;

	@Column(name = "PROFIT_CENTRE_1")
	private String profitCentre1;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "LOCATION")
	private String location;

	@Column(name = "PURCHASE_ORGANISATION")
	private String purchaseOrg;

	@Column(name = "PROFIT_CENTRE_2")
	private String profitCentre2;

	@Column(name = "PROFIT_CENTRE_3")
	private String profitCentre3;

	@Column(name = "PROFIT_CENTRE_4")
	private String profitCentre4;

	@Column(name = "PROFIT_CENTRE_5")
	private String profitCentre5;

	@Column(name = "PROFIT_CENTRE_6")
	private String profitCentre6;

	@Column(name = "PROFIT_CENTRE_7")
	private String profitCentre7;

	@Column(name = "GL_ASSESSABLE_VALUE")
	private String glAssessableValue;

	@Column(name = "GL_IGST")
	private String glIgst;

	@Column(name = "GL_CGST")
	private String glCgst;

	@Column(name = "GL_SGST")
	private String glSgst;

	@Column(name = "GL_ADVALOREM_CESS")
	private String glAdvaloremCess;

	@Column(name = "GL_SPECIFIC_CESS")
	private String glSpecificCess;

	@Column(name = "GL_STATECESS_ADVALOREM")
	private String glStateCessAdvalorem;

	@Column(name = "SUPPLY_TYPE_2B")
	private String supplyType2B;

	@Column(name = "SUPPLY_TYPE_PR")
	private String supplyTypePR;

	@Column(name = "SUPPLIER_TYPE")
	private String supplierType;

	@Column(name = "SUPPLIER_CODE")
	private String supplierCode;

	@Column(name = "SUPPLIER_ADDRESS1")
	private String supplierAddress1;

	@Column(name = "SUPPLIER_ADDRESS2")
	private String supplierAddress2;

	@Column(name = "SUPPLIER_LOCATION")
	private String supplierLocation;

	@Column(name = "SUPPLIER_PINCODE")
	private String supplierPincode;

	@Column(name = "STATE_APPLYING_CESS")
	private String stateApplyingCess;

	@Column(name = "PORTCODE_PR")
	private String portCodePR;

	@Column(name = "BILL_OF_ENTRY_PR")
	private String billOfEntryPR;

	@Column(name = "BILL_OF_ENTRY_DATE_PR")
	private String billOFEntryDatePR;

	@Column(name = "CIF")
	private String cif;

	@Column(name = "CUSTOM_DUTY")
	private String customDuty;

	@Column(name = "HSN")
	private String hsn;

	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@Column(name = "PRODUCT_DESCRIPTION")
	private String productDesc;

	@Column(name = "CATEGORY_OF_PRODUCT")
	private String categoryOfProduct;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "QUANTITY")
	private String qnty;

	@Column(name = "CESS_ADVALOREM_AMOUNT")
	private String cessAdvaloremAmt;

	@Column(name = "CESS_SPECIFIC_RATE")
	private String cessAdvaloremRate;

	@Column(name = "CESS_SPECIFIC_AMOUNT")
	private String cessSpecificAmt;

	@Column(name = "STATE_CESS_ADVALOREM_AMOUNT")
	private String stateCessAdvaloremAmt;

	@Column(name = "STATE_CESS_ADVALOREM_RATE")
	private String stateCessAdvaloremRate;

	@Column(name = "ITEM_OTHER_CHARGES")
	private String itemOtherCharges;

	@Column(name = "CLAIM_REFUND_FLAG")
	private String claimRefundFlag;

	@Column(name = "AUTO_POPULATE_TO_REFUND")
	private String autoPopulateToRefund;

	@Column(name = "ADJUSTEMENT_REFERENCE_NO")
	private String adjustmentRefNo;

	@Column(name = "ADJUSTEMENT_REFERENCE_DATE")
	private String adjustmentRefDate;

	@Column(name = "ELIGIBILITY_INDICATOR")
	private String eligibilityIndicator;

	@Column(name = "COMMONSUPPLY_INDICATOR")
	private String commonSupplyIndicator;

	@Column(name = "ITC_ENTITLEMENT")
	private String itcEntitlement;

	@Column(name = "ITC_REVERSAL_IDENTIFIER")
	private String itcReversalIdentifier;

	@Column(name = "REASON_FOR_CREDIT_DEBIT_NOTE")
	private String reasonForCrDrNote;

	@Column(name = "ACCOUNTING_VOUCHER_NUMBER")
	private String accountingVoucherNumber;

	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	private String accountingVoucherDate;

	@Column(name = "GL_POSTING_DATE")
	private String glPostingDate;

	@Column(name = "CUSTOMER_PO_REFERENCE_NUMBER")
	private String customerPORefNumber;

	@Column(name = "CUSTOMER_PO_REFERENCE_DATE")
	private String customerPORefDate;

	@Column(name = "PURCHASE_ORDER_VALUE")
	private String purchaseOrderValue;

	@Column(name = "USERDEFINEDFIELD1")
	private String userDefinedField1;

	@Column(name = "USERDEFINEDFIELD2")
	private String userDefinedField2;

	@Column(name = "USERDEFINEDFIELD3")
	private String userDefinedField3;

	@Column(name = "USERDEFINEDFIELD4")
	private String userDefinedField4;

	@Column(name = "USERDEFINEDFIELD5")
	private String userDefinedField5;

	@Column(name = "USERDEFINEDFIELD6")
	private String userDefinedField6;

	@Column(name = "USERDEFINEDFIELD7")
	private String userDefinedField7;

	@Column(name = "USERDEFINEDFIELD8")
	private String userDefinedField8;

	@Column(name = "USERDEFINEDFIELD9")
	private String userDefinedField9;

	@Column(name = "USERDEFINEDFIELD10")
	private String userDefinedField10;

	@Column(name = "USERDEFINEDFIELD11")
	private String userDefinedField11;

	@Column(name = "USERDEFINEDFIELD12")
	private String userDefinedField12;

	@Column(name = "USERDEFINEDFIELD13")
	private String userDefinedField13;

	@Column(name = "USERDEFINEDFIELD14")
	private String userDefinedField14;

	@Column(name = "USERDEFINEDFIELD15")
	private String userDefinedField15;

	@Column(name = "USERDEFINEDFIELD28")
	private String userDefinedField28;

	@Column(name = "EWB_NUMBER")
	private String ewbNumber;

	@Column(name = "EWB_DATE")
	private String ewbDate;

	@Column(name = "MATCHING_ID")
	private String matchingId;

	@Column(name = "REFERENCE_IDPR")
	private String refIDPR;

	@Column(name = "REFERENCE_ID2B")
	private String refID2B;

	@Column(name = "IRN_2B")
	private String irn2B;

	@Column(name = "IRN_PR")
	private String irnPR;

	@Column(name = "IRNDATE_2B")
	private String irnDate2B;

	@Column(name = "IRNDATE_PR")
	private String irnDatePR;

	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;

	@Column(name = "SUPP_PAN_2B")
	private String supplierPan2B;

	@Column(name = "SUPP_PAN_PR")
	private String supplierPanPR;

	@Column(name = "RECON_GEN_DATE")
	private String reconGenDate;

	@Column(name = "EINV_APPLICABILITY")
	private String einvApplicability;

	@Column(name = "SUPP_RETURN_FILING_PERIODICITY")
	private String supplierReturnFilingPeriodicty;

	@Column(name = "REC_STATUS")
	private String recordStatus;

	@Column(name = "KEY_DESCRIPTION")
	private String keyDescription;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSourceType;

	@Column(name = "ITC_REVERSED_TAX_PERIOD")
	private String itcReversedTaxPeriod;

	@Column(name = "ITC_RECLAIMED_TAX_PERIOD")
	private String itcReclaimedTaxPeriod;

	@Column(name = "GSTR3B_FILINGDATE")
	private String gstr3bFilingDate;

	@Column(name = "SUPPLIERGSTIN_STATUS")
	private String supplierGSTINStatus;

	@Column(name = "BILL_OF_ENTRY_CREATED_DATE_2B")
	private String billOfEntryCreatedDate2B;

	@Column(name = "VENDOR_TAX_PAID_VARIANCE")
	private String vendorTaxPaidVariance;

	@Column(name = "VENDOR_TYPE")
	private String vendorType;

	@Column(name = "HSN_VENDOR")
	private String hsnVendor;

	@Column(name = "VENDOR_RISK_CATEGORY")
	private String vendorRiskCategory;

	@Column(name = "VENDOR_PAYMENT_TERMS_DAYS")
	private String vendorPaymentTermsDays;

	@Column(name = "VENDOR_REMARKS")
	private String vendorRemarks;

	@Column(name = "SYSTEMDEFINEDFIELD1")
	private String systemDefinedField1;

	@Column(name = "SYSTEMDEFINEDFIELD2")
	private String systemDefinedField2;

	@Column(name = "SYSTEMDEFINEDFIELD3")
	private String systemDefinedField3;

	@Column(name = "SYSTEMDEFINEDFIELD4")
	private String systemDefinedField4;

	@Column(name = "SYSTEMDEFINEDFIELD5")
	private String systemDefinedField5;
	
	@Column(name = "IMS_USER_RESPONSE")
	private String imsUserResponse;
	
	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;
	
	@Column(name = "IMS_RESPONSE_REMARKS")
	private String imsResponseRemarks;
	
	@Column(name = "ACTION_GSTN")
	private String actionGstn;
	
	@Column(name = "IS_PENDING_ACTION_BLOCKED")
	private String isPendingActionBlocked;

	@Column(name = "GETCALL_DATE_TIME")
	private String getCallTime;

	@Column(name = "ACTION_DIGI")
	private String actionDigigst;
	
	@Column(name = "ACTION_DIGIGST_DATE_TIME")
	private String digigstDateTime;

	@Column(name = "IS_SAVED_TO_GSTIN")
	private String isSaveToGstn;
	
	@Column(name = "AVAILABLE_IN_IMS")
	private String avalInIms;

	@Column(name = "TotalTax_Diff")
	private String totalTaxDiff;

	@Column(name = "Total_Tax_PR_2B")
	private String prTaxGreaterThan2B;

	@Column(name = "Total_Tax_2B_PR")
	private String Tax2BGreaterThanPr;

}
