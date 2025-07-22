package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcceptorRejectDto {
	@SerializedName("tot_amt")
	@Expose
	private String totamt;
	@SerializedName("tot_iamt")
	@Expose
	private String totiamt;
	@SerializedName("tot_camt")
	@Expose
	private String totcamt;
	@SerializedName("tot_samt")
	@Expose
	private String totsamt;
	@SerializedName("tot_count")
	@Expose
	private String totcount;
}
