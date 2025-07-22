package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6ItemDetails {

	@Expose
	@SerializedName("txval")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("rt")
	private BigDecimal rate;

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

	

}
