package com.ey.advisory.app.approvalWorkflow;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.google.common.base.Strings;
import lombok.Data;

@Data
public class ApprovalDataRespDto {

	public ApprovalDataRespDto(String gstin, String retType,
			List<ApprovalEmailIdDto> userInfo) {
		this.gstin = gstin;
		this.retType = retType;
		this.makerMailIds = userInfo;
		this.checkerMailIds = userInfo;
	}

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("isPresent")
	private boolean isPresent = false;

	@Expose
	private String retType;

	@Expose
	@SerializedName("selectedMakers")
	private List<String> selectedMakers = new ArrayList<>();

	@Expose
	@SerializedName("selectedCheckers")
	private List<String> selectedCheckers = new ArrayList<>();

	@Expose
	private List<ApprovalEmailIdDto> makerMailIds = new ArrayList<>();

	@Expose
	private List<ApprovalEmailIdDto> checkerMailIds = new ArrayList<>();

	public void addSelectedMakers(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			selectedMakers.add(emailId);
		}
	}

	public void addSelectedCheckers(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			selectedCheckers.add(emailId);
		}
	}

}
