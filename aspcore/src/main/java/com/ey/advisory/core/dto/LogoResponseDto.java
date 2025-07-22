package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LogoResponseDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("logoFile")
	private  String logoFile;

	@Expose
	@SerializedName("logoType")
	private String logoType;
}
