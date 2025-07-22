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
public class GetEWBDetailsByIrnRespDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("EwbNo")
	@XmlElement(name = "Ewb-no")
	private String ewbNo;

	@Expose
	@SerializedName("EwbDt")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "Ewb-dt")
	private LocalDateTime ewbDt;

	@Expose
	@SerializedName("EwbValidTill")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "Ewb-validtill")
	private LocalDateTime ewbValidTill;

	@Expose
	@SerializedName("errorCode")
	@XmlElement(name = "error-code")
	private String errorCode;

	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "error-message")
	private String errorMessage;

	@SerializedName("Status")
	@Expose
	@XmlElement(name = "Status")
	private String status;

	@SerializedName("GenGstin")
	@Expose
	@XmlElement(name = "Gen-gstin")
	private String genGstin;

}
