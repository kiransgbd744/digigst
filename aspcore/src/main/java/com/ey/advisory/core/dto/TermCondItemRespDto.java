package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TermCondItemRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstinId")
	private Long gstinId;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("termsCond")
	private String termsCond;
}
