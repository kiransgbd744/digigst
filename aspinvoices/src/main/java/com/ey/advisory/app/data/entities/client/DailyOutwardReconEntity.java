/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Data
@Table(name = "DAILY_OUTWARD_RECON")
public class DailyOutwardReconEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "EXTRACT_ID")
	private String extractionId;

	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	private LocalDate accountingVoucherDate;

	@Column(name = "TYPE")
	private String extractionType;

	@Column(name = "EXTRACTED_COUNT")
	private Integer extractedDocCount;

	@Column(name = "STRUCTURAL_ERROR_COUNT")
	private Integer structuralErrorCount;

	@Column(name = "ONHOLD_COUNT")
	private Integer onHold;

	@Column(name = "AVAILABLE_PUSH_COUNT")
	private Integer availableForPush;

	@Column(name = "PUSHED_CLOUD_COUNT")
	private Integer pushedToCloud;

	@Column(name = "ERROR_PUSH_COUNT")
	private Integer erroredInPush;

	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "EXTRACTED_ON")
	private LocalDate extractedOn;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
