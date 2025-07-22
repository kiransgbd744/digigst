package com.ey.advisory.controller;

//To-DO This class is To be removed
import java.math.BigDecimal;

public class TestDto {
	
	private String table;
	private String tableSection;
	private int records;
	private BigDecimal taxableValue;
	private BigDecimal taxPayble;
	private BigDecimal invValue;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal diff;
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
	 * @return the diff
	 */
	public BigDecimal getDiff() {
		return diff;
	}
	/**
	 * @param diff the diff to set
	 */
	public void setDiff(BigDecimal diff) {
		this.diff = diff;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"TestDto [table=%s, tableSection=%s, records=%s, "
				+ "taxableValue=%s, taxPayble=%s, invValue=%s, igst=%s, "
				+ "cgst=%s, sgst=%s, cess=%s, diff=%s]",
				table, tableSection, records, taxableValue, taxPayble, invValue,
				igst, cgst, sgst, cess, diff);
	}

}
