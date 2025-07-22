package com.ey.advisory.app.data.entities.client;

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
 * @author SriBhavya
 *
 */

@Entity
@Table(name = "PROCEED_FILE")
@Data
public class ProceedFileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "REFERENCE_ID")
	private String referenceId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
}
