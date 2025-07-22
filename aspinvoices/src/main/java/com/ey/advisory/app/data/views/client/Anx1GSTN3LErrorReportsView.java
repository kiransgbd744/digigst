/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Anx1GSTN3LErrorReportsView {

	private String recipientGSTIN;
	private String supplierGSTIN;
	private String tableReference;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String hsn;
	private String taxRate;
	private String taxableValue;
	private String integTaxAmt;
	private String centralTaxAmt;
	private String stateUtTaxAmt;
	private String cessAmt;
	private String invoiceValue;
	private String pos;
	private String diffPerFlag;
	private String section7ofIGSTFlag;
	private String claimRefundFlag;
	private String gSTNErrorCode;
	private String gSTNErrorMessage;
	private String refID;
	private String refDate;

	public String getRecipientGSTIN() {
		return recipientGSTIN;
	}

	public void setRecipientGSTIN(String recipientGSTIN) {
		this.recipientGSTIN = recipientGSTIN;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getTableReference() {
		return tableReference;
	}

	public void setTableReference(String tableReference) {
		this.tableReference = tableReference;
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

	public String getIntegTaxAmt() {
		return integTaxAmt;
	}

	public void setIntegTaxAmt(String integTaxAmt) {
		this.integTaxAmt = integTaxAmt;
	}

	public String getCentralTaxAmt() {
		return centralTaxAmt;
	}

	public void setCentralTaxAmt(String centralTaxAmt) {
		this.centralTaxAmt = centralTaxAmt;
	}

	public String getStateUtTaxAmt() {
		return stateUtTaxAmt;
	}

	public void setStateUtTaxAmt(String stateUtTaxAmt) {
		this.stateUtTaxAmt = stateUtTaxAmt;
	}

	public String getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(String cessAmt) {
		this.cessAmt = cessAmt;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getDiffPerFlag() {
		return diffPerFlag;
	}

	public void setDiffPerFlag(String diffPerFlag) {
		this.diffPerFlag = diffPerFlag;
	}

	public String getSection7ofIGSTFlag() {
		return section7ofIGSTFlag;
	}

	public void setSection7ofIGSTFlag(String section7ofIGSTFlag) {
		this.section7ofIGSTFlag = section7ofIGSTFlag;
	}

	public String getClaimRefundFlag() {
		return claimRefundFlag;
	}

	public void setClaimRefundFlag(String claimRefundFlag) {
		this.claimRefundFlag = claimRefundFlag;
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
		return "Anx1GSTN3LErrorReportsView [recipientGSTIN=" + recipientGSTIN
				+ ", supplierGSTIN=" + supplierGSTIN + ", tableReference="
				+ tableReference + ", documentType=" + documentType
				+ ", documentNumber=" + documentNumber + ", documentDate="
				+ documentDate + ", hsn=" + hsn + ", taxRate=" + taxRate
				+ ", taxableValue=" + taxableValue + ", integTaxAmt="
				+ integTaxAmt + ", centralTaxAmt=" + centralTaxAmt
				+ ", stateUtTaxAmt=" + stateUtTaxAmt + ", cessAmt=" + cessAmt
				+ ", invoiceValue=" + invoiceValue + ", pos=" + pos
				+ ", diffPerFlag=" + diffPerFlag + ", section7ofIGSTFlag="
				+ section7ofIGSTFlag + ", claimRefundFlag=" + claimRefundFlag
				+ ", gSTNErrorCode=" + gSTNErrorCode + ", gSTNErrorMessage="
				+ gSTNErrorMessage + ", refID=" + refID + ", refDate=" + refDate
				+ "]";
	}

}
