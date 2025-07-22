package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ApprovalCheckerSubmitInfoDto {

	@SerializedName("requestId")
	private String reqId;
	
	@SerializedName("action")
	private String action;
	
	@SerializedName("checkComments")
	private String checkComm;
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("actionTakenBy")
	private String actionTakenBy;
}
