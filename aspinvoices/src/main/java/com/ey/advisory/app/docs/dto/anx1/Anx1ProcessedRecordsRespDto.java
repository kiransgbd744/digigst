package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Anx1ProcessedRecordsRespDto {

	private String state;
	private String gstin;
	private String regType;
	private String docType;
	private String authToken;
	private String outType;
	private BigInteger outCount;
	private BigDecimal outSupplies;
	private BigDecimal outIgst;
	private BigDecimal outCgst;
	private BigDecimal outSgst;
	private BigDecimal outCess;
	private String inType;
	private BigDecimal inSupplies;
	private BigInteger inCount;
	private BigDecimal inIgst;
	private BigDecimal inCgst;
	private BigDecimal inSgst;
	private BigDecimal inCess;
	private String status;
	private String timeStamp;

	private int notSentCount;
	private int savedCount;
	private int notSavedCount;
	private int errorCount;
	private int totalCount;

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

	public int getNotSentCount() {
		return notSentCount;
	}

	public void setNotSentCount(int notSentCount) {
		this.notSentCount = notSentCount;
	}

	public int getSavedCount() {
		return savedCount;
	}

	public void setSavedCount(int savedCount) {
		this.savedCount = savedCount;
	}

	public int getNotSavedCount() {
		return notSavedCount;
	}

	public void setNotSavedCount(int notSavedCount) {
		this.notSavedCount = notSavedCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
