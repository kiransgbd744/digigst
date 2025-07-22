package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalCheckerStatusSummaryDto {
	
	@Expose
	private String requestId;
	
	@Expose
	private String requestDateTime;
	
	@Expose
	private String reqBy;
	
	@Expose
	private String commMakers;
	
	@Expose
	private String taskDesc;
	
	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private String retType;
	
	@Expose
	private String commChecker;
	
	@Expose
	private String status;
	
	@Expose
	private String actionTakenBy;
	
	@Expose
	private String actionDateTime;
	
	@Expose
	private String revertBackAction;
	
}
