package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class TaxLedgerDetails {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("tr")
	private TaxTransactionDetails transations;

	@Expose
	@SerializedName("cl_bal")
	private TaxClosingBalance closingBal;

	public String getGstin() {
		return gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public TaxTransactionDetails getTransations() {
		return transations;
	}

	public TaxClosingBalance getClosingBal() {
		return closingBal;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public void setTransations(TaxTransactionDetails transations) {
		this.transations = transations;
	}

	public void setClosingBal(TaxClosingBalance closingBal) {
		this.closingBal = closingBal;
	}

}
