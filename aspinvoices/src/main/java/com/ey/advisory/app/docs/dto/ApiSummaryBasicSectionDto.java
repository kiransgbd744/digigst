package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Mahesh.Golla
 *
 */

public class ApiSummaryBasicSectionDto {
	
	@Expose
	@SerializedName("date")
	private LocalDate date;

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("period")
	private String period;
	
	@Expose
	@SerializedName("section")
	private String section;
	
	@Expose
	@SerializedName("count")
	private BigDecimal count = BigDecimal.ZERO;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("toatlTaxes")
	private BigDecimal toatlTaxes = BigDecimal.ZERO;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("status")
	private String status;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getToatlTaxes() {
		return toatlTaxes;
	}

	public void setToatlTaxes(BigDecimal toatlTaxes) {
		this.toatlTaxes = toatlTaxes;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
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
    }