package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx2InwardErrorRequestDto extends SearchCriteria {

	public Anx2InwardErrorRequestDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("recordType")
	private List<String> recordType;

	@Expose
	@SerializedName("docType")
	private List<String> docType;

	@Expose
	@SerializedName("data")
	private List<String> data;

	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;


	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public List<String> getRecordType() {
		return recordType;
	}

	public void setRecordType(List<String> recordType) {
		this.recordType = recordType;
	}

	public List<String> getDocType() {
		return docType;
	}

	public void setDocType(List<String> docType) {
		this.docType = docType;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	@Override
	public String toString() {
		return "Anx2InwardErrorRequestDto [entityId=" + entityId
				+ ", taxPeriod=" + taxPeriod + ", recordType=" + recordType
				+ ", docType=" + docType + ", data=" + data + ", type=" + type
				+ ", dataSecAttrs=" + dataSecAttrs + "]";
	}

}