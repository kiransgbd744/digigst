
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesha.M
 *
 */
@Setter
@Getter
public class ProfitCenter2Dto {

	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("profitCenter2")
	private String profitCenter2;

	
}
