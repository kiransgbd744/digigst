package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class BaseGstr2SummaryEntity {
	
	
	private String supplierGstin;

	//@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedTaxPeriod;

	//@Column(name = "TABLE_SECTION")
	private String tableSection;


	private Integer records;
	//private BigDecimal invValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal taxPayble = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal itcIgst = BigDecimal.ZERO;
	private BigDecimal itcCgst = BigDecimal.ZERO;
	private BigDecimal itcSgst = BigDecimal.ZERO;
	private BigDecimal itcCess = BigDecimal.ZERO;
	/**
	 * @return the supplierGstin
	 */
	public String getSupplierGstin() {
		return supplierGstin;
	}
	/**
	 * @param supplierGstin the supplierGstin to set
	 */
	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}
	/**
	 * @return the derivedTaxPeriod
	 */
	public Integer getDerivedTaxPeriod() {
		return derivedTaxPeriod;
	}
	/**
	 * @param derivedTaxPeriod the derivedTaxPeriod to set
	 */
	public void setDerivedTaxPeriod(Integer derivedTaxPeriod) {
		this.derivedTaxPeriod = derivedTaxPeriod;
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
	 * @return the records
	 */
	public Integer getRecords() {
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(Integer records) {
		this.records = records;
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
	/**
	 * @return the itcIgst
	 */
	public BigDecimal getItcIgst() {
		return itcIgst;
	}
	/**
	 * @param itcIgst the itcIgst to set
	 */
	public void setItcIgst(BigDecimal itcIgst) {
		this.itcIgst = itcIgst;
	}
	/**
	 * @return the itcCgst
	 */
	public BigDecimal getItcCgst() {
		return itcCgst;
	}
	/**
	 * @param itcCgst the itcCgst to set
	 */
	public void setItcCgst(BigDecimal itcCgst) {
		this.itcCgst = itcCgst;
	}
	/**
	 * @return the itcSgst
	 */
	public BigDecimal getItcSgst() {
		return itcSgst;
	}
	/**
	 * @param itcSgst the itcSgst to set
	 */
	public void setItcSgst(BigDecimal itcSgst) {
		this.itcSgst = itcSgst;
	}
	/**
	 * @return the itcCess
	 */
	public BigDecimal getItcCess() {
		return itcCess;
	}
	/**
	 * @param itcCess the itcCess to set
	 */
	public void setItcCess(BigDecimal itcCess) {
		this.itcCess = itcCess;
	}
	
	
	
	
}
