package com.ey.advisory.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ERPReqAggParamsDto {

	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("date")
	private String fromDate;
	
	@Expose
	@SerializedName("toDate")
	private String toDate;
}
