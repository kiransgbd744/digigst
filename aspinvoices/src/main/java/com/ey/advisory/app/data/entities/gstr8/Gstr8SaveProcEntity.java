package com.ey.advisory.app.data.entities.gstr8;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */

@Data
@Entity
@Table(name = "GSTR8_SAVE_SUMMARY")
public class Gstr8SaveProcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "ORG_RET_PERIOD")
	private String orgRetPeriod;

	@Column(name = "ORG_NET_SUPPLIES")
	private BigDecimal orgNetSupplies;

	@Column(name = "SGSTIN_OR_ENROL_ID")
	private String sgstinOrEnrolId;

	@Column(name = "ORG_SGSTIN_OR_ENROL_ID")
	private String orgSgstinOrEnrolId;

	@Column(name = "SUPPLIES_TO_REGISTERED")
	private BigDecimal suppliesToRegistered;

	@Column(name = "RETURNS_FROM_REGISTERED")
	private BigDecimal returnsFromRegistered;

	@Column(name = "SUPPLIES_TO_UNREGISTERED")
	private BigDecimal suppliesToUnregistered;

	@Column(name = "RETURNS_FROM_UNREGISTERED")
	private BigDecimal returnsFromUnregistered;

	@Column(name = "NET_SUPPLIES")
	private BigDecimal netSupplies;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedToGstn;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDateTime sentToGstnDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDateTime savedToGstnDate;

	@Column(name = "IS_SUBMITTED")
	private boolean isSubmitted;

	@Column(name = "SUBMITTED_DATE")
	private LocalDateTime submittedDate;

	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "ACTION_TYPE")
	private String actionType;

}
