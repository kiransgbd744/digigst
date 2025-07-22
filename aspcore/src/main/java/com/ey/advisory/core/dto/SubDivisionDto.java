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
public class SubDivisionDto {

	@Expose
	@SerializedName("id")
	private Long id;

	/**
	 * 
	 */
	@Expose
	@SerializedName("subDivision")
	public String subDivision;

}
