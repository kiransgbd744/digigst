package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table10ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("dbn_amd")
	private Gstr9Table10DbnAmdReqDto dbnAmdReqDto;

	@Expose
	@SerializedName("cdn_amd")
	private Gstr9Table10CdnAmdReqDto cdnAmdReqDto;

	@Expose
	@SerializedName("itc_rvsl")
	private Gstr9Table10ItcRvslReqDto gstr9Table10ItcRvslReqDto;

	@Expose
	@SerializedName("itc_availd")
	private Gstr9Table10ItcAvaildReqDto gstr9Table10ItcAvaildReqDto;

	@Expose
	@SerializedName("total_turnover")
	private Gstr9Table10TotalTurnOverReqDto gstr9Table10TotalTurnOverReqDto;
}
