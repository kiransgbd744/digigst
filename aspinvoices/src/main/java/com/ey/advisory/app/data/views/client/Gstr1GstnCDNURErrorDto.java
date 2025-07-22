/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnCDNURErrorDto {

	private String type;
	private String noteType;
	private String creditdebitNoteNumber;
	private String creditdebitNoteDate;
	private String originalinvoiceNumber;
	private String originalinvoiceDate;
	private String differentialPercentage;
	private String taxableValue;
	private String rate;
	private String integratedAmount;
	private String cess;
	private String invoiceValue;
	private String pregstFlag;
	private String reasonforNote;
	private String errorCode;
	private String gstnErrorMessage;
	private String invoiceNumber;
	private String invoiceDate;
	private String refID;
	private String pos;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNoteType() {
		return noteType;
	}

	public String getRefID() {
		return refID;
	}

	public void setRefID(String refID) {
		this.refID = refID;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
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

	public String getOriginalinvoiceNumber() {
		return originalinvoiceNumber;
	}

	public void setOriginalinvoiceNumber(String originalinvoiceNumber) {
		this.originalinvoiceNumber = originalinvoiceNumber;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	@Override
	public String toString() {
		return "Gstr1GstnCDNURErrorDto [type=" + type + ", noteType=" + noteType
				+ ", creditdebitNoteNumber=" + creditdebitNoteNumber
				+ ", creditdebitNoteDate=" + creditdebitNoteDate
				+ ", originalinvoiceNumber=" + originalinvoiceNumber
				+ ", originalinvoiceDate=" + originalinvoiceDate
				+ ", differentialPercentage=" + differentialPercentage
				+ ", taxableValue=" + taxableValue + ", rate=" + rate
				+ ", integratedAmount=" + integratedAmount + ", cess=" + cess
				+ ", invoiceValue=" + invoiceValue + ", pregstFlag="
				+ pregstFlag + ", reasonforNote=" + reasonforNote
				+ ", errorCode=" + errorCode + ", gstnErrorMessage="
				+ gstnErrorMessage + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceDate=" + invoiceDate + ", refID=" + refID + ", pos="
				+ pos + "]";
	}

}
