package com.ey.advisory.app.services.ret1a;

import java.math.BigDecimal;

/**
 * 
 * @author Anand3.M
 *
 */
public class Ret1AProcessedRecordsResponseDto {

	private String state;
	private String gstin;
	private String regType;
	private String authToken;
	private String status;
	private String timestamp;
	private BigDecimal liability = BigDecimal.ZERO;
	private BigDecimal revCharge = BigDecimal.ZERO;
	private BigDecimal otherCharge = BigDecimal.ZERO;
	private BigDecimal itc = BigDecimal.ZERO;

	public Ret1AProcessedRecordsResponseDto() {
		super();

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

	@Override
	public String toString() {
		return "Ret1AProcessedRecordsResponseDto [state=" + state + ", gstin="
				+ gstin + ", regType=" + regType + ", authToken=" + authToken
				+ ", status=" + status + ", timestamp=" + timestamp
				+ ", liability=" + liability + ", revCharge=" + revCharge
				+ ", otherCharge=" + otherCharge + ", itc=" + itc + "]";
	}
	
	

	}