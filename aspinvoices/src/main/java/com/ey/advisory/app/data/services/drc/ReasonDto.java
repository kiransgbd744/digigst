/**
 * 
 */
package com.ey.advisory.app.data.services.drc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ReasonDto {

	@Expose
	@SerializedName("rsn")
	private String rsn;
	
	@Expose
	@SerializedName("rsncd")
	private String rsncd;

}
