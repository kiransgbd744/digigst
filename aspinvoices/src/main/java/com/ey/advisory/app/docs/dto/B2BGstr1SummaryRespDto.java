package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

public class B2BGstr1SummaryRespDto {
	private Long id;
	private Long recordCount;
	private String suuplierGstin;
	private String returnPeriod;
	private BigDecimal taxableValue;
	private String taxDocType;
	private String tableSection;
	private BigDecimal taxRate;
	private BigDecimal invoiceValue;
	private BigDecimal igstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal cgstAmt;
	private BigDecimal cessAmt;
	private BigDecimal taxPayable;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the recordCount
	 */
	public Long getRecordCount() {
		return recordCount;
	}
	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}
	/**
	 * @return the suuplierGstin
	 */
	public String getSuuplierGstin() {
		return suuplierGstin;
	}
	/**
	 * @param suuplierGstin the suuplierGstin to set
	 */
	public void setSuuplierGstin(String suuplierGstin) {
		this.suuplierGstin = suuplierGstin;
	}
	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}
	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
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
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}
	/**
	 * @param taxDocType the taxDocType to set
	 */
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
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
	 * @return the taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	/**
	 * @return the invoiceValue
	 */
	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}
	/**
	 * @param invoiceValue the invoiceValue to set
	 */
	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	/**
	 * @return the igstAmt
	 */
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}
	/**
	 * @param igstAmt the igstAmt to set
	 */
	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}
	/**
	 * @return the sgstAmt
	 */
	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}
	/**
	 * @param sgstAmt the sgstAmt to set
	 */
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}
	/**
	 * @return the cgstAmt
	 */
	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}
	/**
	 * @param cgstAmt the cgstAmt to set
	 */
	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}
	/**
	 * @return the cessAmt
	 */
	public BigDecimal getCessAmt() {
		return cessAmt;
	}
	/**
	 * @param cessAmt the cessAmt to set
	 */
	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}
	/**
	 * @return the taxPayable
	 */
	public BigDecimal getTaxPayable() {
		return taxPayable;
	}
	/**
	 * @param taxPayable the taxPayable to set
	 */
	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
	}
}
