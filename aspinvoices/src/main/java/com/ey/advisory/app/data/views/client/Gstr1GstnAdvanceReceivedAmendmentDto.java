/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Gstr1GstnAdvanceReceivedAmendmentDto {

	private String serialNo;
	private String supplierGSTIN;
	private String returnPeriod;
	private String originalReturnPeriod;
	private String supplyType;
	private String originaSupplyType;
	private String stateName;
	private String pos;
	private String originalPos;
	private String rateofTax;
	private String grossAdvanceReceived;
	private String igstAmount;
	private String cgstAmount;
	private String sgstUTGSTAmount;
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

	public String getOriginalReturnPeriod() {
		return originalReturnPeriod;
	}

	public void setOriginalReturnPeriod(String originalReturnPeriod) {
		this.originalReturnPeriod = originalReturnPeriod;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getOriginaSupplyType() {
		return originaSupplyType;
	}

	public void setOriginaSupplyType(String originaSupplyType) {
		this.originaSupplyType = originaSupplyType;
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

	public String getOriginalPos() {
		return originalPos;
	}

	public void setOriginalPos(String originalPos) {
		this.originalPos = originalPos;
	}

	public String getRateofTax() {
		return rateofTax;
	}

	public void setRateofTax(String rateofTax) {
		this.rateofTax = rateofTax;
	}

	public String getGrossAdvanceReceived() {
		return grossAdvanceReceived;
	}

	public void setGrossAdvanceReceived(String grossAdvanceReceived) {
		this.grossAdvanceReceived = grossAdvanceReceived;
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

	public String getIsFiled() {
		return isFiled;
	}

	public void setIsFiled(String isFiled) {
		this.isFiled = isFiled;
	}

	@Override
	public String toString() {
		return "Gstr1GstnAdvanceReceivedAmendmentDto [serialNo=" + serialNo
				+ ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", originalReturnPeriod="
				+ originalReturnPeriod + ", supplyType=" + supplyType
				+ ", originaSupplyType=" + originaSupplyType + ", stateName="
				+ stateName + ", pos=" + pos + ", originalPos=" + originalPos
				+ ", rateofTax=" + rateofTax + ", grossAdvanceReceived="
				+ grossAdvanceReceived + ", igstAmount=" + igstAmount
				+ ", cgstAmount=" + cgstAmount + ", sgstUTGSTAmount="
				+ sgstUTGSTAmount + ", cessAmount=" + cessAmount
				+ ", differentialPercentageRate=" + differentialPercentageRate
				+ ", isFiled=" + isFiled + "]";
	}

}
