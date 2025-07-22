package com.ey.advisory.app.gstr1.einv;

import lombok.Data;

@Data
public class Gstr6aReportDto{
	
	private String returnPeriod;
	private String recipentGSTIN;
	private String documentType;
	private String transactionType;
	private String dlinkFlag;
	private String documentNumber;
	private String documentDate;
	private String lineNumber;
	private String supplierGstin;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String originalInvoiceNo;
	private String originalInvoiceDate;
	private String crdrPreGST;
	private String pos;
	private String taxableValue;
	private String taxRate;
	private String integratedTaxAmount;
	private String centralTaxAmount;
	private String stateUTTaxAmount;
	private String cessAmount;
	private String invoiceValue;
	private String differentialPercentage;
	private String reasonForCreditDebitNote;
	private String reverseCharge;
	private String sourceTypeIRN;
	private String irnNumber;
	private String irnGenerationDate;
	private String cFp;
	private String counterPartyReturnStatus;
	
	// Legal Name And Trade Name 
			private String legalName;
			private String tradeName;

}
