/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InitiateMultiVehicleRespDto implements Serializable{
	
	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	public String ewbNo;
	@SerializedName("groupNo")
	@Expose
	 @XmlElement(name = "grp-no")
	public String groupNo;
	@SerializedName("createdDate")
	@Expose
	@XmlElement(name = "created-dt")
	public String createdDate;
	
	@Expose
	@SerializedName("errorCode")
	 @XmlElement(name = "err-code")
	private String errorCode;
	
	@Expose
	@SerializedName("errorMessage")
	@XmlElement(name = "err-message")
	private String errorMessage;
	
	private static final  long serialVersionUID = 3747081539862873567L;

}
