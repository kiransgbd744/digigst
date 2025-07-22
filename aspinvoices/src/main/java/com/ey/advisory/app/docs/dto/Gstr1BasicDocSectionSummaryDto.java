/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1BasicDocSectionSummaryDto {

	private List<Gstr1SummaryDocSectionDto> eySummary;

	private List<Gstr1SummaryDocSectionDto> gstnSummary;

	private List<Gstr1SummaryDocSectionDto> diffSummary;

	/**
	 * @return the eySummary
	 */
	public List<Gstr1SummaryDocSectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr1SummaryDocSectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the gstnSummary
	 */
	public List<Gstr1SummaryDocSectionDto> getGstnSummary() {
		return gstnSummary;
	}

	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Gstr1SummaryDocSectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}

	/**
	 * @return the diffSummary
	 */
	public List<Gstr1SummaryDocSectionDto> getDiffSummary() {
		return diffSummary;
	}

	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Gstr1SummaryDocSectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
	
	
	
}
