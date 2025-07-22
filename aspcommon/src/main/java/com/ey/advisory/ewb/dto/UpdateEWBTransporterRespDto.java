/**
 * 
 */
package com.ey.advisory.ewb.dto;

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
public class UpdateEWBTransporterRespDto {
	
	/**
	 * EwayBill Number
	 *
	 */
	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	private String ewbNo;
	/**
	 * 15 Digit Transporter GSTIN/TRANSIN
	 *
	 */
	@SerializedName("transporterId")
	@Expose
	@XmlElement(name = "transprter-id")
	private String transporterId;
	
	@SerializedName("transUpdateDate")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@Expose
	 @XmlElement(name = "trns-updt-date")
	private LocalDateTime transUpdateDate;
	
	@Expose
	@SerializedName("errorCode")
	 @XmlElement(name = "error-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	 @XmlElement(name = "err-msg")
	private String errorMessage;
	
	

}
