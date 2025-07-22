package com.ey.advisory.app.glrecon.dump;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.MasterHeaderCheckService;
import com.ey.advisory.app.util.HeaderCheckerUtil;

@Component("GLReconDumpHeaderCheckService")
public class GLReconDumpHeaderCheckService implements MasterHeaderCheckService {
	
	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	protected static final String[] EXPECTED_HEADERS = { "TransactionType",
			"CompanyCode", "FiscalYear", "Period(MMYYYY)", "BusinessPlace",
			"BusinessArea", "GLAccount", "GLDescription", "Text",
			"AssignmentNumber", "ERPDocumentType", "AccountingVoucherNumber",
			"AccountingVoucherDate", "ItemSerialNumber", "PostingKey",
			"PostingDate", "AmountInLocalCurrency", "LocalCurrencyCode",
			"ClearingDocumentNumber", "ClearingDocumentDate", "CustomerCode",
			"CustomerName", "CustomerGSTIN", "SupplierCode", "SupplierName",
			"SupplierGSTIN", "PlantCode", "CostCentre", "ProfitCentre",
			"SpecialGLIndicator", "Reference", "AmountinDocumentCurrency",
			"EffectiveExchangeRate", "DocumentCurrencyCode", "AccountType",
			"TaxCode", "WithholdingTaxAmount", "WithholdingExemptAmount",
			"WithholdingTaxBaseAmount", "InvoiceReference",
			"DebitCreditIndicator", "PaymentDate", "PaymentBlock",
			"PaymentReference", "TermsofPayment", "Material", "ReferenceKey1",
			"OffsettingAccountType", "OffsettingAccountNumber",
			"DocumentHeaderText", "BillingDocumentNumber",
			"BillingDocumentDate", "MIGONumber", "MIGODate", "MIRONumber",
			"MIRODate", "ExpenseGLMapping", "Segment", "GeoLevel", "StateName",
			"UserID", "ParkedBy", "EntryDate", "TimeofEntry", "Remarks",
			"UserDefinedField1", "UserDefinedField2", "UserDefinedField3",
			"UserDefinedField4", "UserDefinedField5", "UserDefinedField6",
			"UserDefinedField7", "UserDefinedField8", "UserDefinedField9",
			"UserDefinedField10"

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
		Pair<Boolean, String> pair = headerCheckerUtil
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
