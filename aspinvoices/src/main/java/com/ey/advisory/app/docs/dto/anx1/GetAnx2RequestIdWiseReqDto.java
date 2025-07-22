package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAnx2RequestIdWiseReqDto extends SearchCriteria {
	public GetAnx2RequestIdWiseReqDto(String searchType) {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}

	@Expose
	@SerializedName("requestId")
	private String requestId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("initiatedBy")
	private List<String> initiatedBy = new ArrayList<>();

	@Expose
	@SerializedName("initiationDateTime")
	private String initiationDateTime;

	@Expose
	@SerializedName("completionDateTime")
	private String completionDateTime;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs = new HashMap<>();

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public List<String> getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(List<String> initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public String getInitiationDateTime() {
		return initiationDateTime;
	}

	public void setInitiationDateTime(String initiationDateTime) {
		this.initiationDateTime = initiationDateTime;
	}

	public String getCompletionDateTime() {
		return completionDateTime;
	}

	public void setCompletionDateTime(String completionDateTime) {
		this.completionDateTime = completionDateTime;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

}
