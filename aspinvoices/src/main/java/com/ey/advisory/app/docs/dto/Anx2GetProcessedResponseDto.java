package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Anx2GetProcessedResponseDto {

	private String gstin;

	private boolean highlight;

	private LocalDateTime lastUpdated;

	private String state;

	private String status;

	private int count;

	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal tiTax = BigDecimal.ZERO;

	private BigDecimal IGST = BigDecimal.ZERO;

	private BigDecimal CGST = BigDecimal.ZERO;

	private BigDecimal SGST = BigDecimal.ZERO;

	private BigDecimal Cess = BigDecimal.ZERO;

	private String authToken;

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin
	 *            the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the highlight
	 */
	public boolean isHighlight() {
		return highlight;
	}

	/**
	 * @param highlight
	 *            the highlight to set
	 */
	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	/**
	 * @return the lastUpdated
	 */
	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated
	 *            the lastUpdated to set
	 */
	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the invValue
	 */
	public BigDecimal getInvValue() {
		return invValue;
	}

	/**
	 * @param invValue
	 *            the invValue to set
	 */
	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	/**
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	/**
	 * @param taxableValue
	 *            the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	/**
	 * @return the tiTax
	 */
	public BigDecimal getTiTax() {
		return tiTax;
	}

	/**
	 * @param tiTax
	 *            the tiTax to set
	 */
	public void setTiTax(BigDecimal tiTax) {
		this.tiTax = tiTax;
	}

	/**
	 * @return the iGST
	 */
	public BigDecimal getIGST() {
		return IGST;
	}

	/**
	 * @param iGST
	 *            the iGST to set
	 */
	public void setIGST(BigDecimal iGST) {
		IGST = iGST;
	}

	/**
	 * @return the cGST
	 */
	public BigDecimal getCGST() {
		return CGST;
	}

	/**
	 * @param cGST
	 *            the cGST to set
	 */
	public void setCGST(BigDecimal cGST) {
		CGST = cGST;
	}

	/**
	 * @return the sGST
	 */
	public BigDecimal getSGST() {
		return SGST;
	}

	/**
	 * @param sGST
	 *            the sGST to set
	 */
	public void setSGST(BigDecimal sGST) {
		SGST = sGST;
	}

	/**
	 * @return the cess
	 */
	public BigDecimal getCess() {
		return Cess;
	}

	/**
	 * @param cess
	 *            the cess to set
	 */
	public void setCess(BigDecimal cess) {
		Cess = cess;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken
	 *            the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Anx2GetProcessedResponseDto [gstin=" + gstin + ", highlight="
				+ highlight + ", lastUpdated=" + lastUpdated + ", state="
				+ state + ", status=" + status + ", count=" + count
				+ ", invValue=" + invValue + ", taxableValue=" + taxableValue
				+ ", tiTax=" + tiTax + ", IGST=" + IGST + ", CGST=" + CGST
				+ ", SGST=" + SGST + ", Cess=" + Cess + ", authToken="
				+ authToken + "]";
	}

}