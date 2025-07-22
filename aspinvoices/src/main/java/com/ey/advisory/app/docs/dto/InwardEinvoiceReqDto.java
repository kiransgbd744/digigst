/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class InwardEinvoiceReqDto {
	
	@Expose
	@SerializedName("irn")
	private String irn;
	
	@Expose
	@SerializedName("supplyType")
	private String supplyType;
	
	@Expose
	@SerializedName("irnStatus")
	private String irnStatus;
	
	@Expose
	@SerializedName("docNum")
	private String docNum;
	
	@Expose
	@SerializedName("docType")
	private String docType;

}
