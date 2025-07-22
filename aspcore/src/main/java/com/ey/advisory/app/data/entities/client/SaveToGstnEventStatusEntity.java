package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
@Entity
@Table(name = "EVENT_SAVE_REQUEST_STATUS")
public class SaveToGstnEventStatusEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "EVENT_SAVE_REQUEST_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedTaxperiod;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "SECTION")
	private String section;

	@Column(name = "STATUS_CODE")
	private Integer statusCode;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	
}
