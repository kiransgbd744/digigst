package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class MakerCheWorkFlowRespDto {

	@Expose
	public Long id;

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
