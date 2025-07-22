/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@EqualsAndHashCode
public class EinvEwbDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Transin/GSTIN
	 *
	 */
	@SerializedName("TransId")
	@Expose
	private String transId;
	/**
	 * Name of the transporter
	 *
	 */
	@SerializedName("TransName")
	@Expose
	private String transName;
	/**
	 * Mode of transport (Road-1, Rail-2, Air-3, Ship-4) (Required)
	 *
	 */
	@SerializedName("TransMode")
	@Expose
	private String transMode;
	/**
	 * Distance between source and destination PIN codes (Required)
	 *
	 */
	@SerializedName("Distance")
	@Expose
	private Integer distance;
	/**
	 * Tranport Document Number
	 *
	 */
	@SerializedName("TransDocNo")
	@Expose
	private String transDocNo;
	/**
	 * Transport Document Date
	 *
	 */
	@SerializedName("TransDocDt")
	@Expose
	private LocalDate transDocDt;
	/**
	 * Vehicle Number
	 *
	 */
	@SerializedName("VehNo")
	@Expose
	private String vehNo;
	/**
	 * Whether O-ODC or R-Regular
	 *
	 */
	@SerializedName("VehType")
	@Expose
	private String vehType;
	
	public static boolean isEmpty(EinvEwbDetails ewbDetails) {
		EinvEwbDetails ewbDtls = new EinvEwbDetails();
		return ewbDetails.hashCode() == ewbDtls.hashCode();
		
	}

}
