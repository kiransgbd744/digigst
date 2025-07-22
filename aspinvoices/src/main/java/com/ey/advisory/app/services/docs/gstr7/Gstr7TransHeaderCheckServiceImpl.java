package com.ey.advisory.app.services.docs.gstr7;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("Gstr7TransHeaderCheckServiceImpl")
public class Gstr7TransHeaderCheckServiceImpl
		implements Gstr7TransHeaderCheckService {

	@Autowired
	@Qualifier("Gstr7TransHeaderChecker")
	private Gstr7TransHeaderChecker gstr7HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "SourceIdentifier",
			"SourceFileName", "GLAccountCode", "Division", "SubDivision",
			"ProfitCentre1", "ProfitCentre2", "PlantCode", "ReturnPeriod",
			"DocumentType", "SupplyType", "DeductorGSTIN", "DocumentNumber",
			"DocumentDate", "OriginalDocumentNumber", "OriginalDocumentDate",
			"DeducteeGSTIN", "OriginalDeducteeGSTIN", "OriginalReturnPeriod",
			"OriginalTaxableValue","OriginalInvoiceValue","LineNumber", "TaxableValue", "IGSTAmount",
			"CGSTAmount", "SGSTAmount", "InvoiceValue", "ContractNumber",
			"ContractDate", "ContractValue", "PaymentAdviceNumber",
			"PaymentAdviceDate", "UserDefinedField1", "UserDefinedField2",
			"UserDefinedField3", "UserDefinedField4", "UserDefinedField5",
			"UserDefinedField6", "UserDefinedField7", "UserDefinedField8",
			"UserDefinedField9", "UserDefinedField10", "UserDefinedField11",
			"UserDefinedField12", "UserDefinedField13", "UserDefinedField14",
			"UserDefinedField15" };

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
		return gstr7HeaderChecker.validateHeaders(EXPECTED_HEADERS, headerCols);
	}

}