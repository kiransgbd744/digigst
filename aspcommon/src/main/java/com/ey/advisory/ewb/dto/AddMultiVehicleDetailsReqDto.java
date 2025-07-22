package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddMultiVehicleDetailsReqDto implements Serializable {
	
	
	@XmlElement(name = "id-token")
	private String idToken;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	/**
	 * Ewaybill Number (Required)
	 * 
	 */
	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-No")
	private String ewbNo;
	/**
	 * Group Number (Required)
	 * 
	 */
	@SerializedName("groupNo")
	@Expose
	@XmlElement(name = "group-no")
	private String groupNo;
	/**
	 * Vehicle Number (Required)
	 * 
	 */
	@SerializedName("vehicleNo")
	@Expose
	@XmlElement(name = "vehicle-no")
	private String vehicleNo;
	/**
	 * Transport Document Number (Required)
	 * 
	 */
	@SerializedName("transDocNo")
	@Expose
	@XmlElement(name = "trans-doc-no")
	private String transDocNo;
	/**
	 * Transport Document Date (Required)
	 * 
	 */
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	@SerializedName("transDocDate")
	@Expose
	@XmlElement(name = "trans-doc-date")
	private LocalDate transDocDate;
	/**
	 * Quantity (Required)
	 * 
	 */
	@SerializedName("quantity")
	@Expose
	private BigDecimal quantity;
	
	
	private static final long serialVersionUID = 8097193022919213438L;

}
