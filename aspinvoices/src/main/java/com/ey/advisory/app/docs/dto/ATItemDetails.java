package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ATItemDetails {

	@Expose
	@SerializedName("rt")
	private BigDecimal rt;

	@Expose
	@SerializedName("ad_amt")
	private BigDecimal adAmt;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt;
	
}
