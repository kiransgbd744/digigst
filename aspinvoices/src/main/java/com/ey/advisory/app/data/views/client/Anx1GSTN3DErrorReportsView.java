package com.ey.advisory.app.data.views.client;

public class Anx1GSTN3DErrorReportsView {
	
	private String supplierGSTIN;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String portCode;
	private String shippingBillNumber;
	private String shippingBillDate;
	private String hsn;
	private String taxRate;
	private String taxableValue;
	private String invoiceValue;
	private String autoPopltToRefund;
	private String gSTNErrorCode;
	private String gSTNErrorMessage;
	private String refID;
	private String refDate;
	
	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}
	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
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
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getShippingBillNumber() {
		return shippingBillNumber;
	}
	public void setShippingBillNumber(String shippingBillNumber) {
		this.shippingBillNumber = shippingBillNumber;
	}
	public String getShippingBillDate() {
		return shippingBillDate;
	}
	public void setShippingBillDate(String shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}
	public String getHsn() {
		return hsn;
	}
	public void setHsn(String hsn) {
		this.hsn = hsn;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public String getTaxableValue() {
		return taxableValue;
	}
	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}
	public String getInvoiceValue() {
		return invoiceValue;
	}
	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public String getAutoPopltToRefund() {
		return autoPopltToRefund;
	}
	public void setAutoPopltToRefund(String autoPopltToRefund) {
		this.autoPopltToRefund = autoPopltToRefund;
	}
	public String getgSTNErrorCode() {
		return gSTNErrorCode;
	}
	public void setgSTNErrorCode(String gSTNErrorCode) {
		this.gSTNErrorCode = gSTNErrorCode;
	}
	public String getgSTNErrorMessage() {
		return gSTNErrorMessage;
	}
	public void setgSTNErrorMessage(String gSTNErrorMessage) {
		this.gSTNErrorMessage = gSTNErrorMessage;
	}
	public String getRefID() {
		return refID;
	}
	public void setRefID(String refID) {
		this.refID = refID;
	}
	public String getRefDate() {
		return refDate;
	}
	public void setRefDate(String refDate) {
		this.refDate = refDate;
	}
	@Override
	public String toString() {
		return "Anx1GSTN3DErrorReportsView [supplierGSTIN=" + supplierGSTIN
				+ ", documentType=" + documentType + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate
				+ ", portCode=" + portCode + ", shippingBillNumber="
				+ shippingBillNumber + ", shippingBillDate=" + shippingBillDate
				+ ", hsn=" + hsn + ", taxRate=" + taxRate + ", taxableValue="
				+ taxableValue + ", invoiceValue=" + invoiceValue
				+ ", autoPopltToRefund=" + autoPopltToRefund
				+ ", gSTNErrorCode=" + gSTNErrorCode + ", gSTNErrorMessage="
				+ gSTNErrorMessage + ", refID=" + refID + ", refDate=" + refDate
				+ "]";
	}
	
}
