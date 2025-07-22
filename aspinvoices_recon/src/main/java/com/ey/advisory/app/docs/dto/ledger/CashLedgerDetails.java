package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class CashLedgerDetails {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fr_dt")
	private String fromDate;

	@Expose
	@SerializedName("to_dt")
	private String toDate;

	@Expose
	@SerializedName("op_bal")
	private CashOpeningBalance openingBal;

	@Expose
	@SerializedName("tr")
	private CashTransactionDetails transactions;

	@Expose
	@SerializedName("cl_bal")
	private CashClosingBalance closingBal;

	public String getGstin() {
		return gstin;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public CashOpeningBalance getOpeningBal() {
		return openingBal;
	}

	public CashTransactionDetails getTransactions() {
		return transactions;
	}

	public CashClosingBalance getClosingBal() {
		return closingBal;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public void setOpeningBal(CashOpeningBalance openingBal) {
		this.openingBal = openingBal;
	}

	public void setTransactions(CashTransactionDetails transactions) {
		this.transactions = transactions;
	}

	public void setClosingBal(CashClosingBalance closingBal) {
		this.closingBal = closingBal;
	}

}
