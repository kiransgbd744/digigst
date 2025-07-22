/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class RevertingInvReqDto {

	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("gstnData")
	private boolean gstnData;
	
	@Expose
	@SerializedName("dataType")
	private String dataType;

}
