package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GET_GSTIN_VALIDATOR_API_PUSH")
public class GetGstinMasterDetailApiPushEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "META_ID")
	private Long metaId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "VENDOR_NAME_AS_UPLOADED")
	private String vendorNameAsUploaded;
	
	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Column(name = "LAST_GET_CALL_STATUS")
	private String lastGetCallStatus;
	
	@Column(name = "LAST_UPDATED")
	private LocalDateTime lastUpdated;
	
	@Column(name = "GSTIN_STATUS")
	private String gstinStatus;
	
	@Column(name = "EINV_APPLICABLE")
	private String einvApplicable;
	
	@Column(name = "BUSINESS_CONSTITUTION")
	private String businessConstitution;
	
	@Column(name = "TAXPAYER_TYPE")
	private String taxpayerType;
	
	@Column(name = "LEGAL_NAME")
	private String legalName;
	
	@Column(name = "TRADE_NAME")
	private String tradeName;
	
	@Column(name = "REGISTRATION_DATE")
	private LocalDate registrationDate;
	
	@Column(name = "CANCELLATION_DATE")
	private LocalDate cancellationDate;
	
	@Column(name = "BUSINESS_NATURE_ACTIVITY")
	private String businessNatureActivity;
	
	@Column(name = "BULIDING_NAME")
	private String buildingName;
	
	@Column(name = "STREET")
	private String street;
	
	@Column(name = "LOCATION")
	private String location;
	
	@Column(name = "DOOR_NUM")
	private String doorNum;
	
	@Column(name = "STATE")
	private String state;
	
	@Column(name = "FLOOR_NUM")
	private String floorNum;
	
	@Column(name = "PIN")
	private String pin;
	
	@Column(name = "CENTRE_JURISDICTION")
	private String centreJurisdiction;
	
	@Column(name = "STATE_JURISDICTION")
	private String stateJurisdiction;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;	
	
	@Column(name = "ERROR_DESCRIPTION")
	private String errorDiscription;
	
	@Column(name = "CUSTOMER_CODE")
	private String customerCode;
	
	@Column(name = "IS_Vendor_Validation_API")
	private Boolean vendorValidationApi = false;
	
}