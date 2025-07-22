package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * This class represents the basic information contained at a line item level
 * for all financial documents. The class is marked as abstract so that relevant
 * subclasses can be used to instantiate.
 * 
 * @author Sai.Pakanati
 *
 */
@MappedSuperclass
public abstract class TransDocLineItem extends BasicLineItemValues {

	/**
	 * This is a line number field given by the user. This can contain errors or
	 * can be empty or null. Hence this field cannot be reliably used for
	 * identifying the order of line numbers in the input document.
	 */

	// Required only if the doc is Credit/Debit Note.
	@Expose
	@SerializedName("crDrReason")
	@Column(name = "CRDR_REASON")
	protected String crDrReason;

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
	@SerializedName("glCodeTaxableVal")
	@Column(name = "GLCODE_TAXABLEVALUE")
	protected String glCodeTaxableValue;

	@Expose
	@SerializedName("adjustmentRefNo")
	@Column(name = "ADJ_REF_NO")
	protected String adjustmentRefNo;

	@Expose
	@SerializedName("adjustmentRefDate")
	@Column(name = "ADJ_REF_DATE")
	protected LocalDate adjustmentRefDate;

	@Expose
	@SerializedName("adjustedTaxableValue")
	@Column(name = "ADJ_TAXABLE_VALUE")
	protected BigDecimal adjustedTaxableValue;

	@Expose
	@SerializedName("adjustedIgstAmt")
	@Column(name = "ADJ_IGST_AMT")
	protected BigDecimal adjustedIgstAmt;

	@Expose
	@SerializedName("adjustedCgstAmt")
	@Column(name = "ADJ_CGST_AMT")
	protected BigDecimal adjustedCgstAmt;

	@Expose
	@SerializedName("adjustedSgstAmt")
	@Column(name = "ADJ_SGST_AMT")
	protected BigDecimal adjustedSgstAmt;

	@Expose
	@SerializedName("adjustedCessAmtAdvalorem")
	@Column(name = "ADJ_CESS_AMT_ADVALOREM")
	protected BigDecimal adjustedCessAmtAdvalorem;

	@Expose
	@SerializedName("adjustedCessAmtSpecific")
	@Column(name = "ADJ_CESS_AMT_SPECIFIC")
	protected BigDecimal adjustedCessAmtSpecific;

	@Expose
	@SerializedName("adjustedStateCessAmt")
	@Column(name = "ADJ_STATECESS_AMT")
	protected BigDecimal adjustedStateCessAmt;

	@Expose
	@SerializedName("tcsAmt")
	@Column(name = "TCS_AMT")
	protected BigDecimal tcsAmount;

	@Expose
	@SerializedName("itemQty")
	@Column(name = "ITM_QTY")
	protected BigDecimal qty;

	@Expose
	@SerializedName("lineItemAmt")
	@Column(name = "LINE_ITEM_AMT")
	private BigDecimal lineItemAmt;

	@Expose
	@SerializedName("taxPayable")
	@Column(name = "TAX_PAYABLE")
	protected BigDecimal taxPayable;

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

	/* Amendment details */
	@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	protected LocalDate origDocDate;

