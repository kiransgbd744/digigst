package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@MappedSuperclass
@Getter
@Setter
@ToString
public class TransDocHeaderItemError extends TransDocErrorLineItem {

	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected String irnDate;

	@Expose
	@SerializedName("taxScheme")
	@Column(name = "TAX_SCHEME")
	protected String taxScheme;

	@Expose
	@SerializedName("docCat")
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Expose
	@SerializedName("canReason")
	@Column(name = "CANCEL_REASON")
	protected String cancellationReason;

	@Expose
	@SerializedName("canRemarks")
	@Column(name = "CANCEL_REMARKS")
	protected String cancellationRemarks;

/*	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;*/

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	/*@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;*/

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	/**
	 * CGSTIN is mandatory for inward documents, but can be null for outward
	 * documents.
	 */
	/*@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;*/

	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	/*@Expose
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
	protected String pos;*/ // State code

	/**
	 * For Outward invoices, it's called customer code and for Inward Invoices
	 * it's called supplier code.
	 */
	/*@Expose
	@SerializedName("custOrSuppCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("custOrSuppType")
	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;*/

	/*@Expose
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
	protected String custOrSuppAddress4;*/

	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	/*@Expose
	@SerializedName("custOrSuppName")
	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;
*/
	@Expose
	@SerializedName("supTradeName")
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@SerializedName("supLegalName")
	@Column(name = "SUPP_LEGAL_NAME")
	protected String supplierLegalName;

	@Expose
	@SerializedName("supBuildingNo")
	@Column(name = "SUPP_BUILDING_NUM")
	protected String supplierBuildingNumber;

	@Expose
	@SerializedName("supBuildingName")
	@Column(name = "SUPP_BUILDING_NAME")
	protected String supplierBuildingName;

	@Expose
	@SerializedName("supLocation")
	@Column(name = "SUPP_LOCATION")
	protected String supplierLocation;

	@Expose
	@SerializedName("supPincode")
	@Column(name = "SUPP_PINCODE")
	protected String supplierPincode;

	@Expose
	@SerializedName("supStateCode")
	@Column(name = "SUPP_STATE_CODE")
	protected String supplierStateCode;

	@Expose
	@SerializedName("supPhone")
	@Column(name = "SUPP_PHONE")
	protected String supplierPhone;

	@Expose
	@SerializedName("supEmail")
	@Column(name = "SUPP_EMAIL")
	protected String supplierEmail;

	@Expose
	@SerializedName("custTradeName")
	@Column(name = "CUST_TRADE_NAME")
	protected String customerTradeName;

	@Expose
	@SerializedName("custPincode")
	@Column(name = "CUST_PINCODE")
	protected String customerPincode;

	@Expose
	@SerializedName("custPhone")
	@Column(name = "CUST_PHONE")
	protected String customerPhone;

	@Expose
	@SerializedName("custEmail")
	@Column(name = "CUST_EMAIL")
	protected String customerEmail;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

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
	@SerializedName("dispatcherPincode")
	@Column(name = "DISPATCHER_PINCODE")
	protected String dispatcherPincode;

	@Expose
	@SerializedName("dispatcherStateCode")
	@Column(name = "DISPATCHER_STATE_CODE")
	protected String dispatcherStateCode;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@SerializedName("shipToTradeName")
	@Column(name = "SHIP_TO_TRADE_NAME")
	protected String shipToTradeName;

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
	@SerializedName("shipToPincode")
	@Column(name = "SHIP_TO_PINCODE")
	protected String shipToPincode;

	@Expose
	@SerializedName("custPANOrAadhaar")
	@Column(name = "CUSTOMER_PAN_OR_AADHAAR")
	protected String customerPANOrAadhaar;

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
	protected String transportDocDate;

	@Expose
	@SerializedName("distance")
	@Column(name = "DISTANCE")
	protected String distance;

	@Expose
	@SerializedName("vehicleNo")
	@Column(name = "VEHICLE_NUM")
	protected String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@SerializedName("invPeriodStartDate")
	@Column(name = "INV_PERIOD_START_DATE")
	protected String invoicePeriodStartDate;

	@Expose
	@SerializedName("invPeriodEndDate")
	@Column(name = "INV_PERIOD_END_DATE")
	protected String invoicePeriodEndDate;

	@Expose
	@SerializedName("payeeName")
	@Column(name = "PAYEE_NAME")
	protected String payeeName;

	@Expose
	@SerializedName("modeOfPayment")
	@Column(name = "MODE_OF_PAYMENT")
	protected String modeOfPayment;

	@Expose
	@SerializedName("branchOrIfscCode")
	@Column(name = "BRANCH_IFSC_CODE")
	protected String branchOrIfscCode;

