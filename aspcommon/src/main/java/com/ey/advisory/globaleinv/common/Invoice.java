package com.ey.advisory.globaleinv.common;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice {

	@XmlElement(name = "UBLVersionID")
	public Double uBLVersionID;

	@XmlElement(name = "ID")
	public String iD;

	@XmlElement(name = "IssueDate")
	public String issueDate;

	@XmlElement(name = "InvoiceTypeCode")
	public String invoiceTypeCode;

	@XmlElement(name = "Note")
	public String note;

	@XmlElement(name = "TaxPointDate")
	public String taxPointDate;

	@XmlElement(name = "DocumentCurrencyCode")
	public String documentCurrencyCode;

	@XmlElement(name = "CreationDateTime")
	public String creationDateTime;

	@XmlElement(name = "AccountingCost")
	public String accountingCost;

	@XmlElement(name = "InvoicePeriod")
	public InvoicePeriod invoicePeriod;

	@XmlElement(name = "OrderReference")
	public OrderReference orderReference;

	@XmlElement(name = "ContractDocumentReference")
	public ContractDocumentReference contractDocumentReference;

	@XmlElement(name = "AdditionalDocumentReference")
	public List<AdditionalDocumentReference> additionalDocumentReference;

	@XmlElement(name = "AccountingSupplierParty")
	public AccountingParty accountingSupplierParty;

	@XmlElement(name = "AccountingCustomerParty")
	public AccountingParty accountingCustomerParty;

	@XmlElement(name = "PayeeParty")
	public PayeeParty payeeParty;

	@XmlElement(name = "Delivery")
	public Delivery delivery;

	@XmlElement(name = "PaymentMeans")
	public PaymentMeans paymentMeans;

	@XmlElement(name = "PaymentTerms")
	public PaymentTerms paymentTerms;

	@XmlElement(name = "AllowanceCharge")
	public List<AllowanceCharge> allowanceCharge;

	@XmlElement(name = "TaxTotal")
	public TaxTotal taxTotal;

	@XmlElement(name = "LegalMonetaryTotal")
	public LegalMonetaryTotal legalMonetaryTotal;

	@XmlElement(name = "InvoiceLine")
	public List<InvoiceLine> invoiceList;

}
