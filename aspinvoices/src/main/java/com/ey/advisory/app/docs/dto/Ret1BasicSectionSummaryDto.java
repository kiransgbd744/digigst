package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.docs.dto.simplified.TaxSectionPaymentSummaryDto;


/**
 * 
 * @author Balakrishna.S
 *
 */
public class Ret1BasicSectionSummaryDto {

	
	private List<Ret1SummarySectionDto> eySummary;
	private List<Ret1SummarySectionDto> gstnSummary;
	private List<Ret1SummarySectionDto> diffSummary;
	private List<Ret1LateFeeSummarySectionDto> eySummaryforint6;
	private List<Ret1PaymentSummarySectionDto> eySummaryPayment7;
	private List<Ret1RefundSummarySectionDto> eySummaryRefund8;
	private List<TaxSectionPaymentSummaryDto> eySummaryPaymentTax6;
	
	/**
	 * @return the eySummaryPayment7
	 */
	public List<Ret1PaymentSummarySectionDto> getEySummaryPayment7() {
		return eySummaryPayment7;
	}
	/**
	 * @param eySummaryPayment7 the eySummaryPayment7 to set
	 */
	public void setEySummaryPayment7(
			List<Ret1PaymentSummarySectionDto> eySummaryPayment7) {
		this.eySummaryPayment7 = eySummaryPayment7;
	}
	/**
	 * @return the eySummary
	 */
	public List<Ret1SummarySectionDto> getEySummary() {
		return eySummary;
	}
	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Ret1SummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}
	/**
	 * @return the gstnSummary
	 */
	public List<Ret1SummarySectionDto> getGstnSummary() {
		return gstnSummary;
	}
	/**
	 * @param gstnSummary the gstnSummary to set
	 */
	public void setGstnSummary(List<Ret1SummarySectionDto> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}
	/**
	 * @return the diffSummary
	 */
	public List<Ret1SummarySectionDto> getDiffSummary() {
		return diffSummary;
	}
	/**
	 * @param diffSummary the diffSummary to set
	 */
	public void setDiffSummary(List<Ret1SummarySectionDto> diffSummary) {
		this.diffSummary = diffSummary;
	}
	/**
	 * @return the eySummaryforint6
	 */
	public List<Ret1LateFeeSummarySectionDto> getEySummaryforint6() {
		return eySummaryforint6;
	}
	/**
	 * @param eySummaryforint6 the eySummaryforint6 to set
	 */
	public void setEySummaryforint6(
			List<Ret1LateFeeSummarySectionDto> eySummaryforint6) {
		this.eySummaryforint6 = eySummaryforint6;
	}
	public List<Ret1RefundSummarySectionDto> getEySummaryRefund8() {
		return eySummaryRefund8;
	}
	public void setEySummaryRefund8(
			List<Ret1RefundSummarySectionDto> eySummaryRefund8) {
		this.eySummaryRefund8 = eySummaryRefund8;
	}
	public List<TaxSectionPaymentSummaryDto> getEySummaryPaymentTax6() {
		return eySummaryPaymentTax6;
	}
	public void setEySummaryPaymentTax6(
			List<TaxSectionPaymentSummaryDto> eySummaryPaymentTax6) {
		this.eySummaryPaymentTax6 = eySummaryPaymentTax6;
	}
	
	
	
}
