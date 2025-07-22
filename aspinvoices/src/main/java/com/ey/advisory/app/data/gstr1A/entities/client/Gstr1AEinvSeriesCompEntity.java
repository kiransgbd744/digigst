/**
 * 
 */
package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "GSTR1A_EINV_SERIES_COMPUTE_INFO")
@Data
public class Gstr1AEinvSeriesCompEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "REQUEST_STATUS")
	private String requestStatus;

	@Expose
	@Column(name = "ENTITY_ID")
	protected Long entityId;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Expose
	@Column(name = "START_TIME")
	private LocalDateTime startTime;

	@Expose
	@Column(name = "END_TIME")
	private LocalDateTime endTime;

}
