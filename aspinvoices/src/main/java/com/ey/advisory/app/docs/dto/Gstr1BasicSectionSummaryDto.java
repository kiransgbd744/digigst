/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1BasicSectionSummaryDto {

	private List<Gstr1SummarySectionDto> eySummary;

	private List<Gstr1SummarySectionDto> gstnSummary;

	private List<Gstr1SummarySectionDto> diffSummary;

	/**
	 * @return the eySummary
	 */
	public List<Gstr1SummarySectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr1SummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the gstnSummary
	 */
	public List<Gstr1SummarySectionDto> getGstnSummary() {
		return gstnSummary;
	}

	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Gstr1SummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	/**
	 * @return the diffSummary
	 */
	public List<Gstr1SummarySectionDto> getDiffSummary() {
		return diffSummary;
	}

	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Gstr1SummarySectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
	
	

}
