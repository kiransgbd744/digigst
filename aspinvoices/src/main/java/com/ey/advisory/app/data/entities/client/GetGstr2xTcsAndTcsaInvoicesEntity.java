package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Entity
@Table(name = "GETGSTR2X_TCS_TCSA")
@Data
public class GetGstr2xTcsAndTcsaInvoicesEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2X_TCS_TCSA_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String sgstin;

	@Column(name = "CTIN")
	protected String cgstin;

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "RET_PERIOD")
	protected String retPeriod;

	@Column(name = "FROM_TIME")
	protected LocalDate fromTime;

	@Column(name = "RECORD_TYPE")
	protected String recordType;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableType;

	@Column(name = "REG_SUP")
	protected BigDecimal regSupplier;

	@Column(name = "REG_SUPRET")
	protected BigDecimal regRetSupplier;

	@Column(name = "UNREG_SUP")
	protected BigDecimal unRegSupplier;

	@Column(name = "UNREG_SUPRET")
	protected BigDecimal unRegRetSupplier;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "SAVED_ACTION")
	protected String saveAction;

	@Column(name = "DEDUCTOR_UPL_MONTH")
	protected String deductorUploadedMonth;

	@Column(name = "ORG_DEDUCTOR_UPL_MONTH")
	protected String orgDeductorUploadedMonth;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

	@Column(name = "BATCH_ID")
	protected Long tcsAndTcsaBatchIdGstr2x;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "PS_KEY")
	protected String psKey;

	@Column(name = "USER_ACTION_FLAG")
	protected String userActionFlag;
	
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
}
