/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
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
public class UpdatePartBEwbResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	@SerializedName("vehUpdDate")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	 @XmlElement(name = "veh-updt-dt")
	private LocalDateTime vehUpdDate;

	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@SerializedName("validUpto")
	 @XmlElement(name = "valid-upto")
	private LocalDateTime validUpto;
	
	@Expose
	@SerializedName("errorCode")
	 @XmlElement(name = "err-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	 @XmlElement(name = "err-msg")
	private String errorMessage;
}
