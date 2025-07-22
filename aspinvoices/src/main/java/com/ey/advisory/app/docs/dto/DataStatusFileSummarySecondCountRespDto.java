package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DataStatusFileSummarySecondCountRespDto {

	@Expose
	@SerializedName("returnSection")
	private String returnSection;

	@Expose
	@SerializedName("returnType")
	private String returnType;
	
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
}
