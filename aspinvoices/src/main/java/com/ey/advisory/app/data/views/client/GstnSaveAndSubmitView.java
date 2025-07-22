package com.ey.advisory.app.data.views.client;

import java.time.LocalDate;

import jakarta.persistence.Column;

/**
 * This class represents GSTN Save And Submit View 
 * @author Mohana.Dasari
 *
 */
public class GstnSaveAndSubmitView {
	
	
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "RECEIVED_DATE")
	private LocalDate receivedDate;
	
	@Column(name = "ASPTOTAL")
	private Integer aspTotal;
	
	@Column(name="PROCESSED")
	private Integer aspProcessed;
	
	@Column(name="ERRORS")
	private Integer aspError;
	
	@Column(name="INFORMATION")
	private Integer aspInfo;

	

	/**
	 * @return the sgstin
	 */
	public String getSgstin() {
		return sgstin;
	}

	/**
	 * @param sgstin the sgstin to set
	 */
	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	/**
	 * @return the derivedRetPeriod
	 */
	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	/**
	 * @param derivedRetPeriod the derivedRetPeriod to set
	 */
	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"GstnSaveAndSubmitView [sgstin=%s, derivedRetPeriod=%s, "
				+ "receivedDate=%s, aspTotal=%s, aspProcessed=%s, aspError=%s, "
				+ "aspInfo=%s]",
				sgstin, derivedRetPeriod, receivedDate, aspTotal, aspProcessed,
				aspError, aspInfo);
	}

}
