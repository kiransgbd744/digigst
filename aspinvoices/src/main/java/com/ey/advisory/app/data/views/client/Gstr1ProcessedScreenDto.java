package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Gstr1ProcessedScreenDto {

	private String GSTIN;
	private String stateCode;
	private String stateName;
	private String registrationType;
	private String saveStatus;
	private String dateTime;
	private BigInteger count;
	private BigDecimal taxableValue;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;

	public String getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	@Override
	public String toString() {
		return "Anx1ProcessedScreenDto [GSTIN=" + GSTIN + ", stateCode="
				+ stateCode + ", stateName=" + stateName + ", registrationType="
				+ registrationType + ", saveStatus=" + saveStatus
				+ ", dateTime=" + dateTime + ", count=" + count
				+ ", taxableValue=" + taxableValue + ", igst=" + igst
				+ ", cgst=" + cgst + ", sgst=" + sgst + ", cess=" + cess + "]";
	}

}
