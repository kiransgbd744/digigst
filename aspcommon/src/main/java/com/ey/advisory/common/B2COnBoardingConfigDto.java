package com.ey.advisory.common;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * 
 * @POST For B2C_QR_ONBOARDING_CONFIG
 * 
 * @author Siva Reddy
 *
 */

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class B2COnBoardingConfigDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("pan")
	private String pan;

	@Expose
	@SerializedName("qrMode")
	private String qrMode;

	@Expose
	@SerializedName("transMode")
	private String transMode;

	@Expose
	@SerializedName("payeeAddress")
	private String payeeAddress;

	@Expose
	@SerializedName("payeeName")
	private String payeeName;

	@Expose
	@SerializedName("payeeMerCode")
	private String payeeMerCode;

	@Expose
	@SerializedName("transQRMed")
	private String transQRMed;

	@Expose
	@SerializedName("qrExpiry")
	private String qrExpireTime;

	@Expose
	@SerializedName("isActive")
	private boolean isActive;

	@Expose
	@SerializedName("createdOn")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("createdBy")
	private String createdBy;

}