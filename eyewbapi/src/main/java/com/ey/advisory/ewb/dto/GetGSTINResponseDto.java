package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetGSTINResponseDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("errorCode")
	@Expose
	private String errorCode;
	@SerializedName("errorMessage")
	@Expose
	private String errorMessage;
	@SerializedName("gstin")
	@Expose
	private String gstin;
	@SerializedName("tradeName")
	@Expose
	private String tradeName;
	@SerializedName("legalName")
	@Expose
	private String legalName;
	@SerializedName("address1")
	@Expose
	private String address1;
	@SerializedName("address2")
	@Expose
	private String address2;
	@SerializedName("stateCode")
	@Expose
	private String stateCode;
	@SerializedName("pinCode")
	@Expose
	private String pinCode;
	@SerializedName("txpType")
	@Expose
	private String txpType;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("blkStatus")
	@Expose
	private String blkStatus;
	
	

}
