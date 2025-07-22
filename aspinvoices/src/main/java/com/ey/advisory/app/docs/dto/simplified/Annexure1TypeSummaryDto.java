package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class Annexure1TypeSummaryDto {

	
	private BigDecimal invoiceValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal taxPayble = BigDecimal.ZERO;
	
	private BigDecimal igst = BigDecimal.ZERO;

	private BigDecimal cgst = BigDecimal.ZERO;
	
	private BigDecimal sgst = BigDecimal.ZERO;
	
	private BigDecimal cess = BigDecimal.ZERO;

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
