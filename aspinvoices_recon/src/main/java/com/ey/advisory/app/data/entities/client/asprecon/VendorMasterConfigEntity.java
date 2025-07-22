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
 * @author Ravindra V S
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_VENDOR_MASTER_CONFIG")
public class VendorMasterConfigEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VENDOR_MASTER_ID")
	private Long id;

//	@Column(name = "RECIPIENT_GSTIN")
//	private String recipientGstin;

	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name = "LEGAL_NAME")
	private String legalName;

	@Column(name = "TRADE_NAME")
	private String tradeName;

	@Column(name = "TAXPAYER_TYPE")
	private String taxpayerType;

	@Column(name = "GSTIN_STATUS")
	private String gstinStatus;

	@Column(name = "IS_GSTIN_UPDATED")
	private Boolean isGstinUpdated;

	@Column(name = "GSTIN_UPDATED_DATE")
	private LocalDateTime gstinUpdatedOn;

	@Column(name = "IS_NAME_UPDATED")
	private Boolean isNameUpdated;

	@Column(name = "NAME_UPDATED_DATE")
	private LocalDateTime nameUpdatedOn;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

//	@Column(name = "IS_DELETE")
//	private Boolean isDelete;

	@Column(name = "IS_FETCHED")
	private Boolean isFetched;

	@Column(name = "SOURCE_DATA")
	private String sourceData;

	@Column(name = "REGISTRATION_DATE")
	private LocalDate dateOfRegistration;
	
	@Column(name = "CANCELLATION_DATE")
	private LocalDate dateOfCancellation;
}