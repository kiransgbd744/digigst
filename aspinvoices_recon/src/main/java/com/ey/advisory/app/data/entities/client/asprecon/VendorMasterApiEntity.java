package com.ey.advisory.app.data.entities.client.asprecon;

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
 * @author Shashikant.Shukla
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_VENDOR_MASTER_API")
public class VendorMasterApiEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VENDOR_MASTER_API_ID")
	private Long id;

	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name = "RECIPIENT_PAN")
	private String recipientPAN;
	
	@Column(name = "VENDOR_PAN")
	private String vendorPAN;

	@Column(name = "VENDOR_NAME")
	private String vendorName;

	@Column(name = "ENTITY_ID")
	private Long entityId;

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

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

}