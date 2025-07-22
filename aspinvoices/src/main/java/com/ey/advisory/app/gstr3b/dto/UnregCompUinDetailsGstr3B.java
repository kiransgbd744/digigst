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
public class UnregCompUinDetailsGstr3B {

	@SerializedName("txval")
	@Expose
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	@SerializedName("pos")
	@Expose
	private String pos;
		
	@SerializedName("iamt")
	@Expose
	private BigDecimal igstAmount = BigDecimal.ZERO;

	
}
