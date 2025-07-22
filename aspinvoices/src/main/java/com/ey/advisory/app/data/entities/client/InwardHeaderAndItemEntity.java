package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@MappedSuperclass
@Data
public class InwardHeaderAndItemEntity {

	@Expose
	@SerializedName("taxScheme")
	@Column(name = "TAX_SCHEME")
	protected String taxScheme;

	@Expose
	@SerializedName("docCat")
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Expose
	@SerializedName("supTradeName")
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@SerializedName("custOrSupName")
	@Column(name = "CUST_SUPP_NAME")
	protected String supplierLegalName;

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
	@SerializedName("custLegalName")
	@Column(name = "CUST_LEGAL_NAME")
	protected String customerLegalName;

	@Expose
	@SerializedName("custBuildingNo")
	@Column(name = "CUST_BUILDING_NUM")
	protected String custBuidIngNo;

	@Expose
	@SerializedName("custBuildingName")
	@Column(name = "CUST_BUILDING_NAME")
	protected String custBuidIngName;

	@Expose
	@SerializedName("custLocation")
	@Column(name = "CUST_LOCATION")
	protected String supplierLocation;

	@Expose
	@SerializedName("custPincode")
	@Column(name = "CUST_PINCODE")
	protected String customerPincode;

	@Expose
	@SerializedName("billToState")
	@Column(name = "BILL_TO_STATE")
	protected String billToState;

	@Expose
	@SerializedName("custPhone")
	@Column(name = "CUST_PHONE")
	protected String customerPhone;

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
	@SerializedName("dispatcherPincode")
	@Column(name = "DISPATCHER_PINCODE")
	protected String dispatcherPincode;

	@Expose
	@SerializedName("dispatcherStateCode")
	@Column(name = "DISPATCHER_STATE_CODE")
	protected String dispatcherStateCode;

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
	@SerializedName("shipToState")
	@Column(name = "SHIP_TO_STATE")
	protected String shipToState;

	@Expose
	@SerializedName("serialNoII")
	@Column(name = "SERIAL_NUM2")
	protected String sNumber2;

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
	@SerializedName("orderLineRef")
	@Column(name = "ORDER_ITEM_REFERENCE")
	private String orderItemRef;

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
	@SerializedName("stateCessSpecificRt")
	@Column(name = "STATE_CESS_SPECIFIC_RATE")
	private String stateCessSpeRate;

	@Expose
	@SerializedName("stateCessSpecificAmt")
	@Column(name = "STATE_CESS_SPECIFIC_AMOUNT")
	private String stateCessSpeAmt;

	@Expose
	@SerializedName("totalItemAmt")
	@Column(name = "TOT_ITEM_AMT")
	private String totalItemAmount;

	@Expose
	@SerializedName("preTaxAmt")
	@Column(name = "PRE_TAX_AMOUNT")
	private String preTaxAmount;

	@Expose
	@SerializedName("roundOff")
	@Column(name = "ROUND_OFF")
	protected String roundOff;

	@Expose
	@SerializedName("totalInvValueInWords")
	@Column(name = "TOT_INV_VAL_WORDS")
	protected String totalInvoiceValueInWords;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@Column(name = "TCS_FLAG_INCOME_TAX")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("tcsRtIncomeTax")
	@Column(name = "TCS_RATE_INCOME_TAX")
	protected String tcsRateIncomeTax;

	@Expose
	@SerializedName("tcsAmtIncomeTax")
	@Column(name = "TCS_AMOUNT_INCOME_TAX")
	protected String tcsAmtIncomeTax;

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
	@SerializedName("invRemarks")
	@Column(name = "INV_REMARKS")
	private String invoiceRemarks;

	@Expose
	@SerializedName("invPeriodStartDate")
	@Column(name = "INV_PERIOD_START_DATE")
	protected String invoicePeriodStartDate;

	@Expose
	@SerializedName("invPeriodEndDate")
	@Column(name = "INV_PERIOD_END_DATE")
	protected String invoicePeriodEndDate;

	@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	private String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	private String origDocDate;

	@Expose
	@SerializedName("invRef")
	@Column(name = "INV_REFERENCE")
	protected String othRef;

	@Expose
	@SerializedName("receiptAdviceRef")
	@Column(name = "RECEIPT_ADVICE_REFERENCE")
	protected String receiptAdvRef;

	@Expose
	@SerializedName("receiptAdviceDate")
	@Column(name = "RECEIPT_ADVICE_DATE")
	protected String receiptAdvDate;

	@Expose
	@SerializedName("tenderRef")
	@Column(name = "TENDER_REFERENCE")
	protected String tenderRef;

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
	@SerializedName("paidAmt")
	@Column(name = "PAID_AMT")
	protected String paidAmount;

