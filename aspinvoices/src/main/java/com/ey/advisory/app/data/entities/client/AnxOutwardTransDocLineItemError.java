package com.ey.advisory.app.data.entities.client;

import java.util.Date;

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

@Entity
@Table(name = "ANX_OUTWARD_ERR_ITEM")
@Setter
@Getter
@ToString
public class AnxOutwardTransDocLineItemError extends TransDocHeaderItemError {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_ERR_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("crDrReason")
	@Column(name = "CRDR_REASON")
	protected String crDrReason;

	@Expose
	@SerializedName("fob")
	@Column(name = "FOB")
	protected String fob;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("exportDuty")
	@Column(name = "EXPORT_DUTY")
	protected String exportDuty;

	@Expose
	@SerializedName("itcFlag")
	@Column(name = "ITC_FLAG")
	protected String itcFlag;

	/*
	 * @Column(name = "MEMO_VALUE_IGST") protected String memoValIgst;
	 * 
	 * @Column(name = "MEMO_VALUE_CGST") protected String memoValCgst;
	 * 
	 * @Column(name = "MEMO_VALUE_SGST") protected String memoValSgst;
	 * 
	 * @Column(name = "MEMO_VALUE_CESS") protected String memoValCess;
	 * 
	 * @Column(name = "IGST_RET1_IMPACT") protected String ret1IgstImpact;
	 * 
	 * @Column(name = "CGST_RET1_IMPACT") protected String ret1CgstImpact;
	 * 
	 * @Column(name = "SGST_RET1_IMPACT") protected String ret1SgstImpact;
	 */
	@Column(name = "CREATED_BY")
	private String createdBy;

	/*
	 * @Column(name = "CREATED_ON") private Date createdDate;
	 */

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Date modifiedDate;

	// Start - Fix for Defect Error code "ER0037"
	/*
	 * @Column(name = "SALES_ORGANIZATION") protected String salesOrgnization;
	 * 
	 * @Column(name = "DISTRIBUTION_CHANNEL") protected String
	 * distributionChannel;
	 * 
	 * @Column(name = "ORIGINAL_DOC_TYPE") protected String origDocType;
	 * 
	 * @Column(name = "CUST_GSTIN") protected String cgstin;
	 * 
	 * @Column(name = "ORIGINAL_CUST_GSTIN") protected String origCgstin;
	 * 
	 * @Column(name = "BILL_TO_STATE") protected String billToState;
	 * 
	 * @Column(name = "SHIP_TO_STATE") protected String shipToState;
	 * 
	 * @Column(name = "SHIP_BILL_NUM") protected String shippingBillNo;
	 * 
	 * @Column(name = "SHIP_BILL_DATE") protected String shippingBillDate;
	 * 
	 * @Column(name = "TCS_FLAG") protected String tcsFlag;
	 * 
	 * @Column(name = "ECOM_GSTIN") protected String egstin;
	 * 
	 * @Column(name = "ACCOUNTING_VOUCHER_NUM") protected String
	 * accountingVoucherNumber;
	 * 
	 * @Column(name = "ACCOUNTING_VOUCHER_DATE") protected String
	 * accountingVoucherDate;
	 */
	// End - Fix for Defect Error code "ER0037"

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
	private String batchExpiryDate;

	@Expose
	@SerializedName("warrantyDate")
	@Column(name = "WARRANTY_DATE")
	private String warrantyDate;

	@Expose
	@SerializedName("originCountry")
	@Column(name = "ORIGIN_COUNTRY")
	private String originCountry;

	@Expose
	@SerializedName("freeQuantity")
	@Column(name = "FREE_QTY")
	private String freeQuantity;

	@Expose
	@SerializedName("unitPrice")
	@Column(name = "UNIT_PRICE")
	private String unitPrice;

	@Expose
	@SerializedName("itemAmt")
	@Column(name = "ITEM_AMT_UP_QTY")
	private String itemAmount;

	@Expose
	@SerializedName("itemDiscount")
	@Column(name = "ITEM_DISCOUNT")
	private String itemDiscount;

	@Expose
	@SerializedName("preTaxAmt")
	@Column(name = "PRE_TAX_AMOUNT")
	private String preTaxAmount;

	@Expose
	@SerializedName("totalItemAmt")
	@Column(name = "TOT_ITEM_AMT")
	private String totalItemAmount;

	@Expose
	@SerializedName("originalInvoiceNumber")
	@Column(name = "ORG_INV_NUM")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@Column(name = "ORG_INV_DATE")
	private String originalInvoiceDate;

	@Expose
	@SerializedName("invRef")
	@Column(name = "INV_REFERENCE")
	private String invoiceReference;

	@Expose
	@SerializedName("preceedingInvNo")
	@Column(name = "PRECEEDING_INV_NUM")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@Column(name = "PRECEEDING_INV_DATE")
	private String preceedingInvoiceDate;

