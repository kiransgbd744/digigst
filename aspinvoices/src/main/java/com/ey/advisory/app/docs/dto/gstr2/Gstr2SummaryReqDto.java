package com.ey.advisory.app.docs.dto.gstr2;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2SummaryReqDto extends SearchCriteria {
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private String fromTaxPeriod;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	public Gstr2SummaryReqDto() {
		super(SearchTypeConstants.DOC_SUMMARY_SEARCH);
	}
	
	/**
	 * @return the sgstins
	 */
	public List<String> getSgstins() {
		return sgstins;
	}

	/**
	 * @param sgstins the sgstins to set
	 */
	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}


	/**
	 * @return the fromTaxPeriod
	 */
	public String getFromTaxPeriod() {
		return fromTaxPeriod;
	}
	/**
	 * @param fromTaxPeriod the fromTaxPeriod to set
	 */
	public void setFromTaxPeriod(String fromTaxPeriod) {
		this.fromTaxPeriod = fromTaxPeriod;
	}
	/**
	 * @return the toTaxPeriod
	 */
	public String getToTaxPeriod() {
		return toTaxPeriod;
	}
	/**
	 * @param toTaxPeriod the toTaxPeriod to set
	 */
	public void setToTaxPeriod(String toTaxPeriod) {
		this.toTaxPeriod = toTaxPeriod;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}	
}
