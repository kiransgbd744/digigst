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
@Component("Anx2GetAnx2FileHeaderCheckService")
public class Anx2GetAnx2FileHeaderCheckService
		implements Gstr2HeaderCheckService {

	@Autowired
	@Qualifier("Gstr2HeaderChecker")
	private Gstr2HeaderChecker gstr2HeaderChecker;

	protected static final String[] EXPECTED_HEADERS = { "TABLE_SECTION", "CGSTIN",
			"SGSTIN", "CFS", "CHKSUM", "UPLOAD_DATE_SUPPLIER",
			"SUPPLIER_INV_NUM", "SUPPLIER_INV_DATE", "SUPPLIER_INV_VAL", "POS",
			"INV_TYPE", "DIFF_PERCENT", "BATCH_ID", "IGST_AMT", "CGST_AMT",
			"SGST_AMT", "CESS_AMT", "TAXABLE_VALUE", "TAX_PERIOD",
			"ACTION_TAKEN", "DERIVED_RET_PERIOD", "IS_DELETE", "CGSTIN_PAN",
			"SGSTIN_PAN", "ITC_ENT", "REFUND_ELG", "SEC_7_ACT" };

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
