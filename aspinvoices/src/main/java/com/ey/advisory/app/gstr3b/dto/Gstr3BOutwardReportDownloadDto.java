package com.ey.advisory.app.gstr3b.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@ToString
@NoArgsConstructor
public class Gstr3BOutwardReportDownloadDto {
	
	private String aspErrorCode;
	private String aspErrorDesc;
	private String aspInformationId;
	private String aspInformationDesc;
	private String irnStatus;
	private String irnNo;
	private String irnAcknowledgmentNo;
	private String irnAcknowledgmentDateTime;
	private String signedQRCode;
	private String signedInvoice;
	private String irpErrorCode;
	private String irpErrorDescription;
	private String ewbStatus;
	private String ewbValidupto;
	private String ewbNo;
	private String ewbRespDate;
	private String ewbErrorCode;
	private String ewbErrorDescription;
	private String gstnStatus;
	private String gstnRefid;
	private String gstnRefidDateTime;
	private String gstnErrorCode;
	private String gstnErrorDescription;
	private String returnType;
	private String tableNumber;
	private String tableNumberGSTR3B;
	private String irn;
	private String irnDate;
	private String taxScheme;
	private String cancellationReason;
	private String cancellationRemarks;
	private String supplyType;
	private String docCategory;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String reverseChargeFlag;
	private String supplierGSTIN;
	private String supplierTradeName;
	private String supplierLegalName;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierLocation;
	private String supplierPincode;
	private String supplierStateCode;
	private String supplierPhone;
	private String supplierEmail;
	private String customerGSTIN;
	private String customerTradeName;
	private String customerLegalName;
	private String customerAddress1;
	private String customerAddress2;
	private String customerLocation;
	private String customerPincode;
	private String customerStateCode;
	private String billingPOS;
	private String customerPhone;
	private String customerEmail;
	private String dispatcherGSTIN;
	private String dispatcherTradeName;
	private String dispatcherAddress1;
	private String dispatcherAddress2;
	private String dispatcherLocation;
	private String dispatcherPincode;
	private String dispatcherStateCode;
	private String shipToGSTIN;
	private String shipToTradeName;
	private String shipToLegalName;
	private String shipToAddress1;
	private String shipToAddress2;
	private String shipToLocation;
	private String shipToPincode;
	private String shipToStateCode;
	private String itemSerialNumber;
	private String productSerialNumber;
	private String productName;
	private String productDescription;
	private String isService;
	private String hsn;
	private String barcode;
	private String batchName;
	private String batchExpiryDate;
	private String warrantyDate;
	private String orderlineReference;
	private String attributeName;
	private String attributeValue;
	private String originCountry;
	private String uQC;
	private String Quantity;
	private String freeQuantity;
	private String unitPrice;
	private String itemAmount;
	private String itemDiscount;
	private String preTaxAmount;
	private String itemAssessableAmount;
	private String iGSTRate;
	private String iGSTAmount;
	private String cGSTRate;
	private String cGSTAmount;
	private String sGSTRate;
	private String sGSTAmount;
	private String cessAdvaloremRate;
	private String cessAdvaloremAmount;
	private String cessSpecificRate;
	private String cessSpecificAmount;
	private String stateCessAdvaloremRate;
	private String stateCessAdvaloremAmount;
	private String stateCessSpecificRate;
	private String stateCessSpecificAmount;
	private String itemOtherCharges;
	private String totalItemAmount;
	private String invoiceOtherCharges;
	private String invoiceAssessableAmount;
	private String invoiceIGSTAmount;
	private String invoiceCGSTAmount;
	private String invoiceSGSTAmount;
	private String invoiceCessAdvaloremAmount;
	private String invoiceCessSpecificAmount;
	private String invoiceStateCessAdvaloremAmount;
	private String invoiceStateCessSpecificAmount;
	private String invoiceValue;
	private String roundOff;
	private String totalInvoiceValue;
	private String tCSFlagIncomeTax;
	private String tCSRateIncomeTax;
	private String tCSAmountIncomeTax;
	private String customerPANOrAadhaar;
	private String currencyCode;
	private String countryCode;
	private String invoiceValueFC;
	private String portCode;
	private String shippingBillNumber;
	private String shippingBillDate;
	private String invoiceRemarks;
	private String invoicePeriodStartDate;
	private String invoicePeriodEndDate;
	private String preceedingInvoiceNumber;
	private String preceedingInvoiceDate;
	private String otherReference;
	private String receiptAdviceReference;
	private String receiptAdviceDate;
	private String tenderReference;
	private String contractReference;
	private String externalReference;
	private String projectReference;
	private String customerPOReferenceNumber;
	private String customerPOReferenceDate;
	private String payeeName;
	private String modeOfPayment;
	private String branchOrIFSCCode;
	private String paymentTerms;
	private String paymentInstruction;
	private String creditTransfer;
	private String directDebit;
	private String creditDays;
	private String paidAmount;
	private String balanceAmount;
	private String paymentDueDate;
	private String accountDetail;
	private String ecomGSTIN;
	private String ecomTransactionID;
	private String supportingDocURL;
	private String supportingDocument;
	private String additionalInformation;
	private String transactionType;
	private String subSupplyType;
	private String otherSupplyTypeDescription;
	private String transporterID;
	private String transporterName;
	private String transportMode;
	private String transportDocNo;
	private String transportDocDate;
	private String distance;
	private String vehicleNo;
	private String vehicleType;
	private String returnPeriod;
	private String originalDocumentType;
	private String originalCustomerGSTIN;
	private String differentialPercentageFlag;
	private String sec7ofIGSTFlag;
	private String claimRefndFlag;
	private String autoPopltToRefund;
	private String cRDRPreGST;
	private String customerType;
	private String customerCode;
	private String productCode;
	private String categoryOfProduct;
	private String iTCFlag;
	private String stateApplyingCess;
	private String fOB;
	private String exportDuty;
	private String exchangeRate;
	private String reasonForCreditDebitNote;
	private String tCSFlagGST;
	private String tCSIGSTAmount;
	private String tCSCGSTAmount;
	private String tCSSGSTAmount;
	private String tDSFlagGST;
	private String tDSIGSTAmount;
	private String tDSCGSTAmount;
	private String tDSSGSTAmount;
	private String userID;
	private String companyCode;
	private String sourceIdentifier;
	private String sourceFileName;
	private String plantCode;
	private String division;
	private String subDivision;
	private String location;
	private String salesOrganisation;
	private String distributionChannel;
	private String profitCentre1;
	private String profitCentre2;
	private String profitCentre3;
	private String profitCentre4;
	private String profitCentre5;
	private String profitCentre6;
	private String profitCentre7;
	private String profitCentre8;
	private String glAssessableValue;
	private String glIGST;
	private String glCGST;
	private String glSGST;
	private String glAdvaloremCess;
	private String glSpecificCess;
	private String gLStateCessAdvalorem;
	private String gLStateCessSpecific;
	private String glPostingDate;
	private String salesOrderNumber;
	private String eWBNumber;
	private String eWBDate;
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String documentReferenceNumber;
	private String customerTAN;
	private String userDefField1;
	private String userDefField2;
	private String userDefField3;
	private String userDefField4;
	private String userDefField5;
	private String userDefField6;
	private String userDefField7;
	private String userDefField8;
	private String userDefField9;
	private String userDefField10;
	private String userDefField11;
	private String userDefField12;
	private String userDefField13;
	private String userDefField14;
	private String userDefField15;
	private String userDefField16;
	private String userDefField17;
	private String userDefField18;
	private String userDefField19;
	private String userDefField20;
	private String userDefField21;
	private String userDefField22;
	private String userDefField23;
	private String userDefField24;
	private String userDefField25;
	private String userDefField26;
	private String userDefField27;
	private String userDefField28;
	private String userDefField29;
	private String userDefField30;
	private String supplyTypeASP;
	private String approximateDistanceASP;
	private String distanceSavedtoEWB;
	private String userId;
	private String fileID;
	private String fileName;
	private String invoiceOtherChargesASP;
	private String invoiceAssessableAmountASP;
	private String invoiceIGSTAmountASP;
	private String invoiceCGSTAmountASP;
	private String invoiceSGSTAmountASP;
	private String invoiceCessAdvaloremAmountASP;
	private String invoiceCessSpecificAmountASP;
	private String invoiceStateCessAdvaloremAmountASP;
	private String invoiceStateCessSpecificAmountASP;
	private String invoiceValueASP;
	private String integratedTaxAmountASP;
	private String centralTaxAmountASP;
	private String stateUTTaxAmountASP;
	private String cessAdvaloremAmountASP;
	private String stateCessAdvaloremAmountASP;
	private String integratedTaxAmountRET1Impact;
	private String centralTaxAmountRET1Impact;
	private String stateUTTaxAmountRET1Impact;
	private String cessAdvaloremAmountDifference;
	private String stateCessAdvaloremAmountDifference;
	private String recordStatus;
	private String digiGSTStatus;//starts new column
	private String errorPoint;
	private String irnAcknowledgmentDate;
	private String irnAcknowledgmentTime;
	private String irnCancellationDate;
	private String irnCancellationTime;
	private String ewbTime;
	private String ewbValiduptoTime;
	private String eWBCancellationDate;
	private String eWBCancellationTime;
	private String gstnRefidDate;
	private String gstnRefidTime;
	private String infoErrorCode;
	private String infoErrorMsg;
	
