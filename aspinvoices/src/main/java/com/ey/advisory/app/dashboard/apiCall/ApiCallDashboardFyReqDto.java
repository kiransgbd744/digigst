package com.ey.advisory.app.dashboard.apiCall;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiCallDashboardFyReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("fyDetails")
	private List<String> fyDetails;
}
