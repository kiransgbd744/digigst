package com.ey.advisory.app.data.views.client;

public class Gstr3bdownloadErrorDto {

	private String aspErrorCode;
	private String aspErrorDescription;
	private String taxpayerGSTIN;
	private String returnPeriod;
	private String serialNo;
	private String description;
	private String iGSTAmount;
	private String cGSTAmount;
	private String sGSTAmount;
	private String cessAmount;
	private String recordStatus;

	public String getAspErrorCode() {
		return aspErrorCode;
	}

	public void setAspErrorCode(String aspErrorCode) {
		this.aspErrorCode = aspErrorCode;
	}

	public String getAspErrorDescription() {
		return aspErrorDescription;
	}

	public void setAspErrorDescription(String aspErrorDescription) {
		this.aspErrorDescription = aspErrorDescription;
	}

	public String getTaxpayerGSTIN() {
		return taxpayerGSTIN;
	}

	public void setTaxpayerGSTIN(String taxpayerGSTIN) {
		this.taxpayerGSTIN = taxpayerGSTIN;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getiGSTAmount() {
		return iGSTAmount;
	}

	public void setiGSTAmount(String iGSTAmount) {
		this.iGSTAmount = iGSTAmount;
	}

	public String getcGSTAmount() {
		return cGSTAmount;
	}

	public void setcGSTAmount(String cGSTAmount) {
		this.cGSTAmount = cGSTAmount;
	}

	public String getsGSTAmount() {
		return sGSTAmount;
	}

	public void setsGSTAmount(String sGSTAmount) {
		this.sGSTAmount = sGSTAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@Override
	public String toString() {
		return "Gstr3bdownloadErrorDto [aspErrorCode=" + aspErrorCode + ", aspErrorDescription=" + aspErrorDescription
				+ ", taxpayerGSTIN=" + taxpayerGSTIN + ", returnPeriod=" + returnPeriod + ", serialNo=" + serialNo
				+ ", description=" + description + ", iGSTAmount=" + iGSTAmount + ", cGSTAmount=" + cGSTAmount
				+ ", sGSTAmount=" + sGSTAmount + ", cessAmount=" + cessAmount + ", recordStatus=" + recordStatus + "]";
	}

}
