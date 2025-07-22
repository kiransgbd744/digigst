package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1VsGstr3bReviewSummaryItemsRespDto {

	@Expose
	@SerializedName("supplies")
	private String supplies;

	@Expose
	@SerializedName("taxDocType")
	private String taxDocType;

	@Expose
	@SerializedName("formula")
	private String formula;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
}
