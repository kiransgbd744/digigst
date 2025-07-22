package com.ey.advisory.einv.dto;

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
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelIrnSAPResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;
	
	@SerializedName("irn")
	@Expose
	@XmlElement(name = "irn")
	private String irn;
	
	@SerializedName("cancelReason")
	@Expose
	@XmlElement(name = "cancelReason")
	private String cancelReason;
	
	@SerializedName("cancelRemarks")
	@Expose
	@XmlElement(name = "cancelRemarks")
	private String cancelRemarks;
	
	@SerializedName("cancelDate")
	@Expose
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "cancel-date")
	private LocalDateTime cancelDate;
	
	@SerializedName("errorCode")
	@Expose
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@SerializedName("errorMessage")
	@Expose
	@XmlElement(name = "error-message")
	private String errorMessage;

}
