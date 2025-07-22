package com.ey.advisory.app.docs.dto.anx1;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class TtcDetails {

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("totalItc")
	private TotalItc totalItc;
	
	@Expose
	@SerializedName("inelgitc")
	private TotalItc inelgitc;
	
	@Expose
	@SerializedName("elgitc")
	private TotalItc elgitc;
	
	@Expose
	@SerializedName("isdItcCross")
	private TotalItc isdItcCross;
	
	
	
}
