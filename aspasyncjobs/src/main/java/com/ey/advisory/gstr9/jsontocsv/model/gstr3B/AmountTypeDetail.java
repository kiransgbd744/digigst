package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AmountTypeDetail {

	@SerializedName("intr")
	@Expose
	private String interest;

	@SerializedName("tx")
	@Expose
	private String tax;

	@SerializedName("fee")
	@Expose
	private String fee;

	public String getInterest() {
		return interest;
	}

	public String getTax() {
		return tax;
	}

	public String getFee() {
		return fee;
	}

	@Override
	public String toString() {
		return "AmountTypeDetail [interest="
				+ interest + ", tax=" + tax + ", fee="+ fee +"]";
	}
}
