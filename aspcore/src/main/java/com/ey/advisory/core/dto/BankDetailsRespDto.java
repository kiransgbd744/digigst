package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class BankDetailsRespDto {

	@Expose
	@SerializedName("groupCode")
	private String groupCode;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("groupId")
	private Long groupId;

	@Expose
	@SerializedName("entityName")
	private String entityName;

	@Expose
	@SerializedName("gstins")
	private List<GstinTemplateDto> gstins;

	@Expose
	@SerializedName("item")
	private List<BankDetailsItemRespDto> items;

}
