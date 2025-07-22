package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2DataStatusRespDto {
	
	@Expose
	@SerializedName("date")
	private LocalDate date;
	@Expose
	@SerializedName("reviewstatus")
	private String reviewstatus;
	@Expose
	@SerializedName("sapTotal")
	private Integer sapTotal;
	@Expose
	@SerializedName("diffCount")
	private Integer diffCount;
	@Expose
	@SerializedName("hciTotal")
	private Integer hciTotal;
	@Expose
	@SerializedName("hciProcess")
	private Integer hciProcess;	
	@Expose
	@SerializedName("hciError")
	private Integer hciError;
	@Expose
	@SerializedName("aspTotal")
	private Integer aspTotal;
	@Expose
	@SerializedName("aspProcess")
	private Integer aspProcess;
	@Expose
	@SerializedName("aspError")
	private Integer aspError;
	@Expose
	@SerializedName("aspInfo")
	private Integer aspInfo;
	@Expose
	@SerializedName("aspRect")
	private Integer aspRect;
	@Expose
	@SerializedName("aspStatus")
	private String aspStatus;
	@Expose
	@SerializedName("gstinProcess")
	private Integer gstinProcess;
	@Expose
	@SerializedName("gstinError")
	private Integer gstinError;
	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	/**
	 * @return the reviewstatus
	 */
	public String getReviewstatus() {
		return reviewstatus;
	}
	/**
	 * @param reviewstatus the reviewstatus to set
	 */
	public void setReviewstatus(String reviewstatus) {
		this.reviewstatus = reviewstatus;
	}
	/**
	 * @return the sapTotal
	 */
	public Integer getSapTotal() {
		return sapTotal;
	}
	/**
	 * @param sapTotal the sapTotal to set
	 */
	public void setSapTotal(Integer sapTotal) {
		this.sapTotal = sapTotal;
	}
	/**
	 * @return the diffCount
	 */
	public Integer getDiffCount() {
		return diffCount;
	}
	/**
	 * @param diffCount the diffCount to set
	 */
	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}
	/**
	 * @return the hciTotal
	 */
	public Integer getHciTotal() {
		return hciTotal;
	}
	/**
	 * @param hciTotal the hciTotal to set
	 */
	public void setHciTotal(Integer hciTotal) {
		this.hciTotal = hciTotal;
	}
	/**
	 * @return the hciProcess
	 */
	public Integer getHciProcess() {
		return hciProcess;
	}
	/**
	 * @param hciProcess the hciProcess to set
	 */
	public void setHciProcess(Integer hciProcess) {
		this.hciProcess = hciProcess;
	}
	/**
	 * @return the hciError
	 */
	public Integer getHciError() {
		return hciError;
	}
	/**
	 * @param hciError the hciError to set
	 */
	public void setHciError(Integer hciError) {
		this.hciError = hciError;
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
	 * @return the aspProcess
	 */
	public Integer getAspProcess() {
		return aspProcess;
	}
	/**
	 * @param aspProcess the aspProcess to set
	 */
	public void setAspProcess(Integer aspProcess) {
		this.aspProcess = aspProcess;
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
	 * @return the aspRect
	 */
	public Integer getAspRect() {
		return aspRect;
	}
	/**
	 * @param aspRect the aspRect to set
	 */
	public void setAspRect(Integer aspRect) {
		this.aspRect = aspRect;
	}
	/**
	 * @return the aspStatus
	 */
	public String getAspStatus() {
		return aspStatus;
	}
	/**
	 * @param aspStatus the aspStatus to set
	 */
	public void setAspStatus(String aspStatus) {
		this.aspStatus = aspStatus;
	}
	/**
	 * @return the gstinProcess
	 */
	public Integer getGstinProcess() {
		return gstinProcess;
	}
	/**
	 * @param gstinProcess the gstinProcess to set
	 */
	public void setGstinProcess(Integer gstinProcess) {
		this.gstinProcess = gstinProcess;
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
	
	

}
