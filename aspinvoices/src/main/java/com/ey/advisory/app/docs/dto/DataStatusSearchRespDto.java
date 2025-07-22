package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

public class DataStatusSearchRespDto {
	
	private LocalDate documentDate;
	private LocalDate receivedDate;
	private Integer sapTotal;
	private Integer diff;
	private Integer aspTotal;
	private Integer aspProcessed;
	private Integer aspError;
	private Integer aspInfo;
	private String aspStatus;
	private Integer gstnProcessed;
	private Integer gstnError;
	private Integer rectifier;
	/*private LocalDate documentDate;
	private Integer derivedRetPeriod;
	private List<String> sgstins;*/
		
	/**
	 * @return the documentDate
	 */
	public LocalDate getDocumentDate() {
		return documentDate;
	}
	/**
	 * @param documentDate the documentDate to set
	 */
	public void setDocumentDate(LocalDate documentDate) {
		this.documentDate = documentDate;
	}
	/**
	 * @return the receivedDate
	 */
	public LocalDate getReceivedDate() {
		return receivedDate;
	}
	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
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
	 * @return the diff
	 */
	public Integer getDiff() {
		return diff;
	}
	/**
	 * @param diff the diff to set
	 */
	public void setDiff(Integer diff) {
		this.diff = diff;
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
	 * @return the rectifier
	 */
	public Integer getRectifier() {
		return rectifier;
	}
	/**
	 * @param rectifier the rectifier to set
	 */
	public void setRectifier(Integer rectifier) {
		this.rectifier = rectifier;
	}
}
