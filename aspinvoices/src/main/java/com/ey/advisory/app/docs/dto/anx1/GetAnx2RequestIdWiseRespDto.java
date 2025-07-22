package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAnx2RequestIdWiseRespDto {

	@Expose
	@SerializedName("requestId")
	private String requestId;

	@Expose
	@SerializedName("gstin")
	private List<String> gstin = new ArrayList<>();

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("initiatedBy")
	private String initiatedBy;

	@Expose
	@SerializedName("initiationDateTime")
	private String initiationDateTime;

	@Expose
	@SerializedName("completionDateTime")
	private String completionDateTime;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public List<String> getGstin() {
		return gstin;
	}

	public void setGstin(List<String> gstin) {
		this.gstin = gstin;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(String initiatedBy) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
