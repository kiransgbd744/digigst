package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataStatusSearchReqDto extends SearchCriteria {

	
	public DataStatusSearchReqDto(String searchType) {
		super(SearchTypeConstants.DATA_STATUS_SEARCH);
	}
	
	@Expose
	@SerializedName("dataType")
	private String dataType;
	
	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("dataRecvFrom")
	private LocalDate dataRecvFrom;

	@Expose
	@SerializedName("dataRecvTo")
	private LocalDate dataRecvTo;
	
	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;

	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String retPeriodFrom;
	
	@Expose
	@SerializedName("taxPeriodTo")
	private String retPeriodTo;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("documentDateFrom")
	private LocalDate documentDateFrom;
	
	@Expose
	@SerializedName("documentDateTo")
	private LocalDate documentDateTo;
	
	@Expose
	@SerializedName("accVoucherDateFrom")
	private LocalDate accVoucherDateFrom;
	
	@Expose
	@SerializedName("accVoucherDateTo")
	private LocalDate accVoucherDateTo;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();
	

	/*@Expose
	@SerializedName("dataSecAttrs")
	private DataSecAttrs dataSecAttrs;*/
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String,List<String>> dataSecAttrs;
	
	/*@Expose
	@SerializedName("dates")
	private List<LocalDate> dates;
*/
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public LocalDate getDataRecvFrom() {
		return dataRecvFrom;
	}

	public void setDataRecvFrom(LocalDate dataRecvFrom) {
		this.dataRecvFrom = dataRecvFrom;
	}

	public LocalDate getDataRecvTo() {
		return dataRecvTo;
	}

	public void setDataRecvTo(LocalDate dataRecvTo) {
		this.dataRecvTo = dataRecvTo;
	}

	public LocalDate getDocDateFrom() {
		return docDateFrom;
	}

	public void setDocDateFrom(LocalDate docDateFrom) {
		this.docDateFrom = docDateFrom;
	}

	public LocalDate getDocDateTo() {
		return docDateTo;
	}

	public void setDocDateTo(LocalDate docDateTo) {
		this.docDateTo = docDateTo;
	}

	public String getRetPeriodFrom() {
		return retPeriodFrom;
	}

	public void setRetPeriodFrom(String retPeriodFrom) {
		this.retPeriodFrom = retPeriodFrom;
	}

	public String getRetPeriodTo() {
		return retPeriodTo;
	}

	public void setRetPeriodTo(String retPeriodTo) {
		this.retPeriodTo = retPeriodTo;
	}

	public List<String> getSgstins() {
		return sgstins;
	}

	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

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
	
	public LocalDate getDocumentDateFrom() {
		return documentDateFrom;
	}

	public void setDocumentDateFrom(LocalDate documentDateFrom) {
		this.documentDateFrom = documentDateFrom;
	}

	public LocalDate getDocumentDateTo() {
		return documentDateTo;
	}

	public void setDocumentDateTo(LocalDate documentDateTo) {
		this.documentDateTo = documentDateTo;
	}

	public LocalDate getAccVoucherDateFrom() {
		return accVoucherDateFrom;
	}

	public void setAccVoucherDateFrom(LocalDate accVoucherDateFrom) {
		this.accVoucherDateFrom = accVoucherDateFrom;
	}

	public LocalDate getAccVoucherDateTo() {
		return accVoucherDateTo;
	}

	public void setAccVoucherDateTo(LocalDate accVoucherDateTo) {
		this.accVoucherDateTo = accVoucherDateTo;
	}
	

	@Override
	public String toString() {
		return "DataStatusSearchReqDto [dataType=" + dataType + ", criteria="
				+ criteria + ", dataRecvFrom=" + dataRecvFrom + ", dataRecvTo="
				+ dataRecvTo + ", docDateFrom=" + docDateFrom + ", docDateTo="
				+ docDateTo + ", retPeriodFrom=" + retPeriodFrom
				+ ", retPeriodTo=" + retPeriodTo + ", entityId=" + entityId
				+ ", sgstins=" + sgstins + ", dataSecAttrs=" + dataSecAttrs
				+ "]";
	}
	
	
	}
