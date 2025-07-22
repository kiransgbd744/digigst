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
public class UsrReasonList {

	@SerializedName("readonCode")
	@Expose
	private String readonCode;
	
	@SerializedName("explanation")
	@Expose
	private String explanation;
	
	@SerializedName("id")
	@Expose
	private Long id;
	
}
