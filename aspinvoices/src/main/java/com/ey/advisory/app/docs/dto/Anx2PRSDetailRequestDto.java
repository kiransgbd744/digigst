package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx2PRSDetailRequestDto extends SearchCriteria {

	public Anx2PRSDetailRequestDto(String searchType) {
		super(SearchTypeConstants.PRS_DETAIL);
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("gstin")
	private List<String> gstins;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("docType")
	private List<String> docType;

	@Expose
	@SerializedName("recordType")
	private List<String> recordType;

	@Expose
	@SerializedName("fromDate")
	private LocalDate fromdate;

	@Expose
	@SerializedName("toDate")
	private LocalDate toDate;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@Expose
	@SerializedName("data")
	private List<String> data;

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public List<String> getGstins() {
		return gstins;
	}

	public void setGstins(List<String> gstins) {
		this.gstins = gstins;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public List<String> getDocType() {
		return docType;
	}

	public void setDocType(List<String> docType) {
		this.docType = docType;
	}

	public List<String> getRecordType() {
		return recordType;
	}

	public void setRecordType(List<String> recordType) {
		this.recordType = recordType;
	}

	public LocalDate getFromdate() {
		return fromdate;
	}

	public void setFromdate(LocalDate fromdate) {
		this.fromdate = fromdate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Anx2PRSDetailRequestDto [entityId=" + entityId + ", gstins="
				+ gstins + ", taxPeriod=" + taxPeriod + ", docType=" + docType
				+ ", recordType=" + recordType + ", fromdate=" + fromdate
				+ ", toDate=" + toDate + ", dataSecAttrs=" + dataSecAttrs
				+ ", data=" + data + "]";
	}
	
	

}