package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TemplateDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("tempType")
	private String tempType;
}
