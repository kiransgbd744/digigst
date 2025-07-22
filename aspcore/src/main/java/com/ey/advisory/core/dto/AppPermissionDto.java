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
public class AppPermissionDto {

	@Expose
	@SerializedName("role")
	private List<String> roles;
	
	@Expose
	@SerializedName("permission")
	private List<String> permissions; 
}
