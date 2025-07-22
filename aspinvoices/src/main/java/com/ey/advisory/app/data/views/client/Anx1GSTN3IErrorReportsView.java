package com.ey.advisory.app.data.views.client;

public class Anx1GSTN3IErrorReportsView {

	private String recipientGSTIN;
	private String pos;
	private String hsn;
	private String taxRate;
	private String taxableValue;
	private String integTaxAmt;
	private String cessAmt;
	private String diffPerFlag;
	private String autoPopltToRefund;
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
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
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
	public String getCessAmt() {
		return cessAmt;
	}
	public void setCessAmt(String cessAmt) {
		this.cessAmt = cessAmt;
	}
	public String getDiffPerFlag() {
		return diffPerFlag;
	}
	public void setDiffPerFlag(String diffPerFlag) {
		this.diffPerFlag = diffPerFlag;
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
		return "Anx1GSTN3IErrorReportsView [recipientGSTIN=" + recipientGSTIN
				+ ", pos=" + pos + ", hsn=" + hsn + ", taxRate=" + taxRate
				+ ", taxableValue=" + taxableValue + ", integTaxAmt="
				+ integTaxAmt + ", cessAmt=" + cessAmt + ", diffPerFlag="
				+ diffPerFlag + ", autoPopltToRefund=" + autoPopltToRefund
				+ ", gSTNErrorCode=" + gSTNErrorCode + ", gSTNErrorMessage="
				+ gSTNErrorMessage + ", refID=" + refID + ", refDate=" + refDate
				+ "]";
	}
	
	
}
