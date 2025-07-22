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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@Entity
@Table(name = "TBL_GL_DUMP_PSD")
public class GlDumpProcessedEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GL_DUMP_PSD_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("transactionType")
	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Expose
	@SerializedName("fiscalYear")
	@Column(name = "FISCAL_YEAR")
	private Long fiscalYear;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Expose
	@SerializedName("derivedTaxPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	private String derivedTaxPeriod;
	
	@Expose
	@SerializedName("bussinessPlace")
	@Column(name = "BUSINESS_PLACE")
	private String bussinessPlace;

	@Expose
	@SerializedName("businessArea")
	@Column(name = "BUSINESS_AREA")
	private String businessArea;

	@Expose
	@SerializedName("glAccount")
	@Column(name = "GL_ACCOUNT")
	private String glAccount;

	@Expose
	@SerializedName("glDescription")
	@Column(name = "GL_DESCRIPTION")
	private String glDescription;

	@Expose
	@SerializedName("text")
	@Column(name = "TEXT")
	private String text;

	@Expose
	@SerializedName("assignmentNumber")
	@Column(name = "ASSIGNMENT_NUMBER")
	private String assignmentNumber;

	@Expose
	@SerializedName("erpDocType")
	@Column(name = "ERP_DOC_TYPE")
	private String erpDocType;

	@Expose
	@SerializedName("accountingVoucherNumber")
	@Column(name = "ACCOUNTING_VOUCHER_NUMBER")
	private String accountingVoucherNumber;

	@Expose
	@SerializedName("accountingVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	private LocalDate accountingVoucherDate;

	@Expose
	@SerializedName("itemNumber")
	@Column(name = "ITEM_NUMBER")
	private Long itemNumber;

	@Expose
	@SerializedName("postingKey")
	@Column(name = "POSTING_KEY")
	private String postingKey;

	@Expose
	@SerializedName("postingDate")
	@Column(name = "POSTING_DATE")
	private LocalDate postingDate;

	@Expose
	@SerializedName("amountInLocalCurrency")
	@Column(name = "AMOUNT_IN_LOCAL_CURR")
	private BigDecimal amountInLocalCurrency;

	@Expose
	@SerializedName("localCurrencyCode")
	@Column(name = "LOCAL_CURRENCY_CODE")
	private String localCurrencyCode;

	@Expose
	@SerializedName("clearingDocNumber")
	@Column(name = "CLEARING_DOCUMENT_NUMBER")
	private String clearingDocNumber;

	@Expose
	@SerializedName("clearingDocDate")
	@Column(name = "CLEARING_DOCUMENT_DATE")
	private LocalDate clearingDocDate;

	@Expose
	@SerializedName("customerCode")
	@Column(name = "CUSTOMER_CODE")
	private String customerCode;

	@Expose
	@SerializedName("customerName")
	@Column(name = "CUSTOMER_NAME")
	private String customerName;

	@Expose
	@SerializedName("customerGstin")
	@Column(name = "CUSTOMER_GSTIN")
	private String customerGstin;

	@Expose
	@SerializedName("supplierCode")
	@Column(name = "SUPPLIER_CODE")
	private String supplierCode;

	@Expose
	@SerializedName("supplierName")
	@Column(name = "SUPPLIER_NAME")
	private String supplierName;

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Expose
	@SerializedName("costCentre")
	@Column(name = "COST_CENTRE")
	private String costCentre;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	private String profitCentre;

	@Expose
	@SerializedName("specialGlIndicator")
	@Column(name = "SPECIAL_GL_INDICATOR")
	private String specialGlIndicator;

	@Expose
	@SerializedName("reference")
	@Column(name = "REFERENCE")
	private String reference;

	@Expose
	@SerializedName("amountinDocumentCurrency")
	@Column(name = "AMOUNT_IN_DOCUMENT_CURRENCY")
	private String amountinDocumentCurrency;

	@Expose
	@SerializedName("effectiveExchangeRate")
	@Column(name = "EFFECTIVE_EXCHANGE_RATE")
	private String effectiveExchangeRate;

	@Expose
	@SerializedName("documentCurrencyCode")
	@Column(name = "DOCUMENT_CURRENCY_CODE")
	private String documentCurrencyCode;

	@Expose
	@SerializedName("accountType")
	@Column(name = "ACCOUNT_TYPE")
	private String accountType;

	@Expose
	@SerializedName("taxCode")
	@Column(name = "TAX_CODE")
	private String taxCode;

	@Expose
	@SerializedName("withHoldingTaxAmount")
	@Column(name = "WITH_HOLDING_TAX_AMOUNT")
	private String withHoldingTaxAmount;

	@Expose
	@SerializedName("withHoldingExemptAmount")
	@Column(name = "WITH_HOLDING_EXEMPT_AMOUNT")
	private String withHoldingExemptAmount;

	@Expose
	@SerializedName("withHoldingTaxBaseAmount")
	@Column(name = "WITH_HOLDING_TAXBASE_AMOUNT")
	private String withHoldingTaxBaseAmount;

	@Expose
	@SerializedName("invoiceReference")
	@Column(name = "INVOICE_REFERENCE")
	private String invoiceReference;

	@Expose
	@SerializedName("debitCreditIndicator")
	@Column(name = "DEBIT_CREDIT_INDICATOR")
	private String debitCreditIndicator;

	@Expose
	@SerializedName("paymentDate")
	@Column(name = "PAYMENT_DATE")
	private LocalDate paymentDate;

	@Expose
	@SerializedName("paymentBlock")
	@Column(name = "PAYMENT_BLOCK")
	private String paymentBlock;

	@Expose
	@SerializedName("paymentReference")
	@Column(name = "PAYMENT_REFERENCE")
	private String paymentReference;

	@Expose
	@SerializedName("termsOfPayment")
	@Column(name = "TERMS_OF_PAYMENT")
	private String termsOfPayment;

	@Expose
	@SerializedName("material")
	@Column(name = "MATERIAL")
	private String material;

	@Expose
	@SerializedName("referenceKey1")
	@Column(name = "REFERENCE_KEY1")
	private String referenceKey1;

	@Expose
	@SerializedName("offSettingAccountType")
	@Column(name = "OFFSETTING_ACCOUNT_TYPE")
	private String offSettingAccountType;

	@Expose
	@SerializedName("offSettingAccountNumber")
	@Column(name = "OFFSETTING_ACCOUNT_NUMBER")
	private String offSettingAccountNumber;

	@Expose
	@SerializedName("documentHeaderText")
	@Column(name = "DOCUMENT_HEADER_TEXT")
	private String documentHeaderText;

	@Expose
	@SerializedName("billingDocNumber")
	@Column(name = "BILLING_DOCUMENT_NUMBER")
	private String billingDocNumber;

	@Expose
	@SerializedName("billingDocDate")
	@Column(name = "BILLING_DOCUMENT_DATE")
	private String billingDocDate;

	@Expose
	@SerializedName("migoNumber")
	@Column(name = "MIGO_NUMBER")
	private String migoNumber;

	@Expose
	@SerializedName("migoDate")
	@Column(name = "MIGO_DATE")
	private LocalDate migoDate;

	@Expose
	@SerializedName("miroNumber")
	@Column(name = "MIRO_NUMBER")
	private String miroNumber;

	@Expose
	@SerializedName("miroDate")
	@Column(name = "MIRO_DATE")
	private LocalDate miroDate;

	@Expose
	@SerializedName("expenseGlMapping")
	@Column(name = "EXPENSE_GL_MAPPING")
	private String expenseGlMapping;

	@Expose
	@SerializedName("segment")
	@Column(name = "SEGMENT")
	private String segment;

	@Expose
	@SerializedName("geoLevel")
	@Column(name = "GEO_LEVEL")
	private String geoLevel;

	@Expose
	@SerializedName("stateName")
	@Column(name = "STATE_NAME")
	private String stateName;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("parkedBy")
	@Column(name = "PARKED_BY")
	private String parkedBy;

	@Expose
	@SerializedName("entryDate")
	@Column(name = "ENTRY_DATE")
	protected LocalDate entryDate;

	@Expose
	@SerializedName("timeOfEntry")
	@Column(name = "TIME_OF_ENTRY")
	private String timeOfEntry;

	@Expose
	@SerializedName("remarks")
	@Column(name = "REMARKS")
	private String remarks;

	@Expose
	@SerializedName("userDefinedField1")
	@Column(name = "USER_DEFINED_FIELD1")
	private String userDefinedField1;

	@Expose
	@SerializedName("userDefinedField2")
	@Column(name = "USER_DEFINED_FIELD2")
	private String userDefinedField2;

	@Expose
	@SerializedName("userDefinedField3")
	@Column(name = "USER_DEFINED_FIELD3")
	private String userDefinedField3;

	@Expose
	@SerializedName("userDefinedField4")
	@Column(name = "USER_DEFINED_FIELD4")
	private String userDefinedField4;

	@Expose
	@SerializedName("userDefinedField5")
	@Column(name = "USER_DEFINED_FIELD5")
	private String userDefinedField5;

	@Expose
	@SerializedName("userDefinedField6")
	@Column(name = "USER_DEFINED_FIELD6")
	private String userDefinedField6;

	@Expose
	@SerializedName("userDefinedField7")
	@Column(name = "USER_DEFINED_FIELD7")
	private String userDefinedField7;

	@Expose
	@SerializedName("userDefinedField8")
	@Column(name = "USER_DEFINED_FIELD8")
	private String userDefinedField8;

	@Expose
	@SerializedName("userDefinedField9")
	@Column(name = "USER_DEFINED_FIELD9")
	private String userDefinedField9;

	@Expose
	@SerializedName("userDefinedField10")
	@Column(name = "USER_DEFINED_FIELD10")
	private String userDefinedField10;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	private String docKey; 

	@Expose
	@SerializedName("DataOriginType")
	@Column(name = "DATA_ORIGIN_TYPE")
	private String DataOriginType;
}
