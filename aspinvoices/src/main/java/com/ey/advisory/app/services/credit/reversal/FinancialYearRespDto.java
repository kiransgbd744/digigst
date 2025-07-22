package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FinancialYearRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("statusTimestamp")
	private String statusTimestamp;

	@Expose
	@SerializedName("totalTaxAmount")
	private BigDecimal totalTaxAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("aprilRevRatio")
	private BigDecimal aprilRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("aprilTaxamount")
	private BigDecimal aprilTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("mayRevRatio")
	private BigDecimal mayRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("mayTaxamount")
	private BigDecimal mayTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("juneRevRatio")
	private BigDecimal juneRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("juneTaxamount")
	private BigDecimal juneTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyRevRatio")
	private BigDecimal julyRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyTaxamount")
	private BigDecimal julyTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("augRevRatio")
	private BigDecimal augRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("augTaxamount")
	private BigDecimal augTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepRevRatio")
	private BigDecimal sepRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepTaxamount")
	private BigDecimal sepTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("octRevRatio")
	private BigDecimal octRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("octTaxamount")
	private BigDecimal octTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("novRevRatio")
	private BigDecimal novRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("novTaxamount")
	private BigDecimal novTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("decRevRatio")
	private BigDecimal decRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("decTaxamount")
	private BigDecimal decTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("janRevRatio")
	private BigDecimal janRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("janTaxamount")
	private BigDecimal janTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("febRevRatio")
	private BigDecimal febRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("febTaxamount")
	private BigDecimal febTaxamount = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchRevRatio")
	private BigDecimal marchRevRatio = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchTaxamount")
	private BigDecimal marchTaxamount = BigDecimal.ZERO;

}
