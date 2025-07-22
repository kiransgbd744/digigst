package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImsDataDto {

	 	private String action;
	    private String tableType;
	    private String recipientGstin;
	    private String supplierGstin;
	    private String documentType;
	    private String documentNumber;
	    private LocalDate documentDate;
	    private BigDecimal taxableValue;
	    private BigDecimal igst;
	    private BigDecimal cgst;
	    private BigDecimal sgst;
	    private BigDecimal cess;
	    private BigDecimal totalTax;
	    private BigDecimal invoiceValue;
	    private String pos;
	    private String formType;
	    private String gstr1FilingStatus;
	    private String gstr1FilingPeriod;
	    private String originalDocumentNumber;
	    private LocalDate originalDocumentDate;
	    private String pendingActionBlocked;
	    private String checksum;


}
