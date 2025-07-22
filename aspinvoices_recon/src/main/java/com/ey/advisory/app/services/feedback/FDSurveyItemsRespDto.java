package com.ey.advisory.app.services.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FDSurveyItemsRespDto {

	@SerializedName("ques")
	@Expose
	private String ques;

	@SerializedName("keyType")
	@Expose
	private String keyType;

	@SerializedName("answerDesc")
	@Expose
	private String answerDesc;

	@SerializedName("answerCode")
	@Expose
	String answerCode;

	@SerializedName("answerValue")
	@Expose
	private String answerValue;
	
	@SerializedName("quesId")
	@Expose
	private Long quesId;

}
