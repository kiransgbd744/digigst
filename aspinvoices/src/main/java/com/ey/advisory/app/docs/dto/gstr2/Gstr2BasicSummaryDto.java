package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2BasicSummaryDto {
	
	@Expose
	@SerializedName("ey")
	private List<Gstr2BasicSummarySectionDto> eySummary;
	
	@Expose
	@SerializedName("2A")
	private List<Gstr2BasicSummarySectionDto> A2Summary;
	
	@Expose
	@SerializedName("2APR")
	private List<Gstr2BasicSummarySectionDto> A2PRSummary;
	
	@Expose
	@SerializedName("diff1")
	private List<Gstr2BasicSummarySectionDto> diff1Summary;
	
	@Expose
	@SerializedName("diff2")
	private List<Gstr2BasicSummarySectionDto> diff2Summary;

	/**
	 * @return the eySummary
	 */
	public List<Gstr2BasicSummarySectionDto> getEySummary() {
		return eySummary;
	}

	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr2BasicSummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}

	/**
	 * @return the a2Summary
	 */
	public List<Gstr2BasicSummarySectionDto> getA2Summary() {
		return A2Summary;
	}

	/**
	 * @param a2Summary the a2Summary to set
	 */
	public void setA2Summary(List<Gstr2BasicSummarySectionDto> a2Summary) {
		A2Summary = a2Summary;
	}

	/**
	 * @return the a2PRSummary
	 */
	public List<Gstr2BasicSummarySectionDto> getA2PRSummary() {
		return A2PRSummary;
	}

	/**
	 * @param a2prSummary the a2PRSummary to set
	 */
	public void setA2PRSummary(List<Gstr2BasicSummarySectionDto> a2prSummary) {
		A2PRSummary = a2prSummary;
	}

	/**
	 * @return the diff1Summary
	 */
	public List<Gstr2BasicSummarySectionDto> getDiff1Summary() {
		return diff1Summary;
	}

	/**
	 * @param diff1Summary the diff1Summary to set
	 */
	public void setDiff1Summary(List<Gstr2BasicSummarySectionDto> diff1Summary) {
		this.diff1Summary = diff1Summary;
	}

	/**
	 * @return the diff2Summary
	 */
	public List<Gstr2BasicSummarySectionDto> getDiff2Summary() {
		return diff2Summary;
	}

	/**
	 * @param diff2Summary the diff2Summary to set
	 */
	public void setDiff2Summary(List<Gstr2BasicSummarySectionDto> diff2Summary) {
		this.diff2Summary = diff2Summary;
	}
	
	
	
	
	


}
