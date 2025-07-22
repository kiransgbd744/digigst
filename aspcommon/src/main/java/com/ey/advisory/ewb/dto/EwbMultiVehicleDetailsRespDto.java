package com.ey.advisory.ewb.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@ToString
public class EwbMultiVehicleDetailsRespDto {

	@SerializedName("docNo")
	@Expose
	private String docNo;

	@SerializedName("ewbNo")
	@Expose
	private Long ewbNo;

	@SerializedName("ewbDate")
	@Expose
	private String ewbDate;

	@SerializedName("ewbStatus")
	@Expose
	private String ewbStatus;

	@SerializedName("suppGstin")
	@Expose
	private String suppGstin;

	@SerializedName("groupNo")
	@Expose
	private Long groupNo;

	@SerializedName("fromStateCode")
	@Expose
	private String fromStateCode;

	@SerializedName("fromState")
	@Expose
	private String fromState;

	@SerializedName("toPlacecode")
	@Expose
	private String toPlacecode;

	@SerializedName("toPlace")
	@Expose
	private String toPlace;

	@SerializedName("transMode")
	@Expose
	private String transMode;

	@SerializedName("totalQty")
	@Expose
	private Long totalQty;

	@SerializedName("vehicleQty")
	@Expose
	private Long vehicleQty;

	@SerializedName("vehicleNo")
	@Expose
	private String vehicleNo;

	@SerializedName("transDocNum")
	@Expose
	private String transDocNum;

	@SerializedName("transDocDate")
	@Expose
	private String transDocDate;

	@SerializedName("reason")
	@Expose
	private String reason;

	@SerializedName("remarks")
	@Expose
	private String remarks;

	@SerializedName("unit")
	@Expose
	private String unit;

	@SerializedName("isDelete")
	@Expose
	private Boolean isDelete;

	@SerializedName("function")
	@Expose
	private String function;

	@SerializedName("errorCode")
	@Expose
	private String errorCode;

	@SerializedName("errorMessage")
	@Expose
	private String errorMessage;

}
