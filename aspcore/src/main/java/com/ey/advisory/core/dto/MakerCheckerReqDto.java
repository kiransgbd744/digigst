package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class MakerCheckerReqDto {

	@Expose
	private Long entityId;

	@Expose
	private Long id;

	@Expose
	private boolean action;

	@Expose
	private String workFlowType;

	@Expose
	private String gstin;

	@Expose
	private String retType;

	@Expose
	private String checkNotfTo;

	@Expose
	private String mkrUsrId;

	@Expose
	private String ckrUsrId;
}
