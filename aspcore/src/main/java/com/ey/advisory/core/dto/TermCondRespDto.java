package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TermCondRespDto {

	@Expose
	@SerializedName("groupId")
	private Long groupId;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("gstins")
	private List<TermCondGstinDto> gstinDtos;

	@Expose
	@SerializedName("items")
	private List<TermCondItemRespDto> termCondItemRespDtos;
}
