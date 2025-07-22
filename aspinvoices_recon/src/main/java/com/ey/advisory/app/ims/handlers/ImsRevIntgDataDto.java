package com.ey.advisory.app.ims.handlers;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class ImsRevIntgDataDto {

	private String action;
    private String tableType;
    private String recipientGstin;
    private String supplierGstin;
    private String supplierLegalName;
    private String supplierTradeName;
    private String documentType;
    private String documentNumber;
    private String documentDate;
    private BigDecimal taxableValue;
    private BigDecimal igst;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal cess;
    private BigDecimal invoiceValue;
    private Integer pos;
    private String formType;
    private String gstr1FilingStatus;
    private String gstr1FilingPeriod;
    private String originalDocumentNumber;
    private String originalDocumentDate;
    private String pendingActionBlocked;
    private String checksum;
    private String getCallDateTime;
    private String activeInIms;
    private String entityName;
    private String flag;
}
