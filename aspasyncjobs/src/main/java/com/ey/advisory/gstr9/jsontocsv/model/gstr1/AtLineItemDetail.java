package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AtLineItemDetail {

	@Expose
	@SerializedName("rt")
	private String rate;

	@Expose
	@SerializedName("ad_amt")
	private String advanceReceived;

	@Expose
	@SerializedName("iamt")
	private String igstAmount;

	@Expose
	@SerializedName("camt")
	private String cgstAmount;

	@Expose
	@SerializedName("samt")
	private String sgstAmount;

	@Expose
	@SerializedName("csamt")
	private String cessAmount;

	public String getAdvanceReceived() {
		return advanceReceived;
	}

	public String getRate() {
		return rate;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	@Override
	public String toString() {
		return "AtLineItemDetail [rate=" + rate + ", advanceReceived="
				+ advanceReceived + ", igstAmount=" + igstAmount
				+ ", cgstAmount=" + cgstAmount + ", sgstAmount=" + sgstAmount
				+ ", cessAmount=" + cessAmount + "]";
	}

}
