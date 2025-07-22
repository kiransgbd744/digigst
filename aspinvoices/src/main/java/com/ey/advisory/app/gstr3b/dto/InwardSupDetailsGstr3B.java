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
public class InwardSupDetailsGstr3B {

	@SerializedName("ty")
	@Expose
	private String ty;

	@SerializedName("inter")
	@Expose
	private BigDecimal inter = BigDecimal.ZERO;

	@SerializedName("intra")
	@Expose
	private BigDecimal intra = BigDecimal.ZERO;

	
}
