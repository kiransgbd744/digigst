package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class is the base class for all financial transaction documents (like
 * invoices, invoice amendments, credit notes, debit notes, export docs etc).
 * This document contains the complete transactional information, including all
 * the line items (i.e. this does not represent rolled up information)
 * 
 * This can be used to represent all transactional financial documents that we
 * accept at an invoice level - B2B, B2BA, B2CL, B2CLA, B2CS (at invoicel
 * level), B2CSA (at invoice level), CDNR, CDNRA, CDNUR, CDNURA. Some of the
 * above types of documents can be accepted at a rolled up format, in which case
 * this class cannot be used to represent it.
 * 
 * @author Umesha M
 *
 */
@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class TransErrorDocument extends Anx1DocumentError {

	/* The KEY fields of a financial document */
	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;
	
	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;
	
	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;
	
	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	/**
	 * The financial year in the format 1819, 1920, 2021 etc. This can be used
	 * for validating the invoices for duplicates. According to government
	 * rules, two document numbers belonging to the same type cannot be the
	 * same, within the same financial year.
	 */

	@Expose
	@SerializedName("fiYear")
	@Column(name = "FI_YEAR")
	protected String finYear;

	/**
	 * A unique string by which we can identify the financial document. The key
	 * can be different for inward and outward documents.
	 * 
	 */
	@Column(name = "DOC_KEY")
	protected String docKey;

	/* End of the KEY fields of the financial document */

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	/**
	 * SGSTIN is mandatory for outward documents, but can be null for inward
	 * documents.
	 */

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	/**
	 * CGSTIN is mandatory for inward documents, but can be null for outward
	 * documents.
	 */
	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	@Expose
	@SerializedName("ecomCustGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("reverseCharge")
	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos; // State code

	/**
	 * For Outward invoices, it's called customer code and for Inward Invoices
	 * it's called supplier code.
	 */
	@Expose
	@SerializedName("custOrSuppCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("custOrSuppType")
	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;

	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;

	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;

	@Expose
	@SerializedName("custOrSuppAddress1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSuppAddress2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	@Expose
	@SerializedName("custOrSuppAddress3")
	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;

	@Expose
	@SerializedName("custOrSuppAddress4")
	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;

	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	@Expose
	@SerializedName("custOrSuppName")
	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	/* Amendment details */

	@Expose
	@SerializedName("originalDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String origDocType;

	/*@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	protected String origDocDate;
*/
	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private String isError;

	/**
	 * Sets if the document has information messages
	 */
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected String isInfo;

	@Expose
	@SerializedName("docAmount")
	@Column(name = "DOC_AMT")
	protected String docAmount; // Total Invoice/Document Amount

	@Column(name = "TAXABLE_VALUE")
	protected String taxableValue; // Total Taxable Value.

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Transient
	protected String status;

	@Expose
	@SerializedName("accountVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accountVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected String accountingVoucherDate;

	/* Org Hierarchy Details */
	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUBDIVISION")
	protected String subDivision;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	// Required only if the doc is Credit/Debit Note.
	

	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String isCrDrPreGst;

	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;
	
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
	@SerializedName("eWayBillNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	@Expose
	@SerializedName("eWayBillDate")
	@Column(name = "EWAY_BILL_DATE")
	protected String eWayBillDate;

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

}
