package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class CashClosingBalance {

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRunningBal;

	@Expose
	@SerializedName("igst")
	private CashIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private CashIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private CashIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private CashIgstCgstSgstCess cess;

	public String getDesc() {
		return desc;
	}

	public BigDecimal getTotRunningBal() {
		return totRunningBal;
	}

	public CashIgstCgstSgstCess getIgst() {
		return igst;
	}

	public CashIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public CashIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public CashIgstCgstSgstCess getCess() {
		return cess;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTotRunningBal(BigDecimal totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

	public void setIgst(CashIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(CashIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(CashIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(CashIgstCgstSgstCess cess) {
		this.cess = cess;
	}

}
