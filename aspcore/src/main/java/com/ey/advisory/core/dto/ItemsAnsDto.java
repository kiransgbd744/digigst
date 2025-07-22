package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ItemsAnsDto {

	@Expose
	@SerializedName("answerCode")
	private String answerCode;

	@Expose
	@SerializedName("answerDesc")
	private String answerDesc;
}
