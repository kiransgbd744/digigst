package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

@Entity
@Table(name = "ANX_INWARD_DOC_ITEM")
public class InwardTransDocLineItem extends TransDocLineItem {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_INWARD_DOC_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_NO")
	protected Integer lineNo;

	@Expose
	@SerializedName("eligibilityIndicator")
	@Column(name = "ELIGIBILITY_INDICATOR")
	protected String eligibilityIndicator;

	@Expose
	@SerializedName("commonSupplyIndicator")
	@Column(name = "COMMON_SUP_INDICATOR")
	protected String commonSupplyIndicator;

	@Expose
	@SerializedName("availableIgst")
	@Column(name = "AVAILABLE_IGST")
	protected BigDecimal availableIgst;

	@Expose
	@SerializedName("availableCgst")
	@Column(name = "AVAILABLE_CGST")
	protected BigDecimal availableCgst;

	@Expose
	@SerializedName("availableSgst")
	@Column(name = "AVAILABLE_SGST")
	protected BigDecimal availableSgst;

	@Expose
	@SerializedName("availableCess")
	@Column(name = "AVAILABLE_CESS")
	protected BigDecimal availableCess;

	@Expose
	@SerializedName("itcReversalIdentifier")
	@Column(name = "ITC_REVERSAL_IDENTIFER")
	protected String itcReversalIdentifier;

	@Expose
	@SerializedName("paymentVoucherNumber")
	@Column(name = "PAYMENT_VOUCHER_NUM")
	protected String paymentVoucherNumber;

	@Expose
	@SerializedName("paymentDate")
	@Column(name = "PAYMENT_DATE")
	protected LocalDate paymentDate;

	@Expose
	@SerializedName("contractNumber")
	@Column(name = "CONTRACT_NUMBER")
	protected String contractNumber;

	@Expose
	@SerializedName("contractDate")
	@Column(name = "CONTRACT_DATE")
	protected LocalDate contractDate;

	@Expose
	@SerializedName("contractValue")
	@Column(name = "CONTRACT_VALUE")
	protected BigDecimal contractValue;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private InwardTransDocument document;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Date createdDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Date modifiedDate;

	@Expose
	@SerializedName("taxDocType")
	@Column(name = "TAX_DOC_TYPE")
	protected String taxDocType;

	@Expose
	@SerializedName("cifValue")
	@Column(name = "CIF_VALUE")
	protected BigDecimal cifValue;
	@Expose
	@SerializedName("customDuty")
	@Column(name = "CUSTOM_DUTY")
	protected BigDecimal customDuty;

	@Expose
	@SerializedName("purchaseVoucherDate")
	@Column(name = "PURCHASE_VOUCHER_DATE")
	protected LocalDate purchaseVoucherDate;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("purchaseVoucherNum")
	@Column(name = "PURCHASE_VOUCHER_NUM")
	protected String purchaseVoucherNum;

	@Column(name = "MEMO_VALUE_IGST")
	protected BigDecimal memoValIgst;

	@Column(name = "MEMO_VALUE_CGST")
	protected BigDecimal memoValCgst;

	@Column(name = "MEMO_VALUE_SGST")
	protected BigDecimal memoValSgst;

	// Start - Fix for Defect Error code "ER0037"
	@Column(name = "PURCHASE_ORGANIZATION")
	protected String purchaseOrganization;

	@Column(name = "ORIG_SUPPLIER_GSTIN")
	protected String origSgstin;

	@Column(name = "BILL_OF_ENTRY")
	protected String billOfEntryNo;

	@Column(name = "BILL_OF_ENTRY_DATE")
	protected LocalDate billOfEntryDate;

	@Column(name = "ITC_ENTITLEMENT")
	protected String itcEntitlement;

	@Column(name = "POSTING_DATE")
	protected LocalDate postingDate;

	@Column(name = "ELIGIBLE_IGST_AMT")
	protected BigDecimal eligibleIgst;

