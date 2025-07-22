package com.ey.advisory.app.data.views.client;

public class Gstr6B2BGstnErrorDto {

	private String supplierGSTIN;
	private String documentNumber;
	private String documentDate;
	private String lineNo;
	private String rate;
	private String pos;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String invoiceValue;
	private String refId;
	private String refIdDateTime;
	private String errorCode;
	private String errorMessage;

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
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

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getRefIdDateTime() {
		return refIdDateTime;
	}

	public void setRefIdDateTime(String refIdDateTime) {
		this.refIdDateTime = refIdDateTime;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "Gstr6B2BGstnErrorDto [supplierGSTIN=" + supplierGSTIN + ", documentNumber=" + documentNumber
				+ ", documentDate=" + documentDate + ", lineNo=" + lineNo + ", rate=" + rate + ", pos=" + pos
				+ ", taxableValue=" + taxableValue + ", igst=" + igst + ", cgst=" + cgst + ", sgst=" + sgst + ", cess="
				+ cess + ", invoiceValue=" + invoiceValue + ", refId=" + refId + ", refIdDateTime=" + refIdDateTime
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}

}
