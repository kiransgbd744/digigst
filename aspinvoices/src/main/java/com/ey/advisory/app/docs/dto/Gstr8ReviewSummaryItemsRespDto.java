package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr8ReviewSummaryItemsRespDto {

	@Expose
	@SerializedName("section")
	private String section;

	@Expose
	@SerializedName("aspGrossSuppliesMade")
	private BigDecimal aspGrossSuppliesMade;

	@Expose
	@SerializedName("aspGrossSuppliesReturned")
	private BigDecimal aspGrossSuppliesReturned;

}
