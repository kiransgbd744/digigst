package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx1ProcessedRecordsReqDto extends SearchCriteria {
	public Anx1ProcessedRecordsReqDto(String searchType) {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId  = new ArrayList<>();

	@Expose
	@SerializedName("GSTIN")
	private List<String> GSTIN = new ArrayList<>();

	@Expose
	@SerializedName("taxPeriod")
	private String retunPeriod;

	@Expose
	@SerializedName("gstnUploadDate")
	private String gstnUploadDate;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	private Map<String, List<String>> outwardDataSecAttrs;
	
	private Map<String, List<String>> inwardDataSecAttrs;
	

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getRetunPeriod() {
		return retunPeriod;
	}

	public void setRetunPeriod(String retunPeriod) {
		this.retunPeriod = retunPeriod;
	}

	public List<String> getGstin() {
		return GSTIN;
	}

	public void setGstin(List<String> GSTIN) {
		this.GSTIN = GSTIN;
	}

	public String getTaxPeriod() {
		return retunPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.retunPeriod = taxPeriod;
	}

	public String getGstnUploadDate() {
		return gstnUploadDate;
	}

	public void setGstnUploadDate(String gstnUploadDate) {
		this.gstnUploadDate = gstnUploadDate;
	}

	public Map<String, List<String>> getOutwardDataSecAttrs() {
		return outwardDataSecAttrs;
	}

	public void setOutwardDataSecAttrs(
			Map<String, List<String>> outwardDataSecAttrs) {
		this.outwardDataSecAttrs = outwardDataSecAttrs;
	}

	public Map<String, List<String>> getInwardDataSecAttrs() {
		return inwardDataSecAttrs;
	}

	public void setInwardDataSecAttrs(
			Map<String, List<String>> inwardDataSecAttrs) {
		this.inwardDataSecAttrs = inwardDataSecAttrs;
	}

}
