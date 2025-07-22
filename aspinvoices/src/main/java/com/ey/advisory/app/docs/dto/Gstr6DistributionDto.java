/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author kiran
 *
 */
@Data
public class Gstr6DistributionDto {
	
	@Expose
	@SerializedName("id")
	private String id;

	
	@Expose
	@SerializedName("sgstin")
	private String sgstin;
	
	
	@Expose
	@SerializedName("documentType")
	private String documentType;
	
	@Expose
	@SerializedName("entityId")
	private String entityId;
	
	@Expose
	@SerializedName("docnumber")
	private String docnumber;

	
	
}
