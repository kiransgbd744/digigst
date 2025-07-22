/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Gstr6CalculateR6Dto {

	@Expose
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;
	
	@Expose
	private Long userRequestId;

}
