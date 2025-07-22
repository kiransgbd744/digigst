package com.ey.advisory.app.recon.type.tagging.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Vishal.verma
 *
 */

@Entity
@Table(name = "TBL_PR_RECON_TAG_PROCEDURE")
@Data
public class Gstr2ProcessedReconTypeTaggedReportProcEntity {

	
	@Id
	@Column(name = "PROCEDURE_ID")
	private Integer id;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
}