	@Column(name = "ELIGIBLE_CGST_AMT")
	protected BigDecimal eligibleCgst;

	@Column(name = "ELIGIBLE_SGST_AMT")
	protected BigDecimal eligibleSgst;

	@Column(name = "ELIGIBLE_CESS_AMT")
	protected BigDecimal eligibleCess;

	@Column(name = "IN_ELIGIBLE_IGST_AMT")
	protected BigDecimal inEligibleIgst;

	@Column(name = "IN_ELIGIBLE_CGST_AMT")
	protected BigDecimal inEligibleCgst;

	@Column(name = "IN_ELIGIBLE_SGST_AMT")
	protected BigDecimal inEligibleSgst;

	@Column(name = "IN_ELIGIBLE_CESS_AMT")
	protected BigDecimal inEligibleCess;

	@Column(name = "ELIGIBLE_TAX_PAYABLE")
	protected BigDecimal eligibleTaxPayable;

	@Column(name = "IN_ELIGIBLE_TAX_PAYABLE")
	protected BigDecimal inEligibleTaxPayable;

	@Column(name = "AVAILABLE_TAX_PAYABLE")
	protected BigDecimal availableTaxPayable;

	@Column(name = "PRECEEDING_TAXABLE_VALUE")
	protected BigDecimal precTaxableValue;

	@Column(name = "PRECEEDING_INVOICE_VALUE")
	protected BigDecimal precInvoiceValue;

	@Column(name = "PRECEEDING_TOTAL_TAX")
	protected BigDecimal precTotalTax;

	@Column(name = "PRECEEDING_IGST_AMT")
	protected BigDecimal precIgstAmt;

	@Column(name = "PRECEEDING_CGST_AMT")
	protected BigDecimal precCgstAmt;

	@Column(name = "PRECEEDING_SGST_AMT")
	protected BigDecimal precSgstAmt;

	@Column(name = "PRECEEDING_CESS_AMT")
	protected BigDecimal precCessAmt;

	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected LocalDate irnDate;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected LocalDate paymentDueDate;

	@Expose
	@SerializedName("taxScheme")
	@Column(name = "TAX_SCHEME")
	protected String taxScheme;

	@Expose
	@SerializedName("dispatcherPincode")
	@Column(name = "DISPATCHER_PINCODE")
	protected String dispatcherPincode;

	@Expose
	@SerializedName("shipToPincode")
	@Column(name = "SHIP_TO_PINCODE")
	protected String shipToPincode;

	@Expose
	@SerializedName("supTradeName")
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@SerializedName("supPhone")
	@Column(name = "SUPP_PHONE")
	protected String supplierPhone;

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
	protected String customerBuildingNumber;

	@Expose
	@SerializedName("custBuildingName")
	@Column(name = "CUST_BUILDING_NAME")
	protected String customerBuildingName;

	@Expose
	@SerializedName("custLocation")
	@Column(name = "CUST_LOCATION")
	protected String customerLocation;

	@Expose
	@SerializedName("custPincode")
	@Column(name = "CUST_PINCODE")
	protected String customerPincode;

	@Expose
	@SerializedName("custPhone")
	@Column(name = "CUST_PHONE")
	protected String customerPhone;

	@Expose
	@SerializedName("shipToTradeName")
	@Column(name = "SHIP_TO_TRADE_NAME")
	protected String shipToTradeName;

	@Expose
	@SerializedName("roundOff")
	@Column(name = "ROUND_OFF")
	protected BigDecimal roundOff;

	@Expose
	@SerializedName("totalInvValueInWords")
	@Column(name = "TOT_INV_VAL_WORDS")
	protected String totalInvoiceValueInWords;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@Column(name = "TCS_FLAG_INCOME_TAX")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("invRemarks")
	@Column(name = "INV_REMARKS")
	private String invoiceRemarks;

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
	@SerializedName("supportingDoc")
	@Column(name = "SUPPORTING_DOC")
	private String supportingDoc;

