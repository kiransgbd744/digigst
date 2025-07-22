package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalRetTypeDto {

	public ApprovalRetTypeDto(String retType2) {
		this.retType = retType2;
	}
	@Expose
	private String retType;
	
}
