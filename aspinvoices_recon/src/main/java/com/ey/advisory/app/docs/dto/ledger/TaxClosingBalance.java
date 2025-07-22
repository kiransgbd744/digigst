package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class TaxClosingBalance {

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRunningBal;

	@Expose
	@SerializedName("igstbal")
	private TaxIgstCgstSgstCess igstBal;

	@Expose
	@SerializedName("cgstbal")
	private TaxIgstCgstSgstCess cgstBal;

	@Expose
	@SerializedName("sgstbal")
	private TaxIgstCgstSgstCess sgstBal;

	@Expose
	@SerializedName("cessbal")
	private TaxIgstCgstSgstCess cessBal;

	public String getDesc() {
		return desc;
	}

	public BigDecimal getTotRunningBal() {
		return totRunningBal;
	}

	public TaxIgstCgstSgstCess getIgstBal() {
		return igstBal;
	}

	public TaxIgstCgstSgstCess getCgstBal() {
		return cgstBal;
	}

	public TaxIgstCgstSgstCess getSgstBal() {
		return sgstBal;
	}

	public TaxIgstCgstSgstCess getCessBal() {
		return cessBal;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTotRunningBal(BigDecimal totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

	public void setIgstBal(TaxIgstCgstSgstCess igstBal) {
		this.igstBal = igstBal;
	}

	public void setCgstBal(TaxIgstCgstSgstCess cgstBal) {
		this.cgstBal = cgstBal;
	}

	public void setSgstBal(TaxIgstCgstSgstCess sgstBal) {
		this.sgstBal = sgstBal;
	}

	public void setCessBal(TaxIgstCgstSgstCess cessBal) {
		this.cessBal = cessBal;
	}

}
