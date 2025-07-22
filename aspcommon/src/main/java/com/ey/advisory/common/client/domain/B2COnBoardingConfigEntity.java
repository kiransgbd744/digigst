package com.ey.advisory.common.client.domain;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @POST For ENTITY_CONFIG_PARAMTR
 * 
 * @author Siva Reddy
 *
 */
@Entity
@Table(name = "B2C_QR_ONBOARDING_CONFIG")
@Setter
@Getter
@ToString
public class B2COnBoardingConfigEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Expose
	@SerializedName("pan")
	@Column(name = "PAN")
	private String pan;

	@Expose
	@SerializedName("qrMode")
	@Column(name = "QR_MODE")
	private String qrMode;

	@Expose
	@SerializedName("transMode")
	@Column(name = "TRANS_MODE")
	private String transMode;

	@Expose
	@SerializedName("payeeAddress")
	@Column(name = "PAYEE_ADDRESS")
	private String payeeAddress;

	@Expose
	@SerializedName("payeeName")
	@Column(name = "PAYEE_NAME")
	private String payeeName;

	@Expose
	@SerializedName("payeeMerCode")
	@Column(name = "PAYEE_MERCHANT_CODE")
	private String payeeMerCode;

	@Expose
	@SerializedName("transQRMed")
	@Column(name = "TRANS_QR_MEDIUM")
	private String transQRMed;

	@Expose
	@SerializedName("qrExpiry")
	@Column(name = "QR_EXPIRE_TIME")
	private String qrExpireTime;

	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	private String profitCentre;

	@Expose
	@SerializedName("option")
	@Column(name = "OPTION_SELECTED")
	private String optionSelected;

	@Expose
	@SerializedName("entityName")
	@Column(name = "ENTITY_NAME")
	private String entityName;

	@Expose
	@SerializedName("paymentInfo")
	@Column(name = "PAYMENT_INFO")
	private String paymentInfo;

	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Expose
	@SerializedName("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;
}