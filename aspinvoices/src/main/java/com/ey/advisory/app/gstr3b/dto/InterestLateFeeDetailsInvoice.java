package com.ey.advisory.app.gstr3b.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InterestLateFeeDetailsInvoice {

	@SerializedName("intr_details")
	@Expose
	private InterestandLatefeeDetails interestDetails;

	@SerializedName("ltfee_details")
	@Expose
	private InterestandLatefeeDetails latetfeeDetails;

	public InterestandLatefeeDetails getInterestDetails() {
		return interestDetails;
	}

	public InterestandLatefeeDetails getLatetfeeDetails() {
		return latetfeeDetails;
	}

	@Override
	public String toString() {
		return "InterestLateFeeDetailsInvoice [interestDetails="
				+ interestDetails + ", latetfeeDetails=" + latetfeeDetails + "]";
	}
}
