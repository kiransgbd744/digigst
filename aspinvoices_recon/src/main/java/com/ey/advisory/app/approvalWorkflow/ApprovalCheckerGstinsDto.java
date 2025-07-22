package com.ey.advisory.app.approvalWorkflow;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalCheckerGstinsDto {
	
	public ApprovalCheckerGstinsDto(String gstinVal)
	
	{
		this.gstin = gstinVal;
	}
	
	@Expose
	private String gstin;
}
