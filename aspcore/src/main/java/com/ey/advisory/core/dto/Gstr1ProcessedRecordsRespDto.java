package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class Gstr1ProcessedRecordsRespDto {

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
	@SerializedName("taxDocType")
	private String taxDocType;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("count")
	private BigInteger count;

	@Expose
	@SerializedName("supplies")
	private BigDecimal supplies;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst;

	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	@Expose
	@SerializedName("timeStamp")
	private String timeStamp;

	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("invSerTimeStamp")
	private String invTimeStamp;

	@Expose
	@SerializedName("invSerStatus")
	private String invSerStatus;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("stateCode")
	private String stateCode;
}
