package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AppProfilePermissionResp {

	@Expose
	@SerializedName("profileName")
	private String profileName;
	
	@Expose
	@SerializedName("promCode")
	private String promCode;
	
}
