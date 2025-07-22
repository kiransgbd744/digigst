package com.ey.advisory.app.services.feedback;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FDSurveyGetRespDto {

	@SerializedName("quesCode")
	@Expose
	private String quesCode;

	@SerializedName("ques")
	@Expose
	private String ques;

	@SerializedName("keyType")
	@Expose
	private String keyType;

	@SerializedName("answerDesc")
	@Expose
	private String answerDesc;
	
	@SerializedName("isFileReq")
	@Expose
	private boolean isFileReq;

	@SerializedName("quesId")
	@Expose
	private Long quesId;

	@SerializedName("sequenceId")
	@Expose
	private Integer sequenceId;

	@SerializedName("fileUpload")
	@Expose
	private boolean fileUpload;

	@SerializedName("items")
	@Expose
	private List<FDSurveyItemsRespDto> items;
}
