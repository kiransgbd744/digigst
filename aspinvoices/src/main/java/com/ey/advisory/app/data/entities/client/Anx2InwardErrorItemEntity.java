package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "ANX_INWARD_ERROR_ITEM")
@Setter
@Getter
@ToString
public class Anx2InwardErrorItemEntity extends InwardHeaderAndItemEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_INWARD_ERROR_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("custOrSupAddr1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSupAddr2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	@Expose
	@SerializedName("custOrSupAddr3")
	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;

	@Expose
	@SerializedName("custOrSupAddr4")
	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;

	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_NO")
	private String lineNo;

	@Expose
	@SerializedName("itemDesc")
	@Column(name = "ITM_DESCRIPTION")
	private String itemDescription;

	@Expose
	@SerializedName("hsnsacCode")
	@Column(name = "ITM_HSNSAC")
	private String hsnSac;

	@Expose
	@SerializedName("itemUqc")
	@Column(name = "ITM_UQC")
	private String uom;

	@Expose
	@SerializedName("itemQty")
	@Column(name = "ITM_QTY")
	private String qty;

	@Expose
	@SerializedName("taxableVal")
	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Expose
	@SerializedName("igstRt")
	@Column(name = "IGST_RATE")
	private String igstRate;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private String igstAmount;

	@Expose
	@SerializedName("cgstRt")
	@Column(name = "CGST_RATE")
	private String cgstRate;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private String cgstAmount;

	@Expose
	@SerializedName("sgstRt")
	@Column(name = "SGST_RATE")
	private String sgstRate;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private String sgstAmount;

	@Expose
	@SerializedName("cessRtAdvalorem")
	@Column(name = "CESS_RATE_ADVALOREM")
	private String cessRateAdvalorem;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	private String cessAmountAdvalorem;

	@Expose
	@SerializedName("cessRtSpecific")
	@Column(name = "CESS_RATE_SPECIFIC")
	private String cessRateSpecific;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@Column(name = "CESS_AMT_SPECIFIC")
	private String cessAmountSpecific;

	@Expose
	@SerializedName("stateCessRt")
	@Column(name = "STATECESS_RATE")
	private String stateCessRate;

	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATECESS_AMT")
	private String stateCessAmount;

	@Expose
	@SerializedName("otherValues")
	@Column(name = "OTHER_VALUES")
	private String otherValues;

	@Expose
	@SerializedName("lineItemAmt")
	@Column(name = "LINE_ITEM_AMT")
	private String lineItemAmt;

	@Expose
	@SerializedName("contractNumber")
	@Column(name = "CONTRACT_NUMBER")
	private String contractNumber;

	@Expose
	@SerializedName("eligibilityIndicator")
	@Column(name = "ELIGIBILITY_INDICATOR")
	private String eligibilityIndicator;

	@Expose
	@SerializedName("commonSupplyIndicator")
	@Column(name = "COMMON_SUP_INDICATOR")
	private String commonSupplyIndicator;

	@Expose
	@SerializedName("availableIgst")
	@Column(name = "AVAILABLE_IGST")
	private String availableIgst;

	@Expose
	@SerializedName("availableCgst")
	@Column(name = "AVAILABLE_CGST")
	private String availableCgst;

	@Expose
	@SerializedName("availableSgst")
	@Column(name = "AVAILABLE_SGST")
	private String availableSgst;

	@Expose
	@SerializedName("availableCess")
	@Column(name = "AVAILABLE_CESS")
	private String availableCess;

	@Expose
	@SerializedName("itcReversalIdentifier")
	@Column(name = "ITC_REVERSAL_IDENTIFER")
	private String itcReversalIdentifier;

	@Expose
	@SerializedName("contractDate")
	@Column(name = "CONTRACT_DATE")
	private String contractDate;

	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String crDrPreGst;

	@Expose
	@SerializedName("custOrSupCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("productCode")
	@Column(name = "PRODUCT_CODE")
	private String itemCode;

	@Expose
	@SerializedName("itemType")
	@Column(name = "ITM_TYPE")
	private String itemCategory;

	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;

	@Expose
	@SerializedName("cifValue")
	@Column(name = "CIF_VALUE")
	private String cifValue;

	@Expose
	@SerializedName("customDuty")
	@Column(name = "CUSTOM_DUTY")
	private String customDuty;

	@Expose
	@SerializedName("exchangeRt")
	@Column(name = "EXCHANGE_RATE")
	protected String exchangeRate;

	@Expose
	@SerializedName("crDrReason")
	@Column(name = "CRDR_REASON")
	private String crDrReason;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("profitCentre3")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@Column(name = "USERACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@Column(name = "USERACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("glCodeTaxableVal")
	@Column(name = "GLCODE_TAXABLEVALUE")
	private String glCodeTaxableValue;

	@Expose
	@SerializedName("glCodeIgst")
	@Column(name = "GLCODE_IGST")
	private String glCodeIgst;

	@Expose
	@SerializedName("glCodeCgst")
	@Column(name = "GLCODE_CGST")
	private String glCodeCgst;

	@Expose
	@SerializedName("glCodeSgst")
	@Column(name = "GLCODE_SGST")
	private String glCodeSgst;

	@Expose
	@SerializedName("glCodeAdvCess")
	@Column(name = "GLCODE_ADV_CESS")
	private String glCodeAdvCess;

	@Expose
	@SerializedName("glCodeSpCess")
	@Column(name = "GLCODE_SP_CESS")
	private String glCodeSpCess;

	@Expose
	@SerializedName("glCodeStateCess")
	@Column(name = "GLCODE_STATE_CESS")
	private String glCodeStateCess;

	@Expose
	@SerializedName("contractValue")
	@Column(name = "CONTRACT_VALUE")
	private String contractValue;

	@Expose
	@SerializedName("purchaseVoucherNum")
	@Column(name = "PURCHASE_VOUCHER_NUM")
	private String purchaseVoucherNum;

	@Expose
	@SerializedName("udf1")
	@Column(name = "USERDEFINED_FIELD1")
	private String userDefinedField1;

	@Expose
	@SerializedName("udf2")
	@Column(name = "USERDEFINED_FIELD2")
	private String userDefinedField2;

	@Expose
	@SerializedName("udf3")
	@Column(name = "USERDEFINED_FIELD3")
	private String userDefinedField3;

	@Expose
	@SerializedName("udf4")
	@Column(name = "USERDEFINED_FIELD4")
	private String userDefinedField4;

	@Expose
	@SerializedName("udf5")
	@Column(name = "USERDEFINED_FIELD5")
	private String userDefinedField5;

	@Expose
	@SerializedName("udf6")
	@Column(name = "USERDEFINED_FIELD6")
	private String userDefinedField6;

	@Expose
	@SerializedName("udf7")
	@Column(name = "USERDEFINED_FIELD7")
	private String userDefinedField7;

	@Expose
	@SerializedName("udf8")
	@Column(name = "USERDEFINED_FIELD8")
	private String userDefinedField8;

	@Expose
	@SerializedName("udf9")
	@Column(name = "USERDEFINED_FIELD9")
	private String userDefinedField9;

	@Expose
	@SerializedName("udf10")
	@Column(name = "USERDEFINED_FIELD10")
	private String userDefinedField10;

	@Expose
	@SerializedName("udf11")
	@Column(name = "USERDEFINED_FIELD11")
	private String userDefinedField11;

	@Expose
	@SerializedName("udf12")
	@Column(name = "USERDEFINED_FIELD12")
	private String userDefinedField12;

	@Expose
	@SerializedName("udf13")
	@Column(name = "USERDEFINED_FIELD13")
	private String userDefinedField13;

	@Expose
	@SerializedName("udf14")
	@Column(name = "USERDEFINED_FIELD14")
	private String userDefinedField14;

	@Expose
	@SerializedName("udf15")
	@Column(name = "USERDEFINED_FIELD15")
	private String userDefinedField15;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected String isDeleted;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private String isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected String isInfo;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime updatedDate;
	
	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Anx2InwardErrorHeaderEntity document;

}