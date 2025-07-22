package com.ey.advisory.app.get.notices.handlers;

import lombok.Data;
import com.google.gson.annotations.Expose;

@Data
public class NoticeSummaryDto {
	
	@Expose
	private String gstin;

	@Expose
	private String noticeId;

	@Expose
	private String dateOfIssue;
	@Expose
	private String dueDateOfResponse;
	@Expose
	private String status;
	@Expose
	private String dateOfResponse;
	@Expose
	private String timeLeft;
	@Expose
	private String categoryOfNotice;
	@Expose
	private String typeOfNotice;
	@Expose
	private String periodRange;
	@Expose
	private String amountTaxInvolved;
}