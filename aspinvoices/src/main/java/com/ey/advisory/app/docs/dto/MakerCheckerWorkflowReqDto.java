package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

public class MakerCheckerWorkflowReqDto {

	@Expose
	private Long id;

	@Expose
	private boolean action;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}
}
