package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is responsible for sending 
 * gstn save or submit response data to UI
 * @author Mohana.Dasari
 *
 */
public class GstnSaveSubmitRespDto {
	
	@Expose
	@SerializedName("authToken")
	private String authToken;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;//renamed
	
	@Expose
	@SerializedName("makerId")
	private String makerId;
	
	@Expose
	@SerializedName("aspProcessed")
	private Integer aspProcessed;
	
	@Expose
	@SerializedName("aspError")
	private Integer aspError;
	
	@Expose
	@SerializedName("aspInfo")
	private Integer aspInfo;
	
	@Expose
	@SerializedName("aspTotal")
	private Integer aspTotal;
	
	@Expose
	@SerializedName("aspRectify")
	private Integer aspRectify;
	
	@Expose
	@SerializedName("gstnProcessed")
	private Integer gstnProcessed;
	
	@Expose
	@SerializedName("gstnError")
	private Integer gstnError;
	
	@Expose
	@SerializedName("saveStatus")
	private String saveStatus;
	
	@Expose
	@SerializedName("statusCode")
	private Integer statusCode;
	
	@Expose
	@SerializedName("reviewStatus")
	private String reviewStatus;
	
	@Expose
	@SerializedName("arn")
	private String arn;
	
	@Expose
	@SerializedName("filingDate")
	private LocalDate filingDate;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}

	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	/**
	 * @return the makerId
	 */
	public String getMakerId() {
		return makerId;
	}

	/**
	 * @param makerId the makerId to set
	 */
	public void setMakerId(String makerId) {
		this.makerId = makerId;
	}

	/**
	 * @return the aspProcessed
	 */
	public Integer getAspProcessed() {
		return aspProcessed;
	}

	/**
	 * @param aspProcessed the aspProcessed to set
	 */
	public void setAspProcessed(Integer aspProcessed) {
		this.aspProcessed = aspProcessed;
	}

	/**
	 * @return the aspError
	 */
	public Integer getAspError() {
		return aspError;
	}

	/**
	 * @param aspError the aspError to set
	 */
	public void setAspError(Integer aspError) {
		this.aspError = aspError;
	}

	/**
	 * @return the aspInfo
	 */
	public Integer getAspInfo() {
		return aspInfo;
	}

	/**
	 * @param aspInfo the aspInfo to set
	 */
	public void setAspInfo(Integer aspInfo) {
		this.aspInfo = aspInfo;
	}

	/**
	 * @return the aspTotal
	 */
	public Integer getAspTotal() {
		return aspTotal;
	}

	/**
	 * @param aspTotal the aspTotal to set
	 */
	public void setAspTotal(Integer aspTotal) {
		this.aspTotal = aspTotal;
	}

	/**
	 * @return the aspRectify
	 */
	public Integer getAspRectify() {
		return aspRectify;
	}

	/**
	 * @param aspRectify the aspRectify to set
	 */
	public void setAspRectify(Integer aspRectify) {
		this.aspRectify = aspRectify;
	}

	/**
	 * @return the gstnProcessed
	 */
	public Integer getGstnProcessed() {
		return gstnProcessed;
	}

	/**
	 * @param gstnProcessed the gstnProcessed to set
	 */
	public void setGstnProcessed(Integer gstnProcessed) {
		this.gstnProcessed = gstnProcessed;
	}

	/**
	 * @return the gstnError
	 */
	public Integer getGstnError() {
		return gstnError;
	}

	/**
	 * @param gstnError the gstnError to set
	 */
	public void setGstnError(Integer gstnError) {
		this.gstnError = gstnError;
	}

	/**
	 * @return the saveStatus
	 */
	public String getSaveStatus() {
		return saveStatus;
	}

	/**
	 * @param saveStatus the saveStatus to set
	 */
	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the reviewStatus
	 */
	public String getReviewStatus() {
		return reviewStatus;
	}

	/**
	 * @param reviewStatus the reviewStatus to set
	 */
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	/**
	 * @return the arn
	 */
	public String getArn() {
		return arn;
	}

	/**
	 * @param arn the arn to set
	 */
	public void setArn(String arn) {
		this.arn = arn;
	}

	/**
	 * @return the filingDate
	 */
	public LocalDate getFilingDate() {
		return filingDate;
	}

	/**
	 * @param filingDate the filingDate to set
	 */
	public void setFilingDate(LocalDate filingDate) {
		this.filingDate = filingDate;
	}

	/**
	 * @return the entityId
	 */
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"GstnSaveSubmitRespDto [authToken=%s, gstin=%s, "
				+ "returnPeriod=%s, makerId=%s, aspProcessed=%s, "
				+ "aspError=%s, aspInfo=%s, aspTotal=%s, aspRectify=%s, "
				+ "gstnProcessed=%s, gstnError=%s, saveStatus=%s, "
				+ "statusCode=%s, reviewStatus=%s, arn=%s, filingDate=%s,"
				+ "entityId=%s]",
				authToken, gstin, returnPeriod, makerId, aspProcessed, aspError,
				aspInfo, aspTotal, aspRectify, gstnProcessed, gstnError,
				saveStatus, statusCode, reviewStatus, arn, filingDate,
				entityId);
	}

}
