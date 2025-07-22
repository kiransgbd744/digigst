package com.ey.advisory.itc.reversal.api.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An Outward Document represents a concrete Financial Document like an Invoice
 * or a credit note.
 * 
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ITCReversal180DayApiLineItemReq {
	
	@Expose
    @SerializedName("actionType")
    @XmlElement(name = "action-type")
    private String actionType;

    @Expose
    @SerializedName("customerGSTIN")
    @XmlElement(name = "customer-gstin")
    private String customerGSTIN;

    @Expose
    @SerializedName("supplierGSTIN")
    @XmlElement(name = "supplier-gstin")
    private String supplierGSTIN;

    @Expose
    @SerializedName("supplierName")
    @XmlElement(name = "supplier-name")
    private String supplierName;

    @Expose
    @SerializedName("supplierCode")
    @XmlElement(name = "supplier-code")
    private String supplierCode;

    @Expose
    @SerializedName("documentType")
    @XmlElement(name = "doc-type")
    private String documentType;

    @Expose
    @SerializedName("documentNumber")
    @XmlElement(name = "doc-number")
    private String documentNumber;

    @Expose
    @SerializedName("documentDate")
    @XmlElement(name = "doc-date")
    private String documentDate;

    @Expose
    @SerializedName("invoiceValue")
    @XmlElement(name = "invoice-value")
    private String invoiceValue;

    @Expose
    @SerializedName("fiscalYear")
    @XmlElement(name = "fiscal-year")
    private String fiscalYear;

    @Expose
    @SerializedName("statuDedApplicabl")
    @XmlElement(name = "statu-ded-appl")
    private String statutoryDeductionsApplicable;

    @Expose
    @SerializedName("payReferenceNo")
    @XmlElement(name = "pay-ref-no")
    private String paymentReferenceNumber;

    @Expose
    @SerializedName("payReferenceDate")
    @XmlElement(name = "pay-ref-date")
    private String paymentReferenceDate;

    @Expose
    @SerializedName("paymentStatus")
    @XmlElement(name = "payment-status")
    private String paymentStatus;

    @Expose
    @SerializedName("statuDedAmount")
    @XmlElement(name = "statu-ded-amt")
    private String statutoryDeductionAmount;

    @Expose
    @SerializedName("anyOthDedAmount")
    @XmlElement(name = "any-oth-ded-amt")
    private String anyOtherDeductionAmount;

    @Expose
    @SerializedName("remarksforDed")
    @XmlElement(name = "remarks-for-ded")
    private String remarksforDeductions;

    @Expose
    @SerializedName("dueDateofPayment")
    @XmlElement(name = "duedate-payment")
    private String dueDateofPayment;

    @Expose
    @SerializedName("payDescription")
    @XmlElement(name = "pay-desc")
    private String paymentDescription;

    @Expose
    @SerializedName("paidAmttoSupplier")
    @XmlElement(name = "paid-amt-sup")
    private String paidAmounttoSupplier;

    @Expose
    @SerializedName("currencyCode")
    @XmlElement(name = "currency-code")
    private String currencyCode;

    @Expose
    @SerializedName("exchangeRate")
    @XmlElement(name = "exchange-rate")
    private String exchangeRate;

    @Expose
    @SerializedName("unpaidAmttoSuplr")
    @XmlElement(name = "unpaid-amt-sup")
    private String unpaidAmounttoSupplier;

    @Expose
    @SerializedName("postingDate")
    @XmlElement(name = "posting-date")
    private String postingDate;

    @Expose
    @SerializedName("plantCode")
    @XmlElement(name = "plant-code")
    private String plantCode;

    @Expose
    @SerializedName("profitCentre")
    @XmlElement(name = "profit-centre")
    private String profitCentre;

    @Expose
    @SerializedName("division")
    @XmlElement(name = "division")
    private String division;

    @Expose
    @SerializedName("userDefinField1")
    @XmlElement(name = "usr-def-field1")
    private String userDefinedField1;

    @Expose
    @SerializedName("userDefinField2")
    @XmlElement(name = "usr-def-field2")
    private String userDefinedField2;

    @Expose
    @SerializedName("userDefinField3")
    @XmlElement(name = "usr-def-field3")
    private String userDefinedField3;}
