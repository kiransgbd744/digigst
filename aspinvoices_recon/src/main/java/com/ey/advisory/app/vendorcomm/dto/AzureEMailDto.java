package com.ey.advisory.app.vendorcomm.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class AzureEMailDto {
	
	@Expose
	@SerializedName("EntityName")
	private String entityName;
	
	@Expose
	@SerializedName("Source")
	private String source;
	
	@Expose
	@SerializedName("FY")
	private String fy;
	
	@Expose
	@SerializedName("ReconType")
	private String reconType;

	@Expose
	@SerializedName("VendorDetails")
	private List<VendorAzureEmailCommDto> vendorDetails;

}
