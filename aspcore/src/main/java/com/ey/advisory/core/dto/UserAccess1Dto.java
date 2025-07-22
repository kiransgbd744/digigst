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
@Getter
@Setter
@ToString
public class UserAccess1Dto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("userAccess1")
	private String userAccess1;

}
