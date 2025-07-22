package com.ey.advisory.app.data.views.client;

public class Gstr6CDNGstnErrorDto {

	private String supplierGSTIN;
	private String noteType;
	private String documentNumber;
	private String documentDate;
	private String invoiceNumber;
	private String invoiceDate;
	private String lineNo;
	private String rate;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String refId;
	private String refIdDateTime;
	private String errorCode;
	private String errorMessage;
	private String pos;
	
	

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
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

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "Gstr6CDNGstnErrorDto [supplierGSTIN=" + supplierGSTIN
				+ ", noteType=" + noteType + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate
				+ ", invoiceNumber=" + invoiceNumber + ", invoiceDate="
				+ invoiceDate + ", lineNo=" + lineNo + ", rate=" + rate
				+ ", taxableValue=" + taxableValue + ", igst=" + igst
				+ ", cgst=" + cgst + ", sgst=" + sgst + ", cess=" + cess
				+ ", refId=" + refId + ", refIdDateTime=" + refIdDateTime
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ ", pos=" + pos + "]";
	}

}
