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
public class UserAccess5Dto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("userAccess5")
	public String userAccess5;

	
}
