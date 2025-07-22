package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2AReviewSumRecordsReqDto {
	@Expose
	@SerializedName("table")
	private String table;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("count")
	private BigInteger count;

	@Expose
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("taxPayable")
	private BigDecimal taxPayable;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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

	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
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
