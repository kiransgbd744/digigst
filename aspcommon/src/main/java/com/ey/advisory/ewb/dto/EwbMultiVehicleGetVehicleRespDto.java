package com.ey.advisory.ewb.dto;

import java.util.List;

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
public class EwbMultiVehicleGetVehicleRespDto {
	
	@SerializedName("ewbNo")
	@Expose
	private Long ewbNo;
	
	@SerializedName("reasonCode")
	@Expose
	private String reasonCode;
	
	@SerializedName("reasonRem")
	@Expose
	private String reasonRem;
	
	@SerializedName("fromPlace")
	@Expose
	private String fromPlace;
	
	@SerializedName("fromState")
	@Expose
	private String fromState;
	
	@SerializedName("toPlace")
	@Expose
	private String toPlace;
	
	@SerializedName("toState")
	@Expose
	private String toState;
	
	@SerializedName("transMode")
	@Expose
	private String transMode;
	
	@SerializedName("totalQty")
	@Expose
	private Long totalQty;
	
	@SerializedName("unitCode")
	@Expose
	private String unitCode;
	
	@SerializedName("docNo")
	@Expose
	private Long docNo;
	
	@SerializedName("suppGstin")
	@Expose
	private String gstin;
	
	@SerializedName("vechicleDetails")
	@Expose
	List<VehicleDetailsRespDto> listVehicles;

}
