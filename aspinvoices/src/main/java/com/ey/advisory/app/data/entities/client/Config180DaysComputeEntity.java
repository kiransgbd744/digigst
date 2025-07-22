package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */

@Entity
@Table(name = "TBL_180_DAYS_COMPUTE_CONFIG")
@Setter
@Getter
@ToString
public class Config180DaysComputeEntity {

	@Id
	@Column(name = "COMPUTE_ID")
	protected Long computeId;

	
	@Column(name = "ENTITY_ID")
	protected Long entityId;
	
	@Expose
	@Column(name = "STATUS")
	protected String status;

	@Expose
	@Column(name = "FROM_DOC_DATE")
	protected String fromDocDate;
	
	@Expose
	@Column(name = "TO_DOC_DATE")
	protected String toDocDate;
	
	@Expose
	@Column(name = "FROM_ACC_DATE")
	protected String fromAccDate;
	
	@Expose
	@Column(name = "TO_ACC_DATE")
	protected String toAccDate;
	
	@Expose
	@Column(name = "FROM_TAX_PERIOD")
	protected Integer fromTaxPeriod;
	
	@Expose
	@Column(name = "TO_TAX_PERIOD")
	protected Integer toTaxPeriod;

	@Expose
	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Expose
	@Column(name = "ERROR_DESCRIPTION")
	protected String errorDesc;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@Column(name = "IS_ACTIVE")
	protected boolean isActive;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

}
