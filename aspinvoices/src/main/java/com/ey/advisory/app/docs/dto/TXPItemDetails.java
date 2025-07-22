package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TXPItemDetails {

	@Expose
	@SerializedName("rt")
	private BigDecimal rateOfInvoice;
	
	@Expose
	@SerializedName("ad_amt")
	private BigDecimal advanceToBeAdjested;
	
	@Expose
	@SerializedName("iamt")
	private BigDecimal iGstAmount;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal cGstAmount;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal sGstAmount;
	
	@Expose
	@SerializedName("csamt")
	private BigDecimal csGstAmount;


}
