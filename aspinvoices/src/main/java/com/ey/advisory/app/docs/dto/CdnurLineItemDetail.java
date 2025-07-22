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
public class CdnurLineItemDetail {

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
	@SerializedName("csamt")
	private BigDecimal cessAmount;
	
}
