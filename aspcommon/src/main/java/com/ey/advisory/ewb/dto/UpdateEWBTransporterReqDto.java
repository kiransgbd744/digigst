/**
 * 
 */
package com.ey.advisory.ewb.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Setter
@Getter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateEWBTransporterReqDto {
	
	@XmlElement(name = "id-token")
	private String idToken;

	@SerializedName("gstin")
	@Expose
	private String gstin;
	
	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;

	/**
	 * EwayBill Number
	 *
	 */
	@SerializedName("ewbNo")
	@Expose
	@XmlElement(name = "ewb-no")
	private String ewbNo;
	/**
	 * 15 Digit Transporter GSTIN/TRANSIN
	 *
	 */
	@SerializedName("transporterId")
	@Expose
	@XmlElement(name = "trnsprter-id")
	private String transporterId;
	private static final long serialVersionUID = 7238494684565913477L;

}
