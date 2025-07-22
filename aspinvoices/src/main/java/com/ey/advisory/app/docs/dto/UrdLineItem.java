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
public class UrdLineItem {

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("eid")
	private String enrolmentId;

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
