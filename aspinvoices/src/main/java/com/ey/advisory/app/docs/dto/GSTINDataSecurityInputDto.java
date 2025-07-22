package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Data
public class GSTINDataSecurityInputDto {

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

}
