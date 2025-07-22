package com.ey.advisory.app.docs.dto.ledger;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ITCLedger {

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
	@SerializedName("tr")
	private List<ITCTransactionsDetails> transactions;

	@Expose
	@SerializedName("op_bal")
	private OpeningBalanceAndClosingBalanceDetails openingBal;

	@Expose
	@SerializedName("cl_bal")
	private OpeningBalanceAndClosingBalanceDetails closingBal;

	public String getGstin() {
		return gstin;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public List<ITCTransactionsDetails> getTransactions() {
		return transactions;
	}

	public OpeningBalanceAndClosingBalanceDetails getOpeningBal() {
		return openingBal;
	}

	public OpeningBalanceAndClosingBalanceDetails getClosingBal() {
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

	public void setTransactions(List<ITCTransactionsDetails> transactions) {
		this.transactions = transactions;
	}

	public void setOpeningBal(
			OpeningBalanceAndClosingBalanceDetails openingBal) {
		this.openingBal = openingBal;
	}

	public void setClosingBal(
			OpeningBalanceAndClosingBalanceDetails closingBal) {
		this.closingBal = closingBal;
	}

}
