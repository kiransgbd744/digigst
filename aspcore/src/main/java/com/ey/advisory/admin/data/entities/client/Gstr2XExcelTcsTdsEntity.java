package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Data
@Table(name = "GSTR2X_AS_ENTERED_TCS_TDS")
public class Gstr2XExcelTcsTdsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "CTIN")
	protected String ctin;

	@Column(name = "RET_PERIOD")
	protected String retPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	@Column(name = "RECORD_TYPE")
	protected String recordType;

	@Column(name = "DEDUCTOR_UPL_MONTH")
	protected String deductorUplMonth;

	@Column(name = "ORG_DEDUCTOR_UPL_MONTH")
	protected String orgDeductorUplMonth;

	@Column(name = "TAXABLE_VALUE")
	protected String taxableType;

	@Column(name = "REG_SUP")
	protected String regSup;

	@Column(name = "REG_SUPRET")
	protected String regSupRet;

	@Column(name = "UNREG_SUP")
	protected String unRegSup;

	@Column(name = "UNREG_SUPRET")
	protected String unRegSupRet;

	@Column(name = "IGST_AMT")
	protected String igstAmt;

	@Column(name = "CGST_AMT")
	protected String cgstAmt;

	@Column(name = "SGST_AMT")
	protected String sgstAmt;

	@Column(name = "SAVED_ACTION")
	protected String saveAction;

	@Column(name = "USER_ACTION")
	protected String userAction;

	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "IS_INFORMATION")
	protected boolean isInformation;

	@Column(name = "IS_ERROR")
	protected boolean isError;

	@Column(name = "FILE_ID")
	protected Long fileId;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifieddBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "PS_KEY")
	protected String psKey;
	
	@Column(name = "DIGIGST_REMARKS")
	protected String digiGstRemarks;
	
	@Column(name = "DIGIGST_COMMENT")
	protected String digiGstComment;
	
	@Column(name = "GSTN_REMARKS")
	protected String GstnRemarks;
	
	@Column(name = "GSTN_COMMENT")
	protected String gstnComment;
	
	@Column(name = "DEDUCTOR_NAME")
	protected String deductorName;
	
	@Column(name = "DOC_NO")
	protected String docNo;
	
	@Column(name = "DOC_DATE")
	protected String docDate;
	
	@Column(name = "ORG_DOC_NO")
	protected String orgDocNo;
	
	@Column(name = "ORG_DOC_DATE")
	protected String orgDocDate;
	
	@Column(name = "SUPPLIES_COLLECTED")
	protected String suppliesCollected;
	
	@Column(name = "SUPPLIES_RETURNED")
	protected String suppliesReturned;
	
	@Column(name = "NET_SUPPLIES")
	protected String netSupplies;
	
	@Column(name = "INVOICE_VALUE")
	protected String invoiceValue;
	
	@Column(name = "ORG_TAXABLE_VALUE")
	protected String orgTaxableValue;
	
	@Column(name = "ORG_INVOICE_VALUE")
	protected String orgInvoiceValue;
	
	@Column(name = "POS")
	protected String pos;
	
	@Column(name = "CHECK_SUM")
	protected String chkSum;
}
