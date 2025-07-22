package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1DocIssuedBasicSummary {
	
	@Expose
	@SerializedName("ey")
	private List<Gstr1DocIssuedSummarySectionDto> eySummary;
	
	@Expose
	@SerializedName("gstn")
	private List<Gstr1DocIssuedSummarySectionDto> gstnSummary;
	
	@Expose
	@SerializedName("diff")
	private Gstr1DocIssuedSummarySectionDto diffSummary;
	
	
	
	
	/**
	 * @return the eySummary
	 */
	public List<Gstr1DocIssuedSummarySectionDto> getEySummary() {
		return eySummary;
	}
	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr1DocIssuedSummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}
	public List<Gstr1DocIssuedSummarySectionDto> getGstnSummary() {
		return gstnSummary;
	}
	public void setGstnSummary(List<Gstr1DocIssuedSummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}
	public Gstr1DocIssuedSummarySectionDto getDiffSummary() {
		return diffSummary;
	}
	public void setDiffSummary(Gstr1DocIssuedSummarySectionDto diffSummary) {
		this.diffSummary = diffSummary;
	}
	
	

}
