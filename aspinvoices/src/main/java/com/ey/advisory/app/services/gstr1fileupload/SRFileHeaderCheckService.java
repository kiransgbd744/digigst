package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr1HeaderChecker;

@Component("SRFileHeaderCheckService")
public class SRFileHeaderCheckService implements Gstr1HeaderCheckService {

	@Autowired
	@Qualifier("Gstr1HeaderChecker")
	private Gstr1HeaderChecker gstr1HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "UserID",
			"SourceFileName", "ProfitCentre", "Plant", "Division", "Location",
			"SalesOrganisation", "DistributionChannel", "UserAccess1",
			"UserAccess2", "UserAccess3", "UserAccess4", "UserAccess5",
			"UserAccess6", "GLCode-TaxableValue", "GLCode-IGST", "GLCode-CGST",
			"GLCode-SGST", "GLCode-AdvaloremCess", "GLCode-SpecificCess",
			"GLCode-StateCess", "ReturnPeriod", "SupplierGSTIN", "DocumentType",
			"SupplyType", "DocumentNumber", "DocumentDate","OriginalDocumentType",
			"OriginalDocumentNumber", "OriginalDocumentDate", "CRDRPreGST",
			"LineNumber", "RecipientGSTIN", "RecipientType",
			"DifferentialPercentageFlag", "OriginalRecipientGSTIN",
			"RecipientName", "RecipientCode", "RecipientAddress1",
			"RecipientAddress2", "RecipientAddress3", "RecipientAddress4",
			"BillToState", "ShipToState", "POS", "StateApplyingCess",
			"PortCode", "ShippingBillNumber", "ShippingBillDate", "FOB",
			"ExportDuty", "HSNorSAC", "ProductCode", "ProductDescription",
			"CategoryOfProduct", "UnitOfMeasurement", "Quantity",
			"Section7ofIGSTFlag", "TaxableValue", "IntegratedTaxRate",
			"IntegratedTaxAmount", "CentralTaxRate", "CentralTaxAmount",
			"StateUTTaxRate", "StateUTTaxAmount", "AdvaloremCessRate",
			"AdvaloremCessAmount", "SpecificCessRate", "SpecificCessAmount",
			"StateCessRate", "StateCessAmount", "OtherValue", "InvoiceValue",
			"AdjustmentReferenceNo", "AdjustmentReferenceDate",
			"TaxableValueAdjusted", "IntegratedTaxAmountAdjusted",
			"CentralTaxAmountAdjusted", "StateUTTaxAmountAdjusted",
			"AdvaloremCessAmountAdjusted", "SpecificCessAmountAdjusted",
			"StateCessAmountAdjusted", "ReverseChargeFlag", "TCSFlag",
			"eComGSTIN", "TCSAmount", "ITCFlag", "ClaimRefundFlag",
			"AutoPopulateToRefund", "ReasonForCreditDebitNote",
			"AccountingVoucherNumber", "AccountingVoucherDate",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "UserDefinedField6",
			"UserDefinedField7", "UserDefinedField8", "UserDefinedField9",
			"UserDefinedField10", "UserDefinedField11", "UserDefinedField12",
			"UserDefinedField13", "UserDefinedField14", "UserDefinedField15",
			"E-WayBillNumber", "E-WayBillDate" };

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