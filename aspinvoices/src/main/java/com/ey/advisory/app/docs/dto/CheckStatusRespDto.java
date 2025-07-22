package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckStatusRespDto {
	
	@Expose
	@SerializedName("sNo")
	private Long id;
	@Expose
	@SerializedName("retPeriod")
    private String returnPeriod;
	@Expose
	@SerializedName("gstin")
    private String sgstin;
	@Expose
	@SerializedName("date")
    private LocalDate createdOn;
	@Expose
	@SerializedName("time")
    private String time;
	@Expose
	@SerializedName("action")
    private Boolean action;
	@Expose
	@SerializedName("refId")
    private String refId;
	@Expose
	@SerializedName("status")
    private String status;
  //  private String errorRpt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	public String getSgstin() {
		return sgstin;
	}
	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}
	public LocalDate getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String formatDateTime) {
		this.time = formatDateTime;
	}
	public Boolean getAction() {
		return action;
	}
	public void setAction(Boolean action) {
		this.action = action;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "CheckStatusRespDto [id=" + id + ", returnPeriod=" + returnPeriod + ", sgstin=" + sgstin + ", createdOn="
				+ createdOn + ", time=" + time + ", action=" + action + ", refId=" + refId + ", status=" + status + "]";
	}
	  
	
	
}
