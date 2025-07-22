package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table14IamtReqDto {

	@Expose
	@SerializedName("txpyble")
	private BigDecimal txpyble;

	@Expose
	@SerializedName("txpaid")
	private BigDecimal txpaid = new BigDecimal("0.00");
	
}
