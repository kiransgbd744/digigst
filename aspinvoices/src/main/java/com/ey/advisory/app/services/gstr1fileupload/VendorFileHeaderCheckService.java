package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.Gstr1HeaderChecker;
import com.ey.advisory.app.util.HeaderCheckerUtil;

@Component("VendorFileHeaderCheckService")
public class VendorFileHeaderCheckService implements Gstr1HeaderCheckService {

	@Autowired
	@Qualifier("Gstr1HeaderChecker")
	private Gstr1HeaderChecker gstr1HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "SupplierGSTIN/PAN",
			"LegalName", "SupplierType", "SupplierCode", "OutsideIndia",
			"E-MailID", "MobileNumber" };

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

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