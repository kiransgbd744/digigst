/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1BasicNilSectionSummaryDto {

	private List<Gstr1SummaryNilSectionDto> eySummary;

	private List<Gstr1SummaryNilSectionDto> gstnSummary;

	private List<Gstr1SummaryNilSectionDto> diffSummary;

	/**
	 * @return the eySummary
	 */
	public List<Gstr1SummaryNilSectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr1SummaryNilSectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the gstnSummary
	 */
	public List<Gstr1SummaryNilSectionDto> getGstnSummary() {
		return gstnSummary;
	}

	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Gstr1SummaryNilSectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	/**
	 * @return the diffSummary
	 */
	public List<Gstr1SummaryNilSectionDto> getDiffSummary() {
		return diffSummary;
	}

	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Gstr1SummaryNilSectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
	
	
}
