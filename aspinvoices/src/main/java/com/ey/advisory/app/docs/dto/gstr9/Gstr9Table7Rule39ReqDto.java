package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table7Rule39ReqDto {

	@Expose
	@SerializedName("camt")
	private BigDecimal camt = new BigDecimal("0.00");

	@Expose
	@SerializedName("samt")
	private BigDecimal samt = new BigDecimal("0.00");

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt = new BigDecimal("0.00");

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt = new BigDecimal("0.00");
	 
}
