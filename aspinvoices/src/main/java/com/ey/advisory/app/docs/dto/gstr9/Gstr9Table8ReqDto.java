package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table8ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName(value = "itc_2a",alternate = { "itc_2b" })
	private Gstr9Table8Itc2AReqDto gstr9Table8Itc2AReqDto;

	@Expose
	@SerializedName("itc_tot")
	private Gstr9Table8ItcTotReqDto gstr9Table8ItcTotReqDto;

	@Expose
	@SerializedName("itc_inwd_supp")
	private Gstr9Table8ItcInwdSuppReqDto gstr9Table8ItcInwdSuppReqDto;

	@Expose
	@SerializedName("itc_nt_availd")
	private Gstr9Table8ItcNtAvaildReqDto gstr9Table8ItcNtAvaildReqDto;

	@Expose
	@SerializedName("itc_nt_eleg")
	private Gstr9Table8ItcNtElegReqDto gstr9Table8ItcNtElegReqDto;

	@Expose
	@SerializedName("iog_taxpaid")
	private Gstr9Table8IogTaxPaidReqDto gstr9Table8IogTaxPaidReqDto;

	@Expose
	@SerializedName("iog_itc_availd")
	private Gstr9Table8IogItcAvaildReqDto gstr9Table8IogItcAvaildReqDto;

	@Expose
	@SerializedName("iog_itc_ntavaild")
	private Gstr9Table8IogItcNtAvaildReqDto gstr9Table8IogItcNtAvaildReqDto;

	@Expose
	@SerializedName("differenceABC")
	private Gstr9Table8DifferenceABCReqDto gstr9Table8DifferenceABCReqDto;

	@Expose
	@SerializedName("differenceGH")
	private Gstr9Table8DifferenceGhReqDto gstr9Table8DifferenceGhReqDto;

	@Expose
	@SerializedName("tot_itc_lapsed")
	private Gstr9Table8TotItcLapsedReqDto gstr9Table8TotItcLapsedReqDto;

}
