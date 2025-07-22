package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr2xTcdsSummaryResDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName("tds")
	private Gstr2xTdsTcsSummaryResDto tds;
	
	@Expose
	@SerializedName("tdsa")
	private Gstr2xTdsTcsSummaryResDto tdsa;
	
	@Expose
	@SerializedName("tcs")
	private Gstr2xTdsTcsSummaryResDto tcs = new Gstr2xTdsTcsSummaryResDto();
	
	@Expose
	@SerializedName("tcsa")
	private Gstr2xTdsTcsSummaryResDto tcsa = new Gstr2xTdsTcsSummaryResDto();

}
