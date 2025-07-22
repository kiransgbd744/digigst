/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnB2CSErrorDto {

	private String placeofSupply;
	private String supplyType;
	private String type;
	private String rate;
	private String differentialPercentage;
	private String taxableValue;
	private String integratedAmount;
	private String centralTaxAmount;
	private String stateTaxAmount;
	private String cessAmount;
	private String ecommerceGSTIN;
	private String errorCode;
	private String gstnErrorMessage;
	private String refId;

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getPlaceofSupply() {
		return placeofSupply;
	}

	public void setPlaceofSupply(String placeofSupply) {
		this.placeofSupply = placeofSupply;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
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
		return "Gstr1GstnB2CSErrorDto [placeofSupply=" + placeofSupply + ", supplyType=" + supplyType + ", type=" + type
				+ ", rate=" + rate + ", differentialPercentage=" + differentialPercentage + ", taxableValue="
				+ taxableValue + ", integratedAmount=" + integratedAmount + ", centralTaxAmount=" + centralTaxAmount
				+ ", stateTaxAmount=" + stateTaxAmount + ", cessAmount=" + cessAmount + ", ecommerceGSTIN="
				+ ecommerceGSTIN + ", errorCode=" + errorCode + ", gstnErrorMessage=" + gstnErrorMessage + ", refId="
				+ refId + "]";
	}

}
