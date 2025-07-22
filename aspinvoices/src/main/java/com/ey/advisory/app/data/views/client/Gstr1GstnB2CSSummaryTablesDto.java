/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnB2CSSummaryTablesDto {

	private String serialNo;
	private String supplierGSTIN;
	private String returnPeriod;
	private String supplyType;
	private String stateName;
	private String pos;
	private String rateofTax;
	private String taxableValue;
	private String igstAmount;
	private String cgstAmount;
	private String sgstUTGSTAmount;
	private String cessAmount;
	private String differentialPercentageRate;
	private String type;
	private String ecomGSTIN;
	private String isFiled;

	public String getIsFiled() {
		return isFiled;
	}

	public void setIsFiled(String isFiled) {
		this.isFiled = isFiled;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getRateofTax() {
		return rateofTax;
	}

	public void setRateofTax(String rateofTax) {
		this.rateofTax = rateofTax;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(String igstAmount) {
		this.igstAmount = igstAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(String cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public String getSgstUTGSTAmount() {
		return sgstUTGSTAmount;
	}

	public void setSgstUTGSTAmount(String sgstUTGSTAmount) {
		this.sgstUTGSTAmount = sgstUTGSTAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getDifferentialPercentageRate() {
		return differentialPercentageRate;
	}

	public void setDifferentialPercentageRate(
			String differentialPercentageRate) {
		this.differentialPercentageRate = differentialPercentageRate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEcomGSTIN() {
		return ecomGSTIN;
	}

	public void setEcomGSTIN(String ecomGSTIN) {
		this.ecomGSTIN = ecomGSTIN;
	}

	@Override
	public String toString() {
		return "Gstr1GstnB2CSSummaryTablesDto [serialNo=" + serialNo
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", supplyType=" + supplyType + ", stateName="
				+ stateName + ", pos=" + pos + ", rateofTax=" + rateofTax
				+ ", taxableValue=" + taxableValue + ", igstAmount="
				+ igstAmount + ", cgstAmount=" + cgstAmount
				+ ", sgstUTGSTAmount=" + sgstUTGSTAmount + ", cessAmount="
				+ cessAmount + ", differentialPercentageRate="
				+ differentialPercentageRate + ", type=" + type + ", ecomGSTIN="
				+ ecomGSTIN + ", isFiled=" + isFiled + "]";
	}

}
