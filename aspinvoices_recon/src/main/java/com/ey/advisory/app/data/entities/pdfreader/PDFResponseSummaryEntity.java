package com.ey.advisory.app.data.entities.pdfreader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.ey.advisory.app.data.services.qrvspdf.PdfValidatorLineItemRespDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "PDF_RESPONSE_SUMMARY")
@EqualsAndHashCode
@ToString
public class PDFResponseSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@SerializedName("fileName")
	@Column(name = "FILE_NAME")
	private String fileName;

	@Expose
	@SerializedName("validatedDateTime")
	@Column(name = "VALIDATED_DATE_TIME")
	private LocalDateTime validatedDateTime;
	

	@Expose
	@SerializedName("irnNumber")
	@Column(name = "IRN_NUMBER")
	private String irnNumber;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	private LocalDate irnDate;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOCUMENT_TYPE")
	private String docType;

	@Expose
	@SerializedName("docNumber")
	@Column(name = "DOCUMENT_NUMBER")
	private String docNumber;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOCUMENT_DATE")
	private LocalDate docDate;

	@Expose
	@SerializedName("revChrgFlag")
	@Column(name = "REVERSE_CHARGE_FLAG")
	private String revChrgFlag;

	@Expose
	@SerializedName("suppGstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String suppGstin;

	@Expose
	@SerializedName("suppName")
	@Column(name = "SUPPLIER_NAME")
	private String suppName;

	@Expose
	@SerializedName("suppAddress")
	@Column(name = "SUPPLIER_ADDRESS")
	private String suppAddress;

	@Expose
	@SerializedName("suppPincode")
	@Column(name = "SUPPLIER_PINCODE")
	private String suppPincode;

	@Expose
	@SerializedName("suppState")
	@Column(name = "SUPPLIER_STATE")
	private String suppState;
	
	@Expose
	@SerializedName("suppStateCode")
	@Column(name = "SUPPLIER_STATE_CODE")
	private String suppStateCode;
	
	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUSTOMER_GSTIN")
	private String custGstin;
	
	@Expose
	@SerializedName("custName")
	@Column(name = "CUSTOMER_NAME")
	private String custName;
	
	@Expose
	@SerializedName("custAddress")
	@Column(name = "CUSTOMER_ADDRESS")
	private String custAddress;
	
	@Expose
	@SerializedName("custPincode")
	@Column(name = "CUSTOMER_PINCODE")
	private String custPincode;
	
	@Expose
	@SerializedName("custState")
	@Column(name = "CUSTOMER_STATE")
	private String custState;
	
	@Expose
	@SerializedName("custStateCode")
	@Column(name = "CUSTOMER_STATE_CODE")
	private String custStateCode;
	
	@Expose
	@SerializedName("billingAddress")
	@Column(name = "BILLING_ADDRESS")
	private String billingAddress;
	
	@Expose
	@SerializedName("shippingAddress")
	@Column(name = "SHIPPING_ADDRESS")
	private String shippingAddress;
	
	@Expose
	@SerializedName("billingPos")
	@Column(name = "BILLING_POS")
	private String billingPos;
	
	@Expose
	@SerializedName("invTaxableAmt")
	@Column(name = "INVOICE_TAXABLE_AMOUNT")
	private BigDecimal invTaxableAmt;
	
	@Expose
	@SerializedName("igstRate")
	@Column(name = "IGST_RATE")
	private BigDecimal igstRate;
	
	@Expose
	@SerializedName("igstAmount")
	@Column(name = "IGST_AMOUNT")
	private BigDecimal igstAmount;
	
	@Expose
	@SerializedName("cgstRate")
	@Column(name = "CGST_RATE")
	private BigDecimal cgstRate;
	
	@Expose
	@SerializedName("cgstAmount")
	@Column(name = "CGST_AMOUNT")
	private BigDecimal cgstAmount;
	
	@Expose
	@SerializedName("sgstRate")
	@Column(name = "SGST_RATE")
	private BigDecimal sgstRate;
	
	@Expose
	@SerializedName("sgstAmount")
	@Column(name = "SGST_AMOUNT")
	private BigDecimal sgstAmount;
	
	@Expose
	@SerializedName("utgstRate")
	@Column(name = "UTGST_RATE")
	private BigDecimal utgstRate;
	
	@Expose
	@SerializedName("utgstAmount")
	@Column(name = "UTGST_AMOUNT")
	private BigDecimal utgstAmount;
	
	@Expose
	@SerializedName("cessRate")
	@Column(name = "CESS_RATE")
	private BigDecimal cessRate;
	
	@Expose
	@SerializedName("cessAmount")
	@Column(name = "CESS_AMOUNT")
	private BigDecimal cessAmount;
	
	@Expose
	@SerializedName("ttlTax")
	@Column(name = "TOTAL_TAX")
	private BigDecimal ttlTax;
	
	@Expose
	@SerializedName("invValue")
	@Column(name = "INVOICE_VALUE")
	private BigDecimal invValue;
	
	@Expose
	@SerializedName("purchaseOrdNum")
	@Column(name = "PURCHASE_ORDER_NUMBER")
	private String purchaseOrdNum;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "DOC_KEY_PDF")
	private String docKeyPDF;
	
	@Transient
	List<PDFResponseLineItemEntity> pdflistItems = new ArrayList<>();

}
