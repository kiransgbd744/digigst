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
public class CreditTurnOverItemUiDto {

	@Expose
	@SerializedName("sno")
	private String sno;

	@Expose
	@SerializedName("turnoverComp")
	private String turnoverComp;

	@Expose
	@SerializedName("digigstAutoComp")
	private BigDecimal digigstAutoComp = BigDecimal.ZERO;

}