	public String getInfoErrorCode() {
		return infoErrorCode;
	}

	public void setInfoErrorCode(String infoErrorCode) {
		this.infoErrorCode = infoErrorCode;
	}

	public String getInfoErrorMsg() {
		return infoErrorMsg;
	}

	public void setInfoErrorMsg(String infoErrorMsg) {
		this.infoErrorMsg = infoErrorMsg;
	}

	public String getAspErrorCode() {
		return aspErrorCode;
	}

	public void setAspErrorCode(String aspErrorCode) {
		this.aspErrorCode = aspErrorCode;
	}

	public String getAspErrorDesc() {
		return aspErrorDesc;
	}

	public void setAspErrorDesc(String aspErrorDesc) {
		this.aspErrorDesc = aspErrorDesc;
	}

	public String getAspInformationId() {
		return aspInformationId;
	}

	public void setAspInformationId(String aspInformationId) {
		this.aspInformationId = aspInformationId;
	}

	public String getAspInformationDesc() {
		return aspInformationDesc;
	}

	public void setAspInformationDesc(String aspInformationDesc) {
		this.aspInformationDesc = aspInformationDesc;
	}

	public String getIrnStatus() {
		return irnStatus;
	}

	public void setIrnStatus(String irnStatus) {
		this.irnStatus = irnStatus;
	}

	public String getIrnNo() {
		return irnNo;
	}

	public void setIrnNo(String irnNo) {
		this.irnNo = irnNo;
	}

	public String getIrnAcknowledgmentNo() {
		return irnAcknowledgmentNo;
	}

	public void setIrnAcknowledgmentNo(String irnAcknowledgmentNo) {
		this.irnAcknowledgmentNo = irnAcknowledgmentNo;
	}

	public String getIrnAcknowledgmentDateTime() {
		return irnAcknowledgmentDateTime;
	}

	public void setIrnAcknowledgmentDateTime(String irnAcknowledgmentDateTime) {
		this.irnAcknowledgmentDateTime = irnAcknowledgmentDateTime;
	}

	public String getSignedQRCode() {
		return signedQRCode;
	}

	public void setSignedQRCode(String signedQRCode) {
		this.signedQRCode = signedQRCode;
	}

	public String getSignedInvoice() {
		return signedInvoice;
	}

	public void setSignedInvoice(String signedInvoice) {
		this.signedInvoice = signedInvoice;
	}

	public String getIrpErrorCode() {
		return irpErrorCode;
	}

	public void setIrpErrorCode(String irpErrorCode) {
		this.irpErrorCode = irpErrorCode;
	}

	public String getIrpErrorDescription() {
		return irpErrorDescription;
	}

	public void setIrpErrorDescription(String irpErrorDescription) {
		this.irpErrorDescription = irpErrorDescription;
	}

	public String getEwbStatus() {
		return ewbStatus;
	}

	public void setEwbStatus(String ewbStatus) {
		this.ewbStatus = ewbStatus;
	}

	public String getEwbValidupto() {
		return ewbValidupto;
	}

	public void setEwbValidupto(String ewbValidupto) {
		this.ewbValidupto = ewbValidupto;
	}

	public String getEwbNo() {
		return ewbNo;
	}

	public void setEwbNo(String ewbNo) {
		this.ewbNo = ewbNo;
	}

	public String getEwbRespDate() {
		return ewbRespDate;
	}

	public void setEwbRespDate(String ewbRespDate) {
		this.ewbRespDate = ewbRespDate;
	}

	public String getEwbErrorCode() {
		return ewbErrorCode;
	}

	public void setEwbErrorCode(String ewbErrorCode) {
		this.ewbErrorCode = ewbErrorCode;
	}

	public String getEwbErrorDescription() {
		return ewbErrorDescription;
	}

	public void setEwbErrorDescription(String ewbErrorDescription) {
		this.ewbErrorDescription = ewbErrorDescription;
	}

	public String getGstnStatus() {
		return gstnStatus;
	}

	public void setGstnStatus(String gstnStatus) {
		this.gstnStatus = gstnStatus;
	}

	public String getGstnRefid() {
		return gstnRefid;
	}

	public void setGstnRefid(String gstnRefid) {
		this.gstnRefid = gstnRefid;
	}

	public String getGstnRefidDateTime() {
		return gstnRefidDateTime;
	}

	public void setGstnRefidDateTime(String gstnRefidDateTime) {
		this.gstnRefidDateTime = gstnRefidDateTime;
	}

	public String getGstnErrorCode() {
		return gstnErrorCode;
	}

	public void setGstnErrorCode(String gstnErrorCode) {
		this.gstnErrorCode = gstnErrorCode;
	}

	public String getGstnErrorDescription() {
		return gstnErrorDescription;
	}

