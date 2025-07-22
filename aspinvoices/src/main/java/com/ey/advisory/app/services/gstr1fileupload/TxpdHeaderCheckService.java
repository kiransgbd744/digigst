package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr1HeaderChecker;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("TxpdHeaderCheckService")
public class TxpdHeaderCheckService implements Gstr1HeaderCheckService {
	@Autowired
	@Qualifier("Gstr1HeaderChecker")
	private Gstr1HeaderChecker gstr1HeaderChecker;

	public static final String[] EXPECTED_HEADERS = { "SupplierGSTIN",
			"ReturnPeriod", "TransactionType", "Month", "OrgPOS", "OrgRate",
			"OrgGrossAdvanceAdjusted", "NewPOS", "NewRate",
			"NewGrossAdvanceAdjusted", "IntegratedTaxAmount",
			"CentralTaxAmount", "StateUTTaxAmount", "CessAmount",
			"ProfitCentre", "Plant", "Division", "Location",
			"SalesOrganisation", "DistributionChannel", "UserAccess1",
			"UserAccess2", "UserAccess3", "UserAccess4", "UserAccess5",
			"UserAccess6", "Userdefinedfield1", "Userdefinedfield2",
			"Userdefinedfield3" };

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
		Pair<Boolean, String> pair = gstr1HeaderChecker
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