	@Expose
	@SerializedName("exchangeRt")
	@Column(name = "EXCHANGE_RATE")
	protected String exchangeRate;

	@Expose
	@SerializedName("tcsIgstAmt")
	@Column(name = "TCS_IGST_AMT")
	protected BigDecimal tcsIgstAmount;

	@Expose
	@SerializedName("srcIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("distance")
	@Column(name = "DISTANCE")
	protected Integer distance;

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

	// End - Fix for Defect Error code "ER0037"

	/*
	 * @Column(name = "CUST_SUPP_ADDRESS3") protected String custOrSuppAddress3;
	 */

	public InwardTransDocLineItem() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of inward document
		// line item.
	}

	/**
	 * @return the eligibilityIndicator
	 */
	public String getEligibilityIndicator() {
		return eligibilityIndicator;
	}

	/**
	 * @param eligibilityIndicator
	 *            the eligibilityIndicator to set
	 */
	public void setEligibilityIndicator(String eligibilityIndicator) {
		this.eligibilityIndicator = eligibilityIndicator;
	}

	/**
	 * @return the commonSupplyIndicator
	 */
	public String getCommonSupplyIndicator() {
		return commonSupplyIndicator;
	}

	/**
	 * @param commonSupplyIndicator
	 *            the commonSupplyIndicator to set
	 */
	public void setCommonSupplyIndicator(String commonSupplyIndicator) {
		this.commonSupplyIndicator = commonSupplyIndicator;
	}

	/**
	 * @return the availableIgst
	 */
	public BigDecimal getAvailableIgst() {
		return availableIgst;
	}

	/**
	 * @param availableIgst
	 *            the availableIgst to set
	 */
	public void setAvailableIgst(BigDecimal availableIgst) {
		this.availableIgst = availableIgst;
	}

	/**
	 * @return the availableCgst
	 */
	public BigDecimal getAvailableCgst() {
		return availableCgst;
	}

	/**
	 * @param availableCgst
	 *            the availableCgst to set
	 */
	public void setAvailableCgst(BigDecimal availableCgst) {
		this.availableCgst = availableCgst;
	}

	/**
	 * @return the availableSgst
	 */
	public BigDecimal getAvailableSgst() {
		return availableSgst;
	}

	/**
	 * @param availableSgst
	 *            the availableSgst to set
	 */
	public void setAvailableSgst(BigDecimal availableSgst) {
		this.availableSgst = availableSgst;
	}

	/**
	 * @return the availableCess
	 */
	public BigDecimal getAvailableCess() {
		return availableCess;
	}

	/**
	 * @param availableCess
	 *            the availableCess to set
	 */
	public void setAvailableCess(BigDecimal availableCess) {
		this.availableCess = availableCess;
	}

	/**
	 * @return the itcReversalIdentifier
	 */
	public String getItcReversalIdentifier() {
		return itcReversalIdentifier;
	}

	/**
	 * @param itcReversalIdentifier
	 *            the itcReversalIdentifier to set
	 */
	public void setItcReversalIdentifier(String itcReversalIdentifier) {
		this.itcReversalIdentifier = itcReversalIdentifier;
	}

	/**
	 * @return the paymentVoucherNumber
	 */
	public String getPaymentVoucherNumber() {
		return paymentVoucherNumber;
	}

	/**
	 * @param paymentVoucherNumber
	 *            the paymentVoucherNumber to set
	 */
	public void setPaymentVoucherNumber(String paymentVoucherNumber) {
		this.paymentVoucherNumber = paymentVoucherNumber;
	}

	/**
	 * @return the paymentDate
	 */
	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate
	 *            the paymentDate to set
	 */
	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the contractNumber
	 */
	public String getContractNumber() {
		return contractNumber;
	}

	/**
	 * @param contractNumber
	 *            the contractNumber to set
	 */
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	/**
	 * @return the contractDate
	 */
	public LocalDate getContractDate() {
		return contractDate;
	}

