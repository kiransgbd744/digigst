package com.ey.advisory.app.gstr3b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr3bRatioUserInputDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("userInputRatio1")
	private String userInputRatio1;

	@Expose
	@SerializedName("userInputRatio2")
	private String userInputRatio2;

}
