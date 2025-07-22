package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Gstr2XgetCallSummeryDto {

	@SerializedName("chksum")
	@Expose
	private String chksum;

	@SerializedName("ret_period")
	@Expose
	private String retperiod;
	
	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("tds")
	@Expose
	private Tds tds;

	@SerializedName("tdsa")
	@Expose
	private Tds tdsa;

	@SerializedName("tcs")
	@Expose
	private Tds tcs;

	@SerializedName("tcsa")
	@Expose
	private Tds tcsa;

}
