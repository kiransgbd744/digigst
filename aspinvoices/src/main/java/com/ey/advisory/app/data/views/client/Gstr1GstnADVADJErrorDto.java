/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnADVADJErrorDto {

	private String supplyType;
	private String placeofSupply;
	private String differentialPercentage;
	private String advanceAmount;
	private String rate;
	private String integratedAmount;
	private String centralTaxAmount;
	private String stateTaxAmount;
	private String cessAmount;
	private String errorCode;
	private String gstnErrorMessage;

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getPlaceofSupply() {
		return placeofSupply;
	}

	public void setPlaceofSupply(String placeofSupply) {
		this.placeofSupply = placeofSupply;
	}

	public String getDifferentialPercentage() {
		return differentialPercentage;
	}

	public void setDifferentialPercentage(String differentialPercentage) {
		this.differentialPercentage = differentialPercentage;
	}

	public String getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(String advanceAmount) {
		this.advanceAmount = advanceAmount;
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
		return "Gstr1GstnADVADJErrorDto [supplyType=" + supplyType
				+ ", placeofSupply=" + placeofSupply
				+ ", differentialPercentage=" + differentialPercentage
				+ ", advanceAmount=" + advanceAmount + ", rate=" + rate
				+ ", integratedAmount=" + integratedAmount
				+ ", centralTaxAmount=" + centralTaxAmount + ", stateTaxAmount="
				+ stateTaxAmount + ", cessAmount=" + cessAmount + ", errorCode="
				+ errorCode + ", gstnErrorMessage=" + gstnErrorMessage + "]";
	}

}
