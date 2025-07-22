package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InwardSupDetailsGstr3B {

	@SerializedName("ty")
	@Expose
	private String ty;

	@SerializedName("inter")
	@Expose
	private String inter;

	@SerializedName("intra")
	@Expose
	private String intra;

	
	public String getTy() {
		return ty;
	}

	public String getInter() {
		return inter;
	}

	public String getIntra() {
		return intra;
	}

	@Override
	public String toString() {
		return "InwardSupDetailsGstr3B [ty=" + ty + ", inter=" + inter + ","
				+ " intra=" + intra + "]";
	}
}
