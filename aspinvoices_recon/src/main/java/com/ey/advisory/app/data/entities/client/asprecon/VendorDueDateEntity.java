package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "VENDOR_DUE_DATE_MASTER")
@Data
public class VendorDueDateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "DUE_DATE")
	private LocalDate dueDate;
	
	@Column(name = "RETURN_FILING_FREQUENCY")
	private String returnFilingFrequency;

	@Column(name = "VENDOR_STATE_CODE")
	private Integer vendorStateCode;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "KEY")
	private String key;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

}
