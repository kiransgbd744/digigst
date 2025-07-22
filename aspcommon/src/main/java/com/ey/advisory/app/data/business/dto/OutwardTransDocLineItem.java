package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OutwardTransDocLineItem //extends TransDocLineItem {
{

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("fob")
	protected BigDecimal fob;

	@Expose
	@SerializedName("exportDuty")
	@XmlElement(name = "exprt-duty")
	protected BigDecimal exportDuty;

	@Expose
	@SerializedName("itcFlag")
	@XmlElement(name = "itc-flg")
	protected String itcFlag;

	@XmlElement(name = "memo-val-igst")
	protected BigDecimal memoValIgst;

	@XmlElement(name = "memo-val-cgst")
	protected BigDecimal memoValCgst;

	@XmlElement(name = "memo-val-sgst")
	protected BigDecimal memoValSgst;

	@XmlElement(name = "memo-val-cess")
	protected BigDecimal memoValCess;

	@XmlElement(name = "igst-ret1-impct")
	protected BigDecimal ret1IgstImpact;

	@XmlElement(name = "cgst-ret1-impct")
	protected BigDecimal ret1CgstImpact;

	@XmlElement(name = "sgst-ret1-impct")
	protected BigDecimal ret1SgstImpact;

	@XmlElement(name = "created-by")
	private String createdBy;

	@XmlElement(name = "created-on")
	private Date createdDate;

	@XmlElement(name = "modified-by")
	private String modifiedBy;

	@XmlElement(name = "modified-on")
	private Date modifiedDate;

	// Start - Fix for Defect Error code "ER0037"
	@XmlElement(name = "sales-org")
	protected String salesOrgnization;

	@XmlElement(name = "dist-chnl")
	protected String distributionChannel;

	@XmlElement(name = "orig-doc-type")
	protected String origDocType;

	protected String cgstin;

	@XmlElement(name = "orig-cgstin")
	protected String origCgstin;

	@XmlElement(name = "bill-to-state")
	protected String billToState;

	@XmlElement(name = "shp-to-state")
	protected String shipToState;

	@XmlElement(name = "shp-bill-num")
	protected String shippingBillNo;

	@XmlElement(name = "shp-bill-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate shippingBillDate;

	@XmlElement(name = "tcs-flg")
	protected String tcsFlag;

	@XmlElement(name = "ecom-gstin")
	protected String egstin;

	@XmlElement(name = "acc-vouch-no")
	protected String accountingVoucherNumber;

	@XmlElement(name = "acc-vouch-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate accountingVoucherDate;
	// End - Fix for Defect Error code "ER0037"

	@Expose
	@SerializedName("serialNoII")
	@XmlElement(name = "ser-no2")
	private String serialNumberII;

	@Expose
	@SerializedName("productName")
	@XmlElement(name = "prdct-nm")
	private String productName;

	@Expose
	@SerializedName("isService")
	@XmlElement(name = "is-service")
	private String isService;

	@Expose
	@SerializedName("barcode")
	@XmlElement(name = "br-cd")
	private String barcode;

	@Expose
	@SerializedName("batchNameOrNo")
	@XmlElement(name = "btch-nm-or-no")
	private String batchNameOrNumber;

	@Expose
	@SerializedName("batchExpiryDate")
	@XmlElement(name = "btch-exp-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate batchExpiryDate;

	@Expose
	@SerializedName("warrantyDate")
	@XmlElement(name = "warranty-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate warrantyDate;

	@Expose
	@SerializedName("originCountry")
	@XmlElement(name = "orig-cntry")
	private String originCountry;

	@Expose
	@SerializedName("freeQuantity")
	@XmlElement(name = "free-qty")
	private BigDecimal freeQuantity;

	@Expose
	@SerializedName("unitPrice")
	@XmlElement(name = "unit-prc")
	private BigDecimal unitPrice;

	@Expose
	@SerializedName("itemAmt")
	@XmlElement(name = "itm-amt")
	private BigDecimal itemAmount;

	@Expose
	@SerializedName("itemDiscount")
	@XmlElement(name = "itm-discnt")
	private BigDecimal itemDiscount;

	@Expose
	@SerializedName("preTaxAmt")
	@XmlElement(name = "pre-tx-amt")
	private BigDecimal preTaxAmount;

	@Expose
	@SerializedName("totalItemAmt")
	@XmlElement(name = "tot-itm-amt")
	private BigDecimal totalItemAmount;

	@Expose
	@SerializedName("originalInvoiceNumber")
	@XmlElement(name = "orig-inv-no")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@XmlElement(name = "orig-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate originalInvoiceDate;

	@Expose
	@SerializedName("orderLineRef")
	@XmlElement(name = "ordr-ln-ref")
	private String orderLineReference;

	@Expose
	@SerializedName("tcsCgstAmt")
	@XmlElement(name = "tcs-cgst-amt")
	protected BigDecimal tcsCgstAmount;

	@Expose
	@SerializedName("tcsSgstAmt")
	@XmlElement(name = "tcs-igst-amt")
	protected BigDecimal tcsSgstAmount;

	@Expose
	@SerializedName("tdsIgstAmt")
	@XmlElement(name = "tds-igst-amt")
	protected BigDecimal tdsIgstAmount;

	@Expose
	@SerializedName("tdsCgstAmt")
	@XmlElement(name = "tds-cgst-amt")
	protected BigDecimal tdsCgstAmount;

	@Expose
	@SerializedName("tdsSgstAmt")
	@XmlElement(name = "tds-sgst-amt")
	protected BigDecimal tdsSgstAmount;

	@Expose
	@SerializedName("profitCentre2")
	@XmlElement(name = "prft-centr2")
	protected String profitCentre2;

	@Expose
	@SerializedName("subDivision")
	@XmlElement(name = "sub-div")
	protected String subDivision;

	@Expose
	@SerializedName("udf16")
	@XmlElement(name = "udf16")
	protected String userDefinedField16;

	@Expose
	@SerializedName("udf17")
	@XmlElement(name = "udf17")
	protected String userDefinedField17;

	@Expose
	@SerializedName("udf18")
	@XmlElement(name = "udf18")
	protected String userDefinedField18;

	@Expose
	@SerializedName("udf19")
	@XmlElement(name = "udf19")
	protected String userDefinedField19;

	@Expose
	@SerializedName("udf20")
	@XmlElement(name = "udf20")
	protected String userDefinedField20;

	@Expose
	@SerializedName("udf21")
	@XmlElement(name = "udf21")
	protected String userDefinedField21;

	@Expose
	@SerializedName("udf22")
	@XmlElement(name = "udf22")
	protected String userDefinedField22;

	@Expose
	@SerializedName("udf23")
	@XmlElement(name = "udf23")
	protected String userDefinedField23;

	@Expose
	@SerializedName("udf24")
	@XmlElement(name = "udf24")
	protected String userDefinedField24;

	@Expose
	@SerializedName("udf25")
	@XmlElement(name = "udf25")
	protected String userDefinedField25;

	@Expose
	@SerializedName("udf26")
	@XmlElement(name = "udf26")
	protected String userDefinedField26;

	@Expose
	@SerializedName("udf27")
	@XmlElement(name = "udf27")
	protected String userDefinedField27;

	@Expose
	@SerializedName("udf30")
	@XmlElement(name = "udf30")
	protected String userDefinedField30;

	@Expose
	@SerializedName("attribDtls")
	@XmlElement(name = "attr-dtls")
	protected List<AttributeDetails> attributeDtls;

	@Expose
	@SerializedName("stateCessSpecificRt")
	@XmlElement(name = "st-cess-sp-rt")
	protected BigDecimal stateCessSpecificRate;

	@Expose
	@SerializedName("stateCessSpecificAmt")
	@XmlElement(name = "st-ces-sp-rt")
	protected BigDecimal stateCessSpecificAmt;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@XmlElement(name = "inv-st-sp-amt")
	protected BigDecimal invStateCessSpecificAmt;

	@Expose
	@SerializedName("tcsRtIncomeTax")
	@XmlElement(name = "tcs-rt-incm-tx")
	protected BigDecimal tcsRateIncomeTax;

	@Expose
	@SerializedName("tcsAmtIncomeTax")
	@XmlElement(name = "tcs-amt-inc-tx")
	protected BigDecimal tcsAmountIncomeTax;

	@Expose
	@SerializedName("docRefNo")
	@XmlElement(name = "doc-ref-no")
	protected String docReferenceNumber;

	@Expose
	@SerializedName("invOtherCharges")
	@XmlElement(name = "inv-oth-chrg")
	protected BigDecimal invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@XmlElement(name = "inv-asses-amt")
	protected BigDecimal invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@XmlElement(name = "inv-igst-amt")
	protected BigDecimal invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@XmlElement(name = "inv-cgst-amt")
	protected BigDecimal invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@XmlElement(name = "inv-sgst-amt")
	protected BigDecimal invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@XmlElement(name = "inv-css-adv-amt")
	protected BigDecimal invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@XmlElement(name = "inv-ces-sp-amt")
	protected BigDecimal invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@XmlElement(name = "inv-st-cess-amt")
	protected BigDecimal invoiceStateCessAmount;

	@Expose
	@SerializedName("paidAmt")
	@XmlElement(name = "paid-amt")
	protected BigDecimal paidAmount;

	@Expose
	@SerializedName("balanceAmt")
	@XmlElement(name = "bal-amt")
	protected BigDecimal balanceAmount;

	@XmlElement(name = "tds-flg")
	protected String tdsFlag;
	
	@Expose
	@SerializedName("itemNo")
	@XmlElement(name = "itm-no")
	protected String lineNo;

	// Required only if the doc is Credit/Debit Note.
	@Expose
	@SerializedName("crDrReason")
	@XmlElement(name = "cr-dr-rsn")
	protected String crDrReason;

	@Expose
	@SerializedName("hsnsacCode")
	@XmlElement(name = "hsn-sac")
	protected String hsnSac;

	@Expose
	@SerializedName("supplyType")
	@XmlElement(name = "supp-type")
	protected String supplyType;

	@Expose
	@SerializedName("productCode")
	@XmlElement(name = "itm-cd")
	protected String itemCode;

	@Expose
	@SerializedName("itemDesc")
	@XmlElement(name = "itm-desc")
	protected String itemDescription;

	@Expose
	@SerializedName("itemType")
	@XmlElement(name = "itm-type")
	protected String itemCategory;

	@Expose
	@SerializedName("itemUqc")
	@XmlElement(name = "uom")
	protected String uom;

	@Expose
	@SerializedName("glCodeTaxableVal")
	@XmlElement(name = "gl-cd-tx-val")
	protected String glCodeTaxableValue;

	@Expose
	@SerializedName("adjustmentRefNo")
	@XmlElement(name = "adj-ref-no")
	protected String adjustmentRefNo;

	@Expose
	@SerializedName("adjustmentRefDate")
	@XmlElement(name = "adj-ref-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate adjustmentRefDate;

	@Expose
	@SerializedName("adjustedTaxableValue")
	@XmlElement(name = "adj-tx-val")
	protected BigDecimal adjustedTaxableValue;

	@Expose
	@SerializedName("adjustedIgstAmt")
	@XmlElement(name = "adj-igst-amt")
	protected BigDecimal adjustedIgstAmt;

	@Expose
	@SerializedName("adjustedCgstAmt")
	@XmlElement(name = "adj-cgst-amt")
	protected BigDecimal adjustedCgstAmt;

	@Expose
	@SerializedName("adjustedSgstAmt")
	@XmlElement(name = "adj-sgst-amt")
	protected BigDecimal adjustedSgstAmt;

	@Expose
	@SerializedName("adjustedCessAmtAdvalorem")
	@XmlElement(name = "adj-cess-amt-adv")
	protected BigDecimal adjustedCessAmtAdvalorem;

	@Expose
	@SerializedName("adjustedCessAmtSpecific")
	@XmlElement(name = "adj-cs-amt-spec")
	protected BigDecimal adjustedCessAmtSpecific;

	@Expose
	@SerializedName("adjustedStateCessAmt")
	@XmlElement(name = "adj-st-cess-amt")
	protected BigDecimal adjustedStateCessAmt;

	@Expose
	@SerializedName("tcsAmt")
	@XmlElement(name = "tcs-amt")
	protected BigDecimal tcsAmount;

	@Expose
	@SerializedName("itemQty")
	@XmlElement(name = "qty")
	protected BigDecimal qty;

	@Expose
	@SerializedName("lineItemAmt")
	@XmlElement(name = "line-itm-amt")
	private BigDecimal lineItemAmt;

	@Expose
	@SerializedName("taxPayable")
	@XmlElement(name = "tx-paybl")
	protected BigDecimal taxPayable;

	@Expose
	@SerializedName("location")
	protected String location;

	@Expose
	@SerializedName("profitCentre")
	@XmlElement(name = "profit-cntr")
	protected String profitCentre;

	@Expose
	@SerializedName("plantCode")
	@XmlElement(name = "plnt-cd")
	protected String plantCode;

	/* Amendment details */
	@Expose
	@SerializedName("originalDocNo")
	@XmlElement(name = "orig-doc-num")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@XmlElement(name = "orig-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate origDocDate;

	@Expose
	@SerializedName("udf1")
	@XmlElement(name = "udf1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@XmlElement(name = "udf2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@XmlElement(name = "udf3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@XmlElement(name = "udf4")
	protected String userDefinedField4;

	@Expose
	@SerializedName("udf5")
	@XmlElement(name = "udf5")
	protected String userDefinedField5;

	@Expose
	@SerializedName("udf6")
	@XmlElement(name = "udf6")
	protected String userDefinedField6;

	@Expose
	@SerializedName("udf7")
	@XmlElement(name = "udf7")
	protected String userDefinedField7;

	@Expose
	@SerializedName("udf8")
	@XmlElement(name = "udf-8")
	protected String userDefinedField8;

	@Expose
	@SerializedName("udf9")
	@XmlElement(name = "udf9")
	protected String userDefinedField9;

	@Expose
	@SerializedName("udf10")
	@XmlElement(name = "udf10")
	protected String userDefinedField10;

	@Expose
	@SerializedName("udf11")
	@XmlElement(name = "udf11")
	protected String userDefinedField11;

	@Expose
	@SerializedName("udf12")
	@XmlElement(name = "udf12")
	protected String userDefinedField12;

	@Expose
	@SerializedName("udf13")
	@XmlElement(name = "udf13")
	protected String userDefinedField13;

	@Expose
	@SerializedName("udf14")
	@XmlElement(name = "udf14")
	protected String userDefinedField14;

	@Expose
	@SerializedName("udf15")
	@XmlElement(name = "udf15")
	protected String userDefinedField15;

	// Start - Fix for Defect Error code "ER0037"
	@XmlElement(name = "user-id")
	protected String userId;

	@XmlElement(name = "src-file-nm")
	protected String sourceFileName;

	protected String division;

	@Expose
	@SerializedName("profitCentre3")
	@XmlElement(name = "profit-cntr3")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@XmlElement(name = "profit-cntr4")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@XmlElement(name = "profit-cntr5")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@XmlElement(name = "profit-cntr6")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@XmlElement(name = "profit-cntr7")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@XmlElement(name = "profit-cntr8")
	protected String userAccess6;

	@XmlElement(name = "ret-period")
	protected String taxperiod;

	@XmlElement(name = "doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;

	@XmlElement(name = "cr-dr-pre-gst")
	protected String crDrPreGst;

	@XmlElement(name = "cust-supp-type")
	protected String custOrSuppType;

	@XmlElement(name = "diff-prcnt")
	protected String diffPercent;

	@XmlElement(name = "cust-supp-nm")
	protected String custOrSuppName;

	@XmlElement(name = "cust-supp-cd")
	protected String custOrSuppCode;

	@XmlElement(name = "cust-supp-add1")
	protected String custOrSuppAddress1;

	@XmlElement(name = "cust-supp-add2")
	protected String custOrSuppAddress2;

	@XmlElement(name = "cust-supp-add3")
	protected String custOrSuppAddress3;

	@XmlElement(name = "cust-supp-add4")
	protected String custOrSuppAddress4;

	protected String pos;

	@XmlElement(name = "state-app-cs")
	protected String stateApplyingCess;

	@XmlElement(name = "shp-prt-cd")
	protected String portCode;

	@XmlElement(name = "sec70-igst-flg")
	protected String section7OfIgstFlag;

	@XmlElement(name = "rev-chrg")
	protected String reverseCharge;

	@XmlElement(name = "clm-ref-flg")
	protected String claimRefundFlag;

	@XmlElement(name = "auto-pop-to-ref")
	protected String autoPopToRefundFlag;

	@XmlElement(name = "ewb-no")
	protected String eWayBillNo;

	@XmlElement(name = "ewb-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate eWayBillDate;
	// End - Fix for Defect Error code "ER0037"

	// This field is added in line items for report purpose
	@Expose
	@SerializedName("derivedTaxperiod")
	@XmlElement(name = "derv-ret-perd")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("glCodeIgst")
	@XmlElement(name = "gl-cd-igst")
	protected String glCodeIgst;

	@Expose
	@SerializedName("glCodeCgst")
	@XmlElement(name = "gl-cd-cgst")
	protected String glCodeCgst;

	@Expose
	@SerializedName("glCodeSgst")
	@XmlElement(name = "gl-cd-sgst")
	protected String glCodeSgst;

	@Expose
	@SerializedName("glCodeAdvCess")
	@XmlElement(name = "glcd-adv-cess")
	protected String glCodeAdvCess;

	@Expose
	@SerializedName("glCodeSpCess")
	@XmlElement(name = "glcd-sp-cess")
	protected String glCodeSpCess;

	@Expose
	@SerializedName("glCodeStateCess")
	@XmlElement(name = "glcd-st-cess")
	protected String glCodeStateCess;
	
	@XmlElement(name = "tx-rt")
	protected BigDecimal taxRate;
	
	@Expose
	@SerializedName("igstRt")
	@XmlElement(name = "igst-rt")
	protected BigDecimal igstRate;
		
	@Expose
	@SerializedName("cgstRt")
	@XmlElement(name = "cgst-rt")
	protected BigDecimal cgstRate;
	
	@Expose
	@SerializedName("sgstRt")
	@XmlElement(name = "sgst-rt")
	protected BigDecimal sgstRate;

	@Expose
	@SerializedName("igstAmt")
	@XmlElement(name = "igst-amt")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@XmlElement(name = "cgst-amt")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@XmlElement(name = "sgst-amt")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@XmlElement(name = "cess-amt-adv")
	protected BigDecimal cessAmountAdvalorem;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@XmlElement(name = "cess-amt-spec")
	protected BigDecimal cessAmountSpecific;

	@Expose
	@SerializedName("cessRtAdvalorem")
	@XmlElement(name = "cess-rt-adv")
	protected BigDecimal cessRateAdvalorem;

	@Expose
	@SerializedName("cessRtSpecific")
	@XmlElement(name = "cess-rt-spec")
	protected BigDecimal cessRateSpecific;
	
	@Expose
	@SerializedName("stateCessRt")
	@XmlElement(name = "state-cs-rt")
	protected BigDecimal stateCessRate;
	
	@Expose
	@SerializedName("stateCessAmt")
	@XmlElement(name = "st-cess-amt")
	protected BigDecimal stateCessAmount;
	
	@Expose
	@SerializedName("otherValues")
	@XmlElement(name = "oth-val")
	protected BigDecimal otherValues;
		
	@Expose
	@SerializedName("taxableVal")
	@XmlElement(name = "tax-val")
	protected BigDecimal taxableValue;	
	
	
	
	/**
	 * This field can be used to represent amounts like invoice amount,
	 * credit note amount, advance received amount, advance adjusted 
	 * amount etc.
	 */
	@Transient
	@XmlElement(name = "tot-amt")
	protected BigDecimal totalAmt;
	
	@XmlTransient
	private OutwardTransDocument document;
	
	@Expose
	@SerializedName("preceedingInvNo")
	@XmlElement(name = "prec-inv-no")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@XmlElement(name = "prec-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate preceedingInvoiceDate;
	
	@Expose
	@SerializedName("invRef")
	@XmlElement(name = "inv-ref")
	private String invoiceReference;
	
	@Expose
	@SerializedName("receiptAdviceRef")
	@XmlElement(name = "recp-adv-refrnc")
	protected String receiptAdviceReference;
	
	@Expose
	@SerializedName("receiptAdviceDate")
	@XmlElement(name = "rcpt-adv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate receiptAdviceDate;
	
	@Expose
	@SerializedName("tenderRef")
	@XmlElement(name = "tndr-ref")
	protected String tenderReference;
	
	@Expose
	@SerializedName("externalRef")
	@XmlElement(name = "ext-ref")
	protected String externalReference;

	@Expose
	@SerializedName("projectRef")
	@XmlElement(name = "prj-ref")
	protected String projectReference;

	@Expose
	@SerializedName("contractRef")
	@XmlElement(name = "contrct-ref")
	protected String contractReference;

	@Expose
	@SerializedName("custPoRefNo")
	@XmlElement(name = "cst-po-ref-no")
	protected String customerPOReferenceNumber;
	
	@Expose
	@SerializedName("custPoRefDate")
	@XmlElement(name = "cust-po-ref-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate customerPOReferenceDate;

	@Expose
	@SerializedName("supportingDocURL")
	@XmlElement(name = "support-doc-url")
	private String supportingDocURL;

	@Expose
	@SerializedName("supportingDocBase64")
	@XmlElement(name = "support-doc-b64")
	private String supportingDocBase64;
	
	@Expose
	@SerializedName("addlInfo")
	@XmlElement(name = "add-inf")
	protected String additionalInformation;
	
	@Expose
	@SerializedName("attributeName")
	@XmlElement(name = "attr-nm")
	protected String attributeName;

	@Expose
	@SerializedName("attributeValue")
	@XmlElement(name = "attr-val")
	protected String attributeValue;


	public OutwardTransDocLineItem() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of outward document
		// line item.
	}

	
	public void addAttributeDtls(List<AttributeDetails> attributeDtlList) {
		List<AttributeDetails> filterList = attributeDtlList.stream()
				.filter(o -> !AttributeDetails.isEmpty(o))
				.collect(Collectors.toList());
		if (!filterList.isEmpty()) {
			attributeDtls = new ArrayList<>();
			attributeDtls.addAll(filterList);
		}

	}
	

}
