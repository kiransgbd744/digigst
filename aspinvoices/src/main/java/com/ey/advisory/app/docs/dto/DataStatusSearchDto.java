package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataStatusSearchDto {

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
	@SerializedName("retPeriodFrom")
	private String retPeriodFrom;
	
	@Expose
	@SerializedName("retPeriodTo")
	private String retPeriodTo;
	
	@Expose
	@SerializedName("entity")
	private Long entity;
	
	@Expose
	@SerializedName("gstin")
	private List<String> sgstins = new ArrayList<>();

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

	public Long getEntity() {
		return entity;
	}

	public void setEntity(Long entity) {
		this.entity = entity;
	}

	public List<String> getSgstins() {
		return sgstins;
	}

	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}
}
