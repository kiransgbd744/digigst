package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class ITCLedgerDetails {

	@Expose
	@SerializedName("itcLdgDtls")
	private ITCLedger itcLedger;

	@Expose
	@SerializedName("provCrdBalList")
	private ProvisionalCreditBalance provisonalCreditBal;

	public ITCLedger getItcLedger() {
		return itcLedger;
	}

	public ProvisionalCreditBalance getProvisonalCreditBal() {
		return provisonalCreditBal;
	}

	public void setItcLedger(ITCLedger itcLedger) {
		this.itcLedger = itcLedger;
	}

	public void setProvisonalCreditBal(
			ProvisionalCreditBalance provisonalCreditBal) {
		this.provisonalCreditBal = provisonalCreditBal;
	}

}
