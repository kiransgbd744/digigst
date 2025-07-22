/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import java.util.List;

/**
 * @author BalaKrishna S
 *
 */
public class Annexure1BasicEcomSummaryDto {
	
	private List<Annexure1SummarySectionEcomDto> eySummary;

	
	private List<Annexure1SummarySectionEcomDto> gstnSummary;
	
	
	private List<Annexure1SummarySectionEcomDto> diffSummary;


	/**
	 * @return the eySummary
	 */
	public List<Annexure1SummarySectionEcomDto> getEySummary() {
		return eySummary;
	}


	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Annexure1SummarySectionEcomDto> eySummary) {
		this.eySummary = eySummary;
	}


	/**
	 * @return the gstnSummary
	 */
	public List<Annexure1SummarySectionEcomDto> getGstnSummary() {
		return gstnSummary;
	}


	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Annexure1SummarySectionEcomDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}


	/**
	 * @return the diffSummary
	 */
	public List<Annexure1SummarySectionEcomDto> getDiffSummary() {
		return diffSummary;
	}


	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Annexure1SummarySectionEcomDto> diffSummary) {
		this.diffSummary = diffSummary;
	}


	
	
	
	
}
