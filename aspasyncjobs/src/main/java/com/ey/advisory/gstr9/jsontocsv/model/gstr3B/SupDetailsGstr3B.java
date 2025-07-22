package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupDetailsGstr3B {

	@SerializedName("txval")
	@Expose
	private String taxableValue;

	@SerializedName("iamt")
	@Expose
	private String igstAmount;

	@SerializedName("camt")
	@Expose
	private String cgstAmount;

	@SerializedName("samt")
	@Expose
	private String sgstAmount;

	@SerializedName("csamt")
	@Expose
	private String cessAmount;

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
		return "SupDetailsGstr3B [taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + ", cgstAmount="
				+ cgstAmount + ", sgstAmount=" + sgstAmount + ", cessAmount="
				+ cessAmount + "]";
	}
}
