/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtendEWBReqDto implements Serializable {
	
	@XmlElement(name = "id-token")
	private String idToken;
	
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;
	
	/**
	 * Ewaybill Number (Required)
	 *
	 */
	@Expose
	@SerializedName("ewbNo")
	private String ewbNo;
	/**
	 * Vehicle Number
	 *
	 */
	@Expose
	@SerializedName("vehicleNo")
	private String vehicleNo;
	/**
	 * From Place (Required)
	 *
	 */
	@Expose
	@SerializedName("fromPlace")
	private String fromPlace;
	/**
	 * From State (Required)
	 *
	 */
	@Expose
	@SerializedName("fromState")
	private String fromState;
	/**
	 * Remaining Distance (Required)
	 *
	 */
	@Expose
	@SerializedName("remainingDistance")
	private Integer remainingDistance;
	/**
	 * Transport Document Number
	 *
	 */
	@Expose
	@SerializedName("transDocNo")
	private String transDocNo;
	/**
	 * Transport Document Date
	 *
	 */
	@Expose
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	@SerializedName("transDocDate")
	private LocalDate transDocDate;
	/**
	 * Transport Mode (Required)
	 *
	 */
	@Expose
	@SerializedName("transMode")
	private String transMode;
	/**
	 * Extension Reason Code (Required)
	 *
	 */
	@Expose
	@SerializedName("extnRsnCode")
	private String extnRsnCode;
	/**
	 * Extension Remarks (Required)
	 *
	 */
	@Expose
	@SerializedName("extnRemarks")
	private String extnRemarks;
	/**
	 * From Pincode (Required)
	 *
	 */
	@Expose
	@SerializedName("fromPincode")
	private String fromPincode;
	/**
	 * consignmentStatus(T)
	 *
	 */
	@Expose
	@SerializedName("consignmentStatus")
	private String consignmentStatus;
	/**
	 * transit Type
	 *
	 */
	@Expose
	@SerializedName("transitType")
	private String transitType;
	/**
	 * addressLine1
	 *
	 */
	@Expose
	@SerializedName("addressLine1")
	private String addressLine1;
	/**
	 * addressLine2
	 *
	 */
	@Expose
	@SerializedName("addressLine2")
	private String addressLine2;
	/**
	 * addressLine3
	 *
	 */
	@Expose
	@SerializedName("addressLine3")
	private String addressLine3;
	private final static long serialVersionUID = 2277574414705505210L;

}
