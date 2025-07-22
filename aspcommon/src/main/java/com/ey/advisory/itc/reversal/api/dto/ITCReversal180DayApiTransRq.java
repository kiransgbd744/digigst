package com.ey.advisory.itc.reversal.api.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Akhilesh.Yadav
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ITCReversal180DayApiTransRq { 
	@XmlTransient
	private Long id;
	/* this is just added for soap request for vendor */
	
	@XmlElement(name = "action-type")
	private String actionType = "";
	
	@XmlElement(name = "customer-gstin")
	private String customerGSTIN = "";
	
	@XmlElement(name = "supplier-gstin")
	private String supplierGSTIN = "";
	
	@XmlElement(name = "supplier-name")
	private String supplierName = "";
	
	@XmlElement(name = "supplier-code")
	private String supplierCode = "";
	
	@XmlElement(name = "doc-type")
	private String documentType = "";
	
	@XmlElement(name = "doc-number")
	private String documentNumber = "";
	
	@XmlElement(name = "doc-date")
	private String documentDate = "";
	
	@XmlElement(name = "invoice-value")
	private String invoiceValue = "";
	
	@XmlElement(name = "fiscal-year")
	private String fiscalYear = "";
	
	@XmlElement(name = "statu-ded-appl")
	private String statutoryDeductionsApplicable = "";
	
	@XmlElement(name = "pay-ref-no")
	private String paymentReferenceNumber = "";
	
	@XmlElement(name = "pay-ref-date")
	private String paymentReferenceDate = "";
	
	@XmlElement(name = "payment-status")
	private String paymentStatus = "";
	
	@XmlElement(name = "statu-ded-amt")
	private String statutoryDeductionAmount = "";
	
	@XmlElement(name = "any-oth-ded-amt")
	private String anyOtherDeductionAmount = "";
	
	@XmlElement(name = "remarks-for-ded")
	private String remarksforDeductions = "";	
	
	@XmlElement(name = "duedate-payment")
	private String dueDateofPayment = "";
	
	@XmlElement(name = "pay-desc")
	private String paymentDescription = "";
	
	@XmlElement(name = "paid-amt-sup")
	private String paidAmounttoSupplier = "";
	
	@XmlElement(name = "currency-code")
	private String currencyCode = "";
	
	@XmlElement(name = "exchange-rate")
	private String exchangeRate = "";
	
	@XmlElement(name = "unpaid-amt-sup")
	private String unpaidAmounttoSupplier = "";
	
	@XmlElement(name = "posting-date")
	private String postingDate = "";
	
	@XmlElement(name = "plant-code")
	private String plantCode = "";
	
	@XmlElement(name = "profit-centre")
	private String profitCentre = "";
	
	@XmlElement(name = "division")
	private String division = "";
	
	@XmlElement(name = "usr-def-field1")
	private String userDefinedField1 = "";
	
	@XmlElement(name = "usr-def-field2")
	private String userDefinedField2 = "";
	
	@XmlElement(name = "usr-def-field3")
	private String userDefinedField3 = "";
}