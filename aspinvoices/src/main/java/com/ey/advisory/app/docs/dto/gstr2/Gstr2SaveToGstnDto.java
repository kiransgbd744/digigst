package com.ey.advisory.app.docs.dto.gstr2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2SaveToGstnDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("period")
	private String returnPeriod;
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
	@SerializedName("aspRectified")
	private Integer aspRectified;
	@Expose
	@SerializedName("gstinProcessed")
	private Integer gstinProcessed;
	@Expose
	@SerializedName("gstinError")
	private Integer gstinError;
	@Expose
	@SerializedName("returnStatus")
	private String returnStatus;
	@Expose
	@SerializedName("reviewStatus")
	private String reviewStatus;
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
	 * @return the aspRectified
	 */
	public Integer getAspRectified() {
		return aspRectified;
	}
	/**
	 * @param aspRectified the aspRectified to set
	 */
	public void setAspRectified(Integer aspRectified) {
		this.aspRectified = aspRectified;
	}
	/**
	 * @return the gstinProcessed
	 */
	public Integer getGstinProcessed() {
		return gstinProcessed;
	}
	/**
	 * @param gstinProcessed the gstinProcessed to set
	 */
	public void setGstinProcessed(Integer gstinProcessed) {
		this.gstinProcessed = gstinProcessed;
	}
	/**
	 * @return the gstinError
	 */
	public Integer getGstinError() {
		return gstinError;
	}
	/**
	 * @param gstinError the gstinError to set
	 */
	public void setGstinError(Integer gstinError) {
		this.gstinError = gstinError;
	}
	/**
	 * @return the returnStatus
	 */
	public String getReturnStatus() {
		return returnStatus;
	}
	/**
	 * @param returnStatus the returnStatus to set
	 */
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
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
	
	

}
