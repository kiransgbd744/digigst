package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr2ProcessedRecordsFinalRespDto {
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
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("lastUpdate")
	private String lastUpdate;

	@Expose
	@SerializedName("count")
	private BigInteger count;

	@Expose
	@SerializedName("invoiceValue")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("taxPayable")
	private BigDecimal taxPayable;

	@Expose
	@SerializedName("taxPayableIgst")
	private BigDecimal taxPayableIgst;

	@Expose
	@SerializedName("taxPayableCgst")
	private BigDecimal taxPayableCgst;

	@Expose
	@SerializedName("taxPayableSgst")
	private BigDecimal taxPayableSgst;

	@Expose
	@SerializedName("taxPayableCess")
	private BigDecimal taxPayableCess;

	@Expose
	@SerializedName("crEligibleTotal")
	private BigDecimal crEligibleTotal;

	@Expose
	@SerializedName("crEligibleIgst")
	private BigDecimal crEligibleIgst;

	@Expose
	@SerializedName("crEligibleCgst")
	private BigDecimal crEligibleCgst;

	@Expose
	@SerializedName("crEligibleSgst")
	private BigDecimal crEligibleSgst;

	@Expose
	@SerializedName("crEligibleCess")
	private BigDecimal crEligibleCess;

}