package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
@Entity
@Table(name = "TBL_2B_REPORT_INSERT")
public class Gstr2bReportRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "REPORT_DOWNLOAD_ID")
	private Long reportDownloadId;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

}
