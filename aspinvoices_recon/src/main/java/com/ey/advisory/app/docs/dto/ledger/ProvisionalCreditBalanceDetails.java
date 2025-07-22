package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ProvisionalCreditBalanceDetails {

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("igstProCrBal")
	private BigDecimal igstProCrBal;

	@Expose
	@SerializedName("cgstProCrBal")
	private BigDecimal cgstProCrBal;

	@Expose
	@SerializedName("sgstProCrBal")
	private BigDecimal sgstProCrBal;

	@Expose
	@SerializedName("cessProCrBal")
	private BigDecimal cessProCrBal;

	@Expose
	@SerializedName("totProCrBal")
	private BigDecimal totProCrBal;

	public String getRetPeriod() {
		return retPeriod;
	}

	public BigDecimal getIgstProCrBal() {
		return igstProCrBal;
	}

	public BigDecimal getCgstProCrBal() {
		return cgstProCrBal;
	}

	public BigDecimal getSgstProCrBal() {
		return sgstProCrBal;
	}

	public BigDecimal getCessProCrBal() {
		return cessProCrBal;
	}

	public BigDecimal getTotProCrBal() {
		return totProCrBal;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setIgstProCrBal(BigDecimal igstProCrBal) {
		this.igstProCrBal = igstProCrBal;
	}

	public void setCgstProCrBal(BigDecimal cgstProCrBal) {
		this.cgstProCrBal = cgstProCrBal;
	}

	public void setSgstProCrBal(BigDecimal sgstProCrBal) {
		this.sgstProCrBal = sgstProCrBal;
	}

	public void setCessProCrBal(BigDecimal cessProCrBal) {
		this.cessProCrBal = cessProCrBal;
	}

	public void setTotProCrBal(BigDecimal totProCrBal) {
		this.totProCrBal = totProCrBal;
	}

}
