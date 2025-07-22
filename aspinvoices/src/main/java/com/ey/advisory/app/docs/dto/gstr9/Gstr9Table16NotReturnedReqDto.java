package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table16NotReturnedReqDto {
	
	@SerializedName("txval")
	@Expose
	private BigDecimal txval = new BigDecimal("0.00");
	/**
	 * Integrated Tax
	 *
	 */
	@SerializedName("iamt")
	@Expose
	private BigDecimal iamt = new BigDecimal("0.00");
	/**
	 * Central Tax
	 *
	 */
	@SerializedName("camt")
	@Expose
	private BigDecimal camt = new BigDecimal("0.00");
	/**
	 * State/UT tax
	 *
	 */
	@SerializedName("samt")
	@Expose
	private BigDecimal samt = new BigDecimal("0.00");
	/**
	 * Cess
	 *
	 */
	@SerializedName("csamt")
	@Expose
	private BigDecimal csamt = new BigDecimal("0.00");
}
