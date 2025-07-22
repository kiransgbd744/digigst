package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditTurnOverItemDto {

	@Expose
	@SerializedName("sno")
	private String sno;
	
	private String taxPeriod;

	@Expose
	@SerializedName("turnoverComp")
	private String turnoverComp;

	@Expose
	@SerializedName("digigstAutoComp")
	private BigDecimal digigstAutoComp = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("ratio1")
	private BigDecimal ratio1 = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("ratio2")
	private BigDecimal ratio2 = BigDecimal.ZERO;


	@Expose
	@SerializedName("ratio3")
	private BigDecimal ratio3 = BigDecimal.ZERO;
}
