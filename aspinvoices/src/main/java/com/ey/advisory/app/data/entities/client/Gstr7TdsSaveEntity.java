package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.ibm.icu.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */

@Entity
@Table(name = "GSTR7_SAVE_TDS")
@Data
public class Gstr7TdsSaveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "TDS_DEDUCTOR_GSTIN")
	private String deductorGstin;

	@Column(name = "TDS_DEDUCTEE_GSTIN")
	private String deducteeGstin;

	@Column(name = "ORG_TDS_DEDUCTEE_GSTIN")
	private String orgDeducteeGstin;

	@Column(name = "SECTION_NAME")
	private String sectionName;

	@Column(name = "NEW_GROSS_AMT")
	private BigDecimal newGrossAmt;

	@Column(name = "ORG_GROSS_AMT")
	private BigDecimal orgGrossAmt;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedToGstn;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDateTime sentToGstinDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDateTime savedToGstinDate;

	@Column(name = "IS_SUBMITTED")
	private boolean isSubmitted;

	@Column(name = "SUBMITTED_DATE")
	private LocalDateTime submittedDate;

	@Column(name = "IS_FILED")
	private boolean isFiled;

	@Column(name = "FILED_DATE")
	private LocalDateTime filedDate;

	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "TAX_DOC_TYPE")
	private String taxDocType;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "ORG_MONTH")
	private String orgMonth;

	@Column(name = "ACTION_TYPE")
	private String actionType;

}
