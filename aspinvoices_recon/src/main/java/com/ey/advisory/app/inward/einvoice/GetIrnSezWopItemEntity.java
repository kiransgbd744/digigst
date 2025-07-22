package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

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

@Data
@Entity
@Table(name = "TBL_GETIRN_SEZWOP_ITEM")
public class GetIrnSezWopItemEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_SEZWOP_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "HEADER_ID")
	private Long headerId;

	@Column(name = "ITEM_SERIAL_NUMBER")
	private String itemSerialNumber;

	@Column(name = "PRODUCT_SERIAL_NUMBER")
	private String productSerialNumber;

	@Column(name = "PRODUCT_DESCRIPTION")
	private String productDescription;

	@Column(name = "IS_SERVICE")
	private String isService;

	@Column(name = "HSN")
	private String hsn;

	@Column(name = "BARCODE")
	private String barcode;

	@Column(name = "BATCH_NAME")
	private String batchName;

	@Column(name = "BATCH_EXPIRY_DATE")
	private LocalDateTime batchExpiryDate;

	@Column(name = "WARRANTY_DATE")
	private LocalDateTime warrantyDate;

	@Column(name = "ORDER_LINE_REFERENCE")
	private String orderLineReference;

	@Column(name = "ATTRIBUTE_NAME")
	private String attributeName;

	@Column(name = "ATTRIBUTE_VALUE")
	private String attributeValue;

	@Column(name = "ORIGIN_COUNTRY")
	private String originCountry;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "QUANTITY")
	private BigDecimal quantity;

	@Column(name = "FREE_QUANTITY")
	private BigDecimal freeQuantity;

	@Column(name = "UNIT_PRICE")
	private BigDecimal unitPrice;

	@Column(name = "ITEM_AMOUNT")
	private BigDecimal itemAmount;

	@Column(name = "ITEM_DISCOUNT")
	private BigDecimal itemDiscount;

	@Column(name = "PRE_TAX_AMOUNT")
	private BigDecimal preTaxAmount;

	@Column(name = "ITEM_ASSESSABLE_AMT")
	private BigDecimal itemAssessableAmt;

	@Column(name = "IGST_RATE")
	private BigDecimal igstRate;

	@Column(name = "IGST_AMOUNT")
	private BigDecimal igstAmount;

	@Column(name = "CGST_RATE")
	private BigDecimal cgstRate;

	@Column(name = "CGST_AMOUNT")
	private BigDecimal cgstAmount;

	@Column(name = "SGST_RATE")
	private BigDecimal sgstRate;

	@Column(name = "SGST_AMOUNT")
	private BigDecimal sgstAmount;

	@Column(name = "CESS_ADVALOREM_RATE")
	private BigDecimal cessAdvaloremRate;

	@Column(name = "CESS_ADVALOREM_AMOUNT")
	private BigDecimal cessAdvaloremAmount;

	@Column(name = "CESS_SPECIFIC_AMOUNT")
	private BigDecimal cessSpecificAmount;

	@Column(name = "STATE_CESS_ADVALOREM_RATE")
	private BigDecimal stateCessAdvaloremRate;

	@Column(name = "STATE_CESS_ADVALOREM_AMOUNT")
	private BigDecimal stateCessAdvaloremAmount;

	@Column(name = "STATE_CESS_SPECIFIC_AMOUNT")
	private BigDecimal stateCessSpecificAmount;

	@Column(name = "ITEM_OTHER_CHARGES")
	private BigDecimal itemOtherCharges;

	@Column(name = "TOTAL_ITEM_AMOUNT")
	private BigDecimal totalItemAmount;

	@Column(name = "PRECEEDING_INVOICE_NUMBER")
	private String precedingInvoiceNumber;

	@Column(name = "PRECEEDING_INVOICE_DATE")
	private LocalDateTime precedingInvoiceDate;

	@Column(name = "OTHER_REFERENCE")
	private String otherReference;

	@Column(name = "RECEIPT_ADVICE_REFERENCE")
	private String receiptAdviceReference;

	@Column(name = "RECEIPT_ADVICE_DATE")
	private LocalDateTime receiptAdviceDate;

	@Column(name = "TENDER_REFERENCE")
	private String tenderReference;

	@Column(name = "CONTRACT_REFERENCE")
	private String contractReference;

	@Column(name = "EXTERNAL_REFERENCE")
	private String externalReference;

	@Column(name = "PROJECT_REFERENCE")
	private String projectReference;

	@Column(name = "CUSTOMER_PO_REFERENCE_NUMBER")
	private String customerPoReferenceNumber;

	@Column(name = "CUSTOMER_PO_REFERENCE_DATE")
	private LocalDateTime customerPoReferenceDate;

	@Column(name = "PAID_AMOUNT")
	private BigDecimal paidAmount;

	@Column(name = "BALANCE_AMOUNT")
	private BigDecimal balanceAmount;

	@Column(name = "SUPPORTING_DOC_URL")
	private String supportingDocUrl;

	@Column(name = "SUPPORTING_DOCUMENT")
	private String supportingDocument;

	@Column(name = "ADDITIONAL_INFORMATION")
	private String additionalInformation;

	@Column(name = "EXPORT_DUTY")
	private BigDecimal exportDuty;

	@Column(name = "USERDEFINEDFIELD30")
	private String userDefinedField30;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UNIT")
	private String unit;
	
	/*@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID")
	protected GetIrnSezWopHeaderEntity header;*/

}

