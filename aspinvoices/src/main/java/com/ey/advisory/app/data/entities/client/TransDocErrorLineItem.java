package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents the basic information contained at a line item level
 * for all financial documents. The class is marked as abstract so that 
 * relevant subclasses can be used to instantiate.
 * 
 * @author Umesha M
 *
 */
@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class TransDocErrorLineItem extends BasicLineItemErrorValues {
	
	/**
	 * This is a line number field given by the user. This can contain errors
	 * or can be empty or null. Hence this field cannot be reliably used for
	 * identifying the order of line numbers in the input document.
	 */
	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_NO")
	protected String lineNo;

	@Expose
	@SerializedName("hsnsacCode")
	@Column(name = "ITM_HSNSAC")
	protected String hsnSac;
	
	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("productCode")
	@Column(name = "PRODUCT_CODE")
	protected String itemCode;

	@Expose
	@SerializedName("itemDesc")
	@Column(name = "ITM_DESCRIPTION")
	protected String itemDescription;

	@Expose
	@SerializedName("itemType")
	@Column(name = "ITM_TYPE")
	protected String itemCategory;

	@Expose
	@SerializedName("itemUqc")
	@Column(name = "ITM_UQC")
	protected String uom;
	
	@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	protected String origDocDate;
	
	@Expose
	@SerializedName("glCodeTaxableVal")
	@Column(name = "GLCODE_TAXABLEVALUE")
	protected String glCodeTaxableValue;
	
	@Expose
	@SerializedName("glCodeIgst")
	@Column(name = "GLCODE_IGST")
	protected String glCodeIgst;
	
	@Expose
	@SerializedName("glCodeCgst")
	@Column(name = "GLCODE_CGST")
	protected String glCodeCgst;
	
	@Expose
	@SerializedName("glCodeSgst")
	@Column(name = "GLCODE_SGST")
	protected String glCodeSgst;
	
	@Expose
	@SerializedName("glCodeAdvCess")
	@Column(name = "GLCODE_ADV_CESS")
	protected String glCodeAdvCess;
	
	@Expose
	@SerializedName("glCodeSpCess")
	@Column(name = "GLCODE_SP_CESS")
	protected String glCodeSpCess;
	
	@Expose
	@SerializedName("glCodeStateCess")
	@Column(name = "GLCODE_STATE_CESS")
	protected String glCodeStateCess;
	
	@Expose
	@SerializedName("adjustmentRefNo")
	@Column(name = "ADJ_REF_NO")
	protected String adjustmentRefNo;
	
	@Expose
	@SerializedName("adjustmentRefDate")
	@Column(name = "ADJ_REF_DATE")
	protected String adjustmentRefDate;
	
	@Expose
	@SerializedName("adjustedTaxableValue")
	@Column(name = "ADJ_TAXABLE_VALUE")
	protected String adjustedTaxableValue;
	
	@Expose
	@SerializedName("adjustedIgstAmt")
	@Column(name = "ADJ_IGST_AMT")
	protected String adjustedIgstAmt;
	
	@Expose
	@SerializedName("adjustedCgstAmt")
	@Column(name = "ADJ_CGST_AMT")
	protected String adjustedCgstAmt;
	
	@Expose
	@SerializedName("adjustedSgstAmt")
	@Column(name = "ADJ_SGST_AMT")
	protected String adjustedSgstAmt;
	
	@Expose
	@SerializedName("adjustedCessAmtAdvalorem")
	@Column(name = "ADJ_CESS_AMT_ADVALOREM")
	protected String adjustedCessAmtAdvalorem;
	
	@Expose
	@SerializedName("adjustedCessAmtSpecific")
	@Column(name = "ADJ_CESS_AMT_SPECIFIC")
	protected String adjustedCessAmtSpecific;
	
	@Expose
	@SerializedName("adjustedStateCessAmt")
	@Column(name = "ADJ_STATECESS_AMT")
	protected String adjustedStateCessAmt;
	
	@Expose
	@SerializedName("tcsAmount")
	@Column(name = "TCS_AMT")
	protected String tcsAmount;

	@Expose
	@SerializedName("itemQty")
	@Column(name = "ITM_QTY")
	protected String qty;
	
	@Expose
	@SerializedName("lineItemAmt")
	@Column(name = "LINE_ITEM_AMT")
	private String lineItemAmt;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;
	
	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;
	
	@Expose
	@SerializedName("salesOrgnization")
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrgnization;
	
	@Expose
	@SerializedName("distChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;
	
	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;
	
	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;
	
	
	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USERACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USERACCESS6")
	protected String userAccess6;
	
	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;
	
	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;
	
	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;
	
	@Expose
	@SerializedName("originalDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String origDocType;
	
	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String isCrDrPreGst;
	
	@Expose
	@SerializedName("origCgstin")
	@Column(name = "ORIGINAL_CUST_GSTIN")
	protected String origCgstin;
	
	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;
	
	@Expose
	@SerializedName("custOrSuppAddress1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSuppAddress2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	/*@Expose
	@SerializedName("custOrSuppAddress3")
	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;*/

	@Expose
	@SerializedName("custOrSuppAddress4")
	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;
	
	@Expose
	@SerializedName("userDefinedField1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("userDefinedField2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("userDefinedField3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;
	
	@Expose
	@SerializedName("userDefinedField4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;
	
	@Expose
	@SerializedName("userDefinedField5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userDefinedField5;

	@Expose
	@SerializedName("userDefinedField6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userDefinedField6;

	@Expose
	@SerializedName("userDefinedField7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userDefinedField7;

	@Expose
	@SerializedName("userDefinedField8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("userDefinedField9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userDefinedField9;

	@Expose
	@SerializedName("userDefinedField10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userDefinedField10;

	@Expose
	@SerializedName("userDefinedField11")
	@Column(name = "USERDEFINED_FIELD11")
	protected String userDefinedField11;

	@Expose
	@SerializedName("userDefinedField12")
	@Column(name = "USERDEFINED_FIELD12")
	protected String userDefinedField12;

	@Expose
	@SerializedName("userDefinedField13")
	@Column(name = "USERDEFINED_FIELD13")
	protected String userDefinedField13;

	@Expose
	@SerializedName("userDefinedField14")
	@Column(name = "USERDEFINED_FIELD14")
	protected String userDefinedField14;

	@Expose
	@SerializedName("userDefinedField15")
	@Column(name = "USERDEFINED_FIELD15")
	protected String userDefinedField15;
	
	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;
	
	@Expose
	@SerializedName("billToState")
	@Column(name = "BILL_TO_STATE")
	protected String billToState;

	@Expose
	@SerializedName("shipToState")
	@Column(name = "SHIP_TO_STATE")
	protected String shipToState;
	
	@Expose
	@SerializedName("shippingBillNo")
	@Column(name = "SHIP_BILL_NUM")
	protected String shippingBillNo;

	@Expose
	@SerializedName("shippingBillDate")
	@Column(name = "SHIP_BILL_DATE")
	protected String shippingBillDate;
	
	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;
	
	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos; // State code

	@Expose
	@SerializedName("reverseCharge")
	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;
	
	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;
	
	@Expose
	@SerializedName("ecomCustGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;
	
	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;
	
	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;
	
	@Expose
	@SerializedName("accountVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accountVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected String accountingVoucherDate;
	
	@Expose
	@SerializedName("eWayBillNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	@Expose
	@SerializedName("eWayBillDate")
	@Column(name = "EWAY_BILL_DATE")
	protected String eWayBillDate;
	
	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;
	
	@Expose
	@SerializedName("custOrSuppCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("custOrSuppType")
	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;
	
	@Expose
	@SerializedName("custOrSuppName")
	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;
	
	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;
}
