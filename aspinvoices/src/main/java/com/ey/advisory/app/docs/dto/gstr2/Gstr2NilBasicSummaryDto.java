package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2NilBasicSummaryDto {
	
	
	@Expose
	@SerializedName("ey")
	private List<Gstr2NilBasicSummarySectionDto> eySummary;
	
	@Expose
	@SerializedName("2A")
	private List<Gstr2NilBasicSummarySectionDto> a2Summary;
	
	@Expose
	@SerializedName("2APR")
	private List<Gstr2NilBasicSummarySectionDto> a2prSummary;
	@Expose
	@SerializedName("diff1")
	private List<Gstr2NilBasicSummarySectionDto> diff1Summary;
	@Expose
	@SerializedName("diff2")
	private List<Gstr2NilBasicSummarySectionDto> diff2Summary;
	/**
	 * @return the eySummary
	 */
	public List<Gstr2NilBasicSummarySectionDto> getEySummary() {
		return eySummary;
	}
	/**
	 * @param eySummary the eySummary to set
	 */
	public void setEySummary(List<Gstr2NilBasicSummarySectionDto> eySummary) {
		this.eySummary = eySummary;
	}
	/**
	 * @return the a2Summary
	 */
	public List<Gstr2NilBasicSummarySectionDto> getA2Summary() {
		return a2Summary;
	}
	/**
	 * @param a2Summary the a2Summary to set
	 */
	public void setA2Summary(List<Gstr2NilBasicSummarySectionDto> a2Summary) {
		this.a2Summary = a2Summary;
	}
	/**
	 * @return the a2prSummary
	 */
	public List<Gstr2NilBasicSummarySectionDto> getA2prSummary() {
		return a2prSummary;
	}
	/**
	 * @param a2prSummary the a2prSummary to set
	 */
	public void setA2prSummary(List<Gstr2NilBasicSummarySectionDto> a2prSummary) {
		this.a2prSummary = a2prSummary;
	}
	/**
	 * @return the diff1Summary
	 */
	public List<Gstr2NilBasicSummarySectionDto> getDiff1Summary() {
		return diff1Summary;
	}
	/**
	 * @param diff1Summary the diff1Summary to set
	 */
	public void setDiff1Summary(List<Gstr2NilBasicSummarySectionDto> diff1Summary) {
		this.diff1Summary = diff1Summary;
	}
	/**
	 * @return the diff2Summary
	 */
	public List<Gstr2NilBasicSummarySectionDto> getDiff2Summary() {
		return diff2Summary;
	}
	/**
	 * @param diff2Summary the diff2Summary to set
	 */
	public void setDiff2Summary(List<Gstr2NilBasicSummarySectionDto> diff2Summary) {
		this.diff2Summary = diff2Summary;
	}
	
	
		

}
