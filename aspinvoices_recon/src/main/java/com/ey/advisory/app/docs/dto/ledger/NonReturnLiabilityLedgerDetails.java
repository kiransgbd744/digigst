package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class NonReturnLiabilityLedgerDetails {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("tr")
	private NonReturnLiabilityTransactions nonRetTransactions;

	public String getGstin() {
		return gstin;
	}

	public NonReturnLiabilityTransactions getNonRetTransactions() {
		return nonRetTransactions;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public void setNonRetTransactions(
			NonReturnLiabilityTransactions nonRetTransactions) {
		this.nonRetTransactions = nonRetTransactions;
	}

}
