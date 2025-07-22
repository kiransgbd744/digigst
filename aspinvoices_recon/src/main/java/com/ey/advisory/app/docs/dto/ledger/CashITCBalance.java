package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class CashITCBalance {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("cash_bal")
	private CashBalanceDetails cashBal;

	@Expose
	@SerializedName("itc_bal")
	private ITCBalanceDetails itcBal;

	public String getGstin() {
		return gstin;
	}

	public CashBalanceDetails getCashBal() {
		return cashBal;
	}

	public ITCBalanceDetails getItcBal() {
		return itcBal;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setCashBal(CashBalanceDetails cashBal) {
		this.cashBal = cashBal;
	}

	public void setItcBal(ITCBalanceDetails itcBal) {
		this.itcBal = itcBal;
	}

}
