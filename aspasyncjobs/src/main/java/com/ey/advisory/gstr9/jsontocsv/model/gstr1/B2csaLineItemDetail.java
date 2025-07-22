package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2csaLineItemDetail {

	@Expose @SerializedName("rt")
	private String rate;

	@Expose @SerializedName("txval")
	private String taxableValue;

	@Expose @SerializedName("iamt")
	private String igstAmount;

	@Expose @SerializedName("camt")
	private String cgstAmount;

	@Expose @SerializedName("samt")
	private String sgstAmount;

	@Expose @SerializedName("csamt")
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

	public String getCgstAmount() {
		return cgstAmount;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	@Override
	public String toString() {
		return "B2bLineItemDetail [rate=" + rate + ", taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount + ", cessAmount="
				+ cessAmount + "]";
	}

}
