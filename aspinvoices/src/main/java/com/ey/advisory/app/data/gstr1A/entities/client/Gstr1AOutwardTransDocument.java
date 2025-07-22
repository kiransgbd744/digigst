package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.TransDocument;
import com.ey.advisory.core.dto.EInvoiceMasterSaveDto;
import com.ey.advisory.core.dto.EwbMasterSaveDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * An Outward Document represents a concrete Financial Document like an Invoice
 * or a credit note.
 * 
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "ANX_OUTWARD_DOC_HEADER_1A")
public class Gstr1AOutwardTransDocument extends TransDocument {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_DOC_HEADER_SEQ_1A", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("ackNum")
	@Column(name = "ACK_NUM")
	private String ackNum;

	@Expose
	@SerializedName("ackDate")
	@Column(name = "ACK_DATE")
	protected LocalDateTime ackDate;

	@Expose
	@SerializedName("isEinvoice")
	@Column(name = "IS_E_INVOICE")
	private boolean isEinvoice;

	@Expose
	@SerializedName("isEwb")
	@Column(name = "IS_EWB")
	private boolean isEwb;

	@Expose
	@SerializedName("isCompliance")
	@Column(name = "IS_COMPLIANCE")
	private boolean isCompliance;

	@Expose
	@SerializedName("derivedSupplyType")
	@Column(name = "DERIVED_SUPPLY_TYPE")
	private String derivedSupplyType;

	@Expose
	@SerializedName("eInvStatus")
	@Column(name = "EINV_STATUS")
	private Integer eInvStatus;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("eInvErrorCode")
	@Column(name = "EINV_ERROR_CODE")
	private String eInvErrorCode;

	@Expose
	@SerializedName("eInvErrorDesc")
	@Column(name = "EINV_ERROR_DESC")
	private String eInvErrorDesc;

	@Expose
	@SerializedName("ewbStatus")
	@Column(name = "EWB_STATUS")
	private Integer ewbStatus;

	@Expose
	@SerializedName("irnStatus")
	@Column(name = "IRN_STATUS")
	private Integer irnStatus;

	@Expose
	@SerializedName("irnResponse")
	@Column(name = "IRN_RESPONSE")
	private String irnResponse;

	@Expose
	@SerializedName("ewbNoresp")
	@Column(name = "EWB_NO_RESP")
	private Long ewbNoresp;

	@Expose
	@SerializedName("ewbDateResp")
	@Column(name = "EWB_DATE_RESP")
	private LocalDateTime ewbDateResp;

	@Expose
	@SerializedName("ewbErrorCode")
	@Column(name = "EWB_ERROR_CODE")
	private String ewbErrorCode;

	@Expose
	@SerializedName("ewbErrorDesc")
	@Column(name = "EWB_ERROR_DESC")
	private String ewbErrorDesc;

	@Expose
	@SerializedName("ewbProcessingStatus")
	@Column(name = "EWB_PROCESSING_STATUS")
	private Integer ewbProcessingStatus;

	@Expose
	@SerializedName("aspInvoiceStatus")
	@Column(name = "ASP_INVOICE_STATUS")
	private Integer aspInvoiceStatus;

	@Expose
	@SerializedName("complianceApplicable")
	@Column(name = "COMPLIANCE_APPLICABLE")
	private boolean complianceApplicable;

	@Expose
	@SerializedName("einvApplicable")
	@Column(name = "EINV_APPL")
	private boolean einvApplicable;

	@Expose
	@SerializedName("ewbApplicable")
	@Column(name = "EWB_APPL")
	private boolean ewbApplicable;

	@Expose
	@SerializedName("infoErrorCode")
	@Column(name = "INFO_ERROR_CODE")
	private String infoErrorCode;

	@Expose
	@SerializedName("infoErrorMsg")
	@Column(name = "INFO_ERROR_MSG")
	private String infoErrorMsg;

	@Expose
	@SerializedName("salesOrg")
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrgnization;

