package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
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
 * 
 * @author SriBhavya
 *
 */

@Entity
@Data
@Table(name = "GSTR2X_PROCESSED_TCS_TDS")
public class Gstr2XProcessedTcsTdsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "AS_ENTERED_ID")
	protected Long asEnteredId;

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
	protected BigDecimal taxableType;

	@Column(name = "REG_SUP")
	protected BigDecimal regSup;

	@Column(name = "REG_SUPRET")
	protected BigDecimal regSupRet;

	@Column(name = "UNREG_SUP")
	protected BigDecimal unRegSup;

	@Column(name = "UNREG_SUPRET")
	protected BigDecimal unRegSupRet;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "SAVED_ACTION")
	protected String saveAction;

	@Column(name = "USER_ACTION")
	protected String userAction;

	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Column(name = "DOC_KEY")
	protected String docKey;
	
	@Column(name = "PS_KEY")
	protected String psKey;

	@Column(name = "IS_INFORMATION")
	protected boolean isInformation;

	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSavedToGstn;

	@Column(name = "GSTN_ERROR")
	protected boolean gstnError;

	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGstnDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	protected LocalDate savedToGstnDate;

	@Column(name = "IS_SUBMITTED")
	protected boolean isSubmitted;

	@Column(name = "SUBMITTED_DATE")
	protected LocalDate submittedDate;

	@Column(name = "FILE_ID")
	protected Long fileId;

	@Column(name = "IS_FILED")
	protected boolean isFiled;

	@Column(name = "FILED_DATE")
	protected LocalDate FiledDate;

	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;

	@Column(name = "GSTN_ERROR_DESCRIPTION")
	protected String gstnErrorDesc;

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

	/*@Column(name = "GET_ID")
	protected Long getId;*/
	
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
	protected BigDecimal suppliesCollected;
	
	@Column(name = "SUPPLIES_RETURNED")
	protected BigDecimal suppliesReturned;
	
	@Column(name = "NET_SUPPLIES")
	protected BigDecimal netSupplies;
	
	@Column(name = "INVOICE_VALUE")
	protected BigDecimal invoiceValue;
	
	@Column(name = "ORG_TAXABLE_VALUE")
	protected BigDecimal orgTaxableValue;
	
	@Column(name = "ORG_INVOICE_VALUE")
	protected BigDecimal orgInvoiceValue;
	
	@Column(name = "POS")
	protected String pos;
	
	@Column(name = "CHECK_SUM")
	protected String chkSum;
}
