package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalCheckerTabSummaryDto {

	@Expose
	private String totalRequests;
	
	@Expose
	private String totalApproved;
	
	@Expose
	private String totalPending;
	
	@Expose
	private String totalRejected;
	
	
}
