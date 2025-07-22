package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *   @author Mahesh.Golla
 */

/**
 * This class contains the summary details for all summaries beloning to B2B,
 * B2BA, B2CL, B2CLA, CDNR, CDNRA, CDNUR, CDNURA etc. There are other summaries
 * like Nil Rated Summary and Doc Issued Summary, that will not fit in the
 * structure of this class. These summary types will have their own Dto classes.
 * Most of the summaries for GSTR1 can be represented using this class.
 */
public class Gstr1BasicSummarySectionDto {

	/**
	 * Represents the total number of documents that are summmed up to get this
	 * summary. Since the user provides different search criteria for getting
	 * the summary, he/she would like to know the number of invoices/documents
	 * that were used to arrive at this summary values, based on the search
	 * criteria. This variable represents this total count of
	 * invoices/documents.
	 */
	//@Expose
	//@SerializedName("records")
	private int records;

	//@Expose
	//@SerializedName("tableSection")
	private String tableSection;

	/**
	 * Document type to be displayed on the UI (Ex: Debit Note, Credit Note,
	 * Invoice and respective amendments)
	 */
	private String docType;
	
	/**
	 * The total document/invoice value.
	 */
	//@Expose
	//@SerializedName("invValue")
	private BigDecimal invValue = BigDecimal.ZERO;

	/**
	 * The sum of taxable value for all the invoices involved in getting the
	 * summary. In case of advance received, this represents the received amount
	 * (because the taxable value is the received amount). In case of advance
	 * tax adjusted, this represents the tax amount adjusted (because, this is
	 * the amount that can be reduced from the total tax payable to the
	 * government)
	 */
	//@Expose
	//@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	//@Expose
	//@SerializedName("taxPayble")
	private BigDecimal taxPayble = BigDecimal.ZERO;

	/**
	 * The IGST amount
	 */
	//@Expose
	//@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	

	/**
	 * The CGST Amount
	 */
	//@Expose
	//@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	/**
	 * The SGST Amount
	 */
	//@Expose
	//@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	/**
	 * The Cess Amount
	 */
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

	public Gstr1BasicSummarySectionDto() {
	}

	public Gstr1BasicSummarySectionDto(String section) {
		this.tableSection = section;
	}

	public Gstr1BasicSummarySectionDto(String section, BigDecimal recordCount,
			BigDecimal taxableValue, BigDecimal taxPayable, BigDecimal docAmt,
			BigDecimal igstAmt, BigDecimal cgstAmt, BigDecimal sgstAmt,
			BigDecimal cessAmt) {
		Integer count = recordCount.intValue();
		this.tableSection = section;
		this.records = (count != null) ? count : 0;
		this.taxableValue = (taxableValue != null) ? taxableValue
				: BigDecimal.ZERO;
		this.taxPayble = (taxPayable != null) ? taxPayable : BigDecimal.ZERO;
		this.invValue = (docAmt != null) ? docAmt : BigDecimal.ZERO;
		this.igst = (igstAmt != null) ? igstAmt : BigDecimal.ZERO;
		this.cgst = (cgstAmt != null) ? cgstAmt : BigDecimal.ZERO;
		this.sgst = (sgstAmt != null) ? sgstAmt : BigDecimal.ZERO;
		this.cess = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
	}

	/**
	 * Add another section summary object to this object. Make sure that the
	 * sections of both objects match. Otherwise we wil get an object that sums
	 * up multiple sections.
	 * 
	 * @param other
	 * @return
	 */
	public Gstr1BasicSummarySectionDto add(Gstr1BasicSummarySectionDto other) {
		this.records = this.records + other.records;
		this.tableSection = other.tableSection;
		this.taxableValue = this.taxableValue.add(other.taxableValue);
		this.taxPayble = this.taxPayble.add(other.taxPayble);
		this.invValue = this.invValue.add(other.invValue);
		this.igst = this.igst.add(other.igst);
		this.sgst = this.sgst.add(other.sgst);
		this.cgst = this.cgst.add(other.cgst);
		this.cess = this.cess.add(other.cess);
		return this;
	}

	/**
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(int records) {
		this.records = records;
	}

	/**
	 * @return the tableSection
	 */
	public String getTableSection() {
		return tableSection;
	}

	/**
	 * @param tableSection the tableSection to set
	 */
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	/**
	 * @return the invValue
	 */
	public BigDecimal getInvValue() {
		return invValue;
	}

	/**
	 * @param invValue the invValue to set
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
	 * @param taxableValue the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	/**
	 * @return the taxPayble
	 */
	public BigDecimal getTaxPayble() {
		return taxPayble;
	}

	/**
	 * @param taxPayble the taxPayble to set
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
	 * @param igst the igst to set
	 */
	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	/**
	 * @return the cgst
	 */
	public BigDecimal getCgst() {
		return cgst;
	}

	/**
	 * @param cgst the cgst to set
	 */
	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	/**
	 * @return the sgst
	 */
	public BigDecimal getSgst() {
		return sgst;
	}

	/**
	 * @param sgst the sgst to set
	 */
	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	/**
	 * @return the cess
	 */
	public BigDecimal getCess() {
		return cess;
	}

	/**
	 * @param cess the cess to set
	 */
	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gstr1BasicSummarySectionDto [records=" + records
				+ ", tableSection=" + tableSection + ", invValue=" + invValue
				+ ", taxableValue=" + taxableValue + ", taxPayble=" + taxPayble
				+ ", igst=" + igst + ", cgst=" + cgst + ", sgst=" + sgst
				+ ", cess=" + cess + ", getRecords()=" + getRecords()
				+ ", getTableSection()=" + getTableSection()
				+ ", getInvValue()=" + getInvValue() + ", getTaxableValue()="
				+ getTaxableValue() + ", getTaxPayble()=" + getTaxPayble()
				+ ", getIgst()=" + getIgst() + ", getCgst()=" + getCgst()
				+ ", getSgst()=" + getSgst() + ", getCess()=" + getCess()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
}
