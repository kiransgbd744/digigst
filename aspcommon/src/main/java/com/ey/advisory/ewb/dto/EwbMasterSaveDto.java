/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class EwbMasterSaveDto {

	@Expose
	@SerializedName("ewayBillNo")
	protected String ewayBillNo;

	@Expose
	@SerializedName("ewayBillDate")
	protected LocalDateTime ewayBillDate;

	@Expose
	@SerializedName("validUpto")
	protected LocalDateTime validUpto;

	@Expose
	@SerializedName("alert")
	protected String alert;

	@Expose
	@SerializedName("cancelDate")
	protected LocalDateTime cancelDate;

	@Expose
	@SerializedName("cancelReason")
	protected String cancelReason;

	@Expose
	@SerializedName("cancelRemarks")
	protected LocalDateTime cancelRemarks;

	@Expose
	@SerializedName("transporterId")
	protected String transporterId;

	@Expose
	@SerializedName("nicDistance")
	protected Integer nicDistance;

	@Expose
	@SerializedName("fromPincode")
	protected Integer fromPincode;

	/*
	 * @Expose
	 * 
	 * @SerializedName("status") protected Integer status;
	 */

	@Expose
	@SerializedName("fromPlace")
	protected String fromPlace;

	@Expose
	@SerializedName("fromState")
	protected String fromState;

	@Expose
	@SerializedName("vehicleNum")
	protected String vehicleNum;

	@Expose
	@SerializedName("vehicleType")
	protected String vehicleType;

	@Expose
	@SerializedName("transportMode")
	protected String transportMode;

	@Expose
	@SerializedName("transDocNum")
	protected String transDocNum;

	@Expose
	@SerializedName("transDocDate")
	protected LocalDate transDocDate;

	@Expose
	@SerializedName("aspDistance")
	protected Integer aspDistance;
}
