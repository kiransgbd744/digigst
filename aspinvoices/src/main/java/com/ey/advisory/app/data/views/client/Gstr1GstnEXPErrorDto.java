/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnEXPErrorDto {

	private String invoiceNumber;
	private String invoiceDate;
	private String shippingbillNumber;
	private String shippingbillDate;
	private String portCode;
	private String differentialPercentage;
	private String taxableValue;
	private String rate;
	private String integratedAmount;
	private String cess;
	private String invoiceValue;
	private String expType;
	private String errorCode;
	private String gstnErrorMessage;
	private String refId;

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
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

	public String getShippingbillNumber() {
		return shippingbillNumber;
	}

	public void setShippingbillNumber(String shippingbillNumber) {
		this.shippingbillNumber = shippingbillNumber;
	}

	public String getShippingbillDate() {
		return shippingbillDate;
	}

	public void setShippingbillDate(String shippingbillDate) {
		this.shippingbillDate = shippingbillDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
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

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
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
		return "Gstr1GstnEXPErrorDto [invoiceNumber=" + invoiceNumber + ", invoiceDate=" + invoiceDate
				+ ", shippingbillNumber=" + shippingbillNumber + ", shippingbillDate=" + shippingbillDate
				+ ", portCode=" + portCode + ", differentialPercentage=" + differentialPercentage + ", taxableValue="
				+ taxableValue + ", rate=" + rate + ", integratedAmount=" + integratedAmount + ", cess=" + cess
				+ ", invoiceValue=" + invoiceValue + ", expType=" + expType + ", errorCode=" + errorCode
				+ ", gstnErrorMessage=" + gstnErrorMessage + ", refId=" + refId + "]";
	}

}
