package com.ey.advisory.app.data.entities.client.asprecon;

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
import lombok.ToString;

@Entity
@Table(name = "TBL_AUTO_2APR_RECON_REQUEST")
@Getter
@Setter
@ToString
public class AutoReconRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "NO_OF_RECIPIENT_GSTINS")
	private Long noOfRecipientGstins;

	@Column(name = "NO_OF_REPORT_TYPES")
	private Long noOfReportTypes;

	@Column(name = "REPORT_TYPES")
	private String reportTypes;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "FROM_TAX_PERIOD")
	private int fromTaxPeriod;

	@Column(name = "TO_TAX_PERIOD")
	private int toTaxPeriod;

	@Column(name = "RECON_FROM_DATE")
	private LocalDate reconFromDate;

	@Column(name = "RECON_TO_DATE")
	private LocalDate reconToDate;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "FILEPATH")
	private String filePath;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "DOC_ID")
	private String docId;
}
