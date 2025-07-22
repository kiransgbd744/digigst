package com.ey.advisory.app.docs.dto.simplified;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * 
 * @author Mohana.Dasari
 *
 */
public class Annexure1BasicSummaryDto {

	
	private List<Annexure1SummarySectionDto> eySummary;

	
	private List<Annexure1SummarySectionDto> gstnSummary;
	
	
	private List<Annexure1SummarySectionDto> diffSummary;

	/**
	 * @return the eySummary
	 */
	public List<Annexure1SummarySectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Annexure1SummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the gstnSummary
	 */
	public List<Annexure1SummarySectionDto> getGstnSummary() {
		return gstnSummary;
	}

	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Annexure1SummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	/**
	 * @return the diffSummary
	 */
	public List<Annexure1SummarySectionDto> getDiffSummary() {
		return diffSummary;
	}

	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Annexure1SummarySectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
	
}
