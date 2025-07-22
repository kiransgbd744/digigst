package com.ey.advisory.controller.gl.recon;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;


@Data
public class GLProcessedRecordsRespDto {

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("count")
	private BigDecimal count = BigDecimal.ZERO;

	@Expose
	@SerializedName("assessableAmt")
	private BigDecimal assessableAmt = BigDecimal.ZERO;

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


}

