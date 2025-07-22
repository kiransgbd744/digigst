/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnHSNSUMErrorDto {

	private String serialNumber;
	private String hsnSac;
	private String descriptionofGoodsSold;
	private String uqc;
	private String quantity;
	private String taxableValue;
	private String integratedAmount;
	private String centralTaxAmount;
	private String stateTaxAmount;
	private String cessAmount;
	private String totalValue;
	private String errorCode;
	private String gstnErrorMessage;
	private String taxRate;
	private String refid;
	private String recordtype;
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getHsnSac() {
		return hsnSac;
	}

	public void setHsnSac(String hsnSac) {
		this.hsnSac = hsnSac;
	}

	public String getDescriptionofGoodsSold() {
		return descriptionofGoodsSold;
	}

	public void setDescriptionofGoodsSold(String descriptionofGoodsSold) {
		this.descriptionofGoodsSold = descriptionofGoodsSold;
	}

	public String getUqc() {
		return uqc;
	}

	public void setUqc(String uqc) {
		this.uqc = uqc;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
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

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
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
	
	

	/**
	 * @return the taxRate
	 */
	public String getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	
	

	/**
	 * @return the refid
	 */
	public String getRefid() {
		return refid;
	}

	/**
	 * @param refid the refid to set
	 */
	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getRecordtype() {
		return recordtype;
	}

	public void setRecordtype(String recordtype) {
		this.recordtype = recordtype;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gstr1GstnHSNSUMErrorDto [serialNumber=" + serialNumber
				+ ", hsnSac=" + hsnSac + ", descriptionofGoodsSold="
				+ descriptionofGoodsSold + ", uqc=" + uqc + ", quantity="
				+ quantity + ", taxableValue=" + taxableValue
				+ ", integratedAmount=" + integratedAmount
				+ ", centralTaxAmount=" + centralTaxAmount + ", stateTaxAmount="
				+ stateTaxAmount + ", cessAmount=" + cessAmount
				+ ", totalValue=" + totalValue + ", errorCode=" + errorCode
				+ ", gstnErrorMessage=" + gstnErrorMessage + ", taxRate="
				+ taxRate + ", refid=" + refid + "]";
	}

}
