package com.ey.advisory.app.approvalWorkflow;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ApprovalMakerDataDto {

	public ApprovalMakerDataDto(String gstin,
			List<ApprovalEmailIdDto> checkerMail) {
		this.gstin = gstin;
		this.checkerMailIds = checkerMail;
	}

	@Expose
	private String gstin;

	@Expose
	private List<ApprovalEmailIdDto> checkerMailIds = new ArrayList<>();
	
	@Expose
	private String noOfCheckers;

}
