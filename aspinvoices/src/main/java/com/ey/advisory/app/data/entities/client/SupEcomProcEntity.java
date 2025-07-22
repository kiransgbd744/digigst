package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "GSTR1_SAVE_SUPECOM")
@Data
public class SupEcomProcEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "ECOM_GSTIN")
	private String ecomGstin;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "CESS")
	private BigDecimal cess;

	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "TAX_DOC_TYPE")
	private String gstnBifurcation;

	@Column(name = "TABLE_SECTION")
	private String tableSection;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
}