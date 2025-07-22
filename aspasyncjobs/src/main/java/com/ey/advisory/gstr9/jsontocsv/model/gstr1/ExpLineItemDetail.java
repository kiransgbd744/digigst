package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpLineItemDetail {

	@Expose
	@SerializedName("rt")
	private String rate;

	@Expose
	@SerializedName("iamt")
	private String igstAmount;

	@Expose
	@SerializedName("csamt")
	private String cessAmount;

	@Expose
	@SerializedName("txval")
	private String taxableValue;

	public String getRate() {
		return rate;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	@Override
	public String toString() {
		return "ExpLineItemDetail [rate=" + rate + ", igstAmount=" + igstAmount
				+ ", cessAmount=" + cessAmount + ", taxableValue="
				+ taxableValue + "]";
	}

	

}
