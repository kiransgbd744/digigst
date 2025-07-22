package com.ey.advisory.gstr2.initiaterecon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EWBSummaryDataRequestStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Expose private Long requestId = 0L;
	 */
	@Expose
	private String gstin;

	@Expose
	private String ewbStatus;

	@Expose
	private BigInteger countOfRecords;

	@Expose
	private BigDecimal taxableValue;

	@Expose
	private BigDecimal totalTax;

	@Expose
	private BigDecimal invoiceValue;

	@Expose
	private String createdOn;

	@Expose
	@SerializedName("stateName")
	private String stateName;

	@Expose
	@SerializedName("authStatus")
	private String auth;
	
	@Expose
	private String regType;
	
	@Expose
	private String ewbGetCallStatus;

	@Expose
	private String ewbGetCallIniTime;
}
