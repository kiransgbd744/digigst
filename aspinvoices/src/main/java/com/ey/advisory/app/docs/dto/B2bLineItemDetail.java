package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class B2bLineItemDetail {

	@Expose
	@SerializedName("rt")
	private BigDecimal rate;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

	public BigDecimal getRate() {
		return rate;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	@Override
	public String toString() {
		return "B2bLineItemDetail [rate=" + rate + ", taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount + ", cessAmount="
				+ cessAmount + "]";
	}

}
