package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table14ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("iamt")
	private Gstr9Table14IamtReqDto gstr9Table14IamtReqDto;

	@Expose
	@SerializedName("samt")
	private Gstr9Table14SamtReqDto gstr9Table14SamtReqDto;

	@Expose
	@SerializedName("camt")
	private Gstr9Table14CamtReqDto gstr9Table14CamtReqDto;

	@Expose
	@SerializedName("csamt")
	private Gstr9Table14CSamtReqDto gstr9Table14CSamtReqDto;

	@Expose
	@SerializedName("intr")
	private Gstr9Table14IntrReqDto gstr9Table14IntrReqDto;

}
