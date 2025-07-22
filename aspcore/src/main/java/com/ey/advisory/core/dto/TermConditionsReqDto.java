package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TermConditionsReqDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("groupId")
	private Long groupId;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstinId")
	private List<Long> gstinId;

	@Expose
	@SerializedName("id")
	private List<Long> id;

	@Expose
	@SerializedName("termsCond")
	private String termsCond;
}
