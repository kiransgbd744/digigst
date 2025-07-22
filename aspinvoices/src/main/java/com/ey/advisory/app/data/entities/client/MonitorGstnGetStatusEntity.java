package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Saif.S
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MONITOR_GSTN_GET_STATUS")
public class MonitorGstnGetStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MONITOR_ID")
	private Long monitorId;

	@Column(name = "INVOCATION_ID")
	private Long invocationId;

	@Column(name = "IS_BATCH_UPDATED")
	private boolean isBatchUpdated;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAXPERIOD")
	private String taxPeriod;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "SECTION")
	private String section;
	
	@Column(name = "IS_AUTO_REQUEST")
	private boolean isAutoRequest;

}