	@Expose
	@SerializedName("distChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Expose
	@SerializedName("orgDocType")
	@Column(name = "ORIGINAL_DOC_TYPE")
	protected String origDocType;

	@Expose
	@SerializedName("orgCgstin")
	@Column(name = "ORIGINAL_CUST_GSTIN")
	protected String origCgstin;

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
	protected LocalDate shippingBillDate;

	@Expose
	@SerializedName("accVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected LocalDate accountingVoucherDate;

	/**
	 * Table type represents the table in the GSTR-1 form, where this document
	 * can be placed. E.g. 4A, 4B, 4C etc.
	 */
	@Expose
	@SerializedName("gstr1TableNo")
	@Column(name = "TABLE_SECTION")
	protected String tableType;

	/**
	 * This field represents the classification of invoices according to return
	 * filing rules by GSTN. Some of the possible values are B2B, B2CL, B2CS,
	 * EXP etc.
	 * 
	 */
	@Expose
	@SerializedName("gstr1SubCategory")
	@Column(name = "TAX_DOC_TYPE")
	protected String gstnBifurcation;

	@Expose
	@SerializedName("an1Gstr1TableNo")
	@Column(name = "AN_TABLE_SECTION")
	protected String tableTypeNew;

	@Expose
	@SerializedName("an1Gstr1SubCategory")
	@Column(name = "AN_TAX_DOC_TYPE")
	protected String gstnBifurcationNew;

	@Expose
	@SerializedName("returnType")
	@Column(name = "AN_RETURN_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("gstrReturnType")
	@Column(name = "RETURN_TYPE")
	protected String gstrReturnType;

	@Expose
	@SerializedName("erpBatchId")
	@Column(name = "ERP_BATCH_ID")
	protected Long erpBatchId;

	@Expose
	// @SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	// @SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	// @SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected BigDecimal cessAmountSpecific;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected BigDecimal cessAmountAdvalorem;

	@Column(name = "MEMO_VALUE_IGST")
	protected BigDecimal memoValIgst;

	@Column(name = "MEMO_VALUE_CGST")
	protected BigDecimal memoValCgst;

	@Column(name = "MEMO_VALUE_SGST")
	protected BigDecimal memoValSgst;

	@Column(name = "MEMO_VALUE_CESS")
	protected BigDecimal memoValCess;

	@Column(name = "IGST_RET1_IMPACT")
	protected BigDecimal ret1IgstImpact;

	@Column(name = "CGST_RET1_IMPACT")
	protected BigDecimal ret1CgstImpact;

	@Column(name = "SGST_RET1_IMPACT")
	protected BigDecimal ret1SgstImpact;

	@Column(name = "DERIVED_SGSTIN_PAN")
	protected String derivedSgstinPan;

	@Column(name = "DERIVED_CGSTIN_PAN")
	protected String derivedCgstinPan;

	@Expose
	@SerializedName("taxScheme")
	@Column(name = "TAX_SCHEME")
	protected String taxScheme;

	@Expose
	@SerializedName("docCat")
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

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
	protected Integer supplierPincode;

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
	protected Integer customerPincode;

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
	protected Integer dispatcherPincode;

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
	protected Integer shipToPincode;

	@Expose
	@SerializedName("roundOff")
	@Column(name = "ROUND_OFF")
	protected BigDecimal roundOff;

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
	protected Integer creditDays;

	@Expose
	@SerializedName("accDetail")
	@Column(name = "ACCOUNT_DETAIL")
	protected String accountDetail;

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
	@SerializedName("distance")
	@Column(name = "DISTANCE")
	protected BigDecimal distance;

	@Expose
	@SerializedName("vehicleNo")
	@Column(name = "VEHICLE_NUM")
	protected String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@SerializedName("exchangeRt")
	@Column(name = "EXCHANGE_RATE")
	protected String exchangeRate;

	@Expose
	@SerializedName("glPostingDate")
	@Column(name = "GL_POSTING_DATE")
	protected LocalDate glPostingDate;

	@Expose
	@SerializedName("salesOrderNo")
	@Column(name = "SALES_ORD_NUM")
	protected String salesOrderNumber;

	@Expose
	@SerializedName("custTan")
	@Column(name = "CUST_TAN")
	protected String customerTan;

	@Expose
	@SerializedName("canReason")
	@Column(name = "CANCEL_REASON")
	protected String cancellationReason;

	@Expose
	@SerializedName("canRemarks")
	@Column(name = "CANCEL_REMARKS")
	protected String cancellationRemarks;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@Column(name = "TCS_FLAG_INCOME_TAX")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("custPANOrAadhaar")
	@Column(name = "CUSTOMER_PAN_OR_AADHAAR")
	protected String customerPANOrAadhaar;

	@Expose
	@SerializedName("glStateCessSpecific")
	@Column(name = "GL_STATE_CESS_SPECIFIC")
	protected String glStateCessSpecific;

	@Expose
	@SerializedName("originalInvoiceNumber")
	@Column(name = "ORG_INV_NUM")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@Column(name = "ORG_INV_DATE")
	private LocalDate originalInvoiceDate;

	@Expose
	@SerializedName("preceedingInvNo")
	@Column(name = "PRECEEDING_INV_NUM")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@Column(name = "PRECEEDING_INV_DATE")
	private LocalDate preceedingInvoiceDate;

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
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("invRemarks")
	@Column(name = "INV_REMARKS")
	private String invoiceRemarks;

	@Expose
	@SerializedName("ecomTransactionID")
	@Column(name = "ECOM_TRANS_ID")
	protected String ecomTransactionID;

	@Expose
	@SerializedName("stateCessSpecificAmt")
	@Column(name = "STATE_CESS_SPECIFIC_AMOUNT")
	protected BigDecimal stateCessSpecificAmt;

	@Column(name = "PRECEEDING_INV_AMT")
	protected BigDecimal preceedingInvAmt;

	@Column(name = "PRECEEDING_TAXABLE_VALUE")
	protected BigDecimal preceedingTaxableVal;

	@Expose
	@SerializedName("udf29")
	@Column(name = "USERDEFINED_FIELD29")
	protected String userDefinedField29;

	@Expose
	@SerializedName("udf30")
	@Column(name = "USERDEFINED_FIELD30")
	protected String userDefinedField30;

	@Expose
	@SerializedName("einvGstnSaveStatus")
	@Column(name = "EINV_GSTN_SAVE_STATUS")
	protected String einvGstnSaveStatus;

	@Expose
	@SerializedName("reconResponseId")
	@Column(name = "RECON_RESPONSE_ID")
	protected Long reconResponseId;

	@Expose
	@SerializedName("reconResponseSource")
	@Column(name = "RECON_RESPONSE_SOURCE")
	protected String reconResponseSource;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected LocalDateTime irnDate;

	@Expose
	@SerializedName("isMultiSuppInv")
	@Column(name = "IS_MULTI_SUPP_INV")
	protected boolean isMultiSuppInv;

	@Transient
	protected boolean isCgstInMasterCust;

	// These columns are not part of header tables, fetching from ewbmaster to
	// display in invoice management
	@Expose
	@SerializedName("computedDistance")
	@Transient
	protected Integer computedDistance;

	@Expose
	@SerializedName("remainingDistance")
	@Transient
	protected Integer remainingDistance;

	@Expose
	@SerializedName("fileName")
	@Transient
	protected String fileName;

	@Expose
	@SerializedName("validUpto")
	@Transient
	protected LocalDateTime validUpto;

	@Expose
	@SerializedName("reasonCode")
	@Transient
	protected String reasonCode;

	@Expose
	@SerializedName("fromPlace")
	@Transient
	protected String fromPlace;

	@Expose
	@SerializedName("toPlace")
	@Transient
	protected String toPlace;

	@Expose
	@SerializedName("refId")
	@Transient
	protected String refId;

	@Expose
	@SerializedName("ewbPartBUpdated")
	@Transient
	protected LocalDateTime ewbPartBUpdated;

	@Transient
	private String optedForEwb;

	@Transient
	private String optedForEinv;

	@Transient
	private Integer einvJob;

	@Transient
	private Integer ewbJob;

	@Transient
	private String uqcNotFoundInMaster;

	@Transient
	protected Map<String, List<Pair<Integer, BigDecimal>>> masterProductMap;

	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr1AOutwardTransDocLineItem> lineItems = new ArrayList<>();

	@Expose
	@SerializedName("preDocDtls")
	@OneToMany(mappedBy = "preDocDetails")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr1APreceedingDocDetails> preDocDtls = new ArrayList<>();

	@Expose
	@SerializedName("contrDtls")
	@OneToMany(mappedBy = "contractDetails")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr1AContractDetails> contrDtls = new ArrayList<>();

	@Expose
	@SerializedName("addlDocDtls")
	@OneToMany(mappedBy = "addtitionalDocDetails")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr1AAdditionalDocDetails> addlDocDtls = new ArrayList<>();

	@Expose
	@Transient
	@SerializedName("eInvDtls")
	protected EInvoiceMasterSaveDto eInvDetails;

	@Expose
	@Transient
	@SerializedName("ewbDtls")
	protected EwbMasterSaveDto ewbDetails;

	@Column(name = "GSTN_SAVE_REF_ID")
	private String gstnSaveRefId;

	public boolean isMultiSuppInv() {
		return isMultiSuppInv;
	}

	public void setMultiSuppInv(boolean isMultiSuppInv) {
		this.isMultiSuppInv = isMultiSuppInv;
	}

	public String getGstnSaveRefId() {
		return gstnSaveRefId;
	}

	public void setGstnSaveRefId(String gstnSaveRefId) {
		this.gstnSaveRefId = gstnSaveRefId;
	}

	public boolean isEinvApplicable() {
		return einvApplicable;
	}

	public void setEinvApplicable(boolean einvApplicable) {
		this.einvApplicable = einvApplicable;
	}

	public boolean isEwbApplicable() {
		return ewbApplicable;
	}

	public void setEwbApplicable(boolean ewbApplicable) {
		this.ewbApplicable = ewbApplicable;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAckNum() {
		return ackNum;
	}

	public void setAckNum(String ackNum) {
		this.ackNum = ackNum;
	}

	/**
	 * @return the isEinvoice
	 */
	public boolean getIsEinvoice() {
		return isEinvoice;
	}

	/**
	 * @param isEinvoice
	 *            the isEinvoice to set
	 */
	public void setIsEinvoice(boolean isEinvoice) {
		this.isEinvoice = isEinvoice;
	}

	/**
	 * @return the eInvStatus
	 */
	public Integer geteInvStatus() {
		return eInvStatus;
	}

	/**
	 * @param eInvStatus
	 *            the eInvStatus to set
	 */
	public void seteInvStatus(Integer eInvStatus) {
		this.eInvStatus = eInvStatus;
	}

	public Integer getIrnStatus() {
		return irnStatus;
	}

	public void setIrnStatus(Integer irnStatus) {
		this.irnStatus = irnStatus;
	}

	public String getIrnResponse() {
		return irnResponse;
	}

	public void setIrnResponse(String irnResponse) {
		this.irnResponse = irnResponse;
	}

	public Long getEwbNoresp() {
		return ewbNoresp;
	}

	public void setEwbNoresp(Long ewbNoresp) {
		this.ewbNoresp = ewbNoresp;
	}

	public LocalDateTime getEwbDateResp() {
		return ewbDateResp;
	}

	public void setEwbDateResp(LocalDateTime ewbDateResp) {
		this.ewbDateResp = ewbDateResp;
	}

	/**
	 * @return the eInvErrorCode
	 */
	public String geteInvErrorCode() {
		return eInvErrorCode;
	}

	/**
	 * @param eInvErrorCode
	 *            the eInvErrorCode to set
	 */
	public void seteInvErrorCode(String eInvErrorCode) {
		this.eInvErrorCode = eInvErrorCode;
	}

	/**
	 * @return the eInvErrorDesc
	 */
	public String geteInvErrorDesc() {
		return eInvErrorDesc;
	}

	/**
	 * @param eInvErrorDesc
	 *            the eInvErrorDesc to set
	 */
	public void seteInvErrorDesc(String eInvErrorDesc) {
		this.eInvErrorDesc = eInvErrorDesc;
	}

	/**
	 * @return the ewbStatus
	 */
	public Integer getEwbStatus() {
		return ewbStatus;
	}

	/**
	 * @param ewbStatus
	 *            the ewbStatus to set
	 */
	public void setEwbStatus(Integer ewbStatus) {
		this.ewbStatus = ewbStatus;
	}

	/**
	 * @return the salesOrgnization
	 */
	public String getSalesOrgnization() {
		return salesOrgnization;
	}

	/**
	 * @param salesOrgnization
	 *            the salesOrgnization to set
	 */
	public void setSalesOrgnization(String salesOrgnization) {
		this.salesOrgnization = salesOrgnization;
	}

	/**
	 * @return the distributionChannel
	 */
	public String getDistributionChannel() {
		return distributionChannel;
	}

	/**
	 * @param distributionChannel
	 *            the distributionChannel to set
	 */
	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
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
	 * @return the origCgstin
	 */
	public String getOrigCgstin() {
		return origCgstin;
	}

	/**
	 * @param origCgstin
	 *            the origCgstin to set
	 */
	public void setOrigCgstin(String origCgstin) {
		this.origCgstin = origCgstin;
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

	/**
	 * @return the shippingBillNo
	 */
	public String getShippingBillNo() {
		return shippingBillNo;
	}

	/**
	 * @param shippingBillNo
	 *            the shippingBillNo to set
	 */
	public void setShippingBillNo(String shippingBillNo) {
		this.shippingBillNo = shippingBillNo;
	}

	/**
	 * @return the shippingBillDate
	 */
	public LocalDate getShippingBillDate() {
		return shippingBillDate;
	}

	/**
	 * @param shippingBillDate
	 *            the shippingBillDate to set
	 */
	public void setShippingBillDate(LocalDate shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}

	/**
	 * @return the accountingVoucherNumber
	 */
	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	/**
	 * @param accountingVoucherNumber
	 *            the accountingVoucherNumber to set
	 */
	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	/**
	 * @return the accountingVoucherDate
	 */
	public LocalDate getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	/**
	 * @param accountingVoucherDate
	 *            the accountingVoucherDate to set
	 */
	public void setAccountingVoucherDate(LocalDate accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	/**
	 * @return the tableType
	 */
	public String getTableType() {
		return tableType;
	}

	/**
	 * @param tableType
	 *            the tableType to set
	 */
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	/**
	 * @return the gstnBifurcation
	 */
	public String getGstnBifurcation() {
		return gstnBifurcation;
	}

	/**
	 * @param gstnBifurcation
	 *            the gstnBifurcation to set
	 */
	public void setGstnBifurcation(String gstnBifurcation) {
		this.gstnBifurcation = gstnBifurcation;
	}

	/**
	 * @return the tableTypeNew
	 */
	public String getTableTypeNew() {
		return tableTypeNew;
	}

	/**
	 * @param tableTypeNew
	 *            the tableTypeNew to set
	 */
	public void setTableTypeNew(String tableTypeNew) {
		this.tableTypeNew = tableTypeNew;
	}

	/**
	 * @return the gstnBifurcationNew
	 */
	public String getGstnBifurcationNew() {
		return gstnBifurcationNew;
	}

	/**
	 * @param gstnBifurcationNew
	 *            the gstnBifurcationNew to set
	 */
	public void setGstnBifurcationNew(String gstnBifurcationNew) {
		this.gstnBifurcationNew = gstnBifurcationNew;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the gstrReturnType
	 */
	public String getGstrReturnType() {
		return gstrReturnType;
	}

	/**
	 * @param gstrReturnType
	 *            the gstrReturnType to set
	 */
	public void setGstrReturnType(String gstrReturnType) {
		this.gstrReturnType = gstrReturnType;
	}

	/**
	 * @return the erpBatchId
	 */
	public Long getErpBatchId() {
		return erpBatchId;
	}

	/**
	 * @param erpBatchId
	 *            the erpBatchId to set
	 */
	public void setErpBatchId(Long erpBatchId) {
		this.erpBatchId = erpBatchId;
	}

	/**
	 * @return the igstAmount
	 */
	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	/**
	 * @param igstAmount
	 *            the igstAmount to set
	 */
	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	/**
	 * @return the cgstAmount
	 */
	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	/**
	 * @param cgstAmount
	 *            the cgstAmount to set
	 */
	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	/**
	 * @return the sgstAmount
	 */
	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	/**
	 * @param sgstAmount
	 *            the sgstAmount to set
	 */
	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	/**
	 * @return the cessAmountSpecific
	 */
	public BigDecimal getCessAmountSpecific() {
		return cessAmountSpecific;
	}

	/**
	 * @param cessAmountSpecific
	 *            the cessAmountSpecific to set
	 */
	public void setCessAmountSpecific(BigDecimal cessAmountSpecific) {
		this.cessAmountSpecific = cessAmountSpecific;
	}

	/**
	 * @return the cessAmountAdvalorem
	 */
	public BigDecimal getCessAmountAdvalorem() {
		return cessAmountAdvalorem;
	}

	/**
	 * @param cessAmountAdvalorem
	 *            the cessAmountAdvalorem to set
	 */
	public void setCessAmountAdvalorem(BigDecimal cessAmountAdvalorem) {
		this.cessAmountAdvalorem = cessAmountAdvalorem;
	}

	/**
	 * @return the memoValIgst
	 */
	public BigDecimal getMemoValIgst() {
		return memoValIgst;
	}

	/**
	 * @param memoValIgst
	 *            the memoValIgst to set
	 */
	public void setMemoValIgst(BigDecimal memoValIgst) {
		this.memoValIgst = memoValIgst;
	}

	/**
	 * @return the memoValCgst
	 */
	public BigDecimal getMemoValCgst() {
		return memoValCgst;
	}

	/**
	 * @param memoValCgst
	 *            the memoValCgst to set
	 */
	public void setMemoValCgst(BigDecimal memoValCgst) {
		this.memoValCgst = memoValCgst;
	}

	/**
	 * @return the memoValSgst
	 */
	public BigDecimal getMemoValSgst() {
		return memoValSgst;
	}

	/**
	 * @param memoValSgst
	 *            the memoValSgst to set
	 */
	public void setMemoValSgst(BigDecimal memoValSgst) {
		this.memoValSgst = memoValSgst;
	}

	/**
	 * @return the memoValCess
	 */
	public BigDecimal getMemoValCess() {
		return memoValCess;
	}

	/**
	 * @param memoValCess
	 *            the memoValCess to set
	 */
	public void setMemoValCess(BigDecimal memoValCess) {
		this.memoValCess = memoValCess;
	}

	/**
	 * @return the ret1IgstImpact
	 */
	public BigDecimal getRet1IgstImpact() {
		return ret1IgstImpact;
	}

	/**
	 * @param ret1IgstImpact
	 *            the ret1IgstImpact to set
	 */
	public void setRet1IgstImpact(BigDecimal ret1IgstImpact) {
		this.ret1IgstImpact = ret1IgstImpact;
	}

	/**
	 * @return the ret1CgstImpact
	 */
	public BigDecimal getRet1CgstImpact() {
		return ret1CgstImpact;
	}

	/**
	 * @param ret1CgstImpact
	 *            the ret1CgstImpact to set
	 */
	public void setRet1CgstImpact(BigDecimal ret1CgstImpact) {
		this.ret1CgstImpact = ret1CgstImpact;
	}

	/**
	 * @return the ret1SgstImpact
	 */
	public BigDecimal getRet1SgstImpact() {
		return ret1SgstImpact;
	}

	/**
	 * @param ret1SgstImpact
	 *            the ret1SgstImpact to set
	 */
	public void setRet1SgstImpact(BigDecimal ret1SgstImpact) {
		this.ret1SgstImpact = ret1SgstImpact;
	}

	/**
	 * @return the derivedSgstinPan
	 */
	public String getDerivedSgstinPan() {
		return derivedSgstinPan;
	}

	/**
	 * @param derivedSgstinPan
	 *            the derivedSgstinPan to set
	 */
	public void setDerivedSgstinPan(String derivedSgstinPan) {
		this.derivedSgstinPan = derivedSgstinPan;
	}

	/**
	 * @return the derivedCgstinPan
	 */
	public String getDerivedCgstinPan() {
		return derivedCgstinPan;
	}

	/**
	 * @param derivedCgstinPan
	 *            the derivedCgstinPan to set
	 */
	public void setDerivedCgstinPan(String derivedCgstinPan) {
		this.derivedCgstinPan = derivedCgstinPan;
	}

	/**
	 * @return the taxScheme
	 */
	public String getTaxScheme() {
		return taxScheme;
	}

	/**
	 * @param taxScheme
	 *            the taxScheme to set
	 */
	public void setTaxScheme(String taxScheme) {
		this.taxScheme = taxScheme;
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
	 * @return the supplierLegalName
	 */
	public String getSupplierLegalName() {
		return supplierLegalName;
	}

	/**
	 * @param supplierLegalName
	 *            the supplierLegalName to set
	 */
	public void setSupplierLegalName(String supplierLegalName) {
		this.supplierLegalName = supplierLegalName;
	}

	/**
	 * @return the supplierBuildingNumber
	 */
	public String getSupplierBuildingNumber() {
		return supplierBuildingNumber;
	}

	/**
	 * @param supplierBuildingNumber
	 *            the supplierBuildingNumber to set
	 */
	public void setSupplierBuildingNumber(String supplierBuildingNumber) {
		this.supplierBuildingNumber = supplierBuildingNumber;
	}

	/**
	 * @return the supplierBuildingName
	 */
	public String getSupplierBuildingName() {
		return supplierBuildingName;
	}

	/**
	 * @param supplierBuildingName
	 *            the supplierBuildingName to set
	 */
	public void setSupplierBuildingName(String supplierBuildingName) {
		this.supplierBuildingName = supplierBuildingName;
	}

	/**
	 * @return the supplierLocation
	 */
	public String getSupplierLocation() {
		return supplierLocation;
	}

	/**
	 * @param supplierLocation
	 *            the supplierLocation to set
	 */
	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

	/**
	 * @return the supplierPincode
	 */
	public Integer getSupplierPincode() {
		return supplierPincode;
	}

	/**
	 * @param supplierPincode
	 *            the supplierPincode to set
	 */
	public void setSupplierPincode(Integer supplierPincode) {
		this.supplierPincode = supplierPincode;
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
	 * @return the supplierPhone
	 */
	public String getSupplierPhone() {
		return supplierPhone;
	}

	/**
	 * @param supplierPhone
	 *            the supplierPhone to set
	 */
	public void setSupplierPhone(String supplierPhone) {
		this.supplierPhone = supplierPhone;
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
	 * @return the customerTradeName
	 */
	public String getCustomerTradeName() {
		return customerTradeName;
	}

	/**
	 * @param customerTradeName
	 *            the customerTradeName to set
	 */
	public void setCustomerTradeName(String customerTradeName) {
		this.customerTradeName = customerTradeName;
	}

	/**
	 * @return the customerPincode
	 */
	public Integer getCustomerPincode() {
		return customerPincode;
	}

	/**
	 * @param customerPincode
	 *            the customerPincode to set
	 */
	public void setCustomerPincode(Integer customerPincode) {
		this.customerPincode = customerPincode;
	}

	/**
	 * @return the customerPhone
	 */
	public String getCustomerPhone() {
		return customerPhone;
	}

	/**
	 * @param customerPhone
	 *            the customerPhone to set
	 */
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
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
	 * @return the dispatcherPincode
	 */
	public Integer getDispatcherPincode() {
		return dispatcherPincode;
	}

	/**
	 * @param dispatcherPincode
	 *            the dispatcherPincode to set
	 */
	public void setDispatcherPincode(Integer dispatcherPincode) {
		this.dispatcherPincode = dispatcherPincode;
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
	 * @return the shipToTradeName
	 */
	public String getShipToTradeName() {
		return shipToTradeName;
	}

	/**
	 * @param shipToTradeName
	 *            the shipToTradeName to set
	 */
	public void setShipToTradeName(String shipToTradeName) {
		this.shipToTradeName = shipToTradeName;
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
	 * @return the shipToPincode
	 */
	public Integer getShipToPincode() {
		return shipToPincode;
	}

	/**
	 * @param shipToPincode
	 *            the shipToPincode to set
	 */
	public void setShipToPincode(Integer shipToPincode) {
		this.shipToPincode = shipToPincode;
	}

	/**
	 * @return the roundOff
	 */
	public BigDecimal getRoundOff() {
		return roundOff;
	}

	/**
	 * @param roundOff
	 *            the roundOff to set
	 */
	public void setRoundOff(BigDecimal roundOff) {
		this.roundOff = roundOff;
	}

	/**
	 * @return the totalInvoiceValueInWords
	 */
	public String getTotalInvoiceValueInWords() {
		return totalInvoiceValueInWords;
	}

	/**
	 * @param totalInvoiceValueInWords
	 *            the totalInvoiceValueInWords to set
	 */
	public void setTotalInvoiceValueInWords(String totalInvoiceValueInWords) {
		this.totalInvoiceValueInWords = totalInvoiceValueInWords;
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
	 * @return the payeeName
	 */
	public String getPayeeName() {
		return payeeName;
	}

	/**
	 * @param payeeName
	 *            the payeeName to set
	 */
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	/**
	 * @return the modeOfPayment
	 */
	public String getModeOfPayment() {
		return modeOfPayment;
	}

	/**
	 * @param modeOfPayment
	 *            the modeOfPayment to set
	 */
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	/**
	 * @return the branchOrIfscCode
	 */
	public String getBranchOrIfscCode() {
		return branchOrIfscCode;
	}

	/**
	 * @param branchOrIfscCode
	 *            the branchOrIfscCode to set
	 */
	public void setBranchOrIfscCode(String branchOrIfscCode) {
		this.branchOrIfscCode = branchOrIfscCode;
	}

	/**
	 * @return the paymentTerms
	 */
	public String getPaymentTerms() {
		return paymentTerms;
	}

	/**
	 * @param paymentTerms
	 *            the paymentTerms to set
	 */
	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/**
	 * @return the paymentInstruction
	 */
	public String getPaymentInstruction() {
		return paymentInstruction;
	}

	/**
	 * @param paymentInstruction
	 *            the paymentInstruction to set
	 */
	public void setPaymentInstruction(String paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	/**
	 * @return the creditTransfer
	 */
	public String getCreditTransfer() {
		return creditTransfer;
	}

	/**
	 * @param creditTransfer
	 *            the creditTransfer to set
	 */
	public void setCreditTransfer(String creditTransfer) {
		this.creditTransfer = creditTransfer;
	}

	/**
	 * @return the directDebit
	 */
	public String getDirectDebit() {
		return directDebit;
	}

	/**
	 * @param directDebit
	 *            the directDebit to set
	 */
	public void setDirectDebit(String directDebit) {
		this.directDebit = directDebit;
	}

	/**
	 * @return the creditDays
	 */
	public Integer getCreditDays() {
		return creditDays;
	}

	/**
	 * @param creditDays
	 *            the creditDays to set
	 */
	public void setCreditDays(Integer creditDays) {
		this.creditDays = creditDays;
	}

	/**
	 * @return the accountDetail
	 */
	public String getAccountDetail() {
		return accountDetail;
	}

	/**
	 * @param accountDetail
	 *            the accountDetail to set
	 */
	public void setAccountDetail(String accountDetail) {
		this.accountDetail = accountDetail;
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
	 * @return the distance
	 */
	public BigDecimal getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
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
	 * @return the exchangeRate
	 */
	public String getExchangeRate() {
		return exchangeRate;
	}

	/**
	 * @param exchangeRate
	 *            the exchangeRate to set
	 */
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	/**
	 * @return the glPostingDate
	 */
	public LocalDate getGlPostingDate() {
		return glPostingDate;
	}

	/**
	 * @param glPostingDate
	 *            the glPostingDate to set
	 */
	public void setGlPostingDate(LocalDate glPostingDate) {
		this.glPostingDate = glPostingDate;
	}

	/**
	 * @return the salesOrderNumber
	 */
	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	/**
	 * @param salesOrderNumber
	 *            the salesOrderNumber to set
	 */
	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	/**
	 * @return the customerTan
	 */
	public String getCustomerTan() {
		return customerTan;
	}

	/**
	 * @param customerTan
	 *            the customerTan to set
	 */
	public void setCustomerTan(String customerTan) {
		this.customerTan = customerTan;
	}

	/**
	 * @return the cancellationReason
	 */
	public String getCancellationReason() {
		return cancellationReason;
	}

	/**
	 * @param cancellationReason
	 *            the cancellationReason to set
	 */
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	/**
	 * @return the cancellationRemarks
	 */
	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	/**
	 * @param cancellationRemarks
	 *            the cancellationRemarks to set
	 */
	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	/**
	 * @return the tcsFlagIncomeTax
	 */
	public String getTcsFlagIncomeTax() {
		return tcsFlagIncomeTax;
	}

	/**
	 * @param tcsFlagIncomeTax
	 *            the tcsFlagIncomeTax to set
	 */
	public void setTcsFlagIncomeTax(String tcsFlagIncomeTax) {
		this.tcsFlagIncomeTax = tcsFlagIncomeTax;
	}

	/**
	 * @return the customerPANOrAadhaar
	 */
	public String getCustomerPANOrAadhaar() {
		return customerPANOrAadhaar;
	}

	/**
	 * @param customerPANOrAadhaar
	 *            the customerPANOrAadhaar to set
	 */
	public void setCustomerPANOrAadhaar(String customerPANOrAadhaar) {
		this.customerPANOrAadhaar = customerPANOrAadhaar;
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
	 * @return the originalInvoiceNumber
	 */
	public String getOriginalInvoiceNumber() {
		return originalInvoiceNumber;
	}

	/**
	 * @param originalInvoiceNumber
	 *            the originalInvoiceNumber to set
	 */
	public void setOriginalInvoiceNumber(String originalInvoiceNumber) {
		this.originalInvoiceNumber = originalInvoiceNumber;
	}

	/**
	 * @return the originalInvoiceDate
	 */
	public LocalDate getOriginalInvoiceDate() {
		return originalInvoiceDate;
	}

	/**
	 * @param originalInvoiceDate
	 *            the originalInvoiceDate to set
	 */
	public void setOriginalInvoiceDate(LocalDate originalInvoiceDate) {
		this.originalInvoiceDate = originalInvoiceDate;
	}

	/**
	 * @return the preceedingInvoiceNumber
	 */
	public String getPreceedingInvoiceNumber() {
		return preceedingInvoiceNumber;
	}

	/**
	 * @param preceedingInvoiceNumber
	 *            the preceedingInvoiceNumber to set
	 */
	public void setPreceedingInvoiceNumber(String preceedingInvoiceNumber) {
		this.preceedingInvoiceNumber = preceedingInvoiceNumber;
	}

	/**
	 * @return the preceedingInvoiceDate
	 */
	public LocalDate getPreceedingInvoiceDate() {
		return preceedingInvoiceDate;
	}

	/**
	 * @param preceedingInvoiceDate
	 *            the preceedingInvoiceDate to set
	 */
	public void setPreceedingInvoiceDate(LocalDate preceedingInvoiceDate) {
		this.preceedingInvoiceDate = preceedingInvoiceDate;
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
	 * @return the isCgstInMasterCust
	 */
	public boolean getIsCgstInMasterCust() {
		return isCgstInMasterCust;
	}

	/**
	 * @param isCgstInMasterCust
	 *            the isCgstInMasterCust to set
	 */
	public void setIsCgstInMasterCust(boolean isCgstInMasterCust) {
		this.isCgstInMasterCust = isCgstInMasterCust;
	}

	/**
	 * @param isCgstInMasterCust
	 *            the isCgstInMasterCust to set
	 */
	public void setCgstInMasterCust(boolean isCgstInMasterCust) {
		this.isCgstInMasterCust = isCgstInMasterCust;
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
	 * @return the isEwb
	 */
	public boolean getIsEwb() {
		return isEwb;
	}

	/**
	 * @param isEwb
	 *            the isEwb to set
	 */
	public void setIsEwb(boolean isEwb) {
		this.isEwb = isEwb;
	}

	/**
	 * @return the isCompliance
	 */
	public boolean getIsCompliance() {
		return isCompliance;
	}

	/**
	 * @param isCompliance
	 *            the isCompliance to set
	 */
	public void setIsCompliance(boolean isCompliance) {
		this.isCompliance = isCompliance;
	}

	/**
	 * @return the derivedSupplyType
	 */
	public String getDerivedSupplyType() {
		return derivedSupplyType;
	}

	/**
	 * @param derivedSupplyType
	 *            the derivedSupplyType to set
	 */
	public void setDerivedSupplyType(String derivedSupplyType) {
		this.derivedSupplyType = derivedSupplyType;
	}

	/**
	 * @return the invoiceRemarks
	 */
	public String getInvoiceRemarks() {
		return invoiceRemarks;
	}

	/**
	 * @param invoiceRemarks
	 *            the invoiceRemarks to set
	 */
	public void setInvoiceRemarks(String invoiceRemarks) {
		this.invoiceRemarks = invoiceRemarks;
	}

	/**
	 * @return the ecomTransactionID
	 */
	public String getEcomTransactionID() {
		return ecomTransactionID;
	}

	/**
	 * @param ecomTransactionID
	 *            the ecomTransactionID to set
	 */
	public void setEcomTransactionID(String ecomTransactionID) {
		this.ecomTransactionID = ecomTransactionID;
	}

	/**
	 * @return the ewbProcessingStatus
	 */
	public Integer getEwbProcessingStatus() {
		return ewbProcessingStatus;
	}

	/**
	 * @param ewbProcessingStatus
	 *            the ewbProcessingStatus to set
	 */
	public void setEwbProcessingStatus(Integer ewbProcessingStatus) {
		this.ewbProcessingStatus = ewbProcessingStatus;
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
	 * @return the aspInvoiceStatus
	 */
	public Integer getAspInvoiceStatus() {
		return aspInvoiceStatus;
	}

	/**
	 * @param aspInvoiceStatus
	 *            the aspInvoiceStatus to set
	 */
	public void setAspInvoiceStatus(Integer aspInvoiceStatus) {
		this.aspInvoiceStatus = aspInvoiceStatus;
	}

	/**
	 * @return the cmplianceApplicable
	 */
	public boolean getComplianceApplicable() {
		return complianceApplicable;
	}

	/**
	 * @param cmplianceApplicable
	 *            the cmplianceApplicable to set
	 */
	public void setCmplianceApplicable(boolean complianceApplicable) {
		this.complianceApplicable = complianceApplicable;
	}

	/**
	 * @return the masterProductMap
	 */
	public Map<String, List<Pair<Integer, BigDecimal>>> getMasterProductMap() {
		return masterProductMap;
	}

	/**
	 * @param masterProductMap
	 *            the masterProductMap to set
	 */
	public void setMasterProductMap(
			Map<String, List<Pair<Integer, BigDecimal>>> masterProductMap) {
		this.masterProductMap = masterProductMap;
	}

	/**
	 * @return the lineItems
	 */
	public List<Gstr1AOutwardTransDocLineItem> getLineItems() {
		return lineItems;
	}

	/**
	 * @param lineItems
	 *            the lineItems to set
	 */
	public void setLineItems(List<Gstr1AOutwardTransDocLineItem> lineItems) {
		this.lineItems = lineItems;
	}

	/**
	 * @return the errCodes
	 */
	public String getErrCodes() {
		return errCodes;
	}

	/**
	 * @param errCodes
	 *            the errCodes to set
	 */
	public void setErrCodes(String errCodes) {
		this.errCodes = errCodes;
	}

	/**
	 * @return the infoCodes
	 */
	public String getInfoCodes() {
		return infoCodes;
	}

	/**
	 * @param infoCodes
	 *            the infoCodes to set
	 */
	public void setInfoCodes(String infoCodes) {
		this.infoCodes = infoCodes;
	}

	/**
	 * @return the computedDistance
	 */
	public Integer getComputedDistance() {
		return computedDistance;
	}

	/**
	 * @param computedDistance
	 *            the computedDistance to set
	 */
	public void setComputedDistance(Integer computedDistance) {
		this.computedDistance = computedDistance;
	}

	/**
	 * @return the remainingDistance
	 */
	public Integer getRemainingDistance() {
		return remainingDistance;
	}

	/**
	 * @param remainingDistance
	 *            the remainingDistance to set
	 */
	public void setRemainingDistance(Integer remainingDistance) {
		this.remainingDistance = remainingDistance;
	}

	/**
	 * @return the preceedingInvAmt
	 */
	public BigDecimal getPreceedingInvAmt() {
		return preceedingInvAmt;
	}

	/**
	 * @param preceedingInvAmt
	 *            the preceedingInvAmt to set
	 */
	public void setPreceedingInvAmt(BigDecimal preceedingInvAmt) {
		this.preceedingInvAmt = preceedingInvAmt;
	}

	/**
	 * @return the preceedingTaxableVal
	 */
	public BigDecimal getPreceedingTaxableVal() {
		return preceedingTaxableVal;
	}

	/**
	 * @param preceedingTaxableVal
	 *            the preceedingTaxableVal to set
	 */
	public void setPreceedingTaxableVal(BigDecimal preceedingTaxableVal) {
		this.preceedingTaxableVal = preceedingTaxableVal;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the validUpto
	 */
	public LocalDateTime getValidUpto() {
		return validUpto;
	}

	/**
	 * @param validUpto
	 *            the validUpto to set
	 */
	public void setValidUpto(LocalDateTime validUpto) {
		this.validUpto = validUpto;
	}

	/**
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}

	/**
	 * @param reasonCode
	 *            the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * @return the fromPlace
	 */
	public String getFromPlace() {
		return fromPlace;
	}

	/**
	 * @param fromPlace
	 *            the fromPlace to set
	 */
	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId
	 *            the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	/**
	 * @return the ewbPartBUpdated
	 */
	public LocalDateTime getEwbPartBUpdated() {
		return ewbPartBUpdated;
	}

	/**
	 * @param ewbPartBUpdated
	 *            the ewbPartBUpdated to set
	 */
	public void setEwbPartBUpdated(LocalDateTime ewbPartBUpdated) {
		this.ewbPartBUpdated = ewbPartBUpdated;
	}

	/**
	 * @return the toPlace
	 */
	public String getToPlace() {
		return toPlace;
	}

	/**
	 * @param toPlace
	 *            the toPlace to set
	 */
	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
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
	 * @return the preDocDtls
	 */
	public List<Gstr1APreceedingDocDetails> getPreDocDtls() {
		return preDocDtls;
	}

	/**
	 * @param preDocDtls
	 *            the preDocDtls to set
	 */
	public void setPreDocDtls(List<Gstr1APreceedingDocDetails> preDocDtls) {
		this.preDocDtls = preDocDtls;
	}

	/**
	 * @return the contrDtls
	 */
	public List<Gstr1AContractDetails> getContrDtls() {
		return contrDtls;
	}

	/**
	 * @param contrDtls
	 *            the contrDtls to set
	 */
	public void setContrDtls(List<Gstr1AContractDetails> contrDtls) {
		this.contrDtls = contrDtls;
	}

	/**
	 * @return the addlDocDtls
	 */
	public List<Gstr1AAdditionalDocDetails> getAddlDocDtls() {
		return addlDocDtls;
	}

	/**
	 * @param addlDocDtls
	 *            the addlDocDtls to set
	 */
	public void setAddlDocDtls(List<Gstr1AAdditionalDocDetails> addlDocDtls) {
		this.addlDocDtls = addlDocDtls;
	}

	/**
	 * @return the optedForEwb
	 */
	public String getOptedForEwb() {
		return optedForEwb;
	}

	/**
	 * @param optedForEwb
	 *            the optedForEwb to set
	 */
	public void setOptedForEwb(String optedForEwb) {
		this.optedForEwb = optedForEwb;
	}

	/**
	 * @return the optedForEinv
	 */
	public String getOptedForEinv() {
		return optedForEinv;
	}

	/**
	 * @param optedForEinv
	 *            the optedForEinv to set
	 */
	public void setOptedForEinv(String optedForEinv) {
		this.optedForEinv = optedForEinv;
	}

	/**
	 * @return the einvJob
	 */
	public Integer getEinvJob() {
		return einvJob;
	}

	/**
	 * @param einvJob
	 *            the einvJob to set
	 */
	public void setEinvJob(Integer einvJob) {
		this.einvJob = einvJob;
	}

	/**
	 * @return the ewbJob
	 */
	public Integer getEwbJob() {
		return ewbJob;
	}

	/**
	 * @param ewbJob
	 *            the ewbJob to set
	 */
	public void setEwbJob(Integer ewbJob) {
		this.ewbJob = ewbJob;
	}

	/**
	 * @return the eInvDetails
	 */
	public EInvoiceMasterSaveDto geteInvDetails() {
		return eInvDetails;
	}

	/**
	 * @param eInvDetails
	 *            the eInvDetails to set
	 */
	public void seteInvDetails(EInvoiceMasterSaveDto eInvDetails) {
		this.eInvDetails = eInvDetails;
	}

	/**
	 * @return the ewbDetails
	 */
	public EwbMasterSaveDto getEwbDetails() {
		return ewbDetails;
	}

	/**
	 * @param ewbDetails
	 *            the ewbDetails to set
	 */
	public void setEwbDetails(EwbMasterSaveDto ewbDetails) {
		this.ewbDetails = ewbDetails;
	}

	/**
	 * @return the einvGstnSaveStatus
	 */
	public String getEinvGstnSaveStatus() {
		return einvGstnSaveStatus;
	}

	/**
	 * @param einvGstnSaveStatus
	 *            the einvGstnSaveStatus to set
	 */
	public void setEinvGstnSaveStatus(String einvGstnSaveStatus) {
		this.einvGstnSaveStatus = einvGstnSaveStatus;
	}

	/**
	 * @return the uqcNotFoundInMaster
	 */
	public String getUqcNotFoundInMaster() {
		return uqcNotFoundInMaster;
	}

	/**
	 * @param uqcNotFoundInMaster
	 *            the uqcNotFoundInMaster to set
	 */
	public void setUqcNotFoundInMaster(String uqcNotFoundInMaster) {
		this.uqcNotFoundInMaster = uqcNotFoundInMaster;
	}

	public LocalDateTime getIrnDate() {
		return irnDate;
	}

	public void setIrnDate(LocalDateTime irnDate) {
		this.irnDate = irnDate;
	}

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();

		if (itemIndex < lineItemCount && itemIndex >= 0) {
			Gstr1AOutwardTransDocLineItem lineItem = lineItems.get(itemIndex);
			Integer lineNo = 0;
			if (lineItem.getLineNo() != null) {
				lineNo = Integer.parseInt(lineItem.getLineNo());
			}
			return lineNo;
		}

		return null;
	}

	public LocalDateTime getAckDate() {
		return ackDate;
	}

	public void setAckDate(LocalDateTime ackDate) {
		this.ackDate = ackDate;
	}

	@Override
	public String toString() {
		return "OutwardTransDocument [id=" + id + ", ackNum=" + ackNum
				+ ", ackDate=" + ackDate + ", isEinvoice=" + isEinvoice
				+ ", isEwb=" + isEwb + ", isCompliance=" + isCompliance
				+ ", derivedSupplyType=" + derivedSupplyType + ", eInvStatus="
				+ eInvStatus + ", errCodes=" + errCodes + ", infoCodes="
				+ infoCodes + ", eInvErrorCode=" + eInvErrorCode
				+ ", eInvErrorDesc=" + eInvErrorDesc + ", ewbStatus="
				+ ewbStatus + ", irnStatus=" + irnStatus + ", irnResponse="
				+ irnResponse + ", ewbNoresp=" + ewbNoresp + ", ewbDateResp="
				+ ewbDateResp + ", ewbErrorCode=" + ewbErrorCode
				+ ", ewbErrorDesc=" + ewbErrorDesc + ", ewbProcessingStatus="
				+ ewbProcessingStatus + ", aspInvoiceStatus=" + aspInvoiceStatus
				+ ", complianceApplicable=" + complianceApplicable
				+ ", infoErrorCode=" + infoErrorCode + ", infoErrorMsg="
				+ infoErrorMsg + ", salesOrgnization=" + salesOrgnization
				+ ", distributionChannel=" + distributionChannel
				+ ", origDocType=" + origDocType + ", origCgstin=" + origCgstin
				+ ", billToState=" + billToState + ", shipToState="
				+ shipToState + ", shippingBillNo=" + shippingBillNo
				+ ", shippingBillDate=" + shippingBillDate
				+ ", accountingVoucherNumber=" + accountingVoucherNumber
				+ ", accountingVoucherDate=" + accountingVoucherDate
				+ ", tableType=" + tableType + ", gstnBifurcation="
				+ gstnBifurcation + ", tableTypeNew=" + tableTypeNew
				+ ", gstnBifurcationNew=" + gstnBifurcationNew + ", returnType="
				+ returnType + ", gstrReturnType=" + gstrReturnType
				+ ", erpBatchId=" + erpBatchId + ", igstAmount=" + igstAmount
				+ ", cgstAmount=" + cgstAmount + ", sgstAmount=" + sgstAmount
				+ ", cessAmountSpecific=" + cessAmountSpecific
				+ ", cessAmountAdvalorem=" + cessAmountAdvalorem
				+ ", memoValIgst=" + memoValIgst + ", memoValCgst="
				+ memoValCgst + ", memoValSgst=" + memoValSgst
				+ ", memoValCess=" + memoValCess + ", ret1IgstImpact="
				+ ret1IgstImpact + ", ret1CgstImpact=" + ret1CgstImpact
				+ ", ret1SgstImpact=" + ret1SgstImpact + ", derivedSgstinPan="
				+ derivedSgstinPan + ", derivedCgstinPan=" + derivedCgstinPan
				+ ", taxScheme=" + taxScheme + ", docCategory=" + docCategory
				+ ", supplierLegalName=" + supplierLegalName
				+ ", supplierBuildingNumber=" + supplierBuildingNumber
				+ ", supplierBuildingName=" + supplierBuildingName
				+ ", supplierLocation=" + supplierLocation
				+ ", supplierPincode=" + supplierPincode
				+ ", supplierStateCode=" + supplierStateCode
				+ ", supplierPhone=" + supplierPhone + ", supplierEmail="
				+ supplierEmail + ", customerTradeName=" + customerTradeName
				+ ", customerPincode=" + customerPincode + ", customerPhone="
				+ customerPhone + ", customerEmail=" + customerEmail
				+ ", dispatcherTradeName=" + dispatcherTradeName
				+ ", dispatcherBuildingNumber=" + dispatcherBuildingNumber
				+ ", dispatcherBuildingName=" + dispatcherBuildingName
				+ ", dispatcherLocation=" + dispatcherLocation
				+ ", dispatcherPincode=" + dispatcherPincode
				+ ", dispatcherStateCode=" + dispatcherStateCode
				+ ", shipToTradeName=" + shipToTradeName + ", shipToLegalName="
				+ shipToLegalName + ", shipToBuildingNumber="
				+ shipToBuildingNumber + ", shipToBuildingName="
				+ shipToBuildingName + ", shipToLocation=" + shipToLocation
				+ ", shipToPincode=" + shipToPincode + ", roundOff=" + roundOff
				+ ", totalInvoiceValueInWords=" + totalInvoiceValueInWords
				+ ", foreignCurrency=" + foreignCurrency + ", countryCode="
				+ countryCode + ", invoiceValueFc=" + invoiceValueFc
				+ ", invoicePeriodStartDate=" + invoicePeriodStartDate
				+ ", invoicePeriodEndDate=" + invoicePeriodEndDate
				+ ", payeeName=" + payeeName + ", modeOfPayment="
				+ modeOfPayment + ", branchOrIfscCode=" + branchOrIfscCode
				+ ", paymentTerms=" + paymentTerms + ", paymentInstruction="
				+ paymentInstruction + ", creditTransfer=" + creditTransfer
				+ ", directDebit=" + directDebit + ", creditDays=" + creditDays
				+ ", accountDetail=" + accountDetail + ", transactionType="
				+ transactionType + ", subSupplyType=" + subSupplyType
				+ ", otherSupplyTypeDescription=" + otherSupplyTypeDescription
				+ ", transporterID=" + transporterID + ", transporterName="
				+ transporterName + ", transportMode=" + transportMode
				+ ", transportDocNo=" + transportDocNo + ", transportDocDate="
				+ transportDocDate + ", distance=" + distance + ", vehicleNo="
				+ vehicleNo + ", vehicleType=" + vehicleType + ", exchangeRate="
				+ exchangeRate + ", glPostingDate=" + glPostingDate
				+ ", salesOrderNumber=" + salesOrderNumber + ", customerTan="
				+ customerTan + ", cancellationReason=" + cancellationReason
				+ ", cancellationRemarks=" + cancellationRemarks
				+ ", tcsFlagIncomeTax=" + tcsFlagIncomeTax
				+ ", customerPANOrAadhaar=" + customerPANOrAadhaar
				+ ", glStateCessSpecific=" + glStateCessSpecific
				+ ", originalInvoiceNumber=" + originalInvoiceNumber
				+ ", originalInvoiceDate=" + originalInvoiceDate
				+ ", preceedingInvoiceNumber=" + preceedingInvoiceNumber
				+ ", preceedingInvoiceDate=" + preceedingInvoiceDate
				+ ", glCodeIgst=" + glCodeIgst + ", glCodeCgst=" + glCodeCgst
				+ ", glCodeSgst=" + glCodeSgst + ", glCodeAdvCess="
				+ glCodeAdvCess + ", glCodeSpCess=" + glCodeSpCess
				+ ", glCodeStateCess=" + glCodeStateCess + ", profitCentre2="
				+ profitCentre2 + ", invoiceRemarks=" + invoiceRemarks
				+ ", ecomTransactionID=" + ecomTransactionID
				+ ", stateCessSpecificAmt=" + stateCessSpecificAmt
				+ ", preceedingInvAmt=" + preceedingInvAmt
				+ ", preceedingTaxableVal=" + preceedingTaxableVal
				+ ", userDefinedField29=" + userDefinedField29
				+ ", userDefinedField30=" + userDefinedField30
				+ ", einvGstnSaveStatus=" + einvGstnSaveStatus
				+ ", reconResponseId=" + reconResponseId + ", ewbApplicable="
				+ ewbApplicable + ", einvApplicable=" + einvApplicable
				+ ", reconResponseSource=" + reconResponseSource + ", irnDate="
				+ irnDate + ", isCgstInMasterCust=" + isCgstInMasterCust
				+ ", computedDistance=" + computedDistance
				+ ", remainingDistance=" + remainingDistance + ", fileName="
				+ fileName + ", validUpto=" + validUpto + ", reasonCode="
				+ reasonCode + ", fromPlace=" + fromPlace + ", toPlace="
				+ toPlace + ", refId=" + refId + ", ewbPartBUpdated="
				+ ewbPartBUpdated + ", optedForEwb=" + optedForEwb
				+ ", optedForEinv=" + optedForEinv + ", einvJob=" + einvJob
				+ ", ewbJob=" + ewbJob + ", uqcNotFoundInMaster="
				+ uqcNotFoundInMaster + ", masterProductMap=" + masterProductMap
				+ ", lineItems=" + lineItems + ", preDocDtls=" + preDocDtls
				+ ", contrDtls=" + contrDtls + ", addlDocDtls=" + addlDocDtls
				+ ", eInvDetails=" + eInvDetails + ", ewbDetails=" + ewbDetails
				+ "]";
	}

}