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

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)


public class RejectEwbSoapRespDto {
	@Expose
	@SerializedName("ewayBillNo")
	@XmlElement(name = "ewb-no")
	private String ewayBillNo;

	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@SerializedName("ewbRejectedDate")
	@XmlElement(name = "reject-date")
	private LocalDateTime ewbRejectedDate;
	
	
	@Expose
	@SerializedName("errorCode")
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "error-message")
	private String errorMessage;


}
