package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ApprovalCheckerActionRequestDto {
	
	@SerializedName("entityId")
	private String entityId;
	
	@SerializedName("isSubmit")
	private boolean issubmit;
	
	@SerializedName("submitInfo")
	private List<ApprovalCheckerSubmitInfoDto> submitInfo;
	
}
