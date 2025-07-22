/**
 * 
 */
package com.ey.advisory.einv.dto;

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
import lombok.ToString;

/**
 * @author Arun KA
 *
 */

@Data
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelEWBBillSoapRespDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("ewayBillNo")
	@XmlElement(name = "ewb-billno")
	private String ewayBillNo;

	@Expose
	@SerializedName("cancelDate")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "cancel-date")
	private LocalDateTime cancelDate;
	
	@Expose
	@SerializedName("errorCode")
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "error-message")
	private String errorMessage;
	
}
