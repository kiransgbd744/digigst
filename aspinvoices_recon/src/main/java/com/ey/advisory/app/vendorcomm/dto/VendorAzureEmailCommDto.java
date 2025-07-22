package com.ey.advisory.app.vendorcomm.dto;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorAzureEmailCommDto {
	@Expose
	@SerializedName("VGSTIN")
	private String vendorGstin;

	@Expose
	@SerializedName("vendorName")
	private String vendorName;

	@Expose
	@SerializedName("VndrPrmryEmail")
	private String vendPrimEmailId;

	@Expose
	@SerializedName("VndrSecndEmail")
	private Set<String> secondaryEmailIds = new HashSet<>();

	@Expose
	@SerializedName("RcpntEmail")
	private Set<String> recipientEmailIds = new HashSet<>();
	
	@Expose
	@SerializedName("totalSecEmail")
	private Set<String> totalSecEmailIds = new HashSet<>();

	@Expose
	@SerializedName("totalRecpEmail")
	private Set<String> totalRecpEmailIds = new HashSet<>();

	@Expose
	@SerializedName("EmailStatus")
	private String emailStatus;

	@Expose
	@SerializedName("VndrContactNo")
	private String vendorContactNumber;

	@Expose
	@SerializedName("VcomRequestID")
	private Long requestID;

	@Expose
	@SerializedName("CreatedBy")
	private String createdBy;

	private String updatedOn;

	@Expose
	@SerializedName("ReturnType")
	private String returnType;
	
	@Expose
	@SerializedName("RGSTIN")
	private String RGSTIN;
	
	// Vendor Name for email triggering
	@Expose
	@SerializedName("CreateName")
	private String vendrName;
	
	@Expose
	@SerializedName("ReconType")
	private String reconType;

	

}
