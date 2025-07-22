package com.ey.advisory.app.recon.type.tagging.report;

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
 * @author Vishal.verma
 *
 */

@Entity
@Table(name = "TBL_PR_RECON_TAG_GSTIN_DETAILS")
@Data
public class Gstr2ProcessedReconTypeTaggedReportGstinDetailsEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "REPORT_DOWNLOAD_ID")
	private Long reportDwndId;
	
	@Column(name = "RECON_TYPE")
	private String reconType;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "TAXPERIOD")
	private String taxPeriod;
	
	@Column(name = "RECORD_COUNT")
	private Integer recordCount;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
}
