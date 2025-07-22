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
public class UserAccess6Dto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("userAccess6")
	private String userAccess6;

}
