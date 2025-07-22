package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnrLineItemDetail {

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

	public void setRate(String rate) {
		this.rate = rate;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public void setIgstAmount(String igstAmount) {
		this.igstAmount = igstAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

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

	public String getCgstAmount() {
		return cgstAmount;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	@Override
	public String toString() {
		return "CdnrLineItemDetail [rate=" + rate + ", taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount + ", cessAmount="
				+ cessAmount + "]";
	}

}
