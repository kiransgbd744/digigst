package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class HSNSummaryInvData {

	@Expose
	@SerializedName("num")
	private Integer serialNumber;
	
	@Expose
	@SerializedName("hsn_sc")
	private String hsnGoodsOrService;
	
	@Expose
	@SerializedName("desc")
	private String descOfGoodsSold;
	
	@Expose
	@SerializedName("uqc")
	private String unitOfMeasureOfGoodsSold;
	
	@Expose
	@SerializedName("qty")
	private BigDecimal qtyOfGoodsSold;
	
	@Expose
	@SerializedName("val")
	private BigDecimal totalValue;
	
	@Expose
	@SerializedName("txval")
	private BigDecimal taxValOfGoodsOrService;
	
	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;
	
	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmount;
	
	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;
	
	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;
	
	@Expose
	@SerializedName("rt")
	private BigDecimal taxRate;
	
	@Expose
	@SerializedName("user_desc")
	private String userDescOfGoodsSold;

}
