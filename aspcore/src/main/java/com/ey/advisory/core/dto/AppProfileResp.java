package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AppProfileResp {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("profileName")
	private String profileName;

	@Expose
	@SerializedName("appProfilePermissionResps")
	List<AppProfilePermissionResp> appProfilePermissionResps;

}
