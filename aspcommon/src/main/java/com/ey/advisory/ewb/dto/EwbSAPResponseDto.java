/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
public class EwbSAPResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("ewayBillNo")
	@Expose
	@XmlElement(name = "ewb-no")
	private String ewayBillNo;

	@SerializedName("ewayBillDate")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ewb-date")
	private LocalDateTime ewayBillDate;

	@SerializedName("validUpto")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "valid-upto")
	private LocalDateTime validUpto;

	@SerializedName("alert")
	@Expose
	private String alert;

	@Expose
	@SerializedName("transporterId")
	@XmlElement(name = "trnsprt-id")
	private String transporterID;

	@Expose
	@SerializedName("vehicleNum")
	@XmlElement(name = "veh-no")
	private String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@XmlElement(name = "veh-typ")
	private String vehicleType;

	@Expose
	@SerializedName("transportMode")
	@XmlElement(name = "trnsprt-mode")
	private String transportMode;

	@Expose
	@SerializedName("transDocNum")
	@XmlElement(name = "trnsprt-doc-no")
	private String transportDocNo;

	@Expose
	@SerializedName("transDocDate")
	@XmlElement(name = "trnsprt-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate transportDocDate;

	@SerializedName("aspDistance")
	@Expose
	@XmlElement(name = "nic-dist")
	private Integer aspDistance;

	@SerializedName("fromPlace")
	@Expose
	@XmlElement(name = "from-place")
	private String fromPlace;

	@SerializedName("toPlace")
	@Expose
	@XmlElement(name = "to-place")
	private String toPlace;

	@SerializedName("fromPincode")
	@Expose
	@XmlElement(name = "from-pincode")
	private Integer fromPincode;

	@SerializedName("fromState")
	@Expose
	@XmlElement(name = "from-state")
	private String fromState;

	@SerializedName("cancelDate")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "cancel-date")
	private LocalDateTime cancelDate;
	
	@SerializedName("nicDistance")
	@Expose
	private Integer nicDistance;
}
