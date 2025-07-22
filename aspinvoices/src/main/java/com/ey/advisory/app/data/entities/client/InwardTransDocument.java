package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.javatuples.Pair;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
@Table(name = "ANX_INWARD_DOC_HEADER")
public class InwardTransDocument extends TransDocument {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_INWARD_DOC_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Expose
	@SerializedName("originalSupplierGstin")
	@Column(name = "ORIG_SUPPLIER_GSTIN")
	protected String origSgstin;

	@Expose
	@SerializedName("billOfEntryNo")
	@Column(name = "BILL_OF_ENTRY")
	protected String billOfEntryNo;

	@Expose
	@SerializedName("billOfEntryDate")
	@Column(name = "BILL_OF_ENTRY_DATE")
	protected LocalDate billOfEntryDate;

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

	/**
	 * Table type represents the table in the GSTR-1 form, where this document
	 * can be placed. E.g. 4A, 4B, 4C etc.
	 */
	@Expose
	@SerializedName("gstr2TableNo")
	@Column(name = "TABLE_SECTION")
	protected String tableType;

	/**
	 * This field represents the classification of invoices according to return
	 * filing rules by GSTN. Some of the possible values are B2B, B2CL, B2CS,
	 * EXP etc.
	 * 
	 */
	@Expose
	@SerializedName("gstr2SubCategory")
	@Column(name = "TAX_DOC_TYPE")
	protected String gstnBifurcation;
	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("anxTableNo")
	@Column(name = "AN_TABLE_SECTION")
	protected String tableTypeNew;

	@Expose
	@SerializedName("anxSubCategory")
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
	@SerializedName("oldtaxperiod")
	@Column(name = "OLD_RETURN_PERIOD")
	protected String oldtaxperiod;


	@Expose
	@SerializedName("itcReversalIdentifier")
	@Column(name = "ITC_REVERSAL_IDENTIFER")
	protected String itcReversalIdentifier;

	@Expose
	@SerializedName("itcEntitlement")
	@Column(name = "ITC_ENTITLEMENT")
	protected String itcEntitlement;

	@Expose
	@SerializedName("purchaseOrganization")
	@Column(name = "PURCHASE_ORGANIZATION")
	protected String purchaseOrganization;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	protected LocalDate postingDate;

	@Expose
	//@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	//@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	//@SerializedName("sgstAmt")
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

	@Transient
	protected boolean isSgstInMasterVendor;

	@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	protected LocalDate origDocDate;

	@Expose
	@SerializedName("purchaseVoucherNum")
	@Column(name = "PURCHASE_VOUCHER_NUM")
	protected String purchaseVoucherNum;

	@Expose
	@SerializedName("purchaseVoucherDate")
	@Column(name = "PURCHASE_VOUCHER_DATE")
	protected LocalDate purchaseVoucherDate;

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
	@SerializedName("is240Format")
	@Column(name = "IS_240_FORMAT")
	private boolean is240Format;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected LocalDate irnDate;

	@Transient
	protected Map<String, List<Pair<Integer, BigDecimal>>> masterItemMap;

	@Expose
	@SerializedName("derEligibilityIndicator")
	@Column(name = "DERIVED_ELIGIBILITY_INDICATOR")
	protected String derEligibilityIndicator;
	
	@Expose
	@SerializedName("inactiveReason")
	@Column(name = "INACTIVE_REASON")
	protected String inactiveReason;
	
	@Expose
	@SerializedName("gstr6SavedReturnPeriod")
	@Column(name = "GSTR6_SAVED_RET_PERIOD")
	protected String gstr6SavedReturnPeriod;
	
	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<InwardTransDocLineItem> lineItems = new ArrayList<>();

	public String getDerEligibilityIndicator() {
		return derEligibilityIndicator;
	}

