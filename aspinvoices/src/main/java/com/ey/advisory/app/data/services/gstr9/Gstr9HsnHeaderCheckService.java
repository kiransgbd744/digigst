package com.ey.advisory.app.data.services.gstr9;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.MasterHeaderCheckService;
import com.ey.advisory.app.util.HeaderCheckerUtil;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnHeaderCheckService")
public class Gstr9HsnHeaderCheckService implements MasterHeaderCheckService {

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	public static final String[] EXPECTED_HEADERS = { "GSTIN", "FY",
			"TableNumber", "HSN", "Description", "RateofTax", "UQC",
			"TotalQuantity", "TaxableValue", "ConcessionalRateFlag", "IGST",
			"CGST", "SGST", "Cess",

	};

	@Override
	public Pair<Boolean, String> validate(Object[] headerCols) {
		Pair<Boolean, String> pair = headerCheckerUtil
				.validateHeaders(EXPECTED_HEADERS, headerCols);
		return pair;
	}

}
