package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditTurnOverDto {

	@Expose
	@SerializedName("turnoverComp")
	private String turnoverComp;

	@Expose
	@SerializedName("digigstAutoComp")
	private String digigstAutoComp;
	
	@Expose
	@SerializedName("userInputRatio")
	private String userInputRatio;

	@Expose
	@SerializedName("sno")
	private String sno;

	@Expose
	@SerializedName("items")
	private List<CreditTurnOverItemUiDto> items;
}
