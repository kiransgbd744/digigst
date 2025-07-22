/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnAdvanceAdjustedTXPDto {

	private String serialNo;
	private String supplierGSTIN;
	private String returnPeriod;
	private String supplyType;
	private String stateName;
	private String pos;
	private String rateofTax;
	private String grossAdvanceAdjusted;
	private String igstAmount;
	private String cgstAmount;
	private String SGSTUTGSTAmount;
	private String cessAmount;
	private String differentialPercentageRate;
	private String isFiled;

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

	public String getGrossAdvanceAdjusted() {
		return grossAdvanceAdjusted;
	}

	public void setGrossAdvanceAdjusted(String grossAdvanceAdjusted) {
		this.grossAdvanceAdjusted = grossAdvanceAdjusted;
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

	public String getSGSTUTGSTAmount() {
		return SGSTUTGSTAmount;
	}

	public void setSGSTUTGSTAmount(String sGSTUTGSTAmount) {
		SGSTUTGSTAmount = sGSTUTGSTAmount;
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

	public String getIsFiled() {
		return isFiled;
	}

	public void setIsFiled(String isFiled) {
		this.isFiled = isFiled;
	}

	@Override
	public String toString() {
		return "Gstr1GstnAdvanceAdjustedTXPDto [serialNo=" + serialNo
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", supplyType=" + supplyType + ", stateName="
				+ stateName + ", pos=" + pos + ", rateofTax=" + rateofTax
				+ ", grossAdvanceAdjusted=" + grossAdvanceAdjusted
				+ ", igstAmount=" + igstAmount + ", cgstAmount=" + cgstAmount
				+ ", SGSTUTGSTAmount=" + SGSTUTGSTAmount + ", cessAmount="
				+ cessAmount + ", differentialPercentageRate="
				+ differentialPercentageRate + ", isFiled=" + isFiled + "]";
	}

}
