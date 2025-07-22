package com.ey.advisory.app.inward.einvoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class EinvoiceSummaryReportDto {
	
	private String month;
	private String recipientGSTIN;
	private String supplierGSTIN;
	private String docNo;
	private String docDate;
	private String docType;
	private String supplyType;
	private String taxableValue;
	private String totalTax;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String totalInvoiceValue;
	private String irnNum;
	private String irnStatus;
    private String acknowledgmentNumber;
    private String acknowledgmentDate;
    private String ewayBillNumber;
    private String ewayBillDate;
    private String cancellationDate;
    private String irpName;

}
