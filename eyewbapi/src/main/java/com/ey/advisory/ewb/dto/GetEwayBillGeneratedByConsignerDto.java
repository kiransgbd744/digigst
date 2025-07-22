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
public class GetEwayBillGeneratedByConsignerDto implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@SerializedName("ewayBillNo")
	@Expose
	private String ewayBillNo;
	@SerializedName("ewayBillDate")
	@Expose
	private String ewayBillDate;
	@SerializedName("validUpto")
	@Expose
	private String validUpto;
	@SerializedName("alert")
	@Expose
	private String alert;	
	@SerializedName("errorCode")
	@Expose
	private String errorCode;
	@SerializedName("errorMessage")
	@Expose
	private String errorMessage;

}