	/**
	 * @param contractDate
	 *            the contractDate to set
	 */
	public void setContractDate(LocalDate contractDate) {
		this.contractDate = contractDate;
	}

	/**
	 * @return the contractValue
	 */
	public BigDecimal getContractValue() {
		return contractValue;
	}

	/**
	 * @param contractValue
	 *            the contractValue to set
	 */
	public void setContractValue(BigDecimal contractValue) {
		this.contractValue = contractValue;
	}

	/**
	 * @return the document
	 */
	public InwardTransDocument getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(InwardTransDocument document) {
		this.document = document;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}

	/**
	 * @param taxDocType
	 *            the taxDocType to set
	 */
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	/**
	 * @return the cifValue
	 */
	public BigDecimal getCifValue() {
		return cifValue;
	}

	/**
	 * @param cifValue
	 *            the cifValue to set
	 */
	public void setCifValue(BigDecimal cifValue) {
		this.cifValue = cifValue;
	}

	/**
	 * @return the customDuty
	 */
	public BigDecimal getCustomDuty() {
		return customDuty;
	}

	/**
	 * @param customDuty
	 *            the customDuty to set
	 */
	public void setCustomDuty(BigDecimal customDuty) {
		this.customDuty = customDuty;
	}

	/**
	 * @return the purchaseVoucherDate
	 */
	public LocalDate getPurchaseVoucherDate() {
		return purchaseVoucherDate;
	}

	/**
	 * @param purchaseVoucherDate
	 *            the purchaseVoucherDate to set
	 */
	public void setPurchaseVoucherDate(LocalDate purchaseVoucherDate) {
		this.purchaseVoucherDate = purchaseVoucherDate;
	}

	/**
	 * @return the purchaseVoucherNum
	 */
	public String getPurchaseVoucherNum() {
		return purchaseVoucherNum;
	}

