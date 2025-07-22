/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class ConsolidatedGstr2ADto {

	private String returnPeriod;
	private String counterPartyReturnStatus;
	private String recipientGSTIN;
	private String supplierGSTIN;
	private String supplierName;
	private String legalName;
	private String documentType;
	private String supplyType;
	private String documentNumber;
	private String documentDate;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private String taxRate;
	private BigDecimal igstAmt = BigDecimal.ZERO;
	private BigDecimal cgstAmt = BigDecimal.ZERO;
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	private BigDecimal cessAmt = BigDecimal.ZERO;
	private BigDecimal totalTaxAmt = BigDecimal.ZERO;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private String pos;
	private String stateName;
	private String portCode;
	private String billofentryNumber;
	private String billofentryDate;
	private String billOfEntryRefDate;
	private String bOEAmendmentFlag;
	private String originalSupplierGSTIN;
	private String originalSupplierTradeName;
	private String originalPortCode;
	private String originalBillOfEntryNumber;
	private String originalBillOfEntryDate;
	private String originalBillOfEntryRefDate;
	private String originalTaxableValue;
	private String originalIGSTAmount;
	private String originalCessAmount;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String invoiceNumber;
	private String invoiceDate;
	private String originalInvAmendmentPeriod;
	private String originalAmendmentType;
	private String reverseChargeFlag;
	private String gSTR1FilingStatus;
	private String gSTR1FilingDate;
	private String gSTR1FilingPeriod;
	private String gSTR3BFilingStatus;
	private String cancellationDate;
	private String cDNDelinkingFlag;
	private String crdrpreGst;
	private String itcEligible;
	private String differentialPercentage;
	private String lineNumber;
	private String ecomGstin;
	private String merchantID;
	private String initiatedDate;
	private String initiatedTime;
	private String irnNum;
	private String irnGenDate;
	private String irnSourceType;

	
	public String getReturnPeriod() {
		return returnPeriod;
	}



	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}



	public String getCounterPartyReturnStatus() {
		return counterPartyReturnStatus;
	}



	public void setCounterPartyReturnStatus(String counterPartyReturnStatus) {
		this.counterPartyReturnStatus = counterPartyReturnStatus;
	}



	public String getRecipientGSTIN() {
		return recipientGSTIN;
	}



	public void setRecipientGSTIN(String recipientGSTIN) {
		this.recipientGSTIN = recipientGSTIN;
	}



	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}



	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}



	public String getSupplierName() {
		return supplierName;
	}



	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}



	public String getLegalName() {
		return legalName;
	}



	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}



	public String getDocumentType() {
		return documentType;
	}



	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}



	public String getSupplyType() {
		return supplyType;
	}



	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}



	public String getDocumentNumber() {
		return documentNumber;
	}



	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}



	public String getDocumentDate() {
		return documentDate;
	}



	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}



	public BigDecimal getTaxableValue() {
		return taxableValue;
	}



	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}



	public String getTaxRate() {
		return taxRate;
	}



	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}



	public BigDecimal getIgstAmt() {
		return igstAmt;
	}



	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}



	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}



	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}



	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}



	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}



	public BigDecimal getCessAmt() {
		return cessAmt;
	}



	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}



	public BigDecimal getTotalTaxAmt() {
		return totalTaxAmt;
	}



	public void setTotalTaxAmt(BigDecimal totalTaxAmt) {
		this.totalTaxAmt = totalTaxAmt;
	}



	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}



	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}



	public String getPos() {
		return pos;
	}



	public void setPos(String pos) {
		this.pos = pos;
	}



	public String getStateName() {
		return stateName;
	}



	public void setStateName(String stateName) {
		this.stateName = stateName;
	}



	public String getPortCode() {
		return portCode;
	}



	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}



	public String getBillofentryNumber() {
		return billofentryNumber;
	}



	public void setBillofentryNumber(String billofentryNumber) {
		this.billofentryNumber = billofentryNumber;
	}



	public String getBillofentryDate() {
		return billofentryDate;
	}



	public void setBillofentryDate(String billofentryDate) {
		this.billofentryDate = billofentryDate;
	}



	public String getBillOfEntryRefDate() {
		return billOfEntryRefDate;
	}



	public void setBillOfEntryRefDate(String billOfEntryRefDate) {
		this.billOfEntryRefDate = billOfEntryRefDate;
	}



	public String getbOEAmendmentFlag() {
		return bOEAmendmentFlag;
	}



	public void setbOEAmendmentFlag(String bOEAmendmentFlag) {
		this.bOEAmendmentFlag = bOEAmendmentFlag;
	}



	public String getOriginalSupplierGSTIN() {
		return originalSupplierGSTIN;
	}



	public void setOriginalSupplierGSTIN(String originalSupplierGSTIN) {
		this.originalSupplierGSTIN = originalSupplierGSTIN;
	}



	public String getOriginalSupplierTradeName() {
		return originalSupplierTradeName;
	}



	public void setOriginalSupplierTradeName(String originalSupplierTradeName) {
		this.originalSupplierTradeName = originalSupplierTradeName;
	}



	public String getOriginalPortCode() {
		return originalPortCode;
	}



	public void setOriginalPortCode(String originalPortCode) {
		this.originalPortCode = originalPortCode;
	}



	public String getOriginalBillOfEntryNumber() {
		return originalBillOfEntryNumber;
	}



	public void setOriginalBillOfEntryNumber(String originalBillOfEntryNumber) {
		this.originalBillOfEntryNumber = originalBillOfEntryNumber;
	}



	public String getOriginalBillOfEntryDate() {
		return originalBillOfEntryDate;
	}



	public void setOriginalBillOfEntryDate(String originalBillOfEntryDate) {
		this.originalBillOfEntryDate = originalBillOfEntryDate;
	}



	public String getOriginalBillOfEntryRefDate() {
		return originalBillOfEntryRefDate;
	}



	public void setOriginalBillOfEntryRefDate(String originalBillOfEntryRefDate) {
		this.originalBillOfEntryRefDate = originalBillOfEntryRefDate;
	}



	

	public String getOriginalTaxableValue() {
		return originalTaxableValue;
	}



	public void setOriginalTaxableValue(String originalTaxableValue) {
		this.originalTaxableValue = originalTaxableValue;
	}



	public String getOriginalIGSTAmount() {
		return originalIGSTAmount;
	}



	public void setOriginalIGSTAmount(String originalIGSTAmount) {
		this.originalIGSTAmount = originalIGSTAmount;
	}



	public String getOriginalCessAmount() {
		return originalCessAmount;
	}



	public void setOriginalCessAmount(String originalCessAmount) {
		this.originalCessAmount = originalCessAmount;
	}



	public String getOriginalDocumentNumber() {
		return originalDocumentNumber;
	}



	public void setOriginalDocumentNumber(String originalDocumentNumber) {
		this.originalDocumentNumber = originalDocumentNumber;
	}



	public String getOriginalDocumentDate() {
		return originalDocumentDate;
	}



	public void setOriginalDocumentDate(String originalDocumentDate) {
		this.originalDocumentDate = originalDocumentDate;
	}



	public String getInvoiceNumber() {
		return invoiceNumber;
	}



	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}



	public String getInvoiceDate() {
		return invoiceDate;
	}



	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}



	public String getOriginalInvAmendmentPeriod() {
		return originalInvAmendmentPeriod;
	}



	public void setOriginalInvAmendmentPeriod(String originalInvAmendmentPeriod) {
		this.originalInvAmendmentPeriod = originalInvAmendmentPeriod;
	}



	public String getOriginalAmendmentType() {
		return originalAmendmentType;
	}



	public void setOriginalAmendmentType(String originalAmendmentType) {
		this.originalAmendmentType = originalAmendmentType;
	}



	public String getReverseChargeFlag() {
		return reverseChargeFlag;
	}



	public void setReverseChargeFlag(String reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}



	public String getgSTR1FilingStatus() {
		return gSTR1FilingStatus;
	}



	public void setgSTR1FilingStatus(String gSTR1FilingStatus) {
		this.gSTR1FilingStatus = gSTR1FilingStatus;
	}



	public String getgSTR1FilingDate() {
		return gSTR1FilingDate;
	}



	public void setgSTR1FilingDate(String gSTR1FilingDate) {
		this.gSTR1FilingDate = gSTR1FilingDate;
	}



	public String getgSTR1FilingPeriod() {
		return gSTR1FilingPeriod;
	}



	public void setgSTR1FilingPeriod(String gSTR1FilingPeriod) {
		this.gSTR1FilingPeriod = gSTR1FilingPeriod;
	}



	public String getgSTR3BFilingStatus() {
		return gSTR3BFilingStatus;
	}



	public void setgSTR3BFilingStatus(String gSTR3BFilingStatus) {
		this.gSTR3BFilingStatus = gSTR3BFilingStatus;
	}



	public String getCancellationDate() {
		return cancellationDate;
	}



	public void setCancellationDate(String cancellationDate) {
		this.cancellationDate = cancellationDate;
	}



	public String getcDNDelinkingFlag() {
		return cDNDelinkingFlag;
	}



	public void setcDNDelinkingFlag(String cDNDelinkingFlag) {
		this.cDNDelinkingFlag = cDNDelinkingFlag;
	}



	public String getCrdrpreGst() {
		return crdrpreGst;
	}



	public void setCrdrpreGst(String crdrpreGst) {
		this.crdrpreGst = crdrpreGst;
	}



	public String getItcEligible() {
		return itcEligible;
	}



	public void setItcEligible(String itcEligible) {
		this.itcEligible = itcEligible;
	}



	public String getDifferentialPercentage() {
		return differentialPercentage;
	}



	public void setDifferentialPercentage(String differentialPercentage) {
		this.differentialPercentage = differentialPercentage;
	}



	public String getLineNumber() {
		return lineNumber;
	}



	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}



	public String getEcomGstin() {
		return ecomGstin;
	}



	public void setEcomGstin(String ecomGstin) {
		this.ecomGstin = ecomGstin;
	}



	public String getMerchantID() {
		return merchantID;
	}



	public void setMerchantID(String merchantID) {
		this.merchantID = merchantID;
	}



	public String getInitiatedDate() {
		return initiatedDate;
	}



	public void setInitiatedDate(String initiatedDate) {
		this.initiatedDate = initiatedDate;
	}



	public String getInitiatedTime() {
		return initiatedTime;
	}



	public void setInitiatedTime(String initiatedTime) {
		this.initiatedTime = initiatedTime;
	}



	public String getIrnNum() {
		return irnNum;
	}



	public void setIrnNum(String irnNum) {
		this.irnNum = irnNum;
	}



	public String getIrnGenDate() {
		return irnGenDate;
	}



	public void setIrnGenDate(String irnGenDate) {
		this.irnGenDate = irnGenDate;
	}



	public String getIrnSourceType() {
		return irnSourceType;
	}



	public void setIrnSourceType(String irnSourceType) {
		this.irnSourceType = irnSourceType;
	}



	@Override
	public String toString() {
		return "ConsolidatedGstr2ADto [returnPeriod=" + returnPeriod + ", counterPartyReturnStatus="
				+ counterPartyReturnStatus + ", recipientGSTIN=" + recipientGSTIN + ", supplierGSTIN=" + supplierGSTIN
				+ ", supplierName=" + supplierName + ", legalName=" + legalName + ", documentType=" + documentType
				+ ", supplyType=" + supplyType + ", documentNumber=" + documentNumber + ", documentDate=" + documentDate
				+ ", taxableValue=" + taxableValue + ", taxRate=" + taxRate + ", igstAmt=" + igstAmt + ", cgstAmt="
				+ cgstAmt + ", sgstAmt=" + sgstAmt + ", cessAmt=" + cessAmt + ", totalTaxAmt=" + totalTaxAmt
				+ ", invoiceValue=" + invoiceValue + ", pos=" + pos + ", stateName=" + stateName + ", portCode="
				+ portCode + ", billofentryNumber=" + billofentryNumber + ", billofentryDate=" + billofentryDate
				+ ", billOfEntryRefDate=" + billOfEntryRefDate + ", bOEAmendmentFlag=" + bOEAmendmentFlag
				+ ", originalSupplierGSTIN=" + originalSupplierGSTIN + ", originalSupplierTradeName="
				+ originalSupplierTradeName + ", originalPortCode=" + originalPortCode + ", originalBillOfEntryNumber="
				+ originalBillOfEntryNumber + ", originalBillOfEntryDate=" + originalBillOfEntryDate
				+ ", originalBillOfEntryRefDate=" + originalBillOfEntryRefDate + ", originalTaxableValue="
				+ originalTaxableValue + ", originalIGSTAmount=" + originalIGSTAmount + ", originalCessAmount="
				+ originalCessAmount + ", originalDocumentNumber=" + originalDocumentNumber + ", originalDocumentDate="
				+ originalDocumentDate + ", invoiceNumber=" + invoiceNumber + ", invoiceDate=" + invoiceDate
				+ ", originalInvAmendmentPeriod=" + originalInvAmendmentPeriod + ", originalAmendmentType="
				+ originalAmendmentType + ", reverseChargeFlag=" + reverseChargeFlag + ", gSTR1FilingStatus="
				+ gSTR1FilingStatus + ", gSTR1FilingDate=" + gSTR1FilingDate + ", gSTR1FilingPeriod="
				+ gSTR1FilingPeriod + ", gSTR3BFilingStatus=" + gSTR3BFilingStatus + ", cancellationDate="
				+ cancellationDate + ", cDNDelinkingFlag=" + cDNDelinkingFlag + ", crdrpreGst=" + crdrpreGst
				+ ", itcEligible=" + itcEligible + ", differentialPercentage=" + differentialPercentage
				+ ", lineNumber=" + lineNumber + ", ecomGstin=" + ecomGstin + ", merchantID=" + merchantID
				+ ", initiatedDate=" + initiatedDate + ", initiatedTime=" + initiatedTime + ", irnNum=" + irnNum
				+ ", irnGenDate=" + irnGenDate + ", irnSourceType=" + irnSourceType + "]";
	}

}