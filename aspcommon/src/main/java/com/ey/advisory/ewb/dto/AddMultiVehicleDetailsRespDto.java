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
import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AddMultiVehicleDetailsRespDto implements Serializable{

	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	private String ewbNo;
	
	@SerializedName("groupNo")
	@Expose
	@XmlElement(name = "group-no")
	private String groupNo;
	
	@SerializedName("vehAddedDate")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@Expose
	@XmlElement(name = "veh-added-date")
	private LocalDateTime vehAddedDate;
	
	@Expose
	@SerializedName("errorCode")
	@XmlElement(name = "error-code")
	private String errorCode;
	
	@Expose
	@SerializedName("msg")
	private String message;
	
	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "error-message")
	private String errorMessage;
	private static final long serialVersionUID = -2944945751585398185L;

}
