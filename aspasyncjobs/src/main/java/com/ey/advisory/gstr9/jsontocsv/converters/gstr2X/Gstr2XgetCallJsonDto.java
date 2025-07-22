package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Gstr2XgetCallJsonDto {

	@SerializedName("req_time")
	@Expose
	private String reqtime;

	@SerializedName("tds")
	@Expose
	private List<Gstr2XgetCallJsonTdsDto> tdsList;

	@SerializedName("tdsa")
	@Expose
	private List<Gstr2XgetCallJsonTdsaDto> tdsaList;

	@SerializedName("tcs")
	@Expose
	private List<Gstr2XgetCallJsonTcsDto> tcsList;

	@SerializedName("tcsa")
	@Expose
	private List<Gstr2XgetCallJsonTcsaDto> tcsaList;
	
	@SerializedName("summary")
	@Expose
	private Gstr2XgetCallSummeryDto summaryDto;
	
	
}
