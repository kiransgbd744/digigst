package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9ItemsResponseDto {

	@SerializedName("hsn_sc")
	@Expose
	private String hsnSc;

	@SerializedName("uqc")
	@Expose
	private String uqc;

	@SerializedName("rt")
	@Expose
	private BigDecimal rt;
	
	@SerializedName("processeddesc")
	@Expose
	private String processeddesc;
	
	@SerializedName("processedqty")
	@Expose
	private BigDecimal processedqty;
	
	@SerializedName("processedtxval")
	@Expose
	private BigDecimal processedtxval;
	
	@SerializedName("isprocessedconcesstional")
	@Expose
	private String isprocessedconcesstional;

	@SerializedName("processedigst")
	@Expose
	private BigDecimal processedigst;

	@SerializedName("processedcgst")
	@Expose
	private BigDecimal processedcgst;

	@SerializedName("processedsgst")
	@Expose
	private BigDecimal processedsgst;

	@SerializedName("processedcess")
	@Expose
	private BigDecimal processedcess;
	
	@SerializedName("userdesc")
	@Expose
	private String userdesc;
	
	@SerializedName("userqty")
	@Expose
	private BigDecimal userqty;
	
	@SerializedName("usertxval")
	@Expose
	private BigDecimal usertxval;
	
	@SerializedName("isuserconcesstional")
	@Expose
	private String isuserconcesstional;

	@SerializedName("userigst")
	@Expose
	private BigDecimal userigst;

	@SerializedName("usercgst")
	@Expose
	private BigDecimal usercgst;

	@SerializedName("usersgst")
	@Expose
	private BigDecimal usersgst;

	@SerializedName("usercess")
	@Expose
	private BigDecimal usercess;

}
