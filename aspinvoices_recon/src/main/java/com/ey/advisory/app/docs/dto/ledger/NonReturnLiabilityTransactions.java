package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class NonReturnLiabilityTransactions {

	@Expose
	@SerializedName("demandid")
	private String demandId;

	@Expose
	@SerializedName("dtl")
	private NonReturnLiabilityTransactionDetails detailTransactions;

	public String getDemandId() {
		return demandId;
	}

	public NonReturnLiabilityTransactionDetails getDetailTransactions() {
		return detailTransactions;
	}

	public void setDemandId(String demandId) {
		this.demandId = demandId;
	}

	public void setDetailTransactions(
			NonReturnLiabilityTransactionDetails detailTransactions) {
		this.detailTransactions = detailTransactions;
	}

}
