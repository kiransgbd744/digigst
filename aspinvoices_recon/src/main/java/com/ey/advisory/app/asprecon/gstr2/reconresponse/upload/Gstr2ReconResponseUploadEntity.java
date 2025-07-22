package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mohit.basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_STG_RECON_RESPONSE")
public class Gstr2ReconResponseUploadEntity {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_STG_RECON_RESPONSE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCHID")
	private String batchID;

	@Column(name = "INVOICEKEYPR")
	private String invoicekeyPR;

	@Column(name = "INVOICEKEYA2")
	private String invoicekeyA2;

	@Column(name = "FMRESPONSE")
	private String fMResponse;

	@Column(name = "ISFM")
	private String isFM;

	@Column(name = "RSPTAXPERIOD3B")
	private String rspTaxPeriod3B;

	@Column(name = "TAXPERIOD2A")
	private String taxPeriod2A;

	@Column(name = "TAXPERIODPR")
	private String taxPeriodPR;

	@Column(name = "CALENDAR_MONTH_PR")
	private String calanderMonthPR;

	@Column(name = "RGSTIN2A")
	private String rGSTIN2A;

	@Column(name = "RGSTINPR")
	private String rGSTINPR;

	@Column(name = "SGSTIN2A")
	private String sGSTIN2A;

	@Column(name = "SGSTINPR")
	private String sGSTINPR;

	@Column(name = "DOCTYPE2A")
	private String docType2A;

	@Column(name = "DOCTYPEPR")
	private String docTypePR;

	@Column(name = "DOCUMENTNUMBERPR")
	private String documentNumberPR;

	@Column(name = "DOCUMENTNUMBER2A")
	private String documentNumber2A;

	@Column(name = "DOCDATE2A")
	private String docDate2A;

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

	@Column(name = "IGST2A")
	private String iGST2A;

	@Column(name = "CGST2A")
	private String cGST2A;

	@Column(name = "SGST2A")
	private String sGST2A;

	@Column(name = "CESS2A")
	private String cESS2A;

	@Column(name = "TAXABLE2A")
	private String taxable2A;

	@Column(name = "TABLETYPE")
	private String tableType;

	@Column(name = "IDPR")
	private String iDPR;

	@Column(name = "ID2A")
	private String iD2A;

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

	@Column(name = "PREVIOUS_REPORT_TYPE_2A")
	private String previousReportType2A;

	@Column(name = "PREVIOUS_REPORT_TYPE_PR")
	private String previousReportTypePR;

	@Column(name = "TAX_PERIOD_2B")
	private String taxPeriod2B;

	@Column(name = "SUPPLIER_LEGAL_NAME_2A")
	private String supplierLegalName2A;

	@Column(name = "SUPPLIER_TRADE_NAME_2A")
	private String supplierTradeName2A;

	@Column(name = "SUPPLIER_NAME_PR")
	private String supplierNamePR;

	@Column(name = "POS_2A")
	private String pos2A;

	@Column(name = "POS_PR")
	private String posPR;

	@Column(name = "TOTAL_TAX_2A")
	private String totalTax2A;

	@Column(name = "TOTAL_TAX_PR")
	private String totalTaxPR;

	@Column(name = "INVOICE_VALUE_2A")
	private String invoiceValue2A;

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

	@Column(name = "REVERSE_CHARGEFLAG_2A")
	private String reverseChargeFlag2A;

	@Column(name = "DIFFERENTIAL_PERCENTAGE_2A")
	private String differentialPercentage2A;

	@Column(name = "DIFFERENTIAL_PERCENTAGE_PR")
	private String differentialPercentagePR;

	@Column(name = "ORG_DOC_NUMBER_2A")
	private String orgDocNumber2A;

	@Column(name = "ORG_DOC_NUMBER_PR")
	private String orgDocNumberPR;

	@Column(name = "ORG_DOC_DATE_2A")
	private String orgDocDate2A;

	@Column(name = "ORG_DOC_DATE_PR")
	private String orgDocDatePR;

	@Column(name = "ORG_SUPPLIER_GSTIN_PR")
	private String orgSupplierGstinPR;

	@Column(name = "ORG_SUPPLIER_NAME_PR")
	private String orgSupplierNamePR;

	@Column(name = "CRDR_PRE_GST_2A")
	private String crdrPreGst2A;

	@Column(name = "CRDR_PRE_GST_PR")
	private String crdrPreGstPR;

