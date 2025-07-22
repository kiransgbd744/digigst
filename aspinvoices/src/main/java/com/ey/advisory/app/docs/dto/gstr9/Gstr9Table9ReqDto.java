package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table9ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("iamt")
	private Gstr9Table9IamtReqDto gstr9Table9IamtReqDto;

	@Expose
	@SerializedName("camt")
	private Gstr9Table9CamtReqDto gstr9Table9CamtReqDto;

	@Expose
	@SerializedName("samt")
	private Gstr9Table9SamtReqDto gstr9Table9SamtReqDto;

	@Expose
	@SerializedName("csamt")
	private Gstr9Table9CSamtReqDto gstr9Table9CsamtReqDto;

	@Expose
	@SerializedName("intr")
	private Gstr9Table9IntrReqDto gstr9Table9IntrReqDto;

	@Expose
	@SerializedName("fee")
	private Gstr9Table9TeeReqDto gstr9Table9TeeReqDto;

	@Expose
	@SerializedName("pen")
	private Gstr9Table9PenReqDto gstr9Table9PenReqDto;

	@Expose
	@SerializedName("other")
	private Gstr9Table9OtherReqDto gstr9Table9OtherReqDto;

}