	@Expose
	@SerializedName("udf1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;

	@Expose
	@SerializedName("udf5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userDefinedField5;

	@Expose
	@SerializedName("udf6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userDefinedField6;

	@Expose
	@SerializedName("udf7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userDefinedField7;

	@Expose
	@SerializedName("udf8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("udf9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userDefinedField9;

	@Expose
	@SerializedName("udf10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userDefinedField10;

	@Expose
	@SerializedName("udf11")
	@Column(name = "USERDEFINED_FIELD11")
	protected String userDefinedField11;

	@Expose
	@SerializedName("udf12")
	@Column(name = "USERDEFINED_FIELD12")
	protected String userDefinedField12;

	@Expose
	@SerializedName("udf13")
	@Column(name = "USERDEFINED_FIELD13")
	protected String userDefinedField13;

	@Expose
	@SerializedName("udf14")
	@Column(name = "USERDEFINED_FIELD14")
	protected String userDefinedField14;

	@Expose
	@SerializedName("udf15")
	@Column(name = "USERDEFINED_FIELD15")
	protected String userDefinedField15;

	// Start - Fix for Defect Error code "ER0037"
	@Column(name = "USER_ID")
	protected String userId;

	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Column(name = "DIVISION")
	protected String division;

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

	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

	@Column(name = "CRDR_PRE_GST")
	protected String crDrPreGst;

	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;

	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;

	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;

	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;

	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;

	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;

	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;

	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;

	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;

	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	// End - Fix for Defect Error code "ER0037"

	// This field is added in line items for report purpose
	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

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

	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Column(name = "EWAY_BILL_DATE")
	protected LocalDateTime eWayBillDate;

	@Expose
	@SerializedName("invOtherCharges")
	@Column(name = "INV_OTHER_CHARGES")
	protected BigDecimal invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@Column(name = "INV_ASSESSABLE_AMT")
	protected BigDecimal invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@Column(name = "INV_IGST_AMT")
	protected BigDecimal invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@Column(name = "INV_CGST_AMT")
	protected BigDecimal invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@Column(name = "INV_SGST_AMT")
	protected BigDecimal invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@Column(name = "INV_CESS_ADVLRM_AMT")
	protected BigDecimal invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@Column(name = "INV_CESS_SPECIFIC_AMT")
	protected BigDecimal invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@Column(name = "INV_STATE_CESS_AMT")
	protected BigDecimal invoiceStateCessAmount;

	@Expose
	@SerializedName("udf28")
	@Column(name = "USERDEFINED_FIELD28")
	protected BigDecimal userDefinedField28;

	@Expose
	@SerializedName("ecomGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Expose
	@SerializedName("docCat")
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Expose
	@SerializedName("supEmail")
	@Column(name = "SUPP_EMAIL")
	protected String supplierEmail;

	@Expose
	@SerializedName("custEmail")
	@Column(name = "CUST_EMAIL")
	protected String customerEmail;

	@Expose
	@SerializedName("dispatcherTradeName")
	@Column(name = "DISPATCHER_TRADE_NAME")
	protected String dispatcherTradeName;

	@Expose
	@SerializedName("dispatcherBuildingNo")
	@Column(name = "DISPATCHER_BUILDING_NUM")
	protected String dispatcherBuildingNumber;

	@Expose
	@SerializedName("dispatcherBuildingName")
	@Column(name = "DISPATCHER_BUILDING_NAME")
	protected String dispatcherBuildingName;

	@Expose
	@SerializedName("dispatcherLocation")
	@Column(name = "DISPATCHER_LOCATION")
	protected String dispatcherLocation;

	@Expose
	@SerializedName("shipToLegalName")
	@Column(name = "SHIP_TO_LEGAL_NAME")
	protected String shipToLegalName;

	@Expose
	@SerializedName("shipToBuildingNo")
	@Column(name = "SHIP_TO_BUILDING_NUM")
	protected String shipToBuildingNumber;

	@Expose
	@SerializedName("shipToBuildingName")
	@Column(name = "SHIP_TO_BUILDING_NAME")
	protected String shipToBuildingName;

	@Expose
	@SerializedName("shipToLocation")
	@Column(name = "SHIP_TO_LOCATION")
	protected String shipToLocation;

	@Expose
	@SerializedName("serialNoII")
	@Column(name = "SERIAL_NUM2")
	private String serialNumberII;

	@Expose
	@SerializedName("productName")
	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Expose
	@SerializedName("isService")
	@Column(name = "IS_SERVICE")
	private String isService;

	@Expose
	@SerializedName("barcode")
	@Column(name = "BAR_CODE")
	private String barcode;

	@Expose
	@SerializedName("batchNameOrNo")
	@Column(name = "BATCH_NAME_OR_NUM")
	private String batchNameOrNumber;

	@Expose
	@SerializedName("batchExpiryDate")
	@Column(name = "BATCH_EXPIRY_DATE")
	private LocalDate batchExpiryDate;

	@Expose
	@SerializedName("warrantyDate")
	@Column(name = "WARRANTY_DATE")
	private LocalDate warrantyDate;

	@Expose
	@SerializedName("orderLineRef")
	@Column(name = "ORDER_ITEM_REFERENCE")
	private String orderLineReference;

	@Expose
	@SerializedName("attributeName")
	@Column(name = "ATTRIBUTE_NAME")
	protected String attributeName;

	@Expose
	@SerializedName("attributeValue")
	@Column(name = "ATTRIBUTE_VALUE")
	protected String attributeValue;

	@Expose
	@SerializedName("originCountry")
	@Column(name = "ORIGIN_COUNTRY")
	private String originCountry;

	@Expose
	@SerializedName("freeQuantity")
	@Column(name = "FREE_QTY")
	private BigDecimal freeQuantity;

	@Expose
	@SerializedName("unitPrice")
	@Column(name = "UNIT_PRICE")
	private BigDecimal unitPrice;

	@Expose
	@SerializedName("itemAmt")
	@Column(name = "ITEM_AMT_UP_QTY")
	private BigDecimal itemAmount;

	@Expose
	@SerializedName("itemDiscount")
	@Column(name = "ITEM_DISCOUNT")
	private BigDecimal itemDiscount;

	@Expose
	@SerializedName("preTaxAmt")
	@Column(name = "PRE_TAX_AMOUNT")
	private BigDecimal preTaxAmount;

	@Expose
	@SerializedName("stateCessSpecificRt")
	@Column(name = "STATE_CESS_SPECIFIC_RATE")
	protected BigDecimal stateCessSpecificRate;

	@Expose
	@SerializedName("stateCessSpecificAmt")
	@Column(name = "STATE_CESS_SPECIFIC_AMOUNT")
	protected BigDecimal stateCessSpecificAmt;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected BigDecimal invStateCessSpecificAmt;

	@Expose
	@SerializedName("totalItemAmt")
	@Column(name = "TOT_ITEM_AMT")
	private BigDecimal totalItemAmount;

	@Expose
	@SerializedName("tcsRtIncomeTax")
	@Column(name = "TCS_RATE_INCOME_TAX")
	protected BigDecimal tcsRateIncomeTax;

	@Expose
	@SerializedName("tcsAmtIncomeTax")
	@Column(name = "TCS_AMOUNT_INCOME_TAX")
	protected BigDecimal tcsAmountIncomeTax;

	@Expose
	@SerializedName("foreignCurrency")
	@Column(name = "FOREIGN_CURRENCY")
	protected String foreignCurrency;

	@Expose
	@SerializedName("countryCode")
	@Column(name = "COUNTRY_CODE")
	protected String countryCode;

	@Expose
	@SerializedName("invValueFc")
	@Column(name = "INV_VAL_FC")
	protected BigDecimal invoiceValueFc;

	@Expose
	@SerializedName("invPeriodStartDate")
	@Column(name = "INV_PERIOD_START_DATE")
	protected LocalDate invoicePeriodStartDate;

	@Expose
	@SerializedName("invPeriodEndDate")
	@Column(name = "INV_PERIOD_END_DATE")
	protected LocalDate invoicePeriodEndDate;

	@Expose
	@SerializedName("invRef")
	@Column(name = "INV_REFERENCE")
	private String invoiceReference;

	@Expose
	@SerializedName("receiptAdviceRef")
	@Column(name = "RECEIPT_ADVICE_REFERENCE")
	protected String receiptAdviceReference;

	@Expose
	@SerializedName("receiptAdviceDate")
	@Column(name = "RECEIPT_ADVICE_DATE")
	protected LocalDate receiptAdviceDate;

	@Expose
	@SerializedName("externalRef")
	@Column(name = "EXTERNAL_REFERENCE")
	protected String externalReference;

	@Expose
	@SerializedName("projectRef")
	@Column(name = "PROJECT_REFERENCE")
	protected String projectReference;

	@Expose
	@SerializedName("contractRef")
	@Column(name = "CONTRACT_REFERENCE")
	protected String contractReference;

	@Expose
	@SerializedName("tenderRef")
	@Column(name = "TENDER_REFERENCE")
	protected String tenderReference;

	@Expose
	@SerializedName("paidAmt")
	@Column(name = "PAID_AMT")
	protected BigDecimal paidAmount;

	@Expose
	@SerializedName("balanceAmt")
	@Column(name = "BAL_AMT")
	protected BigDecimal balanceAmount;

	@Expose
	@SerializedName("supportingDocURL")
	@Column(name = "SUPPORTING_DOC_URL")
	private String supportingDocURL;

	@Expose
	@SerializedName("addlInfo")
	@Column(name = "ADDITIONAL_INFORMATION")
	protected String additionalInformation;

	@Expose
	@SerializedName("tranType")
	@Column(name = "TRANS_TYPE")
	protected String transactionType;

	@Expose
	@SerializedName("subsupplyType")
	@Column(name = "SUB_SUPP_TYPE")
	protected String subSupplyType;

	@Expose
	@SerializedName("otherSupplyTypeDesc")
	@Column(name = "OTHER_SUPP_TYPE_DESC")
	protected String otherSupplyTypeDescription;

	@Expose
	@SerializedName("transporterID")
	@Column(name = "TRANSPORTER_ID")
	protected String transporterID;

	@Expose
	@SerializedName("transporterName")
	@Column(name = "TRANSPORTER_NAME")
	protected String transporterName;

	@Expose
	@SerializedName("transportMode")
	@Column(name = "TRANSPORT_MODE")
	protected String transportMode;

	@Expose
	@SerializedName("transportDocNo")
	@Column(name = "TRANSPORT_DOC_NUM")
	protected String transportDocNo;

	@Expose
	@SerializedName("transportDocDate")
	@Column(name = "TRANSPORT_DOC_DATE")
	protected LocalDate transportDocDate;

	@Expose
	@SerializedName("vehicleNo")
	@Column(name = "VEHICLE_NUM")
	protected String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@SerializedName("orgDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String origDocType;

	@Expose
	@SerializedName("tcsCgstAmt")
	@Column(name = "TCS_CGST_AMT")
	protected BigDecimal tcsCgstAmount;

	@Expose
	@SerializedName("tcsSgstAmt")
	@Column(name = "TCS_SGST_AMT")
	protected BigDecimal tcsSgstAmount;

	@Expose
	@SerializedName("tdsIgstAmt")
	@Column(name = "TDS_IGST_AMT")
	protected BigDecimal tdsIgstAmount;

	@Expose
	@SerializedName("tdsCgstAmt")
	@Column(name = "TDS_CGST_AMT")
	protected BigDecimal tdsCgstAmount;

	@Expose
	@SerializedName("tdsSgstAmt")
	@Column(name = "TDS_SGST_AMT")
	protected BigDecimal tdsSgstAmount;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUB_DIVISION")
	protected String subDivision;

	@Expose
	@SerializedName("glStateCessSpecific")
	@Column(name = "GL_STATE_CESS_SPECIFIC")
	protected String glStateCessSpecific;

	@Expose
	@SerializedName("docRefNo")
	@Column(name = "DOCUMENT_REFERENCE_NUMBER")
	protected String docReferenceNumber;

	@Expose
	@SerializedName("udf16")
	@Column(name = "USERDEFINED_FIELD16")
	protected String userDefinedField16;

	@Expose
	@SerializedName("udf17")
	@Column(name = "USERDEFINED_FIELD17")
	protected String userDefinedField17;

	@Expose
	@SerializedName("udf18")
	@Column(name = "USERDEFINED_FIELD18")
	protected String userDefinedField18;

	@Expose
	@SerializedName("udf19")
	@Column(name = "USERDEFINED_FIELD19")
	protected String userDefinedField19;

	@Expose
	@SerializedName("udf20")
	@Column(name = "USERDEFINED_FIELD20")
	protected String userDefinedField20;

	@Expose
	@SerializedName("udf21")
	@Column(name = "USERDEFINED_FIELD21")
	protected String userDefinedField21;

	@Expose
	@SerializedName("udf22")
	@Column(name = "USERDEFINED_FIELD22")
	protected String userDefinedField22;

	@Expose
	@SerializedName("udf23")
	@Column(name = "USERDEFINED_FIELD23")
	protected String userDefinedField23;

	@Expose
	@SerializedName("udf24")
	@Column(name = "USERDEFINED_FIELD24")
	protected String userDefinedField24;

	@Expose
	@SerializedName("udf25")
	@Column(name = "USERDEFINED_FIELD25")
	protected String userDefinedField25;

	@Expose
	@SerializedName("udf26")
	@Column(name = "USERDEFINED_FIELD26")
	protected String userDefinedField26;

	@Expose
	@SerializedName("udf27")
	@Column(name = "USERDEFINED_FIELD27")
	protected String userDefinedField27;

	@Expose
	@SerializedName("udf29")
	@Column(name = "USERDEFINED_FIELD29")
	protected String userDefinedField29;

	@Expose
	@SerializedName("udf30")
	@Column(name = "USERDEFINED_FIELD30")
	protected String userDefinedField30;

	@Expose
	@SerializedName("supStateCode")
	@Column(name = "SUPP_STATE_CODE")
	protected String supplierStateCode;

	@Expose
	@SerializedName("billToState")
	@Column(name = "BILL_TO_STATE")
	protected String billToState;

	@Expose
	@SerializedName("dispatcherStateCode")
	@Column(name = "DISPATCHER_STATE_CODE")
	protected String dispatcherStateCode;

	@Expose
	@SerializedName("shipToState")
	@Column(name = "SHIP_TO_STATE")
	protected String shipToState;

	/**
	 * @return the crDrReason
	 */
	public String getCrDrReason() {
		return crDrReason;
	}

	/**
	 * @param crDrReason
	 *            the crDrReason to set
	 */
	public void setCrDrReason(String crDrReason) {
		this.crDrReason = crDrReason;
	}

	/**
	 * @return the hsnSac
	 */
	public String getHsnSac() {
		return hsnSac;
	}

	/**
	 * @param hsnSac
	 *            the hsnSac to set
	 */
	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}

	/**
	 * @param supplyType
	 *            the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode
	 *            the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @param itemDescription
	 *            the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the itemCategory
	 */
	public String getItemCategory() {
		return itemCategory;
	}

	/**
	 * @param itemCategory
	 *            the itemCategory to set
	 */
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom
	 *            the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the glCodeTaxableValue
	 */
	public String getGlCodeTaxableValue() {
		return glCodeTaxableValue;
	}

	/**
	 * @param glCodeTaxableValue
	 *            the glCodeTaxableValue to set
	 */
	public void setGlCodeTaxableValue(String glCodeTaxableValue) {
		this.glCodeTaxableValue = glCodeTaxableValue;
	}

	/**
	 * @return the adjustmentRefNo
	 */
	public String getAdjustmentRefNo() {
		return adjustmentRefNo;
	}

	/**
	 * @param adjustmentRefNo
	 *            the adjustmentRefNo to set
	 */
	public void setAdjustmentRefNo(String adjustmentRefNo) {
		this.adjustmentRefNo = adjustmentRefNo;
	}

	/**
	 * @return the adjustmentRefDate
	 */
	public LocalDate getAdjustmentRefDate() {
		return adjustmentRefDate;
	}

	/**
	 * @param adjustmentRefDate
	 *            the adjustmentRefDate to set
	 */
	public void setAdjustmentRefDate(LocalDate adjustmentRefDate) {
		this.adjustmentRefDate = adjustmentRefDate;
	}

	/**
	 * @return the adjustedTaxableValue
	 */
	public BigDecimal getAdjustedTaxableValue() {
		return adjustedTaxableValue;
	}

	/**
	 * @param adjustedTaxableValue
	 *            the adjustedTaxableValue to set
	 */
	public void setAdjustedTaxableValue(BigDecimal adjustedTaxableValue) {
		this.adjustedTaxableValue = adjustedTaxableValue;
	}

	/**
	 * @return the adjustedIgstAmt
	 */
	public BigDecimal getAdjustedIgstAmt() {
		return adjustedIgstAmt;
	}

	/**
	 * @param adjustedIgstAmt
	 *            the adjustedIgstAmt to set
	 */
	public void setAdjustedIgstAmt(BigDecimal adjustedIgstAmt) {
		this.adjustedIgstAmt = adjustedIgstAmt;
	}

	/**
	 * @return the adjustedCgstAmt
	 */
	public BigDecimal getAdjustedCgstAmt() {
		return adjustedCgstAmt;
	}

	/**
	 * @param adjustedCgstAmt
	 *            the adjustedCgstAmt to set
	 */
	public void setAdjustedCgstAmt(BigDecimal adjustedCgstAmt) {
		this.adjustedCgstAmt = adjustedCgstAmt;
	}

	/**
	 * @return the adjustedSgstAmt
	 */
	public BigDecimal getAdjustedSgstAmt() {
		return adjustedSgstAmt;
	}

	/**
	 * @param adjustedSgstAmt
	 *            the adjustedSgstAmt to set
	 */
	public void setAdjustedSgstAmt(BigDecimal adjustedSgstAmt) {
		this.adjustedSgstAmt = adjustedSgstAmt;
	}

	/**
	 * @return the adjustedCessAmtAdvalorem
	 */
	public BigDecimal getAdjustedCessAmtAdvalorem() {
		return adjustedCessAmtAdvalorem;
	}

	/**
	 * @param adjustedCessAmtAdvalorem
	 *            the adjustedCessAmtAdvalorem to set
	 */
	public void setAdjustedCessAmtAdvalorem(
			BigDecimal adjustedCessAmtAdvalorem) {
		this.adjustedCessAmtAdvalorem = adjustedCessAmtAdvalorem;
	}

	/**
	 * @return the adjustedCessAmtSpecific
	 */
	public BigDecimal getAdjustedCessAmtSpecific() {
		return adjustedCessAmtSpecific;
	}

	/**
	 * @param adjustedCessAmtSpecific
	 *            the adjustedCessAmtSpecific to set
	 */
	public void setAdjustedCessAmtSpecific(BigDecimal adjustedCessAmtSpecific) {
		this.adjustedCessAmtSpecific = adjustedCessAmtSpecific;
	}

	/**
	 * @return the adjustedStateCessAmt
	 */
	public BigDecimal getAdjustedStateCessAmt() {
		return adjustedStateCessAmt;
	}

	/**
	 * @param adjustedStateCessAmt
	 *            the adjustedStateCessAmt to set
	 */
	public void setAdjustedStateCessAmt(BigDecimal adjustedStateCessAmt) {
		this.adjustedStateCessAmt = adjustedStateCessAmt;
	}

	/**
	 * @return the tcsAmount
	 */
	public BigDecimal getTcsAmount() {
		return tcsAmount;
	}

	/**
	 * @param tcsAmount
	 *            the tcsAmount to set
	 */
	public void setTcsAmount(BigDecimal tcsAmount) {
		this.tcsAmount = tcsAmount;
	}

	/**
	 * @return the qty
	 */
	public BigDecimal getQty() {
		return qty;
	}

	/**
	 * @param qty
	 *            the qty to set
	 */
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	/**
	 * @return the lineItemAmt
	 */
	public BigDecimal getLineItemAmt() {
		return lineItemAmt;
	}

	/**
	 * @param lineItemAmt
	 *            the lineItemAmt to set
	 */
	public void setLineItemAmt(BigDecimal lineItemAmt) {
		this.lineItemAmt = lineItemAmt;
	}

	/**
	 * @return the origDocNo
	 */
	public String getOrigDocNo() {
		return origDocNo;
	}

	/**
	 * @param origDocNo
	 *            the origDocNo to set
	 */
	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}

	/**
	 * @return the origDocDate
	 */
	public LocalDate getOrigDocDate() {
		return origDocDate;
	}

	/**
	 * @param origDocDate
	 *            the origDocDate to set
	 */
	public void setOrigDocDate(LocalDate origDocDate) {
		this.origDocDate = origDocDate;
	}

	/**
	 * @return the userdefinedfield1
	 */
	public String getUserdefinedfield1() {
		return userdefinedfield1;
	}

	/**
	 * @param userdefinedfield1
	 *            the userdefinedfield1 to set
	 */
	public void setUserdefinedfield1(String userdefinedfield1) {
		this.userdefinedfield1 = userdefinedfield1;
	}

	/**
	 * @return the userdefinedfield2
	 */
	public String getUserdefinedfield2() {
		return userdefinedfield2;
	}

	/**
	 * @param userdefinedfield2
	 *            the userdefinedfield2 to set
	 */
	public void setUserdefinedfield2(String userdefinedfield2) {
		this.userdefinedfield2 = userdefinedfield2;
	}

	/**
	 * @return the userdefinedfield3
	 */
	public String getUserdefinedfield3() {
		return userdefinedfield3;
	}

	/**
	 * @param userdefinedfield3
	 *            the userdefinedfield3 to set
	 */
	public void setUserdefinedfield3(String userdefinedfield3) {
		this.userdefinedfield3 = userdefinedfield3;
	}

	/**
	 * @return the userDefinedField4
	 */
	public String getUserDefinedField4() {
		return userDefinedField4;
	}

	/**
	 * @param userDefinedField4
	 *            the userDefinedField4 to set
	 */
	public void setUserDefinedField4(String userDefinedField4) {
		this.userDefinedField4 = userDefinedField4;
	}

	/**
	 * @return the userDefinedField5
	 */
	public String getUserDefinedField5() {
		return userDefinedField5;
	}

	/**
	 * @param userDefinedField5
	 *            the userDefinedField5 to set
	 */
	public void setUserDefinedField5(String userDefinedField5) {
		this.userDefinedField5 = userDefinedField5;
	}

	/**
	 * @return the userDefinedField6
	 */
	public String getUserDefinedField6() {
		return userDefinedField6;
	}

	/**
	 * @param userDefinedField6
	 *            the userDefinedField6 to set
	 */
	public void setUserDefinedField6(String userDefinedField6) {
		this.userDefinedField6 = userDefinedField6;
	}

	/**
	 * @return the userDefinedField7
	 */
	public String getUserDefinedField7() {
		return userDefinedField7;
	}

	/**
	 * @param userDefinedField7
	 *            the userDefinedField7 to set
	 */
	public void setUserDefinedField7(String userDefinedField7) {
		this.userDefinedField7 = userDefinedField7;
	}

	/**
	 * @return the userDefinedField8
	 */
	public String getUserDefinedField8() {
		return userDefinedField8;
	}

	/**
	 * @param userDefinedField8
	 *            the userDefinedField8 to set
	 */
	public void setUserDefinedField8(String userDefinedField8) {
		this.userDefinedField8 = userDefinedField8;
	}

	/**
	 * @return the userDefinedField9
	 */
	public String getUserDefinedField9() {
		return userDefinedField9;
	}

	/**
	 * @param userDefinedField9
	 *            the userDefinedField9 to set
	 */
	public void setUserDefinedField9(String userDefinedField9) {
		this.userDefinedField9 = userDefinedField9;
	}

	/**
	 * @return the userDefinedField10
	 */
	public String getUserDefinedField10() {
		return userDefinedField10;
	}

	/**
	 * @param userDefinedField10
	 *            the userDefinedField10 to set
	 */
	public void setUserDefinedField10(String userDefinedField10) {
		this.userDefinedField10 = userDefinedField10;
	}

	/**
	 * @return the userDefinedField11
	 */
	public String getUserDefinedField11() {
		return userDefinedField11;
	}

	/**
	 * @param userDefinedField11
	 *            the userDefinedField11 to set
	 */
	public void setUserDefinedField11(String userDefinedField11) {
		this.userDefinedField11 = userDefinedField11;
	}

	/**
	 * @return the userDefinedField12
	 */
	public String getUserDefinedField12() {
		return userDefinedField12;
	}

	/**
	 * @param userDefinedField12
	 *            the userDefinedField12 to set
	 */
	public void setUserDefinedField12(String userDefinedField12) {
		this.userDefinedField12 = userDefinedField12;
	}

	/**
	 * @return the userDefinedField13
	 */
	public String getUserDefinedField13() {
		return userDefinedField13;
	}

	/**
	 * @param userDefinedField13
	 *            the userDefinedField13 to set
	 */
	public void setUserDefinedField13(String userDefinedField13) {
		this.userDefinedField13 = userDefinedField13;
	}

	/**
	 * @return the userDefinedField14
	 */
	public String getUserDefinedField14() {
		return userDefinedField14;
	}

	/**
	 * @param userDefinedField14
	 *            the userDefinedField14 to set
	 */
	public void setUserDefinedField14(String userDefinedField14) {
		this.userDefinedField14 = userDefinedField14;
	}

	/**
	 * @return the userDefinedField15
	 */
	public String getUserDefinedField15() {
		return userDefinedField15;
	}

	/**
	 * @param userDefinedField15
	 *            the userDefinedField15 to set
	 */
	public void setUserDefinedField15(String userDefinedField15) {
		this.userDefinedField15 = userDefinedField15;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getUserAccess1() {
		return userAccess1;
	}

	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}

	public String getUserAccess2() {
		return userAccess2;
	}

	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}

	public String getUserAccess3() {
		return userAccess3;
	}

	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}

	public String getUserAccess4() {
		return userAccess4;
	}

	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}

	public String getUserAccess5() {
		return userAccess5;
	}

	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}

	public String getUserAccess6() {
		return userAccess6;
	}

	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
	}

	public String getTaxperiod() {
		return taxperiod;
	}

	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	public String getCrDrPreGst() {
		return crDrPreGst;
	}

	public void setCrDrPreGst(String crDrPreGst) {
		this.crDrPreGst = crDrPreGst;
	}

	public String getCustOrSuppType() {
		return custOrSuppType;
	}

	public void setCustOrSuppType(String custOrSuppType) {
		this.custOrSuppType = custOrSuppType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getCustOrSuppName() {
		return custOrSuppName;
	}

	public void setCustOrSuppName(String custOrSuppName) {
		this.custOrSuppName = custOrSuppName;
	}

	public String getCustOrSuppCode() {
		return custOrSuppCode;
	}

	public void setCustOrSuppCode(String custOrSuppCode) {
		this.custOrSuppCode = custOrSuppCode;
	}

	public String getCustOrSuppAddress1() {
		return custOrSuppAddress1;
	}

	public void setCustOrSuppAddress1(String custOrSuppAddress1) {
		this.custOrSuppAddress1 = custOrSuppAddress1;
	}

	public String getCustOrSuppAddress2() {
		return custOrSuppAddress2;
	}

	public void setCustOrSuppAddress2(String custOrSuppAddress2) {
		this.custOrSuppAddress2 = custOrSuppAddress2;
	}

	public String getCustOrSuppAddress3() {
		return custOrSuppAddress3;
	}

	public void setCustOrSuppAddress3(String custOrSuppAddress3) {
		this.custOrSuppAddress3 = custOrSuppAddress3;
	}

	public String getCustOrSuppAddress4() {
		return custOrSuppAddress4;
	}

	public void setCustOrSuppAddress4(String custOrSuppAddress4) {
		this.custOrSuppAddress4 = custOrSuppAddress4;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getStateApplyingCess() {
		return stateApplyingCess;
	}

	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getSection7OfIgstFlag() {
		return section7OfIgstFlag;
	}

	public void setSection7OfIgstFlag(String section7OfIgstFlag) {
		this.section7OfIgstFlag = section7OfIgstFlag;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getClaimRefundFlag() {
		return claimRefundFlag;
	}

	public void setClaimRefundFlag(String claimRefundFlag) {
		this.claimRefundFlag = claimRefundFlag;
	}

	public String getAutoPopToRefundFlag() {
		return autoPopToRefundFlag;
	}

	public void setAutoPopToRefundFlag(String autoPopToRefundFlag) {
		this.autoPopToRefundFlag = autoPopToRefundFlag;
	}

	public String geteWayBillNo() {
		return eWayBillNo;
	}

	public void seteWayBillNo(String eWayBillNo) {
		this.eWayBillNo = eWayBillNo;
	}

	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	/**
	 * @return the glCodeIgst
	 */
	public String getGlCodeIgst() {
		return glCodeIgst;
	}

	/**
	 * @param glCodeIgst
	 *            the glCodeIgst to set
	 */
	public void setGlCodeIgst(String glCodeIgst) {
		this.glCodeIgst = glCodeIgst;
	}

	/**
	 * @return the glCodeCgst
	 */
	public String getGlCodeCgst() {
		return glCodeCgst;
	}

	/**
	 * @param glCodeCgst
	 *            the glCodeCgst to set
	 */
	public void setGlCodeCgst(String glCodeCgst) {
		this.glCodeCgst = glCodeCgst;
	}

	/**
	 * @return the glCodeSgst
	 */
	public String getGlCodeSgst() {
		return glCodeSgst;
	}

	/**
	 * @param glCodeSgst
	 *            the glCodeSgst to set
	 */
	public void setGlCodeSgst(String glCodeSgst) {
		this.glCodeSgst = glCodeSgst;
	}

	/**
	 * @return the glCodeAdvCess
	 */
	public String getGlCodeAdvCess() {
		return glCodeAdvCess;
	}

	/**
	 * @param glCodeAdvCess
	 *            the glCodeAdvCess to set
	 */
	public void setGlCodeAdvCess(String glCodeAdvCess) {
		this.glCodeAdvCess = glCodeAdvCess;
	}

	/**
	 * @return the glCodeSpCess
	 */
	public String getGlCodeSpCess() {
		return glCodeSpCess;
	}

	/**
	 * @param glCodeSpCess
	 *            the glCodeSpCess to set
	 */
	public void setGlCodeSpCess(String glCodeSpCess) {
		this.glCodeSpCess = glCodeSpCess;
	}

	/**
	 * @return the glCodeStateCess
	 */
	public String getGlCodeStateCess() {
		return glCodeStateCess;
	}

	/**
	 * @param glCodeStateCess
	 *            the glCodeStateCess to set
	 */
	public void setGlCodeStateCess(String glCodeStateCess) {
		this.glCodeStateCess = glCodeStateCess;
	}

	/**
	 * @return the acceptanceId
	 */
	public Long getAcceptanceId() {
		return acceptanceId;
	}

	/**
	 * @param acceptanceId
	 *            the acceptanceId to set
	 */
	public void setAcceptanceId(Long acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	/**
	 * @return the eWayBillDate
	 */
	public LocalDateTime geteWayBillDate() {
		return eWayBillDate;
	}

	/**
	 * @param eWayBillDate
	 *            the eWayBillDate to set
	 */
	public void seteWayBillDate(LocalDateTime eWayBillDate) {
		this.eWayBillDate = eWayBillDate;
	}

	/**
	 * @return the invoiceOtherCharges
	 */
	public BigDecimal getInvoiceOtherCharges() {
		return invoiceOtherCharges;
	}

	/**
	 * @param invoiceOtherCharges
	 *            the invoiceOtherCharges to set
	 */
	public void setInvoiceOtherCharges(BigDecimal invoiceOtherCharges) {
		this.invoiceOtherCharges = invoiceOtherCharges;
	}

	/**
	 * @return the invoiceAssessableAmount
	 */
	public BigDecimal getInvoiceAssessableAmount() {
		return invoiceAssessableAmount;
	}

	/**
	 * @param invoiceAssessableAmount
	 *            the invoiceAssessableAmount to set
	 */
	public void setInvoiceAssessableAmount(BigDecimal invoiceAssessableAmount) {
		this.invoiceAssessableAmount = invoiceAssessableAmount;
	}

	/**
	 * @return the invoiceIgstAmount
	 */
	public BigDecimal getInvoiceIgstAmount() {
		return invoiceIgstAmount;
	}

	/**
	 * @param invoiceIgstAmount
	 *            the invoiceIgstAmount to set
	 */
	public void setInvoiceIgstAmount(BigDecimal invoiceIgstAmount) {
		this.invoiceIgstAmount = invoiceIgstAmount;
	}

	/**
	 * @return the invoiceCgstAmount
	 */
	public BigDecimal getInvoiceCgstAmount() {
		return invoiceCgstAmount;
	}

	/**
	 * @param invoiceCgstAmount
	 *            the invoiceCgstAmount to set
	 */
	public void setInvoiceCgstAmount(BigDecimal invoiceCgstAmount) {
		this.invoiceCgstAmount = invoiceCgstAmount;
	}

	/**
	 * @return the invoiceSgstAmount
	 */
	public BigDecimal getInvoiceSgstAmount() {
		return invoiceSgstAmount;
	}

	/**
	 * @param invoiceSgstAmount
	 *            the invoiceSgstAmount to set
	 */
	public void setInvoiceSgstAmount(BigDecimal invoiceSgstAmount) {
		this.invoiceSgstAmount = invoiceSgstAmount;
	}

	/**
	 * @return the invoiceCessAdvaloremAmount
	 */
	public BigDecimal getInvoiceCessAdvaloremAmount() {
		return invoiceCessAdvaloremAmount;
	}

	/**
	 * @param invoiceCessAdvaloremAmount
	 *            the invoiceCessAdvaloremAmount to set
	 */
	public void setInvoiceCessAdvaloremAmount(
			BigDecimal invoiceCessAdvaloremAmount) {
		this.invoiceCessAdvaloremAmount = invoiceCessAdvaloremAmount;
	}

	/**
	 * @return the invoiceCessSpecificAmount
	 */
	public BigDecimal getInvoiceCessSpecificAmount() {
		return invoiceCessSpecificAmount;
	}

	/**
	 * @param invoiceCessSpecificAmount
	 *            the invoiceCessSpecificAmount to set
	 */
	public void setInvoiceCessSpecificAmount(
			BigDecimal invoiceCessSpecificAmount) {
		this.invoiceCessSpecificAmount = invoiceCessSpecificAmount;
	}

	/**
	 * @return the invoiceStateCessAmount
	 */
	public BigDecimal getInvoiceStateCessAmount() {
		return invoiceStateCessAmount;
	}

	/**
	 * @param invoiceStateCessAmount
	 *            the invoiceStateCessAmount to set
	 */
	public void setInvoiceStateCessAmount(BigDecimal invoiceStateCessAmount) {
		this.invoiceStateCessAmount = invoiceStateCessAmount;
	}

	/**
	 * @return the userDefinedField28
	 */
	public BigDecimal getUserDefinedField28() {
		return userDefinedField28;
	}

	/**
	 * @param userDefinedField28
	 *            the userDefinedField28 to set
	 */
	public void setUserDefinedField28(BigDecimal userDefinedField28) {
		this.userDefinedField28 = userDefinedField28;
	}

	/**
	 * @return the egstin
	 */
	public String getEgstin() {
		return egstin;
	}

	/**
	 * @param egstin
	 *            the egstin to set
	 */
	public void setEgstin(String egstin) {
		this.egstin = egstin;
	}

	/**
	 * @return the tcsFlag
	 */
	public String getTcsFlag() {
		return tcsFlag;
	}

	/**
	 * @param tcsFlag
	 *            the tcsFlag to set
	 */
	public void setTcsFlag(String tcsFlag) {
		this.tcsFlag = tcsFlag;
	}

	/**
	 * @return the tdsFlag
	 */
	public String getTdsFlag() {
		return tdsFlag;
	}

	/**
	 * @param tdsFlag
	 *            the tdsFlag to set
	 */
	public void setTdsFlag(String tdsFlag) {
		this.tdsFlag = tdsFlag;
	}

	/**
	 * @return the docCategory
	 */
	public String getDocCategory() {
		return docCategory;
	}

	/**
	 * @param docCategory
	 *            the docCategory to set
	 */
	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	/**
	 * @return the supplierEmail
	 */
	public String getSupplierEmail() {
		return supplierEmail;
	}

	/**
	 * @param supplierEmail
	 *            the supplierEmail to set
	 */
	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail
	 *            the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * @return the dispatcherTradeName
	 */
	public String getDispatcherTradeName() {
		return dispatcherTradeName;
	}

	/**
	 * @param dispatcherTradeName
	 *            the dispatcherTradeName to set
	 */
	public void setDispatcherTradeName(String dispatcherTradeName) {
		this.dispatcherTradeName = dispatcherTradeName;
	}

	/**
	 * @return the dispatcherBuildingNumber
	 */
	public String getDispatcherBuildingNumber() {
		return dispatcherBuildingNumber;
	}

	/**
	 * @param dispatcherBuildingNumber
	 *            the dispatcherBuildingNumber to set
	 */
	public void setDispatcherBuildingNumber(String dispatcherBuildingNumber) {
		this.dispatcherBuildingNumber = dispatcherBuildingNumber;
	}

	/**
	 * @return the dispatcherBuildingName
	 */
	public String getDispatcherBuildingName() {
		return dispatcherBuildingName;
	}

	/**
	 * @param dispatcherBuildingName
	 *            the dispatcherBuildingName to set
	 */
	public void setDispatcherBuildingName(String dispatcherBuildingName) {
		this.dispatcherBuildingName = dispatcherBuildingName;
	}

	/**
	 * @return the dispatcherLocation
	 */
	public String getDispatcherLocation() {
		return dispatcherLocation;
	}

	/**
	 * @param dispatcherLocation
	 *            the dispatcherLocation to set
	 */
	public void setDispatcherLocation(String dispatcherLocation) {
		this.dispatcherLocation = dispatcherLocation;
	}

	/**
	 * @return the shipToLegalName
	 */
	public String getShipToLegalName() {
		return shipToLegalName;
	}

	/**
	 * @param shipToLegalName
	 *            the shipToLegalName to set
	 */
	public void setShipToLegalName(String shipToLegalName) {
		this.shipToLegalName = shipToLegalName;
	}

	/**
	 * @return the shipToBuildingNumber
	 */
	public String getShipToBuildingNumber() {
		return shipToBuildingNumber;
	}

	/**
	 * @param shipToBuildingNumber
	 *            the shipToBuildingNumber to set
	 */
	public void setShipToBuildingNumber(String shipToBuildingNumber) {
		this.shipToBuildingNumber = shipToBuildingNumber;
	}

	/**
	 * @return the shipToBuildingName
	 */
	public String getShipToBuildingName() {
		return shipToBuildingName;
	}

	/**
	 * @param shipToBuildingName
	 *            the shipToBuildingName to set
	 */
	public void setShipToBuildingName(String shipToBuildingName) {
		this.shipToBuildingName = shipToBuildingName;
	}

	/**
	 * @return the shipToLocation
	 */
	public String getShipToLocation() {
		return shipToLocation;
	}

	/**
	 * @param shipToLocation
	 *            the shipToLocation to set
	 */
	public void setShipToLocation(String shipToLocation) {
		this.shipToLocation = shipToLocation;
	}

	/**
	 * @return the serialNumberII
	 */
	public String getSerialNumberII() {
		return serialNumberII;
	}

	/**
	 * @param serialNumberII
	 *            the serialNumberII to set
	 */
	public void setSerialNumberII(String serialNumberII) {
		this.serialNumberII = serialNumberII;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName
	 *            the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the isService
	 */
	public String getIsService() {
		return isService;
	}

	/**
	 * @param isService
	 *            the isService to set
	 */
	public void setIsService(String isService) {
		this.isService = isService;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode
	 *            the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the batchNameOrNumber
	 */
	public String getBatchNameOrNumber() {
		return batchNameOrNumber;
	}

	/**
	 * @param batchNameOrNumber
	 *            the batchNameOrNumber to set
	 */
	public void setBatchNameOrNumber(String batchNameOrNumber) {
		this.batchNameOrNumber = batchNameOrNumber;
	}

	/**
	 * @return the batchExpiryDate
	 */
	public LocalDate getBatchExpiryDate() {
		return batchExpiryDate;
	}

	/**
	 * @param batchExpiryDate
	 *            the batchExpiryDate to set
	 */
	public void setBatchExpiryDate(LocalDate batchExpiryDate) {
		this.batchExpiryDate = batchExpiryDate;
	}

	/**
	 * @return the warrantyDate
	 */
	public LocalDate getWarrantyDate() {
		return warrantyDate;
	}

	/**
	 * @param warrantyDate
	 *            the warrantyDate to set
	 */
	public void setWarrantyDate(LocalDate warrantyDate) {
		this.warrantyDate = warrantyDate;
	}

	/**
	 * @return the orderLineReference
	 */
	public String getOrderLineReference() {
		return orderLineReference;
	}

	/**
	 * @param orderLineReference
	 *            the orderLineReference to set
	 */
	public void setOrderLineReference(String orderLineReference) {
		this.orderLineReference = orderLineReference;
	}

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName
	 *            the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @param attributeValue
	 *            the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the originCountry
	 */
	public String getOriginCountry() {
		return originCountry;
	}

	/**
	 * @param originCountry
	 *            the originCountry to set
	 */
	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	/**
	 * @return the freeQuantity
	 */
	public BigDecimal getFreeQuantity() {
		return freeQuantity;
	}

	/**
	 * @param freeQuantity
	 *            the freeQuantity to set
	 */
	public void setFreeQuantity(BigDecimal freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice
	 *            the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the itemAmount
	 */
	public BigDecimal getItemAmount() {
		return itemAmount;
	}

	/**
	 * @param itemAmount
	 *            the itemAmount to set
	 */
	public void setItemAmount(BigDecimal itemAmount) {
		this.itemAmount = itemAmount;
	}

	/**
	 * @return the itemDiscount
	 */
	public BigDecimal getItemDiscount() {
		return itemDiscount;
	}

	/**
	 * @param itemDiscount
	 *            the itemDiscount to set
	 */
	public void setItemDiscount(BigDecimal itemDiscount) {
		this.itemDiscount = itemDiscount;
	}

	/**
	 * @return the preTaxAmount
	 */
	public BigDecimal getPreTaxAmount() {
		return preTaxAmount;
	}

	/**
	 * @param preTaxAmount
	 *            the preTaxAmount to set
	 */
	public void setPreTaxAmount(BigDecimal preTaxAmount) {
		this.preTaxAmount = preTaxAmount;
	}

	/**
	 * @return the stateCessSpecificRate
	 */
	public BigDecimal getStateCessSpecificRate() {
		return stateCessSpecificRate;
	}

	/**
	 * @param stateCessSpecificRate
	 *            the stateCessSpecificRate to set
	 */
	public void setStateCessSpecificRate(BigDecimal stateCessSpecificRate) {
		this.stateCessSpecificRate = stateCessSpecificRate;
	}

	/**
	 * @return the stateCessSpecificAmt
	 */
	public BigDecimal getStateCessSpecificAmt() {
		return stateCessSpecificAmt;
	}

	/**
	 * @param stateCessSpecificAmt
	 *            the stateCessSpecificAmt to set
	 */
	public void setStateCessSpecificAmt(BigDecimal stateCessSpecificAmt) {
		this.stateCessSpecificAmt = stateCessSpecificAmt;
	}

	/**
	 * @return the invStateCessSpecificAmt
	 */
	public BigDecimal getInvStateCessSpecificAmt() {
		return invStateCessSpecificAmt;
	}

	/**
	 * @param invStateCessSpecificAmt
	 *            the invStateCessSpecificAmt to set
	 */
	public void setInvStateCessSpecificAmt(BigDecimal invStateCessSpecificAmt) {
		this.invStateCessSpecificAmt = invStateCessSpecificAmt;
	}

	/**
	 * @return the totalItemAmount
	 */
	public BigDecimal getTotalItemAmount() {
		return totalItemAmount;
	}

	/**
	 * @param totalItemAmount
	 *            the totalItemAmount to set
	 */
	public void setTotalItemAmount(BigDecimal totalItemAmount) {
		this.totalItemAmount = totalItemAmount;
	}

	/**
	 * @return the tcsRateIncomeTax
	 */
	public BigDecimal getTcsRateIncomeTax() {
		return tcsRateIncomeTax;
	}

	/**
	 * @param tcsRateIncomeTax
	 *            the tcsRateIncomeTax to set
	 */
	public void setTcsRateIncomeTax(BigDecimal tcsRateIncomeTax) {
		this.tcsRateIncomeTax = tcsRateIncomeTax;
	}

	/**
	 * @return the tcsAmountIncomeTax
	 */
	public BigDecimal getTcsAmountIncomeTax() {
		return tcsAmountIncomeTax;
	}

	/**
	 * @param tcsAmountIncomeTax
	 *            the tcsAmountIncomeTax to set
	 */
	public void setTcsAmountIncomeTax(BigDecimal tcsAmountIncomeTax) {
		this.tcsAmountIncomeTax = tcsAmountIncomeTax;
	}

	/**
	 * @return the foreignCurrency
	 */
	public String getForeignCurrency() {
		return foreignCurrency;
	}

	/**
	 * @param foreignCurrency
	 *            the foreignCurrency to set
	 */
	public void setForeignCurrency(String foreignCurrency) {
		this.foreignCurrency = foreignCurrency;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the invoiceValueFc
	 */
	public BigDecimal getInvoiceValueFc() {
		return invoiceValueFc;
	}

	/**
	 * @param invoiceValueFc
	 *            the invoiceValueFc to set
	 */
	public void setInvoiceValueFc(BigDecimal invoiceValueFc) {
		this.invoiceValueFc = invoiceValueFc;
	}

	/**
	 * @return the invoicePeriodStartDate
	 */
	public LocalDate getInvoicePeriodStartDate() {
		return invoicePeriodStartDate;
	}

	/**
	 * @param invoicePeriodStartDate
	 *            the invoicePeriodStartDate to set
	 */
	public void setInvoicePeriodStartDate(LocalDate invoicePeriodStartDate) {
		this.invoicePeriodStartDate = invoicePeriodStartDate;
	}

	/**
	 * @return the invoicePeriodEndDate
	 */
	public LocalDate getInvoicePeriodEndDate() {
		return invoicePeriodEndDate;
	}

	/**
	 * @param invoicePeriodEndDate
	 *            the invoicePeriodEndDate to set
	 */
	public void setInvoicePeriodEndDate(LocalDate invoicePeriodEndDate) {
		this.invoicePeriodEndDate = invoicePeriodEndDate;
	}

	/**
	 * @return the invoiceReference
	 */
	public String getInvoiceReference() {
		return invoiceReference;
	}

	/**
	 * @param invoiceReference
	 *            the invoiceReference to set
	 */
	public void setInvoiceReference(String invoiceReference) {
		this.invoiceReference = invoiceReference;
	}

	/**
	 * @return the receiptAdviceReference
	 */
	public String getReceiptAdviceReference() {
		return receiptAdviceReference;
	}

	/**
	 * @param receiptAdviceReference
	 *            the receiptAdviceReference to set
	 */
	public void setReceiptAdviceReference(String receiptAdviceReference) {
		this.receiptAdviceReference = receiptAdviceReference;
	}

	/**
	 * @return the receiptAdviceDate
	 */
	public LocalDate getReceiptAdviceDate() {
		return receiptAdviceDate;
	}

	/**
	 * @param receiptAdviceDate
	 *            the receiptAdviceDate to set
	 */
	public void setReceiptAdviceDate(LocalDate receiptAdviceDate) {
		this.receiptAdviceDate = receiptAdviceDate;
	}

	/**
	 * @return the externalReference
	 */
	public String getExternalReference() {
		return externalReference;
	}

	/**
	 * @param externalReference
	 *            the externalReference to set
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	/**
	 * @return the projectReference
	 */
	public String getProjectReference() {
		return projectReference;
	}

	/**
	 * @param projectReference
	 *            the projectReference to set
	 */
	public void setProjectReference(String projectReference) {
		this.projectReference = projectReference;
	}

	/**
	 * @return the contractReference
	 */
	public String getContractReference() {
		return contractReference;
	}

	/**
	 * @param contractReference
	 *            the contractReference to set
	 */
	public void setContractReference(String contractReference) {
		this.contractReference = contractReference;
	}

	/**
	 * @return the tenderReference
	 */
	public String getTenderReference() {
		return tenderReference;
	}

	/**
	 * @param tenderReference
	 *            the tenderReference to set
	 */
	public void setTenderReference(String tenderReference) {
		this.tenderReference = tenderReference;
	}

	/**
	 * @return the paidAmount
	 */
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	/**
	 * @param paidAmount
	 *            the paidAmount to set
	 */
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	/**
	 * @return the balanceAmount
	 */
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	/**
	 * @param balanceAmount
	 *            the balanceAmount to set
	 */
	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	/**
	 * @return the supportingDocURL
	 */
	public String getSupportingDocURL() {
		return supportingDocURL;
	}

	/**
	 * @param supportingDocURL
	 *            the supportingDocURL to set
	 */
	public void setSupportingDocURL(String supportingDocURL) {
		this.supportingDocURL = supportingDocURL;
	}

	/**
	 * @return the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * @param additionalInformation
	 *            the additionalInformation to set
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the subSupplyType
	 */
	public String getSubSupplyType() {
		return subSupplyType;
	}

	/**
	 * @param subSupplyType
	 *            the subSupplyType to set
	 */
	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType;
	}

	/**
	 * @return the otherSupplyTypeDescription
	 */
	public String getOtherSupplyTypeDescription() {
		return otherSupplyTypeDescription;
	}

	/**
	 * @param otherSupplyTypeDescription
	 *            the otherSupplyTypeDescription to set
	 */
	public void setOtherSupplyTypeDescription(
			String otherSupplyTypeDescription) {
		this.otherSupplyTypeDescription = otherSupplyTypeDescription;
	}

	/**
	 * @return the transporterID
	 */
	public String getTransporterID() {
		return transporterID;
	}

	/**
	 * @param transporterID
	 *            the transporterID to set
	 */
	public void setTransporterID(String transporterID) {
		this.transporterID = transporterID;
	}

	/**
	 * @return the transporterName
	 */
	public String getTransporterName() {
		return transporterName;
	}

	/**
	 * @param transporterName
	 *            the transporterName to set
	 */
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}

	/**
	 * @return the transportMode
	 */
	public String getTransportMode() {
		return transportMode;
	}

	/**
	 * @param transportMode
	 *            the transportMode to set
	 */
	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}

	/**
	 * @return the transportDocNo
	 */
	public String getTransportDocNo() {
		return transportDocNo;
	}

	/**
	 * @param transportDocNo
	 *            the transportDocNo to set
	 */
	public void setTransportDocNo(String transportDocNo) {
		this.transportDocNo = transportDocNo;
	}

	/**
	 * @return the transportDocDate
	 */
	public LocalDate getTransportDocDate() {
		return transportDocDate;
	}

	/**
	 * @param transportDocDate
	 *            the transportDocDate to set
	 */
	public void setTransportDocDate(LocalDate transportDocDate) {
		this.transportDocDate = transportDocDate;
	}

	/**
	 * @return the vehicleNo
	 */
	public String getVehicleNo() {
		return vehicleNo;
	}

	/**
	 * @param vehicleNo
	 *            the vehicleNo to set
	 */
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	/**
	 * @return the vehicleType
	 */
	public String getVehicleType() {
		return vehicleType;
	}

	/**
	 * @param vehicleType
	 *            the vehicleType to set
	 */
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	/**
	 * @return the origDocType
	 */
	public String getOrigDocType() {
		return origDocType;
	}

	/**
	 * @param origDocType
	 *            the origDocType to set
	 */
	public void setOrigDocType(String origDocType) {
		this.origDocType = origDocType;
	}

	/**
	 * @return the tcsCgstAmount
	 */
	public BigDecimal getTcsCgstAmount() {
		return tcsCgstAmount;
	}

	/**
	 * @param tcsCgstAmount
	 *            the tcsCgstAmount to set
	 */
	public void setTcsCgstAmount(BigDecimal tcsCgstAmount) {
		this.tcsCgstAmount = tcsCgstAmount;
	}

	/**
	 * @return the tcsSgstAmount
	 */
	public BigDecimal getTcsSgstAmount() {
		return tcsSgstAmount;
	}

	/**
	 * @param tcsSgstAmount
	 *            the tcsSgstAmount to set
	 */
	public void setTcsSgstAmount(BigDecimal tcsSgstAmount) {
		this.tcsSgstAmount = tcsSgstAmount;
	}

	/**
	 * @return the tdsIgstAmount
	 */
	public BigDecimal getTdsIgstAmount() {
		return tdsIgstAmount;
	}

	/**
	 * @param tdsIgstAmount
	 *            the tdsIgstAmount to set
	 */
	public void setTdsIgstAmount(BigDecimal tdsIgstAmount) {
		this.tdsIgstAmount = tdsIgstAmount;
	}

	/**
	 * @return the tdsCgstAmount
	 */
	public BigDecimal getTdsCgstAmount() {
		return tdsCgstAmount;
	}

	/**
	 * @param tdsCgstAmount
	 *            the tdsCgstAmount to set
	 */
	public void setTdsCgstAmount(BigDecimal tdsCgstAmount) {
		this.tdsCgstAmount = tdsCgstAmount;
	}

	/**
	 * @return the tdsSgstAmount
	 */
	public BigDecimal getTdsSgstAmount() {
		return tdsSgstAmount;
	}

	/**
	 * @param tdsSgstAmount
	 *            the tdsSgstAmount to set
	 */
	public void setTdsSgstAmount(BigDecimal tdsSgstAmount) {
		this.tdsSgstAmount = tdsSgstAmount;
	}

	/**
	 * @return the profitCentre2
	 */
	public String getProfitCentre2() {
		return profitCentre2;
	}

	/**
	 * @param profitCentre2
	 *            the profitCentre2 to set
	 */
	public void setProfitCentre2(String profitCentre2) {
		this.profitCentre2 = profitCentre2;
	}

	/**
	 * @return the subDivision
	 */
	public String getSubDivision() {
		return subDivision;
	}

	/**
	 * @param subDivision
	 *            the subDivision to set
	 */
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	/**
	 * @return the glStateCessSpecific
	 */
	public String getGlStateCessSpecific() {
		return glStateCessSpecific;
	}

	/**
	 * @param glStateCessSpecific
	 *            the glStateCessSpecific to set
	 */
	public void setGlStateCessSpecific(String glStateCessSpecific) {
		this.glStateCessSpecific = glStateCessSpecific;
	}

	/**
	 * @return the docReferenceNumber
	 */
	public String getDocReferenceNumber() {
		return docReferenceNumber;
	}

	/**
	 * @param docReferenceNumber
	 *            the docReferenceNumber to set
	 */
	public void setDocReferenceNumber(String docReferenceNumber) {
		this.docReferenceNumber = docReferenceNumber;
	}

	/**
	 * @return the userDefinedField16
	 */
	public String getUserDefinedField16() {
		return userDefinedField16;
	}

	/**
	 * @param userDefinedField16
	 *            the userDefinedField16 to set
	 */
	public void setUserDefinedField16(String userDefinedField16) {
		this.userDefinedField16 = userDefinedField16;
	}

	/**
	 * @return the userDefinedField17
	 */
	public String getUserDefinedField17() {
		return userDefinedField17;
	}

	/**
	 * @param userDefinedField17
	 *            the userDefinedField17 to set
	 */
	public void setUserDefinedField17(String userDefinedField17) {
		this.userDefinedField17 = userDefinedField17;
	}

	/**
	 * @return the userDefinedField18
	 */
	public String getUserDefinedField18() {
		return userDefinedField18;
	}

	/**
	 * @param userDefinedField18
	 *            the userDefinedField18 to set
	 */
	public void setUserDefinedField18(String userDefinedField18) {
		this.userDefinedField18 = userDefinedField18;
	}

	/**
	 * @return the userDefinedField19
	 */
	public String getUserDefinedField19() {
		return userDefinedField19;
	}

	/**
	 * @param userDefinedField19
	 *            the userDefinedField19 to set
	 */
	public void setUserDefinedField19(String userDefinedField19) {
		this.userDefinedField19 = userDefinedField19;
	}

	/**
	 * @return the userDefinedField20
	 */
	public String getUserDefinedField20() {
		return userDefinedField20;
	}

	/**
	 * @param userDefinedField20
	 *            the userDefinedField20 to set
	 */
	public void setUserDefinedField20(String userDefinedField20) {
		this.userDefinedField20 = userDefinedField20;
	}

	/**
	 * @return the userDefinedField21
	 */
	public String getUserDefinedField21() {
		return userDefinedField21;
	}

	/**
	 * @param userDefinedField21
	 *            the userDefinedField21 to set
	 */
	public void setUserDefinedField21(String userDefinedField21) {
		this.userDefinedField21 = userDefinedField21;
	}

	/**
	 * @return the userDefinedField22
	 */
	public String getUserDefinedField22() {
		return userDefinedField22;
	}

	/**
	 * @param userDefinedField22
	 *            the userDefinedField22 to set
	 */
	public void setUserDefinedField22(String userDefinedField22) {
		this.userDefinedField22 = userDefinedField22;
	}

	/**
	 * @return the userDefinedField23
	 */
	public String getUserDefinedField23() {
		return userDefinedField23;
	}

	/**
	 * @param userDefinedField23
	 *            the userDefinedField23 to set
	 */
	public void setUserDefinedField23(String userDefinedField23) {
		this.userDefinedField23 = userDefinedField23;
	}

	/**
	 * @return the userDefinedField24
	 */
	public String getUserDefinedField24() {
		return userDefinedField24;
	}

	/**
	 * @param userDefinedField24
	 *            the userDefinedField24 to set
	 */
	public void setUserDefinedField24(String userDefinedField24) {
		this.userDefinedField24 = userDefinedField24;
	}

	/**
	 * @return the userDefinedField25
	 */
	public String getUserDefinedField25() {
		return userDefinedField25;
	}

	/**
	 * @param userDefinedField25
	 *            the userDefinedField25 to set
	 */
	public void setUserDefinedField25(String userDefinedField25) {
		this.userDefinedField25 = userDefinedField25;
	}

	/**
	 * @return the userDefinedField26
	 */
	public String getUserDefinedField26() {
		return userDefinedField26;
	}

	/**
	 * @param userDefinedField26
	 *            the userDefinedField26 to set
	 */
	public void setUserDefinedField26(String userDefinedField26) {
		this.userDefinedField26 = userDefinedField26;
	}

	/**
	 * @return the userDefinedField27
	 */
	public String getUserDefinedField27() {
		return userDefinedField27;
	}

	/**
	 * @param userDefinedField27
	 *            the userDefinedField27 to set
	 */
	public void setUserDefinedField27(String userDefinedField27) {
		this.userDefinedField27 = userDefinedField27;
	}

	/**
	 * @return the userDefinedField29
	 */
	public String getUserDefinedField29() {
		return userDefinedField29;
	}

	/**
	 * @param userDefinedField29
	 *            the userDefinedField29 to set
	 */
	public void setUserDefinedField29(String userDefinedField29) {
		this.userDefinedField29 = userDefinedField29;
	}

	/**
	 * @return the userDefinedField30
	 */
	public String getUserDefinedField30() {
		return userDefinedField30;
	}

	/**
	 * @param userDefinedField30
	 *            the userDefinedField30 to set
	 */
	public void setUserDefinedField30(String userDefinedField30) {
		this.userDefinedField30 = userDefinedField30;
	}

	/**
	 * @return the supplierStateCode
	 */
	public String getSupplierStateCode() {
		return supplierStateCode;
	}

	/**
	 * @param supplierStateCode
	 *            the supplierStateCode to set
	 */
	public void setSupplierStateCode(String supplierStateCode) {
		this.supplierStateCode = supplierStateCode;
	}

	/**
	 * @return the billToState
	 */
	public String getBillToState() {
		return billToState;
	}

	/**
	 * @param billToState
	 *            the billToState to set
	 */
	public void setBillToState(String billToState) {
		this.billToState = billToState;
	}

	/**
	 * @return the dispatcherStateCode
	 */
	public String getDispatcherStateCode() {
		return dispatcherStateCode;
	}

	/**
	 * @param dispatcherStateCode
	 *            the dispatcherStateCode to set
	 */
	public void setDispatcherStateCode(String dispatcherStateCode) {
		this.dispatcherStateCode = dispatcherStateCode;
	}

	/**
	 * @return the shipToState
	 */
	public String getShipToState() {
		return shipToState;
	}

	/**
	 * @param shipToState
	 *            the shipToState to set
	 */
	public void setShipToState(String shipToState) {
		this.shipToState = shipToState;
	}

	@Override
	public String toString() {
		return "TransDocLineItem [crDrReason=" + crDrReason + ", hsnSac="
				+ hsnSac + ", supplyType=" + supplyType + ", itemCode="
				+ itemCode + ", itemDescription=" + itemDescription
				+ ", itemCategory=" + itemCategory + ", uom=" + uom
				+ ", glCodeTaxableValue=" + glCodeTaxableValue
				+ ", adjustmentRefNo=" + adjustmentRefNo
				+ ", adjustmentRefDate=" + adjustmentRefDate
				+ ", adjustedTaxableValue=" + adjustedTaxableValue
				+ ", adjustedIgstAmt=" + adjustedIgstAmt + ", adjustedCgstAmt="
				+ adjustedCgstAmt + ", adjustedSgstAmt=" + adjustedSgstAmt
				+ ", adjustedCessAmtAdvalorem=" + adjustedCessAmtAdvalorem
				+ ", adjustedCessAmtSpecific=" + adjustedCessAmtSpecific
				+ ", adjustedStateCessAmt=" + adjustedStateCessAmt
				+ ", tcsAmount=" + tcsAmount + ", qty=" + qty + ", lineItemAmt="
				+ lineItemAmt + ", taxPayable=" + taxPayable + ", location="
				+ location + ", profitCentre=" + profitCentre + ", plantCode="
				+ plantCode + ", origDocNo=" + origDocNo + ", origDocDate="
				+ origDocDate + ", userdefinedfield1=" + userdefinedfield1
				+ ", userdefinedfield2=" + userdefinedfield2
				+ ", userdefinedfield3=" + userdefinedfield3
				+ ", userDefinedField4=" + userDefinedField4
				+ ", userDefinedField5=" + userDefinedField5
				+ ", userDefinedField6=" + userDefinedField6
				+ ", userDefinedField7=" + userDefinedField7
				+ ", userDefinedField8=" + userDefinedField8
				+ ", userDefinedField9=" + userDefinedField9
				+ ", userDefinedField10=" + userDefinedField10
				+ ", userDefinedField11=" + userDefinedField11
				+ ", userDefinedField12=" + userDefinedField12
				+ ", userDefinedField13=" + userDefinedField13
				+ ", userDefinedField14=" + userDefinedField14
				+ ", userDefinedField15=" + userDefinedField15 + ", userId="
				+ userId + ", sourceFileName=" + sourceFileName + ", division="
				+ division + ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6 + ", taxperiod="
				+ taxperiod + ", docDate=" + docDate + ", crDrPreGst="
				+ crDrPreGst + ", custOrSuppType=" + custOrSuppType
				+ ", diffPercent=" + diffPercent + ", custOrSuppName="
				+ custOrSuppName + ", custOrSuppCode=" + custOrSuppCode
				+ ", custOrSuppAddress1=" + custOrSuppAddress1
				+ ", custOrSuppAddress2=" + custOrSuppAddress2
				+ ", custOrSuppAddress3=" + custOrSuppAddress3
				+ ", custOrSuppAddress4=" + custOrSuppAddress4 + ", pos=" + pos
				+ ", stateApplyingCess=" + stateApplyingCess + ", portCode="
				+ portCode + ", section7OfIgstFlag=" + section7OfIgstFlag
				+ ", reverseCharge=" + reverseCharge + ", claimRefundFlag="
				+ claimRefundFlag + ", autoPopToRefundFlag="
				+ autoPopToRefundFlag + ", eWayBillNo=" + eWayBillNo
				+ ", derivedTaxperiod=" + derivedTaxperiod + ", glCodeIgst="
				+ glCodeIgst + ", glCodeCgst=" + glCodeCgst + ", glCodeSgst="
				+ glCodeSgst + ", glCodeAdvCess=" + glCodeAdvCess
				+ ", glCodeSpCess=" + glCodeSpCess + ", glCodeStateCess="
				+ glCodeStateCess + ", acceptanceId=" + acceptanceId
				+ ", eWayBillDate=" + eWayBillDate + ", invoiceOtherCharges="
				+ invoiceOtherCharges + ", invoiceAssessableAmount="
				+ invoiceAssessableAmount + ", invoiceIgstAmount="
				+ invoiceIgstAmount + ", invoiceCgstAmount=" + invoiceCgstAmount
				+ ", invoiceSgstAmount=" + invoiceSgstAmount
				+ ", invoiceCessAdvaloremAmount=" + invoiceCessAdvaloremAmount
				+ ", invoiceCessSpecificAmount=" + invoiceCessSpecificAmount
				+ ", invoiceStateCessAmount=" + invoiceStateCessAmount
				+ ", userDefinedField28=" + userDefinedField28 + ", egstin="
				+ egstin + ", tcsFlag=" + tcsFlag + ", tdsFlag=" + tdsFlag
				+ ", docCategory=" + docCategory + ", supplierEmail="
				+ supplierEmail + ", customerEmail=" + customerEmail
				+ ", dispatcherTradeName=" + dispatcherTradeName
				+ ", dispatcherBuildingNumber=" + dispatcherBuildingNumber
				+ ", dispatcherBuildingName=" + dispatcherBuildingName
				+ ", dispatcherLocation=" + dispatcherLocation
				+ ", shipToLegalName=" + shipToLegalName
				+ ", shipToBuildingNumber=" + shipToBuildingNumber
				+ ", shipToBuildingName=" + shipToBuildingName
				+ ", shipToLocation=" + shipToLocation + ", serialNumberII="
				+ serialNumberII + ", productName=" + productName
				+ ", isService=" + isService + ", barcode=" + barcode
				+ ", batchNameOrNumber=" + batchNameOrNumber
				+ ", batchExpiryDate=" + batchExpiryDate + ", warrantyDate="
				+ warrantyDate + ", orderLineReference=" + orderLineReference
				+ ", attributeName=" + attributeName + ", attributeValue="
				+ attributeValue + ", originCountry=" + originCountry
				+ ", freeQuantity=" + freeQuantity + ", unitPrice=" + unitPrice
				+ ", itemAmount=" + itemAmount + ", itemDiscount="
				+ itemDiscount + ", preTaxAmount=" + preTaxAmount
				+ ", stateCessSpecificRate=" + stateCessSpecificRate
				+ ", stateCessSpecificAmt=" + stateCessSpecificAmt
				+ ", invStateCessSpecificAmt=" + invStateCessSpecificAmt
				+ ", totalItemAmount=" + totalItemAmount + ", tcsRateIncomeTax="
				+ tcsRateIncomeTax + ", tcsAmountIncomeTax="
				+ tcsAmountIncomeTax + ", foreignCurrency=" + foreignCurrency
				+ ", countryCode=" + countryCode + ", invoiceValueFc="
				+ invoiceValueFc + ", invoicePeriodStartDate="
				+ invoicePeriodStartDate + ", invoicePeriodEndDate="
				+ invoicePeriodEndDate + ", invoiceReference="
				+ invoiceReference + ", receiptAdviceReference="
				+ receiptAdviceReference + ", receiptAdviceDate="
				+ receiptAdviceDate + ", externalReference=" + externalReference
				+ ", projectReference=" + projectReference
				+ ", contractReference=" + contractReference
				+ ", tenderReference=" + tenderReference + ", paidAmount="
				+ paidAmount + ", balanceAmount=" + balanceAmount
				+ ", supportingDocURL=" + supportingDocURL
				+ ", additionalInformation=" + additionalInformation
				+ ", transactionType=" + transactionType + ", subSupplyType="
				+ subSupplyType + ", otherSupplyTypeDescription="
				+ otherSupplyTypeDescription + ", transporterID="
				+ transporterID + ", transporterName=" + transporterName
				+ ", transportMode=" + transportMode + ", transportDocNo="
				+ transportDocNo + ", transportDocDate=" + transportDocDate
				+ ", vehicleNo=" + vehicleNo + ", vehicleType=" + vehicleType
				+ ", origDocType=" + origDocType + ", tcsCgstAmount="
				+ tcsCgstAmount + ", tcsSgstAmount=" + tcsSgstAmount
				+ ", tdsIgstAmount=" + tdsIgstAmount + ", tdsCgstAmount="
				+ tdsCgstAmount + ", tdsSgstAmount=" + tdsSgstAmount
				+ ", profitCentre2=" + profitCentre2 + ", subDivision="
				+ subDivision + ", glStateCessSpecific=" + glStateCessSpecific
				+ ", docReferenceNumber=" + docReferenceNumber
				+ ", userDefinedField16=" + userDefinedField16
				+ ", userDefinedField17=" + userDefinedField17
				+ ", userDefinedField18=" + userDefinedField18
				+ ", userDefinedField19=" + userDefinedField19
				+ ", userDefinedField20=" + userDefinedField20
				+ ", userDefinedField21=" + userDefinedField21
				+ ", userDefinedField22=" + userDefinedField22
				+ ", userDefinedField23=" + userDefinedField23
				+ ", userDefinedField24=" + userDefinedField24
				+ ", userDefinedField25=" + userDefinedField25
				+ ", userDefinedField26=" + userDefinedField26
				+ ", userDefinedField27=" + userDefinedField27
				+ ", userDefinedField29=" + userDefinedField29
				+ ", userDefinedField30=" + userDefinedField30
				+ ", supplierStateCode=" + supplierStateCode + ", billToState="
				+ billToState + ", dispatcherStateCode=" + dispatcherStateCode
				+ ", shipToState=" + shipToState + "]";
	}
}
