package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table5DbNtReqDto {

	@Expose
	@SerializedName("txval")
	private BigDecimal txval = new BigDecimal("0.00");
}