	@Expose
	@SerializedName("orderLineRef")
	@Column(name = "ORDER_ITEM_REFERENCE")
	private String orderLineReference;

	@Expose
	@SerializedName("supportingDocURL")
	@Column(name = "SUPPORTING_DOC_URL")
	private String supportingDocURL;

	@Expose
	@SerializedName("supportingDocBase64")
	@Column(name = "SUPPORTING_DOC_BASE64")
	private String supportingDocBase64;

	@Expose
	@SerializedName("tcsCgstAmt")
	@Column(name = "TCS_CGST_AMT")
	protected String tcsCgstAmount;

	@Expose
	@SerializedName("tcsSgstAmt")
	@Column(name = "TCS_SGST_AMT")
	protected String tcsSgstAmount;

	@Expose
	@SerializedName("tdsIgstAmt")
	@Column(name = "TDS_IGST_AMT")
	protected String tdsIgstAmount;

	@Expose
	@SerializedName("tdsCgstAmt")
	@Column(name = "TDS_CGST_AMT")
	protected String tdsCgstAmount;

	@Expose
	@SerializedName("tdsSgstAmt")
	@Column(name = "TDS_SGST_AMT")
	protected String tdsSgstAmount;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUB_DIVISION")
	protected String subDivision;

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
	@SerializedName("udf28")
	@Column(name = "USERDEFINED_FIELD28")
	protected String userDefinedField28;

	@Expose
	@SerializedName("udf29")
	@Column(name = "USERDEFINED_FIELD29")
	protected String userDefinedField29;

	@Expose
	@SerializedName("udf30")
	@Column(name = "USERDEFINED_FIELD30")
	protected String userDefinedField30;

	@Expose
	@SerializedName("attributeName")
	@Column(name = "ATTRIBUTE_NAME")
	protected String attributeName;

	@Expose
	@SerializedName("attributeValue")
	@Column(name = "ATTRIBUTE_VALUE")
	protected String attributeValue;

	@Expose
	@SerializedName("stateCessSpecificRt")
	@Column(name = "STATE_CESS_SPECIFIC_RATE")
	protected String stateCessSpecificRate;

	@Expose
	@SerializedName("stateCessSpecificAmt")
	@Column(name = "STATE_CESS_SPECIFIC_AMOUNT")
	protected String stateCessSpecificAmt;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected String invStateCessSpecificAmt;

	@Expose
	@SerializedName("tcsRtIncomeTax")
	@Column(name = "TCS_RATE_INCOME_TAX")
	protected String tcsRateIncomeTax;

	@Expose
	@SerializedName("tcsAmtIncomeTax")
	@Column(name = "TCS_AMOUNT_INCOME_TAX")
	protected String tcsAmountIncomeTax;

	@Expose
	@SerializedName("receiptAdviceDate")
	@Column(name = "RECEIPT_ADVICE_DATE")
	protected String receiptAdviceDate;

	@Expose
	@SerializedName("addlInfo")
	@Column(name = "ADDITIONAL_INFORMATION")
	protected String additionalInformation;

	@Expose
	@SerializedName("docRefNo")
	@Column(name = "DOCUMENT_REFERENCE_NUMBER")
	protected String docReferenceNumber;

	@Expose
	@SerializedName("invOtherCharges")
	@Column(name = "INV_OTHER_CHARGES")
	protected String invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@Column(name = "INV_ASSESSABLE_AMT")
	protected String invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@Column(name = "INV_IGST_AMT")
	protected String invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@Column(name = "INV_CGST_AMT")
	protected String invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@Column(name = "INV_SGST_AMT")
	protected String invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@Column(name = "INV_CESS_ADVLRM_AMT")
	protected String invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@Column(name = "INV_CESS_SPECIFIC_AMT")
	protected String invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@Column(name = "INV_STATE_CESS_AMT")
	protected String invoiceStateCessAmount;

	@Expose
	@SerializedName("receiptAdviceRef")
	@Column(name = "RECEIPT_ADVICE_REFERENCE")
	protected String receiptAdviceReference;

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
	@SerializedName("custPoRefNo")
	@Column(name = "CUST_PO_REF_NUM")
	protected String customerPOReferenceNumber;

	@Expose
	@SerializedName("tenderRef")
	@Column(name = "TENDER_REFERENCE")
	protected String tenderReference;

	@Expose
	@SerializedName("paidAmt")
	@Column(name = "PAID_AMT")
	protected String paidAmount;

	@Expose
	@SerializedName("balanceAmt")
	@Column(name = "BAL_AMT")
	protected String balanceAmount;

	@Expose
	@SerializedName("custPoRefDate")
	@Column(name = "CUST_PO_REF_DATE")
	protected String customerPOReferenceDate;

	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Anx1OutWardErrHeader document;

	public AnxOutwardTransDocLineItemError() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of outward document
		// line item.
	}
}
