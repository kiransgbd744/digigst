package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;

/**
 * @author Siva.Nandam
 *
 */
public class Anx2BigTableResponseDto {

private String table;
	
	private int count;

	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTaxPayable = BigDecimal.ZERO;

	private BigDecimal tpIGST = BigDecimal.ZERO;

	private BigDecimal tpCGST = BigDecimal.ZERO;

	private BigDecimal tpSGST = BigDecimal.ZERO;

	private BigDecimal tpCess = BigDecimal.ZERO;

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

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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
	 * @return the totalTaxPayable
	 */
	public BigDecimal getTotalTaxPayable() {
		return totalTaxPayable;
	}

	/**
	 * @param totalTaxPayable the totalTaxPayable to set
	 */
	public void setTotalTaxPayable(BigDecimal totalTaxPayable) {
		this.totalTaxPayable = totalTaxPayable;
	}

	/**
	 * @return the tpIGST
	 */
	public BigDecimal getTpIGST() {
		return tpIGST;
	}

	/**
	 * @param tpIGST the tpIGST to set
	 */
	public void setTpIGST(BigDecimal tpIGST) {
		this.tpIGST = tpIGST;
	}

	/**
	 * @return the tpCGST
	 */
	public BigDecimal getTpCGST() {
		return tpCGST;
	}

	/**
	 * @param tpCGST the tpCGST to set
	 */
	public void setTpCGST(BigDecimal tpCGST) {
		this.tpCGST = tpCGST;
	}

	/**
	 * @return the tpSGST
	 */
	public BigDecimal getTpSGST() {
		return tpSGST;
	}

	/**
	 * @param tpSGST the tpSGST to set
	 */
	public void setTpSGST(BigDecimal tpSGST) {
		this.tpSGST = tpSGST;
	}

	/**
	 * @return the tpCess
	 */
	public BigDecimal getTpCess() {
		return tpCess;
	}

	/**
	 * @param tpCess the tpCess to set
	 */
	public void setTpCess(BigDecimal tpCess) {
		this.tpCess = tpCess;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Anx2BigTableResponseDto [table=" + table + ", count=" + count
				+ ", invValue=" + invValue + ", taxableValue=" + taxableValue
				+ ", totalTaxPayable=" + totalTaxPayable + ", tpIGST=" + tpIGST
				+ ", tpCGST=" + tpCGST + ", tpSGST=" + tpSGST + ", tpCess="
				+ tpCess + "]";
	}

	
	
}
