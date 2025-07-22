package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Umesha.M
 *
 */
@Setter
@Getter
@ToString
public class AppPermissionReqDto {
	
	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("userName")
	private String userName;
	
	@Expose
	@SerializedName("applicaple")
	private boolean applicaple;
	
	@Expose
	@SerializedName("permCode")
	private String permCode;
	
	@Expose
	@SerializedName("applicable")
	private boolean applicable;

}
