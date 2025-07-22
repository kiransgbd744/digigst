package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr1HeaderChecker;

@Component("EInvoiceSRFileHeaderCheckService")
public class EInvoiceSRFileHeaderCheckService 
                                           implements Gstr1HeaderCheckService {

	@Autowired
	@Qualifier("Gstr1HeaderChecker")
	private Gstr1HeaderChecker gstr1HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { 
			"IRN", "IRNDate", "TaxScheme", "CancellationReason",
			"CancellationRemarks", "SupplyType", "DocCategory", "DocumentType",
			"DocumentNumber", "DocumentDate", "ReverseChargeFlag",
			"SupplierGSTIN", "SupplierTradeName", "SupplierLegalName",
			"SupplierAddress1", "SupplierAddress2", "SupplierLocation",
			"SupplierPincode", "SupplierStateCode", "SupplierPhone",//20
			"SupplierEmail", "CustomerGSTIN", "CustomerTradeName",
			"CustomerLegalName	", "CustomerAddress1", "CustomerAddress2",
			"CustomerLocation", "CustomerPincode", "CustomerStateCode",
			"BillingPOS", "CustomerPhone", "CustomerEmail", "DispatcherGSTIN",
			"DispatcherTradeName", "DispatcherAddress1", "DispatcherAddress2",
			"DispatcherLocation", "DispatcherPincode", "DispatcherStateCode	",
			"ShipToGSTIN", "ShipToTradeName", "ShipToLegalName",
			"ShipToAddress1", "ShipToAddress2", "ShipToLocation",
			"ShipToPincode", "ShipToStateCode", "ItemSerialNumber",
			"ProductSerialNumber", "ProductName", "ProductDescription",//51
			"IsService	", "HSN", "Barcode", "BatchName", "BatchExpiryDate",
			"WarrantyDate", "OrderLineReference", "AttributeName",
			"AttributeValue", "OriginCountry", "UQC", "Quantity",
			"FreeQuantity", "UnitPrice", "ItemAmount", "ItemDiscount",
			"PreTaxAmount", "ItemAssessableAmount", "IGSTRate", "IGSTAmount",
			"CGSTRate", "CGSTAmount", "SGSTRate", "SGSTAmount",
			"CessAdvaloremRate", "CessAdvaloremAmount", "CessSpecificRate",
			"CessSpecificAmount", "StateCessAdvaloremRate",
			"StateCessAdvaloremAmount", "StateCessSpecificRate",//31
			"StateCessSpecificAmount", "ItemOtherCharges",
			"TotalItemAmount", "InvoiceOtherCharges",
			"InvoiceAssessableAmount", "InvoiceIGSTAmount", "InvoiceCGSTAmount",
			"InvoiceSGSTAmount", "InvoiceCessAdvaloremAmount",
			"InvoiceCessSpecificAmount", "InvoiceStateCessAdvaloremAmount",
			"InvoiceStateCessSpecificAmount", "InvoiceValue", "RoundOff",
			"TotalInvoiceValue(InWords)", "TCSFlagIncomeTax	",
			"TCSRateIncomeTax", "TCSAmountIncomeTax", "CustomerPANOrAadhaar",//50
			"CurrencyCode", "CountryCode", "InvoiceValueFC", "PortCode",
			"ShippingBillNumber", "ShippingBillDate", "InvoiceRemarks",
			"InvoicePeriodStartDate", "InvoicePeriodEndDate",
			"PreceedingInvoiceNumber", "PreceedingInvoiceDate",
			"OtherReference", "ReceiptAdviceReference", "ReceiptAdviceDate",
			"TenderReference", "ContractReference", "ExternalReference",
			"ProjectReference", "CustomerPOReferenceNumber",
			"CustomerPOReferenceDate", "PayeeName", "ModeOfPayment",
			"BranchOrIFSCCode", "PaymentTerms", "PaymentInstruction",
			"CreditTransfer", "DirectDebit", "CreditDays",
			"PaidAmount",	"BalanceAmount", "PaymentDueDate", "AccountDetail",//32
			"EcomGSTIN", "EcomTransactionID", "SupportingDocURL",
			"SupportingDocument", "AdditionalInformation", "TransactionType",
			"SubSupplyType", "OtherSupplyTypeDescription", "TransporterID",
			"TransporterName", "TransportMode", "TransportDocNo",
			"TransportDocDate", "Distance", "VehicleNo", "VehicleType",
			"ReturnPeriod", "OriginalDocumentType", "OriginalCustomerGSTIN",//51
			"DifferentialPercentageFlag", "Section7OfIGSTFlag",
			"ClaimRefundFlag", "AutoPopulateToRefund", "CRDRPreGST",
			"CustomerType", "CustomerCode", "ProductCode", "CategoryOfProduct",
			"ITCFlag", "StateApplyingCess", "FOB", "ExportDuty", "ExchangeRate",
			"ReasonForCreditDebitNote", "TCSFlagGST", "TCSIGSTAmount",
			"TCSCGSTAmount", "TCSSGSTAmount", "TDSFlagGST", "TDSIGSTAmount",//21
			"TDSCGSTAmount", "TDSSGSTAmount", "UserID", "CompanyCode",
			"SourceIdentifier", "SourceFileName", "PlantCode", "Division",
			"SubDivision", "Location", "SalesOrganisation",
			"DistributionChannel", "ProfitCentre1", "ProfitCentre2",
			"ProfitCentre3", "ProfitCentre4", "ProfitCentre5", "ProfitCentre6",
			"ProfitCentre7", "ProfitCentre8", "GLAssessableValue",
			"GLIGST", "GLCGST", "GLSGST", "GLAdvaloremCess", "GLSpecificCess",
			"GLStateCessAdvalorem", "GLStateCessSpecific", "GLPostingDate",//50
			"SalesOrderNumber", "EWBNumber", "EWBDate",
			"AccountingVoucherNumber", "AccountingVoucherDate",
			"DocumentReferenceNumber", "CustomerTAN", "UserDefinedField1",
			"UserDefinedField2", "UserDefinedField3", "UserDefinedField4",
			"UserDefinedField5", "UserDefinedField6", "UserDefinedField7",
			"UserDefinedField8", "UserDefinedField9", "UserDefinedField10	",
			"UserDefinedField11", "UserDefinedField12", "UserDefinedField13",
			"UserDefinedField14", "UserDefinedField15", "UserDefinedField16",
			"UserDefinedField17", "UserDefinedField18",
			"UserDefinedField19", "UserDefinedField20",
			"UserDefinedField21", "UserDefinedField22",
			"UserDefinedField23", "UserDefinedField24",
			"UserDefinedField25", "UserDefinedField26",
			"UserDefinedField27", "UserDefinedField28", "UserDefinedField29",
			"UserDefinedField30" };

	// Check if the size of the headerCols array is less than
	// EXPECTED_HEADERS size. If so, return new
	// Pair<Boolean, String>(false,
	// "The size of the headers do not match!".

	// If the size is equal or greater, then get the first required
	// no of elements from the headerCols array into another array.

	// iterate and compare the elements of the EXPECTED_HEADERS and the
	// above array. If any element is different, then return a false
	// and an error message.

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		return gstr1HeaderChecker.validateHeaders(EXPECTED_HEADERS, headerCols);
	}

}