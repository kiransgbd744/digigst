package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Entity
@Table(name = "TBL_EINV_RECON_PROCEDURE")
@Data
public class EinvReconProcedureEntity {
	
	@Id
	@Column(name = "RECON_PROCEDURE_ID")
	private Integer procId;
	
	@Column(name = "REPORT_NAME")
	private String reportName;
	
	@Column(name = "REPORT_TYPE")
	private String reportType;
	
	@Column(name = "REPORT_TYPE_ID")
	private Integer reportTypeID;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
	
	
}
