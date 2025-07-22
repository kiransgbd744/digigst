/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
@Table(name = "TBL_GETIMS_INVOICE_STAGGING_ECOM")
@Data
public class ImsInvoiceStagingECOMEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_INVOICE_HEADER_ECOM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
    private Long id;

    @Column(name = "RECIPIENT_GSTIN")
    private String recipientGstin;

    @Column(name = "SUPPLIER_GSTIN")
    private String supplierGstin;

    @Column(name = "SUPPLIER_LEGAL_NAME")
    private String supplierLegalName;

    @Column(name = "SUPPLIER_TRADE_NAME")
    private String supplierTradeName;

    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

    @Column(name = "INVOICE_TYPE")
    private String invoiceType;

    @Column(name = "INVOICE_DATE")
    private Date invoiceDate;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "IS_PENDING_ACTION_BLOCKED")
    private String isPendingActionBlocked;

    @Column(name = "FORM_TYPE")
    private String formType;

    @Column(name = "RETURN_PERIOD")
    private String returnPeriod;

    @Column(name = "DERIVED_RET_PERIOD")
    private Long derivedRetPeriod;

    @Column(name = "FILING_STATUS")
    private String filingStatus;

    @Column(name = "INVOICE_VALUE")
    private BigDecimal invoiceValue;

    @Column(name = "TAXABLE_VALUE")
    private BigDecimal taxableValue;

    @Column(name = "IGST_AMT")
    private BigDecimal igstAmt;

    @Column(name = "CGST_AMT")
    private BigDecimal cgstAmt;

    @Column(name = "SGST_AMT")
    private BigDecimal sgstAmt;

    @Column(name = "CESS_AMT")
    private BigDecimal cessAmt;

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
    
    @Column(name = "GSTN_INV_TYPE")
    private String gstnInvType;
    
    @Column(name = "LINKING_DOC_KEY")
    private String lnkingDocKey;
    
    @Column(name = "REMARKS")
    private String remarks;
}
