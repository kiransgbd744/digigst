package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1NillratedBasicSummaryDto {
	@Expose
	@SerializedName("ey")
	private List<Gstr1NilRatedSummarySectionDto> eySummary;
	@Expose
	@SerializedName("gstn")
	private List<Gstr1NilRatedSummarySectionDto> gstnSummary;
	@Expose
	@SerializedName("diff")
	private Gstr1NilRatedSummarySectionDto diffSummary;
	
	public List<Gstr1NilRatedSummarySectionDto> getEySummary() {
		return eySummary;
	}
	public void setEySummary(List<Gstr1NilRatedSummarySectionDto> eySummary) {
		this.eySummary =  eySummary;
	}
	public List<Gstr1NilRatedSummarySectionDto> getGstnSummary() {
		return (List<Gstr1NilRatedSummarySectionDto>) gstnSummary;
	}
	public void setGstnSummary(List<Gstr1NilRatedSummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}
	public Gstr1NilRatedSummarySectionDto getDiffSummary() {
		return diffSummary;
	}
	public void setDiffSummary(Gstr1NilRatedSummarySectionDto diffSummary) {
		this.diffSummary = diffSummary;
	}

}
