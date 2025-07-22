package com.ey.advisory.app.glrecon;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * @author ashutosh.kar
 *
 */

@Data
public class GlDumpProcessedDto {
	private String transactionType;
	private String companyCode;
	private Long fiscalYear;
	private String derivedTaxPeriod;
	private String bussinessPlace;
	private String businessArea;
	private String glAccount;
	private String glDescription;
	private String text;
	private String assignmentNumber;
	private String erpDocType;
	private String accountingVoucherNumber;
	private LocalDate accountingVoucherDate;
	private Long itemNumber;
	private String postingKey;
	private LocalDate postingDate;
	private BigDecimal amountInLocalCurrency;
	private String localCurrencyCode;
	private String clearingDocNumber;
	private LocalDate clearingDocDate;
	private String customerCode;
	private String customerName;
	private String customerGstin;
	private String supplierCode;
	private String supplierName;
	private String supplierGstin;
	private String plantCode;
	private String costCentre;
	private String profitCentre;
	private String specialGlIndicator;
	private String reference;
	private String amountinDocumentCurrency;
	private String effectiveExchangeRate;
	private String documentCurrencyCode;
	private String accountType;
	private String taxCode;
	private String withHoldingTaxAmount;
	private String withHoldingExemptAmount;
	private String withHoldingTaxBaseAmount;
	private String invoiceReference;
	private String debitCreditIndicator;
	private LocalDate paymentDate;
	private String paymentBlock;
	private String paymentReference;
	private String material;
	private String referenceKey1;
	private String offSettingAccountType;
	private String offSettingAccountNumber;
	private String documentHeaderText;
	private String billingDocNumber;
	private String billingDocDate;
	private String migoNumber;
	private LocalDate migoDate;
	private String miroNumber;
	private LocalDate miroDate;
	private String expenseGlMapping;
	private String segment;
	private String geoLevel;
	private String stateName;
	private String userId;
	private String parkedBy;
	private LocalDate entryDate;
	private String timeOfEntry;
	private String remarks;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String userDefinedField4;
	private String userDefinedField5;
	private String userDefinedField6;
	private String userDefinedField7;
	private String userDefinedField8;
	private String userDefinedField9;
	private String userDefinedField10;
}
