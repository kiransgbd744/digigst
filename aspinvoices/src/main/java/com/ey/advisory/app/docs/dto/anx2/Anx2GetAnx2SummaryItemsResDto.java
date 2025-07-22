package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

public class Anx2GetAnx2SummaryItemsResDto {

	private String table;

	private String docType;

	private int records;

	private BigDecimal invoiceValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTax = BigDecimal.ZERO;

	private BigDecimal igst = BigDecimal.ZERO;

	private BigDecimal cgst = BigDecimal.ZERO;

	private BigDecimal sgst = BigDecimal.ZERO;

	private BigDecimal cess = BigDecimal.ZERO;

	public Anx2GetAnx2SummaryItemsResDto() {
		super();

	}

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

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
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

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
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

	@Override
	public String toString() {
		return "Anx2GetAnx2SummaryItemsResDto [table=" + table + ", docType="
				+ docType + ", records=" + records + ", invoiceValue="
				+ invoiceValue + ", taxableValue=" + taxableValue
				+ ", totalTax=" + totalTax + ", igst=" + igst + ", cgst=" + cgst
				+ ", sgst=" + sgst + ", cess=" + cess + "]";
	}

}