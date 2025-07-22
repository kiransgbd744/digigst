package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItcDetailsGstr3B {

	@SerializedName("ty")
	@Expose
	private String ty;

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

	public String getTy() {
		return ty;
	}

	public String getIamt() {
		return igstAmount;
	}

	public String getCamt() {
		return cgstAmount;
	}

	public String getSamt() {
		return sgstAmount;
	}

	public String getCsamt() {
		return cessAmount;
	}

	@Override
	public String toString() {
		return "ItcDetailsGstr3B [ty=" + ty + ", igstAmount=" + igstAmount + ","
				+ " cgstAmount=" + cgstAmount + ",sgstAmount=" + sgstAmount
				+ "," + "cessAmount=" + cessAmount + "]";
	}
}
