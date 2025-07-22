package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ErpCompanyCodeDto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("sourceId")
	private String sourceId;
}
