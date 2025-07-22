package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2ItemDetails {

	@Expose
	@SerializedName("hsn")
	private Integer hsn;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("rt")
	private BigDecimal rate;

	@Expose
	@SerializedName("igst")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("cess")
	private BigDecimal cessAmount;

	public Integer getHsn() {
		return hsn;
	}

	public void setHsn(Integer hsn) {
		this.hsn = hsn;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

}
