package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnregCompUinDetailsGstr3B {

	@SerializedName("txval")
	@Expose
	private String taxableValue;
	
	@SerializedName("pos")
	@Expose
	private String pos;
		
	@SerializedName("iamt")
	@Expose
	private String igstAmount;

	public String getTaxableValue() {
		return taxableValue;
	}
	
	public String getPos() {
		return pos;
	}

	public String getIgstAmount() {
		return igstAmount;
	}

	@Override
	public String toString() {
		return "UnregCompUinDetailsGstr3B [taxableValue="
				+ taxableValue + ", igstAmount=" + igstAmount + ", pos="
				+ pos + "]";
	}
}
