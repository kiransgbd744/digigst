/**
 * 
 */
package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class OtrdTrnsDocLnItemsReq {
	
	@XmlTransient
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	protected BigDecimal fob;
	@XmlElement(name = "exprt-duty")
	protected BigDecimal exportDuty;
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
	@XmlElement(name = "ser-no2")
	private String serialNumberII;
	@XmlElement(name = "prdct-nm")
	private String productName;
	@XmlElement(name = "is-service")
	private String isService;
	@XmlElement(name = "br-cd")
	private String barcode;
	@XmlElement(name = "btch-nm-or-no")
	private String batchNameOrNumber;
	@XmlElement(name = "btch-exp-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate batchExpiryDate;
	@XmlElement(name = "warranty-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate warrantyDate;
	@XmlElement(name = "orig-cntry")
	private String originCountry;
	@XmlElement(name = "free-qty")
	private BigDecimal freeQuantity;
	@XmlElement(name = "unit-prc")
	private BigDecimal unitPrice;
	@XmlElement(name = "itm-amt")
	private BigDecimal itemAmount;
	@XmlElement(name = "itm-discnt")
	private BigDecimal itemDiscount;
	@XmlElement(name = "pre-tx-amt")
	private BigDecimal preTaxAmount;
	@XmlElement(name = "tot-itm-amt")
	private BigDecimal totalItemAmount;
	@XmlElement(name = "orig-inv-no")
	private String originalInvoiceNumber;
	@XmlElement(name = "orig-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate originalInvoiceDate;
	@XmlElement(name = "ordr-ln-ref")
	private String orderLineReference;
	@XmlElement(name = "tcs-cgst-amt")
	protected BigDecimal tcsCgstAmount;
	@XmlElement(name = "tcs-igst-amt")
	protected BigDecimal tcsSgstAmount;
	@XmlElement(name = "tds-igst-amt")
	protected BigDecimal tdsIgstAmount;
	@XmlElement(name = "tds-cgst-amt")
	protected BigDecimal tdsCgstAmount;
	@XmlElement(name = "tds-sgst-amt")
	protected BigDecimal tdsSgstAmount;
	@XmlElement(name = "prft-centr2")
	protected String profitCentre2;
	@XmlElement(name = "sub-div")
	protected String subDivision;
	@XmlElement(name = "udf16")
	protected String userDefinedField16;
	@XmlElement(name = "udf17")
	protected String userDefinedField17;
	@XmlElement(name = "udf18")
	protected String userDefinedField18;
	@XmlElement(name = "udf19")
	protected String userDefinedField19;
	@XmlElement(name = "udf20")
	protected String userDefinedField20;
	@XmlElement(name = "udf21")
	protected String userDefinedField21;
	@XmlElement(name = "udf22")
	protected String userDefinedField22;
	@XmlElement(name = "udf23")
	protected String userDefinedField23;
	@XmlElement(name = "udf24")
	protected String userDefinedField24;
	@XmlElement(name = "udf25")
	protected String userDefinedField25;
	@XmlElement(name = "udf26")
	protected String userDefinedField26;
	@XmlElement(name = "udf27")
	protected String userDefinedField27;
	@XmlElement(name = "udf30")
	protected String userDefinedField30;
	@XmlElement(name = "st-cess-sp-rt")
	protected BigDecimal stateCessSpecificRate;
	@XmlElement(name = "st-ces-sp-rt")
	protected BigDecimal stateCessSpecificAmt;
	@XmlElement(name = "inv-st-sp-amt")
	protected BigDecimal invStateCessSpecificAmt;
	
	@XmlElement(name = "tcs-rt-incm-tx")
	protected BigDecimal tcsRateIncomeTax;
	@XmlElement(name = "tcs-amt-inc-tx")
	protected BigDecimal tcsAmountIncomeTax;
	@XmlElement(name = "doc-ref-no")
	protected String docReferenceNumber;
	@XmlElement(name = "inv-oth-chrg")
	protected BigDecimal invoiceOtherCharges;
	@XmlElement(name = "inv-asses-amt")
	protected BigDecimal invoiceAssessableAmount;
	@XmlElement(name = "inv-igst-amt")
	protected BigDecimal invoiceIgstAmount;
	@XmlElement(name = "inv-cgst-amt")
	protected BigDecimal invoiceCgstAmount;
	@XmlElement(name = "inv-sgst-amt")
	protected BigDecimal invoiceSgstAmount;
	@XmlElement(name = "inv-css-adv-amt")
	protected BigDecimal invoiceCessAdvaloremAmount;
	@XmlElement(name = "inv-ces-sp-amt")
	protected BigDecimal invoiceCessSpecificAmount;
	@XmlElement(name = "inv-st-cess-amt")
	protected BigDecimal invoiceStateCessAmount;
	@XmlElement(name = "paid-amt")
	protected BigDecimal paidAmount;
	@XmlElement(name = "bal-amt")
	protected BigDecimal balanceAmount;
	@XmlElement(name = "tds-flg")
	protected String tdsFlag;
	@XmlTransient
	private OutwardTransDocument document;
	@XmlElement(name = "itm-no")
	protected String lineNo;
	// Required only if the doc is Credit/Debit Note.
	@XmlElement(name = "cr-dr-rsn")
	protected String crDrReason;
	@XmlElement(name = "hsn-sac")
	protected String hsnSac;
	@XmlElement(name = "supp-type")
	protected String supplyType;
	@XmlElement(name = "itm-cd")
	protected String itemCode;
	@XmlElement(name = "itm-desc")
	protected String itemDescription;
	@XmlElement(name = "itm-type")
	protected String itemCategory;
	@XmlElement(name = "uom")
	protected String uom;
	@XmlElement(name = "gl-cd-tx-val")
	protected String glCodeTaxableValue;
	@XmlElement(name = "adj-ref-no")
	protected String adjustmentRefNo;
	@XmlElement(name = "adj-ref-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate adjustmentRefDate;
	@XmlElement(name = "adj-tx-val")
	protected BigDecimal adjustedTaxableValue;
	@XmlElement(name = "adj-igst-amt")
	protected BigDecimal adjustedIgstAmt;
	@XmlElement(name = "adj-cgst-amt")
	protected BigDecimal adjustedCgstAmt;
	@XmlElement(name = "adj-sgst-amt")
	protected BigDecimal adjustedSgstAmt;
	@XmlElement(name = "adj-cess-amt-adv")
	protected BigDecimal adjustedCessAmtAdvalorem;
	@XmlElement(name = "adj-cs-amt-spec")
	protected BigDecimal adjustedCessAmtSpecific;
	@XmlElement(name = "adj-st-cess-amt")
	protected BigDecimal adjustedStateCessAmt;
	@XmlElement(name = "tcs-amt")
	protected BigDecimal tcsAmount;
	@XmlElement(name = "qty")
	protected BigDecimal qty;
	@XmlElement(name = "line-itm-amt")
	private BigDecimal lineItemAmt;
	@XmlElement(name = "tx-paybl")
	protected BigDecimal taxPayable;
	protected String location;
	@XmlElement(name = "profit-cntr")
	protected String profitCentre;
	@XmlElement(name = "plnt-cd")
	protected String plantCode;
	/* Amendment details */
	@XmlElement(name = "orig-doc-num")
	protected String origDocNo;
	@XmlElement(name = "orig-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate origDocDate;
	@XmlElement(name = "udf1")
	protected String userdefinedfield1;
	@XmlElement(name = "udf2")
	protected String userdefinedfield2;
	@XmlElement(name = "udf3")
	protected String userdefinedfield3;
	@XmlElement(name = "udf4")
	protected String userDefinedField4;
	@XmlElement(name = "udf5")
	protected String userDefinedField5;
	@XmlElement(name = "udf6")
	protected String userDefinedField6;
	@XmlElement(name = "udf7")
	protected String userDefinedField7;
	@XmlElement(name = "udf-8")
	protected String userDefinedField8;
	@XmlElement(name = "udf9")
	protected String userDefinedField9;
	@XmlElement(name = "udf10")
	protected String userDefinedField10;
	@XmlElement(name = "udf11")
	protected String userDefinedField11;
	@XmlElement(name = "udf12")
	protected String userDefinedField12;
	@XmlElement(name = "udf13")
	protected String userDefinedField13;
	@XmlElement(name = "udf14")
	protected String userDefinedField14;
	@XmlElement(name = "udf15")
	protected String userDefinedField15;
	// Start - Fix for Defect Error code "ER0037"
	@XmlElement(name = "user-id")
	protected String userId;
	@XmlElement(name = "src-file-nm")
	protected String sourceFileName;
	protected String division;
	@XmlElement(name = "profit-cntr3")
	protected String userAccess1;
	@XmlElement(name = "profit-cntr4")
	protected String userAccess2;
	@XmlElement(name = "profit-cntr5")
	protected String userAccess3;
	@XmlElement(name = "profit-cntr6")
	protected String userAccess4;
	@XmlElement(name = "profit-cntr7")
	protected String userAccess5;
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
	@XmlElement(name = "derv-ret-perd")
	protected Integer derivedTaxperiod;
	@XmlElement(name = "gl-cd-igst")
	protected String glCodeIgst;
	@XmlElement(name = "gl-cd-cgst")
	protected String glCodeCgst;
	@XmlElement(name = "gl-cd-sgst")
	protected String glCodeSgst;
	@XmlElement(name = "glcd-adv-cess")
	protected String glCodeAdvCess;
	@XmlElement(name = "glcd-sp-cess")
	protected String glCodeSpCess;
	@XmlElement(name = "glcd-st-cess")
	protected String glCodeStateCess;
	@XmlElement(name = "tx-rt")
	protected BigDecimal taxRate;
	@XmlElement(name = "igst-rt")
	protected BigDecimal igstRate;
	@XmlElement(name = "cgst-rt")
	protected BigDecimal cgstRate;
	@XmlElement(name = "sgst-rt")
	protected BigDecimal sgstRate;
	@XmlElement(name = "igst-amt")
	protected BigDecimal igstAmount;
	@XmlElement(name = "cgst-amt")
	protected BigDecimal cgstAmount;
	@XmlElement(name = "sgst-amt")
	protected BigDecimal sgstAmount;
	@XmlElement(name = "cess-amt-adv")
	protected BigDecimal cessAmountAdvalorem;
	@XmlElement(name = "cess-amt-spec")
	protected BigDecimal cessAmountSpecific;
	@XmlElement(name = "cess-rt-adv")
	protected BigDecimal cessRateAdvalorem;
	@XmlElement(name = "cess-rt-spec")
	protected BigDecimal cessRateSpecific;
	@XmlElement(name = "state-cs-rt")
	protected BigDecimal stateCessRate;
	@XmlElement(name = "st-cess-amt")
	protected BigDecimal stateCessAmount;
	@XmlElement(name = "oth-val")
	protected BigDecimal otherValues;
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
	
	@XmlElementWrapper(name = "attribute-dtls")
	@XmlElement(name = "attribute-dtl")
	protected List<AttributeDetails> attributeDtls;
	
	
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
}
