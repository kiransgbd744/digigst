package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "TBL_AUTO_2APR_ERP_REQUEST")
@Data
public class AutoRecon2AERPRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long reconConfigID;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "PR_SOURCE_IDENTIFIER")
	private String sourceIdentifier;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "IS_ERP_PUSH")
	private boolean isERPPush;

	@Column(name = "ERP_PUSH_DATE")
	private LocalDateTime erpPushDate;

	@Column(name = "TOTAL_RECORD")
	private Long totalRecord;

}
