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
public class AmountTypeDetail {

	@SerializedName("intr")
	@Expose
	private String interest;

	@SerializedName("tx")
	@Expose
	private BigDecimal tax = BigDecimal.ZERO;

	@SerializedName("fee")
	@Expose
	private String fee;

}
