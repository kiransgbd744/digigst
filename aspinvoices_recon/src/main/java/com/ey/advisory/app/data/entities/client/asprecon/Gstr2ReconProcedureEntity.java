package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Entity
@Table(name = "TBL_RECON_2APR_PROCEDURE")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2ReconProcedureEntity {
	
	@Id
	@Column(name = "RECON_PROCEDURE_ID")
	private Integer procId;
	
	@Column(name = "REPORT_NAME")
	private String reportName;
	
	@Column(name = "REPORT_TYPE")
	private String reportType;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
	
	
}
