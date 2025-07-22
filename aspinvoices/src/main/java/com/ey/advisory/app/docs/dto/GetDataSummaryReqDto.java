package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;




public class GetDataSummaryReqDto extends SearchCriteria {
	
private LocalDate dataRecvFrom;
private LocalDate dataRecvTo;
private LocalDate docDateFrom;
private LocalDate docDateTo;
private String retPeriodFrom;
private String retPeriodTo;
private List<Long> entityId;
private List<String> gstin =   new ArrayList<>();
private List<LocalDate> dates = new ArrayList<>();


public GetDataSummaryReqDto() {
	super(SearchTypeConstants.GET_DATA_SUMMARY);
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

public List<Long> getEntityId() {
	return entityId;
}


public void setEntityId(List<Long> entityId) {
	this.entityId = entityId;
}


public List<String> getGstin() {
	return gstin;
}


public void setGstin(List<String> gstin) {
	this.gstin = gstin;
}


public List<LocalDate> getDates() {
	return dates;
}


public void setDates(List<LocalDate> dates) {
	this.dates = dates;
}
}