	@Column(name = "BOE_REFERENCE_DATE")
	private String boeReferenceDate;

	@Column(name = "PORT_CODE_2A")
	private String portCode2A;

	@Column(name = "BILL_OF_ENTRY_2A")
	private String billOfEntry2A;

	@Column(name = "BILL_OF_ENTRY_DATE_2A")
	private String billOfEntryDate2A;

	@Column(name = "BOE_AMENDED")
	private String boeAmended;

	@Column(name = "USER_ID")
	private String userID;

	@Column(name = "SOURCE_FILE_NAME")
	private String sourceFileName;

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

	@Column(name = "SUPPLY_TYPE_2A")
	private String supplyType2A;

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

	@Column(name = "CESS_SPECIFIC_AMOUNT")
	private String cessSpecificAmt;

	@Column(name = "STATE_CESS_ADVALOREM_AMOUNT")
	private String stateCessAdvaloremAmt;

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

	@Column(name = "REFERENCE_ID2A")
	private String refID2A;

	@Column(name = "IRN_2A")
	private String irn2A;

	@Column(name = "IRN_PR")
	private String irnPR;

	@Column(name = "IRNDATE_2A")
	private String irnDate2A;

	@Column(name = "IRNDATE_PR")
	private String irnDatePR;

	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;

	@Column(name = "SUPP_PAN_2A")
	private String supplierPan2A;

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
	
	//new added
	@Column(name = "REVERSE_INTEGRATION_DATE")
	private String reverseInteDate;

	@Column(name = "QR_CODE_CHECK")
	private String qrCodeCheck;
	
	@Column(name = "QR_CODE_VALIDATION_RESULT")
	private String qrCodeValidationResult;
	
	@Column(name = "QR_CODE_MATCH_COUNT")
	private String qrCodeMatchCount;
	
	@Column(name = "QR_CODE_MISMATCH_COUNT")
	private String qrCodeMismatchCount;
	
	@Column(name = "QR_MISMATCH_ATTRIBUTES")
	private String qrMismatchAttributes;
	
	@Column(name = "VENDOR_TYPE")
	private String vendorType;
	
	@Column(name = "VENDOR_RISK_CATEGORY")
	private String vendorRiskCategory;
	
	@Column(name = "VENDOR_PAYMENT_TERMS_DAYS")
	private String vendorPaymentTermsDays;
	
	@Column(name = "VENDOR_REMARKS")
	private String vendorRemarks;
	
	@Column(name = "COMPANY_CODE")
	private String companyCode;
	
	@Column(name = "SOURCE_IDENTIFIER")
	private String sourceIdentifier;
	
	@Column(name = "VENDOR_COMPLIANCE_TREND")
	private String vendorComplianceTrend;
	
	//new added
	@Column(name = "SYSTEM_DEFINED_FIELD_1")
	private String systemDefinedField1;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_2")
	private String systemDefinedField2;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_3")
	private String systemDefinedField3;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_4")
	private String systemDefinedField4;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_5")
	private String systemDefinedField5;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_6")
	private String systemDefinedField6;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_7")
	private String systemDefinedField7;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_8")
	private String systemDefinedField8;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_9")
	private String systemDefinedField9;
	
	@Column(name = "SYSTEM_DEFINED_FIELD_10")
	private String systemDefinedField10;
	
	@Column(name = "GSTR3B_FILING_DATE")
	private String gstr3bFilingDate;
	
	@Column(name = "SUPPLIER_GSTIN_STATUS")
	private String supplierGstinSTatus;
	
	//Ims changes
			
	@Column(name = "IMS_USER_RESPONSE")
	private String imsUserResponse;
	
	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;
	
	@Column(name = "IMS_RESPONSE_REMARKS")
	private String imsResponseRemark;
	
	@Column(name = "IMS_ACTION_GSTIN")
	private String imsActionGstn;
	
	@Column(name = "IMS_PENDING_ACTION_BLOCKED")
	private String imsPendingActionBlocked;
	
	@Column(name = "IMS_GETCALL_DATE_TIME")
	private String imsGetCallDateTime;
	
	@Column(name = "IMS_ACTION_DIGI")
	private String imsActionDigi;
	
	@Column(name = "IMS_ACTION_DIGI_DATE_TIME")
	private String imsActionDigiDateTime;
	
	@Column(name = "IMS_SAVED_TO_GSTIN")
	private String imsSavedToGstn;
	
	@Column(name = "IMS_IS_ACTIVE")
	private String ImsIsActive;
	
	
}
