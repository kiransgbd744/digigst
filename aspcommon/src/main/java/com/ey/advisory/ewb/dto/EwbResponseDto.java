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
public class EwbResponseDto implements Serializable{

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
	
	@SerializedName("transactionType")
	@Expose
	 @XmlElement(name = "trns-type")
	private String transactionType;
	
	@SerializedName("errorCode")
	@Expose
	 @XmlElement(name = "error-code")
	private String errorCode;
	
	@SerializedName("errorMessage")
	@Expose
	 @XmlElement(name = "error-message")
	private String errorDesc;
	
	@SerializedName("nicDistance")
	@Expose
	 @XmlElement(name = "nic-dist")
	private Integer nicDistance;
	

	}


