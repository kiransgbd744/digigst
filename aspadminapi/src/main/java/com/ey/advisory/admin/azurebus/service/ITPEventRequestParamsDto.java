package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ITPEventRequestParamsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String tenantCode;
	
	@Expose
	private String requestId;
	
	@Expose
	@SerializedName("users")
	private List<ITPEventCommonResponseDto> users;


}
