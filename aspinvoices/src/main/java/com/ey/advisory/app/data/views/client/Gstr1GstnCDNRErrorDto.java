/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnCDNRErrorDto {

	private String originalPeriod;
	private String customerGSTINUI;
	private String creditdebitNoteNumber;
	private String creditdebitNoteDate;
	private String originalinvoiceDate;
	private String preceedNum;
	private String preceedDate;
	private String differentialPercentage;
	private String taxableValue;
	private String rate;
	private String integratedAmount;
	private String centralTaxAmount;
	private String stateTaxAmount;
	private String cessAmount;
	private String invoiceValue;
	private String noteType;
	private String pregstFlag;
	private String reasonforNote;
	private String errorCode;
	private String gstnErrorMessage;
	private String originalInvoiceNumber;
	private String refId;
	private String pos;
	private String reverseChargeFlag;
	private String supplyType;

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getOriginalPeriod() {
		return originalPeriod;
	}

	public void setOriginalPeriod(String originalPeriod) {
		this.originalPeriod = originalPeriod;
	}

	public String getOriginalInvoiceNumber() {
		return originalInvoiceNumber;
	}

	public void setOriginalInvoiceNumber(String originalInvoiceNumber) {
		this.originalInvoiceNumber = originalInvoiceNumber;
	}

	public String getPreceedNum() {
		return preceedNum;
	}

	public void setPreceedNum(String preceedNum) {
		this.preceedNum = preceedNum;
	}

	public String getPreceedDate() {
		return preceedDate;
	}

	public void setPreceedDate(String preceedDate) {
		this.preceedDate = preceedDate;
	}

	public String getCustomerGSTINUI() {
		return customerGSTINUI;
	}

	public void setCustomerGSTINUI(String customerGSTINUI) {
		this.customerGSTINUI = customerGSTINUI;
	}

	public String getCreditdebitNoteNumber() {
		return creditdebitNoteNumber;
	}

	public void setCreditdebitNoteNumber(String creditdebitNoteNumber) {
		this.creditdebitNoteNumber = creditdebitNoteNumber;
	}

	public String getCreditdebitNoteDate() {
		return creditdebitNoteDate;
	}

	public void setCreditdebitNoteDate(String creditdebitNoteDate) {
		this.creditdebitNoteDate = creditdebitNoteDate;
	}

	public String getOriginalinvoiceDate() {
		return originalinvoiceDate;
	}

	public void setOriginalinvoiceDate(String originalinvoiceDate) {
		this.originalinvoiceDate = originalinvoiceDate;
	}

	public String getDifferentialPercentage() {
		return differentialPercentage;
	}

	public void setDifferentialPercentage(String differentialPercentage) {
		this.differentialPercentage = differentialPercentage;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getIntegratedAmount() {
		return integratedAmount;
	}

	public void setIntegratedAmount(String integratedAmount) {
		this.integratedAmount = integratedAmount;
	}

	public String getCentralTaxAmount() {
		return centralTaxAmount;
	}

	public void setCentralTaxAmount(String centralTaxAmount) {
		this.centralTaxAmount = centralTaxAmount;
	}

	public String getStateTaxAmount() {
		return stateTaxAmount;
	}

	public void setStateTaxAmount(String stateTaxAmount) {
		this.stateTaxAmount = stateTaxAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getPregstFlag() {
		return pregstFlag;
	}

	public void setPregstFlag(String pregstFlag) {
		this.pregstFlag = pregstFlag;
	}

	public String getReasonforNote() {
		return reasonforNote;
	}

	public void setReasonforNote(String reasonforNote) {
		this.reasonforNote = reasonforNote;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getGstnErrorMessage() {
		return gstnErrorMessage;
	}

	public void setGstnErrorMessage(String gstnErrorMessage) {
		this.gstnErrorMessage = gstnErrorMessage;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getReverseChargeFlag() {
		return reverseChargeFlag;
	}

	public void setReverseChargeFlag(String reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	@Override
	public String toString() {
		return "Gstr1GstnCDNRErrorDto [originalPeriod=" + originalPeriod
				+ ", customerGSTINUI=" + customerGSTINUI
				+ ", creditdebitNoteNumber=" + creditdebitNoteNumber
				+ ", creditdebitNoteDate=" + creditdebitNoteDate
				+ ", originalinvoiceDate=" + originalinvoiceDate
				+ ", preceedNum=" + preceedNum + ", preceedDate=" + preceedDate
				+ ", differentialPercentage=" + differentialPercentage
				+ ", taxableValue=" + taxableValue + ", rate=" + rate
				+ ", integratedAmount=" + integratedAmount
				+ ", centralTaxAmount=" + centralTaxAmount + ", stateTaxAmount="
				+ stateTaxAmount + ", cessAmount=" + cessAmount
				+ ", invoiceValue=" + invoiceValue + ", noteType=" + noteType
				+ ", pregstFlag=" + pregstFlag + ", reasonforNote="
				+ reasonforNote + ", errorCode=" + errorCode
				+ ", gstnErrorMessage=" + gstnErrorMessage
				+ ", originalInvoiceNumber=" + originalInvoiceNumber
				+ ", refId=" + refId + ", pos=" + pos + ", reverseChargeFlag="
				+ reverseChargeFlag + ", supplyType=" + supplyType + "]";
	}

}
