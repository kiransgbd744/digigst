package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6ItcDto {

	@Expose
	@SerializedName("totalItc")
	private Gstr6ItcDetails totalItc;

	@Expose
	@SerializedName("elgitc")
	private Gstr6ItcDetails elgItc;

	@Expose
	@SerializedName("inelgitc")
	private Gstr6ItcDetails inelgItc;

	@Expose
	@SerializedName("isdItcCross")
	private Gstr6ItcDetails isdItcCross;
	
	@Expose
	@SerializedName("ret_period")
	private String  retPeriod;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	

}
