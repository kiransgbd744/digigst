package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CDNRUInvoices {

	
	@Expose
	@SerializedName("ctin")
	private String cpGstin;
	
	@Expose
	@SerializedName("cfs")
	private String counFillStatus;
	
	@Expose
	@SerializedName("nt")
	private List<CreditDebitNote> creditDebitNoteDetails;

	/**
	 * @return the cpGstin
	 */
	public String getCpGstin() {
		return cpGstin;
	}

	/**
	 * @param cpGstin the cpGstin to set
	 */
	public void setCpGstin(String cpGstin) {
		this.cpGstin = cpGstin;
	}

	/**
	 * @return the counFillStatus
	 */
	public String getCounFillStatus() {
		return counFillStatus;
	}

	/**
	 * @param counFillStatus the counFillStatus to set
	 */
	public void setCounFillStatus(String counFillStatus) {
		this.counFillStatus = counFillStatus;
	}

	/**
	 * @return the creditDebitNoteDetails
	 */
	public List<CreditDebitNote> getCreditDebitNoteDetails() {
		return creditDebitNoteDetails;
	}

	/**
	 * @param creditDebitNoteDetails the creditDebitNoteDetails to set
	 */
	public void setCreditDebitNoteDetails(
			List<CreditDebitNote> creditDebitNoteDetails) {
		this.creditDebitNoteDetails = creditDebitNoteDetails;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CDNRInvoices [cpGstin=" + cpGstin + ", counFillStatus="
				+ counFillStatus + ", creditDebitNoteDetails="
				+ creditDebitNoteDetails + "]";
	}
}
