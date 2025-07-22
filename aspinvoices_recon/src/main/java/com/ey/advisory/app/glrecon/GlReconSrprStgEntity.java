/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.glrecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TBL_GL_RECON_SRPR_STG")
public class GlReconSrprStgEntity {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "sequence", sequenceName = "TBL_GL_RECON_SRPR_STG_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "RECON_CONFIG_ID")
    private Long reconConfigId;

    @Column(name = "G_L_GL")
    private String glGl;

    @Column(name = "REFERENCE_GL")
    private String referenceGl;

    @Column(name = "ACCOUNTING_DOCUMENT_NUMBER_GL")
    private String accountingDocumentNumberGl;

    @Column(name = "DOCUMENT_TYPE_GL")
    private String documentTypeGl;

    @Column(name = "DOCUMENT_DATE_GL")
    private String documentDateGl;

    @Column(name = "AMOUNT_GL")
    private String amountGl;

    @Column(name = "TEXT_GL")
    private String textGl;

    @Column(name = "CLEARING_DOCUMENT_NUMBER_GL")
    private String clearingDocumentNumberGl;

    @Column(name = "PLANT_GL")
    private String plantGl;

    @Column(name = "POSTING_DATE_GL")
    private String postingDateGl;

    @Column(name = "COMPANY_CODE_GL")
    private String companyCodeGl;

    @Column(name = "CUSTOMER_CODE_GL")
    private String customerCodeGl;

    @Column(name = "VENDOR_CODE_GL")
    private String vendorCodeGl;

    @Column(name = "ENTRY_DATE_GL")
    private String entryDateGl;

    @Column(name = "BP_GL")
    private String bpGl;

    @Column(name = "OFFSET_ACCOUNT_GL")
    private String offsetAccountGl;

    @Column(name = "TRANSACTION_TYPE_GL")
    private String transactionTypeGl;

    @Column(name = "PERIOD_GL")
    private String periodGl;

    @Column(name = "YEAR_MONTH_GL")
    private String yearMonthGl;