	@Expose
	@SerializedName("balanceAmt")
	@Column(name = "BAL_AMT")
	protected String balanceAmount;

	@Expose
	@SerializedName("accDetail")
	@Column(name = "ACCOUNT_DETAIL")
	protected String accountDetail;

	@Expose
	@SerializedName("supportingDocURL")
	@Column(name = "SUPPORTING_DOC_URL")
	private String supportingDocURL;

	@Expose
	@SerializedName("supportingDoc")
	@Column(name = "SUPPORTING_DOC")
	private String supportingDoc;

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
	@SerializedName("orgDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String orgDocType;

	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;

	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;

	@Expose
	@SerializedName("tcsIgstAmt")
	@Column(name = "TCS_IGST_AMT")
	protected String tcsIgstAmount;

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
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("srcIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	@Expose
	@SerializedName("srcFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUB_DIVISION")
	protected String subDivision;

	@Expose
	@SerializedName("glStateCessSpecific")
	@Column(name = "GL_STATE_CESS_SPECIFIC")
	protected String glCodeStateSpCess;

	@Expose
	@SerializedName("docRefNo")
	@Column(name = "DOCUMENT_REFERENCE_NUMBER")
	protected String docRefNumber;

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

	// Headers
	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected String irnDate;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;

	@Expose
	@SerializedName("reverseCharge")
	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;

	@Expose
	@SerializedName("suppGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

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
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected String invoiceStateSpecifAmount;

	@Expose
	@SerializedName("itcEntitlement")
	@Column(name = "ITC_ENTITLEMENT")
	protected String itcEntitlement;

	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;

	@Expose
	@SerializedName("billOfEntryNo")
	@Column(name = "BILL_OF_ENTRY")
	protected String billOfEntryNo;

	@Expose
	@SerializedName("billOfEntryDate")
	@Column(name = "BILL_OF_ENTRY_DATE")
	protected String billOfEntryDate;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected String paymentDueDate;

	@Expose
	@SerializedName("ecomGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	@Expose
	@SerializedName("originalSupplierGstin")
	@Column(name = "ORIG_SUPPLIER_GSTIN")
	protected String origSgstin;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;

	@Expose
	@SerializedName("custOrSupType")
	@Column(name = "CUST_SUPP_TYPE")
	private String supplierType;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("purchaseOrganization")
	@Column(name = "PURCHASE_ORGANIZATION")
	protected String purchaseOrganization;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected String postingDate;

	@Expose
	@SerializedName("ewbNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	@Expose
	@SerializedName("ewbDate")
	@Column(name = "EWAY_BILL_DATE")
	protected String eWayBillDate;

	@Expose
	@SerializedName("purchaseVoucherDate")
	@Column(name = "PURCHASE_VOUCHER_DATE")
	private String purchaseVoucherDate;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long acceptanceId;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	// Headers
	
	// Extra columns
	@Expose
	@SerializedName("adjustmentRefNo")
	@Column(name = "ADJ_REF_NO")
	private String adjustmentRefNo;

	@Expose
	@SerializedName("adjustmentRefDate")
	@Column(name = "ADJ_REF_DATE")
	private String adjustmentRefDate;

	@Expose
	@SerializedName("adjustedTaxableValue")
	@Column(name = "ADJ_TAXABLE_VALUE")
	private String adjustedTaxableValue;

	@Expose
	@SerializedName("adjustedIgstAmt")
	@Column(name = "ADJ_IGST_AMT")
	private String adjustedIgstAmt;

	@Expose
	@SerializedName("adjustedCgstAmt")
	@Column(name = "ADJ_CGST_AMT")
	private String adjustedCgstAmt;

	@Expose
	@SerializedName("adjustedSgstAmt")
	@Column(name = "ADJ_SGST_AMT")
	private String adjustedSgstAmt;

	@Expose
	@SerializedName("adjustedCessAmtAdvalorem")
	@Column(name = "ADJ_CESS_AMT_ADVALOREM")
	private String adjustedCessAmtAdvalorem;

	@Expose
	@SerializedName("adjustedCessAmtSpecific")
	@Column(name = "ADJ_CESS_AMT_SPECIFIC")
	private String adjustedCessAmtSpecific;

	@Expose
	@SerializedName("adjustedStateCessAmt")
	@Column(name = "ADJ_STATECESS_AMT")
	private String adjustedStateCessAmt;

	@Expose
	@SerializedName("paymentVoucherNumber")
	@Column(name = "PAYMENT_VOUCHER_NUM")
	private String paymentVoucherNumber;
	
	@Expose
	@SerializedName("paymentDate")
	@Column(name = "PAYMENT_DATE")
	private String paymentVoucherDate;
	
	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;
		
	// Etra columns

}
