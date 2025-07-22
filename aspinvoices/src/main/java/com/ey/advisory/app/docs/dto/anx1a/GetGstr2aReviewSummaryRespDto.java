package com.ey.advisory.app.docs.dto.anx1a;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GetGstr2aReviewSummaryRespDto {

	private String taxDocType;
	private String table;

	private BigInteger count = BigInteger.ZERO;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal taxPayble = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;

	public String getTaxDocType() {
		return taxDocType;
	}

	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTaxPayble() {
		return taxPayble;
	}

	public void setTaxPayble(BigDecimal taxPayble) {
		this.taxPayble = taxPayble;
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

}
