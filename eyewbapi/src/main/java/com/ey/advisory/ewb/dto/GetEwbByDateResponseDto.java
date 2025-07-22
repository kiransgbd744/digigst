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
public class GetEwbByDateResponseDto implements Serializable {

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
	@SerializedName("ewbNo")
	@Expose
	private String ewbNo;
	@SerializedName("ewbDate")
	@Expose
	private String ewbDate;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("genGstin")
	@Expose
	private String genGstin;
	@SerializedName("docNo")
	@Expose
	private String docNo;
	@SerializedName("docDate")
	@Expose
	private String docDate;
	@SerializedName("delPinCode")
	@Expose
	private String delPinCode;
	@SerializedName("delStateCode")
	@Expose
	private String delStateCode;
	@SerializedName("delPlace")
	@Expose
	private String delPlace;
	@SerializedName("validUpto")
	@Expose
	private String validUpto;
	@SerializedName("extendedTimes")
	@Expose
	private String extendedTimes;
	@SerializedName("rejectStatus")
	@Expose
	private String rejectStatus;

}
