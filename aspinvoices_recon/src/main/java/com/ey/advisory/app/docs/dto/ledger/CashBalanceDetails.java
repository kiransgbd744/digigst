package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class CashBalanceDetails {

	@Expose
	@SerializedName("igst_tot_bal")
	private BigDecimal igstTotBal;

	@Expose
	@SerializedName("cgst_tot_bal")
	private BigDecimal cgstTotBal;

	@Expose
	@SerializedName("sgst_tot_bal")
	private BigDecimal sgstTotBal;

	@Expose
	@SerializedName("cess_tot_bal")
	private BigDecimal cessTotBal;

	@Expose
	@SerializedName("igst")
	private BalanceIgstCgstSgstCess igst;

	@Expose
	@SerializedName("cgst")
	private BalanceIgstCgstSgstCess cgst;

	@Expose
	@SerializedName("sgst")
	private BalanceIgstCgstSgstCess sgst;

	@Expose
	@SerializedName("cess")
	private BalanceIgstCgstSgstCess cess;

	public BigDecimal getIgstTotBal() {
		return igstTotBal;
	}

	public BigDecimal getCgstTotBal() {
		return cgstTotBal;
	}

	public BigDecimal getSgstTotBal() {
		return sgstTotBal;
	}

	public BigDecimal getCessTotBal() {
		return cessTotBal;
	}

	public BalanceIgstCgstSgstCess getIgst() {
		return igst;
	}

	public BalanceIgstCgstSgstCess getCgst() {
		return cgst;
	}

	public BalanceIgstCgstSgstCess getSgst() {
		return sgst;
	}

	public BalanceIgstCgstSgstCess getCess() {
		return cess;
	}

	public void setIgstTotBal(BigDecimal igstTotBal) {
		this.igstTotBal = igstTotBal;
	}

	public void setCgstTotBal(BigDecimal cgstTotBal) {
		this.cgstTotBal = cgstTotBal;
	}

	public void setSgstTotBal(BigDecimal sgstTotBal) {
		this.sgstTotBal = sgstTotBal;
	}

	public void setCessTotBal(BigDecimal cessTotBal) {
		this.cessTotBal = cessTotBal;
	}

	public void setIgst(BalanceIgstCgstSgstCess igst) {
		this.igst = igst;
	}

	public void setCgst(BalanceIgstCgstSgstCess cgst) {
		this.cgst = cgst;
	}

	public void setSgst(BalanceIgstCgstSgstCess sgst) {
		this.sgst = sgst;
	}

	public void setCess(BalanceIgstCgstSgstCess cess) {
		this.cess = cess;
	}

}