	public void setGstnErrorDescription(String gstnErrorDescription) {
		this.gstnErrorDescription = gstnErrorDescription;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getIrn() {
		return irn;
	}

	public void setIrn(String irn) {
		this.irn = irn;
	}

	public String getIrnDate() {
		return irnDate;
	}

	public void setIrnDate(String irnDate) {
		this.irnDate = irnDate;
	}

	public String getTaxScheme() {
		return taxScheme;
	}

	public void setTaxScheme(String taxScheme) {
		this.taxScheme = taxScheme;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
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

	public String getReverseChargeFlag() {
		return reverseChargeFlag;
	}

	public void setReverseChargeFlag(String reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getSupplierTradeName() {
		return supplierTradeName;
	}

	public void setSupplierTradeName(String supplierTradeName) {
		this.supplierTradeName = supplierTradeName;
	}

	public String getSupplierLegalName() {
		return supplierLegalName;
	}

	public void setSupplierLegalName(String supplierLegalName) {
		this.supplierLegalName = supplierLegalName;
	}

	public String getSupplierAddress1() {
		return supplierAddress1;
	}

	public void setSupplierAddress1(String supplierAddress1) {
		this.supplierAddress1 = supplierAddress1;
	}

	public String getSupplierAddress2() {
		return supplierAddress2;
	}

	public void setSupplierAddress2(String supplierAddress2) {
		this.supplierAddress2 = supplierAddress2;
	}

	public String getSupplierLocation() {
		return supplierLocation;
	}

	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

	public String getSupplierPincode() {
		return supplierPincode;
	}

	public void setSupplierPincode(String supplierPincode) {
		this.supplierPincode = supplierPincode;
	}

	public String getSupplierStateCode() {
		return supplierStateCode;
	}

	public void setSupplierStateCode(String supplierStateCode) {
		this.supplierStateCode = supplierStateCode;
	}

	public String getSupplierPhone() {
		return supplierPhone;
	}

	public void setSupplierPhone(String supplierPhone) {
		this.supplierPhone = supplierPhone;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	public String getCustomerGSTIN() {
		return customerGSTIN;
	}

	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}

	public String getCustomerTradeName() {
		return customerTradeName;
	}

	public void setCustomerTradeName(String customerTradeName) {
		this.customerTradeName = customerTradeName;
	}

	public String getCustomerLegalName() {
		return customerLegalName;
	}

	public void setCustomerLegalName(String customerLegalName) {
		this.customerLegalName = customerLegalName;
	}

	public String getCustomerAddress1() {
		return customerAddress1;
	}

	public void setCustomerAddress1(String customerAddress1) {
		this.customerAddress1 = customerAddress1;
	}

	public String getCustomerAddress2() {
		return customerAddress2;
	}

	public void setCustomerAddress2(String customerAddress2) {
		this.customerAddress2 = customerAddress2;
	}

	public String getCustomerLocation() {
		return customerLocation;
	}

	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}

	public String getCustomerPincode() {
		return customerPincode;
	}

	public void setCustomerPincode(String customerPincode) {
		this.customerPincode = customerPincode;
	}

	public String getCustomerStateCode() {
		return customerStateCode;
	}

	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}

	public String getBillingPOS() {
		return billingPOS;
	}

	public void setBillingPOS(String billingPOS) {
		this.billingPOS = billingPOS;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getDispatcherGSTIN() {
		return dispatcherGSTIN;
	}

	public void setDispatcherGSTIN(String dispatcherGSTIN) {
		this.dispatcherGSTIN = dispatcherGSTIN;
	}

	public String getDispatcherTradeName() {
		return dispatcherTradeName;
	}

	public void setDispatcherTradeName(String dispatcherTradeName) {
		this.dispatcherTradeName = dispatcherTradeName;
	}

	public String getDispatcherAddress1() {
		return dispatcherAddress1;
	}

	public void setDispatcherAddress1(String dispatcherAddress1) {
		this.dispatcherAddress1 = dispatcherAddress1;
	}

	public String getDispatcherAddress2() {
		return dispatcherAddress2;
	}

	public void setDispatcherAddress2(String dispatcherAddress2) {
		this.dispatcherAddress2 = dispatcherAddress2;
	}

	public String getDispatcherLocation() {
		return dispatcherLocation;
	}

	public void setDispatcherLocation(String dispatcherLocation) {
		this.dispatcherLocation = dispatcherLocation;
	}

	public String getDispatcherPincode() {
		return dispatcherPincode;
	}

	public void setDispatcherPincode(String dispatcherPincode) {
		this.dispatcherPincode = dispatcherPincode;
	}

	public String getDispatcherStateCode() {
		return dispatcherStateCode;
	}

	public void setDispatcherStateCode(String dispatcherStateCode) {
		this.dispatcherStateCode = dispatcherStateCode;
	}

	public String getShipToGSTIN() {
		return shipToGSTIN;
	}

	public void setShipToGSTIN(String shipToGSTIN) {
		this.shipToGSTIN = shipToGSTIN;
	}

	public String getShipToTradeName() {
		return shipToTradeName;
	}

	public void setShipToTradeName(String shipToTradeName) {
		this.shipToTradeName = shipToTradeName;
	}

	public String getShipToLegalName() {
		return shipToLegalName;
	}

	public void setShipToLegalName(String shipToLegalName) {
		this.shipToLegalName = shipToLegalName;
	}

	public String getShipToAddress1() {
		return shipToAddress1;
	}

	public void setShipToAddress1(String shipToAddress1) {
		this.shipToAddress1 = shipToAddress1;
	}

	public String getShipToAddress2() {
		return shipToAddress2;
	}

	public void setShipToAddress2(String shipToAddress2) {
		this.shipToAddress2 = shipToAddress2;
	}

	public String getShipToLocation() {
		return shipToLocation;
	}

	public void setShipToLocation(String shipToLocation) {
		this.shipToLocation = shipToLocation;
	}

	public String getShipToPincode() {
		return shipToPincode;
	}

	public void setShipToPincode(String shipToPincode) {
		this.shipToPincode = shipToPincode;
	}

	public String getShipToStateCode() {
		return shipToStateCode;
	}

	public void setShipToStateCode(String shipToStateCode) {
		this.shipToStateCode = shipToStateCode;
	}

	public String getItemSerialNumber() {
		return itemSerialNumber;
	}

	public void setItemSerialNumber(String itemSerialNumber) {
		this.itemSerialNumber = itemSerialNumber;
	}

	public String getProductSerialNumber() {
		return productSerialNumber;
	}

	public void setProductSerialNumber(String productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getIsService() {
		return isService;
	}

	public void setIsService(String isService) {
		this.isService = isService;
	}

	public String getHsn() {
		return hsn;
	}

	public void setHsn(String hsn) {
		this.hsn = hsn;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchExpiryDate() {
		return batchExpiryDate;
	}

	public void setBatchExpiryDate(String batchExpiryDate) {
		this.batchExpiryDate = batchExpiryDate;
	}

	public String getWarrantyDate() {
		return warrantyDate;
	}

	public void setWarrantyDate(String warrantyDate) {
		this.warrantyDate = warrantyDate;
	}

	public String getOrderlineReference() {
		return orderlineReference;
	}

	public void setOrderlineReference(String orderlineReference) {
		this.orderlineReference = orderlineReference;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	public String getuQC() {
		return uQC;
	}

	public void setuQC(String uQC) {
		this.uQC = uQC;
	}

	public String getQuantity() {
		return Quantity;
	}

	public void setQuantity(String quantity) {
		Quantity = quantity;
	}

	public String getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(String freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
	}

	public String getItemDiscount() {
		return itemDiscount;
	}

	public void setItemDiscount(String itemDiscount) {
		this.itemDiscount = itemDiscount;
	}

	public String getPreTaxAmount() {
		return preTaxAmount;
	}

	public void setPreTaxAmount(String preTaxAmount) {
		this.preTaxAmount = preTaxAmount;
	}

	public String getItemAssessableAmount() {
		return itemAssessableAmount;
	}

	public void setItemAssessableAmount(String itemAssessableAmount) {
		this.itemAssessableAmount = itemAssessableAmount;
	}

	public String getiGSTRate() {
		return iGSTRate;
	}

	public void setiGSTRate(String iGSTRate) {
		this.iGSTRate = iGSTRate;
	}

	public String getiGSTAmount() {
		return iGSTAmount;
	}

	public void setiGSTAmount(String iGSTAmount) {
		this.iGSTAmount = iGSTAmount;
	}

	public String getcGSTRate() {
		return cGSTRate;
	}

	public void setcGSTRate(String cGSTRate) {
		this.cGSTRate = cGSTRate;
	}

	public String getcGSTAmount() {
		return cGSTAmount;
	}

	public void setcGSTAmount(String cGSTAmount) {
		this.cGSTAmount = cGSTAmount;
	}

	public String getsGSTRate() {
		return sGSTRate;
	}

	public void setsGSTRate(String sGSTRate) {
		this.sGSTRate = sGSTRate;
	}

	public String getsGSTAmount() {
		return sGSTAmount;
	}

	public void setsGSTAmount(String sGSTAmount) {
		this.sGSTAmount = sGSTAmount;
	}

	public String getCessAdvaloremRate() {
		return cessAdvaloremRate;
	}

	public void setCessAdvaloremRate(String cessAdvaloremRate) {
		this.cessAdvaloremRate = cessAdvaloremRate;
	}

	public String getCessAdvaloremAmount() {
		return cessAdvaloremAmount;
	}

	public void setCessAdvaloremAmount(String cessAdvaloremAmount) {
		this.cessAdvaloremAmount = cessAdvaloremAmount;
	}

	public String getCessSpecificRate() {
		return cessSpecificRate;
	}

	public void setCessSpecificRate(String cessSpecificRate) {
		this.cessSpecificRate = cessSpecificRate;
	}

	public String getCessSpecificAmount() {
		return cessSpecificAmount;
	}

	public void setCessSpecificAmount(String cessSpecificAmount) {
		this.cessSpecificAmount = cessSpecificAmount;
	}

	public String getStateCessAdvaloremRate() {
		return stateCessAdvaloremRate;
	}

	public void setStateCessAdvaloremRate(String stateCessAdvaloremRate) {
		this.stateCessAdvaloremRate = stateCessAdvaloremRate;
	}

	public String getStateCessAdvaloremAmount() {
		return stateCessAdvaloremAmount;
	}

	public void setStateCessAdvaloremAmount(String stateCessAdvaloremAmount) {
		this.stateCessAdvaloremAmount = stateCessAdvaloremAmount;
	}

	public String getStateCessSpecificRate() {
		return stateCessSpecificRate;
	}

	public void setStateCessSpecificRate(String stateCessSpecificRate) {
		this.stateCessSpecificRate = stateCessSpecificRate;
	}

	public String getStateCessSpecificAmount() {
		return stateCessSpecificAmount;
	}

	public void setStateCessSpecificAmount(String stateCessSpecificAmount) {
		this.stateCessSpecificAmount = stateCessSpecificAmount;
	}

	public String getItemOtherCharges() {
		return itemOtherCharges;
	}

	public void setItemOtherCharges(String itemOtherCharges) {
		this.itemOtherCharges = itemOtherCharges;
	}

	public String getTotalItemAmount() {
		return totalItemAmount;
	}

	public void setTotalItemAmount(String totalItemAmount) {
		this.totalItemAmount = totalItemAmount;
	}

	public String getInvoiceOtherCharges() {
		return invoiceOtherCharges;
	}

	public void setInvoiceOtherCharges(String invoiceOtherCharges) {
		this.invoiceOtherCharges = invoiceOtherCharges;
	}

	public String getInvoiceAssessableAmount() {
		return invoiceAssessableAmount;
	}

	public void setInvoiceAssessableAmount(String invoiceAssessableAmount) {
		this.invoiceAssessableAmount = invoiceAssessableAmount;
	}

	public String getInvoiceIGSTAmount() {
		return invoiceIGSTAmount;
	}

	public void setInvoiceIGSTAmount(String invoiceIGSTAmount) {
		this.invoiceIGSTAmount = invoiceIGSTAmount;
	}

	public String getInvoiceCGSTAmount() {
		return invoiceCGSTAmount;
	}

	public void setInvoiceCGSTAmount(String invoiceCGSTAmount) {
		this.invoiceCGSTAmount = invoiceCGSTAmount;
	}

	public String getInvoiceSGSTAmount() {
		return invoiceSGSTAmount;
	}

	public void setInvoiceSGSTAmount(String invoiceSGSTAmount) {
		this.invoiceSGSTAmount = invoiceSGSTAmount;
	}

	public String getInvoiceCessAdvaloremAmount() {
		return invoiceCessAdvaloremAmount;
	}

	public void setInvoiceCessAdvaloremAmount(
			String invoiceCessAdvaloremAmount) {
		this.invoiceCessAdvaloremAmount = invoiceCessAdvaloremAmount;
	}

	public String getInvoiceCessSpecificAmount() {
		return invoiceCessSpecificAmount;
	}

	public void setInvoiceCessSpecificAmount(String invoiceCessSpecificAmount) {
		this.invoiceCessSpecificAmount = invoiceCessSpecificAmount;
	}

	public String getInvoiceStateCessAdvaloremAmount() {
		return invoiceStateCessAdvaloremAmount;
	}

	public void setInvoiceStateCessAdvaloremAmount(
			String invoiceStateCessAdvaloremAmount) {
		this.invoiceStateCessAdvaloremAmount = invoiceStateCessAdvaloremAmount;
	}

	public String getInvoiceStateCessSpecificAmount() {
		return invoiceStateCessSpecificAmount;
	}

	public void setInvoiceStateCessSpecificAmount(
			String invoiceStateCessSpecificAmount) {
		this.invoiceStateCessSpecificAmount = invoiceStateCessSpecificAmount;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public String getRoundOff() {
		return roundOff;
	}

	public void setRoundOff(String roundOff) {
		this.roundOff = roundOff;
	}

	public String getTotalInvoiceValue() {
		return totalInvoiceValue;
	}

	public void setTotalInvoiceValue(String totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}

	public String gettCSFlagIncomeTax() {
		return tCSFlagIncomeTax;
	}

	public void settCSFlagIncomeTax(String tCSFlagIncomeTax) {
		this.tCSFlagIncomeTax = tCSFlagIncomeTax;
	}

	public String gettCSRateIncomeTax() {
		return tCSRateIncomeTax;
	}

	public void settCSRateIncomeTax(String tCSRateIncomeTax) {
		this.tCSRateIncomeTax = tCSRateIncomeTax;
	}

	public String gettCSAmountIncomeTax() {
		return tCSAmountIncomeTax;
	}

	public void settCSAmountIncomeTax(String tCSAmountIncomeTax) {
		this.tCSAmountIncomeTax = tCSAmountIncomeTax;
	}

	public String getCustomerPANOrAadhaar() {
		return customerPANOrAadhaar;
	}

	public void setCustomerPANOrAadhaar(String customerPANOrAadhaar) {
		this.customerPANOrAadhaar = customerPANOrAadhaar;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getInvoiceValueFC() {
		return invoiceValueFC;
	}

	public void setInvoiceValueFC(String invoiceValueFC) {
		this.invoiceValueFC = invoiceValueFC;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getShippingBillNumber() {
		return shippingBillNumber;
	}

	public void setShippingBillNumber(String shippingBillNumber) {
		this.shippingBillNumber = shippingBillNumber;
	}

	public String getShippingBillDate() {
		return shippingBillDate;
	}

	public void setShippingBillDate(String shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}

	public String getInvoiceRemarks() {
		return invoiceRemarks;
	}

	public void setInvoiceRemarks(String invoiceRemarks) {
		this.invoiceRemarks = invoiceRemarks;
	}

	public String getInvoicePeriodStartDate() {
		return invoicePeriodStartDate;
	}

	public void setInvoicePeriodStartDate(String invoicePeriodStartDate) {
		this.invoicePeriodStartDate = invoicePeriodStartDate;
	}

	public String getInvoicePeriodEndDate() {
		return invoicePeriodEndDate;
	}

	public void setInvoicePeriodEndDate(String invoicePeriodEndDate) {
		this.invoicePeriodEndDate = invoicePeriodEndDate;
	}

	public String getPreceedingInvoiceNumber() {
		return preceedingInvoiceNumber;
	}

	public void setPreceedingInvoiceNumber(String preceedingInvoiceNumber) {
		this.preceedingInvoiceNumber = preceedingInvoiceNumber;
	}

	public String getPreceedingInvoiceDate() {
		return preceedingInvoiceDate;
	}

	public void setPreceedingInvoiceDate(String preceedingInvoiceDate) {
		this.preceedingInvoiceDate = preceedingInvoiceDate;
	}

	public String getOtherReference() {
		return otherReference;
	}

	public void setOtherReference(String otherReference) {
		this.otherReference = otherReference;
	}

	public String getReceiptAdviceReference() {
		return receiptAdviceReference;
	}

	public void setReceiptAdviceReference(String receiptAdviceReference) {
		this.receiptAdviceReference = receiptAdviceReference;
	}

	public String getReceiptAdviceDate() {
		return receiptAdviceDate;
	}

	public void setReceiptAdviceDate(String receiptAdviceDate) {
		this.receiptAdviceDate = receiptAdviceDate;
	}

	public String getTenderReference() {
		return tenderReference;
	}

	public void setTenderReference(String tenderReference) {
		this.tenderReference = tenderReference;
	}

	public String getContractReference() {
		return contractReference;
	}

	public void setContractReference(String contractReference) {
		this.contractReference = contractReference;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public String getProjectReference() {
		return projectReference;
	}

	public void setProjectReference(String projectReference) {
		this.projectReference = projectReference;
	}

	public String getCustomerPOReferenceNumber() {
		return customerPOReferenceNumber;
	}

	public void setCustomerPOReferenceNumber(String customerPOReferenceNumber) {
		this.customerPOReferenceNumber = customerPOReferenceNumber;
	}

	public String getCustomerPOReferenceDate() {
		return customerPOReferenceDate;
	}

	public void setCustomerPOReferenceDate(String customerPOReferenceDate) {
		this.customerPOReferenceDate = customerPOReferenceDate;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getBranchOrIFSCCode() {
		return branchOrIFSCCode;
	}

	public void setBranchOrIFSCCode(String branchOrIFSCCode) {
		this.branchOrIFSCCode = branchOrIFSCCode;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getPaymentInstruction() {
		return paymentInstruction;
	}

	public void setPaymentInstruction(String paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}

	public String getCreditTransfer() {
		return creditTransfer;
	}

	public void setCreditTransfer(String creditTransfer) {
		this.creditTransfer = creditTransfer;
	}

	public String getDirectDebit() {
		return directDebit;
	}

	public void setDirectDebit(String directDebit) {
		this.directDebit = directDebit;
	}

	public String getCreditDays() {
		return creditDays;
	}

	public void setCreditDays(String creditDays) {
		this.creditDays = creditDays;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(String paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	public String getAccountDetail() {
		return accountDetail;
	}

	public void setAccountDetail(String accountDetail) {
		this.accountDetail = accountDetail;
	}

	public String getEcomGSTIN() {
		return ecomGSTIN;
	}

	public void setEcomGSTIN(String ecomGSTIN) {
		this.ecomGSTIN = ecomGSTIN;
	}

	public String getEcomTransactionID() {
		return ecomTransactionID;
	}

	public void setEcomTransactionID(String ecomTransactionID) {
		this.ecomTransactionID = ecomTransactionID;
	}

	public String getSupportingDocURL() {
		return supportingDocURL;
	}

	public void setSupportingDocURL(String supportingDocURL) {
		this.supportingDocURL = supportingDocURL;
	}

	public String getSupportingDocument() {
		return supportingDocument;
	}

	public void setSupportingDocument(String supportingDocument) {
		this.supportingDocument = supportingDocument;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getSubSupplyType() {
		return subSupplyType;
	}

	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType;
	}

	public String getOtherSupplyTypeDescription() {
		return otherSupplyTypeDescription;
	}

	public void setOtherSupplyTypeDescription(
			String otherSupplyTypeDescription) {
		this.otherSupplyTypeDescription = otherSupplyTypeDescription;
	}

	public String getTransporterID() {
		return transporterID;
	}

	public void setTransporterID(String transporterID) {
		this.transporterID = transporterID;
	}

	public String getTransporterName() {
		return transporterName;
	}

	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}

	public String getTransportMode() {
		return transportMode;
	}

	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}

	public String getTransportDocNo() {
		return transportDocNo;
	}

	public void setTransportDocNo(String transportDocNo) {
		this.transportDocNo = transportDocNo;
	}

	public String getTransportDocDate() {
		return transportDocDate;
	}

	public void setTransportDocDate(String transportDocDate) {
		this.transportDocDate = transportDocDate;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getOriginalDocumentType() {
		return originalDocumentType;
	}

	public void setOriginalDocumentType(String originalDocumentType) {
		this.originalDocumentType = originalDocumentType;
	}

	public String getOriginalCustomerGSTIN() {
		return originalCustomerGSTIN;
	}

	public void setOriginalCustomerGSTIN(String originalCustomerGSTIN) {
		this.originalCustomerGSTIN = originalCustomerGSTIN;
	}

	public String getDifferentialPercentageFlag() {
		return differentialPercentageFlag;
	}

	public void setDifferentialPercentageFlag(
			String differentialPercentageFlag) {
		this.differentialPercentageFlag = differentialPercentageFlag;
	}

	public String getSec7ofIGSTFlag() {
		return sec7ofIGSTFlag;
	}

	public void setSec7ofIGSTFlag(String sec7ofIGSTFlag) {
		this.sec7ofIGSTFlag = sec7ofIGSTFlag;
	}

	public String getClaimRefndFlag() {
		return claimRefndFlag;
	}

	public void setClaimRefndFlag(String claimRefndFlag) {
		this.claimRefndFlag = claimRefndFlag;
	}

	public String getAutoPopltToRefund() {
		return autoPopltToRefund;
	}

	public void setAutoPopltToRefund(String autoPopltToRefund) {
		this.autoPopltToRefund = autoPopltToRefund;
	}

	public String getcRDRPreGST() {
		return cRDRPreGST;
	}

	public void setcRDRPreGST(String cRDRPreGST) {
		this.cRDRPreGST = cRDRPreGST;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCategoryOfProduct() {
		return categoryOfProduct;
	}

	public void setCategoryOfProduct(String categoryOfProduct) {
		this.categoryOfProduct = categoryOfProduct;
	}

	public String getiTCFlag() {
		return iTCFlag;
	}

	public void setiTCFlag(String iTCFlag) {
		this.iTCFlag = iTCFlag;
	}

	public String getStateApplyingCess() {
		return stateApplyingCess;
	}

	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}

	public String getfOB() {
		return fOB;
	}

	public void setfOB(String fOB) {
		this.fOB = fOB;
	}

	public String getExportDuty() {
		return exportDuty;
	}

	public void setExportDuty(String exportDuty) {
		this.exportDuty = exportDuty;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getReasonForCreditDebitNote() {
		return reasonForCreditDebitNote;
	}

	public void setReasonForCreditDebitNote(String reasonForCreditDebitNote) {
		this.reasonForCreditDebitNote = reasonForCreditDebitNote;
	}

	public String gettCSFlagGST() {
		return tCSFlagGST;
	}

	public void settCSFlagGST(String tCSFlagGST) {
		this.tCSFlagGST = tCSFlagGST;
	}

	public String gettCSIGSTAmount() {
		return tCSIGSTAmount;
	}

	public void settCSIGSTAmount(String tCSIGSTAmount) {
		this.tCSIGSTAmount = tCSIGSTAmount;
	}

	public String gettCSCGSTAmount() {
		return tCSCGSTAmount;
	}

	public void settCSCGSTAmount(String tCSCGSTAmount) {
		this.tCSCGSTAmount = tCSCGSTAmount;
	}

	public String gettCSSGSTAmount() {
		return tCSSGSTAmount;
	}

	public void settCSSGSTAmount(String tCSSGSTAmount) {
		this.tCSSGSTAmount = tCSSGSTAmount;
	}

	public String gettDSFlagGST() {
		return tDSFlagGST;
	}

	public void settDSFlagGST(String tDSFlagGST) {
		this.tDSFlagGST = tDSFlagGST;
	}

	public String gettDSIGSTAmount() {
		return tDSIGSTAmount;
	}

	public void settDSIGSTAmount(String tDSIGSTAmount) {
		this.tDSIGSTAmount = tDSIGSTAmount;
	}

	public String gettDSCGSTAmount() {
		return tDSCGSTAmount;
	}

	public void settDSCGSTAmount(String tDSCGSTAmount) {
		this.tDSCGSTAmount = tDSCGSTAmount;
	}

	public String gettDSSGSTAmount() {
		return tDSSGSTAmount;
	}

	public void settDSSGSTAmount(String tDSSGSTAmount) {
		this.tDSSGSTAmount = tDSSGSTAmount;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getSubDivision() {
		return subDivision;
	}

	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSalesOrganisation() {
		return salesOrganisation;
	}

	public void setSalesOrganisation(String salesOrganisation) {
		this.salesOrganisation = salesOrganisation;
	}

	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getProfitCentre1() {
		return profitCentre1;
	}

	public void setProfitCentre1(String profitCentre1) {
		this.profitCentre1 = profitCentre1;
	}

	public String getProfitCentre2() {
		return profitCentre2;
	}

	public void setProfitCentre2(String profitCentre2) {
		this.profitCentre2 = profitCentre2;
	}

	public String getProfitCentre3() {
		return profitCentre3;
	}

	public void setProfitCentre3(String profitCentre3) {
		this.profitCentre3 = profitCentre3;
	}

	public String getProfitCentre4() {
		return profitCentre4;
	}

	public void setProfitCentre4(String profitCentre4) {
		this.profitCentre4 = profitCentre4;
	}

	public String getProfitCentre5() {
		return profitCentre5;
	}

	public void setProfitCentre5(String profitCentre5) {
		this.profitCentre5 = profitCentre5;
	}

	public String getProfitCentre6() {
		return profitCentre6;
	}

	public void setProfitCentre6(String profitCentre6) {
		this.profitCentre6 = profitCentre6;
	}

	public String getProfitCentre7() {
		return profitCentre7;
	}

	public void setProfitCentre7(String profitCentre7) {
		this.profitCentre7 = profitCentre7;
	}

	public String getProfitCentre8() {
		return profitCentre8;
	}

	public void setProfitCentre8(String profitCentre8) {
		this.profitCentre8 = profitCentre8;
	}

	public String getGlAssessableValue() {
		return glAssessableValue;
	}

	public void setGlAssessableValue(String glAssessableValue) {
		this.glAssessableValue = glAssessableValue;
	}

	public String getGlIGST() {
		return glIGST;
	}

	public void setGlIGST(String glIGST) {
		this.glIGST = glIGST;
	}

	public String getGlCGST() {
		return glCGST;
	}

	public void setGlCGST(String glCGST) {
		this.glCGST = glCGST;
	}

	public String getGlSGST() {
		return glSGST;
	}

	public void setGlSGST(String glSGST) {
		this.glSGST = glSGST;
	}

	public String getGlAdvaloremCess() {
		return glAdvaloremCess;
	}

	public void setGlAdvaloremCess(String glAdvaloremCess) {
		this.glAdvaloremCess = glAdvaloremCess;
	}

	public String getGlSpecificCess() {
		return glSpecificCess;
	}

	public void setGlSpecificCess(String glSpecificCess) {
		this.glSpecificCess = glSpecificCess;
	}

	public String getgLStateCessAdvalorem() {
		return gLStateCessAdvalorem;
	}

	public void setgLStateCessAdvalorem(String gLStateCessAdvalorem) {
		this.gLStateCessAdvalorem = gLStateCessAdvalorem;
	}

	public String getgLStateCessSpecific() {
		return gLStateCessSpecific;
	}

	public void setgLStateCessSpecific(String gLStateCessSpecific) {
		this.gLStateCessSpecific = gLStateCessSpecific;
	}

	public String getGlPostingDate() {
		return glPostingDate;
	}

	public void setGlPostingDate(String glPostingDate) {
		this.glPostingDate = glPostingDate;
	}

	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	public String geteWBNumber() {
		return eWBNumber;
	}

	public void seteWBNumber(String eWBNumber) {
		this.eWBNumber = eWBNumber;
	}



	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	public String getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	public void setAccountingVoucherDate(String accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	public String getDocumentReferenceNumber() {
		return documentReferenceNumber;
	}

	public void setDocumentReferenceNumber(String documentReferenceNumber) {
		this.documentReferenceNumber = documentReferenceNumber;
	}

	public String getCustomerTAN() {
		return customerTAN;
	}

	public void setCustomerTAN(String customerTAN) {
		this.customerTAN = customerTAN;
	}

	public String getUserDefField1() {
		return userDefField1;
	}

	public void setUserDefField1(String userDefField1) {
		this.userDefField1 = userDefField1;
	}

	public String getUserDefField2() {
		return userDefField2;
	}

	public void setUserDefField2(String userDefField2) {
		this.userDefField2 = userDefField2;
	}

	public String getUserDefField3() {
		return userDefField3;
	}

	public void setUserDefField3(String userDefField3) {
		this.userDefField3 = userDefField3;
	}

	public String getUserDefField4() {
		return userDefField4;
	}

	public void setUserDefField4(String userDefField4) {
		this.userDefField4 = userDefField4;
	}

	public String getUserDefField5() {
		return userDefField5;
	}

	public void setUserDefField5(String userDefField5) {
		this.userDefField5 = userDefField5;
	}

	public String getUserDefField6() {
		return userDefField6;
	}

	public void setUserDefField6(String userDefField6) {
		this.userDefField6 = userDefField6;
	}

	public String getUserDefField7() {
		return userDefField7;
	}

	public void setUserDefField7(String userDefField7) {
		this.userDefField7 = userDefField7;
	}

	public String getUserDefField8() {
		return userDefField8;
	}

	public void setUserDefField8(String userDefField8) {
		this.userDefField8 = userDefField8;
	}

	public String getUserDefField9() {
		return userDefField9;
	}

	public void setUserDefField9(String userDefField9) {
		this.userDefField9 = userDefField9;
	}

	public String getUserDefField10() {
		return userDefField10;
	}

	public void setUserDefField10(String userDefField10) {
		this.userDefField10 = userDefField10;
	}

	public String getUserDefField11() {
		return userDefField11;
	}

	public void setUserDefField11(String userDefField11) {
		this.userDefField11 = userDefField11;
	}

	public String getUserDefField12() {
		return userDefField12;
	}

	public void setUserDefField12(String userDefField12) {
		this.userDefField12 = userDefField12;
	}

	public String getUserDefField13() {
		return userDefField13;
	}

	public void setUserDefField13(String userDefField13) {
		this.userDefField13 = userDefField13;
	}

	public String getUserDefField14() {
		return userDefField14;
	}

	public void setUserDefField14(String userDefField14) {
		this.userDefField14 = userDefField14;
	}

	public String getUserDefField15() {
		return userDefField15;
	}

	public void setUserDefField15(String userDefField15) {
		this.userDefField15 = userDefField15;
	}

	public String getUserDefField16() {
		return userDefField16;
	}

	public void setUserDefField16(String userDefField16) {
		this.userDefField16 = userDefField16;
	}

	public String getUserDefField17() {
		return userDefField17;
	}

	public void setUserDefField17(String userDefField17) {
		this.userDefField17 = userDefField17;
	}

	public String getUserDefField18() {
		return userDefField18;
	}

	public void setUserDefField18(String userDefField18) {
		this.userDefField18 = userDefField18;
	}

	public String getUserDefField19() {
		return userDefField19;
	}

	public void setUserDefField19(String userDefField19) {
		this.userDefField19 = userDefField19;
	}

	public String getUserDefField20() {
		return userDefField20;
	}

	public void setUserDefField20(String userDefField20) {
		this.userDefField20 = userDefField20;
	}

	public String getUserDefField21() {
		return userDefField21;
	}

	public void setUserDefField21(String userDefField21) {
		this.userDefField21 = userDefField21;
	}

	public String getUserDefField22() {
		return userDefField22;
	}

	public void setUserDefField22(String userDefField22) {
		this.userDefField22 = userDefField22;
	}

	public String getUserDefField23() {
		return userDefField23;
	}

	public void setUserDefField23(String userDefField23) {
		this.userDefField23 = userDefField23;
	}

	public String getUserDefField24() {
		return userDefField24;
	}

	public void setUserDefField24(String userDefField24) {
		this.userDefField24 = userDefField24;
	}

	public String getUserDefField25() {
		return userDefField25;
	}

	public void setUserDefField25(String userDefField25) {
		this.userDefField25 = userDefField25;
	}

	public String getUserDefField26() {
		return userDefField26;
	}

	public void setUserDefField26(String userDefField26) {
		this.userDefField26 = userDefField26;
	}

	public String getUserDefField27() {
		return userDefField27;
	}

	public void setUserDefField27(String userDefField27) {
		this.userDefField27 = userDefField27;
	}

	public String getUserDefField28() {
		return userDefField28;
	}

	public void setUserDefField28(String userDefField28) {
		this.userDefField28 = userDefField28;
	}

	public String getUserDefField29() {
		return userDefField29;
	}

	public void setUserDefField29(String userDefField29) {
		this.userDefField29 = userDefField29;
	}

	public String getUserDefField30() {
		return userDefField30;
	}

	public void setUserDefField30(String userDefField30) {
		this.userDefField30 = userDefField30;
	}

	public String getSupplyTypeASP() {
		return supplyTypeASP;
	}

	public void setSupplyTypeASP(String supplyTypeASP) {
		this.supplyTypeASP = supplyTypeASP;
	}

	public String getApproximateDistanceASP() {
		return approximateDistanceASP;
	}

	public void setApproximateDistanceASP(String approximateDistanceASP) {
		this.approximateDistanceASP = approximateDistanceASP;
	}

	public String getDistanceSavedtoEWB() {
		return distanceSavedtoEWB;
	}

	public void setDistanceSavedtoEWB(String distanceSavedtoEWB) {
		this.distanceSavedtoEWB = distanceSavedtoEWB;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getInvoiceOtherChargesASP() {
		return invoiceOtherChargesASP;
	}

	public void setInvoiceOtherChargesASP(String invoiceOtherChargesASP) {
		this.invoiceOtherChargesASP = invoiceOtherChargesASP;
	}

	public String getInvoiceAssessableAmountASP() {
		return invoiceAssessableAmountASP;
	}

	public void setInvoiceAssessableAmountASP(
			String invoiceAssessableAmountASP) {
		this.invoiceAssessableAmountASP = invoiceAssessableAmountASP;
	}

	public String getInvoiceIGSTAmountASP() {
		return invoiceIGSTAmountASP;
	}

	public void setInvoiceIGSTAmountASP(String invoiceIGSTAmountASP) {
		this.invoiceIGSTAmountASP = invoiceIGSTAmountASP;
	}

	public String getInvoiceCGSTAmountASP() {
		return invoiceCGSTAmountASP;
	}

	public void setInvoiceCGSTAmountASP(String invoiceCGSTAmountASP) {
		this.invoiceCGSTAmountASP = invoiceCGSTAmountASP;
	}

	public String getInvoiceSGSTAmountASP() {
		return invoiceSGSTAmountASP;
	}

	public void setInvoiceSGSTAmountASP(String invoiceSGSTAmountASP) {
		this.invoiceSGSTAmountASP = invoiceSGSTAmountASP;
	}

	public String getInvoiceCessAdvaloremAmountASP() {
		return invoiceCessAdvaloremAmountASP;
	}

	public void setInvoiceCessAdvaloremAmountASP(
			String invoiceCessAdvaloremAmountASP) {
		this.invoiceCessAdvaloremAmountASP = invoiceCessAdvaloremAmountASP;
	}

	public String getInvoiceCessSpecificAmountASP() {
		return invoiceCessSpecificAmountASP;
	}

	public void setInvoiceCessSpecificAmountASP(
			String invoiceCessSpecificAmountASP) {
		this.invoiceCessSpecificAmountASP = invoiceCessSpecificAmountASP;
	}

	public String getInvoiceStateCessAdvaloremAmountASP() {
		return invoiceStateCessAdvaloremAmountASP;
	}

	public void setInvoiceStateCessAdvaloremAmountASP(
			String invoiceStateCessAdvaloremAmountASP) {
		this.invoiceStateCessAdvaloremAmountASP = invoiceStateCessAdvaloremAmountASP;
	}

	public String getInvoiceStateCessSpecificAmountASP() {
		return invoiceStateCessSpecificAmountASP;
	}

	public void setInvoiceStateCessSpecificAmountASP(
			String invoiceStateCessSpecificAmountASP) {
		this.invoiceStateCessSpecificAmountASP = invoiceStateCessSpecificAmountASP;
	}

	public String getInvoiceValueASP() {
		return invoiceValueASP;
	}

	public void setInvoiceValueASP(String invoiceValueASP) {
		this.invoiceValueASP = invoiceValueASP;
	}

	public String getIntegratedTaxAmountASP() {
		return integratedTaxAmountASP;
	}

	public void setIntegratedTaxAmountASP(String integratedTaxAmountASP) {
		this.integratedTaxAmountASP = integratedTaxAmountASP;
	}

	public String getCentralTaxAmountASP() {
		return centralTaxAmountASP;
	}

	public void setCentralTaxAmountASP(String centralTaxAmountASP) {
		this.centralTaxAmountASP = centralTaxAmountASP;
	}

	public String getStateUTTaxAmountASP() {
		return stateUTTaxAmountASP;
	}

	public void setStateUTTaxAmountASP(String stateUTTaxAmountASP) {
		this.stateUTTaxAmountASP = stateUTTaxAmountASP;
	}

	public String getCessAdvaloremAmountASP() {
		return cessAdvaloremAmountASP;
	}

	public void setCessAdvaloremAmountASP(String cessAdvaloremAmountASP) {
		this.cessAdvaloremAmountASP = cessAdvaloremAmountASP;
	}

	public String getStateCessAdvaloremAmountASP() {
		return stateCessAdvaloremAmountASP;
	}

	public void setStateCessAdvaloremAmountASP(
			String stateCessAdvaloremAmountASP) {
		this.stateCessAdvaloremAmountASP = stateCessAdvaloremAmountASP;
	}

	public String getIntegratedTaxAmountRET1Impact() {
		return integratedTaxAmountRET1Impact;
	}

	public void setIntegratedTaxAmountRET1Impact(
			String integratedTaxAmountRET1Impact) {
		this.integratedTaxAmountRET1Impact = integratedTaxAmountRET1Impact;
	}

	public String getCentralTaxAmountRET1Impact() {
		return centralTaxAmountRET1Impact;
	}

	public void setCentralTaxAmountRET1Impact(
			String centralTaxAmountRET1Impact) {
		this.centralTaxAmountRET1Impact = centralTaxAmountRET1Impact;
	}

	public String getStateUTTaxAmountRET1Impact() {
		return stateUTTaxAmountRET1Impact;
	}

	public void setStateUTTaxAmountRET1Impact(
			String stateUTTaxAmountRET1Impact) {
		this.stateUTTaxAmountRET1Impact = stateUTTaxAmountRET1Impact;
	}

	public String getCessAdvaloremAmountDifference() {
		return cessAdvaloremAmountDifference;
	}

	public void setCessAdvaloremAmountDifference(
			String cessAdvaloremAmountDifference) {
		this.cessAdvaloremAmountDifference = cessAdvaloremAmountDifference;
	}

	public String getStateCessAdvaloremAmountDifference() {
		return stateCessAdvaloremAmountDifference;
	}

	public void setStateCessAdvaloremAmountDifference(
			String stateCessAdvaloremAmountDifference) {
		this.stateCessAdvaloremAmountDifference = stateCessAdvaloremAmountDifference;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getDigiGSTStatus() {
		return digiGSTStatus;
	}

	public void setDigiGSTStatus(String digiGSTStatus) {
		this.digiGSTStatus = digiGSTStatus;
	}

	public String getErrorPoint() {
		return errorPoint;
	}

	public void setErrorPoint(String errorPoint) {
		this.errorPoint = errorPoint;
	}

	public String getIrnAcknowledgmentDate() {
		return irnAcknowledgmentDate;
	}

	public void setIrnAcknowledgmentDate(String irnAcknowledgmentDate) {
		this.irnAcknowledgmentDate = irnAcknowledgmentDate;
	}

	public String getIrnAcknowledgmentTime() {
		return irnAcknowledgmentTime;
	}

	public void setIrnAcknowledgmentTime(String irnAcknowledgmentTime) {
		this.irnAcknowledgmentTime = irnAcknowledgmentTime;
	}

	public String geteWBDate() {
		return eWBDate;
	}

	public void seteWBDate(String eWBDate) {
		this.eWBDate = eWBDate;
	}

	public String getIrnCancellationDate() {
		return irnCancellationDate;
	}

	public void setIrnCancellationDate(String irnCancellationDate) {
		this.irnCancellationDate = irnCancellationDate;
	}

	public String getIrnCancellationTime() {
		return irnCancellationTime;
	}

	public void setIrnCancellationTime(String irnCancellationTime) {
		this.irnCancellationTime = irnCancellationTime;
	}

	


	public String getEwbTime() {
		return ewbTime;
	}

	public void setEwbTime(String ewbTime) {
		this.ewbTime = ewbTime;
	}

	public String getEwbValiduptoTime() {
		return ewbValiduptoTime;
	}

	public void setEwbValiduptoTime(String ewbValiduptoTime) {
		this.ewbValiduptoTime = ewbValiduptoTime;
	}

	public String geteWBCancellationDate() {
		return eWBCancellationDate;
	}

	public void seteWBCancellationDate(String eWBCancellationDate) {
		this.eWBCancellationDate = eWBCancellationDate;
	}

	public String geteWBCancellationTime() {
		return eWBCancellationTime;
	}

	public void seteWBCancellationTime(String eWBCancellationTime) {
		this.eWBCancellationTime = eWBCancellationTime;
	}

	public String getGstnRefidDate() {
		return gstnRefidDate;
	}

	public void setGstnRefidDate(String gstnRefidDate) {
		this.gstnRefidDate = gstnRefidDate;
	}

	public String getGstnRefidTime() {
		return gstnRefidTime;
	}

	public void setGstnRefidTime(String gstnRefidTime) {
		this.gstnRefidTime = gstnRefidTime;
	}

	public String getTableNumberGSTR3B() {
		return tableNumberGSTR3B;
	}

	public void setTableNumberGSTR3B(String tableNumberGSTR3B) {
		this.tableNumberGSTR3B = tableNumberGSTR3B;
	}

	

}
