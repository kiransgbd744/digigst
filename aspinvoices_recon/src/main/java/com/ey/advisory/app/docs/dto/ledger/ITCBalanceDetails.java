package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ITCBalanceDetails {

	@Expose
	@SerializedName("igst_bal")
	private BigDecimal igstBal;

	@Expose
	@SerializedName("cgst_bal")
	private BigDecimal cgstBal;

	@Expose
	@SerializedName("sgst_bal")
	private BigDecimal sgstBal;

	@Expose
	@SerializedName("cess_bal")
	private BigDecimal cessBal;

	public BigDecimal getIgstBal() {
		return igstBal;
	}

	public BigDecimal getCgstBal() {
		return cgstBal;
	}

	public BigDecimal getSgstBal() {
		return sgstBal;
	}

	public BigDecimal getCessBal() {
		return cessBal;
	}

	public void setIgstBal(BigDecimal igstBal) {
		this.igstBal = igstBal;
	}

	public void setCgstBal(BigDecimal cgstBal) {
		this.cgstBal = cgstBal;
	}

	public void setSgstBal(BigDecimal sgstBal) {
		this.sgstBal = sgstBal;
	}

	public void setCessBal(BigDecimal cessBal) {
		this.cessBal = cessBal;
	}

}
