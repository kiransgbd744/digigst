
package com.ey.advisory.app.vendorcomm.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class VendorEmailStatusReqDto {
	
	@Expose
	@SerializedName("Id")
	private Long id;;

	@Expose
	@SerializedName("ReconType")
	private String reconType;
	
	@Expose
	@SerializedName("EntityId")
	private Long entityId;
	
	@Expose
	@SerializedName("Status")
	private String status;
	
	@Expose
	@SerializedName("RequestID")
	private Long requestID;
	
	


}
