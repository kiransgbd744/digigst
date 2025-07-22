package com.ey.advisory.app.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SectionWiseDetailsDto {

	@SerializedName("section")
	@Expose
	private String section;

	@SerializedName("status")
	@Expose
	private String status;
	
	@SerializedName("taxPeriod")
	@Expose
	private String taxPeriod;
}
