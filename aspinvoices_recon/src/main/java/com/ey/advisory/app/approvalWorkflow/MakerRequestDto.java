package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class MakerRequestDto {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("retType")
	private String retType;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("requestInfo")
	private List<MakerRequestResponseDto> requestList;
	
	}