    @Column(name = "NATURE_GL")
    private String natureGl;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "GL_TYPE")
    private String glType;

    @Column(name = "TAX_CODE_DESCRIPTION_MS")
    private String taxCodeDescriptionMs;

    @Column(name = "TAX_TYPE_MS")
    private String taxTypeMs;

    @Column(name = "ELIGIBILITY_MS")
    private String eligibilityMs;

    @Column(name = "TAX_RATE_MS")
    private String taxRateMs;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "DOCUMENT_TYPE_MS")
    private String documentTypeMs;

    @Column(name = "PERIOD_X")
    private String periodX;

    @Column(name = "GL_KEY")
    private String glKey;

    @Column(name = "ADVANCE_GL")
    private String advanceGl;

    @Column(name = "REVENUE_GL")
    private String revenueGl;

    @Column(name = "TAX_GL")
    private String taxGl;

    @Column(name = "CGST_GL")
    private String cgstGl;

    @Column(name = "GL_CODE_MISSING_IN_MASTER")
    private String glCodeMissingInMaster;

    @Column(name = "IGST_GL")
    private String igstGl;

    @Column(name = "REVENUE")
    private String revenue;

    @Column(name = "SGST_GL")
    private String sgstGl;

    @Column(name = "CATEGORY_X")
    private String categoryX;

    @Column(name = "ACCOUNTING_DOCUMENT_NUMBER_REG")
    private String accountingDocumentNumberReg;

    @Column(name = "GSTR_9C")
    private String gstr9c;

    @Column(name = "SOURCEIDENTIFIER")
    private String sourceIdentifier;

    @Column(name = "SOURCEFILENAME")
    private String sourceFileName;

    @Column(name = "GLACCOUNTCODE")
    private String glAccountCode;

    @Column(name = "DIVISION")
    private String division;

    @Column(name = "SUBDIVISION")
    private String subdivision;

    @Column(name = "PROFITCENTRE1")
    private String profitCentre1;

    @Column(name = "PROFITCENTRE2")
    private String profitCentre2;

    @Column(name = "PLANTCODE")
    private String plantCode;

    @Column(name = "RETURN_PERIOD")
    private String returnPeriod;

    @Column(name = "SUPPLIER_GSTIN")
    private String supplierGstin;

    @Column(name = "DOCUMENTTYPE")
    private String documenttype;

    @Column(name = "SUPPLY_TYPE")
    private String supplyType;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "DOCUMENT_DATE")
    private String documentDate;

    @Column(name = "ORIGINAL_DOCUMENT_NUMBER")
    private String originalDocumentNumber;

    @Column(name = "ORIGINAL_DOCUMENT_DATE")
    private String originalDocumentDate;

    @Column(name = "CRDRPREGST")
    private String crdrpreGst;

    @Column(name = "LINE_NUMBER")
    private String lineNumber;

    @Column(name = "CUSTOMER_GSTIN")
    private String customerGstin;

    @Column(name = "UINOR_COMPOSITION")
    private String uinOrComposition;

    @Column(name = "ORIGINAL_CUSTOMER_GSTIN")
    private String originalCustomerGstin;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "CUSTOMER_CODE")
    private String customerCode;

    @Column(name = "BILL_TO_STATE")
    private String billToState;

    @Column(name = "SHIP_TO_STATE")
    private String shipToState;

    @Column(name = "POS")
    private String pos;

    @Column(name = "PORT_CODE")
    private String portCode;

    @Column(name = "SHIPPING_BILL_NUMBER")
    private String shippingBillNumber;

    @Column(name = "SHIPPING_BILL_DATE")
    private String shippingBillDate;

    @Column(name = "FOB")
    private String fob;

    @Column(name = "EXPORT_DUTY")
    private String exportDuty;

    @Column(name = "HSNORSAC")
    private String hsnOrSac;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_DESCRIPTION")
    private String productDescription;

    @Column(name = "CATEGORY_OF_PRODUCT")
    private String categoryOfProduct;

    @Column(name = "UNIT_OF_MEASUREMENT")
    private String unitOfMeasurement;

    @Column(name = "INTEGRATED_TAX_RATE")
    private String integratedTaxRate;

    @Column(name = "CENTRAL_TAXRATE")
    private String centralTaxRate;

    @Column(name = "STATE_UT_TAXRATE")
    private String stateUtTaxRate;

    @Column(name = "CESS_RATE_ADVALOREM")
    private String cessRateAdvalorem;

    @Column(name = "CESS_AMOUNT_ADVALOREM")
    private String cessAmountAdvalorem;

    @Column(name = "CESS_RATE_SPECIFIC")
    private String cessRateSpecific;

    @Column(name = "INVOICE_VALUE")
    private String invoiceValue;

    @Column(name = "REVERSE_CHARGE_FLAG")
    private String reverseChargeFlag;

    @Column(name = "TCS_FLAG")
    private String tcsFlag;

    @Column(name = "ECOM_GSTIN")
    private String ecomGstin;

    @Column(name = "ITC_FLAG")
    private String itcFlag;

    @Column(name = "REASON_FOR_CREDIT_DEBIT_NOTE")
    private String reasonForCreditDebitNote;

    @Column(name = "ACCOUNTING_VOUCHER_NUMBER")
    private String accountingVoucherNumber;

    @Column(name = "ACCOUNTING_VOUCHER_DATE")
    private String accountingVoucherDate;

    @Column(name = "USERDEFINEDFIELD1")
    private String userDefinedField1;

    @Column(name = "USERDEFINEDFIELD2")
    private String userDefinedField2;

    @Column(name = "USERDEFINEDFIELD3")
    private String userDefinedField3;

    @Column(name = "TAX_RATE_REG")
    private String taxRateReg;

    @Column(name = "RATE_CHECK")
    private String rateCheck;

    @Column(name = "POS_CHECK")
    private String posCheck;

    @Column(name = "PERIOD_Y")
    private String periodY;

    @Column(name = "FILING_PERIOD")
    private String filingPeriod;

    @Column(name = "GSTIN_VALID")
    private String gstinValid;

    @Column(name = "CREDIT_NOTE")
    private String creditNote;

    @Column(name = "EXPORT_DAYS_CONDITION")
    private String exportDaysCondition;

    @Column(name = "HSN")
    private String hsn;

    @Column(name = "GSTR_9")
    private String gstr9;

    @Column(name = "CATEGORY_Y")
    private String categoryY;

    @Column(name = "SUB_CATEGORY")
    private String subCategory;

    @Column(name = "CENTRAL_TAX_AMOUNT")
    private String centralTaxAmount;

    @Column(name = "CESS_AMOUNT_SPECIFIC")
    private String cessAmountSpecific;

    @Column(name = "INTEGRATED_TAX_AMOUNT")
    private String integratedTaxAmount;

    @Column(name = "QUANTITY")
    private String quantity;

    @Column(name = "STATE_UT_TAX_AMOUNT")
    private String stateUtTaxAmount;

    @Column(name = "TAX_AMOUNT_REG")
    private String taxAmountReg;

    @Column(name = "TAXABLE_VALUE")
    private String taxableValue;

    @Column(name = "SR_VS_GL")
    private String srVsGl;

    @Column(name = "MISMATCH_REASON")
    private String mismatchReason;

    @Column(name = "DIFFERENCE_IN_TAXABLE_AMOUNT")
    private String differenceInTaxableAmount;

    @Column(name = "DIFFERENCE_IN_IGST_AMOUNT")
    private String differenceInIgstAmount;

    @Column(name = "DIFFERENCE_IN_CGST_AMOUNT")
    private String differenceInCgstAmount;

    @Column(name = "DIFFERENCE_IN_SGST_AMOUNT")
    private String differenceInSgstAmount;

    @Column(name = "RECON_KEY")
    private String reconKey;

    @Column(name = "IS_DELETE")
    private Boolean isDelete;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

}
