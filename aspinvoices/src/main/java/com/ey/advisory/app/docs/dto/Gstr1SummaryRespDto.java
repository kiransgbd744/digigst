package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

/**
 * This class is responsible for sending the response data to Review Summary
 * View
 * 
 * @author Balakrishna.S
 *
 */
public class Gstr1SummaryRespDto {
	
	private int records = 0;;
	
	private String tableSection;
	
	private BigDecimal invValue = BigDecimal.ZERO;
	
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	private BigDecimal taxPayble = BigDecimal.ZERO;
	
	private BigDecimal igst = BigDecimal.ZERO;
	
	private BigDecimal cgst = BigDecimal.ZERO;
	
	private BigDecimal sgst = BigDecimal.ZERO;
	
	private BigDecimal cess = BigDecimal.ZERO;

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

}
