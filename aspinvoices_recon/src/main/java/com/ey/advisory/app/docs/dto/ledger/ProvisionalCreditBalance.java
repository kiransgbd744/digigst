package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ProvisionalCreditBalance {

	@Expose
	@SerializedName("provCrdBal")
	private ProvisionalCreditBalanceDetails provisionalCredit;

	public ProvisionalCreditBalanceDetails getProvisionalCredit() {
		return provisionalCredit;
	}

	public void setProvisionalCredit(
			ProvisionalCreditBalanceDetails provisionalCredit) {
		this.provisionalCredit = provisionalCredit;
	}

}
