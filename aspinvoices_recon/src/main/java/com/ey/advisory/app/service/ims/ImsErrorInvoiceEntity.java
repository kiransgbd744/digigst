/**
 * 
 */
package com.ey.advisory.app.service.ims;

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
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_GETIMS_ERROR")
@Data
public class ImsErrorInvoiceEntity {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)

	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "RECIPIENT_GSTIN")
	private String recipientGstin;

	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;

	@Column(name = "INVOICE_NUMBER")
	private String invoiceNumber;

	@Column(name = "INVOICE_TYPE")
	private String invoiceType;

	@Column(name = "INVOICE_DATE")
	private String invoiceDate;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private String derivedRetPeriod;

	@Column(name = "ACTION_RESPONSE")
	private String actionResponse;

	@Column(name = "RESPONSE_REMARKS")
	private String responseRemarks;

	@Column(name = "IS_PENDING_ACTION_BLOCKED")
	private String isPendingActionBlocked;

	@Column(name = "FORM_TYPE")
	private String formType;

	@Column(name = "GSTR1_FILING_STATUS")
	private String filingStatus;

	@Column(name = "INVOICE_VALUE")
	private String invoiceValue;

	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Column(name = "IGST_AMT")
	private String igstAmt;

	@Column(name = "CGST_AMT")
	private String cgstAmt;

	@Column(name = "SGST_AMT")
	private String sgstAmt;

	@Column(name = "CESS_AMT")
	private String cessAmt;

	@Column(name = "POS")
	private String pos;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;

	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "ACTION_GSTN")
	private String actionGstn;

	@Column(name = "ACTION_DIGIGST_DATE_TIME")
	private String digiActionDateTime;
	
	@Column(name = "SUPPLIER_LEGAL_NAME")
	private String supplierLegalName;

	@Column(name = "SUPPLIER_TRADE_NAME")
	private String supplierTradeName;
	
	@Column(name = "TOTAL_TAX")
	private String totalTax;
	
	@Column(name = "ORG_DOC_NUM")
	private String orgDocNum;
	
	@Column(name = "GETCALL_DATE_TIME")
	private String getCallDateTime;
	
	@Column(name = "ORG_DOC_DATE")
	private String orgDocDate;
	
	@Column(name = "TABLE_TYPE")
	private String tableType;
	
	@Column(name = "ACTION_DIGI")
	private String actionDigi;
	
	@Column(name = "AVAILABLE_IN_IMS")
	private String availableInIms;
	
	//V1.1
		@Column(name = "ITC_RED_REQ")
		private String itcRedReq;
		
		@Column(name = "DECLARED_IGST")
		private String declIgst;
		
		@Column(name = "DECLARED_SGST")
		private String declSgst;
		
		@Column(name = "DECLARED_CGST")
		private String declCgst;
		
		@Column(name = "DECLARED_CESS")
		private String declCess;

}
