package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class MakerCheckerWorkflowRespDto {

	@Expose
	private Long id;

	@Expose
	private Long entityId;

	@Expose
	private String workFlowType;

	@Expose
	private boolean action;
}
