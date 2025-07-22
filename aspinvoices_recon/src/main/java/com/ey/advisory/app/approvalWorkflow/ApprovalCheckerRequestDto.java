package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ApprovalCheckerRequestDto {
	
	@Expose
	@SerializedName("entityId")
	private String entityId;
	
	@Expose
	@SerializedName("retType")
	private List<String> retType;
	
	@Expose
	@SerializedName("reqDateFrom")
	private String reqDateFrom;
	
	@Expose
	@SerializedName("reqDateTo")
	private String reqDateTo;
	
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;
	
	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;
	
	@Expose
	@SerializedName("gstins")
	private List<String> gstins;
}
