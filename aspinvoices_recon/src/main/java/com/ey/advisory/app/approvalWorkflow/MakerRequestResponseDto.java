package com.ey.advisory.app.approvalWorkflow;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class MakerRequestResponseDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	
	@Expose
	@SerializedName("selectedCheckers")
	private List<String> selCheckers;

	@Expose
	@SerializedName("requestedFor")
	private List<String> requestedFor;
	
	@Expose
	@SerializedName("comments")
	private String makComments;

	
	}
