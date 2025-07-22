package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1B2BGstnRespDto {
	
	
	@Expose
	@SerializedName("records")
	protected int records = 0;;
	@Expose
	@SerializedName("taxPayble")
	protected BigDecimal taxPayble = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("invValue")
	protected BigDecimal invValue = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("taxableValue")
	protected BigDecimal TaxableValue = BigDecimal.ZERO;

	@Expose
	@SerializedName("igst")
	protected BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cgst")
	protected BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("sgst")
	protected BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cess")
	protected BigDecimal cess = BigDecimal.ZERO;

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
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return TaxableValue;
	}

	/**
	 * @param taxableValue the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		TaxableValue = taxableValue;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetGstn1B2bInvoicesRespDto [records=" + records + ", taxPayble="
				+ taxPayble + ", invValue=" + invValue + ", TaxableValue="
				+ TaxableValue + ", igst=" + igst + ", cgst=" + cgst + ", sgst="
				+ sgst + ", cess=" + cess + "]";
	}
	
}
