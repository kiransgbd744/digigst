package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetImsCountDtlsDto {
	
	//need serializable name of b2bcn, b2bdn, b2ba, b2b, ecom, ecoma, b2bdna, b2bcna, isd, isda, isdcn, isdcna, impg, impga, impgsez, impgseza having data type as List<GetImsCountSectionDtlsDto>
	@Expose
	@SerializedName("b2bcn")
	public GetImsCountSectionDtlsDto b2bcn;

	@Expose
	@SerializedName("b2bdn")
	public GetImsCountSectionDtlsDto b2bdn;

	@Expose
	@SerializedName("b2ba")
	public GetImsCountSectionDtlsDto b2ba;

	@Expose
	@SerializedName("b2b")
	public GetImsCountSectionDtlsDto b2b;

	@Expose
	@SerializedName("ecom")
	public GetImsCountSectionDtlsDto ecom;

	@Expose
	@SerializedName("ecoma")
	public GetImsCountSectionDtlsDto ecoma;

	@Expose
	@SerializedName("b2bdna")
	public GetImsCountSectionDtlsDto b2bdna;

	@Expose
	@SerializedName("b2bcna")
	public GetImsCountSectionDtlsDto b2bcna;

	@Expose
	@SerializedName("isd")
	public GetImsCountSectionDtlsDto isd;

	@Expose
	@SerializedName("isda")
	public GetImsCountSectionDtlsDto isda;

	@Expose
	@SerializedName("isdcn")
	public GetImsCountSectionDtlsDto isdcn;

	@Expose
	@SerializedName("isdcna")
	public GetImsCountSectionDtlsDto isdcna;

	@Expose
	@SerializedName("impg")
	public GetImsCountSectionDtlsDto impg;

	@Expose
	@SerializedName("impga")
	public GetImsCountSectionDtlsDto impga;

	@Expose
	@SerializedName("impgsez")
	public GetImsCountSectionDtlsDto impgsez;

	@Expose
	@SerializedName("impgseza")
	public GetImsCountSectionDtlsDto impgseza;


}
