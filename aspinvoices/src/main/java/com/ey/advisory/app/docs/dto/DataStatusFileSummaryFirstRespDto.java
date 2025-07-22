package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DataStatusFileSummaryFirstRespDto {

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("count")
	private BigInteger count = BigInteger.ZERO;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalTaxes")
	private BigDecimal totalTaxes = BigDecimal.ZERO;

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
	@SerializedName("items")
	private List<DataStatusFileSummarySecondCountRespDto> items = new ArrayList<>();

}
