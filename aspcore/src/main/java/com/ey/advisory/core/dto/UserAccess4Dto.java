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
public class UserAccess4Dto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("userAccess4")
	public String userAccess4;
	
}
