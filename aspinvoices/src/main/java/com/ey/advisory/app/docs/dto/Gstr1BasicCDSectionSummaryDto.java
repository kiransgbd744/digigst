/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1BasicCDSectionSummaryDto {
	
	private List<Gstr1SummaryCDSectionDto> eySummary;

	private List<Gstr1SummaryCDSectionDto> gstnSummary;

	private List<Gstr1SummaryCDSectionDto> diffSummary;

	/**
	 * @return the eySummary
	 */
	public List<Gstr1SummaryCDSectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr1SummaryCDSectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the gstnSummary
	 */
	public List<Gstr1SummaryCDSectionDto> getGstnSummary() {
		return gstnSummary;
	}

	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Gstr1SummaryCDSectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	/**
	 * @return the diffSummary
	 */
	public List<Gstr1SummaryCDSectionDto> getDiffSummary() {
		return diffSummary;
	}

	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Gstr1SummaryCDSectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}

	
	
}
