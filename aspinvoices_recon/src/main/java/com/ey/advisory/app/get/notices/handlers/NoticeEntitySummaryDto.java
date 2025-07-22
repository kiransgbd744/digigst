package com.ey.advisory.app.get.notices.handlers;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class NoticeEntitySummaryDto {
	
	@Expose
    private String gstin;
	@Expose
    private String stateName;
	@Expose
    private String gstinRegType;
	@Expose
    private String status;
	@Expose
    private String getCallTime;
	@Expose
    private String isAuthToken;
	@Expose
    private int noticesIssued;
	@Expose
    private int noticesResponded;
	@Expose
    private int pendingForResponse;
	@Expose
    private String taxLiability = "NA";
	@Expose
    private String taxLiabilityInvolved = "NA";
    
}