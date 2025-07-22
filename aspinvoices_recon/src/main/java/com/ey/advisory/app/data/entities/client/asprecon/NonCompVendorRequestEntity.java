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
@Table(name = "NON_COMPLAINT_VENDOR_REQUEST")
@Getter
@Setter
@ToString
public class NonCompVendorRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "NO_OF_VENDOR_GSTINS")
	private Long noOfVendorGstins;

	@Column(name = "FIN_YEAR")
	private String financialYear;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "FILEPATH")
	private String filePath;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

}
