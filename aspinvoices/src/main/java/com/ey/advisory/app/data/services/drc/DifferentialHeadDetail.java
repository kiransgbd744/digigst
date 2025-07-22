package com.ey.advisory.app.data.services.drc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DifferentialHeadDetail {

	@SerializedName("oth")
	@Expose
	private DifferenceAmtDetails oth;

	@SerializedName("intr")
	@Expose
	private DifferenceAmtDetails intr;

	@SerializedName("tx")
	@Expose
	private DifferenceAmtDetails tx;

	@SerializedName("fee")
	@Expose
	private DifferenceAmtDetails fee;

	@SerializedName("pen")
	@Expose
	private DifferenceAmtDetails pen;

}
