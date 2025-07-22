package com.ey.advisory.app.gstr3b.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InwardSupDetailsInvoiceGstr3B {

	@SerializedName("isup_details")
	@Expose
	private List<InwardSupDetailsGstr3B> inwardsupDetails;

	public List<InwardSupDetailsGstr3B> getIsupDetails() {
		return inwardsupDetails;
	}

	@Override
	public String toString() {
		return "InwardSupDetailsInvoiceGstr3B [inwardsupDetails="
				+ inwardsupDetails + "]";
	}
}
