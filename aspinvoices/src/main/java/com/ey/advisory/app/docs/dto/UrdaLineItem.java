package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Data
public class UrdaLineItem {

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("eid")
	private String enrolmentId;

	@Expose
	@SerializedName("oeid")
	private String originalEnrolmentId;

	@Expose
	@SerializedName("ofp")
	private String originalFinancialPeriod;

	@Expose
	@SerializedName("grsval")
	private BigDecimal grossSuppliesMadeRegistered;

	@Expose
	@SerializedName("supret")
	private BigDecimal grossSuppliesReturnedRegistered;

	@Expose
	@SerializedName("amt")
	private BigDecimal netAmount;

}
