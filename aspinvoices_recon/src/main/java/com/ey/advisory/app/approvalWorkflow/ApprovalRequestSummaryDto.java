package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalRequestSummaryDto {
	
	@Expose
	private String requestId;
	
	@Expose
	private String dateAndTimeOfReq;
	
	@Expose
	private String gstin;
	
	@Expose 
	private Integer noOfRequestMadeTo;
	
	@Expose
	private List<ApprovalEmailIdDto> requestMadeTo;
	
	@Expose
	private String actionTakenFor;
	
	@Expose
	private String statusOfReq;
	
	@Expose
	private String dateAndTimeOfChecker;
	
	@Expose
	private String commentsFromChec;
	
	
	}
