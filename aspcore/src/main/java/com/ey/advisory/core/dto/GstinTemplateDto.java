package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GstinTemplateDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstin")
	private String gstin;
}
