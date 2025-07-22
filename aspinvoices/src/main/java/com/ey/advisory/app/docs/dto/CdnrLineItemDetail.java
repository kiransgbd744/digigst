package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Umesha.M
 *
 */
@Data
public class CdnrLineItemDetail {

	@Expose
	@SerializedName("rt")
	private BigDecimal rate;

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("iamt")
	private BigDecimal igstAmount;

	@Expose
	@SerializedName("camt")
	private BigDecimal cgstAmount;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;
	
	@Expose
	@SerializedName("rsn")
	private String reasonIcredeb;
	
	@Expose
	@SerializedName("irt")
	private BigDecimal igstRate;
	
	@Expose
	@SerializedName("crt")
	private BigDecimal cgstRate;
	
	@Expose
	@SerializedName("srt")
	private BigDecimal sgstRate;
	
	@Expose
	@SerializedName("csrt")
	private BigDecimal cessRate;
	
	@Expose
	@SerializedName("rchrg")
	private String reverceCharge;

}
