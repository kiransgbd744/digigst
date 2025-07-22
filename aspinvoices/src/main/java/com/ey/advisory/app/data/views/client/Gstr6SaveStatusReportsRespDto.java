package com.ey.advisory.app.data.views.client;

import lombok.Data;

@Data
public class Gstr6SaveStatusReportsRespDto {
	private String category;
	private String counterPartyReturnStatus;
	private String returnPeriod;
	private String recipentGSTIN;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String originalInvoiceNo;
	private String originalInvoiceDate;
	private String cRDRPreGST;
	private String lineNumber;
	private String supplierGstin;
	private String taxableValue;
	private String taxRate;
	private String integratedTaxAmount;
	private String centralTaxAmount;
	private String stateUTTaxAmount;
	private String cessAmount;
	private String invoiceValue;
	private String reasonForCreditDebitNote;
	private String reverseChargeFlag;
	private String differentialPercentage;
	private String dlinkFlag;
	private String pos;

}
