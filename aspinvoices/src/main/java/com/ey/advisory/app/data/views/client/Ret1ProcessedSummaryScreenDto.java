/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Ret1ProcessedSummaryScreenDto {

	private String gstin;
	private String stateCode;
	private String stateName;
	private String registrationType;
	private String saveStatus;
	private String dateTime;
	private BigDecimal liability = BigDecimal.ZERO;
	private BigDecimal revCharge = BigDecimal.ZERO;
	private BigDecimal otherCharge = BigDecimal.ZERO;
	private BigDecimal itc = BigDecimal.ZERO;
	private BigDecimal tds = BigDecimal.ZERO;
	private BigDecimal tcs = BigDecimal.ZERO;
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
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
	public BigDecimal getLiability() {
		return liability;
	}
	public void setLiability(BigDecimal liability) {
		this.liability = liability;
	}
	public BigDecimal getRevCharge() {
		return revCharge;
	}
	public void setRevCharge(BigDecimal revCharge) {
		this.revCharge = revCharge;
	}
	public BigDecimal getOtherCharge() {
		return otherCharge;
	}
	public void setOtherCharge(BigDecimal otherCharge) {
		this.otherCharge = otherCharge;
	}
	public BigDecimal getItc() {
		return itc;
	}
	public void setItc(BigDecimal itc) {
		this.itc = itc;
	}
	public BigDecimal getTds() {
		return tds;
	}
	public void setTds(BigDecimal tds) {
		this.tds = tds;
	}
	public BigDecimal getTcs() {
		return tcs;
	}
	public void setTcs(BigDecimal tcs) {
		this.tcs = tcs;
	}
	@Override
	public String toString() {
		return "Ret1ProcessedSummaryScreenDto [gstin=" + gstin + ", stateCode="
				+ stateCode + ", stateName=" + stateName + ", registrationType="
				+ registrationType + ", saveStatus=" + saveStatus
				+ ", dateTime=" + dateTime + ", liability=" + liability
				+ ", revCharge=" + revCharge + ", otherCharge=" + otherCharge
				+ ", itc=" + itc + ", tds=" + tds + ", tcs=" + tcs + "]";
	}

}