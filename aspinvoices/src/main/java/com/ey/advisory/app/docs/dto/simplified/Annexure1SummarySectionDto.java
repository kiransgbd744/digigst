package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class Annexure1SummarySectionDto {

	/**
	 * Represents the total number of documents that are summmed up to get this
	 * summary. Since the user provides different search criteria for getting
	 * the summary, he/she would like to know the number of invoices/documents
	 * that were used to arrive at this summary values, based on the search
	 * criteria. This variable represents this total count of
	 * invoices/documents.
	 */
	// @Expose
	// @SerializedName("records")
	private Integer records = 0;

	private String returnPeriod;

	private String gstin;

	// @Expose
	// @SerializedName("tableSection")
	private String tableSection;

	/**
	 * Document type to be displayed on the UI (Ex: Debit Note, Credit Note,
	 * Invoice and respective amendments)
	 */
	private String docType;

	/**
	 * The total document/invoice value.
	 */
	// @Expose
	// @SerializedName("invValue")
	private BigDecimal invValue = BigDecimal.ZERO;

	/**
	 * The sum of taxable value for all the invoices involved in getting the
	 * summary. In case of advance received, this represents the received amount
	 * (because the taxable value is the received amount). In case of advance
	 * tax adjusted, this represents the tax amount adjusted (because, this is
	 * the amount that can be reduced from the total tax payable to the
	 * government)
	 */
	// @Expose
	// @SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	// @Expose
	// @SerializedName("taxPayble")
	private BigDecimal taxPayble = BigDecimal.ZERO;

	/**
	 * The IGST amount
	 */
	// @Expose
	// @SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	/**
	 * The CGST Amount
	 */
	// @Expose
	// @SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	/**
	 * The SGST Amount
	 */
	// @Expose
	// @SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	/**
	 * The Cess Amount
	 */
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

	private BigDecimal memoIgst = BigDecimal.ZERO;
	private BigDecimal memoSgst = BigDecimal.ZERO;
	private BigDecimal memoCgst = BigDecimal.ZERO;
	private BigDecimal memoTaxPayable = BigDecimal.ZERO;
	private BigDecimal memoCess = BigDecimal.ZERO;

	private Integer gstnCount = 0;
	private BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxableValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;

	public Annexure1SummarySectionDto() {
	}

	public Annexure1SummarySectionDto(String section) {
		this.tableSection = section;
	}

	



	/**
	 * @return the records
	 */
	public Integer getRecords() {
		return records;
	}

	/**
	 * @param records
	 *            the records to set
	 */
	public void setRecords(Integer records) {
		this.records = records != 0 ? records : 0;
	}

	/**
	 * @return the tableSection
	 */
	public String getTableSection() {
		return tableSection;
	}

	/**
	 * @param tableSection
	 *            the tableSection to set
	 */
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the invValue
	 */
	public BigDecimal getInvValue() {
		return invValue;
	}

	/**
	 * @param invValue
	 *            the invValue to set
	 */
	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	/**
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	/**
	 * @param taxableValue
	 *            the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue != null ? taxableValue
				: BigDecimal.ZERO;
	}

	/**
	 * @return the taxPayble
	 */
	public BigDecimal getTaxPayble() {
		return taxPayble;
	}

	/**
	 * @param taxPayble
	 *            the taxPayble to set
	 */
	public void setTaxPayble(BigDecimal taxPayble) {
		this.taxPayble = taxPayble;
	}

	/**
	 * @return the igst
	 */
	public BigDecimal getIgst() {
		return igst;
	}

	/**
	 * @param igst
	 *            the igst to set
	 */
	public void setIgst(BigDecimal igst) {
		this.igst = igst != null ? igst : BigDecimal.ZERO;
	}

	/**
	 * @return the cgst
	 */
	public BigDecimal getCgst() {
		return cgst;
	}

	/**
	 * @param cgst
	 *            the cgst to set
	 */
	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst != null ? cgst : BigDecimal.ZERO;
	}

	/**
	 * @return the sgst
	 */
	public BigDecimal getSgst() {
		return sgst;
	}

	/**
	 * @param sgst
	 *            the sgst to set
	 */
	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst != null ? sgst : BigDecimal.ZERO;
	}

	/**
	 * @return the cess
	 */
	public BigDecimal getCess() {
		return cess;
	}

	/**
	 * @param cess
	 *            the cess to set
	 */
	public void setCess(BigDecimal cess) {
		this.cess = cess != null ? cess : BigDecimal.ZERO;
	}

	public BigDecimal getMemoIgst() {
		return memoIgst;
	}

	public void setMemoIgst(BigDecimal memoIgst) {
		this.memoIgst = memoIgst != null ? memoIgst : BigDecimal.ZERO;
	}

	public BigDecimal getMemoSgst() {
		return memoSgst;
	}

	public void setMemoSgst(BigDecimal memoSgst) {
		this.memoSgst = memoSgst != null ? memoSgst : BigDecimal.ZERO;
	}

	public BigDecimal getMemoCgst() {
		return memoCgst;
	}

	public void setMemoCgst(BigDecimal memoCgst) {
		this.memoCgst = memoCgst != null ? memoCgst : BigDecimal.ZERO;
	}

	public BigDecimal getMemoTaxPayable() {
		return memoTaxPayable;
	}

	public void setMemoTaxPayable(BigDecimal memoTaxPayable) {
		this.memoTaxPayable = memoTaxPayable != null ? memoTaxPayable
				: BigDecimal.ZERO;
	}

	/**
	 * @return the memoCess
	 */
	public BigDecimal getMemoCess() {
		return memoCess;
	}

	/**
	 * @param memoCess
	 *            the memoCess to set
	 */
	public void setMemoCess(BigDecimal memoCess) {
		this.memoCess = memoCess != null ? memoCess : BigDecimal.ZERO;
	}

	public Integer getGstnCount() {
		return gstnCount;
	}

	public void setGstnCount(Integer gstnCount) {
		this.gstnCount = gstnCount;
	}

	public BigDecimal getGstnInvoiceValue() {
		return gstnInvoiceValue;
	}

	public void setGstnInvoiceValue(BigDecimal gstnInvoiceValue) {
		this.gstnInvoiceValue = gstnInvoiceValue;
	}

	public BigDecimal getGstnTaxableValue() {
		return gstnTaxableValue;
	}

	public void setGstnTaxableValue(BigDecimal gstnTaxableValue) {
		this.gstnTaxableValue = gstnTaxableValue;
	}

	public BigDecimal getGstnTaxPayble() {
		return gstnTaxPayble;
	}

	public void setGstnTaxPayble(BigDecimal gstnTaxPayble) {
		this.gstnTaxPayble = gstnTaxPayble;
	}

	public BigDecimal getGstnIgst() {
		return gstnIgst;
	}

	public void setGstnIgst(BigDecimal gstnIgst) {
		this.gstnIgst = gstnIgst;
	}

	public BigDecimal getGstnCgst() {
		return gstnCgst;
	}

	public void setGstnCgst(BigDecimal gstnCgst) {
		this.gstnCgst = gstnCgst;
	}

	public BigDecimal getGstnSgst() {
		return gstnSgst;
	}

	public void setGstnSgst(BigDecimal gstnSgst) {
		this.gstnSgst = gstnSgst;
	}

	public BigDecimal getGstnCess() {
		return gstnCess;
	}

	public void setGstnCess(BigDecimal gstnCess) {
		this.gstnCess = gstnCess;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

		@Override
	public String toString() {
		return "Annexure1SummarySectionDto [records=" + records
				+ ", returnPeriod=" + returnPeriod + ", gstin=" + gstin
				+ ", tableSection=" + tableSection + ", docType=" + docType
				+ ", invValue=" + invValue + ", taxableValue=" + taxableValue
				+ ", taxPayble=" + taxPayble + ", igst=" + igst + ", cgst="
				+ cgst + ", sgst=" + sgst + ", cess=" + cess + ", memoIgst="
				+ memoIgst + ", memoSgst=" + memoSgst + ", memoCgst=" + memoCgst
				+ ", memoTaxPayable=" + memoTaxPayable + ", memoCess="
				+ memoCess + ", gstnCount=" + gstnCount + ", gstnInvoiceValue="
				+ gstnInvoiceValue + ", gstnTaxableValue=" + gstnTaxableValue
				+ ", gstnTaxPayble=" + gstnTaxPayble + ", gstnIgst=" + gstnIgst
				+ ", gstnCgst=" + gstnCgst + ", gstnSgst=" + gstnSgst
				+ ", gstnCess=" + gstnCess + "]";
	}
}
