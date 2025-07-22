package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table7ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("rule37")
	private Gstr9Table7Rule37ReqDto gstr9Table7Rule37ReqDto;

	@Expose
	@SerializedName("rule39")
	private Gstr9Table7Rule39ReqDto gstr9Table7Rule39ReqDto;

	@Expose
	@SerializedName("rule42")
	private Gstr9Table7Rule42ReqDto gstr9Table7Rule42ReqDto;

	@Expose
	@SerializedName("rule43")
	private Gstr9Table7Rule43ReqDto gstr9Table7Rule43ReqDto;

	@Expose
	@SerializedName("sec17")
	private Gstr9Table7Sec17ReqDto gstr9Table7Sec17ReqDto;

	@Expose
	@SerializedName("revsl_tran1")
	private Gstr9Table7RevsTrans1ReqDto gstr9Table7RevsTrans1ReqDto;

	@Expose
	@SerializedName("revsl_tran2")
	private Gstr9Table7RevsTrans2ReqDto gstr9Table7RevsTrans2ReqDto;

	@Expose
	@SerializedName("other")
	private List<Gstr9Table7OtherReqDto> gstr9Table7OtherReqDto;

	@Expose
	@SerializedName("tot_itc_revd")
	private Gstr9Table7TotItcRevdReqDto gstr9Table7TotItcRevdReqDto;

	@Expose
	@SerializedName("net_itc_aval")
	private Gstr9Table7NetItcAvalReqDto gstr9Table7NetItcAvalReqDto;

}
