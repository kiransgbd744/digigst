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
@Table(name = "NON_COMPLAINT_VENDOR_VGSTIN")
@Getter
@Setter
@ToString
public class NonCompVendorVGstinEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "EMAIL_STATUS")
	private String emailStatus;

	@Column(name = "REPORT_STATUS")
	private String reportStatus;

	@Column(name = "FILEPATH")
	private String filePath;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

}
