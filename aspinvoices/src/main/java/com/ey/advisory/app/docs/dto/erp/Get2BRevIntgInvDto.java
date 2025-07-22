package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Get2BRevIntgInvDto {

	@XmlElement(name = "RETURN_PERIOD")
	private String returnPeriod;

	@XmlElement(name = "RECIPIENT_GSTIN")
	private String recipientGSTIN;

	@XmlElement(name = "SUPPLIER_GSTIN")
	private String supplierGSTIN;

	@XmlElement(name = "SUPPLIER_NAME")
	private String supplierName;

	@XmlElement(name = "DOCUMENT_TYPE")
	private String documentType;

	@XmlElement(name = "SUPPLY_TYPE")
	private String supplyType;

	@XmlElement(name = "DOCUMENT_NUMBER")
	private String documentNumber;

	@XmlElement(name = "DOCUMENT_DATE")
	private String documentDate;

	@XmlElement(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue = BigDecimal.ZERO;

	@XmlElement(name = "TAX_RATE")
	private BigDecimal taxRate = BigDecimal.ZERO;

	@XmlElement(name = "IGST_AMOUNT")
	private BigDecimal igstAmount = BigDecimal.ZERO;

	@XmlElement(name = "CGST_AMOUNT")
	private BigDecimal cgstAmount = BigDecimal.ZERO;

	@XmlElement(name = "SGST_AMOUNT")
	private BigDecimal sgstAmount = BigDecimal.ZERO;

	@XmlElement(name = "CESS_AMOUNT")
	private BigDecimal cessAmount = BigDecimal.ZERO;

	@XmlElement(name = "INVOICE_VALUE")
	private BigDecimal invoiceValue = BigDecimal.ZERO;

	@XmlElement(name = "POS")
	private String pos;

	@XmlElement(name = "STATE_NAME")
	private String stateName;

	@XmlElement(name = "LINE_NUMBER")
	private String lineNumber;

	@XmlElement(name = "BOE_REFERENCE_DATE")
	private String boeRefDateIceGate;

	@XmlElement(name = "BOE_RECEIVED_DATE")
	private String boeRecvDateGstin;

	@XmlElement(name = "PORTCODE")
	private String portCode;

	@XmlElement(name = "BOE_NUMBER")
	private String billOfEntryNumber;

	@XmlElement(name = "BOE_DATE")
	private String billOfEntryDate;

	@XmlElement(name = "BOE_AMEND_FLAG")
	private String boeAmendmentFlag;

	@XmlElement(name = "ORG_DOC_NUMBER")
	private String originalDocumentNumber;

	@XmlElement(name = "ORG_DOC_DATE")
	private String originalDocumentDate;

	@XmlElement(name = "ORG_DOC_TYPE")
	private String originalDocumentType;

	@XmlElement(name = "ORG_INV_NUM")
	private String originalInvoiceNumber;

	@XmlElement(name = "ORG_INV_DATE")
	private String originalInvoiceDate;

	@XmlElement(name = "GENERATION_DATE")
	private String date2bGenerationDate;

	@XmlElement(name = "GSTR_FP")
	private String gstrFilingPeriod;

	@XmlElement(name = "GSTR_FD")
	private String gstrFilingDate;

	@XmlElement(name = "DIFF_PERCT")
	private BigDecimal differentialPercentage = BigDecimal.ZERO;

	@XmlElement(name = "REV_CHR_FLAG")
	private String reverseChargeFlag;

	@XmlElement(name = "ITC_AVAIL")
	private String itcAvailability;

	@XmlElement(name = "RSN_ITC_UNAVAIL")
	private String reasonForItcAvailability;

	@XmlElement(name = "SOU_TYPE_IRN")
	private String srcTypeOfIrn;

	@XmlElement(name = "IRN_NUMBER")
	private String irnNum;

	@XmlElement(name = "IRN_GEN_DATE")
	private String irnGenDate;

	@XmlElement(name = "TABLE_TYPE")
	private String tableType;

	@XmlElement(name = "ENTITY_NAME")
	private String entityName;
	
	@XmlElement(name = "ENTITY_PAN")
	private String entityPan;
	
	@XmlElement(name = "INV_STATUS")
	private String invStatus;

}
