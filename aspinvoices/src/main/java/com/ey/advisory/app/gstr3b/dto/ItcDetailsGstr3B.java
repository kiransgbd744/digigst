package com.ey.advisory.app.gstr3b.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItcDetailsGstr3B {

	@SerializedName("ty")
	@Expose
	private String ty;

	@SerializedName("iamt")
	@Expose
	private BigDecimal igstAmount = BigDecimal.ZERO;

	@SerializedName("camt")
	@Expose
	private BigDecimal cgstAmount = BigDecimal.ZERO;

	@SerializedName("samt")
	@Expose
	private BigDecimal sgstAmount = BigDecimal.ZERO;

	@SerializedName("csamt")
	@Expose
	private BigDecimal cessAmount = BigDecimal.ZERO;

}
