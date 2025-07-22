package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public class Gstr1AspVerticalHsnDto {

	private String serialNo;
	private String supplierGSTIN;
	private String returnPeriod;
	private String hsn;
	private String description;
	private String taxRate;
	private String uqc;
	private BigDecimal totalQuantity = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal igstAmount = BigDecimal.ZERO;
	private BigDecimal cgstAmount = BigDecimal.ZERO;
	private BigDecimal sgstutgstAmount = BigDecimal.ZERO;
	private BigDecimal cessAmount = BigDecimal.ZERO;
	/*private BigDecimal totalValue = BigDecimal.ZERO;*/
	private String totalValue;
	private String rate;
	private BigDecimal utotalQuantity = BigDecimal.ZERO;
	private BigDecimal utaxableValue = BigDecimal.ZERO;
	private BigDecimal uigstAmount = BigDecimal.ZERO;
	private BigDecimal ucgstAmount = BigDecimal.ZERO;
	private BigDecimal usgstutgstAmount = BigDecimal.ZERO;
	private BigDecimal ucessAmount = BigDecimal.ZERO;
	/*private BigDecimal totalValue = BigDecimal.ZERO;*/
	private String utotalValue;
	private String urate;
	private String saveStatus;
	private String gSTNRefID;
	private String gSTNRefIDTime;
	private String gSTNErrorcode;
	private String gSTNErrorDescription;
	private String recordType;
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
	public String getHsn() {
		return hsn;
	}
	public void setHsn(String hsn) {
		this.hsn = hsn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public String getUqc() {
		return uqc;
	}
	public void setUqc(String uqc) {
		this.uqc = uqc;
	}
	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}
	public BigDecimal getIgstAmount() {
		return igstAmount;
	}
	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}
	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}
	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}
	public BigDecimal getSgstutgstAmount() {
		return sgstutgstAmount;
	}
	public void setSgstutgstAmount(BigDecimal sgstutgstAmount) {
		this.sgstutgstAmount = sgstutgstAmount;
	}
	public BigDecimal getCessAmount() {
		return cessAmount;
	}
	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}
	public String getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public BigDecimal getUtotalQuantity() {
		return utotalQuantity;
	}
	public void setUtotalQuantity(BigDecimal utotalQuantity) {
		this.utotalQuantity = utotalQuantity;
	}
	public BigDecimal getUtaxableValue() {
		return utaxableValue;
	}
	public void setUtaxableValue(BigDecimal utaxableValue) {
		this.utaxableValue = utaxableValue;
	}
	public BigDecimal getUigstAmount() {
		return uigstAmount;
	}
	public void setUigstAmount(BigDecimal uigstAmount) {
		this.uigstAmount = uigstAmount;
	}
	public BigDecimal getUcgstAmount() {
		return ucgstAmount;
	}
	public void setUcgstAmount(BigDecimal ucgstAmount) {
		this.ucgstAmount = ucgstAmount;
	}
	public BigDecimal getUsgstutgstAmount() {
		return usgstutgstAmount;
	}
	public void setUsgstutgstAmount(BigDecimal usgstutgstAmount) {
		this.usgstutgstAmount = usgstutgstAmount;
	}
	public BigDecimal getUcessAmount() {
		return ucessAmount;
	}
	public void setUcessAmount(BigDecimal ucessAmount) {
		this.ucessAmount = ucessAmount;
	}
	public String getUtotalValue() {
		return utotalValue;
	}
	public void setUtotalValue(String utotalValue) {
		this.utotalValue = utotalValue;
	}
	public String getUrate() {
		return urate;
	}
	public void setUrate(String urate) {
		this.urate = urate;
	}
	public String getSaveStatus() {
		return saveStatus;
	}
	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}
	public String getgSTNRefID() {
		return gSTNRefID;
	}
	public void setgSTNRefID(String gSTNRefID) {
		this.gSTNRefID = gSTNRefID;
	}
	public String getgSTNRefIDTime() {
		return gSTNRefIDTime;
	}
	public void setgSTNRefIDTime(String gSTNRefIDTime) {
		this.gSTNRefIDTime = gSTNRefIDTime;
	}
	public String getgSTNErrorcode() {
		return gSTNErrorcode;
	}
	public void setgSTNErrorcode(String gSTNErrorcode) {
		this.gSTNErrorcode = gSTNErrorcode;
	}
	public String getgSTNErrorDescription() {
		return gSTNErrorDescription;
	}
	public void setgSTNErrorDescription(String gSTNErrorDescription) {
		this.gSTNErrorDescription = gSTNErrorDescription;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	@Override
	public String toString() {
		return "Gstr1AspVerticalHsnDto [serialNo=" + serialNo + ", supplierGSTIN=" + supplierGSTIN + ", returnPeriod="
				+ returnPeriod + ", hsn=" + hsn + ", description=" + description + ", taxRate=" + taxRate + ", uqc="
				+ uqc + ", totalQuantity=" + totalQuantity + ", taxableValue=" + taxableValue + ", igstAmount="
				+ igstAmount + ", cgstAmount=" + cgstAmount + ", sgstutgstAmount=" + sgstutgstAmount + ", cessAmount="
				+ cessAmount + ", totalValue=" + totalValue + ", rate=" + rate + ", utotalQuantity=" + utotalQuantity
				+ ", utaxableValue=" + utaxableValue + ", uigstAmount=" + uigstAmount + ", ucgstAmount=" + ucgstAmount
				+ ", usgstutgstAmount=" + usgstutgstAmount + ", ucessAmount=" + ucessAmount + ", utotalValue="
				+ utotalValue + ", urate=" + urate + ", saveStatus=" + saveStatus + ", gSTNRefID=" + gSTNRefID
				+ ", gSTNRefIDTime=" + gSTNRefIDTime + ", gSTNErrorcode=" + gSTNErrorcode + ", gSTNErrorDescription="
				+ gSTNErrorDescription + ", recordType="+ recordType + "]";
	}

	
	
}