	@Expose
	@SerializedName("paymentTerms")
	@Column(name = "PAYMENT_TERMS")
	protected String paymentTerms;

	@Expose
	@SerializedName("paymentInstruction")
	@Column(name = "PAYMENT_INSTRUCTION")
	protected String paymentInstruction;

	@Expose
	@SerializedName("creditTransfer")
	@Column(name = "CR_TRANSFER")
	protected String creditTransfer;

	@Expose
	@SerializedName("directDebit")
	@Column(name = "DB_DIRECT")
	protected String directDebit;

	@Expose
	@SerializedName("creditDays")
	@Column(name = "CR_DAYS")
	protected String creditDays;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected String paymentDueDate;

	@Expose
	@SerializedName("accDetail")
	@Column(name = "ACCOUNT_DETAIL")
	protected String accountDetail;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

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

	/*
	 * @Expose
	 * 
	 * @SerializedName("invOtherCharges")
	 * 
	 * @Column(name = "INV_OTHER_CHARGES") protected String invoiceOtherCharges;
	 */
	/*
	 * @Expose
	 * 
	 * @SerializedName("invAssessableAmt")
	 * 
	 * @Column(name = "INV_ASSESSABLE_AMT") protected String
	 * invoiceAssessableAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invIgstAmt")
	 * 
	 * @Column(name = "INV_IGST_AMT") protected String invoiceIgstAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invCgstAmt")
	 * 
	 * @Column(name = "INV_CGST_AMT") protected String invoiceCgstAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invSgstAmt")
	 * 
	 * @Column(name = "INV_SGST_AMT") protected String invoiceSgstAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invCessAdvaloremAmt")
	 * 
	 * @Column(name = "INV_CESS_ADVLRM_AMT") protected String
	 * invoiceCessAdvaloremAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invCessSpecificAmt")
	 * 
	 * @Column(name = "INV_CESS_SPECIFIC_AMT") protected String
	 * invoiceCessSpecificAmount;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("invStateCessAmt")
	 * 
	 * @Column(name = "INV_STATE_CESS_AMT") protected String
	 * invoiceStateCessAmount;
	 */

	@Expose
	@SerializedName("roundOff")
	@Column(name = "ROUND_OFF")
	protected String roundOff;

	@Expose
	@SerializedName("totalInvValueInWords")
	@Column(name = "TOT_INV_VAL_WORLDS")
	protected String totalInvoiceValueInWords;

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
	protected String invoiceValueFc;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@Column(name = "TCS_FLAG_INCOME_TAX")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("glStateCessSpecific")
	@Column(name = "GL_STATE_CESS_SPECIFIC")
	protected String glStateCessSpecific;

	/*
	 * @Expose
	 * 
	 * @SerializedName("originalInvoiceNumber")
	 * 
	 * @Column(name = "ORG_INV_NUM") private String originalInvoiceNumber;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("originalInvoiceDate")
	 * 
	 * @Column(name = "ORG_INV_DATE") private String originalInvoiceDate;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("preceedingInvNo")
	 * 
	 * @Column(name = "PRECEEDING_INV_NUM") private String
	 * preceedingInvoiceNumber;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("preceedingInvDate")
	 * 
	 * @Column(name = "PRECEEDING_INV_DATE") private String
	 * preceedingInvoiceDate;
	 */

/*	@Expose
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
	protected String glCodeStateCess;*/

	/*
	 * @Expose
	 * 
	 * @SerializedName("profitCentre2")
	 * 
	 * @Column(name = "PROFIT_CENTRE2") protected String profitCentre2;
	 */
	@Expose
	@SerializedName("invRemarks")
	@Column(name = "INV_REMARKS")
	private String invoiceRemarks;

	@Expose
	@SerializedName("ecomTransactionID")
	@Column(name = "ECOM_TRANS_ID")
	protected String ecomTransactionID;

	@Expose
	@SerializedName("exchangeRt")
	@Column(name = "EXCHANGE_RATE")
	protected String exchangeRate;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("glPostingDate")
	@Column(name = "GL_POSTING_DATE")
	protected String glPostingDate;

	@Expose
	@SerializedName("salesOrderNo")
	@Column(name = "SALES_ORD_NUM")
	protected String salesOrderNumber;

	@Expose
	@SerializedName("custTan")
	@Column(name = "CUST_TAN")
	protected String customerTan;

	/*@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;
*/
	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

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

	/*@Expose
	@SerializedName("originalDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String origDocType;*/

	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected String isDeleted;

	@Expose
	@SerializedName("fiYear")
	@Column(name = "FI_YEAR")
	protected String finYear;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;
}
