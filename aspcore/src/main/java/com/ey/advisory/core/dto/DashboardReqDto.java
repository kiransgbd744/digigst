package com.ey.advisory.core.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DashboardReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	private Map<String, List<String>> outwardDataSecAttrs;

	private Map<String, List<String>> inwardDataSecAttrs;
}
