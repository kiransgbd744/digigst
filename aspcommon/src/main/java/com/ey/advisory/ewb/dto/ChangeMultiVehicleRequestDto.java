package com.ey.advisory.ewb.dto;

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
public class ChangeMultiVehicleRequestDto {

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
	 * Group Number (Required)
	 * 
	 */
	@SerializedName("groupNo")
	@Expose
	private String groupNo;
	/**
	 * Old Vehicle Number (Required)
	 * 
	 */
	@SerializedName("oldvehicleNo")
	@Expose
	private String oldvehicleNo;
	/**
	 * New Vehicle Number (Required)
	 * 
	 */
	@SerializedName("newVehicleNo")
	@Expose
	private String newVehicleNo;
	/**
	 * Old Tran Number (Required)
	 * 
	 */
	@SerializedName("oldTranNo")
	@Expose
	private String oldTranNo;
	/**
	 * New Tran Number (Required)
	 * 
	 */
	@SerializedName("newTranNo")
	@Expose
	private String newTranNo;
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
	private Integer fromState;
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

}
