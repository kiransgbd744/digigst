package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr9Table6IogReqDto {
	
	@Expose
	@SerializedName("itc_typ")
	private String itctyp = "";

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt = new BigDecimal("0.00");

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt = new BigDecimal("0.00");
	
}
