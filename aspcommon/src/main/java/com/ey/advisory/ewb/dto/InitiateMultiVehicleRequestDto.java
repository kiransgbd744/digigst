package com.ey.advisory.ewb.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@Getter
@Setter
@ToString
public class InitiateMultiVehicleRequestDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	/**
	 * Ewaybill Number (Required)
	 * 
	 */
	@SerializedName("ewbNo")
	@Expose
	private Long ewbNo;

	/**
	 * Document Number (Required)
	 * 
	 */
	@SerializedName("docNo")
	@Expose
	private String docNo;

	/**
	 * Reason Code (Required)
	 * 
	 */
	@SerializedName("reasonCode")
	@Expose
	private String reasonCode;

	/**
	 * Remarks (Required)
	 * 
	 */
	@SerializedName("reasonRem")
	@Expose
	private String reasonRem;

	/**
	 * From Place (Required)
	 * 
	 */
	@SerializedName("fromPlace")
	@Expose
	private String fromPlace;

	/**
	 * From State (Required)
	 * 
	 */
	@SerializedName("fromState")
	@Expose
	private String fromState;

	/**
	 * To Place (Required)
	 * 
	 */
	@SerializedName("toPlace")
	@Expose
	private String toPlace;

	/**
	 * From State (Required)
	 * 
	 */
	@SerializedName("toState")
	@Expose
	private String toState;

	/**
	 * Mode of transport (Road-1, Rail-2, Air-3, Ship-4) (Required)
	 * 
	 */
	@SerializedName("transMode")
	@Expose
	private String transMode;

	/**
	 * Total Quantity (Required)
	 * 
	 */
	@SerializedName("totalQuantity")
	@Expose
	private Long totalQuantity;

	/**
	 * Unit Code (Required)
	 * 
	 */
	@SerializedName("unitCode")
	@Expose
	private String unitCode;
	
	/**
	 * Unit Code (Required)
	 * 
	 */
	@SerializedName("docHeaderId")
	@Expose
	private Long docHeaderId;
	
	@SerializedName("vehicleDetails")
	@Expose
	List<AddMultiVehicleDetailsDtoReq> vechicleDetails;

}
