package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalEmailIdDto {

	public ApprovalEmailIdDto(String userName, String emailId) {
		this.email = emailId;
		this.userName = userName;
		this.userEmailStr = String.format("%s(%s)", emailId, userName);
	}

	@Expose
	private String userEmailStr;

	@Expose
	private String userName;

	@Expose
	private String email;
}
