package com.ey.advisory.app.docs.dto.gstr9;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table16ReqDto {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@SerializedName("comp_supp")
	@Expose
	private Gstr9Table16CompSuppReqDto gstr9Table16CompSuppReqDto;
	/**
	* Deemed supply under Section 143
	*
	*/
	@SerializedName("deemed_supp")
	@Expose
	private Gstr9Table16DeemedSuppReqDto gstr9Table16DeemedSuppReqDto;
	/**
	* Goods sent on approval basis but not returned
	*
	*/
	@SerializedName("not_returned")
	@Expose
	private Gstr9Table16NotReturnedReqDto gstr9Table16NotReturnedReqDto;

}
