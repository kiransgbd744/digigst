package com.ey.advisory.app.data.views.client;

public class Anx1DataStatusSummaryReportView {

	private String date;
	private String supplierGstin;
	private String returnPeriod;
	private String returnType;
	private String tableNumber;
	private String count;
	private String invoiceValue;
	private String taxableValue;
	private String totalTaxes;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSupplierGstin() {
		return supplierGstin;
	}

	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getTotalTaxes() {
		return totalTaxes;
	}

	public void setTotalTaxes(String totalTaxes) {
		this.totalTaxes = totalTaxes;
	}

	public String getIgst() {
		return igst;
	}

	public void setIgst(String igst) {
		this.igst = igst;
	}

	public String getCgst() {
		return cgst;
	}

	public void setCgst(String cgst) {
		this.cgst = cgst;
	}

	public String getSgst() {
		return sgst;
	}

	public void setSgst(String sgst) {
		this.sgst = sgst;
	}

	public String getCess() {
		return cess;
	}

	public void setCess(String cess) {
		this.cess = cess;
	}

	@Override
	public String toString() {
		return "Anx1DataStatusSummaryReportView [date=" + date + ", supplierGstin="
				+ supplierGstin + ", returnPeriod=" + returnPeriod + ", returnType="
				+ returnType + ", tableNumber=" + tableNumber + ", count="
				+ count + ", invoiceValue=" + invoiceValue + ", taxableValue="
				+ taxableValue + ", totalTaxes=" + totalTaxes + ", igst=" + igst
				+ ", cgst=" + cgst + ", sgst=" + sgst + ", cess=" + cess + "]";
	}

}