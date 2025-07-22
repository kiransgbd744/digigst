package com.ey.advisory.app.docs.dto;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public class Gstr1BasicSummaryDto {

	@Expose
	@SerializedName("ey")
	private List<Gstr1BasicSummarySectionDto> eySummary;

	@Expose
	@SerializedName("gstn")	
	private List<Gstr1BasicSummarySectionDto> gstnSummary;
	
	@Expose
	@SerializedName("diff")	
	private List<Gstr1BasicSummarySectionDto> diffSummary;

	public List<Gstr1BasicSummarySectionDto> getEySummary() {
		return eySummary;
	}

	public void setEySummary(List<Gstr1BasicSummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	public List<Gstr1BasicSummarySectionDto> getGstnSummary() {
		return gstnSummary;
	}

	public void setGstnSummary(List<Gstr1BasicSummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	public List<Gstr1BasicSummarySectionDto> getDiffSummary() {
		return diffSummary;
	}

	public void setDiffSummary(List<Gstr1BasicSummarySectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
}
