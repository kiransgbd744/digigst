package com.ey.advisory.app.docs.dto.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AsyncGstr2ProcessedRecordsReqDto extends SearchCriteria {

	public AsyncGstr2ProcessedRecordsReqDto(String searchType) {
		super(searchType);
		// TODO Auto-generated constructor stub
	}

	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;

	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;
	
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();
	
	@Expose
	@SerializedName("docType")
	private List<String> docType = new ArrayList<>();
	
	
	@Expose
	@SerializedName("month")
	private List<String> month = new ArrayList<>();
	
	@Expose
	@SerializedName("fyYear")
	private String fyYear;
	

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public String getFromPeriod() {
		return fromPeriod;
	}

	public void setFromPeriod(String fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	public String getToPeriod() {
		return toPeriod;
	}

	public void setToPeriod(String toPeriod) {
		this.toPeriod = toPeriod;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<String> getTableType() {
		return tableType;
	}

	public void setTableType(List<String> tableType) {
		this.tableType = tableType;
	}

	public List<String> getDocType() {
		return docType;
	}

	public void setDocType(List<String> docType) {
		this.docType = docType;
	}

	/**
	 * @return the month
	 */
	public List<String> getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(List<String> month) {
		this.month = month;
	}

	/**
	 * @return the fyYear
	 */
	public String getFyYear() {
		return fyYear;
	}

	/**
	 * @param fyYear the fyYear to set
	 */
	public void setFyYear(String fyYear) {
		this.fyYear = fyYear;
	}

	
}
