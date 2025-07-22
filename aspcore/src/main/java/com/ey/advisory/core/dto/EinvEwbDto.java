/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class EinvEwbDto {

	@Expose
	@SerializedName("CompanyCode")
	private String companyCode;

	@Expose
	@SerializedName("DocumentType")
	private String documentType;

	@Expose
	@SerializedName("AccountingVoucherNumber")
	private String accountingVoucherNumber;

	@Expose
	@SerializedName("FiscalYear")
	private String fiscalYear;

	@Expose
	@SerializedName("SupplierGSTIN")
	private String supplierGstin;

	@Expose
	@SerializedName("DocumentNumber")
	private String documentNumber;

	@Expose
	@SerializedName("DocumentDate")
	private String documentDate;

	@Expose
	@SerializedName("E-InvoicingStatus")
	private String einvoicingStatus;

	@Expose
	@SerializedName("AcknowledgementNumber")
	private String acknowledgementNumber;

	@Expose
	@SerializedName("AcknowledgementDate")
	private String acknowledgementDate;

	@Expose
	@SerializedName("IRNNumber")
	private String iRNNumber;

	@Expose
	@SerializedName("SignedInvoicedata")
	private String signedInvoiceData;

	@Expose
	@SerializedName("SignedQRCodeData")
	private String signedQRCodeData;

	@Expose
	@SerializedName("QrData")
	private String qRData;

	@Expose
	@SerializedName("FormattedQRData")
	private String formattedQRData;

	@Expose
	@SerializedName("EWBNumber")
	private String ewbNo;

	@Expose
	@SerializedName("EWBDate")
	private String ewbDate;

	@Expose
	@SerializedName("EWBValidityEnddate")
	private String ewbValidityEnddate;

	@Expose
	@SerializedName("NICDistance")
	private String nICDistance;

	@Expose
	@SerializedName("EWBErrorCode")
	private String eWBErrorCode;

	@Expose
	@SerializedName("EWBErrormessage")
	private String eWBErrormessage;

	@Expose
	@SerializedName("EWBInformationCode")
	private String eWBInformationCode;

	@Expose
	@SerializedName("EWBInformationmessage")
	private String eWBInformationmessage;

	@Expose
	@SerializedName("CancellationDate")
	private String cancellationDate;

	@Expose
	@SerializedName("EINVErrorCode")
	private String eINVErrorCode;

	@Expose
	@SerializedName("EINVErrormessage")
	private String eINVErrormessage;

	@Expose
	@SerializedName("EINVInformationCode")
	private String eINVInformationCode;

	@Expose
	@SerializedName("EINVInformationmessage")
	private String eINVInformationmessage;

	@Expose
	@SerializedName("FileId")
	private String fileId;

	/*
	 * @Expose
	 * 
	 * @SerializedName("AspErrorCode") private String aspErrorCode;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("AspErrorDes") private String aspErrorDes;
	 */

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public String getSupplierGstin() {
		return supplierGstin;
	}

	public void setSupplierGstin(String supplierGstin) {
		this.supplierGstin = supplierGstin;
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

	public String getEinvoicingStatus() {
		return einvoicingStatus;
	}

	public void setEinvoicingStatus(String einvoicingStatus) {
		this.einvoicingStatus = einvoicingStatus;
	}

	public String getAcknowledgementNumber() {
		return acknowledgementNumber;
	}

	public void setAcknowledgementNumber(String acknowledgementNumber) {
		this.acknowledgementNumber = acknowledgementNumber;
	}

	public String getiRNNumber() {
		return iRNNumber;
	}

	public void setiRNNumber(String iRNNumber) {
		this.iRNNumber = iRNNumber;
	}

	public String getSignedInvoiceData() {
		return signedInvoiceData;
	}

	public void setSignedInvoiceData(String signedInvoiceData) {
		this.signedInvoiceData = signedInvoiceData;
	}

	public String getSignedQRCodeData() {
		return signedQRCodeData;
	}

	public void setSignedQRCodeData(String signedQRCodeData) {
		this.signedQRCodeData = signedQRCodeData;
	}

	public String getqRData() {
		return qRData;
	}

	public void setqRData(String qRData) {
		this.qRData = qRData;
	}

	public String getFormattedQRData() {
		return formattedQRData;
	}

	public void setFormattedQRData(String formattedQRData) {
		this.formattedQRData = formattedQRData;
	}

	public String getEwbNo() {
		return ewbNo;
	}

	public void setEwbNo(String ewbNo) {
		this.ewbNo = ewbNo;
	}

	public String getEwbDate() {
		return ewbDate;
	}

	public void setEwbDate(String ewbDate) {
		this.ewbDate = ewbDate;
	}

	public String getnICDistance() {
		return nICDistance;
	}

	public void setnICDistance(String nICDistance) {
		this.nICDistance = nICDistance;
	}

	public String geteWBErrorCode() {
		return eWBErrorCode;
	}

	public void seteWBErrorCode(String eWBErrorCode) {
		this.eWBErrorCode = eWBErrorCode;
	}

	public String geteWBErrormessage() {
		return eWBErrormessage;
	}

	public void seteWBErrormessage(String eWBErrormessage) {
		this.eWBErrormessage = eWBErrormessage;
	}

	public String geteWBInformationCode() {
		return eWBInformationCode;
	}

	public void seteWBInformationCode(String eWBInformationCode) {
		this.eWBInformationCode = eWBInformationCode;
	}

	public String geteWBInformationmessage() {
		return eWBInformationmessage;
	}

	public void seteWBInformationmessage(String eWBInformationmessage) {
		this.eWBInformationmessage = eWBInformationmessage;
	}

	public String geteINVErrorCode() {
		return eINVErrorCode;
	}

	public void seteINVErrorCode(String eINVErrorCode) {
		this.eINVErrorCode = eINVErrorCode;
	}

	public String geteINVErrormessage() {
		return eINVErrormessage;
	}

	public void seteINVErrormessage(String eINVErrormessage) {
		this.eINVErrormessage = eINVErrormessage;
	}

	public String geteINVInformationCode() {
		return eINVInformationCode;
	}

	public void seteINVInformationCode(String eINVInformationCode) {
		this.eINVInformationCode = eINVInformationCode;
	}

	public String geteINVInformationmessage() {
		return eINVInformationmessage;
	}

	public void seteINVInformationmessage(String eINVInformationmessage) {
		this.eINVInformationmessage = eINVInformationmessage;
	}

	public String getAcknowledgementDate() {
		return acknowledgementDate;
	}

	public void setAcknowledgementDate(String acknowledgementDate) {
		this.acknowledgementDate = acknowledgementDate;
	}

	public String getEwbValidityEnddate() {
		return ewbValidityEnddate;
	}

	public void setEwbValidityEnddate(String ewbValidityEnddate) {
		this.ewbValidityEnddate = ewbValidityEnddate;
	}

	public String getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(String cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "EinvEwbDto [companyCode=" + companyCode + ", documentType="
				+ documentType + ", accountingVoucherNumber="
				+ accountingVoucherNumber + ", fiscalYear=" + fiscalYear
				+ ", supplierGstin=" + supplierGstin + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate
				+ ", einvoicingStatus=" + einvoicingStatus
				+ ", acknowledgementNumber=" + acknowledgementNumber
				+ ", acknowledgementDate=" + acknowledgementDate
				+ ", iRNNumber=" + iRNNumber + ", signedInvoiceData="
				+ signedInvoiceData + ", signedQRCodeData=" + signedQRCodeData
				+ ", qRData=" + qRData + ", formattedQRData=" + formattedQRData
				+ ", ewbNo=" + ewbNo + ", ewbDate=" + ewbDate
				+ ", ewbValidityEnddate=" + ewbValidityEnddate
				+ ", nICDistance=" + nICDistance + ", eWBErrorCode="
				+ eWBErrorCode + ", eWBErrormessage=" + eWBErrormessage
				+ ", eWBInformationCode=" + eWBInformationCode
				+ ", eWBInformationmessage=" + eWBInformationmessage
				+ ", cancellationDate=" + cancellationDate + ", eINVErrorCode="
				+ eINVErrorCode + ", eINVErrormessage=" + eINVErrormessage
				+ ", eINVInformationCode=" + eINVInformationCode
				+ ", eINVInformationmessage=" + eINVInformationmessage
				+ ", fileId=" + fileId + "]";
	}

}
