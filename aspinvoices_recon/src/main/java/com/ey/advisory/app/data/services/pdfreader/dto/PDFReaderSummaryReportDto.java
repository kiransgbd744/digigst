package com.ey.advisory.app.data.services.pdfreader.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Transient;
import lombok.Data;

@Data
public class PDFReaderSummaryReportDto {
	
	
	
	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("fileId")
	private Long fileId;

	@SerializedName("fileName")
	private String fileName;

	@Expose
	@SerializedName("validatedDateTime")
	private String validatedDateTime; // Changed to String for formatted LocalDateTime
	
	@Expose
	@SerializedName("irnNumber")
	private String irnNumber;

	@Expose
	@SerializedName("irnDate")
	private LocalDate irnDate;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docNumber")
	private String docNumber;

	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;

	@Expose
	@SerializedName("revChrgFlag")
	private String revChrgFlag;

	@Expose
	@SerializedName("suppGstin")
	private String suppGstin;

	@Expose
	@SerializedName("suppName")
	private String suppName;

	@Expose
	@SerializedName("suppAddress")
	private String suppAddress;

	@Expose
	@SerializedName("suppPincode")
	private String suppPincode;

	@Expose
	@SerializedName("suppState")
	private String suppState;
	
	@Expose
	@SerializedName("suppStateCode")
	private String suppStateCode;
	
	@Expose
	@SerializedName("custGstin")
	private String custGstin;
	
	@Expose
	@SerializedName("custName")
	private String custName;
	
	@Expose
	@SerializedName("custAddress")
	private String custAddress;
	
	@Expose
	@SerializedName("custPincode")
	private String custPincode;
	
	@Expose
	@SerializedName("custState")
	private String custState;
	
	@Expose
	@SerializedName("custStateCode")
	private String custStateCode;
	
	@Expose
	@SerializedName("billingAddress")
	private String billingAddress;
	
	@Expose
	@SerializedName("shippingAddress")
	private String shippingAddress;
	
	@Expose
	@SerializedName("billingPos")
	private String billingPos;
	
	@Expose
	@SerializedName("invTaxableAmt")
	private BigDecimal invTaxableAmt;
	
	@Expose
	@SerializedName("igstRate")
	private BigDecimal igstRate;
	
	@Expose
	@SerializedName("igstAmount")
	private BigDecimal igstAmount;
	
	@Expose
	@SerializedName("cgstRate")
	private BigDecimal cgstRate;
	
	@Expose
	@SerializedName("cgstAmount")
	private BigDecimal cgstAmount;
	
	@Expose
	@SerializedName("sgstRate")
	private BigDecimal sgstRate;
	
	@Expose
	@SerializedName("sgstAmount")
	private BigDecimal sgstAmount;
	
	@Expose
	@SerializedName("utgstRate")
	private BigDecimal utgstRate;
	
	@Expose
	@SerializedName("utgstAmount")
	private BigDecimal utgstAmount;
	
	@Expose
	@SerializedName("cessRate")
	private BigDecimal cessRate;
	
	@Expose
	@SerializedName("cessAmount")
	private BigDecimal cessAmount;
	
	@Expose
	@SerializedName("ttlTax")
	private BigDecimal ttlTax;
	
	@Expose
	@SerializedName("invValue")
	private BigDecimal invValue;
	
	@Expose
	@SerializedName("purchaseOrdNum")
	private String purchaseOrdNum;
	
	// Line Item
	
	@Expose
	@SerializedName("itemNumber")
	private Integer itemNumber;

	@Expose
	@SerializedName("description")
	private String description;

	@Expose
	@SerializedName("hsnNumber")
	private String hsnNumber;

	@Expose
	@SerializedName("quantity")
	private BigDecimal quantity;

	@Expose
	@SerializedName("unitPrice")
	private BigDecimal unitPrice;

	@Expose
	@SerializedName("taxableAmount")
	private BigDecimal taxableAmount;

	@Expose
	@SerializedName("unit")
	private String unit;

	@Expose
	@SerializedName("taxRate")
	private BigDecimal taxRate;

	@Expose
	@SerializedName("taxAmount")
	private BigDecimal taxAmount;

	@Expose
	@SerializedName("ttlamount")
	private BigDecimal ttlAmount;
	
	@Transient
	private int sINo;

}
