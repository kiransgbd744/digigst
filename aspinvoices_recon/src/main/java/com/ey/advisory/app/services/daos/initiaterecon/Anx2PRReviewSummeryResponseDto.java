package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;

/**
 * @author Siva.Nandam
 *
 */
public class Anx2PRReviewSummeryResponseDto {
	
	private String table;
	
	private int count;

	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTaxPayable = BigDecimal.ZERO;

	private BigDecimal tpIGST = BigDecimal.ZERO;

	private BigDecimal tpCGST = BigDecimal.ZERO;

	private BigDecimal tpSGST = BigDecimal.ZERO;

	private BigDecimal tpCess = BigDecimal.ZERO;

	private BigDecimal totalCreditEligible = BigDecimal.ZERO;

	private BigDecimal ceIGST = BigDecimal.ZERO;

	private BigDecimal ceCGST = BigDecimal.ZERO;

	private BigDecimal ceSGST = BigDecimal.ZERO;
	
	private BigDecimal ceCess = BigDecimal.ZERO;
	private String taxDocType;
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


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTotalTaxPayable() {
		return totalTaxPayable;
	}

	public void setTotalTaxPayable(BigDecimal totalTaxPayable) {
		this.totalTaxPayable = totalTaxPayable;
	}

	public BigDecimal getTpIGST() {
		return tpIGST;
	}

	public void setTpIGST(BigDecimal tpIGST) {
		this.tpIGST = tpIGST;
	}

	public BigDecimal getTpCGST() {
		return tpCGST;
	}

	public void setTpCGST(BigDecimal tpCGST) {
		this.tpCGST = tpCGST;
	}

	public BigDecimal getTpSGST() {
		return tpSGST;
	}

	public void setTpSGST(BigDecimal tpSGST) {
		this.tpSGST = tpSGST;
	}

	public BigDecimal getTpCess() {
		return tpCess;
	}

	public void setTpCess(BigDecimal tpCess) {
		this.tpCess = tpCess;
	}

	public BigDecimal getTotalCreditEligible() {
		return totalCreditEligible;
	}

	public void setTotalCreditEligible(BigDecimal totalCreditEligible) {
		this.totalCreditEligible = totalCreditEligible;
	}

	public BigDecimal getCeIGST() {
		return ceIGST;
	}

	public void setCeIGST(BigDecimal ceIGST) {
		this.ceIGST = ceIGST;
	}

	public BigDecimal getCeCGST() {
		return ceCGST;
	}

	public void setCeCGST(BigDecimal ceCGST) {
		this.ceCGST = ceCGST;
	}

	public BigDecimal getCeSGST() {
		return ceSGST;
	}

	public void setCeSGST(BigDecimal ceSGST) {
		this.ceSGST = ceSGST;
	}

	public BigDecimal getCeCess() {
		return ceCess;
	}

	public void setCeCess(BigDecimal ceCess) {
		this.ceCess = ceCess;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Anx2PRReviewSummeryResponseDto [table=" + table + ", count="
				+ count + ", invValue=" + invValue + ", taxableValue="
				+ taxableValue + ", totalTaxPayable=" + totalTaxPayable
				+ ", tpIGST=" + tpIGST + ", tpCGST=" + tpCGST + ", tpSGST="
				+ tpSGST + ", tpCess=" + tpCess + ", totalCreditEligible="
				+ totalCreditEligible + ", ceIGST=" + ceIGST + ", ceCGST="
				+ ceCGST + ", ceSGST=" + ceSGST + ", ceCess=" + ceCess
				+ ", taxDocType=" + taxDocType + "]";
	}

	

	

	
	
	
}

	

