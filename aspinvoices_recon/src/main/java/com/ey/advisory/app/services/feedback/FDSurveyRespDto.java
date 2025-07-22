package com.ey.advisory.app.services.feedback;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FDSurveyRespDto {

	@SerializedName("isSubmitted")
	@Expose
	private boolean isSubmitted;

	@SerializedName("userName")
	@Expose
	private String userName;
	
	@SerializedName("errMsg")
	@Expose
	private String errMsg;
	
	@SerializedName("groupCode")
	@Expose
	private String groupCode;
	
	@SerializedName("submittedOn")
	@Expose
	private String submittedOn;

	
	@SerializedName("results")
	@Expose
	private List<FDSurveyGetRespDto> results;
}