	/**
	 * @param purchaseVoucherNum
	 *            the purchaseVoucherNum to set
	 */
	public void setPurchaseVoucherNum(String purchaseVoucherNum) {
		this.purchaseVoucherNum = purchaseVoucherNum;
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

	public String getPurchaseOrganization() {
		return purchaseOrganization;
	}

	public void setPurchaseOrganization(String purchaseOrganization) {
		this.purchaseOrganization = purchaseOrganization;
	}

	public String getOrigSgstin() {
		return origSgstin;
	}

	public void setOrigSgstin(String origSgstin) {
		this.origSgstin = origSgstin;
	}

	public String getBillOfEntryNo() {
		return billOfEntryNo;
	}

	public void setBillOfEntryNo(String billOfEntryNo) {
		this.billOfEntryNo = billOfEntryNo;
	}

	public LocalDate getBillOfEntryDate() {
		return billOfEntryDate;
	}

	public void setBillOfEntryDate(LocalDate billOfEntryDate) {
		this.billOfEntryDate = billOfEntryDate;
	}

	public String getItcEntitlement() {
		return itcEntitlement;
	}

	public void setItcEntitlement(String itcEntitlement) {
		this.itcEntitlement = itcEntitlement;
	}

	public LocalDate getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	/**
	 * @return the eligibleIgst
	 */
	public BigDecimal getEligibleIgst() {
		return eligibleIgst;
	}

	/**
	 * @param eligibleIgst
	 *            the eligibleIgst to set
	 */
	public void setEligibleIgst(BigDecimal eligibleIgst) {
		this.eligibleIgst = eligibleIgst;
	}

	/**
	 * @return the eligibleCgst
	 */
	public BigDecimal getEligibleCgst() {
		return eligibleCgst;
	}

	/**
	 * @param eligibleCgst
	 *            the eligibleCgst to set
	 */
	public void setEligibleCgst(BigDecimal eligibleCgst) {
		this.eligibleCgst = eligibleCgst;
	}

	/**
	 * @return the eligibleSgst
	 */
	public BigDecimal getEligibleSgst() {
		return eligibleSgst;
	}

	/**
	 * @param eligibleSgst
	 *            the eligibleSgst to set
	 */
	public void setEligibleSgst(BigDecimal eligibleSgst) {
		this.eligibleSgst = eligibleSgst;
	}

	/**
	 * @return the eligibleCess
	 */
	public BigDecimal getEligibleCess() {
		return eligibleCess;
	}

	/**
	 * @param eligibleCess
	 *            the eligibleCess to set
	 */
	public void setEligibleCess(BigDecimal eligibleCess) {
		this.eligibleCess = eligibleCess;
	}

	/**
	 * @return the inEligibleIgst
	 */
	public BigDecimal getInEligibleIgst() {
		return inEligibleIgst;
	}

	/**
	 * @param inEligibleIgst
	 *            the inEligibleIgst to set
	 */
	public void setInEligibleIgst(BigDecimal inEligibleIgst) {
		this.inEligibleIgst = inEligibleIgst;
	}

	/**
	 * @return the inEligibleCgst
	 */
	public BigDecimal getInEligibleCgst() {
		return inEligibleCgst;
	}

	/**
	 * @param inEligibleCgst
	 *            the inEligibleCgst to set
	 */
	public void setInEligibleCgst(BigDecimal inEligibleCgst) {
		this.inEligibleCgst = inEligibleCgst;
	}

	/**
	 * @return the inEligibleSgst
	 */
	public BigDecimal getInEligibleSgst() {
		return inEligibleSgst;
	}

	/**
	 * @param inEligibleSgst
	 *            the inEligibleSgst to set
	 */
	public void setInEligibleSgst(BigDecimal inEligibleSgst) {
		this.inEligibleSgst = inEligibleSgst;
	}

	/**
	 * @return the inEligibleCess
	 */
	public BigDecimal getInEligibleCess() {
		return inEligibleCess;
	}

	/**
	 * @param inEligibleCess
	 *            the inEligibleCess to set
	 */
	public void setInEligibleCess(BigDecimal inEligibleCess) {
		this.inEligibleCess = inEligibleCess;
	}

	/**
	 * @return the eligibleTaxPayable
	 */
	public BigDecimal getEligibleTaxPayable() {
		return eligibleTaxPayable;
	}

	/**
	 * @param eligibleTaxPayable
	 *            the eligibleTaxPayable to set
	 */
	public void setEligibleTaxPayable(BigDecimal eligibleTaxPayable) {
		this.eligibleTaxPayable = eligibleTaxPayable;
	}

	/**
	 * @return the inEligibleTaxPayable
	 */
	public BigDecimal getInEligibleTaxPayable() {
		return inEligibleTaxPayable;
	}

	/**
	 * @param inEligibleTaxPayable
	 *            the inEligibleTaxPayable to set
	 */
	public void setInEligibleTaxPayable(BigDecimal inEligibleTaxPayable) {
		this.inEligibleTaxPayable = inEligibleTaxPayable;
	}

	/**
	 * @return the availableTaxPayable
	 */
	public BigDecimal getAvailableTaxPayable() {
		return availableTaxPayable;
	}

	/**
	 * @param availableTaxPayable
	 *            the availableTaxPayable to set
	 */
	public void setAvailableTaxPayable(BigDecimal availableTaxPayable) {
		this.availableTaxPayable = availableTaxPayable;
	}

	/**
	 * @return the precTaxableValue
	 */
	public BigDecimal getPrecTaxableValue() {
		return precTaxableValue;
	}

	/**
	 * @param precTaxableValue
	 *            the precTaxableValue to set
	 */
	public void setPrecTaxableValue(BigDecimal precTaxableValue) {
		this.precTaxableValue = precTaxableValue;
	}

	/**
	 * @return the precInvoiceValue
	 */
	public BigDecimal getPrecInvoiceValue() {
		return precInvoiceValue;
	}

	/**
	 * @param precInvoiceValue
	 *            the precInvoiceValue to set
	 */
	public void setPrecInvoiceValue(BigDecimal precInvoiceValue) {
		this.precInvoiceValue = precInvoiceValue;
	}

	/**
	 * @return the precTotalTax
	 */
	public BigDecimal getPrecTotalTax() {
		return precTotalTax;
	}

	/**
	 * @param precTotalTax
	 *            the precTotalTax to set
	 */
	public void setPrecTotalTax(BigDecimal precTotalTax) {
		this.precTotalTax = precTotalTax;
	}

	/**
	 * @return the precIgstAmt
	 */
	public BigDecimal getPrecIgstAmt() {
		return precIgstAmt;
	}

	/**
	 * @param precIgstAmt
	 *            the precIgstAmt to set
	 */
	public void setPrecIgstAmt(BigDecimal precIgstAmt) {
		this.precIgstAmt = precIgstAmt;
	}

	/**
	 * @return the precCgstAmt
	 */
	public BigDecimal getPrecCgstAmt() {
		return precCgstAmt;
	}

	/**
	 * @param precCgstAmt
	 *            the precCgstAmt to set
	 */
	public void setPrecCgstAmt(BigDecimal precCgstAmt) {
		this.precCgstAmt = precCgstAmt;
	}

	/**
	 * @return the precSgstAmt
	 */
	public BigDecimal getPrecSgstAmt() {
		return precSgstAmt;
	}

	/**
	 * @param precSgstAmt
	 *            the precSgstAmt to set
	 */
	public void setPrecSgstAmt(BigDecimal precSgstAmt) {
		this.precSgstAmt = precSgstAmt;
	}

	/**
	 * @return the precCessAmt
	 */
	public BigDecimal getPrecCessAmt() {
		return precCessAmt;
	}

	/**
	 * @param precCessAmt
	 *            the precCessAmt to set
	 */
	public void setPrecCessAmt(BigDecimal precCessAmt) {
		this.precCessAmt = precCessAmt;
	}

	/**
	 * @return the lineNo
	 */
	public Integer getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo
	 *            the lineNo to set
	 */
	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the irn
	 */
	public String getIrn() {
		return irn;
	}

	/**
	 * @param irn
	 *            the irn to set
	 */
	public void setIrn(String irn) {
		this.irn = irn;
	}

	/**
	 * @return the irnDate
	 */
	public LocalDate getIrnDate() {
		return irnDate;
	}

	/**
	 * @param irnDate
	 *            the irnDate to set
	 */
	public void setIrnDate(LocalDate irnDate) {
		this.irnDate = irnDate;
	}

	/**
	 * @return the dispatcherGstin
	 */
	public String getDispatcherGstin() {
		return dispatcherGstin;
	}

	/**
	 * @param dispatcherGstin
	 *            the dispatcherGstin to set
	 */
	public void setDispatcherGstin(String dispatcherGstin) {
		this.dispatcherGstin = dispatcherGstin;
	}

	/**
	 * @return the shipToGstin
	 */
	public String getShipToGstin() {
		return shipToGstin;
	}

	/**
	 * @param shipToGstin
	 *            the shipToGstin to set
	 */
	public void setShipToGstin(String shipToGstin) {
		this.shipToGstin = shipToGstin;
	}

	/**
	 * @return the paymentDueDate
	 */
	public LocalDate getPaymentDueDate() {
		return paymentDueDate;
	}

	/**
	 * @param paymentDueDate
	 *            the paymentDueDate to set
	 */
	public void setPaymentDueDate(LocalDate paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
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
	 * @return the supplierTradeName
	 */
	public String getSupplierTradeName() {
		return supplierTradeName;
	}

	/**
	 * @param supplierTradeName
	 *            the supplierTradeName to set
	 */
	public void setSupplierTradeName(String supplierTradeName) {
		this.supplierTradeName = supplierTradeName;
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
	 * @return the customerLegalName
	 */
	public String getCustomerLegalName() {
		return customerLegalName;
	}

	/**
	 * @param customerLegalName
	 *            the customerLegalName to set
	 */
	public void setCustomerLegalName(String customerLegalName) {
		this.customerLegalName = customerLegalName;
	}

	/**
	 * @return the customerBuildingNumber
	 */
	public String getCustomerBuildingNumber() {
		return customerBuildingNumber;
	}

	/**
	 * @param customerBuildingNumber
	 *            the customerBuildingNumber to set
	 */
	public void setCustomerBuildingNumber(String customerBuildingNumber) {
		this.customerBuildingNumber = customerBuildingNumber;
	}

	/**
	 * @return the customerBuildingName
	 */
	public String getCustomerBuildingName() {
		return customerBuildingName;
	}

	/**
	 * @param customerBuildingName
	 *            the customerBuildingName to set
	 */
	public void setCustomerBuildingName(String customerBuildingName) {
		this.customerBuildingName = customerBuildingName;
	}

	/**
	 * @return the customerLocation
	 */
	public String getCustomerLocation() {
		return customerLocation;
	}

	/**
	 * @param customerLocation
	 *            the customerLocation to set
	 */
	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}

	/**
	 * @return the customerPincode
	 */
	public String getCustomerPincode() {
		return customerPincode;
	}

	/**
	 * @param customerPincode
	 *            the customerPincode to set
	 */
	public void setCustomerPincode(String customerPincode) {
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
	 * @return the supportingDoc
	 */
	public String getSupportingDoc() {
		return supportingDoc;
	}

	/**
	 * @param supportingDoc
	 *            the supportingDoc to set
	 */
	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
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
	 * @return the tcsIgstAmount
	 */
	public BigDecimal getTcsIgstAmount() {
		return tcsIgstAmount;
	}

	/**
	 * @param tcsIgstAmount
	 *            the tcsIgstAmount to set
	 */
	public void setTcsIgstAmount(BigDecimal tcsIgstAmount) {
		this.tcsIgstAmount = tcsIgstAmount;
	}

	/**
	 * @return the sourceIdentifier
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	/**
	 * @param sourceIdentifier
	 *            the sourceIdentifier to set
	 */
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	/**
	 * @return the companyCode
	 */
	public String getCompanyCode() {
		return companyCode;
	}

	/**
	 * @param companyCode
	 *            the companyCode to set
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	/**
	 * @return the dispatcherPincode
	 */
	public String getDispatcherPincode() {
		return dispatcherPincode;
	}

	/**
	 * @param dispatcherPincode
	 *            the dispatcherPincode to set
	 */
	public void setDispatcherPincode(String dispatcherPincode) {
		this.dispatcherPincode = dispatcherPincode;
	}

	/**
	 * @return the shipToPincode
	 */
	public String getShipToPincode() {
		return shipToPincode;
	}

	/**
	 * @param shipToPincode
	 *            the shipToPincode to set
	 */
	public void setShipToPincode(String shipToPincode) {
		this.shipToPincode = shipToPincode;
	}

	/**
	 * @return the distance
	 */
	public Integer getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "InwardTransDocLineItem [id=" + id + ", lineNo=" + lineNo
				+ ", eligibilityIndicator=" + eligibilityIndicator
				+ ", commonSupplyIndicator=" + commonSupplyIndicator
				+ ", availableIgst=" + availableIgst + ", availableCgst="
				+ availableCgst + ", availableSgst=" + availableSgst
				+ ", availableCess=" + availableCess
				+ ", itcReversalIdentifier=" + itcReversalIdentifier
				+ ", paymentVoucherNumber=" + paymentVoucherNumber
				+ ", paymentDate=" + paymentDate + ", contractNumber="
				+ contractNumber + ", contractDate=" + contractDate
				+ ", contractValue=" + contractValue + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", modifiedBy="
				+ modifiedBy + ", modifiedDate=" + modifiedDate
				+ ", taxDocType=" + taxDocType + ", cifValue=" + cifValue
				+ ", customDuty=" + customDuty + ", purchaseVoucherDate="
				+ purchaseVoucherDate + ", errCodes=" + errCodes
				+ ", infoCodes=" + infoCodes + ", purchaseVoucherNum="
				+ purchaseVoucherNum + ", memoValIgst=" + memoValIgst
				+ ", memoValCgst=" + memoValCgst + ", memoValSgst="
				+ memoValSgst + ", purchaseOrganization=" + purchaseOrganization
				+ ", origSgstin=" + origSgstin + ", billOfEntryNo="
				+ billOfEntryNo + ", billOfEntryDate=" + billOfEntryDate
				+ ", itcEntitlement=" + itcEntitlement + ", postingDate="
				+ postingDate + ", eligibleIgst=" + eligibleIgst
				+ ", eligibleCgst=" + eligibleCgst + ", eligibleSgst="
				+ eligibleSgst + ", eligibleCess=" + eligibleCess
				+ ", inEligibleIgst=" + inEligibleIgst + ", inEligibleCgst="
				+ inEligibleCgst + ", inEligibleSgst=" + inEligibleSgst
				+ ", inEligibleCess=" + inEligibleCess + ", eligibleTaxPayable="
				+ eligibleTaxPayable + ", inEligibleTaxPayable="
				+ inEligibleTaxPayable + ", availableTaxPayable="
				+ availableTaxPayable + ", precTaxableValue=" + precTaxableValue
				+ ", precInvoiceValue=" + precInvoiceValue + ", precTotalTax="
				+ precTotalTax + ", precIgstAmt=" + precIgstAmt
				+ ", precCgstAmt=" + precCgstAmt + ", precSgstAmt="
				+ precSgstAmt + ", precCessAmt=" + precCessAmt + ", irn=" + irn
				+ ", irnDate=" + irnDate + ", dispatcherGstin="
				+ dispatcherGstin + ", shipToGstin=" + shipToGstin
				+ ", paymentDueDate=" + paymentDueDate + ", taxScheme="
				+ taxScheme + ", dispatcherPincode=" + dispatcherPincode
				+ ", shipToPincode=" + shipToPincode + ", supplierTradeName="
				+ supplierTradeName + ", supplierPhone=" + supplierPhone
				+ ", customerTradeName=" + customerTradeName
				+ ", customerLegalName=" + customerLegalName
				+ ", customerBuildingNumber=" + customerBuildingNumber
				+ ", customerBuildingName=" + customerBuildingName
				+ ", customerLocation=" + customerLocation
				+ ", customerPincode=" + customerPincode + ", customerPhone="
				+ customerPhone + ", shipToTradeName=" + shipToTradeName
				+ ", roundOff=" + roundOff + ", totalInvoiceValueInWords="
				+ totalInvoiceValueInWords + ", tcsFlagIncomeTax="
				+ tcsFlagIncomeTax + ", invoiceRemarks=" + invoiceRemarks
				+ ", payeeName=" + payeeName + ", modeOfPayment="
				+ modeOfPayment + ", branchOrIfscCode=" + branchOrIfscCode
				+ ", paymentTerms=" + paymentTerms + ", paymentInstruction="
				+ paymentInstruction + ", creditTransfer=" + creditTransfer
				+ ", directDebit=" + directDebit + ", creditDays=" + creditDays
				+ ", accountDetail=" + accountDetail + ", supportingDoc="
				+ supportingDoc + ", exchangeRate=" + exchangeRate
				+ ", tcsIgstAmount=" + tcsIgstAmount + ", sourceIdentifier="
				+ sourceIdentifier + ", companyCode=" + companyCode
				+ ", distance=" + distance + "]";
	}

}