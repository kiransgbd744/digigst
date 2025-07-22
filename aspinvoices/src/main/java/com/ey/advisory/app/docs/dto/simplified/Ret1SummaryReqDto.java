package com.ey.advisory.app.docs.dto.simplified;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ret1SummaryReqDto extends SearchCriteria {

	@Expose
	@SerializedName("table")
	private String table;
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String,List<String>> dataSecAttrs;
    
	public Ret1SummaryReqDto() {
		super(SearchTypeConstants.DOC_SUMMARY_SEARCH);
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}
	

	
}
