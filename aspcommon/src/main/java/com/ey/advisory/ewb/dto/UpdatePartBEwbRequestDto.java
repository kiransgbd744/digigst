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
public class UpdatePartBEwbRequestDto implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "id-token")
	private String idToken;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;
	
	@Expose
	@SerializedName("ewbNo")
	@XmlElement(name = "ewb-no")
	private String ewbNo;

	@Expose
	@SerializedName("vehicleNo")
	@XmlElement(name = "veh-no")
	private String vehicleNo;

	@Expose
	@SerializedName("fromPlace")
	@XmlElement(name = "from-place")
	private String fromPlace;
	
	@Expose
	@SerializedName("fromState")
	@XmlElement(name = "from-state")
	private String fromState;

	@Expose
	@SerializedName("reasonCode")
	@XmlElement(name = "rsn-code")
	private String reasonCode;

	@Expose
	@SerializedName("reasonRem")
	 @XmlElement(name = "rsn-rmk")
	private String reasonRem;
	
	@Expose
	@SerializedName("transDocNo")
	 @XmlElement(name = "trns-doc-no")
	private String transDocNo;

	@Expose
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	@SerializedName("transDocDate")
	 @XmlElement(name = "trns-doc-dt")
	private LocalDate transDocDate;
	
	@Expose
	@SerializedName("transMode")
	 @XmlElement(name = "trans-mode")
	private String transMode;


	@Expose
	@SerializedName("vehicleType")
	 @XmlElement(name = "veh-type")
	private String vehicleType;

}
