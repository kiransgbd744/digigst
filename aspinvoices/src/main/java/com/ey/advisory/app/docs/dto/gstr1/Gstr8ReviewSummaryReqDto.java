package com.ey.advisory.app.docs.dto.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr8ReviewSummaryReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	/**
	 * 
	 */
	public Gstr8ReviewSummaryReqDto() {
		super();
	}

}
