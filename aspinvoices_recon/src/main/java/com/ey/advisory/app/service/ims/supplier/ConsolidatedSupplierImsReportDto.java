package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class ConsolidatedSupplierImsReportDto {
	
	private String imsAction;
	private String returnPeriod;
	private String supplierGSTIN;
	private String supplierName;
	private String tableType;
	private String documentType;
	private String supplyType;
	private String recipientGSTIN;
    private String recipientName;
	private String documentNumber;
	private String documentDate;
	private String taxableValue;
	private String totalTax;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String invoiceValue;
	private String reverseCharge;
	private String Pos;
    private String source;
    private String irn;
    private String irnDate;
    private String returnType;
    private String gstr1FilingStatus;
    private String recipientGstr3BFilingStatus;
    private String estimatedGSTR3BPeriod;
    private String originalDocumentNumber;
    private String originalDocumentDate;
    private String chksum;
    private String imsRemarks;


}
