package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1AllSummaryDto {
	
	@Expose
	@SerializedName("ey")
	private List<Gstr1BasicSummarySectionDto> eySummary;

	public List<Gstr1BasicSummarySectionDto> getEySummary() {
		return eySummary;
	}

	public void setEySummary(List<Gstr1BasicSummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	@Override
	public String toString() {
		return "Gstr1AllSummaryDto [eySummary=" + eySummary + "]";
		
	}
	
	


}
