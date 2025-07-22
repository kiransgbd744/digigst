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
public class CancelIrnNICResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;
	
	@SerializedName("Irn")
	@Expose
	@XmlElement(name = "irn")
	private String irn;
	
	@SerializedName("CancelDate")
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
