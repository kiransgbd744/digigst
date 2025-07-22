/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1SummarySectionDto {

	private Integer records = 0;

	private String taxDocType;
	private String recordType;
	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal taxPayable = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;

	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;

	
//	private String taxDocType;
	private Integer aspCount = 0;
	private BigDecimal aspInvoiceValue = BigDecimal.ZERO;
	private BigDecimal aspTaxableValue = BigDecimal.ZERO;
	private BigDecimal aspTaxPayble = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;
	
	private Integer gstnCount = 0;
	private BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxableValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;
  
	private Integer diffCount = 0;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffTaxPayble = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;
	
	
	
	
	
	public Gstr1SummarySectionDto() {
	}

	public Gstr1SummarySectionDto(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	/**
	 * @param records
	 * @param taxDocType
	 * @param invValue
	 * @param taxableValue
	 * @param taxPayble
	 * @param igst
	 * @param cgst
	 * @param sgst
	 * @param cess
	 */
	public Gstr1SummarySectionDto(Integer records, String taxDocType,
			BigDecimal invValue, BigDecimal taxableValue, BigDecimal taxPayable,
			BigDecimal igst, BigDecimal cgst, BigDecimal sgst,
			BigDecimal cess) {
		Integer count = records;
		this.taxDocType = taxDocType;
		this.records = (count != null) ? count : 0;
		this.taxableValue = (taxableValue != null) ? taxableValue
				: BigDecimal.ZERO;
		this.taxPayable = (taxPayable != null) ? taxPayable : BigDecimal.ZERO;
		this.invValue = (invValue != null) ? invValue : BigDecimal.ZERO;
		this.igst = (igst != null) ? igst : BigDecimal.ZERO;
		this.cgst = (cgst != null) ? cgst : BigDecimal.ZERO;
		this.sgst = (sgst != null) ? sgst : BigDecimal.ZERO;
		this.cess = (cess != null) ? cess : BigDecimal.ZERO;
	}

	/**
	 * Add another section summary object to this object. Make sure that the
	 * sections of both objects match. Otherwise we wil get an object that sums
	 * up multiple sections.
	 * 
	 * @param other
	 * @return
	 */
	public Gstr1SummarySectionDto add(Gstr1SummarySectionDto other) {
		this.records = this.records + other.records;
		this.taxDocType = other.taxDocType;
		this.taxableValue = this.taxableValue.add(other.taxableValue);
		this.taxPayable = this.taxPayable.add(other.taxPayable);
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
		this.invValue = invValue != null ? invValue : BigDecimal.ZERO;
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
	 * @return the taxPayable
	 */
	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	/**
	 * @param taxPayable
	 *            the taxPayable to set
	 */
	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable != null ? taxPayable : BigDecimal.ZERO;
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

	
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@Override
	public String toString() {
		return "Gstr1BasicSummarySectionDto [records=" + records
				+ ", taxDocType=" + taxDocType + ", invValue=" + invValue
				+ ", taxableValue=" + taxableValue + ", taxPayable="
				+ taxPayable + ", igst=" + igst + ", cgst=" + cgst + ", sgst="
				+ sgst + ", cess=" + cess + ", getRecords()=" + getRecords()
				+ ", getTaxDocType()=" + getTaxDocType() + ", getInvValue()="
				+ getInvValue() + ", getTaxableValue()=" + getTaxableValue()
				+ ", getTaxPayable()=" + getTaxPayable() + ", getIgst()="
				+ getIgst() + ", getCgst()=" + getCgst() + ", getSgst()="
				+ getSgst() + ", getCess()=" + getCess() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public Integer getAspCount() {
		return aspCount;
	}

	public void setAspCount(Integer aspCount) {
		this.aspCount = aspCount;
	}

	public BigDecimal getAspInvoiceValue() {
		return aspInvoiceValue;
	}

	public void setAspInvoiceValue(BigDecimal aspInvoiceValue) {
		this.aspInvoiceValue = aspInvoiceValue;
	}

	public BigDecimal getAspTaxableValue() {
		return aspTaxableValue;
	}

	public void setAspTaxableValue(BigDecimal aspTaxableValue) {
		this.aspTaxableValue = aspTaxableValue;
	}

	public BigDecimal getAspTaxPayble() {
		return aspTaxPayble;
	}

	public void setAspTaxPayble(BigDecimal aspTaxPayble) {
		this.aspTaxPayble = aspTaxPayble;
	}

	public BigDecimal getAspIgst() {
		return aspIgst;
	}

	public void setAspIgst(BigDecimal aspIgst) {
		this.aspIgst = aspIgst;
	}

	public BigDecimal getAspCgst() {
		return aspCgst;
	}

	public void setAspCgst(BigDecimal aspCgst) {
		this.aspCgst = aspCgst;
	}

	public BigDecimal getAspSgst() {
		return aspSgst;
	}

	public void setAspSgst(BigDecimal aspSgst) {
		this.aspSgst = aspSgst;
	}

	public BigDecimal getAspCess() {
		return aspCess;
	}

	public void setAspCess(BigDecimal aspCess) {
		this.aspCess = aspCess;
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

	public Integer getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}

	public BigDecimal getDiffInvoiceValue() {
		return diffInvoiceValue;
	}

	public void setDiffInvoiceValue(BigDecimal diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}

	public BigDecimal getDiffTaxableValue() {
		return diffTaxableValue;
	}

	public void setDiffTaxableValue(BigDecimal diffTaxableValue) {
		this.diffTaxableValue = diffTaxableValue;
	}

	public BigDecimal getDiffTaxPayble() {
		return diffTaxPayble;
	}

	public void setDiffTaxPayble(BigDecimal diffTaxPayble) {
		this.diffTaxPayble = diffTaxPayble;
	}

	public BigDecimal getDiffIgst() {
		return diffIgst;
	}

	public void setDiffIgst(BigDecimal diffIgst) {
		this.diffIgst = diffIgst;
	}

	public BigDecimal getDiffCgst() {
		return diffCgst;
	}

	public void setDiffCgst(BigDecimal diffCgst) {
		this.diffCgst = diffCgst;
	}

	public BigDecimal getDiffSgst() {
		return diffSgst;
	}

	public void setDiffSgst(BigDecimal diffSgst) {
		this.diffSgst = diffSgst;
	}

	public BigDecimal getDiffCess() {
		return diffCess;
	}

	public void setDiffCess(BigDecimal diffCess) {
		this.diffCess = diffCess;
	}

}
