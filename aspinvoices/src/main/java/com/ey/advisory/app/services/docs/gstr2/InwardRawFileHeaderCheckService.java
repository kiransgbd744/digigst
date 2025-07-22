package com.ey.advisory.app.services.docs.gstr2;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr2HeaderChecker;
import com.ey.advisory.app.services.gstr2fileupload.Gstr2HeaderCheckService;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("InwardRawFileHeaderCheckService")
public class InwardRawFileHeaderCheckService
		implements Gstr2HeaderCheckService {

	@Autowired
	@Qualifier("Gstr2HeaderChecker")
	private Gstr2HeaderChecker gstr2HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "IRN", "IRNDate",
			"TaxScheme", "SupplyType", "DocCategory", "DocumentType",
			"DocumentNumber", "DocumentDate", "ReverseChargeFlag",
			"SupplierGSTIN", "SupplierTradeName", "SupplierLegalName",
			"SupplierAddress1", "SupplierAddress2", "SupplierLocation",
			"SupplierPincode", "SupplierStateCode", "SupplierPhone",
			"SupplierEmail", "CustomerGSTIN", "CustomerTradeName",
			"CustomerLegalName", "CustomerAddress1", "CustomerAddress2",
			"CustomerLocation", "CustomerPincode", "CustomerStateCode",
			"BillingPOS", "CustomerPhone", "CustomerEmail", "DispatcherGSTIN",
			"DispatcherTradeName", "DispatcherAddress1", "DispatcherAddress2",
			"DispatcherLocation", "DispatcherPincode", "DispatcherStateCode",
			"ShipToGSTIN", "ShipToTradeName", "ShipToLegalName",
			"ShipToAddress1", "ShipToAddress2", "ShipToLocation",
			"ShipToPincode", "ShipToStateCode", "ItemSerialNumber",
			"ProductSerialNumber", "ProductName", "ProductDescription",
			"IsService", "HSN", "Barcode", "BatchName", "BatchExpiryDate",
			"WarrantyDate", "OrderLineReference", "AttributeName",
			"AttributeValue", "OriginCountry", "UQC", "Quantity",
			"FreeQuantity", "UnitPrice", "ItemAmount", "ItemDiscount",
			"PreTaxAmount", "ItemAssessableAmount", "IGSTRate", "IGSTAmount",
			"CGSTRate", "CGSTAmount", "SGSTRate", "SGSTAmount",
			"CessAdvaloremRate", "CessAdvaloremAmount", "CessSpecificRate",
			"CessSpecificAmount", "StateCessAdvaloremRate",
			"StateCessAdvaloremAmount", "StateCessSpecificRate",
			"StateCessSpecificAmount", "ItemOtherCharges", "TotalItemAmount",
			"InvoiceOtherCharges", "InvoiceAssessableAmount",
			"InvoiceIGSTAmount", "InvoiceCGSTAmount", "InvoiceSGSTAmount",
			"InvoiceCessAdvaloremAmount", "InvoiceCessSpecificAmount",
			"InvoiceStateCessAdvaloremAmount", "InvoiceStateCessSpecificAmount",
			"InvoiceValue", "RoundOff", "TotalInvoiceValue(InWords)",
			"EligibilityIndicator", "CommonSupplyIndicator", "AvailableIGST",
			"AvailableCGST", "AvailableSGST", "AvailableCess", "ITCEntitlement",
			"ITCReversalIdentifier", "TCSFlagIncomeTax", "TCSRateIncomeTax",
			"TCSAmountIncomeTax", "CurrencyCode", "CountryCode",
			"InvoiceValueFC", "PortCode", "BillOfEntry", "BillOfEntryDate",
			"InvoiceRemarks", "InvoicePeriodStartDate", "InvoicePeriodEndDate",
			"PreceedingInvoiceNumber", "PreceedingInvoiceDate",
			"OtherReference", "ReceiptAdviceReference", "ReceiptAdviceDate",
			"TenderReference", "ContractReference", "ExternalReference",
			"ProjectReference", "CustomerPOReferenceNumber",
			"CustomerPOReferenceDate", "PayeeName", "ModeOfPayment",
			"BranchOrIFSCCode", "PaymentTerms", "PaymentInstruction",
			"CreditTransfer", "DirectDebit", "CreditDays", "PaidAmount",
			"BalanceAmount", "PaymentDueDate", "AccountDetail", "EcomGSTIN",
			"SupportingDocURL", "SupportingDocument", "AdditionalInformation",
			"TransactionType", "SubSupplyType", "OtherSupplyTypeDescription",
			"TransporterID", "TransporterName", "TransportMode",
			"TransportDocNo", "TransportDocDate", "Distance", "VehicleNo",
			"VehicleType", "ReturnPeriod", "OriginalDocumentType",
			"OriginalSupplierGSTIN", "DifferentialPercentageFlag",
			"Section7OfIGSTFlag", "ClaimRefundFlag", "AutoPopulateToRefund",
			"CRDRPreGST", "SupplierType", "SupplierCode", "ProductCode",
			"CategoryOfProduct", "StateApplyingCess", "CIF", "CustomDuty",
			"ExchangeRate", "ReasonForCreditDebitNote", "TCSFlagGST",
			"TCSIGSTAmount", "TCSCGSTAmount", "TCSSGSTAmount", "TDSFlagGST",
			"TDSIGSTAmount", "TDSCGSTAmount", "TDSSGSTAmount", "UserID",
			"CompanyCode", "SourceIdentifier", "SourceFileName", "PlantCode",
			"Division", "SubDivision", "Location", "PurchaseOrganisation",
			"ProfitCentre1", "ProfitCentre2", "ProfitCentre3", "ProfitCentre4",
			"ProfitCentre5", "ProfitCentre6", "ProfitCentre7", "ProfitCentre8",
			"GLAssessableValue", "GLIGST", "GLCGST", "GLSGST",
			"GLAdvaloremCess", "GLSpecificCess", "GLStateCessAdvalorem",
			"GLStateCessSpecific", "GLPostingDate", "PurchaseOrderValue",
			"EWBNumber", "EWBDate", "AccountingVoucherNumber",
			"AccountingVoucherDate", "DocumentReferenceNumber",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "UserDefinedField6",
			"UserDefinedField7", "UserDefinedField8", "UserDefinedField9",
			"UserDefinedField10", "UserDefinedField11", "UserDefinedField12",
			"UserDefinedField13", "UserDefinedField14", "UserDefinedField15",
			"UserDefinedField16", "UserDefinedField17", "UserDefinedField18",
			"UserDefinedField19", "UserDefinedField20", "UserDefinedField21",
			"UserDefinedField22", "UserDefinedField23", "UserDefinedField24",
			"UserDefinedField25", "UserDefinedField26", "UserDefinedField27",
			"UserDefinedField28", "UserDefinedField29", "UserDefinedField30"

	};

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
		return gstr2HeaderChecker.validateHeaders(EXPECTED_HEADERS, headerCols);
	}

}
