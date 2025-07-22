package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppPermissionRespDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("applicaple")
	private boolean applicaple;

	@Expose
	@SerializedName("permCode")
	private String permCode;

	@Expose
	@SerializedName("permDesc")
	private String permDesc;

	@Expose
	@SerializedName("category")
	private String category;

	@Expose
	@SerializedName("email")
	private String email;

}
