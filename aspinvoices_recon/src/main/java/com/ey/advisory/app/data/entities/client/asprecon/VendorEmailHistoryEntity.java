package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name= "VENDOR_EMAIL_HISTORY")
@Getter
@Setter
@ToString
public class VendorEmailHistoryEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name="RECON_TYPE")
	private String reconType;

	@Column(name="VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name="VENDOR_PRIMARY_EMAIL_ID")
	private String vendrPrimryEmail;
	
	@Column(name="VENDOR_SECONDARY_EMAIL_ID")
	private String vendrSecEmail;
	
	@Column(name="RECIPIENT_EMAIL")
	private String recipientEmail;
	
	@Column(name="VENDOR_TOTAL_SECONDARY_EMAIL")
	private String vendrTotalSecEmail;
	
	@Column(name="RECIPIENT_TOTAL_EMAIL")
	private String totalRecipEmail;

	@Column(name="EMAIL_STATUS")
	private String emailStatus;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "VENDOR_PRIMARY_CONTACT_NUMBER")
	private String vendorContactNumber;
	
	@Column(name = "Counter")
	private Integer counter;
	
}
