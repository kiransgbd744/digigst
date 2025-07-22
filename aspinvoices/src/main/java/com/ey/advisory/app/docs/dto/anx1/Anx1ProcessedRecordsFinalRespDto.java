package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx1ProcessedRecordsFinalRespDto {

	@Expose
	@SerializedName("state")
	private String state;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("regType")
	private String regType;
	
	@Expose
	@SerializedName("docType")
	private String docType;
	
	@Expose
	@SerializedName("authToken")
	private String authToken;
	
	@Expose
	@SerializedName("outType")
	private String outType;
	
	@Expose
	@SerializedName("outCount")
	private BigInteger outCount;
	
	@Expose
	@SerializedName("outSupplies")
	private BigDecimal outSupplies;
	
	@Expose
	@SerializedName("outIgst")
	private BigDecimal outIgst;
	
	@Expose
	@SerializedName("outCgst")
	private BigDecimal outCgst;
	
	@Expose
	@SerializedName("outSgst")
	private BigDecimal outSgst;
	
	@Expose
	@SerializedName("outCess")
	private BigDecimal outCess;
	
	@Expose
	@SerializedName("inType")
	private String inType;
	
	@Expose
	@SerializedName("inSupplies")
	private BigDecimal inSupplies;
	
	@Expose
	@SerializedName("inCount")
	private BigInteger inCount;
	
	@Expose
	@SerializedName("inIgst")
	private BigDecimal inIgst;
	
	@Expose
	@SerializedName("inCgst")
	private BigDecimal inCgst;
	
	@Expose
	@SerializedName("inSgst")
	private BigDecimal inSgst;
	
	@Expose
	@SerializedName("inCess")
	private BigDecimal inCess;
	
	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("timeStamp")
	private String timeStamp;

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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getOutType() {
		return outType;
	}

	public void setOutType(String outType) {
		this.outType = outType;
	}

	public BigInteger getOutCount() {
		return outCount;
	}

	public void setOutCount(BigInteger outCount) {
		this.outCount = outCount;
	}

	public BigDecimal getOutSupplies() {
		return outSupplies;
	}

	public void setOutSupplies(BigDecimal outSupplies) {
		this.outSupplies = outSupplies;
	}

	public BigDecimal getOutIgst() {
		return outIgst;
	}

	public void setOutIgst(BigDecimal outIgst) {
		this.outIgst = outIgst;
	}

	public BigDecimal getOutCgst() {
		return outCgst;
	}

	public void setOutCgst(BigDecimal outCgst) {
		this.outCgst = outCgst;
	}

	public BigDecimal getOutSgst() {
		return outSgst;
	}

	public void setOutSgst(BigDecimal outSgst) {
		this.outSgst = outSgst;
	}

	public BigDecimal getOutCess() {
		return outCess;
	}

	public void setOutCess(BigDecimal outCess) {
		this.outCess = outCess;
	}

	public String getInType() {
		return inType;
	}

	public void setInType(String inType) {
		this.inType = inType;
	}

	public BigDecimal getInSupplies() {
		return inSupplies;
	}

	public void setInSupplies(BigDecimal inSupplies) {
		this.inSupplies = inSupplies;
	}

	public BigInteger getInCount() {
		return inCount;
	}

	public void setInCount(BigInteger inCount) {
		this.inCount = inCount;
	}

	public BigDecimal getInIgst() {
		return inIgst;
	}

	public void setInIgst(BigDecimal inIgst) {
		this.inIgst = inIgst;
	}

	public BigDecimal getInCgst() {
		return inCgst;
	}

	public void setInCgst(BigDecimal inCgst) {
		this.inCgst = inCgst;
	}

	public BigDecimal getInSgst() {
		return inSgst;
	}

	public void setInSgst(BigDecimal inSgst) {
		this.inSgst = inSgst;
	}

	public BigDecimal getInCess() {
		return inCess;
	}

	public void setInCess(BigDecimal inCess) {
		this.inCess = inCess;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "Anx1ProcessedRecordsRespDto [state=" + state + ", gstin="
				+ gstin + ", regType=" + regType + ", docType=" + docType
				+ ", authToken=" + authToken + ", outType=" + outType
				+ ", outCount=" + outCount + ", outSupplies=" + outSupplies
				+ ", outIgst=" + outIgst + ", outCgst=" + outCgst + ", outSgst="
				+ outSgst + ", outCess=" + outCess + ", inType=" + inType
				+ ", inSupplies=" + inSupplies + ", inCount=" + inCount
				+ ", inIgst=" + inIgst + ", inCgst=" + inCgst + ", inSgst="
				+ inSgst + ", inCess=" + inCess + ", status=" + status
				+ ", timeStamp=" + timeStamp + "]";
	}


}
