package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */

public class Gstr2BasicSummarySectionDto {
	
	
	private Integer records;
	private String tableSection;
	private BigDecimal invValue = BigDecimal.ZERO;
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
	public Gstr2BasicSummarySectionDto(String tableSection) {
		// TODO Auto-generated constructor stub
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
	
	
/*	public Gstr2BasicSummarySectionDto(String section, BigDecimal recordCount,
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
		this.itcIgst = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
		this.cess = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
		this.cess = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
		this.cess = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;
	}

*/	
	
	
	
	public Gstr2BasicSummarySectionDto add(Gstr2BasicSummarySectionDto other) {
		this.records = this.records + other.records;
		this.tableSection = other.tableSection;
		this.igst = this.igst.add(other.igst);
		this.sgst = this.sgst.add(other.sgst);
		this.cgst = this.cgst.add(other.cgst);
		this.cess = this.cess.add(other.cess);
		this.itcIgst = this.itcIgst.add(other.itcIgst);
		this.itcSgst = this.itcSgst.add(other.itcSgst);
		this.itcCgst = this.itcCgst.add(other.itcCgst);
		this.itcCess = this.itcCess.add(other.itcCess);
		return this;
	}
	
	public Gstr2BasicSummarySectionDto() {
		super();
	}
	/*public Gstr2BasicSummarySectionDto(String tableSection,
			BigDecimal recordCount,
			BigDecimal taxbleValue,BigDecimal igstAmt,
			BigDecimal cgstAmt, BigDecimal sgstAmt, BigDecimal cessAmt,
			BigDecimal itcIgst, BigDecimal itcSgst, BigDecimal itcCgst,
			BigDecimal itcCess) {
		// TODO Auto-generated constructor stub
		
		
		Integer count = recordCount.intValue();
		this.tableSection = tableSection;
		this.records = (count != null) ? count : 0;
		this.taxableValue = (taxableValue != null) ? taxableValue
				: BigDecimal.ZERO;
		this.taxPayble = (taxPayble != null) ? taxPayble : BigDecimal.ZERO;
		this.invValue = (invValue != null) ? invValue : BigDecimal.ZERO;
		this.igst = (igst != null) ? igst : BigDecimal.ZERO;
		this.cgst = (cgst != null) ? cgst : BigDecimal.ZERO;
		this.sgst = (sgst != null) ? sgst : BigDecimal.ZERO;
		this.cess = (cess != null) ? cess : BigDecimal.ZERO;
		this.itcIgst = (itcIgst != null) ? itcIgst : BigDecimal.ZERO;
		this.itcCgst = (itcCgst != null) ? itcCgst : BigDecimal.ZERO;
		this.itcSgst = (itcSgst != null) ? itcSgst : BigDecimal.ZERO;
		this.itcCess = (itcCess != null) ? itcCess : BigDecimal.ZERO;

	}
	
*/
	public Gstr2BasicSummarySectionDto(String tableSection, Integer records,
			BigDecimal taxableValue, BigDecimal igst, BigDecimal cgst,
			BigDecimal sgst, BigDecimal cess, BigDecimal itcIgst,
			BigDecimal itcSgst, BigDecimal itcCgst, BigDecimal itcCess) {
		
		
		Integer count = records.intValue();
		this.tableSection = tableSection;
		this.records = (count != null) ? count : 0;
		this.taxableValue = (taxableValue != null) ? taxableValue
				: BigDecimal.ZERO;
		this.igst = (igst != null) ? igst : BigDecimal.ZERO;
		this.cgst = (cgst != null) ? cgst : BigDecimal.ZERO;
		this.sgst = (sgst != null) ? sgst : BigDecimal.ZERO;
		this.cess = (cess != null) ? cess : BigDecimal.ZERO;
		this.itcIgst = (itcIgst != null) ? itcIgst : BigDecimal.ZERO;
		this.itcCgst = (itcCgst != null) ? itcCgst : BigDecimal.ZERO;
		this.itcSgst = (itcSgst != null) ? itcSgst : BigDecimal.ZERO;
		this.itcCess = (itcCess != null) ? itcCess : BigDecimal.ZERO;

		
		
		// TODO Auto-generated constructor stub
	}	

}
