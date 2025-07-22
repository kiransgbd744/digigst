package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
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
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GSTR6_CROSS_ITC_SAVE_COMPUTE")
@Setter
@Getter
public class Gstr6CrossItcSaveComputeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "IS_DIGI_GST_COMPUTE")
	private boolean isDigiGstCompute;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedToGstn;
	
	@Column(name = "IS_GSTN_ERROR")
	protected boolean isGstnError;

	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;
	
	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "IAMTI")
	protected BigDecimal iamti;

	@Column(name = "IAMTS")
	protected BigDecimal iamts;

	@Column(name = "IAMTC")
	protected BigDecimal iamtc;

	@Column(name = "SAMTS")
	protected BigDecimal samts;

	@Column(name = "SAMTI")
	protected BigDecimal samti;

	@Column(name = "CAMTC")
	protected BigDecimal camtc;

	@Column(name = "CAMTI")
	protected BigDecimal camti;

	@Column(name = "CESS")
	protected BigDecimal cess;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
