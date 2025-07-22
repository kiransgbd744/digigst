package com.ey.advisory.app.services.ret1;

import java.math.BigDecimal;

/**
 * 
 * @author Anand3.M
 *
 */
public class Ret1ProcessedRecordsResponseDto {

	private String state;
	private String gstin;
	private String retPeriod;
	private String regType;
	private String authToken;
	private String status;
	private String timestamp;
	private BigDecimal liability = BigDecimal.ZERO;
	private BigDecimal revCharge = BigDecimal.ZERO;
	private BigDecimal otherCharge = BigDecimal.ZERO;
	private BigDecimal itc = BigDecimal.ZERO;
	private BigDecimal tds = BigDecimal.ZERO;
	private BigDecimal tcs = BigDecimal.ZERO;

	public Ret1ProcessedRecordsResponseDto() {
		super();

	}

	
	public String getRetPeriod() {
		return retPeriod;
	}


	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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
		return "Ret1ProcessedRecordsResponseDto [state=" + state + ", gstin="
				+ gstin + ", regType=" + regType + ", authToken=" + authToken
				+ ", status=" + status + ", timestamp=" + timestamp
				+ ", liability=" + liability + ", revCharge=" + revCharge
				+ ", otherCharge=" + otherCharge + ", itc=" + itc + ", tds="
				+ tds + ", tcs=" + tcs + "]";
	}

}
