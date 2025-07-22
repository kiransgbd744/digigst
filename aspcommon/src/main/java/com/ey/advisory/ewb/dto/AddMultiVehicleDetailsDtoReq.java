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
public class AddMultiVehicleDetailsDtoReq {

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
	 * Vehicle Number (Required)
	 * 
	 */
	@SerializedName("vehicleNo")
	@Expose
	private String vehicleNo;
	/**
	 * Transport Document Number (Required)
	 * 
	 */
	@SerializedName("transDocNo")
	@Expose
	private String transDocNo;
	/**
	 * Transport Document Date (Required)
	 * 
	 */
	@SerializedName("transDocDate")
	@Expose
	private String transDocDate;
	/**
	 * Quantity (Required)
	 * 
	 */
	@SerializedName("quantity")
	@Expose
	private Long quantity;

}
