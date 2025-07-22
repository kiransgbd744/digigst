package com.ey.advisory.app.services.docs.gstr2;

import org.javatuples.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr2HeaderChecker;
import com.ey.advisory.app.services.gstr2fileupload.Gstr2HeaderCheckService;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Anx2InwardRawFileHeaderCheckService")
public class Anx2InwardRawFileHeaderCheckService
		implements Gstr2HeaderCheckService {

	@Autowired
	@Qualifier("Gstr2HeaderChecker")
	private Gstr2HeaderChecker gstr2HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "UserID",
			"SourceFileName", "ProfitCentre", "Plant", "Division", "Location",
			"PurchaseOrganisation", "UserAccess1", "UserAccess2", "UserAccess3",
			"UserAccess4", "UserAccess5", "UserAccess6", "GLCode-TaxableValue",
			"GLCode-IGST", "GLCode-CGST", "GLCode-SGST", "GLCode-AdvaloremCess",
			"GLCode-SpecificCess", "GLCode-StateCess", "ReturnPeriod",
			"RecipientGSTIN", "DocumentType", "SupplyType", "DocumentNumber",
			"DocumentDate", "OriginalDocumentNumber", "OriginalDocumentDate",
			"CRDRPreGST", "LineNumber", "SupplierGSTIN", "SupplierType",
			"Differential%Flag", "OriginalSupplierGSTIN", "SupplierName",
			"SupplierCode", "SupplierAddress1", "SupplierAddress2",
			"SupplierAddress3", "SupplierAddress4", "POS", "StateApplyingCess",
			"PortCode", "BillOfEntry", "BillOfEntryDate", "CIFValue",
			"CustomDuty", "HSNorSAC", "ItemCode", "ItemDescription",
			"CategoryOfItem", "UnitOfMeasurement", "Quantity",
			"Section7ofIGSTFlag", "TaxableValue", "IntegratedTaxRate",
			"IntegratedTaxAmount", "CentralTaxRate", "CentralTaxAmount",
			"StateUTTaxRate", "StateUTTaxAmount", "AdvaloremCessRate",
			"AdvaloremCessAmount", "SpecificCessRate", "SpecificCessAmount",
			"StateCessRate", "StateCessAmount", "OtherValue", "InvoiceValue",
			"ClaimRefundFlag", "AutoPopulateToRefund", "AdjustmentReferenceNo",
			"AdjustmentReferenceDate", "TaxableValueAdjusted",
			"IntegratedTaxAmountAdjusted", "CentralTaxAmountAdjusted",
			"StateUTTaxAmountAdjusted", "AdvaloremCessAmountAdjusted",
			"SpecificCessAmountAdjusted", "StateCessAmountAdjusted",
			"ReverseChargeFlag", "EligibilityIndicator",
			"CommonSupplyIndicator", "AvailableIGST", "AvailableCGST",
			"AvailableSGST", "AvailableCess", "ITCEntitlement",
			"ITCReversalIdentifier", "ReasonForCreditDebitNote",
			"PurchaseVoucherNumber", "PurchaseVoucherDate", "PostingDate",
			"PaymentVoucherNumber", "PaymentDate", "ContractNumber",
			"ContractDate", "ContractValue", "UserDefinedField1",
			"UserDefinedField2", "UserDefinedField3", "UserDefinedField4",
			"UserDefinedField5", "UserDefinedField6", "UserDefinedField7",
			"UserDefinedField8", "UserDefinedField9", "UserDefinedField10",
			"UserDefinedField11", "UserDefinedField12", "UserDefinedField13",
			"UserDefinedField14", "UserDefinedField15", "E-WayBillNumber",
			"E-WayBillDate" };

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
