package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author vishal.verma
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@ToString
public class VendorMismatchDto implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="ENTITY")
    private String entity;
	
	@XmlElement(name="ENTITY_NAME")
	private String entityName;
	
	@XmlElement(name="COMPANY_CODE")
	private String companyCode;
	
	@XmlElement(name="USER_RESPONSE")
	private String userResponse;
	
	@XmlElement(name="SUG_RESPONSE")
	private String suggestedResponse;
	
	@XmlElement(name="CURRENT_RR")
	private String currentReportType;
	
	@XmlElement(name="CUR_MIS_REASON")
	private String currentMismatchReason;
	
	@XmlElement(name="FORCED_MAT_RESP")
	private String forcedMatchResponse;
	
	@XmlElement(name="ITC_E_A2")
	private String iTCEntitlementAnx2;
	
	@XmlElement(name="ITC_E_PR")
	private String iTCEntitlementPR;
	
	@XmlElement(name="INFO_REP_REF")
	private String informationReportReference;
	
	@XmlElement(name="RET_PER")
	private String reconTaxPeriod;
	
	@XmlElement(name="PREV_RT_A2")
	private String previousReportTypeANX2;
	
	@XmlElement(name="PREV_RT_PR")
	private String previousReportTypePR;
	
	@XmlElement(name="PREV_RES_A2")
	private String previousResponseANX2;
	
	@XmlElement(name="PREV_RES_PR")
	private String previousResponsePR;
	
	@XmlElement(name="SAVE_RES_RT_A2")
	private String savedResponseReportTypeANX2;
	
	@XmlElement(name="DGST_S_RES_A2")
	private String digiGSTSavedResponseANX2;
	
	@XmlElement(name="GSTN_S_RES_A2")
	private String gSTNSavedResponseANX2;
	
	@XmlElement(name="MATCHINGSCORE")
	private String matchingScore;
	
	@XmlElement(name="RECONTAXPERIOD")
	private String respTaxPeriod;
	
	@XmlElement(name="CAL_MONTH_PR")
	private String calendarMonthPR;
	
	@XmlElement(name="DATE_UPLOAD_A2")
	private String dateofUploadANX2;
	
	@XmlElement(name="ORG_MONTH_A2")
	private String orgMonthANX2;
	
	@XmlElement(name="ORG_MONTH_PR")
	private String orgMonthPR;
	
	@XmlElement(name="REP_GSTIN_A2")
	private String recipientGSTINANX2;
	
	@XmlElement(name="GSTIN_NUM")
	private String recipientGSTINPR;
	
	@XmlElement(name="SUP_GSTIN_A2")
	private String supplierGSTINANX2;
	
	@XmlElement(name="SUP_GSTIN_PR")
	private String supplierGSTINPR;
	
	@XmlElement(name="SUP_NAME_A2")
	private String supplierNameANX2;
	
	@XmlElement(name="SUP_NAME_PR")
	private String supplierNamePR;
	
	@XmlElement(name="DOC_TYPE_A2")
	private String docTypeANX2;
	
	@XmlElement(name="DOC_TYPE_PR")
	private String docTypePR;
	
	@XmlElement(name="DOC_NUM_A2")
	private String documentNumberANX2;
	
	@XmlElement(name="DOC_NUM_PR")
	private String documentNumberPR;
	
	@XmlElement(name="DOC_DATE_A2")
	private String documentDateANX2;
	
	@XmlElement(name="DOC_DATE_PR")
	private String documentDatePR;
	
	@XmlElement(name="POS_A2")
	private String pOSANX2;
	
	@XmlElement(name="POS_PR")
	private String pOSPR;
	
	@XmlElement(name="TAXABLE_A2")
	private String taxableValueANX2;
	
	@XmlElement(name="TAXABLE_PR")
	private String taxableValuePR;
	
	@XmlElement(name="TAX_A2")
	private String totalTaxANX2;
	
	@XmlElement(name="TAX_PR")
	private String totalTaxPR;
	
	@XmlElement(name="IGST_A2")
	private String iGSTANX2;
	
	@XmlElement(name="IGST_PR")
	private String iGSTPR;
	
	@XmlElement(name="CGST_A2")
	private String cGSTANX2;
	
	@XmlElement(name="CGST_PR")
	private String cGSTPR;
	
	@XmlElement(name="SGST_A2")
	private String sGSTANX2;
	
	@XmlElement(name="SGST_PR")
	private String sGSTPR;
	
	@XmlElement(name="CESS_A2")
	private String cessANX2;
	
	@XmlElement(name="CESS_PR")
	private String cessPR;
	
	@XmlElement(name="INV_A2")
	private String invoiceValueANX2;
	
	@XmlElement(name="INV_PR")
	private String invoiceValuePR;
	
	@XmlElement(name="A_IGST")
	private String availableIGST;
	
	@XmlElement(name="A_CGST")
	private String availableCGST;
	
	@XmlElement(name="A_SGST")
	private String availableSGST;
	
	@XmlElement(name="A_CESS")
	private String availableCESS;
	
	@XmlElement(name="T_TYPE_A2")
	private String tableTypeANX2;
	
	@XmlElement(name="T_TYPE_PR")
	private String tableTypePR;
	
	@XmlElement(name="CFS_FLAG_A2")
	private String cFSFlagANX2;
	
	@XmlElement(name="CR_FLAG_A2")
	private String claimRefundFlagANX2;
	
	@XmlElement(name="CR_FLAG_PR")
	private String claimRefundFlagPR;
	
	@XmlElement(name="S7_IGST_A2")
	private String section7ofIGSTANX2;
	
	@XmlElement(name="S7_IGST_PR")
	private String section7ofIGSTPR;
	
	@XmlElement(name="APTR_A2")
	private String autoPopulateToRefundANX2;
	
	@XmlElement(name="APTR_PR")
	private String autoPopulateToRefundPR;
	
	@XmlElement(name="DP_A2")
	private String differentialPercentageANX2;
	
	@XmlElement(name="DP_PR")
	private String differentialPercentagePR;
	
	@XmlElement(name="OTD_A2")
	private String orgDocTypeANX2;
	
	//@XmlElement(name="")
	//private String orgDocTypePR;
	
	@XmlElement(name="ODN_A2")
	private String orgDocNumberANX2;
	
	@XmlElement(name="ODN_PR")
	private String orgDocNumberPR;
	
	@XmlElement(name="ODD_A2")
	private String orgDocDateANX2;
	
	@XmlElement(name="ODD_PR")
	private String orgDocDatePR;
	
	@XmlElement(name="OS_GSTIN_PR")
	private String orgSupplierGSTINPR;
	
	@XmlElement(name="USERID")
	private String userID;
	
	@XmlElement(name="S_FNAME")
	private String sourceFileName;
	
	@XmlElement(name="PROFIT_CENTER")
	private String profitCentre;
	
	@XmlElement(name="PLANT_CODE")
	private String plant;
	
	@XmlElement(name="DIVISION")
	private String division;
	
	@XmlElement(name="STO_LOCATION")
	private String location;
	
	@XmlElement(name="PURCHASE_ORG")
	private String purchaseOrganisation;
	
	@XmlElement(name="USERACCESS1")
	private String userAccess1;
	
	@XmlElement(name="USERACCESS2")
	private String userAccess2;
	
	@XmlElement(name="USERACCESS3")
	private String userAccess3;
	
	@XmlElement(name="USERACCESS4")
	private String userAccess4;
	
	@XmlElement(name="USERACCESS5")
	private String userAccess5;
	
	@XmlElement(name="USERACCESS6")
	private String userAccess6;
	
	@XmlElement(name="GL_TAXABLE")
	private String glCodeTaxableValue;
	
	@XmlElement(name="GL_IGST")
	private String glCodeIGST;
	
	@XmlElement(name="GL_CGST")
	private String glCodeCGST;
	
	@XmlElement(name="GL_SGST")
	private String glCodeSGST;
	
	@XmlElement(name="GL_AD_CESS")
	private String glCodeAdvaloremCess;
	
	@XmlElement(name="GL_SP_CESS")
	private String glCodeSpecificCess;
	
	@XmlElement(name="GL_ST_CESS")
	private String glCodeStateCess;
	
	@XmlElement(name="SUPPLYTYPE")
	private String supplyType;
	
	@XmlElement(name="CRDR_PGST")
	private String cRDRPreGST;
	
	@XmlElement(name="SUPPLIERTYPE")
	private String supplierType;
	
	@XmlElement(name="SUPPLIERCODE")
	private String supplierCode;
	
	@XmlElement(name="SUPPLIERADDRESS1")
	private String supplierAddress1;
	
	@XmlElement(name="SUPPLIERADDRESS2")
	private String supplierAddress2;
	
	@XmlElement(name="SUPPLIERADDRESS3")
	private String supplierAddress3;
	
	@XmlElement(name="SUPPLIERADDRESS4")
	private String supplierAddress4;
	
	@XmlElement(name="ST_APPLY_CESS")
	private String stateApplyingCess;
	
	@XmlElement(name="PORTCODE")
	private String portCode;
	
	@XmlElement(name="BILL_ENTRY")
	private String billOfEntry;
	
	@XmlElement(name="BILL_ENTRY_D")
	private String billOfEntryDate;
	
	@XmlElement(name="CIFVALUE")
	private String cIFValue;
	
	@XmlElement(name="CUSTOMDUTY")
	private String customDuty;
	
	@XmlElement(name="QUANTITY")
	private String quantity;
	
	@XmlElement(name="CESS_AD")
	private String cessAmountAdvalorem;
	
	@XmlElement(name="CESS_SP")
	private String cessAmountSpecific;
	
	@XmlElement(name="ST_CESS_A")
	private String stateCessAmount;
	
	@XmlElement(name="OTHERVALUE")
	private String otherValue;
	
	@XmlElement(name="PUR_VOUCHER_N")
	private String purchaseVoucherNumber;
	
	@XmlElement(name="PUR_VOUCHER_D")
	private String purchaseVoucherDate;
	
	@XmlElement(name="POSTINGDATE")
	private String postingDate;
	
	@XmlElement(name="PA_VOUCHER_N")
	private String paymentVoucherNumber;
	
	@XmlElement(name="PAYMENTDATE")
	private String paymentDate;
	
	@XmlElement(name="CONTRACTNUMBER")
	private String contractNumber;
	
	@XmlElement(name="CONTRACTDATE")
	private String contractDate;
	
	@XmlElement(name="CONTRACTVALUE")
	private String contractValue;
	
	@XmlElement(name="E_WAYBILL_N")
	private String eWayBillNumber;
	
	@XmlElement(name="E_WAYBILL_D")
	private String eWayBillDate;
	
	@XmlElement(name="UD_FIELD1")
	private String userDefinedField1;
	
	@XmlElement(name="UD_FIELD2")
	private String userDefinedField2;
	
	@XmlElement(name="UD_FIELD3")
	private String userDefinedField3;
	
	@XmlElement(name="UD_FIELD4")
	private String userDefinedField4;
	
	@XmlElement(name="UD_FIELD5")
	private String userDefinedField5;
	
	@XmlElement(name="UD_FIELD6")
	private String userDefinedField6;
	
	@XmlElement(name="UD_FIELD7")
	private String userDefinedField7;
	
	@XmlElement(name="UD_FIELD8")
	private String userDefinedField8;
	
	@XmlElement(name="UD_FIELD9")
	private String userDefinedField9;
	
	@XmlElement(name="UD_FIELD10")
	private String userDefinedField10;
	
	@XmlElement(name="UD_FIELD11")
	private String userDefinedField11;
	
	@XmlElement(name="UD_FIELD12")
	private String userDefinedField12;
	
	@XmlElement(name="UD_FIELD213")
	private String userDefinedField13;
	
	@XmlElement(name="UD_FIELD214")
	private String userDefinedField14;
	
	@XmlElement(name="UD_FIELD215")
	private String userDefinedField15;
	
	@XmlElement(name="MATCHINGID")
	private String matchingID;
	
	@XmlElement(name="REQUESTID")
	private String requestID;
	
	@XmlElement(name="IDPR")
	private String iDPR;
	
	@XmlElement(name="ID2A")
	private String iDA2;

	
	public VendorMismatchDto(String entity) {
		super();
		this.entity = entity;
	}
	
	/**
	 * 
	 */
	public VendorMismatchDto() {
		super();
	}

	/**
	 * @param entity
	 * @param entityName
	 * @param companyCode
	 */
	public VendorMismatchDto(String entity, String entityName,
			String companyCode) {
		super();
		this.entity = entity;
		this.entityName = entityName;
		this.companyCode = companyCode;
	}


	
}
