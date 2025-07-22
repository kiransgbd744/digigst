package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1ProcessedRecordsReqDto extends SearchCriteria {
	public Gstr1ProcessedRecordsReqDto(String searchType) {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}


	public Gstr1ProcessedRecordsReqDto(String searchType,
			Map<String, List<String>> dataSecAttrs) {
		super(searchType);
		this.dataSecAttrs = dataSecAttrs;
	}

	public Gstr1ProcessedRecordsReqDto() {
		super(SearchTypeConstants.PROCESSED_RECORDS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("taxPeriod")
	private String retunPeriod;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	private String type;

	@Expose
	@SerializedName("GSTIN")
	private List<String> GSTIN = new ArrayList<>();
	
	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();
	@Expose
	@SerializedName("docType")
	private List<String> docType = new ArrayList<>();
	
	
	@Expose
	@SerializedName("docFromDate")
	private LocalDate docFromDate;
	@Expose
	@SerializedName("docToDate")
	private LocalDate docToDate;
	@Expose
	@SerializedName("EINVGenerated")
	private List<String> EINVGenerated = new ArrayList<>();
	@Expose
	@SerializedName("EWBGenerated")
	private List<String> EWBGenerated = new ArrayList<>();

	@Expose
	@SerializedName("returnType")
	private String returnType;
	
	
	public String getReturnType() {
		return returnType;
	}


	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	

	

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public List<String> getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(List<String> gSTIN) {
		GSTIN = gSTIN;
	}

	public String getRetunPeriod() {
		return retunPeriod;
	}

	public void setRetunPeriod(String retunPeriod) {
		this.retunPeriod = retunPeriod;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
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


	public LocalDate getDocFromDate() {
		return docFromDate;
	}


	public void setDocFromDate(LocalDate docFromDate) {
		this.docFromDate = docFromDate;
	}


	public LocalDate getDocToDate() {
		return docToDate;
	}


	public void setDocToDate(LocalDate docToDate) {
		this.docToDate = docToDate;
	}


	public List<String> getEINVGenerated() {
		return EINVGenerated;
	}


	public void setEINVGenerated(List<String> eINVGenerated) {
		EINVGenerated = eINVGenerated;
	}


	public List<String> getEWBGenerated() {
		return EWBGenerated;
	}


	public void setEWBGenerated(List<String> eWBGenerated) {
		EWBGenerated = eWBGenerated;
	}


	@Override
	public String toString() {
		return "Gstr1ProcessedRecordsReqDto [entityId=" + entityId + ", GSTIN="
				+ GSTIN + ", retunPeriod=" + retunPeriod + ", dataSecAttrs="
				+ dataSecAttrs +  ", returnType="
						+ returnType + "]";
	}

}