	public void setDerEligibilityIndicator(String derEligibilityIndicator) {
		this.derEligibilityIndicator = derEligibilityIndicator;
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

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getGstnBifurcation() {
		return gstnBifurcation;
	}

	public void setGstnBifurcation(String gstnBifurcation) {
		this.gstnBifurcation = gstnBifurcation;
	}

	public String getTableTypeNew() {
		return tableTypeNew;
	}

	public void setTableTypeNew(String tableTypeNew) {
		this.tableTypeNew = tableTypeNew;
	}

	public String getGstnBifurcationNew() {
		return gstnBifurcationNew;
	}

	public void setGstnBifurcationNew(String gstnBifurcationNew) {
		this.gstnBifurcationNew = gstnBifurcationNew;
	}

	public String getOldtaxperiod() {
		return oldtaxperiod;
	}

	public void setOldtaxperiod(String oldtaxperiod) {
		this.oldtaxperiod = oldtaxperiod;
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
	 * @return the itcEntitlement
	 */
	public String getItcEntitlement() {
		return itcEntitlement;
	}

	/**
	 * @param itcEntitlement
	 *            the itcEntitlement to set
	 */
	public void setItcEntitlement(String itcEntitlement) {
		this.itcEntitlement = itcEntitlement;
	}

	/**
	 * @return the purchaseOrganization
	 */
	public String getPurchaseOrganization() {
		return purchaseOrganization;
	}

	/**
	 * @param purchaseOrganization
	 *            the purchaseOrganization to set
	 */
	public void setPurchaseOrganization(String purchaseOrganization) {
		this.purchaseOrganization = purchaseOrganization;
	}

	/**
	 * @return the postingDate
	 */
	public LocalDate getPostingDate() {
		return postingDate;
	}

	/**
	 * @param postingDate
	 *            the postingDate to set
	 */
	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	public List<InwardTransDocLineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<InwardTransDocLineItem> lineItems) {
		this.lineItems = lineItems;
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
	 * @return the isSgstInMasterCust
	 */
	public boolean getIsSgstInMasterVendor() {
		return isSgstInMasterVendor;
	}

	/**
	 * @param isCgstInMasterCust
	 *            the isCgstInMasterCust to set
	 */
	public void setIsSgstInMasterVendor(boolean isSgstInMasterVendor) {
		this.isSgstInMasterVendor = isSgstInMasterVendor;
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
	 * @return the masterItemMap
	 */
	public Map<String, List<Pair<Integer, BigDecimal>>> getMasterItemMap() {
		return masterItemMap;
	}

	/**
	 * @param masterItemMap
	 *            the masterItemMap to set
	 */
	public void setMasterItemMap(
			Map<String, List<Pair<Integer, BigDecimal>>> masterItemMap) {
		this.masterItemMap = masterItemMap;
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
	 * @return the is240Format
	 */
	public boolean getIs240Format() {
		return is240Format;
	}

	/**
	 * @param is240Format
	 *            the is240Format to set
	 */
	public void setIs240Format(boolean is240Format) {
		this.is240Format = is240Format;
	}

	public LocalDate getIrnDate() {
		return irnDate;
	}

	public void setIrnDate(LocalDate irnDate) {
		this.irnDate = irnDate;
	}
	
	public String getInactiveReason() {
		return inactiveReason;
	}

	public void setInactiveReason(String inactiveReason) {
		this.inactiveReason = inactiveReason;
	}
	
	

	public String getGstr6SavedReturnPeriod() {
		return gstr6SavedReturnPeriod;
	}

	public void setGstr6SavedReturnPeriod(String gstr6SavedReturnPeriod) {
		this.gstr6SavedReturnPeriod = gstr6SavedReturnPeriod;
	}

	@Override
	public String toString() {
		return "InwardTransDocument [id=" + id + ", origSgstin=" + origSgstin
				+ ", billOfEntryNo=" + billOfEntryNo + ", billOfEntryDate="
				+ billOfEntryDate + ", availableIgst=" + availableIgst
				+ ", availableCgst=" + availableCgst + ", availableSgst="
				+ availableSgst + ", availableCess=" + availableCess
				+ ", tableType=" + tableType + ", gstnBifurcation="
				+ gstnBifurcation + ", errCodes=" + errCodes + ", infoCodes="
				+ infoCodes + ", tableTypeNew=" + tableTypeNew
				+ ", gstnBifurcationNew=" + gstnBifurcationNew + ", returnType="
				+ returnType + ", gstrReturnType=" + gstrReturnType
				+ ", itcReversalIdentifier=" + itcReversalIdentifier
				+ ", itcEntitlement=" + itcEntitlement
				+ ", purchaseOrganization=" + purchaseOrganization
				+ ", postingDate=" + postingDate + ", igstAmount=" + igstAmount
				+ ", cgstAmount=" + cgstAmount + ", sgstAmount=" + sgstAmount
				+ ", cessAmountSpecific=" + cessAmountSpecific
				+ ", cessAmountAdvalorem=" + cessAmountAdvalorem
				+ ", memoValIgst=" + memoValIgst + ", memoValCgst="
				+ memoValCgst + ", memoValSgst=" + memoValSgst
				+ ", isSgstInMasterVendor=" + isSgstInMasterVendor
				+ ", origDocNo=" + origDocNo + ", origDocDate=" + origDocDate
				+ ", purchaseVoucherNum=" + purchaseVoucherNum
				+ ", purchaseVoucherDate=" + purchaseVoucherDate
				+ ", eligibleIgst=" + eligibleIgst + ", eligibleCgst="
				+ eligibleCgst + ", eligibleSgst=" + eligibleSgst
				+ ", eligibleCess=" + eligibleCess + ", inEligibleIgst="
				+ inEligibleIgst + ", inEligibleCgst=" + inEligibleCgst
				+ ", inEligibleSgst=" + inEligibleSgst + ", inEligibleCess="
				+ inEligibleCess + ", eligibleTaxPayable=" + eligibleTaxPayable
				+ ", inEligibleTaxPayable=" + inEligibleTaxPayable
				+ ", availableTaxPayable=" + availableTaxPayable
				+ ", precTaxableValue=" + precTaxableValue
				+ ", oldtaxperiod=" + oldtaxperiod
				+ ", derEligibilityIndicator=" + derEligibilityIndicator
				+ ", precInvoiceValue=" + precInvoiceValue + ", precTotalTax="
				+ precTotalTax + ", precIgstAmt=" + precIgstAmt
				+ ", precCgstAmt=" + precCgstAmt + ", precSgstAmt="
				+ precSgstAmt + ", precCessAmt=" + precCessAmt
				+ ", is240Format=" + is240Format + ", irnDate=" + irnDate
				+ ", masterItemMap=" + masterItemMap + ", lineItems="
				+ lineItems + "]";
	}

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();

		if (itemIndex < lineItemCount && itemIndex >= 0) {
			InwardTransDocLineItem lineItem = lineItems.get(itemIndex);
			return lineItem.getLineNo();
		}

		return null;
	}

}
