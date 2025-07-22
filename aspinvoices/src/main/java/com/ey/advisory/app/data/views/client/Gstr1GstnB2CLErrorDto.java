/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnB2CLErrorDto {

	private String invoiceType;
	private String invoiceNumber;
	private String invoiceDate;
	private String differentialPercentage;
	private String taxableValue;
	private String rate;
	private String integratedAmount;
	private String cessAmount;
	private String invoiceValue;
	private String placeofSupply;
	private String ecommerceGSTIN;
	private String errorCode;
	private String gstnErrorMessage;
	private String refId;

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
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

	public String getDifferentialPercentage() {
		return differentialPercentage;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
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

	public String getPlaceofSupply() {
		return placeofSupply;
	}

	public void setPlaceofSupply(String placeofSupply) {
		this.placeofSupply = placeofSupply;
	}

	public String getEcommerceGSTIN() {
		return ecommerceGSTIN;
	}

	public void setEcommerceGSTIN(String ecommerceGSTIN) {
		this.ecommerceGSTIN = ecommerceGSTIN;
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

	@Override
	public String toString() {
		return "Gstr1GstnB2CLErrorDto [invoiceType=" + invoiceType + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceDate=" + invoiceDate + ", differentialPercentage=" + differentialPercentage
				+ ", taxableValue=" + taxableValue + ", rate=" + rate + ", integratedAmount=" + integratedAmount
				+ ", cessAmount=" + cessAmount + ", invoiceValue=" + invoiceValue + ", placeofSupply=" + placeofSupply
				+ ", ecommerceGSTIN=" + ecommerceGSTIN + ", errorCode=" + errorCode + ", gstnErrorMessage="
				+ gstnErrorMessage + ", refId=" + refId + "]";
	}

}
