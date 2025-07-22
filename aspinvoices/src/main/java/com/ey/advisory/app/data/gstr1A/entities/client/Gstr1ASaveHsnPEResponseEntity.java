package com.ey.advisory.app.data.gstr1A.entities.client;

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
@Table(name = "GSTR1A_SAVE_HSN_PE_RESPONSE")
@Data
public class Gstr1ASaveHsnPEResponseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;
	
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "GSTN_ERROR_CODE")
	private String gstnErrorCode;

	@Column(name = "GSTN_ERROR_DESC")
	private String gstnErrorDesc;
	
	@Column(name = "SERIAL_NUMBER")
	private Integer serialNumber;

	@Column(name = "ITM_HSNSAC")
	private String itmhsnsac;

	@Column(name = "ITM_DESCRIPTION")
	private String itmdesc;

	@Column(name = "ITM_UQC")
	private String itmuqc;
	
	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Column(name = "ITM_QTY")
	private BigDecimal itmqty;
	
	@Column(name = "TOTAL_VALUE")
	private BigDecimal totalvalue;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableval;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;

	@Column(name = "SGST")
	private BigDecimal sgst;

	@Column(name = "CESS")
	private BigDecimal cess;

	@Column(name = "RECORD_TYPE")
	private String recordType;
	
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
