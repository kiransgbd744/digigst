package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2clLineItemDetail {

	@Expose
	@SerializedName("rt")
	private String rate;

	@Expose
	@SerializedName("txval")
	private String taxableValue;

	@Expose
	@SerializedName("iamt")
	private String igstAmount;

	@Expose
	@SerializedName("csamt")
	private String cessAmount;

	public String getRate() {
		return rate;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	@Override
	public String toString() {
		return "B2clLineItemDetail [rate=" + rate + ", taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + "]";
	}

}
