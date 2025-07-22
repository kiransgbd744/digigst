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
public class ReversalInwardDto {
	
	
	private String category;
	private String irn;
	private String irnDate;
	private String taxScheme;
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
	private String eligibilityIndicator;
	private String commonSupplyIndicator;
	private String availableIgst;
	private String availableCgst;
	private String availableSgst;
	private String availableCess;
	private String iTCEntitlement;
	private String iTCReversalIdentifier;
	private String tCSFlagIncomeTax;
	private String tCSRateIncomeTax;
	private String tCSAmountIncomeTax;
	private String currencyCode;
	private String countryCode;
	private String invoiceValueFC;
	private String portCode;
	private String billofEntry;
	private String billofEntryDate;
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
	private String originalSupplierGSTIN;
	private String differentialPercentageFlag;
	private String sec7ofIGSTFlag;
	private String claimRefndFlag;
	private String autoPopltToRefund;
	private String cRDRPreGST;
	private String supplierType;
	private String supplierCode;
	private String productCode;
	private String categoryOfProduct;
	private String stateApplyingCess;
	private String cif;
	private String customDuty;
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
	private String purchaseOrganisation;
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
	private String purchaseOrderValue;
	private String eWBNumber;
	private String eWBDate;
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String documentReferenceNumber;
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
	private String ItcAvailableIGST;
	private String ItcAvailableCGST;
	private String ItcAvailableSGST;
	private String ItcAvailableCess;
	private BigDecimal Ratio1IGST = BigDecimal.ZERO;
	private BigDecimal Ratio1CGST = BigDecimal.ZERO;
	private BigDecimal Ratio1SGST = BigDecimal.ZERO;
	private BigDecimal Ratio1Cess = BigDecimal.ZERO;
	private BigDecimal Ratio2IGST = BigDecimal.ZERO;
	private BigDecimal Ratio2CGST = BigDecimal.ZERO;
	private BigDecimal Ratio2SGST = BigDecimal.ZERO;
	private BigDecimal Ratio2Cess = BigDecimal.ZERO;
	private BigDecimal Ratio3IGST = BigDecimal.ZERO;
	private BigDecimal Ratio3CGST = BigDecimal.ZERO;
	private BigDecimal Ratio3SGST = BigDecimal.ZERO;
	private BigDecimal Ratio3Cess = BigDecimal.ZERO;
	private String commonSupplyIndicatorUpdated;
	private String updatedCommonSupplyIndicator;
	private String originalCommonSupplyIndicator;
	private String returnPeriodUpdated;
	private String updatedReturnPeriod;
	private String originalReturnPeriod;
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the irn
	 */
	public String getIrn() {
		return irn;
	}
	/**
	 * @param irn the irn to set
	 */
	public void setIrn(String irn) {
		this.irn = irn;
	}
	/**
	 * @return the irnDate
	 */
	public String getIrnDate() {
		return irnDate;
	}
	/**
	 * @param irnDate the irnDate to set
	 */
	public void setIrnDate(String irnDate) {
		this.irnDate = irnDate;
	}
	/**
	 * @return the taxScheme
	 */
	public String getTaxScheme() {
		return taxScheme;
	}
	/**
	 * @param taxScheme the taxScheme to set
	 */
	public void setTaxScheme(String taxScheme) {
		this.taxScheme = taxScheme;
	}
	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}
	/**
	 * @param supplyType the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	/**
	 * @return the docCategory
	 */
	public String getDocCategory() {
		return docCategory;
	}
	/**
	 * @param docCategory the docCategory to set
	 */
	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the documentNumber
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}
	/**
	 * @param documentNumber the documentNumber to set
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	/**
	 * @return the documentDate
	 */
	public String getDocumentDate() {
		return documentDate;
	}
	/**
	 * @param documentDate the documentDate to set
	 */
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	/**
	 * @return the reverseChargeFlag
	 */
	public String getReverseChargeFlag() {
		return reverseChargeFlag;
	}
	/**
	 * @param reverseChargeFlag the reverseChargeFlag to set
	 */
	public void setReverseChargeFlag(String reverseChargeFlag) {
		this.reverseChargeFlag = reverseChargeFlag;
	}
	/**
	 * @return the supplierGSTIN
	 */
	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}
	/**
	 * @param supplierGSTIN the supplierGSTIN to set
	 */
	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}
	/**
	 * @return the supplierTradeName
	 */
	public String getSupplierTradeName() {
		return supplierTradeName;
	}
	/**
	 * @param supplierTradeName the supplierTradeName to set
	 */
	public void setSupplierTradeName(String supplierTradeName) {
		this.supplierTradeName = supplierTradeName;
	}
	/**
	 * @return the supplierLegalName
	 */
	public String getSupplierLegalName() {
		return supplierLegalName;
	}
	/**
	 * @param supplierLegalName the supplierLegalName to set
	 */
	public void setSupplierLegalName(String supplierLegalName) {
		this.supplierLegalName = supplierLegalName;
	}
	/**
	 * @return the supplierAddress1
	 */
	public String getSupplierAddress1() {
		return supplierAddress1;
	}
	/**
	 * @param supplierAddress1 the supplierAddress1 to set
	 */
	public void setSupplierAddress1(String supplierAddress1) {
		this.supplierAddress1 = supplierAddress1;
	}
	/**
	 * @return the supplierAddress2
	 */
	public String getSupplierAddress2() {
		return supplierAddress2;
	}
	/**
	 * @param supplierAddress2 the supplierAddress2 to set
	 */
	public void setSupplierAddress2(String supplierAddress2) {
		this.supplierAddress2 = supplierAddress2;
	}
	/**
	 * @return the supplierLocation
	 */
	public String getSupplierLocation() {
		return supplierLocation;
	}
	/**
	 * @param supplierLocation the supplierLocation to set
	 */
	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}
	/**
	 * @return the supplierPincode
	 */
	public String getSupplierPincode() {
		return supplierPincode;
	}
	/**
	 * @param supplierPincode the supplierPincode to set
	 */
	public void setSupplierPincode(String supplierPincode) {
		this.supplierPincode = supplierPincode;
	}
	/**
	 * @return the supplierStateCode
	 */
	public String getSupplierStateCode() {
		return supplierStateCode;
	}
	/**
	 * @param supplierStateCode the supplierStateCode to set
	 */
	public void setSupplierStateCode(String supplierStateCode) {
		this.supplierStateCode = supplierStateCode;
	}
	/**
	 * @return the supplierPhone
	 */
	public String getSupplierPhone() {
		return supplierPhone;
	}
	/**
	 * @param supplierPhone the supplierPhone to set
	 */
	public void setSupplierPhone(String supplierPhone) {
		this.supplierPhone = supplierPhone;
	}
	/**
	 * @return the supplierEmail
	 */
	public String getSupplierEmail() {
		return supplierEmail;
	}
	/**
	 * @param supplierEmail the supplierEmail to set
	 */
	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}
	/**
	 * @return the customerGSTIN
	 */
	public String getCustomerGSTIN() {
		return customerGSTIN;
	}
	/**
	 * @param customerGSTIN the customerGSTIN to set
	 */
	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}
	/**
	 * @return the customerTradeName
	 */
	public String getCustomerTradeName() {
		return customerTradeName;
	}
	/**
	 * @param customerTradeName the customerTradeName to set
	 */
	public void setCustomerTradeName(String customerTradeName) {
		this.customerTradeName = customerTradeName;
	}
	/**
	 * @return the customerLegalName
	 */
	public String getCustomerLegalName() {
		return customerLegalName;
	}
	/**
	 * @param customerLegalName the customerLegalName to set
	 */
	public void setCustomerLegalName(String customerLegalName) {
		this.customerLegalName = customerLegalName;
	}
	/**
	 * @return the customerAddress1
	 */
	public String getCustomerAddress1() {
		return customerAddress1;
	}
	/**
	 * @param customerAddress1 the customerAddress1 to set
	 */
	public void setCustomerAddress1(String customerAddress1) {
		this.customerAddress1 = customerAddress1;
	}
	/**
	 * @return the customerAddress2
	 */
	public String getCustomerAddress2() {
		return customerAddress2;
	}
	/**
	 * @param customerAddress2 the customerAddress2 to set
	 */
	public void setCustomerAddress2(String customerAddress2) {
		this.customerAddress2 = customerAddress2;
	}
	/**
	 * @return the customerLocation
	 */
	public String getCustomerLocation() {
		return customerLocation;
	}
	/**
	 * @param customerLocation the customerLocation to set
	 */
	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}
	/**
	 * @return the customerPincode
	 */
	public String getCustomerPincode() {
		return customerPincode;
	}
	/**
	 * @param customerPincode the customerPincode to set
	 */
	public void setCustomerPincode(String customerPincode) {
		this.customerPincode = customerPincode;
	}
	/**
	 * @return the customerStateCode
	 */
	public String getCustomerStateCode() {
		return customerStateCode;
	}
	/**
	 * @param customerStateCode the customerStateCode to set
	 */
	public void setCustomerStateCode(String customerStateCode) {
		this.customerStateCode = customerStateCode;
	}
	/**
	 * @return the billingPOS
	 */
	public String getBillingPOS() {
		return billingPOS;
	}
	/**
	 * @param billingPOS the billingPOS to set
	 */
	public void setBillingPOS(String billingPOS) {
		this.billingPOS = billingPOS;
	}
	/**
	 * @return the customerPhone
	 */
	public String getCustomerPhone() {
		return customerPhone;
	}
	/**
	 * @param customerPhone the customerPhone to set
	 */
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}
	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	/**
	 * @return the dispatcherGSTIN
	 */
	public String getDispatcherGSTIN() {
		return dispatcherGSTIN;
	}
	/**
	 * @param dispatcherGSTIN the dispatcherGSTIN to set
	 */
	public void setDispatcherGSTIN(String dispatcherGSTIN) {
		this.dispatcherGSTIN = dispatcherGSTIN;
	}
	/**
	 * @return the dispatcherTradeName
	 */
	public String getDispatcherTradeName() {
		return dispatcherTradeName;
	}
	/**
	 * @param dispatcherTradeName the dispatcherTradeName to set
	 */
	public void setDispatcherTradeName(String dispatcherTradeName) {
		this.dispatcherTradeName = dispatcherTradeName;
	}
	/**
	 * @return the dispatcherAddress1
	 */
	public String getDispatcherAddress1() {
		return dispatcherAddress1;
	}
	/**
	 * @param dispatcherAddress1 the dispatcherAddress1 to set
	 */
	public void setDispatcherAddress1(String dispatcherAddress1) {
		this.dispatcherAddress1 = dispatcherAddress1;
	}
	/**
	 * @return the dispatcherAddress2
	 */
	public String getDispatcherAddress2() {
		return dispatcherAddress2;
	}
	/**
	 * @param dispatcherAddress2 the dispatcherAddress2 to set
	 */
	public void setDispatcherAddress2(String dispatcherAddress2) {
		this.dispatcherAddress2 = dispatcherAddress2;
	}
	/**
	 * @return the dispatcherLocation
	 */
	public String getDispatcherLocation() {
		return dispatcherLocation;
	}
	/**
	 * @param dispatcherLocation the dispatcherLocation to set
	 */
	public void setDispatcherLocation(String dispatcherLocation) {
		this.dispatcherLocation = dispatcherLocation;
	}
	/**
	 * @return the dispatcherPincode
	 */
	public String getDispatcherPincode() {
		return dispatcherPincode;
	}
	/**
	 * @param dispatcherPincode the dispatcherPincode to set
	 */
	public void setDispatcherPincode(String dispatcherPincode) {
		this.dispatcherPincode = dispatcherPincode;
	}
	/**
	 * @return the dispatcherStateCode
	 */
	public String getDispatcherStateCode() {
		return dispatcherStateCode;
	}
	/**
	 * @param dispatcherStateCode the dispatcherStateCode to set
	 */
	public void setDispatcherStateCode(String dispatcherStateCode) {
		this.dispatcherStateCode = dispatcherStateCode;
	}
	/**
	 * @return the shipToGSTIN
	 */
	public String getShipToGSTIN() {
		return shipToGSTIN;
	}
	/**
	 * @param shipToGSTIN the shipToGSTIN to set
	 */
	public void setShipToGSTIN(String shipToGSTIN) {
		this.shipToGSTIN = shipToGSTIN;
	}
	/**
	 * @return the shipToTradeName
	 */
	public String getShipToTradeName() {
		return shipToTradeName;
	}
	/**
	 * @param shipToTradeName the shipToTradeName to set
	 */
	public void setShipToTradeName(String shipToTradeName) {
		this.shipToTradeName = shipToTradeName;
	}
	/**
	 * @return the shipToLegalName
	 */
	public String getShipToLegalName() {
		return shipToLegalName;
	}
	/**
	 * @param shipToLegalName the shipToLegalName to set
	 */
	public void setShipToLegalName(String shipToLegalName) {
		this.shipToLegalName = shipToLegalName;
	}
	/**
	 * @return the shipToAddress1
	 */
	public String getShipToAddress1() {
		return shipToAddress1;
	}
	/**
	 * @param shipToAddress1 the shipToAddress1 to set
	 */
	public void setShipToAddress1(String shipToAddress1) {
		this.shipToAddress1 = shipToAddress1;
	}
	/**
	 * @return the shipToAddress2
	 */
	public String getShipToAddress2() {
		return shipToAddress2;
	}
	/**
	 * @param shipToAddress2 the shipToAddress2 to set
	 */
	public void setShipToAddress2(String shipToAddress2) {
		this.shipToAddress2 = shipToAddress2;
	}
	/**
	 * @return the shipToLocation
	 */
	public String getShipToLocation() {
		return shipToLocation;
	}
	/**
	 * @param shipToLocation the shipToLocation to set
	 */
	public void setShipToLocation(String shipToLocation) {
		this.shipToLocation = shipToLocation;
	}
	/**
	 * @return the shipToPincode
	 */
	public String getShipToPincode() {
		return shipToPincode;
	}
	/**
	 * @param shipToPincode the shipToPincode to set
	 */
	public void setShipToPincode(String shipToPincode) {
		this.shipToPincode = shipToPincode;
	}
	/**
	 * @return the shipToStateCode
	 */
	public String getShipToStateCode() {
		return shipToStateCode;
	}
	/**
	 * @param shipToStateCode the shipToStateCode to set
	 */
	public void setShipToStateCode(String shipToStateCode) {
		this.shipToStateCode = shipToStateCode;
	}
	/**
	 * @return the itemSerialNumber
	 */
	public String getItemSerialNumber() {
		return itemSerialNumber;
	}
	/**
	 * @param itemSerialNumber the itemSerialNumber to set
	 */
	public void setItemSerialNumber(String itemSerialNumber) {
		this.itemSerialNumber = itemSerialNumber;
	}
	/**
	 * @return the productSerialNumber
	 */
	public String getProductSerialNumber() {
		return productSerialNumber;
	}
	/**
	 * @param productSerialNumber the productSerialNumber to set
	 */
	public void setProductSerialNumber(String productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the productDescription
	 */
	public String getProductDescription() {
		return productDescription;
	}
	/**
	 * @param productDescription the productDescription to set
	 */
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	/**
	 * @return the isService
	 */
	public String getIsService() {
		return isService;
	}
	/**
	 * @param isService the isService to set
	 */
	public void setIsService(String isService) {
		this.isService = isService;
	}
	/**
	 * @return the hsn
	 */
	public String getHsn() {
		return hsn;
	}
	/**
	 * @param hsn the hsn to set
	 */
	public void setHsn(String hsn) {
		this.hsn = hsn;
	}
	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * @return the batchName
	 */
	public String getBatchName() {
		return batchName;
	}
	/**
	 * @param batchName the batchName to set
	 */
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	/**
	 * @return the batchExpiryDate
	 */
	public String getBatchExpiryDate() {
		return batchExpiryDate;
	}
	/**
	 * @param batchExpiryDate the batchExpiryDate to set
	 */
	public void setBatchExpiryDate(String batchExpiryDate) {
		this.batchExpiryDate = batchExpiryDate;
	}
	/**
	 * @return the warrantyDate
	 */
	public String getWarrantyDate() {
		return warrantyDate;
	}
	/**
	 * @param warrantyDate the warrantyDate to set
	 */
	public void setWarrantyDate(String warrantyDate) {
		this.warrantyDate = warrantyDate;
	}
	/**
	 * @return the orderlineReference
	 */
	public String getOrderlineReference() {
		return orderlineReference;
	}
	/**
	 * @param orderlineReference the orderlineReference to set
	 */
	public void setOrderlineReference(String orderlineReference) {
		this.orderlineReference = orderlineReference;
	}
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return attributeValue;
	}
	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	/**
	 * @return the originCountry
	 */
	public String getOriginCountry() {
		return originCountry;
	}
	/**
	 * @param originCountry the originCountry to set
	 */
	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}
	/**
	 * @return the uQC
	 */
	public String getuQC() {
		return uQC;
	}
	/**
	 * @param uQC the uQC to set
	 */
	public void setuQC(String uQC) {
		this.uQC = uQC;
	}
	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return Quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}
	/**
	 * @return the freeQuantity
	 */
	public String getFreeQuantity() {
		return freeQuantity;
	}
	/**
	 * @param freeQuantity the freeQuantity to set
	 */
	public void setFreeQuantity(String freeQuantity) {
		this.freeQuantity = freeQuantity;
	}
	/**
	 * @return the unitPrice
	 */
	public String getUnitPrice() {
		return unitPrice;
	}
	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	/**
	 * @return the itemAmount
	 */
	public String getItemAmount() {
		return itemAmount;
	}
	/**
	 * @param itemAmount the itemAmount to set
	 */
	public void setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
	}
	/**
	 * @return the itemDiscount
	 */
	public String getItemDiscount() {
		return itemDiscount;
	}
	/**
	 * @param itemDiscount the itemDiscount to set
	 */
	public void setItemDiscount(String itemDiscount) {
		this.itemDiscount = itemDiscount;
	}
	/**
	 * @return the preTaxAmount
	 */
	public String getPreTaxAmount() {
		return preTaxAmount;
	}
	/**
	 * @param preTaxAmount the preTaxAmount to set
	 */
	public void setPreTaxAmount(String preTaxAmount) {
		this.preTaxAmount = preTaxAmount;
	}
	/**
	 * @return the itemAssessableAmount
	 */
	public String getItemAssessableAmount() {
		return itemAssessableAmount;
	}
	/**
	 * @param itemAssessableAmount the itemAssessableAmount to set
	 */
	public void setItemAssessableAmount(String itemAssessableAmount) {
		this.itemAssessableAmount = itemAssessableAmount;
	}
	/**
	 * @return the iGSTRate
	 */
	public String getiGSTRate() {
		return iGSTRate;
	}
	/**
	 * @param iGSTRate the iGSTRate to set
	 */
	public void setiGSTRate(String iGSTRate) {
		this.iGSTRate = iGSTRate;
	}
	/**
	 * @return the iGSTAmount
	 */
	public String getiGSTAmount() {
		return iGSTAmount;
	}
	/**
	 * @param iGSTAmount the iGSTAmount to set
	 */
	public void setiGSTAmount(String iGSTAmount) {
		this.iGSTAmount = iGSTAmount;
	}
	/**
	 * @return the cGSTRate
	 */
	public String getcGSTRate() {
		return cGSTRate;
	}
	/**
	 * @param cGSTRate the cGSTRate to set
	 */
	public void setcGSTRate(String cGSTRate) {
		this.cGSTRate = cGSTRate;
	}
	/**
	 * @return the cGSTAmount
	 */
	public String getcGSTAmount() {
		return cGSTAmount;
	}
	/**
	 * @param cGSTAmount the cGSTAmount to set
	 */
	public void setcGSTAmount(String cGSTAmount) {
		this.cGSTAmount = cGSTAmount;
	}
	/**
	 * @return the sGSTRate
	 */
	public String getsGSTRate() {
		return sGSTRate;
	}
	/**
	 * @param sGSTRate the sGSTRate to set
	 */
	public void setsGSTRate(String sGSTRate) {
		this.sGSTRate = sGSTRate;
	}
	/**
	 * @return the sGSTAmount
	 */
	public String getsGSTAmount() {
		return sGSTAmount;
	}
	/**
	 * @param sGSTAmount the sGSTAmount to set
	 */
	public void setsGSTAmount(String sGSTAmount) {
		this.sGSTAmount = sGSTAmount;
	}
	/**
	 * @return the cessAdvaloremRate
	 */
	public String getCessAdvaloremRate() {
		return cessAdvaloremRate;
	}
	/**
	 * @param cessAdvaloremRate the cessAdvaloremRate to set
	 */
	public void setCessAdvaloremRate(String cessAdvaloremRate) {
		this.cessAdvaloremRate = cessAdvaloremRate;
	}
	/**
	 * @return the cessAdvaloremAmount
	 */
	public String getCessAdvaloremAmount() {
		return cessAdvaloremAmount;
	}
	/**
	 * @param cessAdvaloremAmount the cessAdvaloremAmount to set
	 */
	public void setCessAdvaloremAmount(String cessAdvaloremAmount) {
		this.cessAdvaloremAmount = cessAdvaloremAmount;
	}
	/**
	 * @return the cessSpecificRate
	 */
	public String getCessSpecificRate() {
		return cessSpecificRate;
	}
	/**
	 * @param cessSpecificRate the cessSpecificRate to set
	 */
	public void setCessSpecificRate(String cessSpecificRate) {
		this.cessSpecificRate = cessSpecificRate;
	}
	/**
	 * @return the cessSpecificAmount
	 */
	public String getCessSpecificAmount() {
		return cessSpecificAmount;
	}
	/**
	 * @param cessSpecificAmount the cessSpecificAmount to set
	 */
	public void setCessSpecificAmount(String cessSpecificAmount) {
		this.cessSpecificAmount = cessSpecificAmount;
	}
	/**
	 * @return the stateCessAdvaloremRate
	 */
	public String getStateCessAdvaloremRate() {
		return stateCessAdvaloremRate;
	}
	/**
	 * @param stateCessAdvaloremRate the stateCessAdvaloremRate to set
	 */
	public void setStateCessAdvaloremRate(String stateCessAdvaloremRate) {
		this.stateCessAdvaloremRate = stateCessAdvaloremRate;
	}
	/**
	 * @return the stateCessAdvaloremAmount
	 */
	public String getStateCessAdvaloremAmount() {
		return stateCessAdvaloremAmount;
	}
	/**
	 * @param stateCessAdvaloremAmount the stateCessAdvaloremAmount to set
	 */
	public void setStateCessAdvaloremAmount(String stateCessAdvaloremAmount) {
		this.stateCessAdvaloremAmount = stateCessAdvaloremAmount;
	}
	/**
	 * @return the stateCessSpecificRate
	 */
	public String getStateCessSpecificRate() {
		return stateCessSpecificRate;
	}
	/**
	 * @param stateCessSpecificRate the stateCessSpecificRate to set
	 */
	public void setStateCessSpecificRate(String stateCessSpecificRate) {
		this.stateCessSpecificRate = stateCessSpecificRate;
	}
	/**
	 * @return the stateCessSpecificAmount
	 */
	public String getStateCessSpecificAmount() {
		return stateCessSpecificAmount;
	}
	/**
	 * @param stateCessSpecificAmount the stateCessSpecificAmount to set
	 */
	public void setStateCessSpecificAmount(String stateCessSpecificAmount) {
		this.stateCessSpecificAmount = stateCessSpecificAmount;
	}
	/**
	 * @return the itemOtherCharges
	 */
	public String getItemOtherCharges() {
		return itemOtherCharges;
	}
	/**
	 * @param itemOtherCharges the itemOtherCharges to set
	 */
	public void setItemOtherCharges(String itemOtherCharges) {
		this.itemOtherCharges = itemOtherCharges;
	}
	/**
	 * @return the totalItemAmount
	 */
	public String getTotalItemAmount() {
		return totalItemAmount;
	}
	/**
	 * @param totalItemAmount the totalItemAmount to set
	 */
	public void setTotalItemAmount(String totalItemAmount) {
		this.totalItemAmount = totalItemAmount;
	}
	/**
	 * @return the invoiceOtherCharges
	 */
	public String getInvoiceOtherCharges() {
		return invoiceOtherCharges;
	}
	/**
	 * @param invoiceOtherCharges the invoiceOtherCharges to set
	 */
	public void setInvoiceOtherCharges(String invoiceOtherCharges) {
		this.invoiceOtherCharges = invoiceOtherCharges;
	}
	/**
	 * @return the invoiceAssessableAmount
	 */
	public String getInvoiceAssessableAmount() {
		return invoiceAssessableAmount;
	}
	/**
	 * @param invoiceAssessableAmount the invoiceAssessableAmount to set
	 */
	public void setInvoiceAssessableAmount(String invoiceAssessableAmount) {
		this.invoiceAssessableAmount = invoiceAssessableAmount;
	}
	/**
	 * @return the invoiceIGSTAmount
	 */
	public String getInvoiceIGSTAmount() {
		return invoiceIGSTAmount;
	}
	/**
	 * @param invoiceIGSTAmount the invoiceIGSTAmount to set
	 */
	public void setInvoiceIGSTAmount(String invoiceIGSTAmount) {
		this.invoiceIGSTAmount = invoiceIGSTAmount;
	}
	/**
	 * @return the invoiceCGSTAmount
	 */
	public String getInvoiceCGSTAmount() {
		return invoiceCGSTAmount;
	}
	/**
	 * @param invoiceCGSTAmount the invoiceCGSTAmount to set
	 */
	public void setInvoiceCGSTAmount(String invoiceCGSTAmount) {
		this.invoiceCGSTAmount = invoiceCGSTAmount;
	}
	/**
	 * @return the invoiceSGSTAmount
	 */
	public String getInvoiceSGSTAmount() {
		return invoiceSGSTAmount;
	}
	/**
	 * @param invoiceSGSTAmount the invoiceSGSTAmount to set
	 */
	public void setInvoiceSGSTAmount(String invoiceSGSTAmount) {
		this.invoiceSGSTAmount = invoiceSGSTAmount;
	}
	/**
	 * @return the invoiceCessAdvaloremAmount
	 */
	public String getInvoiceCessAdvaloremAmount() {
		return invoiceCessAdvaloremAmount;
	}
	/**
	 * @param invoiceCessAdvaloremAmount the invoiceCessAdvaloremAmount to set
	 */
	public void setInvoiceCessAdvaloremAmount(String invoiceCessAdvaloremAmount) {
		this.invoiceCessAdvaloremAmount = invoiceCessAdvaloremAmount;
	}
	/**
	 * @return the invoiceCessSpecificAmount
	 */
	public String getInvoiceCessSpecificAmount() {
		return invoiceCessSpecificAmount;
	}
	/**
	 * @param invoiceCessSpecificAmount the invoiceCessSpecificAmount to set
	 */
	public void setInvoiceCessSpecificAmount(String invoiceCessSpecificAmount) {
		this.invoiceCessSpecificAmount = invoiceCessSpecificAmount;
	}
	/**
	 * @return the invoiceStateCessAdvaloremAmount
	 */
	public String getInvoiceStateCessAdvaloremAmount() {
		return invoiceStateCessAdvaloremAmount;
	}
	/**
	 * @param invoiceStateCessAdvaloremAmount the invoiceStateCessAdvaloremAmount to set
	 */
	public void setInvoiceStateCessAdvaloremAmount(
			String invoiceStateCessAdvaloremAmount) {
		this.invoiceStateCessAdvaloremAmount = invoiceStateCessAdvaloremAmount;
	}
	/**
	 * @return the invoiceStateCessSpecificAmount
	 */
	public String getInvoiceStateCessSpecificAmount() {
		return invoiceStateCessSpecificAmount;
	}
	/**
	 * @param invoiceStateCessSpecificAmount the invoiceStateCessSpecificAmount to set
	 */
	public void setInvoiceStateCessSpecificAmount(
			String invoiceStateCessSpecificAmount) {
		this.invoiceStateCessSpecificAmount = invoiceStateCessSpecificAmount;
	}
	/**
	 * @return the invoiceValue
	 */
	public String getInvoiceValue() {
		return invoiceValue;
	}
	/**
	 * @param invoiceValue the invoiceValue to set
	 */
	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	/**
	 * @return the roundOff
	 */
	public String getRoundOff() {
		return roundOff;
	}
	/**
	 * @param roundOff the roundOff to set
	 */
	public void setRoundOff(String roundOff) {
		this.roundOff = roundOff;
	}
	/**
	 * @return the totalInvoiceValue
	 */
	public String getTotalInvoiceValue() {
		return totalInvoiceValue;
	}
	/**
	 * @param totalInvoiceValue the totalInvoiceValue to set
	 */
	public void setTotalInvoiceValue(String totalInvoiceValue) {
		this.totalInvoiceValue = totalInvoiceValue;
	}
	/**
	 * @return the eligibilityIndicator
	 */
	public String getEligibilityIndicator() {
		return eligibilityIndicator;
	}
	/**
	 * @param eligibilityIndicator the eligibilityIndicator to set
	 */
	public void setEligibilityIndicator(String eligibilityIndicator) {
		this.eligibilityIndicator = eligibilityIndicator;
	}
	/**
	 * @return the commonSupplyIndicator
	 */
	public String getCommonSupplyIndicator() {
		return commonSupplyIndicator;
	}
	/**
	 * @param commonSupplyIndicator the commonSupplyIndicator to set
	 */
	public void setCommonSupplyIndicator(String commonSupplyIndicator) {
		this.commonSupplyIndicator = commonSupplyIndicator;
	}
	/**
	 * @return the availableIgst
	 */
	public String getAvailableIgst() {
		return availableIgst;
	}
	/**
	 * @param availableIgst the availableIgst to set
	 */
	public void setAvailableIgst(String availableIgst) {
		this.availableIgst = availableIgst;
	}
	/**
	 * @return the availableCgst
	 */
	public String getAvailableCgst() {
		return availableCgst;
	}
	/**
	 * @param availableCgst the availableCgst to set
	 */
	public void setAvailableCgst(String availableCgst) {
		this.availableCgst = availableCgst;
	}
	/**
	 * @return the availableSgst
	 */
	public String getAvailableSgst() {
		return availableSgst;
	}
	/**
	 * @param availableSgst the availableSgst to set
	 */
	public void setAvailableSgst(String availableSgst) {
		this.availableSgst = availableSgst;
	}
	/**
	 * @return the availableCess
	 */
	public String getAvailableCess() {
		return availableCess;
	}
	/**
	 * @param availableCess the availableCess to set
	 */
	public void setAvailableCess(String availableCess) {
		this.availableCess = availableCess;
	}
	/**
	 * @return the iTCEntitlement
	 */
	public String getiTCEntitlement() {
		return iTCEntitlement;
	}
	/**
	 * @param iTCEntitlement the iTCEntitlement to set
	 */
	public void setiTCEntitlement(String iTCEntitlement) {
		this.iTCEntitlement = iTCEntitlement;
	}
	/**
	 * @return the iTCReversalIdentifier
	 */
	public String getiTCReversalIdentifier() {
		return iTCReversalIdentifier;
	}
	/**
	 * @param iTCReversalIdentifier the iTCReversalIdentifier to set
	 */
	public void setiTCReversalIdentifier(String iTCReversalIdentifier) {
		this.iTCReversalIdentifier = iTCReversalIdentifier;
	}
	/**
	 * @return the tCSFlagIncomeTax
	 */
	public String gettCSFlagIncomeTax() {
		return tCSFlagIncomeTax;
	}
	/**
	 * @param tCSFlagIncomeTax the tCSFlagIncomeTax to set
	 */
	public void settCSFlagIncomeTax(String tCSFlagIncomeTax) {
		this.tCSFlagIncomeTax = tCSFlagIncomeTax;
	}
	/**
	 * @return the tCSRateIncomeTax
	 */
	public String gettCSRateIncomeTax() {
		return tCSRateIncomeTax;
	}
	/**
	 * @param tCSRateIncomeTax the tCSRateIncomeTax to set
	 */
	public void settCSRateIncomeTax(String tCSRateIncomeTax) {
		this.tCSRateIncomeTax = tCSRateIncomeTax;
	}
	/**
	 * @return the tCSAmountIncomeTax
	 */
	public String gettCSAmountIncomeTax() {
		return tCSAmountIncomeTax;
	}
	/**
	 * @param tCSAmountIncomeTax the tCSAmountIncomeTax to set
	 */
	public void settCSAmountIncomeTax(String tCSAmountIncomeTax) {
		this.tCSAmountIncomeTax = tCSAmountIncomeTax;
	}
	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the invoiceValueFC
	 */
	public String getInvoiceValueFC() {
		return invoiceValueFC;
	}
	/**
	 * @param invoiceValueFC the invoiceValueFC to set
	 */
	public void setInvoiceValueFC(String invoiceValueFC) {
		this.invoiceValueFC = invoiceValueFC;
	}
	/**
	 * @return the portCode
	 */
	public String getPortCode() {
		return portCode;
	}
	/**
	 * @param portCode the portCode to set
	 */
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	/**
	 * @return the billofEntry
	 */
	public String getBillofEntry() {
		return billofEntry;
	}
	/**
	 * @param billofEntry the billofEntry to set
	 */
	public void setBillofEntry(String billofEntry) {
		this.billofEntry = billofEntry;
	}
	/**
	 * @return the billofEntryDate
	 */
	public String getBillofEntryDate() {
		return billofEntryDate;
	}
	/**
	 * @param billofEntryDate the billofEntryDate to set
	 */
	public void setBillofEntryDate(String billofEntryDate) {
		this.billofEntryDate = billofEntryDate;
	}
	/**
	 * @return the invoiceRemarks
	 */
	public String getInvoiceRemarks() {
		return invoiceRemarks;
	}
	/**
	 * @param invoiceRemarks the invoiceRemarks to set
	 */
	public void setInvoiceRemarks(String invoiceRemarks) {
		this.invoiceRemarks = invoiceRemarks;
	}
	/**
	 * @return the invoicePeriodStartDate
	 */
	public String getInvoicePeriodStartDate() {
		return invoicePeriodStartDate;
	}
	/**
	 * @param invoicePeriodStartDate the invoicePeriodStartDate to set
	 */
	public void setInvoicePeriodStartDate(String invoicePeriodStartDate) {
		this.invoicePeriodStartDate = invoicePeriodStartDate;
	}
	/**
	 * @return the invoicePeriodEndDate
	 */
	public String getInvoicePeriodEndDate() {
		return invoicePeriodEndDate;
	}
	/**
	 * @param invoicePeriodEndDate the invoicePeriodEndDate to set
	 */
	public void setInvoicePeriodEndDate(String invoicePeriodEndDate) {
		this.invoicePeriodEndDate = invoicePeriodEndDate;
	}
	/**
	 * @return the preceedingInvoiceNumber
	 */
	public String getPreceedingInvoiceNumber() {
		return preceedingInvoiceNumber;
	}
	/**
	 * @param preceedingInvoiceNumber the preceedingInvoiceNumber to set
	 */
	public void setPreceedingInvoiceNumber(String preceedingInvoiceNumber) {
		this.preceedingInvoiceNumber = preceedingInvoiceNumber;
	}
	/**
	 * @return the preceedingInvoiceDate
	 */
	public String getPreceedingInvoiceDate() {
		return preceedingInvoiceDate;
	}
	/**
	 * @param preceedingInvoiceDate the preceedingInvoiceDate to set
	 */
	public void setPreceedingInvoiceDate(String preceedingInvoiceDate) {
		this.preceedingInvoiceDate = preceedingInvoiceDate;
	}
	/**
	 * @return the otherReference
	 */
	public String getOtherReference() {
		return otherReference;
	}
	/**
	 * @param otherReference the otherReference to set
	 */
	public void setOtherReference(String otherReference) {
		this.otherReference = otherReference;
	}
	/**
	 * @return the receiptAdviceReference
	 */
	public String getReceiptAdviceReference() {
		return receiptAdviceReference;
	}
	/**
	 * @param receiptAdviceReference the receiptAdviceReference to set
	 */
	public void setReceiptAdviceReference(String receiptAdviceReference) {
		this.receiptAdviceReference = receiptAdviceReference;
	}
	/**
	 * @return the receiptAdviceDate
	 */
	public String getReceiptAdviceDate() {
		return receiptAdviceDate;
	}
	/**
	 * @param receiptAdviceDate the receiptAdviceDate to set
	 */
	public void setReceiptAdviceDate(String receiptAdviceDate) {
		this.receiptAdviceDate = receiptAdviceDate;
	}
	/**
	 * @return the tenderReference
	 */
	public String getTenderReference() {
		return tenderReference;
	}
	/**
	 * @param tenderReference the tenderReference to set
	 */
	public void setTenderReference(String tenderReference) {
		this.tenderReference = tenderReference;
	}
	/**
	 * @return the contractReference
	 */
	public String getContractReference() {
		return contractReference;
	}
	/**
	 * @param contractReference the contractReference to set
	 */
	public void setContractReference(String contractReference) {
		this.contractReference = contractReference;
	}
	/**
	 * @return the externalReference
	 */
	public String getExternalReference() {
		return externalReference;
	}
	/**
	 * @param externalReference the externalReference to set
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	/**
	 * @return the projectReference
	 */
	public String getProjectReference() {
		return projectReference;
	}
	/**
	 * @param projectReference the projectReference to set
	 */
	public void setProjectReference(String projectReference) {
		this.projectReference = projectReference;
	}
	/**
	 * @return the customerPOReferenceNumber
	 */
	public String getCustomerPOReferenceNumber() {
		return customerPOReferenceNumber;
	}
	/**
	 * @param customerPOReferenceNumber the customerPOReferenceNumber to set
	 */
	public void setCustomerPOReferenceNumber(String customerPOReferenceNumber) {
		this.customerPOReferenceNumber = customerPOReferenceNumber;
	}
	/**
	 * @return the customerPOReferenceDate
	 */
	public String getCustomerPOReferenceDate() {
		return customerPOReferenceDate;
	}
	/**
	 * @param customerPOReferenceDate the customerPOReferenceDate to set
	 */
	public void setCustomerPOReferenceDate(String customerPOReferenceDate) {
		this.customerPOReferenceDate = customerPOReferenceDate;
	}
	/**
	 * @return the payeeName
	 */
	public String getPayeeName() {
		return payeeName;
	}
	/**
	 * @param payeeName the payeeName to set
	 */
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	/**
	 * @return the modeOfPayment
	 */
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	/**
	 * @param modeOfPayment the modeOfPayment to set
	 */
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	/**
	 * @return the branchOrIFSCCode
	 */
	public String getBranchOrIFSCCode() {
		return branchOrIFSCCode;
	}
	/**
	 * @param branchOrIFSCCode the branchOrIFSCCode to set
	 */
	public void setBranchOrIFSCCode(String branchOrIFSCCode) {
		this.branchOrIFSCCode = branchOrIFSCCode;
	}
	/**
	 * @return the paymentTerms
	 */
	public String getPaymentTerms() {
		return paymentTerms;
	}
	/**
	 * @param paymentTerms the paymentTerms to set
	 */
	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	/**
	 * @return the paymentInstruction
	 */
	public String getPaymentInstruction() {
		return paymentInstruction;
	}
	/**
	 * @param paymentInstruction the paymentInstruction to set
	 */
	public void setPaymentInstruction(String paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}
	/**
	 * @return the creditTransfer
	 */
	public String getCreditTransfer() {
		return creditTransfer;
	}
	/**
	 * @param creditTransfer the creditTransfer to set
	 */
	public void setCreditTransfer(String creditTransfer) {
		this.creditTransfer = creditTransfer;
	}
	/**
	 * @return the directDebit
	 */
	public String getDirectDebit() {
		return directDebit;
	}
	/**
	 * @param directDebit the directDebit to set
	 */
	public void setDirectDebit(String directDebit) {
		this.directDebit = directDebit;
	}
	/**
	 * @return the creditDays
	 */
	public String getCreditDays() {
		return creditDays;
	}
	/**
	 * @param creditDays the creditDays to set
	 */
	public void setCreditDays(String creditDays) {
		this.creditDays = creditDays;
	}
	/**
	 * @return the paidAmount
	 */
	public String getPaidAmount() {
		return paidAmount;
	}
	/**
	 * @param paidAmount the paidAmount to set
	 */
	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}
	/**
	 * @return the balanceAmount
	 */
	public String getBalanceAmount() {
		return balanceAmount;
	}
	/**
	 * @param balanceAmount the balanceAmount to set
	 */
	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	/**
	 * @return the paymentDueDate
	 */
	public String getPaymentDueDate() {
		return paymentDueDate;
	}
	/**
	 * @param paymentDueDate the paymentDueDate to set
	 */
	public void setPaymentDueDate(String paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}
	/**
	 * @return the accountDetail
	 */
	public String getAccountDetail() {
		return accountDetail;
	}
	/**
	 * @param accountDetail the accountDetail to set
	 */
	public void setAccountDetail(String accountDetail) {
		this.accountDetail = accountDetail;
	}
	/**
	 * @return the ecomGSTIN
	 */
	public String getEcomGSTIN() {
		return ecomGSTIN;
	}
	/**
	 * @param ecomGSTIN the ecomGSTIN to set
	 */
	public void setEcomGSTIN(String ecomGSTIN) {
		this.ecomGSTIN = ecomGSTIN;
	}
	/**
	 * @return the supportingDocURL
	 */
	public String getSupportingDocURL() {
		return supportingDocURL;
	}
	/**
	 * @param supportingDocURL the supportingDocURL to set
	 */
	public void setSupportingDocURL(String supportingDocURL) {
		this.supportingDocURL = supportingDocURL;
	}
	/**
	 * @return the supportingDocument
	 */
	public String getSupportingDocument() {
		return supportingDocument;
	}
	/**
	 * @param supportingDocument the supportingDocument to set
	 */
	public void setSupportingDocument(String supportingDocument) {
		this.supportingDocument = supportingDocument;
	}
	/**
	 * @return the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}
	/**
	 * @param additionalInformation the additionalInformation to set
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}
	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	/**
	 * @return the subSupplyType
	 */
	public String getSubSupplyType() {
		return subSupplyType;
	}
	/**
	 * @param subSupplyType the subSupplyType to set
	 */
	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType;
	}
	/**
	 * @return the otherSupplyTypeDescription
	 */
	public String getOtherSupplyTypeDescription() {
		return otherSupplyTypeDescription;
	}
	/**
	 * @param otherSupplyTypeDescription the otherSupplyTypeDescription to set
	 */
	public void setOtherSupplyTypeDescription(String otherSupplyTypeDescription) {
		this.otherSupplyTypeDescription = otherSupplyTypeDescription;
	}
	/**
	 * @return the transporterID
	 */
	public String getTransporterID() {
		return transporterID;
	}
	/**
	 * @param transporterID the transporterID to set
	 */
	public void setTransporterID(String transporterID) {
		this.transporterID = transporterID;
	}
	/**
	 * @return the transporterName
	 */
	public String getTransporterName() {
		return transporterName;
	}
	/**
	 * @param transporterName the transporterName to set
	 */
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	/**
	 * @return the transportMode
	 */
	public String getTransportMode() {
		return transportMode;
	}
	/**
	 * @param transportMode the transportMode to set
	 */
	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}
	/**
	 * @return the transportDocNo
	 */
	public String getTransportDocNo() {
		return transportDocNo;
	}
	/**
	 * @param transportDocNo the transportDocNo to set
	 */
	public void setTransportDocNo(String transportDocNo) {
		this.transportDocNo = transportDocNo;
	}
	/**
	 * @return the transportDocDate
	 */
	public String getTransportDocDate() {
		return transportDocDate;
	}
	/**
	 * @param transportDocDate the transportDocDate to set
	 */
	public void setTransportDocDate(String transportDocDate) {
		this.transportDocDate = transportDocDate;
	}
	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}
	/**
	 * @return the vehicleNo
	 */
	public String getVehicleNo() {
		return vehicleNo;
	}
	/**
	 * @param vehicleNo the vehicleNo to set
	 */
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	/**
	 * @return the vehicleType
	 */
	public String getVehicleType() {
		return vehicleType;
	}
	/**
	 * @param vehicleType the vehicleType to set
	 */
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}
	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	/**
	 * @return the originalDocumentType
	 */
	public String getOriginalDocumentType() {
		return originalDocumentType;
	}
	/**
	 * @param originalDocumentType the originalDocumentType to set
	 */
	public void setOriginalDocumentType(String originalDocumentType) {
		this.originalDocumentType = originalDocumentType;
	}
	/**
	 * @return the originalSupplierGSTIN
	 */
	public String getOriginalSupplierGSTIN() {
		return originalSupplierGSTIN;
	}
	/**
	 * @param originalSupplierGSTIN the originalSupplierGSTIN to set
	 */
	public void setOriginalSupplierGSTIN(String originalSupplierGSTIN) {
		this.originalSupplierGSTIN = originalSupplierGSTIN;
	}
	/**
	 * @return the differentialPercentageFlag
	 */
	public String getDifferentialPercentageFlag() {
		return differentialPercentageFlag;
	}
	/**
	 * @param differentialPercentageFlag the differentialPercentageFlag to set
	 */
	public void setDifferentialPercentageFlag(String differentialPercentageFlag) {
		this.differentialPercentageFlag = differentialPercentageFlag;
	}
	/**
	 * @return the sec7ofIGSTFlag
	 */
	public String getSec7ofIGSTFlag() {
		return sec7ofIGSTFlag;
	}
	/**
	 * @param sec7ofIGSTFlag the sec7ofIGSTFlag to set
	 */
	public void setSec7ofIGSTFlag(String sec7ofIGSTFlag) {
		this.sec7ofIGSTFlag = sec7ofIGSTFlag;
	}
	/**
	 * @return the claimRefndFlag
	 */
	public String getClaimRefndFlag() {
		return claimRefndFlag;
	}
	/**
	 * @param claimRefndFlag the claimRefndFlag to set
	 */
	public void setClaimRefndFlag(String claimRefndFlag) {
		this.claimRefndFlag = claimRefndFlag;
	}
	/**
	 * @return the autoPopltToRefund
	 */
	public String getAutoPopltToRefund() {
		return autoPopltToRefund;
	}
	/**
	 * @param autoPopltToRefund the autoPopltToRefund to set
	 */
	public void setAutoPopltToRefund(String autoPopltToRefund) {
		this.autoPopltToRefund = autoPopltToRefund;
	}
	/**
	 * @return the cRDRPreGST
	 */
	public String getcRDRPreGST() {
		return cRDRPreGST;
	}
	/**
	 * @param cRDRPreGST the cRDRPreGST to set
	 */
	public void setcRDRPreGST(String cRDRPreGST) {
		this.cRDRPreGST = cRDRPreGST;
	}
	/**
	 * @return the supplierType
	 */
	public String getSupplierType() {
		return supplierType;
	}
	/**
	 * @param supplierType the supplierType to set
	 */
	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}
	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}
	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the categoryOfProduct
	 */
	public String getCategoryOfProduct() {
		return categoryOfProduct;
	}
	/**
	 * @param categoryOfProduct the categoryOfProduct to set
	 */
	public void setCategoryOfProduct(String categoryOfProduct) {
		this.categoryOfProduct = categoryOfProduct;
	}
	/**
	 * @return the stateApplyingCess
	 */
	public String getStateApplyingCess() {
		return stateApplyingCess;
	}
	/**
	 * @param stateApplyingCess the stateApplyingCess to set
	 */
	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}
	/**
	 * @return the cif
	 */
	public String getCif() {
		return cif;
	}
	/**
	 * @param cif the cif to set
	 */
	public void setCif(String cif) {
		this.cif = cif;
	}
	/**
	 * @return the customDuty
	 */
	public String getCustomDuty() {
		return customDuty;
	}
	/**
	 * @param customDuty the customDuty to set
	 */
	public void setCustomDuty(String customDuty) {
		this.customDuty = customDuty;
	}
	/**
	 * @return the exchangeRate
	 */
	public String getExchangeRate() {
		return exchangeRate;
	}
	/**
	 * @param exchangeRate the exchangeRate to set
	 */
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	/**
	 * @return the reasonForCreditDebitNote
	 */
	public String getReasonForCreditDebitNote() {
		return reasonForCreditDebitNote;
	}
	/**
	 * @param reasonForCreditDebitNote the reasonForCreditDebitNote to set
	 */
	public void setReasonForCreditDebitNote(String reasonForCreditDebitNote) {
		this.reasonForCreditDebitNote = reasonForCreditDebitNote;
	}
	/**
	 * @return the tCSFlagGST
	 */
	public String gettCSFlagGST() {
		return tCSFlagGST;
	}
	/**
	 * @param tCSFlagGST the tCSFlagGST to set
	 */
	public void settCSFlagGST(String tCSFlagGST) {
		this.tCSFlagGST = tCSFlagGST;
	}
	/**
	 * @return the tCSIGSTAmount
	 */
	public String gettCSIGSTAmount() {
		return tCSIGSTAmount;
	}
	/**
	 * @param tCSIGSTAmount the tCSIGSTAmount to set
	 */
	public void settCSIGSTAmount(String tCSIGSTAmount) {
		this.tCSIGSTAmount = tCSIGSTAmount;
	}
	/**
	 * @return the tCSCGSTAmount
	 */
	public String gettCSCGSTAmount() {
		return tCSCGSTAmount;
	}
	/**
	 * @param tCSCGSTAmount the tCSCGSTAmount to set
	 */
	public void settCSCGSTAmount(String tCSCGSTAmount) {
		this.tCSCGSTAmount = tCSCGSTAmount;
	}
	/**
	 * @return the tCSSGSTAmount
	 */
	public String gettCSSGSTAmount() {
		return tCSSGSTAmount;
	}
	/**
	 * @param tCSSGSTAmount the tCSSGSTAmount to set
	 */
	public void settCSSGSTAmount(String tCSSGSTAmount) {
		this.tCSSGSTAmount = tCSSGSTAmount;
	}
	/**
	 * @return the tDSFlagGST
	 */
	public String gettDSFlagGST() {
		return tDSFlagGST;
	}
	/**
	 * @param tDSFlagGST the tDSFlagGST to set
	 */
	public void settDSFlagGST(String tDSFlagGST) {
		this.tDSFlagGST = tDSFlagGST;
	}
	/**
	 * @return the tDSIGSTAmount
	 */
	public String gettDSIGSTAmount() {
		return tDSIGSTAmount;
	}
	/**
	 * @param tDSIGSTAmount the tDSIGSTAmount to set
	 */
	public void settDSIGSTAmount(String tDSIGSTAmount) {
		this.tDSIGSTAmount = tDSIGSTAmount;
	}
	/**
	 * @return the tDSCGSTAmount
	 */
	public String gettDSCGSTAmount() {
		return tDSCGSTAmount;
	}
	/**
	 * @param tDSCGSTAmount the tDSCGSTAmount to set
	 */
	public void settDSCGSTAmount(String tDSCGSTAmount) {
		this.tDSCGSTAmount = tDSCGSTAmount;
	}
	/**
	 * @return the tDSSGSTAmount
	 */
	public String gettDSSGSTAmount() {
		return tDSSGSTAmount;
	}
	/**
	 * @param tDSSGSTAmount the tDSSGSTAmount to set
	 */
	public void settDSSGSTAmount(String tDSSGSTAmount) {
		this.tDSSGSTAmount = tDSSGSTAmount;
	}
	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @return the companyCode
	 */
	public String getCompanyCode() {
		return companyCode;
	}
	/**
	 * @param companyCode the companyCode to set
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	/**
	 * @return the sourceIdentifier
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	/**
	 * @param sourceIdentifier the sourceIdentifier to set
	 */
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}
	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}
	/**
	 * @param sourceFileName the sourceFileName to set
	 */
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}
	/**
	 * @return the plantCode
	 */
	public String getPlantCode() {
		return plantCode;
	}
	/**
	 * @param plantCode the plantCode to set
	 */
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}
	/**
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}
	/**
	 * @param division the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}
	/**
	 * @return the subDivision
	 */
	public String getSubDivision() {
		return subDivision;
	}
	/**
	 * @param subDivision the subDivision to set
	 */
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @return the purchaseOrganisation
	 */
	public String getPurchaseOrganisation() {
		return purchaseOrganisation;
	}
	/**
	 * @param purchaseOrganisation the purchaseOrganisation to set
	 */
	public void setPurchaseOrganisation(String purchaseOrganisation) {
		this.purchaseOrganisation = purchaseOrganisation;
	}
	/**
	 * @return the profitCentre1
	 */
	public String getProfitCentre1() {
		return profitCentre1;
	}
	/**
	 * @param profitCentre1 the profitCentre1 to set
	 */
	public void setProfitCentre1(String profitCentre1) {
		this.profitCentre1 = profitCentre1;
	}
	/**
	 * @return the profitCentre2
	 */
	public String getProfitCentre2() {
		return profitCentre2;
	}
	/**
	 * @param profitCentre2 the profitCentre2 to set
	 */
	public void setProfitCentre2(String profitCentre2) {
		this.profitCentre2 = profitCentre2;
	}
	/**
	 * @return the profitCentre3
	 */
	public String getProfitCentre3() {
		return profitCentre3;
	}
	/**
	 * @param profitCentre3 the profitCentre3 to set
	 */
	public void setProfitCentre3(String profitCentre3) {
		this.profitCentre3 = profitCentre3;
	}
	/**
	 * @return the profitCentre4
	 */
	public String getProfitCentre4() {
		return profitCentre4;
	}
	/**
	 * @param profitCentre4 the profitCentre4 to set
	 */
	public void setProfitCentre4(String profitCentre4) {
		this.profitCentre4 = profitCentre4;
	}
	/**
	 * @return the profitCentre5
	 */
	public String getProfitCentre5() {
		return profitCentre5;
	}
	/**
	 * @param profitCentre5 the profitCentre5 to set
	 */
	public void setProfitCentre5(String profitCentre5) {
		this.profitCentre5 = profitCentre5;
	}
	/**
	 * @return the profitCentre6
	 */
	public String getProfitCentre6() {
		return profitCentre6;
	}
	/**
	 * @param profitCentre6 the profitCentre6 to set
	 */
	public void setProfitCentre6(String profitCentre6) {
		this.profitCentre6 = profitCentre6;
	}
	/**
	 * @return the profitCentre7
	 */
	public String getProfitCentre7() {
		return profitCentre7;
	}
	/**
	 * @param profitCentre7 the profitCentre7 to set
	 */
	public void setProfitCentre7(String profitCentre7) {
		this.profitCentre7 = profitCentre7;
	}
	/**
	 * @return the profitCentre8
	 */
	public String getProfitCentre8() {
		return profitCentre8;
	}
	/**
	 * @param profitCentre8 the profitCentre8 to set
	 */
	public void setProfitCentre8(String profitCentre8) {
		this.profitCentre8 = profitCentre8;
	}
	/**
	 * @return the glAssessableValue
	 */
	public String getGlAssessableValue() {
		return glAssessableValue;
	}
	/**
	 * @param glAssessableValue the glAssessableValue to set
	 */
	public void setGlAssessableValue(String glAssessableValue) {
		this.glAssessableValue = glAssessableValue;
	}
	/**
	 * @return the glIGST
	 */
	public String getGlIGST() {
		return glIGST;
	}
	/**
	 * @param glIGST the glIGST to set
	 */
	public void setGlIGST(String glIGST) {
		this.glIGST = glIGST;
	}
	/**
	 * @return the glCGST
	 */
	public String getGlCGST() {
		return glCGST;
	}
	/**
	 * @param glCGST the glCGST to set
	 */
	public void setGlCGST(String glCGST) {
		this.glCGST = glCGST;
	}
	/**
	 * @return the glSGST
	 */
	public String getGlSGST() {
		return glSGST;
	}
	/**
	 * @param glSGST the glSGST to set
	 */
	public void setGlSGST(String glSGST) {
		this.glSGST = glSGST;
	}
	/**
	 * @return the glAdvaloremCess
	 */
	public String getGlAdvaloremCess() {
		return glAdvaloremCess;
	}
	/**
	 * @param glAdvaloremCess the glAdvaloremCess to set
	 */
	public void setGlAdvaloremCess(String glAdvaloremCess) {
		this.glAdvaloremCess = glAdvaloremCess;
	}
	/**
	 * @return the glSpecificCess
	 */
	public String getGlSpecificCess() {
		return glSpecificCess;
	}
	/**
	 * @param glSpecificCess the glSpecificCess to set
	 */
	public void setGlSpecificCess(String glSpecificCess) {
		this.glSpecificCess = glSpecificCess;
	}
	/**
	 * @return the gLStateCessAdvalorem
	 */
	public String getgLStateCessAdvalorem() {
		return gLStateCessAdvalorem;
	}
	/**
	 * @param gLStateCessAdvalorem the gLStateCessAdvalorem to set
	 */
	public void setgLStateCessAdvalorem(String gLStateCessAdvalorem) {
		this.gLStateCessAdvalorem = gLStateCessAdvalorem;
	}
	/**
	 * @return the gLStateCessSpecific
	 */
	public String getgLStateCessSpecific() {
		return gLStateCessSpecific;
	}
	/**
	 * @param gLStateCessSpecific the gLStateCessSpecific to set
	 */
	public void setgLStateCessSpecific(String gLStateCessSpecific) {
		this.gLStateCessSpecific = gLStateCessSpecific;
	}
	/**
	 * @return the glPostingDate
	 */
	public String getGlPostingDate() {
		return glPostingDate;
	}
	/**
	 * @param glPostingDate the glPostingDate to set
	 */
	public void setGlPostingDate(String glPostingDate) {
		this.glPostingDate = glPostingDate;
	}
	/**
	 * @return the purchaseOrderValue
	 */
	public String getPurchaseOrderValue() {
		return purchaseOrderValue;
	}
	/**
	 * @param purchaseOrderValue the purchaseOrderValue to set
	 */
	public void setPurchaseOrderValue(String purchaseOrderValue) {
		this.purchaseOrderValue = purchaseOrderValue;
	}
	/**
	 * @return the eWBNumber
	 */
	public String geteWBNumber() {
		return eWBNumber;
	}
	/**
	 * @param eWBNumber the eWBNumber to set
	 */
	public void seteWBNumber(String eWBNumber) {
		this.eWBNumber = eWBNumber;
	}
	/**
	 * @return the eWBDate
	 */
	public String geteWBDate() {
		return eWBDate;
	}
	/**
	 * @param eWBDate the eWBDate to set
	 */
	public void seteWBDate(String eWBDate) {
		this.eWBDate = eWBDate;
	}
	/**
	 * @return the accountingVoucherNumber
	 */
	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}
	/**
	 * @param accountingVoucherNumber the accountingVoucherNumber to set
	 */
	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}
	/**
	 * @return the accountingVoucherDate
	 */
	public String getAccountingVoucherDate() {
		return accountingVoucherDate;
	}
	/**
	 * @param accountingVoucherDate the accountingVoucherDate to set
	 */
	public void setAccountingVoucherDate(String accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}
	/**
	 * @return the documentReferenceNumber
	 */
	public String getDocumentReferenceNumber() {
		return documentReferenceNumber;
	}
	/**
	 * @param documentReferenceNumber the documentReferenceNumber to set
	 */
	public void setDocumentReferenceNumber(String documentReferenceNumber) {
		this.documentReferenceNumber = documentReferenceNumber;
	}
	/**
	 * @return the userDefField1
	 */
	public String getUserDefField1() {
		return userDefField1;
	}
	/**
	 * @param userDefField1 the userDefField1 to set
	 */
	public void setUserDefField1(String userDefField1) {
		this.userDefField1 = userDefField1;
	}
	/**
	 * @return the userDefField2
	 */
	public String getUserDefField2() {
		return userDefField2;
	}
	/**
	 * @param userDefField2 the userDefField2 to set
	 */
	public void setUserDefField2(String userDefField2) {
		this.userDefField2 = userDefField2;
	}
	/**
	 * @return the userDefField3
	 */
	public String getUserDefField3() {
		return userDefField3;
	}
	/**
	 * @param userDefField3 the userDefField3 to set
	 */
	public void setUserDefField3(String userDefField3) {
		this.userDefField3 = userDefField3;
	}
	/**
	 * @return the userDefField4
	 */
	public String getUserDefField4() {
		return userDefField4;
	}
	/**
	 * @param userDefField4 the userDefField4 to set
	 */
	public void setUserDefField4(String userDefField4) {
		this.userDefField4 = userDefField4;
	}
	/**
	 * @return the userDefField5
	 */
	public String getUserDefField5() {
		return userDefField5;
	}
	/**
	 * @param userDefField5 the userDefField5 to set
	 */
	public void setUserDefField5(String userDefField5) {
		this.userDefField5 = userDefField5;
	}
	/**
	 * @return the userDefField6
	 */
	public String getUserDefField6() {
		return userDefField6;
	}
	/**
	 * @param userDefField6 the userDefField6 to set
	 */
	public void setUserDefField6(String userDefField6) {
		this.userDefField6 = userDefField6;
	}
	/**
	 * @return the userDefField7
	 */
	public String getUserDefField7() {
		return userDefField7;
	}
	/**
	 * @param userDefField7 the userDefField7 to set
	 */
	public void setUserDefField7(String userDefField7) {
		this.userDefField7 = userDefField7;
	}
	/**
	 * @return the userDefField8
	 */
	public String getUserDefField8() {
		return userDefField8;
	}
	/**
	 * @param userDefField8 the userDefField8 to set
	 */
	public void setUserDefField8(String userDefField8) {
		this.userDefField8 = userDefField8;
	}
	/**
	 * @return the userDefField9
	 */
	public String getUserDefField9() {
		return userDefField9;
	}
	/**
	 * @param userDefField9 the userDefField9 to set
	 */
	public void setUserDefField9(String userDefField9) {
		this.userDefField9 = userDefField9;
	}
	/**
	 * @return the userDefField10
	 */
	public String getUserDefField10() {
		return userDefField10;
	}
	/**
	 * @param userDefField10 the userDefField10 to set
	 */
	public void setUserDefField10(String userDefField10) {
		this.userDefField10 = userDefField10;
	}
	/**
	 * @return the userDefField11
	 */
	public String getUserDefField11() {
		return userDefField11;
	}
	/**
	 * @param userDefField11 the userDefField11 to set
	 */
	public void setUserDefField11(String userDefField11) {
		this.userDefField11 = userDefField11;
	}
	/**
	 * @return the userDefField12
	 */
	public String getUserDefField12() {
		return userDefField12;
	}
	/**
	 * @param userDefField12 the userDefField12 to set
	 */
	public void setUserDefField12(String userDefField12) {
		this.userDefField12 = userDefField12;
	}
	/**
	 * @return the userDefField13
	 */
	public String getUserDefField13() {
		return userDefField13;
	}
	/**
	 * @param userDefField13 the userDefField13 to set
	 */
	public void setUserDefField13(String userDefField13) {
		this.userDefField13 = userDefField13;
	}
	/**
	 * @return the userDefField14
	 */
	public String getUserDefField14() {
		return userDefField14;
	}
	/**
	 * @param userDefField14 the userDefField14 to set
	 */
	public void setUserDefField14(String userDefField14) {
		this.userDefField14 = userDefField14;
	}
	/**
	 * @return the userDefField15
	 */
	public String getUserDefField15() {
		return userDefField15;
	}
	/**
	 * @param userDefField15 the userDefField15 to set
	 */
	public void setUserDefField15(String userDefField15) {
		this.userDefField15 = userDefField15;
	}
	/**
	 * @return the userDefField16
	 */
	public String getUserDefField16() {
		return userDefField16;
	}
	/**
	 * @param userDefField16 the userDefField16 to set
	 */
	public void setUserDefField16(String userDefField16) {
		this.userDefField16 = userDefField16;
	}
	/**
	 * @return the userDefField17
	 */
	public String getUserDefField17() {
		return userDefField17;
	}
	/**
	 * @param userDefField17 the userDefField17 to set
	 */
	public void setUserDefField17(String userDefField17) {
		this.userDefField17 = userDefField17;
	}
	/**
	 * @return the userDefField18
	 */
	public String getUserDefField18() {
		return userDefField18;
	}
	/**
	 * @param userDefField18 the userDefField18 to set
	 */
	public void setUserDefField18(String userDefField18) {
		this.userDefField18 = userDefField18;
	}
	/**
	 * @return the userDefField19
	 */
	public String getUserDefField19() {
		return userDefField19;
	}
	/**
	 * @param userDefField19 the userDefField19 to set
	 */
	public void setUserDefField19(String userDefField19) {
		this.userDefField19 = userDefField19;
	}
	/**
	 * @return the userDefField20
	 */
	public String getUserDefField20() {
		return userDefField20;
	}
	/**
	 * @param userDefField20 the userDefField20 to set
	 */
	public void setUserDefField20(String userDefField20) {
		this.userDefField20 = userDefField20;
	}
	/**
	 * @return the userDefField21
	 */
	public String getUserDefField21() {
		return userDefField21;
	}
	/**
	 * @param userDefField21 the userDefField21 to set
	 */
	public void setUserDefField21(String userDefField21) {
		this.userDefField21 = userDefField21;
	}
	/**
	 * @return the userDefField22
	 */
	public String getUserDefField22() {
		return userDefField22;
	}
	/**
	 * @param userDefField22 the userDefField22 to set
	 */
	public void setUserDefField22(String userDefField22) {
		this.userDefField22 = userDefField22;
	}
	/**
	 * @return the userDefField23
	 */
	public String getUserDefField23() {
		return userDefField23;
	}
	/**
	 * @param userDefField23 the userDefField23 to set
	 */
	public void setUserDefField23(String userDefField23) {
		this.userDefField23 = userDefField23;
	}
	/**
	 * @return the userDefField24
	 */
	public String getUserDefField24() {
		return userDefField24;
	}
	/**
	 * @param userDefField24 the userDefField24 to set
	 */
	public void setUserDefField24(String userDefField24) {
		this.userDefField24 = userDefField24;
	}
	/**
	 * @return the userDefField25
	 */
	public String getUserDefField25() {
		return userDefField25;
	}
	/**
	 * @param userDefField25 the userDefField25 to set
	 */
	public void setUserDefField25(String userDefField25) {
		this.userDefField25 = userDefField25;
	}
	/**
	 * @return the userDefField26
	 */
	public String getUserDefField26() {
		return userDefField26;
	}
	/**
	 * @param userDefField26 the userDefField26 to set
	 */
	public void setUserDefField26(String userDefField26) {
		this.userDefField26 = userDefField26;
	}
	/**
	 * @return the userDefField27
	 */
	public String getUserDefField27() {
		return userDefField27;
	}
	/**
	 * @param userDefField27 the userDefField27 to set
	 */
	public void setUserDefField27(String userDefField27) {
		this.userDefField27 = userDefField27;
	}
	/**
	 * @return the userDefField28
	 */
	public String getUserDefField28() {
		return userDefField28;
	}
	/**
	 * @param userDefField28 the userDefField28 to set
	 */
	public void setUserDefField28(String userDefField28) {
		this.userDefField28 = userDefField28;
	}
	/**
	 * @return the userDefField29
	 */
	public String getUserDefField29() {
		return userDefField29;
	}
	/**
	 * @param userDefField29 the userDefField29 to set
	 */
	public void setUserDefField29(String userDefField29) {
		this.userDefField29 = userDefField29;
	}
	/**
	 * @return the userDefField30
	 */
	public String getUserDefField30() {
		return userDefField30;
	}
	/**
	 * @param userDefField30 the userDefField30 to set
	 */
	public void setUserDefField30(String userDefField30) {
		this.userDefField30 = userDefField30;
	}
	/**
	 * @return the itcAvailableIGST
	 */
	public String getItcAvailableIGST() {
		return ItcAvailableIGST;
	}
	/**
	 * @param itcAvailableIGST the itcAvailableIGST to set
	 */
	public void setItcAvailableIGST(String itcAvailableIGST) {
		ItcAvailableIGST = itcAvailableIGST;
	}
	/**
	 * @return the itcAvailableCGST
	 */
	public String getItcAvailableCGST() {
		return ItcAvailableCGST;
	}
	/**
	 * @param itcAvailableCGST the itcAvailableCGST to set
	 */
	public void setItcAvailableCGST(String itcAvailableCGST) {
		ItcAvailableCGST = itcAvailableCGST;
	}
	/**
	 * @return the itcAvailableSGST
	 */
	public String getItcAvailableSGST() {
		return ItcAvailableSGST;
	}
	/**
	 * @param itcAvailableSGST the itcAvailableSGST to set
	 */
	public void setItcAvailableSGST(String itcAvailableSGST) {
		ItcAvailableSGST = itcAvailableSGST;
	}
	/**
	 * @return the itcAvailableCess
	 */
	public String getItcAvailableCess() {
		return ItcAvailableCess;
	}
	/**
	 * @param itcAvailableCess the itcAvailableCess to set
	 */
	public void setItcAvailableCess(String itcAvailableCess) {
		ItcAvailableCess = itcAvailableCess;
	}
	/**
	 * @return the ratio1IGST
	 */
	public BigDecimal getRatio1IGST() {
		return Ratio1IGST;
	}
	/**
	 * @param ratio1igst the ratio1IGST to set
	 */
	public void setRatio1IGST(BigDecimal ratio1igst) {
		Ratio1IGST = ratio1igst;
	}
	/**
	 * @return the ratio1CGST
	 */
	public BigDecimal getRatio1CGST() {
		return Ratio1CGST;
	}
	/**
	 * @param ratio1cgst the ratio1CGST to set
	 */
	public void setRatio1CGST(BigDecimal ratio1cgst) {
		Ratio1CGST = ratio1cgst;
	}
	/**
	 * @return the ratio1SGST
	 */
	public BigDecimal getRatio1SGST() {
		return Ratio1SGST;
	}
	/**
	 * @param ratio1sgst the ratio1SGST to set
	 */
	public void setRatio1SGST(BigDecimal ratio1sgst) {
		Ratio1SGST = ratio1sgst;
	}
	/**
	 * @return the ratio1Cess
	 */
	public BigDecimal getRatio1Cess() {
		return Ratio1Cess;
	}
	/**
	 * @param ratio1Cess the ratio1Cess to set
	 */
	public void setRatio1Cess(BigDecimal ratio1Cess) {
		Ratio1Cess = ratio1Cess;
	}
	/**
	 * @return the ratio2IGST
	 */
	public BigDecimal getRatio2IGST() {
		return Ratio2IGST;
	}
	/**
	 * @param ratio2igst the ratio2IGST to set
	 */
	public void setRatio2IGST(BigDecimal ratio2igst) {
		Ratio2IGST = ratio2igst;
	}
	/**
	 * @return the ratio2CGST
	 */
	public BigDecimal getRatio2CGST() {
		return Ratio2CGST;
	}
	/**
	 * @param ratio2cgst the ratio2CGST to set
	 */
	public void setRatio2CGST(BigDecimal ratio2cgst) {
		Ratio2CGST = ratio2cgst;
	}
	/**
	 * @return the ratio2SGST
	 */
	public BigDecimal getRatio2SGST() {
		return Ratio2SGST;
	}
	/**
	 * @param ratio2sgst the ratio2SGST to set
	 */
	public void setRatio2SGST(BigDecimal ratio2sgst) {
		Ratio2SGST = ratio2sgst;
	}
	/**
	 * @return the ratio2Cess
	 */
	public BigDecimal getRatio2Cess() {
		return Ratio2Cess;
	}
	/**
	 * @param ratio2Cess the ratio2Cess to set
	 */
	public void setRatio2Cess(BigDecimal ratio2Cess) {
		Ratio2Cess = ratio2Cess;
	}
	/**
	 * @return the ratio3IGST
	 */
	public BigDecimal getRatio3IGST() {
		return Ratio3IGST;
	}
	/**
	 * @param ratio3igst the ratio3IGST to set
	 */
	public void setRatio3IGST(BigDecimal ratio3igst) {
		Ratio3IGST = ratio3igst;
	}
	/**
	 * @return the ratio3CGST
	 */
	public BigDecimal getRatio3CGST() {
		return Ratio3CGST;
	}
	/**
	 * @param ratio3cgst the ratio3CGST to set
	 */
	public void setRatio3CGST(BigDecimal ratio3cgst) {
		Ratio3CGST = ratio3cgst;
	}
	/**
	 * @return the ratio3SGST
	 */
	public BigDecimal getRatio3SGST() {
		return Ratio3SGST;
	}
	/**
	 * @param ratio3sgst the ratio3SGST to set
	 */
	public void setRatio3SGST(BigDecimal ratio3sgst) {
		Ratio3SGST = ratio3sgst;
	}
	/**
	 * @return the ratio3Cess
	 */
	public BigDecimal getRatio3Cess() {
		return Ratio3Cess;
	}
	/**
	 * @param ratio3Cess the ratio3Cess to set
	 */
	public void setRatio3Cess(BigDecimal ratio3Cess) {
		Ratio3Cess = ratio3Cess;
	}
	public String getCommonSupplyIndicatorUpdated() {
		return commonSupplyIndicatorUpdated;
	}
	public String getUpdatedCommonSupplyIndicator() {
		return updatedCommonSupplyIndicator;
	}
	public String getOriginalCommonSupplyIndicator() {
		return originalCommonSupplyIndicator;
	}
	public String getReturnPeriodUpdated() {
		return returnPeriodUpdated;
	}
	public String getUpdatedReturnPeriod() {
		return updatedReturnPeriod;
	}
	public String getOriginalReturnPeriod() {
		return originalReturnPeriod;
	}
	public void setCommonSupplyIndicatorUpdated(
			String commonSupplyIndicatorUpdated) {
		this.commonSupplyIndicatorUpdated = commonSupplyIndicatorUpdated;
	}
	public void setUpdatedCommonSupplyIndicator(
			String updatedCommonSupplyIndicator) {
		this.updatedCommonSupplyIndicator = updatedCommonSupplyIndicator;
	}
	public void setOriginalCommonSupplyIndicator(
			String originalCommonSupplyIndicator) {
		this.originalCommonSupplyIndicator = originalCommonSupplyIndicator;
	}
	public void setReturnPeriodUpdated(String returnPeriodUpdated) {
		this.returnPeriodUpdated = returnPeriodUpdated;
	}
	public void setUpdatedReturnPeriod(String updatedReturnPeriod) {
		this.updatedReturnPeriod = updatedReturnPeriod;
	}
	public void setOriginalReturnPeriod(String originalReturnPeriod) {
		this.originalReturnPeriod = originalReturnPeriod;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
/*	@Override
	public String toString() {
		return "ReversalInwardDto [category=" + category + ", irn=" + irn
				+ ", irnDate=" + irnDate + ", taxScheme=" + taxScheme
				+ ", supplyType=" + supplyType + ", docCategory=" + docCategory
				+ ", documentType=" + documentType + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate
				+ ", reverseChargeFlag=" + reverseChargeFlag
				+ ", supplierGSTIN=" + supplierGSTIN + ", supplierTradeName="
				+ supplierTradeName + ", supplierLegalName=" + supplierLegalName
				+ ", supplierAddress1=" + supplierAddress1
				+ ", supplierAddress2=" + supplierAddress2
				+ ", supplierLocation=" + supplierLocation
				+ ", supplierPincode=" + supplierPincode
				+ ", supplierStateCode=" + supplierStateCode
				+ ", supplierPhone=" + supplierPhone + ", supplierEmail="
				+ supplierEmail + ", customerGSTIN=" + customerGSTIN
				+ ", customerTradeName=" + customerTradeName
				+ ", customerLegalName=" + customerLegalName
				+ ", customerAddress1=" + customerAddress1
				+ ", customerAddress2=" + customerAddress2
				+ ", customerLocation=" + customerLocation
				+ ", customerPincode=" + customerPincode
				+ ", customerStateCode=" + customerStateCode + ", billingPOS="
				+ billingPOS + ", customerPhone=" + customerPhone
				+ ", customerEmail=" + customerEmail + ", dispatcherGSTIN="
				+ dispatcherGSTIN + ", dispatcherTradeName="
				+ dispatcherTradeName + ", dispatcherAddress1="
				+ dispatcherAddress1 + ", dispatcherAddress2="
				+ dispatcherAddress2 + ", dispatcherLocation="
				+ dispatcherLocation + ", dispatcherPincode="
				+ dispatcherPincode + ", dispatcherStateCode="
				+ dispatcherStateCode + ", shipToGSTIN=" + shipToGSTIN
				+ ", shipToTradeName=" + shipToTradeName + ", shipToLegalName="
				+ shipToLegalName + ", shipToAddress1=" + shipToAddress1
				+ ", shipToAddress2=" + shipToAddress2 + ", shipToLocation="
				+ shipToLocation + ", shipToPincode=" + shipToPincode
				+ ", shipToStateCode=" + shipToStateCode + ", itemSerialNumber="
				+ itemSerialNumber + ", productSerialNumber="
				+ productSerialNumber + ", productName=" + productName
				+ ", productDescription=" + productDescription + ", isService="
				+ isService + ", hsn=" + hsn + ", barcode=" + barcode
				+ ", batchName=" + batchName + ", batchExpiryDate="
				+ batchExpiryDate + ", warrantyDate=" + warrantyDate
				+ ", orderlineReference=" + orderlineReference
				+ ", attributeName=" + attributeName + ", attributeValue="
				+ attributeValue + ", originCountry=" + originCountry + ", uQC="
				+ uQC + ", Quantity=" + Quantity + ", freeQuantity="
				+ freeQuantity + ", unitPrice=" + unitPrice + ", itemAmount="
				+ itemAmount + ", itemDiscount=" + itemDiscount
				+ ", preTaxAmount=" + preTaxAmount + ", itemAssessableAmount="
				+ itemAssessableAmount + ", iGSTRate=" + iGSTRate
				+ ", iGSTAmount=" + iGSTAmount + ", cGSTRate=" + cGSTRate
				+ ", cGSTAmount=" + cGSTAmount + ", sGSTRate=" + sGSTRate
				+ ", sGSTAmount=" + sGSTAmount + ", cessAdvaloremRate="
				+ cessAdvaloremRate + ", cessAdvaloremAmount="
				+ cessAdvaloremAmount + ", cessSpecificRate=" + cessSpecificRate
				+ ", cessSpecificAmount=" + cessSpecificAmount
				+ ", stateCessAdvaloremRate=" + stateCessAdvaloremRate
				+ ", stateCessAdvaloremAmount=" + stateCessAdvaloremAmount
				+ ", stateCessSpecificRate=" + stateCessSpecificRate
				+ ", stateCessSpecificAmount=" + stateCessSpecificAmount
				+ ", itemOtherCharges=" + itemOtherCharges
				+ ", totalItemAmount=" + totalItemAmount
				+ ", invoiceOtherCharges=" + invoiceOtherCharges
				+ ", invoiceAssessableAmount=" + invoiceAssessableAmount
				+ ", invoiceIGSTAmount=" + invoiceIGSTAmount
				+ ", invoiceCGSTAmount=" + invoiceCGSTAmount
				+ ", invoiceSGSTAmount=" + invoiceSGSTAmount
				+ ", invoiceCessAdvaloremAmount=" + invoiceCessAdvaloremAmount
				+ ", invoiceCessSpecificAmount=" + invoiceCessSpecificAmount
				+ ", invoiceStateCessAdvaloremAmount="
				+ invoiceStateCessAdvaloremAmount
				+ ", invoiceStateCessSpecificAmount="
				+ invoiceStateCessSpecificAmount + ", invoiceValue="
				+ invoiceValue + ", roundOff=" + roundOff
				+ ", totalInvoiceValue=" + totalInvoiceValue
				+ ", eligibilityIndicator=" + eligibilityIndicator
				+ ", commonSupplyIndicator=" + commonSupplyIndicator
				+ ", availableIgst=" + availableIgst + ", availableCgst="
				+ availableCgst + ", availableSgst=" + availableSgst
				+ ", availableCess=" + availableCess + ", iTCEntitlement="
				+ iTCEntitlement + ", iTCReversalIdentifier="
				+ iTCReversalIdentifier + ", tCSFlagIncomeTax="
				+ tCSFlagIncomeTax + ", tCSRateIncomeTax=" + tCSRateIncomeTax
				+ ", tCSAmountIncomeTax=" + tCSAmountIncomeTax
				+ ", currencyCode=" + currencyCode + ", countryCode="
				+ countryCode + ", invoiceValueFC=" + invoiceValueFC
				+ ", portCode=" + portCode + ", billofEntry=" + billofEntry
				+ ", billofEntryDate=" + billofEntryDate + ", invoiceRemarks="
				+ invoiceRemarks + ", invoicePeriodStartDate="
				+ invoicePeriodStartDate + ", invoicePeriodEndDate="
				+ invoicePeriodEndDate + ", preceedingInvoiceNumber="
				+ preceedingInvoiceNumber + ", preceedingInvoiceDate="
				+ preceedingInvoiceDate + ", otherReference=" + otherReference
				+ ", receiptAdviceReference=" + receiptAdviceReference
				+ ", receiptAdviceDate=" + receiptAdviceDate
				+ ", tenderReference=" + tenderReference
				+ ", contractReference=" + contractReference
				+ ", externalReference=" + externalReference
				+ ", projectReference=" + projectReference
				+ ", customerPOReferenceNumber=" + customerPOReferenceNumber
				+ ", customerPOReferenceDate=" + customerPOReferenceDate
				+ ", payeeName=" + payeeName + ", modeOfPayment="
				+ modeOfPayment + ", branchOrIFSCCode=" + branchOrIFSCCode
				+ ", paymentTerms=" + paymentTerms + ", paymentInstruction="
				+ paymentInstruction + ", creditTransfer=" + creditTransfer
				+ ", directDebit=" + directDebit + ", creditDays=" + creditDays
				+ ", paidAmount=" + paidAmount + ", balanceAmount="
				+ balanceAmount + ", paymentDueDate=" + paymentDueDate
				+ ", accountDetail=" + accountDetail + ", ecomGSTIN="
				+ ecomGSTIN + ", supportingDocURL=" + supportingDocURL
				+ ", supportingDocument=" + supportingDocument
				+ ", additionalInformation=" + additionalInformation
				+ ", transactionType=" + transactionType + ", subSupplyType="
				+ subSupplyType + ", otherSupplyTypeDescription="
				+ otherSupplyTypeDescription + ", transporterID="
				+ transporterID + ", transporterName=" + transporterName
				+ ", transportMode=" + transportMode + ", transportDocNo="
				+ transportDocNo + ", transportDocDate=" + transportDocDate
				+ ", distance=" + distance + ", vehicleNo=" + vehicleNo
				+ ", vehicleType=" + vehicleType + ", returnPeriod="
				+ returnPeriod + ", originalDocumentType="
				+ originalDocumentType + ", originalSupplierGSTIN="
				+ originalSupplierGSTIN + ", differentialPercentageFlag="
				+ differentialPercentageFlag + ", sec7ofIGSTFlag="
				+ sec7ofIGSTFlag + ", claimRefndFlag=" + claimRefndFlag
				+ ", autoPopltToRefund=" + autoPopltToRefund + ", cRDRPreGST="
				+ cRDRPreGST + ", supplierType=" + supplierType
				+ ", supplierCode=" + supplierCode + ", productCode="
				+ productCode + ", categoryOfProduct=" + categoryOfProduct
				+ ", stateApplyingCess=" + stateApplyingCess + ", cif=" + cif
				+ ", customDuty=" + customDuty + ", exchangeRate="
				+ exchangeRate + ", reasonForCreditDebitNote="
				+ reasonForCreditDebitNote + ", tCSFlagGST=" + tCSFlagGST
				+ ", tCSIGSTAmount=" + tCSIGSTAmount + ", tCSCGSTAmount="
				+ tCSCGSTAmount + ", tCSSGSTAmount=" + tCSSGSTAmount
				+ ", tDSFlagGST=" + tDSFlagGST + ", tDSIGSTAmount="
				+ tDSIGSTAmount + ", tDSCGSTAmount=" + tDSCGSTAmount
				+ ", tDSSGSTAmount=" + tDSSGSTAmount + ", userID=" + userID
				+ ", companyCode=" + companyCode + ", sourceIdentifier="
				+ sourceIdentifier + ", sourceFileName=" + sourceFileName
				+ ", plantCode=" + plantCode + ", division=" + division
				+ ", subDivision=" + subDivision + ", location=" + location
				+ ", purchaseOrganisation=" + purchaseOrganisation
				+ ", profitCentre1=" + profitCentre1 + ", profitCentre2="
				+ profitCentre2 + ", profitCentre3=" + profitCentre3
				+ ", profitCentre4=" + profitCentre4 + ", profitCentre5="
				+ profitCentre5 + ", profitCentre6=" + profitCentre6
				+ ", profitCentre7=" + profitCentre7 + ", profitCentre8="
				+ profitCentre8 + ", glAssessableValue=" + glAssessableValue
				+ ", glIGST=" + glIGST + ", glCGST=" + glCGST + ", glSGST="
				+ glSGST + ", glAdvaloremCess=" + glAdvaloremCess
				+ ", glSpecificCess=" + glSpecificCess
				+ ", gLStateCessAdvalorem=" + gLStateCessAdvalorem
				+ ", gLStateCessSpecific=" + gLStateCessSpecific
				+ ", glPostingDate=" + glPostingDate + ", purchaseOrderValue="
				+ purchaseOrderValue + ", eWBNumber=" + eWBNumber + ", eWBDate="
				+ eWBDate + ", accountingVoucherNumber="
				+ accountingVoucherNumber + ", accountingVoucherDate="
				+ accountingVoucherDate + ", documentReferenceNumber="
				+ documentReferenceNumber + ", userDefField1=" + userDefField1
				+ ", userDefField2=" + userDefField2 + ", userDefField3="
				+ userDefField3 + ", userDefField4=" + userDefField4
				+ ", userDefField5=" + userDefField5 + ", userDefField6="
				+ userDefField6 + ", userDefField7=" + userDefField7
				+ ", userDefField8=" + userDefField8 + ", userDefField9="
				+ userDefField9 + ", userDefField10=" + userDefField10
				+ ", userDefField11=" + userDefField11 + ", userDefField12="
				+ userDefField12 + ", userDefField13=" + userDefField13
				+ ", userDefField14=" + userDefField14 + ", userDefField15="
				+ userDefField15 + ", userDefField16=" + userDefField16
				+ ", userDefField17=" + userDefField17 + ", userDefField18="
				+ userDefField18 + ", userDefField19=" + userDefField19
				+ ", userDefField20=" + userDefField20 + ", userDefField21="
				+ userDefField21 + ", userDefField22=" + userDefField22
				+ ", userDefField23=" + userDefField23 + ", userDefField24="
				+ userDefField24 + ", userDefField25=" + userDefField25
				+ ", userDefField26=" + userDefField26 + ", userDefField27="
				+ userDefField27 + ", userDefField28=" + userDefField28
				+ ", userDefField29=" + userDefField29 + ", userDefField30="
				+ userDefField30 + ", ItcAvailableIGST=" + ItcAvailableIGST
				+ ", ItcAvailableCGST=" + ItcAvailableCGST
				+ ", ItcAvailableSGST=" + ItcAvailableSGST
				+ ", ItcAvailableCess=" + ItcAvailableCess + ", Ratio1IGST="
				+ Ratio1IGST + ", Ratio1CGST=" + Ratio1CGST + ", Ratio1SGST="
				+ Ratio1SGST + ", Ratio1Cess=" + Ratio1Cess + ", Ratio2IGST="
				+ Ratio2IGST + ", Ratio2CGST=" + Ratio2CGST + ", Ratio2SGST="
				+ Ratio2SGST + ", Ratio2Cess=" + Ratio2Cess + ", Ratio3IGST="
				+ Ratio3IGST + ", Ratio3CGST=" + Ratio3CGST + ", Ratio3SGST="
				+ Ratio3SGST + ", Ratio3Cess=" + Ratio3Cess + "]";
	}*/
	@Override
	public String toString() {
		return "ReversalInwardDto [category=" + category + ", irn=" + irn
				+ ", irnDate=" + irnDate + ", taxScheme=" + taxScheme
				+ ", supplyType=" + supplyType + ", docCategory=" + docCategory
				+ ", documentType=" + documentType + ", documentNumber="
				+ documentNumber + ", documentDate=" + documentDate
				+ ", reverseChargeFlag=" + reverseChargeFlag
				+ ", supplierGSTIN=" + supplierGSTIN + ", supplierTradeName="
				+ supplierTradeName + ", supplierLegalName=" + supplierLegalName
				+ ", supplierAddress1=" + supplierAddress1
				+ ", supplierAddress2=" + supplierAddress2
				+ ", supplierLocation=" + supplierLocation
				+ ", supplierPincode=" + supplierPincode
				+ ", supplierStateCode=" + supplierStateCode
				+ ", supplierPhone=" + supplierPhone + ", supplierEmail="
				+ supplierEmail + ", customerGSTIN=" + customerGSTIN
				+ ", customerTradeName=" + customerTradeName
				+ ", customerLegalName=" + customerLegalName
				+ ", customerAddress1=" + customerAddress1
				+ ", customerAddress2=" + customerAddress2
				+ ", customerLocation=" + customerLocation
				+ ", customerPincode=" + customerPincode
				+ ", customerStateCode=" + customerStateCode + ", billingPOS="
				+ billingPOS + ", customerPhone=" + customerPhone
				+ ", customerEmail=" + customerEmail + ", dispatcherGSTIN="
				+ dispatcherGSTIN + ", dispatcherTradeName="
				+ dispatcherTradeName + ", dispatcherAddress1="
				+ dispatcherAddress1 + ", dispatcherAddress2="
				+ dispatcherAddress2 + ", dispatcherLocation="
				+ dispatcherLocation + ", dispatcherPincode="
				+ dispatcherPincode + ", dispatcherStateCode="
				+ dispatcherStateCode + ", shipToGSTIN=" + shipToGSTIN
				+ ", shipToTradeName=" + shipToTradeName + ", shipToLegalName="
				+ shipToLegalName + ", shipToAddress1=" + shipToAddress1
				+ ", shipToAddress2=" + shipToAddress2 + ", shipToLocation="
				+ shipToLocation + ", shipToPincode=" + shipToPincode
				+ ", shipToStateCode=" + shipToStateCode + ", itemSerialNumber="
				+ itemSerialNumber + ", productSerialNumber="
				+ productSerialNumber + ", productName=" + productName
				+ ", productDescription=" + productDescription + ", isService="
				+ isService + ", hsn=" + hsn + ", barcode=" + barcode
				+ ", batchName=" + batchName + ", batchExpiryDate="
				+ batchExpiryDate + ", warrantyDate=" + warrantyDate
				+ ", orderlineReference=" + orderlineReference
				+ ", attributeName=" + attributeName + ", attributeValue="
				+ attributeValue + ", originCountry=" + originCountry + ", uQC="
				+ uQC + ", Quantity=" + Quantity + ", freeQuantity="
				+ freeQuantity + ", unitPrice=" + unitPrice + ", itemAmount="
				+ itemAmount + ", itemDiscount=" + itemDiscount
				+ ", preTaxAmount=" + preTaxAmount + ", itemAssessableAmount="
				+ itemAssessableAmount + ", iGSTRate=" + iGSTRate
				+ ", iGSTAmount=" + iGSTAmount + ", cGSTRate=" + cGSTRate
				+ ", cGSTAmount=" + cGSTAmount + ", sGSTRate=" + sGSTRate
				+ ", sGSTAmount=" + sGSTAmount + ", cessAdvaloremRate="
				+ cessAdvaloremRate + ", cessAdvaloremAmount="
				+ cessAdvaloremAmount + ", cessSpecificRate=" + cessSpecificRate
				+ ", cessSpecificAmount=" + cessSpecificAmount
				+ ", stateCessAdvaloremRate=" + stateCessAdvaloremRate
				+ ", stateCessAdvaloremAmount=" + stateCessAdvaloremAmount
				+ ", stateCessSpecificRate=" + stateCessSpecificRate
				+ ", stateCessSpecificAmount=" + stateCessSpecificAmount
				+ ", itemOtherCharges=" + itemOtherCharges
				+ ", totalItemAmount=" + totalItemAmount
				+ ", invoiceOtherCharges=" + invoiceOtherCharges
				+ ", invoiceAssessableAmount=" + invoiceAssessableAmount
				+ ", invoiceIGSTAmount=" + invoiceIGSTAmount
				+ ", invoiceCGSTAmount=" + invoiceCGSTAmount
				+ ", invoiceSGSTAmount=" + invoiceSGSTAmount
				+ ", invoiceCessAdvaloremAmount=" + invoiceCessAdvaloremAmount
				+ ", invoiceCessSpecificAmount=" + invoiceCessSpecificAmount
				+ ", invoiceStateCessAdvaloremAmount="
				+ invoiceStateCessAdvaloremAmount
				+ ", invoiceStateCessSpecificAmount="
				+ invoiceStateCessSpecificAmount + ", invoiceValue="
				+ invoiceValue + ", roundOff=" + roundOff
				+ ", totalInvoiceValue=" + totalInvoiceValue
				+ ", eligibilityIndicator=" + eligibilityIndicator
				+ ", commonSupplyIndicator=" + commonSupplyIndicator
				+ ", availableIgst=" + availableIgst + ", availableCgst="
				+ availableCgst + ", availableSgst=" + availableSgst
				+ ", availableCess=" + availableCess + ", iTCEntitlement="
				+ iTCEntitlement + ", iTCReversalIdentifier="
				+ iTCReversalIdentifier + ", tCSFlagIncomeTax="
				+ tCSFlagIncomeTax + ", tCSRateIncomeTax=" + tCSRateIncomeTax
				+ ", tCSAmountIncomeTax=" + tCSAmountIncomeTax
				+ ", currencyCode=" + currencyCode + ", countryCode="
				+ countryCode + ", invoiceValueFC=" + invoiceValueFC
				+ ", portCode=" + portCode + ", billofEntry=" + billofEntry
				+ ", billofEntryDate=" + billofEntryDate + ", invoiceRemarks="
				+ invoiceRemarks + ", invoicePeriodStartDate="
				+ invoicePeriodStartDate + ", invoicePeriodEndDate="
				+ invoicePeriodEndDate + ", preceedingInvoiceNumber="
				+ preceedingInvoiceNumber + ", preceedingInvoiceDate="
				+ preceedingInvoiceDate + ", otherReference=" + otherReference
				+ ", receiptAdviceReference=" + receiptAdviceReference
				+ ", receiptAdviceDate=" + receiptAdviceDate
				+ ", tenderReference=" + tenderReference
				+ ", contractReference=" + contractReference
				+ ", externalReference=" + externalReference
				+ ", projectReference=" + projectReference
				+ ", customerPOReferenceNumber=" + customerPOReferenceNumber
				+ ", customerPOReferenceDate=" + customerPOReferenceDate
				+ ", payeeName=" + payeeName + ", modeOfPayment="
				+ modeOfPayment + ", branchOrIFSCCode=" + branchOrIFSCCode
				+ ", paymentTerms=" + paymentTerms + ", paymentInstruction="
				+ paymentInstruction + ", creditTransfer=" + creditTransfer
				+ ", directDebit=" + directDebit + ", creditDays=" + creditDays
				+ ", paidAmount=" + paidAmount + ", balanceAmount="
				+ balanceAmount + ", paymentDueDate=" + paymentDueDate
				+ ", accountDetail=" + accountDetail + ", ecomGSTIN="
				+ ecomGSTIN + ", supportingDocURL=" + supportingDocURL
				+ ", supportingDocument=" + supportingDocument
				+ ", additionalInformation=" + additionalInformation
				+ ", transactionType=" + transactionType + ", subSupplyType="
				+ subSupplyType + ", otherSupplyTypeDescription="
				+ otherSupplyTypeDescription + ", transporterID="
				+ transporterID + ", transporterName=" + transporterName
				+ ", transportMode=" + transportMode + ", transportDocNo="
				+ transportDocNo + ", transportDocDate=" + transportDocDate
				+ ", distance=" + distance + ", vehicleNo=" + vehicleNo
				+ ", vehicleType=" + vehicleType + ", returnPeriod="
				+ returnPeriod + ", originalDocumentType="
				+ originalDocumentType + ", originalSupplierGSTIN="
				+ originalSupplierGSTIN + ", differentialPercentageFlag="
				+ differentialPercentageFlag + ", sec7ofIGSTFlag="
				+ sec7ofIGSTFlag + ", claimRefndFlag=" + claimRefndFlag
				+ ", autoPopltToRefund=" + autoPopltToRefund + ", cRDRPreGST="
				+ cRDRPreGST + ", supplierType=" + supplierType
				+ ", supplierCode=" + supplierCode + ", productCode="
				+ productCode + ", categoryOfProduct=" + categoryOfProduct
				+ ", stateApplyingCess=" + stateApplyingCess + ", cif=" + cif
				+ ", customDuty=" + customDuty + ", exchangeRate="
				+ exchangeRate + ", reasonForCreditDebitNote="
				+ reasonForCreditDebitNote + ", tCSFlagGST=" + tCSFlagGST
				+ ", tCSIGSTAmount=" + tCSIGSTAmount + ", tCSCGSTAmount="
				+ tCSCGSTAmount + ", tCSSGSTAmount=" + tCSSGSTAmount
				+ ", tDSFlagGST=" + tDSFlagGST + ", tDSIGSTAmount="
				+ tDSIGSTAmount + ", tDSCGSTAmount=" + tDSCGSTAmount
				+ ", tDSSGSTAmount=" + tDSSGSTAmount + ", userID=" + userID
				+ ", companyCode=" + companyCode + ", sourceIdentifier="
				+ sourceIdentifier + ", sourceFileName=" + sourceFileName
				+ ", plantCode=" + plantCode + ", division=" + division
				+ ", subDivision=" + subDivision + ", location=" + location
				+ ", purchaseOrganisation=" + purchaseOrganisation
				+ ", profitCentre1=" + profitCentre1 + ", profitCentre2="
				+ profitCentre2 + ", profitCentre3=" + profitCentre3
				+ ", profitCentre4=" + profitCentre4 + ", profitCentre5="
				+ profitCentre5 + ", profitCentre6=" + profitCentre6
				+ ", profitCentre7=" + profitCentre7 + ", profitCentre8="
				+ profitCentre8 + ", glAssessableValue=" + glAssessableValue
				+ ", glIGST=" + glIGST + ", glCGST=" + glCGST + ", glSGST="
				+ glSGST + ", glAdvaloremCess=" + glAdvaloremCess
				+ ", glSpecificCess=" + glSpecificCess
				+ ", gLStateCessAdvalorem=" + gLStateCessAdvalorem
				+ ", gLStateCessSpecific=" + gLStateCessSpecific
				+ ", glPostingDate=" + glPostingDate + ", purchaseOrderValue="
				+ purchaseOrderValue + ", eWBNumber=" + eWBNumber + ", eWBDate="
				+ eWBDate + ", accountingVoucherNumber="
				+ accountingVoucherNumber + ", accountingVoucherDate="
				+ accountingVoucherDate + ", documentReferenceNumber="
				+ documentReferenceNumber + ", userDefField1=" + userDefField1
				+ ", userDefField2=" + userDefField2 + ", userDefField3="
				+ userDefField3 + ", userDefField4=" + userDefField4
				+ ", userDefField5=" + userDefField5 + ", userDefField6="
				+ userDefField6 + ", userDefField7=" + userDefField7
				+ ", userDefField8=" + userDefField8 + ", userDefField9="
				+ userDefField9 + ", userDefField10=" + userDefField10
				+ ", userDefField11=" + userDefField11 + ", userDefField12="
				+ userDefField12 + ", userDefField13=" + userDefField13
				+ ", userDefField14=" + userDefField14 + ", userDefField15="
				+ userDefField15 + ", userDefField16=" + userDefField16
				+ ", userDefField17=" + userDefField17 + ", userDefField18="
				+ userDefField18 + ", userDefField19=" + userDefField19
				+ ", userDefField20=" + userDefField20 + ", userDefField21="
				+ userDefField21 + ", userDefField22=" + userDefField22
				+ ", userDefField23=" + userDefField23 + ", userDefField24="
				+ userDefField24 + ", userDefField25=" + userDefField25
				+ ", userDefField26=" + userDefField26 + ", userDefField27="
				+ userDefField27 + ", userDefField28=" + userDefField28
				+ ", userDefField29=" + userDefField29 + ", userDefField30="
				+ userDefField30 + ", ItcAvailableIGST=" + ItcAvailableIGST
				+ ", ItcAvailableCGST=" + ItcAvailableCGST
				+ ", ItcAvailableSGST=" + ItcAvailableSGST
				+ ", ItcAvailableCess=" + ItcAvailableCess + ", Ratio1IGST="
				+ Ratio1IGST + ", Ratio1CGST=" + Ratio1CGST + ", Ratio1SGST="
				+ Ratio1SGST + ", Ratio1Cess=" + Ratio1Cess + ", Ratio2IGST="
				+ Ratio2IGST + ", Ratio2CGST=" + Ratio2CGST + ", Ratio2SGST="
				+ Ratio2SGST + ", Ratio2Cess=" + Ratio2Cess + ", Ratio3IGST="
				+ Ratio3IGST + ", Ratio3CGST=" + Ratio3CGST + ", Ratio3SGST="
				+ Ratio3SGST + ", Ratio3Cess=" + Ratio3Cess
				+ ", commonSupplyIndicatorUpdated="
				+ commonSupplyIndicatorUpdated
				+ ", updatedCommonSupplyIndicator="
				+ updatedCommonSupplyIndicator
				+ ", originalCommonSupplyIndicator="
				+ originalCommonSupplyIndicator + ", returnPeriodUpdated="
				+ returnPeriodUpdated + ", updatedReturnPeriod="
				+ updatedReturnPeriod + ", originalReturnPeriod="
				+ originalReturnPeriod + "]";
	}
}