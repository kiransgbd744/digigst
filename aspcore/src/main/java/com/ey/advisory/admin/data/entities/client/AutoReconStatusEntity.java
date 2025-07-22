/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun.KA
 *
 */
@Getter
@Setter
@Entity
@Table(name = "AUTO_2A_2B_RECON_STATUS")
public class AutoReconStatusEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "DATE")
	private LocalDate date;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GET2A_STATUS")
	private String get2aStatus;

	@Column(name = "GET2A_INITIATED_ON")
	private LocalDateTime get2aInitaitedOn;
	
	@Column(name = "GET2A_COMP_ON")
	private LocalDateTime get2ACompletedOn;

	@Column(name = "RECON_STATUS")
	private String reconStatus;
	
	@Column(name = "RECON_INITIATED_ON")
	private LocalDateTime reconInitiatedOn;
	
	@Column(name = "RECON_COMP_ON")
	private LocalDateTime reconCompletedOn;

	@Column(name = "ERP_REPORT_PUSH_STATUS")
	private String erpReportPushStatus;

	@Column(name = "ERP_REPORT_PUSHED_ON")
	private LocalDateTime erpReportPushOn;
	
	@Column(name = "GET2B_STATUS")
	private String get2bStatus;
	
	@Column(name = "GET2B_INITIATED_ON")
	private LocalDateTime get2bInitiatedOn;
	
	@Column(name = "GET2B_COMP_ON")
	private LocalDateTime get2bCompletedOn;

	@Column(name = "GET2A_ERP_PUSH_STATUS")
	private String get2aErpPushStatus;

	@Column(name = "GET2A_ERP_PUSHED_ON")
	private LocalDateTime get2aErpPushOn;
	
	@Column(name = "GET6A_STATUS")
	private String get6aStatus;
	
	@Column(name = "GET6A_INITIATED_ON")
	private LocalDateTime get6aInitiatedOn;
	
	@Column(name = "GET6A_COMP_ON")
	private LocalDateTime get6aCompletedOn;

	@Column(name = "GET6A_ERP_PUSH_STATUS")
	private String get6aErpPushStatus;

	@Column(name = "GET6A_ERP_PUSHED_ON")
	private LocalDateTime get6aErpPushOn;
	
	@Column(name = "GET6A_REMARKS")
	private String get6aRemarks;
	
	@Column(name = "IS_DELETE")
	private Boolean isDeleted;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "GET2A_REMARKS")
	private String get2aRemarks;
	
	/*@Column(name = "IS_MONTHLY_GET")
	private boolean isMonthlyGet;*/ //true - monthly GET, false - Daily GET
	
	@Column(name = "GET_EVENT")
	private String getEvent; //D-Daily GET, M-Monthly GET, W-Weekly GET
	
	@Column(name = "NUM_OF_TAX_PERIODS")
	private Integer numOfTaxPeriods;
	
}
