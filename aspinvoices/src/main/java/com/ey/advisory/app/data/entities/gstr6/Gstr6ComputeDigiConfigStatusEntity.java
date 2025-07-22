package com.ey.advisory.app.data.entities.gstr6;

import java.io.Serializable;
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
 * @author Shashikant Shukla
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR6_COMPUTE_CONFIG")
public class Gstr6ComputeDigiConfigStatusEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COMPUTE_ID", nullable = false)
	protected Long configId;

	@Expose
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;

	@Expose
	@Column(name = "IS_ACTIVE")
	protected boolean isActive;
	
	@Expose
	@Column(name = "STATUS")
	protected String status;

	@Expose
	@Column(name = "ERROR_DESC")
	protected String errorDesc;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;


}
