package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

/**
 * 
 * @author Anand3.M
 *
 */

public class Anx2GetGstinSummaryDetailsResData {

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal igst = BigDecimal.ZERO;

	private BigDecimal cgst = BigDecimal.ZERO;

	private BigDecimal sgst = BigDecimal.ZERO;

	private BigDecimal cess = BigDecimal.ZERO;
	
	private BigDecimal isdTaxableValue = BigDecimal.ZERO;

	private BigDecimal isdIgst = BigDecimal.ZERO;

	private BigDecimal isdCgst = BigDecimal.ZERO;

	private BigDecimal isdSgst = BigDecimal.ZERO;

	private BigDecimal isdCess = BigDecimal.ZERO;

	public Anx2GetGstinSummaryDetailsResData() {
		super();

	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public BigDecimal getIsdTaxableValue() {
		return isdTaxableValue;
	}

	public void setIsdTaxableValue(BigDecimal isdTaxableValue) {
		this.isdTaxableValue = isdTaxableValue;
	}

	public BigDecimal getIsdIgst() {
		return isdIgst;
	}

	public void setIsdIgst(BigDecimal isdIgst) {
		this.isdIgst = isdIgst;
	}

	public BigDecimal getIsdCgst() {
		return isdCgst;
	}

	public void setIsdCgst(BigDecimal isdCgst) {
		this.isdCgst = isdCgst;
	}

	public BigDecimal getIsdSgst() {
		return isdSgst;
	}

	public void setIsdSgst(BigDecimal isdSgst) {
		this.isdSgst = isdSgst;
	}

	public BigDecimal getIsdCess() {
		return isdCess;
	}

	public void setIsdCess(BigDecimal isdCess) {
		this.isdCess = isdCess;
	}
}
