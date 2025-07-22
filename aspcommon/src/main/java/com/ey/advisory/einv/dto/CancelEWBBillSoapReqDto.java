/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Arun KA
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelEWBBillSoapReqDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;

	@Expose
	@SerializedName("ewbNo")
	@XmlElement(name = "ewb-no")
	private String ewbNo;

	@Expose
	@SerializedName("cancelRsnCode")
	@XmlElement(name = "cancel-rsn-code")
	private String cancelRsnCode;

	@Expose
	@SerializedName("cancelRmrk")
	@XmlElement(name = "cancel-rmrk")
	private String cancelRmrk;
	
	@XmlElement(name = "id-token")
	private String idToken;
} 