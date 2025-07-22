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

/**
 * @author Siva Reddy
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateEWBByIrnSoapRespDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("EwbNo")
	@XmlElement(name = "Ewb-No")
	private String ewbNo;
	
	@Expose
	@SerializedName("EwbDt")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "Ewb-Dt")
	private LocalDateTime ewbDt;
	
	@Expose
	@SerializedName("EwbValidTill")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "Ewb-ValidTill")
	private LocalDateTime ewbValidTill;
	
	@SerializedName("ErrorCode")
	@Expose
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@SerializedName("ErrorMessage")
	@Expose
	@XmlElement(name = "error-message")
	private String errorMessage;
	
	@SerializedName("nicDistance")
	@Expose
	@XmlElement(name = "nic-dist")
	private String nicDistance;
	
	@SerializedName("infoErrorCode")
	@Expose
	@XmlElement(name = "info-err-cd")
	private String infoErrorCode;
	
	
	@SerializedName("infoErrorMessage")
	@Expose
	@XmlElement(name = "info-err-msg")
	private String infoErrorMessage;
}
