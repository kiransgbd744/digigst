package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr8ReviewSummaryItemsRespDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Gstr8ReviewSummaryRespDto {

	@Expose
	@SerializedName("section")
	private String section;
	@Expose
	@SerializedName("aspGrossSuppliesMade")
	private BigDecimal aspGrossSuppliesMade = BigDecimal.ZERO;
	@Expose
	@SerializedName("aspGrossSuppliesReturned")
	private BigDecimal aspGrossSuppliesReturned = BigDecimal.ZERO;
	@Expose
	@SerializedName("aspNetSupplies")
	private BigDecimal aspNetSupplies = BigDecimal.ZERO;

	@Expose
	@SerializedName("aspIgst")
	private BigDecimal aspIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("aspCgst")
	private BigDecimal aspCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("aspSgst")
	private BigDecimal aspSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("gstnGrossSuppliesMade")
	private BigDecimal gstnGrossSuppliesMade = BigDecimal.ZERO;
	@Expose
	@SerializedName("gstnGrossSuppliesReturned")
	private BigDecimal gstnGrossSuppliesReturned = BigDecimal.ZERO;
	@Expose
	@SerializedName("gstnNetSupplies")
	private BigDecimal gstnNetSupplies = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstnIgst")
	private BigDecimal gstnIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstnCgst")
	private BigDecimal gstnCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstnSgst")
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("aspCount")
	private BigInteger aspCount = BigInteger.ZERO;
	
	@Expose
	@SerializedName("gstnCount")
	private BigInteger gstnCount = BigInteger.ZERO;

	@Expose
	@SerializedName("items")
	private List<Gstr8ReviewSummaryItemsRespDto> items = new ArrayList<Gstr8ReviewSummaryItemsRespDto>();

}
